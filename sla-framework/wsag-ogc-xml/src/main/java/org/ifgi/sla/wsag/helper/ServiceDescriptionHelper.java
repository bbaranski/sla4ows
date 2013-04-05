package org.ifgi.sla.wsag.helper;

import java.io.File;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceDescriptionTermType;
import org.ifgi.namespaces.wsag.ogc.ServiceDescriptionDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientResponse.Status;

public class ServiceDescriptionHelper implements Helper<ServiceDescriptionDocument, AgreementType>
{
	protected static Logger LOGGER = Logger.getLogger(ServiceDescriptionHelper.class);

	static private ServiceDescriptionHelper _instance = null;

	/**
	 * @return
	 */
	static public ServiceDescriptionHelper instance()
	{
		if (_instance == null)
		{
			_instance = new ServiceDescriptionHelper();
		}
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.lang.Object)
	 */
	@Override
	public ServiceDescriptionDocument get(AgreementType pAgreement)
	{
		ServiceDescriptionTermType[] serviceDescriptionArray = pAgreement.getTerms().getAll().getServiceDescriptionTermArray();
		return getServiceDescription(serviceDescriptionArray);
	}

	/**
	 * @param pAgreementProperties
	 * @return
	 */
	public ServiceDescriptionDocument get(AgreementPropertiesDocument pAgreementProperties)
	{
		ServiceDescriptionTermType[] serviceDescriptionArray = pAgreementProperties.getAgreementProperties().getTerms().getAll()
				.getServiceDescriptionTermArray();
		return getServiceDescription(serviceDescriptionArray);
	}

	/**
	 * @param serviceDescriptionArray
	 * @return
	 */
	protected ServiceDescriptionDocument getServiceDescription(ServiceDescriptionTermType[] serviceDescriptionArray)
	{
		Node node = null;
		for (int i = 0; i < serviceDescriptionArray.length; i++)
		{
			NodeList nodeList = serviceDescriptionArray[i].getDomNode().getChildNodes();
			for (int j = 0; j < nodeList.getLength(); j++)
			{
				if (nodeList.item(j).getLocalName() != null && nodeList.item(j).getLocalName().equalsIgnoreCase("ServiceDescription"))
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
			return null;
		}
		ServiceDescriptionDocument document;
		try
		{
			document = ServiceDescriptionDocument.Factory.parse(node, new XmlOptions());
		}
		catch (XmlException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());

		}
		return document;
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
	public String getXML(ServiceDescriptionDocument pServiceDescription) throws Exception
	{
		return PrettyPrinter.indent(pServiceDescription.xmlText());
	}

	/**
	 * @param serviceDescriptionTerm
	 * @return
	 */
	public boolean isServiceDescription(ServiceDescriptionTermType pServiceDescriptionTerm)
	{
		NodeList nodeList = pServiceDescriptionTerm.getDomNode().getChildNodes();
		boolean isServiceDescription = false;
		for (int j = 0; j < nodeList.getLength(); j++)
		{
			if (nodeList.item(j).getLocalName() != null && nodeList.item(j).getLocalName().equalsIgnoreCase("ServiceDescription"))
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
