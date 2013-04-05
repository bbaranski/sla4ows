package org.ifgi.sla.wsag.helper;

import java.io.File;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementContextType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementOfferDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceDescriptionTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServicePropertiesType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceReferenceType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.TemplateDocument;
import org.ifgi.namespaces.wsag.ogc.ContactDocument;
import org.wsag4J.schemas.x2009.x07.wsag4JSchedulingExtensions.TimeConstraintDocument;

public class TemplateHelper implements Helper<TemplateDocument, File>
{
	protected static Logger LOGGER = Logger.getLogger(TemplateHelper.class);

	static private TemplateHelper _instance = null;

	/**
	 * @return
	 */
	static public TemplateHelper instance()
	{
		if (_instance == null)
		{
			_instance = new TemplateHelper();
		}
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.io.File)
	 */
	public TemplateDocument get(File pFile) throws Exception
	{
		return TemplateDocument.Factory.parse(pFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#getXML(java.lang.Object)
	 */
	public String getXML(TemplateDocument pTemplate) throws Exception
	{
		return PrettyPrinter.indent(pTemplate.xmlText());
	}

	public AgreementOfferDocument createAgreementOffer(TemplateDocument pTemplate, TimeConstraintDocument pTimeConstraints, ContactDocument pContact) throws Exception 
	{
		AgreementOfferDocument agreementOffer = createAgreementOffer(pTemplate);
		
		/* set agreement initiator */ 
		agreementOffer.getAgreementOffer().getContext().setAgreementInitiator(pContact);
		
		/* set time constraints */
		TimeConstraintHelper.instance().setTimeConstraint(agreementOffer, pTimeConstraints);
		
		return agreementOffer;
	}
	
	/**
	 * @param pTemplate
	 * @return
	 * @throws Exception
	 */
	public AgreementOfferDocument createAgreementOffer(TemplateDocument pTemplate) throws Exception
	{
		AgreementOfferDocument result = AgreementOfferDocument.Factory.newInstance();
		result.addNewAgreementOffer();

		result.getAgreementOffer().setAgreementId(UUID.randomUUID().toString());
		
		/*
		 * CREATE AGREEMENT CONTEXT
		 */
		AgreementContextType context = pTemplate.getTemplate().getContext();
		result.getAgreementOffer().setContext(context);

		/*
		 * CREATE SERVICE TERMS
		 */
		result.getAgreementOffer().addNewTerms().addNewAll();

		// service description
		ServiceDescriptionTermType[] serviceDescriptionTermArray = pTemplate.getTemplate().getTerms().getAll().getServiceDescriptionTermArray();
		result.getAgreementOffer().getTerms().getAll().setServiceDescriptionTermArray(serviceDescriptionTermArray);

		// service properties
		ServicePropertiesType[] servicePropertiesArray = pTemplate.getTemplate().getTerms().getAll().getServicePropertiesArray();
		result.getAgreementOffer().getTerms().getAll().setServicePropertiesArray(servicePropertiesArray);

		// service reference
		ServiceReferenceType[] serviceReferenceArray = pTemplate.getTemplate().getTerms().getAll().getServiceReferenceArray();
		result.getAgreementOffer().getTerms().getAll().setServiceReferenceArray(serviceReferenceArray);

		/*
		 * CREATE GUARANTEE TERMS
		 */
		GuaranteeTermType[] guaranteeTermArray = pTemplate.getTemplate().getTerms().getAll().getGuaranteeTermArray();
		result.getAgreementOffer().getTerms().getAll().setGuaranteeTermArray(guaranteeTermArray);

		return result;
	}
}
