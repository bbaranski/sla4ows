package org.ifgi.sla.proxy.client;

import org.apache.log4j.Logger;
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
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;

public class ProxyClient
{
	protected static Logger LOGGER = Logger.getLogger(ProxyClient.class);

	static private ProxyClient _instance = null;

	protected Client mClient;

	/**
	 * 
	 */
	protected ProxyClient()
	{
		mClient = createClient();
	}

	/**
	 * @return
	 */
	static public ProxyClient instance()
	{
		if (null == _instance)
		{
			_instance = new ProxyClient();
		}
		return _instance;
	}

	/**
	 * @param pProxyUri
	 * @param pQuery
	 * @return
	 */
	public String get(String pUrl)
	{
		WebResource webResource = mClient.resource(pUrl);
		String response = webResource.accept("text/plain").get(String.class);
		return response;
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
