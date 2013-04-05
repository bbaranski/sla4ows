package org.ifgi.sla.wsag.helper;

import java.io.File;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceDescriptionTermType;
import org.ifgi.namespaces.wsag.ogc.PropertyType;
import org.ifgi.namespaces.wsag.ogc.ServiceDescriptionDocument;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientResponse.Status;

public class ServicePropertiesHelper implements Helper<ServicePropertiesDocument, AgreementType>
{
	protected static Logger LOGGER = Logger.getLogger(ServicePropertiesHelper.class);

	static private ServicePropertiesHelper _instance = null;

	/**
	 * @return
	 */
	static public ServicePropertiesHelper instance()
	{
		if (_instance == null)
		{
			_instance = new ServicePropertiesHelper();
		}
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.lang.Object)
	 */
	@Override
	public ServicePropertiesDocument get(AgreementType pAgreement)
	{
		ServiceDescriptionTermType[] serviceDescriptionArray = pAgreement.getTerms().getAll().getServiceDescriptionTermArray();
		Node node = null;
		for (int i = 0; i < serviceDescriptionArray.length; i++)
		{
			NodeList nodeList = serviceDescriptionArray[i].getDomNode().getChildNodes();
			for (int j = 0; j < nodeList.getLength(); j++)
			{
				if (nodeList.item(j).getLocalName() != null && nodeList.item(j).getLocalName().equalsIgnoreCase("ServiceProperties"))
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
		ServicePropertiesDocument document;
		try
		{
			document = ServicePropertiesDocument.Factory.parse(node, new XmlOptions());
		}
		catch (XmlException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		return document;
	}
	
	/**
	 * @param pAgreementProperties
	 * @return
	 */
	public ServicePropertiesDocument get(AgreementPropertiesType pAgreementProperties)
	{
		ServiceDescriptionTermType[] serviceDescriptionArray = pAgreementProperties.getTerms().getAll().getServiceDescriptionTermArray();
		Node node = null;
		for (int i = 0; i < serviceDescriptionArray.length; i++)
		{
			NodeList nodeList = serviceDescriptionArray[i].getDomNode().getChildNodes();
			for (int j = 0; j < nodeList.getLength(); j++)
			{
				if (nodeList.item(j).getLocalName() != null && nodeList.item(j).getLocalName().equalsIgnoreCase("ServiceProperties"))
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
		ServicePropertiesDocument document;
		try
		{
			document = ServicePropertiesDocument.Factory.parse(node, new XmlOptions());
		}
		catch (XmlException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		return document;
	}

	/**
	 * @param pAgreement
	 * @param pType
	 * @return
	 */
	public String getPropertieValue(AgreementType pAgreement, String pType)
	{
		ServicePropertiesDocument ServiceProperties = get(pAgreement);
		for (PropertyType property : ServiceProperties.getServiceProperties().getPropertyArray())
		{
			if (property.getType().equalsIgnoreCase(pType))
			{
				return property.getValue();
			}
		}
		return null;
	}
	
	/**
	 * @param pFile
	 * @return
	 * @throws Exception
	 */
	public ServiceDescriptionDocument get(File pFile) throws Exception
	{
		return ServiceDescriptionDocument.Factory.parse(pFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#getXML(java.lang.Object)
	 */
	@Override
	public String getXML(ServicePropertiesDocument pServiceProperties) throws Exception
	{
		return PrettyPrinter.indent(pServiceProperties.xmlText());
	}

	/**
	 * @param serviceDescriptionTerm
	 * @return
	 */
	public boolean isServiceProperties(ServiceDescriptionTermType pServiceDescriptionTerm)
	{
		NodeList nodeList = pServiceDescriptionTerm.getDomNode().getChildNodes();
		boolean isServiceDescription = false;
		for (int j = 0; j < nodeList.getLength(); j++)
		{
			if (nodeList.item(j).getLocalName() != null && nodeList.item(j).getLocalName().equalsIgnoreCase("ServiceProperties"))
			{
				isServiceDescription = true;
				break;
			}
		}
		return isServiceDescription;
	}

	/**
	 * @param serviceDescriptionTerm
	 * @return
	 */
	public boolean isTimeConstraint(ServiceDescriptionTermType pServiceDescriptionTerm)
	{
		NodeList nodeList = pServiceDescriptionTerm.getDomNode().getChildNodes();
		boolean isTimeConstraint = false;
		for (int j = 0; j < nodeList.getLength(); j++)
		{
			if (nodeList.item(j).getLocalName() != null && nodeList.item(j).getLocalName().equalsIgnoreCase("TimeConstraint"))
			{
				isTimeConstraint = true;
				break;
			}
		}
		return isTimeConstraint;
	}
}
