package org.ifgi.sla.infrastructure.client;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
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
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;

public class InfrastructureClient
{
	protected static Logger LOGGER = Logger.getLogger(InfrastructureClient.class);

	static private InfrastructureClient _instance = null;

	protected Client mClient;

	/**
	 * 
	 */
	protected InfrastructureClient()
	{
		mClient = createClient();
	}

	/**
	 * @return
	 */
	static public InfrastructureClient instance()
	{
		if (null == _instance)
		{
			_instance = new InfrastructureClient();
		}
		return _instance;
	}

	/**
	 * @param pManagerUri
	 * @return
	 */
	public void schedule(String pInfrastructureUri, AgreementDocument pAgreement)
	{
		WebResource webResource = mClient.resource(pInfrastructureUri + "/schedules");
		ClientResponse response = webResource.type("application/xml").post(ClientResponse.class, pAgreement);
	}

	/**
	 * @param pManagerUri
	 * @return
	 */
	public String getAuthority(String pInfrastructureUri, String pAgreementId)
	{
		WebResource webResource = mClient.resource(pInfrastructureUri + "/schedule/" + pAgreementId);
		String authority = webResource.accept("text/plain").get(String.class);
		return authority;
	}

	/**
	 * @return
	 */
	protected Client createClient()
	{
		ClientConfig config = new DefaultClientConfig();

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
