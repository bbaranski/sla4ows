package org.ifgi.sla.wsag.helper;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceDescriptionTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateType;

import com.sun.jersey.api.client.ClientResponse.Status;

public class ServiceTermStateHelper implements Helper<ServiceTermStateType, AgreementPropertiesDocument>
{
	protected static Logger LOGGER = Logger.getLogger(ServiceTermStateHelper.class);

	static private ServiceTermStateHelper _instance = null;

	/**
	 * @return
	 */
	static public ServiceTermStateHelper instance()
	{
		if (_instance == null)
		{
			_instance = new ServiceTermStateHelper();
		}
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.lang.Object)
	 */
	@Override
	public ServiceTermStateType get(AgreementPropertiesDocument pAgreementProperties)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	public ServiceTermStateType getServiceProperties(AgreementPropertiesDocument pAgreementProperties)
	{
		for (int i = 0; i < pAgreementProperties.getAgreementProperties().getServiceTermStateArray().length; i++)
		{
			ServiceTermStateType serviceTermState = pAgreementProperties.getAgreementProperties().getServiceTermStateArray()[i];

			ServiceDescriptionTermType serviceDescriptionTerm = AgreementHelper.instance().getServiceDescriptionTermByName(
					pAgreementProperties.getAgreementProperties().getTerms().getAll().getServiceDescriptionTermArray(), serviceTermState.getTermName());

			// UPDATE SERVICE DESCRIPTION
			if (ServiceDescriptionHelper.instance().isServiceDescription(serviceDescriptionTerm))
			{
			}
			
			// UPDATE SERVICE PROPERTIES
			if (ServicePropertiesHelper.instance().isServiceProperties(serviceDescriptionTerm))
			{
				return serviceTermState;
			}

			// UPDDATE TIME CONSTRAINT
			if (ServiceDescriptionHelper.instance().isTimeConstraint(serviceDescriptionTerm))
			{
			}
		}
		LOGGER.error("Could not find service term state for service properties in agreement properties for agreement with the id '"
				+ pAgreementProperties.getAgreementProperties().getAgreementId() + "'.");
		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
	}
	
	public ServiceTermStateType getServiceDescription(AgreementPropertiesDocument pAgreementProperties)
	{
		for (int i = 0; i < pAgreementProperties.getAgreementProperties().getServiceTermStateArray().length; i++)
		{
			ServiceTermStateType serviceTermState = pAgreementProperties.getAgreementProperties().getServiceTermStateArray()[i];

			ServiceDescriptionTermType serviceDescriptionTerm = AgreementHelper.instance().getServiceDescriptionTermByName(
					pAgreementProperties.getAgreementProperties().getTerms().getAll().getServiceDescriptionTermArray(), serviceTermState.getTermName());

			// UPDATE SERVICE DESCRIPTION
			if (ServiceDescriptionHelper.instance().isServiceDescription(serviceDescriptionTerm))
			{
				return serviceTermState;
			}

			// UPDDATE TIME CONSTRAINT
			if (ServiceDescriptionHelper.instance().isTimeConstraint(serviceDescriptionTerm))
			{
			}
		}
		LOGGER.error("Could not find service term state for service description in agreement properties for agreement with the id '"
				+ pAgreementProperties.getAgreementProperties().getAgreementId() + "'.");
		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
	}

	public ServiceTermStateType getTimeConstraint(AgreementPropertiesDocument pAgreementProperties)
	{
		for (int i = 0; i < pAgreementProperties.getAgreementProperties().getServiceTermStateArray().length; i++)
		{
			ServiceTermStateType serviceTermState = pAgreementProperties.getAgreementProperties().getServiceTermStateArray()[i];

			ServiceDescriptionTermType serviceDescriptionTerm = AgreementHelper.instance().getServiceDescriptionTermByName(
					pAgreementProperties.getAgreementProperties().getTerms().getAll().getServiceDescriptionTermArray(), serviceTermState.getTermName());

			// UPDATE SERVICE DESCRIPTION
			if (ServiceDescriptionHelper.instance().isServiceDescription(serviceDescriptionTerm))
			{
			}

			// UPDDATE TIME CONSTRAINT
			if (ServiceDescriptionHelper.instance().isTimeConstraint(serviceDescriptionTerm))
			{
				return serviceTermState;
			}
		}
		LOGGER.error("Could not find service term state for time constraints in agreement properties for agreement with the id '"
				+ pAgreementProperties.getAgreementProperties().getAgreementId() + "'.");
		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#getXML(java.lang.Object)
	 */
	@Override
	public String getXML(ServiceTermStateType pServiceTermState) throws Exception
	{
		return PrettyPrinter.indent(pServiceTermState.xmlText());
	}
}
