package org.ifgi.sla.evaluator.handler;

import java.util.Date;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateType;
import org.ifgi.sla.wsag.helper.ServiceTermStateHelper;
import org.ifgi.sla.wsag.helper.TimeConstraintHelper;
import org.wsag4J.schemas.x2009.x07.wsag4JSchedulingExtensions.TimeConstraintDocument;

import com.sun.jersey.api.client.ClientResponse.Status;

public class ServiceDescriptionStateHandler extends AbstractEvaluatorHandler
{
	protected static Logger LOGGER = Logger.getLogger(ServiceDescriptionStateHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.evaluator.handler.AbstractEvaluatorHandler#isResponsible(org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument)
	 */
	@Override
	public boolean isResponsible(AgreementPropertiesDocument pAgreementProperties)
	{
		ServiceTermStateType serviceTermState = ServiceTermStateHelper.instance().getServiceDescription(pAgreementProperties);
		if (serviceTermState.getState() != ServiceTermStateDefinition.COMPLETED)
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
		TimeConstraintDocument timeConstraint = TimeConstraintHelper.instance().get(pAgreementProperties);

		ServiceTermStateType serviceTermState = ServiceTermStateHelper.instance().getServiceDescription(pAgreementProperties);

		Date current = new Date(System.currentTimeMillis());

		if (current.before(timeConstraint.getTimeConstraint().getStartTime().getTime()))
		{
			LOGGER.info("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] service description state is 'NOT_READY'");
			setServiceTermState(serviceTermState, ServiceTermStateDefinition.NOT_READY);
		}
		else

		if (current.after(timeConstraint.getTimeConstraint().getEndTime().getTime()))
		{
			LOGGER.info("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] service description state is 'COMPLETED'");
			setServiceTermState(serviceTermState, ServiceTermStateDefinition.COMPLETED);
		}
		else

		if (current.after(timeConstraint.getTimeConstraint().getStartTime().getTime()) && current.before(timeConstraint.getTimeConstraint().getEndTime().getTime()))
		{
			LOGGER.info("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] service description state is 'READY'");
			setServiceTermState(serviceTermState, ServiceTermStateDefinition.READY);
		}
		else
		{
			LOGGER.error("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] service description state could not be determined.");
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}

	/**
	 * @param pAgreementProperties
	 * @param pState
	 */
	protected void setServiceTermState(ServiceTermStateType pServiceTermState, ServiceTermStateDefinition.Enum pState)
	{
		pServiceTermState.setState(pState);
	}
}
