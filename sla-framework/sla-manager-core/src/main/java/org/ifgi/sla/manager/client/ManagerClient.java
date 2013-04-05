package org.ifgi.sla.manager.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementOfferDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermStateDefinition.Enum;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermStateType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.TemplateDocument;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.namespaces.wsag.rest.AgreementListDocument;
import org.ifgi.namespaces.wsag.rest.MeasurementHistoryListDocument;
import org.ifgi.namespaces.wsag.rest.TemplateListDocument;
import org.ifgi.sla.manager.transform.AgreementListMessageBodyReaderXml;
import org.ifgi.sla.manager.transform.AgreementListMessageBodyWriterXml;
import org.ifgi.sla.manager.transform.MeasurementHistoryListMessageBodyReaderXml;
import org.ifgi.sla.manager.transform.MeasurementHistoryListMessageBodyWriterXml;
import org.ifgi.sla.manager.transform.MeasurementListMessageBodyReaderXml;
import org.ifgi.sla.manager.transform.MeasurementListMessageBodyWriterXml;
import org.ifgi.sla.manager.transform.TemplateListMessageBodyReaderXml;
import org.ifgi.sla.manager.transform.TemplateListMessageBodyWriterXml;
import org.ifgi.sla.wsag.transform.AgreementMessageBodyReaderXml;
import org.ifgi.sla.wsag.transform.AgreementMessageBodyWriterXml;
import org.ifgi.sla.wsag.transform.AgreementOfferMessageBodyReaderXml;
import org.ifgi.sla.wsag.transform.AgreementOfferMessageBodyWriterXml;
import org.ifgi.sla.wsag.transform.AgreementPropertiesMessageBodyReaderXml;
import org.ifgi.sla.wsag.transform.AgreementPropertiesMessageBodyWriterXml;
import org.ifgi.sla.wsag.transform.ServiceDescriptionMessageBodyReaderXml;
import org.ifgi.sla.wsag.transform.ServiceDescriptionMessageBodyWriterXml;
import org.ifgi.sla.wsag.transform.ServicePropertiesMessageBodyReaderXml;
import org.ifgi.sla.wsag.transform.ServicePropertiesMessageBodyWriterXml;
import org.ifgi.sla.wsag.transform.TemplateMessageBodyReaderXml;
import org.ifgi.sla.wsag.transform.TemplateMessageBodyWriterXml;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;

public class ManagerClient
{
	protected static Logger LOGGER = Logger.getLogger(ManagerClient.class);

	static private ManagerClient _instance = null;

	protected Client mClient;

	/**
	 * 
	 */
	protected ManagerClient()
	{
		mClient = createClient();
	}

	/**
	 * @return
	 */
	static public ManagerClient instance()
	{
		if (null == _instance)
		{
			_instance = new ManagerClient();
		}
		return _instance;
	}

	/**
	 * @param pManagerUri
	 * @return
	 */
	public List<TemplateDocument> getTemplateList(String pManagerUri)
	{
		WebResource webResource = mClient.resource(pManagerUri + "/templates");
		TemplateListDocument templateList = webResource.accept("application/xml").get(TemplateListDocument.class);
		List<TemplateDocument> result = new ArrayList<TemplateDocument>();
		for (String templatetUri : templateList.getTemplateList().getTemplateURIArray())
		{
			webResource = mClient.resource(templatetUri);
			TemplateDocument template = webResource.accept("application/xml").get(TemplateDocument.class);
			result.add(template);
		}
		return result;
	}

	/**
	 * @param pManagerUri
	 * @param pAgreementId
	 * @return
	 */
	public TemplateDocument getTemplate(String pManagerUri, String pTemplateId)
	{
		WebResource webResource = mClient.resource(pManagerUri + "/template/" + pTemplateId);
		TemplateDocument template = webResource.accept("application/xml").get(TemplateDocument.class);
		return template;
	}

	/**
	 * @param pManagerUri
	 * @param pAgreementOffer
	 * @return
	 */

	public AgreementDocument createAgreement(String pManagerUri, AgreementOfferDocument pAgreementOffer)
	{
		WebResource webResource = mClient.resource(pManagerUri + "/agreements");
		ClientResponse response = webResource.type("application/xml").post(ClientResponse.class, pAgreementOffer);
		webResource = mClient.resource(response.getLocation().toASCIIString());
		AgreementDocument agreement = webResource.accept("application/xml").type("application/xml").get(AgreementDocument.class);
		return agreement;
	}

	/**
	 * @param pManagerUri
	 * @return
	 */
	public List<AgreementDocument> getAgreementList(String pManagerUri)
	{
		WebResource webResource = mClient.resource(pManagerUri + "/agreements");
		AgreementListDocument agreementList = webResource.accept("application/xml").get(AgreementListDocument.class);
		List<AgreementDocument> result = new ArrayList<AgreementDocument>();
		for (String agreementUri : agreementList.getAgreementList().getAgreementURIArray())
		{
			webResource = mClient.resource(agreementUri);
			AgreementDocument agreement = webResource.accept("application/xml").get(AgreementDocument.class);
			result.add(agreement);
		}
		return result;
	}

	/**
	 * @param pManagerUri
	 * @param pAgreementState
	 * @return
	 */
	public List<AgreementDocument> getAgreementList(String pManagerUri, AgreementStateDefinition.Enum pAgreementState)
	{
		List<AgreementDocument> result = new ArrayList<AgreementDocument>();
		for (AgreementDocument agreement : getAgreementList(pManagerUri))
		{
			AgreementPropertiesDocument agreementProperties = ManagerClient.instance().getAgreementState(pManagerUri, agreement.getAgreement().getAgreementId());

			if (agreementProperties.getAgreementProperties().getAgreementState().getState() == pAgreementState)
			{
				result.add(agreement);
			}
		}
		return result;
	}

	/**
	 * @param pManagerUri
	 * @param pAgreementId
	 * @return
	 */
	public AgreementDocument getAgreement(String pManagerUri, String pAgreementId)
	{
		WebResource webResource = mClient.resource(pManagerUri + "/agreement/" + pAgreementId);
		AgreementDocument agreement = webResource.accept("application/xml").get(AgreementDocument.class);
		return agreement;
	}

	/**
	 * @param pManagerUri
	 * @param pAgreementId
	 * @return
	 */
	public AgreementPropertiesDocument getAgreementState(String pManagerUri, String pAgreementId)
	{
		WebResource webResource = mClient.resource(pManagerUri + "/agreement/" + pAgreementId + "/state");
		AgreementPropertiesDocument agreementProperties = webResource.accept("application/xml").get(AgreementPropertiesDocument.class);
		return agreementProperties;
	}

	/**
	 * @param pManagerUri
	 * @param pAgreementId
	 * @param pAgreementProperties
	 * @return
	 */
	public AgreementPropertiesDocument updateAgreementState(String pManagerUri, String pAgreementId, AgreementPropertiesDocument pAgreementProperties)
	{
		WebResource webResource = mClient.resource(pManagerUri + "/agreement/" + pAgreementId + "/state");
		AgreementPropertiesDocument agreementProperties = webResource.type("application/xml").accept("application/xml").put(AgreementPropertiesDocument.class, pAgreementProperties);
		return agreementProperties;
	}

	/**
	 * @param pManagerUri
	 * @param pAgreementId
	 * @return
	 */
	public GuaranteeTermType[] getAgreementGuaranteeTerms(String pManagerUri, String pAgreementId)
	{
		WebResource webResource = mClient.resource(pManagerUri + "/agreement/" + pAgreementId + "/state");
		AgreementPropertiesDocument agreementProperties = webResource.accept("application/xml").get(AgreementPropertiesDocument.class);
		return agreementProperties.getAgreementProperties().getTerms().getAll().getGuaranteeTermArray();
	}

	/**
	 * @param pManagerUri
	 * @param pAgreementId
	 * @param pGuaranteeTerm
	 * @return
	 */
	public Enum getGuaranteeTermStatus(String pManagerUri, String pAgreementId, GuaranteeTermType pGuaranteeTerm)
	{
		WebResource webResource = mClient.resource(pManagerUri + "/agreement/" + pAgreementId + "/state");
		AgreementPropertiesDocument agreementProperties = webResource.accept("application/xml").get(AgreementPropertiesDocument.class);
		for (GuaranteeTermStateType guaranteeTermState : agreementProperties.getAgreementProperties().getGuaranteeTermStateArray())
		{
			if (guaranteeTermState.getTermName().equalsIgnoreCase(pGuaranteeTerm.getName()))
			{
				return guaranteeTermState.getState();
			}
		}
		return GuaranteeTermStateDefinition.NOT_DETERMINED;
	}

	/**
	 * @param pManagerUri
	 * @param pAgreementId
	 * @param pServiceDescription
	 * @return
	 */
	public ServicePropertiesDocument addMeasurement(String pManagerUri, String pAgreementId, ServicePropertiesDocument pServiceProperties)
	{
		WebResource webResource = mClient.resource(pManagerUri + "/measurements/" + pAgreementId);
		ClientResponse response = webResource.type("application/xml").post(ClientResponse.class, pServiceProperties);
		// webResource = mClient.resource(response.getLocation().toASCIIString());
		// pServiceDescription = webResource.accept("application/xml").type("application/xml").get(ServiceDescriptionDocument.class);
		return pServiceProperties;
	}

	/**
	 * @param pManagerUri
	 * @param pAgreementId
	 * @return
	 */
	public MeasurementHistoryListDocument getMeasurements(String pManagerUri, String pAgreementId)
	{
		WebResource webResource = mClient.resource(pManagerUri + "/measurement/" + pAgreementId);
		ClientResponse httpResponse = webResource.type("application/xml").get(ClientResponse.class);
		if (httpResponse.getClientResponseStatus() == Status.OK)
		{
			MeasurementHistoryListDocument response = webResource.accept("application/xml").get(MeasurementHistoryListDocument.class);
			return response;
		}
		else
		{
			MeasurementHistoryListDocument response = MeasurementHistoryListDocument.Factory.newInstance();
			response.addNewMeasurementHistoryList();
			return response;
		}

	}

	/**
	 * @return
	 */
	protected Client createClient()
	{
		ClientConfig config = new DefaultClientConfig();

		config.getClasses().add(TemplateListMessageBodyWriterXml.class);
		config.getClasses().add(TemplateListMessageBodyReaderXml.class);

		config.getClasses().add(MeasurementListMessageBodyWriterXml.class);
		config.getClasses().add(MeasurementListMessageBodyReaderXml.class);

		config.getClasses().add(MeasurementHistoryListMessageBodyWriterXml.class);
		config.getClasses().add(MeasurementHistoryListMessageBodyReaderXml.class);

		config.getClasses().add(AgreementListMessageBodyWriterXml.class);
		config.getClasses().add(AgreementListMessageBodyReaderXml.class);

		config.getClasses().add(TemplateMessageBodyWriterXml.class);
		config.getClasses().add(TemplateMessageBodyReaderXml.class);

		config.getClasses().add(AgreementOfferMessageBodyWriterXml.class);
		config.getClasses().add(AgreementOfferMessageBodyReaderXml.class);

		config.getClasses().add(AgreementMessageBodyWriterXml.class);
		config.getClasses().add(AgreementMessageBodyReaderXml.class);

		config.getClasses().add(AgreementPropertiesMessageBodyWriterXml.class);
		config.getClasses().add(AgreementPropertiesMessageBodyReaderXml.class);

		config.getClasses().add(ServiceDescriptionMessageBodyWriterXml.class);
		config.getClasses().add(ServiceDescriptionMessageBodyReaderXml.class);

		config.getClasses().add(ServicePropertiesMessageBodyWriterXml.class);
		config.getClasses().add(ServicePropertiesMessageBodyReaderXml.class);

		Client client = Client.create(config);

		client.addFilter(new LoggingFilter());

		return client;
	}
}
