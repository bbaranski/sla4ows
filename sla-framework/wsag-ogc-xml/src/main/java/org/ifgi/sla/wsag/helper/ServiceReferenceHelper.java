package org.ifgi.sla.wsag.helper;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceReferenceType;
import org.ifgi.namespaces.wsag.ogc.ServiceReferenceDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientResponse.Status;

public class ServiceReferenceHelper implements Helper<ServiceReferenceDocument, AgreementType>
{
	protected static Logger LOGGER = Logger.getLogger(ServiceReferenceHelper.class);

	static private ServiceReferenceHelper _instance = null;

	/**
	 * @return
	 */
	static public ServiceReferenceHelper instance()
	{
		if (_instance == null)
		{
			_instance = new ServiceReferenceHelper();
		}
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.lang.Object)
	 */
	@Override
	public ServiceReferenceDocument get(AgreementType pAgreement)
	{
		ServiceReferenceType[] serviceDescriptionArray = pAgreement.getTerms().getAll().getServiceReferenceArray();
		Node node = null;
		for (int i = 0; i < serviceDescriptionArray.length; i++)
		{
			NodeList nodeList = serviceDescriptionArray[i].getDomNode().getChildNodes();
			for (int j = 0; j < nodeList.getLength(); j++)
			{
				if (nodeList.item(j).getLocalName() != null && nodeList.item(j).getLocalName().equalsIgnoreCase("ServiceReference"))
				{
					node = nodeList.item(j);
					break;
				}
			}
			if (node != null)
			{
				break;
			}

		}
		
		if (node == null)
		{
			LOGGER.error("Could not find a ServiceReference element in the agreement with id '" + pAgreement.getAgreementId() + "'.");
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());	
		}
		
		ServiceReferenceDocument document;
		try
		{
			document = ServiceReferenceDocument.Factory.parse(node, new XmlOptions());
		}
		catch (XmlException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		return document;
	}
	
	public ServiceReferenceDocument get(ServiceReferenceType[] pServiceReferenceArray)
	{
		Node node = null;
		for (int i = 0; i < pServiceReferenceArray.length; i++)
		{
			NodeList nodeList = pServiceReferenceArray[i].getDomNode().getChildNodes();
			for (int j = 0; j < nodeList.getLength(); j++)
			{
				if (nodeList.item(j).getLocalName() != null && nodeList.item(j).getLocalName().equalsIgnoreCase("ServiceReference"))
				{
					node = nodeList.item(j);
					break;
				}
			}
			if (node != null)
			{
				break;
			}

		}
		
		if (node == null)
		{
			LOGGER.error("Could not find a ServiceReference element in the agreement.");
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());	
		}
		
		ServiceReferenceDocument document;
		try
		{
			document = ServiceReferenceDocument.Factory.parse(node, new XmlOptions());
		}
		catch (XmlException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#getXML(java.lang.Object)
	 */
	@Override
	public String getXML(ServiceReferenceDocument pServiceReference) throws Exception
	{
		return PrettyPrinter.indent(pServiceReference.xmlText());
	}
}
