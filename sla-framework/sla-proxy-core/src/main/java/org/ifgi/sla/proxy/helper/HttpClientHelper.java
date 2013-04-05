package org.ifgi.sla.proxy.helper;

import java.io.IOException;
import java.util.Random;

import javax.ws.rs.WebApplicationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ifgi.namespaces.wsag.ogc.PropertyType;
import org.ifgi.namespaces.wsag.ogc.ServiceReferenceDocument;
import org.ifgi.sla.wsag.helper.ServiceReferenceHelper;

import com.sun.jersey.api.client.ClientResponse.Status;

public class HttpClientHelper
{
	protected static Logger LOGGER = Logger.getLogger(HttpClientHelper.class);

	static private HttpClientHelper _instance = null;

	/**
	 * @return
	 */
	static public HttpClientHelper instance()
	{
		if (_instance == null)
		{
			_instance = new HttpClientHelper();
		}
		return _instance;
	}

	/**
	 * @param pAgreement
	 * @param pServiceProperty
	 * @return
	 */
	public HttpResponse execute(AgreementDocument pAgreement, PropertyType pServiceProperty)
	{
		ServiceReferenceDocument serviceReference = ServiceReferenceHelper.instance().get(pAgreement.getAgreement());
		String serviceUri = serviceReference.getServiceReference().getURL();

		String serviceQuery = "";

		if (pServiceProperty.getMonitoring().getActiveMonitoring() != null)
		{
			if (pServiceProperty.getMonitoring().getActiveMonitoring().getRequestArray() != null)
			{
				if (pServiceProperty.getMonitoring().getActiveMonitoring().getRequestArray()[0].getMethod().equalsIgnoreCase("GET"))
				{
					serviceQuery = pServiceProperty.getMonitoring().getActiveMonitoring().getRequestArray()[0].getContent();
					serviceQuery = prepareQuery(serviceQuery);
					
					return doGet(serviceUri, serviceQuery);
				}
				else
				{
					throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
				}
			}
		}
		else if (pServiceProperty.getMonitoring().getPassiveMonitoring() != null)
		{
			throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
		}
		return doGet(serviceUri, serviceQuery);

	}

	protected String prepareQuery(String pQuery)
	{
		String result = "";
		
		String random = "${__random(";
		
		int fromIndex = 0;
		int randomStart = pQuery.indexOf(random, fromIndex);
		while (randomStart > 0)
		{
			result += pQuery.substring(fromIndex, randomStart);
				
			int randomEnd = pQuery.indexOf(")", randomStart);
			
			String values = pQuery.substring(randomStart + random.length(), randomEnd);
			
			int tmp = values.indexOf(",");
			String num1 = values.substring(0, tmp);
			String num2 = values.substring(tmp + 1);
			
			 
			result += num1;
			
			fromIndex = randomEnd + 2;
			randomStart = pQuery.indexOf(random, randomEnd);
		}
		
		result += pQuery.substring(fromIndex);
		
		
		return result;
	}
	
	/**
	 * @param serviceUri
	 * @param serviceQuery
	 * @return
	 */
	public HttpResponse doGet(String serviceUri, String serviceQuery)
	{
		if (!serviceUri.startsWith("http://"))
		{
			serviceUri = "http://" + serviceUri;
		}
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(serviceUri + "?" + serviceQuery);

		LOGGER.info("HTTP-GET : " + serviceUri + "?" + serviceQuery);

		HttpResponse response;
		try
		{
			response = httpclient.execute(httpget);
		}
		catch (ClientProtocolException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.SERVICE_UNAVAILABLE.getStatusCode());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			LOGGER.error(e);
			throw new WebApplicationException(Status.SERVICE_UNAVAILABLE.getStatusCode());
		}

		return response;
	}

	private HttpResponse doPost(String serviceUri, String serviceQuery)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}
}
