package org.ifgi.sla.wsag.helper;

import java.io.File;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementContextType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementOfferDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceDescriptionTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServicePropertiesType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceReferenceType;
import org.ifgi.namespaces.wsag.ogc.ServiceReferenceDocument;

public class AgreementOfferHelper implements Helper<AgreementOfferDocument, File>
{
	protected static Logger LOGGER = Logger.getLogger(AgreementOfferHelper.class);

	static private AgreementOfferHelper _instance = null;

	static public AgreementOfferHelper instance()
	{
		if (_instance == null)
		{
			_instance = new AgreementOfferHelper();
		}
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.io.File)
	 */
	public AgreementOfferDocument get(File pFile) throws Exception
	{
		return AgreementOfferDocument.Factory.parse(pFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#getXML(java.lang.Object)
	 */
	public String getXML(AgreementOfferDocument pAgreement) throws Exception
	{
		return PrettyPrinter.indent(pAgreement.xmlText());
	}

	/**
	 * @param pAgreementOffer
	 * @return
	 * @throws Exception
	 */
	public AgreementDocument createAgreement(AgreementOfferDocument pAgreementOffer)
	{
		AgreementDocument agreement = AgreementDocument.Factory.newInstance();
		agreement.addNewAgreement();

		String agreementId = UUID.randomUUID().toString();
		agreement.getAgreement().setAgreementId(agreementId);

		/*
		 * CREATE AGREEMENT CONTEXT
		 */
		AgreementContextType context = pAgreementOffer.getAgreementOffer().getContext();
		agreement.getAgreement().setContext(context);

		/*
		 * CREATE SERVICE TERMS
		 */
		agreement.getAgreement().addNewTerms().addNewAll();

		// service description
		ServiceDescriptionTermType[] serviceDescriptionTermArray = pAgreementOffer.getAgreementOffer().getTerms().getAll().getServiceDescriptionTermArray();
		agreement.getAgreement().getTerms().getAll().setServiceDescriptionTermArray(serviceDescriptionTermArray);

		// service properties
		ServicePropertiesType[] servicePropertiesArray = pAgreementOffer.getAgreementOffer().getTerms().getAll().getServicePropertiesArray();
		agreement.getAgreement().getTerms().getAll().setServicePropertiesArray(servicePropertiesArray);

		// service reference
		ServiceReferenceType[] serviceReferenceArray = pAgreementOffer.getAgreementOffer().getTerms().getAll().getServiceReferenceArray();
		
		if (serviceReferenceArray.length != 1)
		{
			LOGGER.error("The amount of ServiceReference elements is " + serviceReferenceArray.length + " (must be exactly one).");
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		
		ServiceReferenceDocument serviceReference = ServiceReferenceHelper.instance().get(pAgreementOffer.getAgreementOffer());
		serviceReference.getServiceReference().setURL(serviceReference.getServiceReference().getURL() + "/" + agreementId);
		serviceReferenceArray[0].set(serviceReference);
		
		agreement.getAgreement().getTerms().getAll().setServiceReferenceArray(serviceReferenceArray);

		/*
		 * CREATE GUARANTEE TERMS
		 */
		GuaranteeTermType[] guaranteeTermArray = pAgreementOffer.getAgreementOffer().getTerms().getAll().getGuaranteeTermArray();
		agreement.getAgreement().getTerms().getAll().setGuaranteeTermArray(guaranteeTermArray);

		return agreement;
	}
}
