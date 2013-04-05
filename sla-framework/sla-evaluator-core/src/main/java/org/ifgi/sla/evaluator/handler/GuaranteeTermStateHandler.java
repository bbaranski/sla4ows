package org.ifgi.sla.evaluator.handler;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.Script;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermStateDefinition.Enum;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermStateType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateDefinition;
import org.ifgi.namespaces.wsag.ogc.CustomBusinessValueDocument;
import org.ifgi.namespaces.wsag.ogc.CustomServiceLevelDocument;
import org.ifgi.namespaces.wsag.ogc.PropertyType;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.namespaces.wsag.ogc.ServiceReferenceDocument;
import org.ifgi.sla.evaluator.Configuration;
import org.ifgi.sla.evaluator.helper.JexlContextHelper;
import org.ifgi.sla.evaluator.type.BusinessType;
import org.ifgi.sla.evaluator.type.ObjectiveType;
import org.ifgi.sla.reporter.client.ReporterClient;
import org.ifgi.sla.wsag.helper.BusinessValueHelper;
import org.ifgi.sla.wsag.helper.ServiceLevelHelper;
import org.ifgi.sla.wsag.helper.ServicePropertiesHelper;
import org.ifgi.sla.wsag.helper.ServiceReferenceHelper;
import org.ifgi.sla.wsag.helper.ServiceTermStateHelper;
import org.ifgi.sla.wsag.urn.ServicePropertiesURN;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientResponse.Status;

public class GuaranteeTermStateHandler extends AbstractEvaluatorHandler
{
	protected static Logger LOGGER = Logger.getLogger(GuaranteeTermStateHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.evaluator.handler.AbstractEvaluatorHandler#isResponsible(org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument)
	 */
	@Override
	public boolean isResponsible(AgreementPropertiesDocument pAgreementProperties)
	{
		// TODO Check all states to be READY/OBSERVED before evaluating guarantee terms

		if (pAgreementProperties.getAgreementProperties().getAgreementState().getState() == AgreementStateDefinition.OBSERVED
				&& ServiceTermStateHelper.instance().getServiceProperties(pAgreementProperties).getState() == ServiceTermStateDefinition.READY)
		{
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.evaluator.handler.AbstractEvaluatorHandler#evaluate(org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument)
	 */
	@Override
	public void evaluate(AgreementPropertiesDocument pAgreementProperties)
	{
		for (GuaranteeTermStateType guaranteeTermState : pAgreementProperties.getAgreementProperties().getGuaranteeTermStateArray())
		{
			LOGGER.info(guaranteeTermState.getTermName());

			Enum previousStatus = guaranteeTermState.getState();

			// EVALUTE GUARANTEE TERM
			evaluate(pAgreementProperties, guaranteeTermState);

			Enum currentStatus = guaranteeTermState.getState();

			// REPORT GUARANTEE TERM STATUS
			if (previousStatus == GuaranteeTermStateDefinition.FULFILLED)
			{
				if (currentStatus == GuaranteeTermStateDefinition.VIOLATED)
				{
					ReporterClient.instance().report(Configuration.instance().getReporter(), pAgreementProperties);
				}
			}
			else

			if (previousStatus == GuaranteeTermStateDefinition.VIOLATED)
			{
				if (currentStatus == GuaranteeTermStateDefinition.FULFILLED)
				{
					ReporterClient.instance().report(Configuration.instance().getReporter(), pAgreementProperties);
				}
			}
			else

			if (previousStatus == GuaranteeTermStateDefinition.NOT_DETERMINED)
			{
				if (currentStatus == GuaranteeTermStateDefinition.VIOLATED)
				{
					ReporterClient.instance().report(Configuration.instance().getReporter(), pAgreementProperties);
				}
			}
		}
	}

	/**
	 * @param pAgreementProperties
	 * @param pGuaranteeTermState
	 */
	public void evaluate(AgreementPropertiesDocument pAgreementProperties, GuaranteeTermStateType pGuaranteeTermState)
	{
		// CHECK IF SERVICE SCOPE
		if (!isServiceScope(pAgreementProperties, pGuaranteeTermState))
		{
			return;
		}

		// CHECK IF QUALIFYING CONDITION
		if (!isQualifyingCondition(pAgreementProperties, pGuaranteeTermState))
		{
			return;
		}

		GuaranteeTermType[] guaranteeTermTypeArray = pAgreementProperties.getAgreementProperties().getTerms().getAll().getGuaranteeTermArray();

		CustomServiceLevelDocument customServiceLevel = ServiceLevelHelper.instance().get(guaranteeTermTypeArray, pGuaranteeTermState.getTermName());
		CustomBusinessValueDocument customBusinessValue = BusinessValueHelper.instance().get(guaranteeTermTypeArray, pGuaranteeTermState.getTermName());

		if (customServiceLevel != null)
		{
			// EVALUTE GUARANTEE TERM (SERVICE LEVEL)
			Boolean result = evaluate(pAgreementProperties, customServiceLevel);

			// SET GUARANTEE TERM STATUS
			if (result.booleanValue())
			{
				LOGGER.info("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] guarantee term state '" + pGuaranteeTermState.getTermName() + "' is 'FULFILLED'");
				pGuaranteeTermState.setState(GuaranteeTermStateDefinition.FULFILLED);
			}
			else
			{
				LOGGER.info("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] guarantee term state '" + pGuaranteeTermState.getTermName() + "' is 'VIOLATED'");
				pGuaranteeTermState.setState(GuaranteeTermStateDefinition.VIOLATED);
			}

		}
		else if (customBusinessValue != null)
		{
			// EVALUATE GUARANTEE TERM (BUSINESS VALUE)
			Double result = evaluate(pAgreementProperties, customBusinessValue);

			// SET GUARANTEE TERM STATUS
			LOGGER.info("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] guarantee term state '" + pGuaranteeTermState.getTermName() + "' as value '" + result.doubleValue() + "'");

			LOGGER.info("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] guarantee term state '" + pGuaranteeTermState.getTermName() + "' is 'FULFILLED'");
			pGuaranteeTermState.setState(GuaranteeTermStateDefinition.FULFILLED);

			// INSERT GUARANTEE TERM INFORMATION
			CustomBusinessValueDocument customBusinessValueGuarantee = CustomBusinessValueDocument.Factory.newInstance();
			customBusinessValueGuarantee.addNewCustomBusinessValue();

			customBusinessValueGuarantee.getCustomBusinessValue().setName(customBusinessValue.getCustomBusinessValue().getName());
			customBusinessValueGuarantee.getCustomBusinessValue().setType(customBusinessValue.getCustomBusinessValue().getType());

			customBusinessValueGuarantee.getCustomBusinessValue().setValue(result.toString());

			XmlCursor guaranteeCursor = pGuaranteeTermState.newCursor();

			// remove existing guarantee term information
			NodeList nodeList = pGuaranteeTermState.getDomNode().getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				if (nodeList.item(i).getLocalName() != null && nodeList.item(i).getLocalName().equalsIgnoreCase("CustomBusinessValue"))
				{
					pGuaranteeTermState.getDomNode().removeChild(nodeList.item(i));
					break;
				}

			}

			guaranteeCursor.toEndToken();

			XmlCursor newCur = guaranteeCursor.newCursor();
			customBusinessValueGuarantee.getCustomBusinessValue().newCursor().copyXml(newCur);

			guaranteeCursor.dispose();
		}
		else
		{
			LOGGER.error("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] found no <CustomServiceLevel> and <CustomBusinessValue> with name '" + pGuaranteeTermState.getTermName() + "'");
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}

	}

	/**
	 * @param customServiceLevel
	 * @return
	 */
	private Boolean evaluate(AgreementPropertiesDocument pAgreementProperties, CustomServiceLevelDocument pCustomServiceLevel)
	{
		JexlEngine jexlEngine = new JexlEngine();
		Script jexlScript = jexlEngine.createScript(pCustomServiceLevel.getCustomServiceLevel().getStatus());

		JexlContext jexlContext = creatJexlContext(pAgreementProperties);

		Boolean result = (Boolean) jexlScript.execute(jexlContext);

		return result;
	}

	/**
	 * @param customServiceLevel
	 * @return
	 */
	private Double evaluate(AgreementPropertiesDocument pAgreementProperties, CustomBusinessValueDocument pCustomBusinessValue)
	{
		JexlEngine jexlEngine = new JexlEngine();
		Script jexlScript = jexlEngine.createScript(pCustomBusinessValue.getCustomBusinessValue().getValue());

		JexlContext jexlContext = creatJexlContext(pAgreementProperties);

		Double result = (Double) jexlScript.execute(jexlContext);

		return result;
	}

	/**
	 * @param pAgreementProperties
	 * @param pGuaranteeTermState
	 * @return
	 */
	protected boolean isServiceScope(AgreementPropertiesDocument pAgreementProperties, GuaranteeTermStateType pGuaranteeTermState)
	{
		return true;
	}

	/**
	 * @param pAgreementProperties
	 * @param pGuaranteeTermState
	 * @return
	 */
	protected boolean isQualifyingCondition(AgreementPropertiesDocument pAgreementProperties, GuaranteeTermStateType pGuaranteeTermState)
	{
		return true;
	}

	/**
	 * @param pAgreementProperties
	 * @return
	 */
	protected JexlContext creatJexlContext(AgreementPropertiesDocument pAgreementProperties)
	{
		JexlContext jexlContext = new MapContext();

		/* CREATE CONTEXT VARIABLES FOR SERVICE PROPERTIES */

		ServicePropertiesDocument serviceProperties = ServicePropertiesHelper.instance().get(pAgreementProperties.getAgreementProperties());

		for (PropertyType property : serviceProperties.getServiceProperties().getPropertyArray())
		{
			String name = property.getName();
			String type = property.getType();

			if (!ServicePropertiesURN.instance().validate(type))
			{
				LOGGER.error("Service property type '" + type + "' is unvalid.");
				throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
			}

			String agreementId = pAgreementProperties.getAgreementProperties().getAgreementId();

			if (type.equalsIgnoreCase(ServicePropertiesURN.RESOURCE_OPERATION))
			{
				JexlContextHelper.createOperation(agreementId, jexlContext, name);
			}
			
			if (type.equalsIgnoreCase(ServicePropertiesURN.RUNTIME_RESPONSE))
			{
				JexlContextHelper.createResponse(agreementId, jexlContext, name);
			}

			if (type.equalsIgnoreCase(ServicePropertiesURN.RUNTIME_AVAILABILITY))
			{
				JexlContextHelper.createAvailability(agreementId, jexlContext, name);
			}

			if (type.equalsIgnoreCase(ServicePropertiesURN.USAGE_PIXEL))
			{
				ServiceReferenceDocument serviceReference = ServiceReferenceHelper.instance().get(pAgreementProperties.getAgreementProperties().getTerms().getAll().getServiceReferenceArray());
				String pService = serviceReference.getServiceReference().getURL();
				JexlContextHelper.createPixel(pService, jexlContext, name, property.getMonitoring().getPassiveMonitoring().getRequest().getResource());
			}
		}

		/* CREATE CONTEXT VARIABLES FOR SERVICE LEVEL OBJECTIVES */

		GuaranteeTermType[] guaranteeTermTypeArray = pAgreementProperties.getAgreementProperties().getTerms().getAll().getGuaranteeTermArray();

		for (GuaranteeTermStateType guaranteeTermStateType : pAgreementProperties.getAgreementProperties().getGuaranteeTermStateArray())
		{
			CustomServiceLevelDocument customServiceLevel = ServiceLevelHelper.instance().get(guaranteeTermTypeArray, guaranteeTermStateType.getTermName());
			CustomBusinessValueDocument customBusinessValue = BusinessValueHelper.instance().get(guaranteeTermTypeArray, guaranteeTermStateType.getTermName());

			if (customServiceLevel != null)
			{
				String name = customServiceLevel.getCustomServiceLevel().getName();

				Enum currentState = guaranteeTermStateType.getState();
				if (currentState == GuaranteeTermStateDefinition.FULFILLED)
				{
					ObjectiveType objective = new ObjectiveType();
					objective.setStatus(true);
					jexlContext.set(name, objective);
				}
				if (currentState == GuaranteeTermStateDefinition.VIOLATED)
				{
					ObjectiveType objective = new ObjectiveType();
					objective.setStatus(false);
					jexlContext.set(name, objective);
				}
			}
			
			if (customBusinessValue != null)
			{
				String name = customBusinessValue.getCustomBusinessValue().getName();

				Enum currentState = guaranteeTermStateType.getState();
				if (currentState == GuaranteeTermStateDefinition.FULFILLED)
				{
					double value = 42.0;
					
					BusinessType business = new BusinessType();
					business.setValue(value);
					jexlContext.set(name, business);
				}
			}

		}

		return jexlContext;
	}
}
