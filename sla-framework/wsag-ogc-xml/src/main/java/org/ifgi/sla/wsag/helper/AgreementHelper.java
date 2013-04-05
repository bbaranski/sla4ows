package org.ifgi.sla.wsag.helper;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementStateType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermStateType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceDescriptionTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateType;
import org.wsag4J.schemas.x2009.x07.wsag4JSchedulingExtensions.TimeConstraintDocument;

public class AgreementHelper implements Helper<AgreementDocument, File>
{
	protected static Logger LOGGER = Logger.getLogger(AgreementHelper.class);

	static private AgreementHelper _instance = null;

	static public AgreementHelper instance()
	{
		if (_instance == null)
		{
			_instance = new AgreementHelper();
		}
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.io.File)
	 */
	public AgreementDocument get(File pFile) throws Exception
	{
		return AgreementDocument.Factory.parse(pFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#getXML(java.lang.Object)
	 */
	public String getXML(AgreementDocument pAgreement) throws Exception
	{
		return PrettyPrinter.indent(pAgreement.xmlText());
	}

	/**
	 * @param pAgreement
	 * @return
	 */
	public boolean isContractRuntime(AgreementDocument pAgreement)
	{
		long currentTime = System.currentTimeMillis();
		TimeConstraintDocument timeConstraint = TimeConstraintHelper.instance().get(pAgreement.getAgreement());
		if (currentTime > timeConstraint.getTimeConstraint().getStartTime().getTimeInMillis()
				&& currentTime < timeConstraint.getTimeConstraint().getEndTime().getTimeInMillis())
		{
			return true;
		}
		return false;
	}

	public AgreementPropertiesDocument createAgreementProperties(AgreementDocument pAgreement)
	{
		AgreementPropertiesDocument result = AgreementPropertiesDocument.Factory.newInstance();

		result.addNewAgreementProperties();

		/*
		 * INSERT GENERAL AGREEMENT INFORMATION
		 */

		result.getAgreementProperties().setAgreementId(pAgreement.getAgreement().getAgreementId());
		result.getAgreementProperties().setName(pAgreement.getAgreement().getName());
		result.getAgreementProperties().setContext(pAgreement.getAgreement().getContext());
		result.getAgreementProperties().setTerms(pAgreement.getAgreement().getTerms());

		/*
		 * CREATE INITIAL STATEMENTS
		 */

		AgreementStateType agreementState = result.getAgreementProperties().addNewAgreementState();
		agreementState.setState(AgreementStateDefinition.PENDING);

		for (ServiceDescriptionTermType serviceDescriptionTerm : pAgreement.getAgreement().getTerms().getAll().getServiceDescriptionTermArray())
		{
			ServiceTermStateType serviceTermState = result.getAgreementProperties().addNewServiceTermState();
			serviceTermState.setTermName(serviceDescriptionTerm.getName());
			serviceTermState.setState(ServiceTermStateDefinition.NOT_READY);
		}

		for (GuaranteeTermType guaranteeTerm : pAgreement.getAgreement().getTerms().getAll().getGuaranteeTermArray())
		{
			GuaranteeTermStateType guaranteeTermState = result.getAgreementProperties().addNewGuaranteeTermState();
			guaranteeTermState.setTermName(guaranteeTerm.getName());
			guaranteeTermState.setState(GuaranteeTermStateDefinition.NOT_DETERMINED);
		}

		return result;
	}

	/**
	 * @param pAgreement
	 * @param pName
	 * @return
	 */
	public ServiceDescriptionTermType getServiceDescriptionTermByName(ServiceDescriptionTermType[] pServiceDescriptionTermArray, String pName)
	{
		for (ServiceDescriptionTermType serviceDescription : pServiceDescriptionTermArray)
		{
			if (serviceDescription.getName().equalsIgnoreCase(pName))
			{
				return serviceDescription;
			}
		}
		return null;
	}
}
