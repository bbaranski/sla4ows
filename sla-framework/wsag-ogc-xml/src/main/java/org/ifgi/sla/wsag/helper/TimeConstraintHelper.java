package org.ifgi.sla.wsag.helper;

import java.util.Calendar;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementOfferDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceDescriptionTermType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wsag4J.schemas.x2009.x07.wsag4JSchedulingExtensions.TimeConstraintDocument;

import com.sun.jersey.api.client.ClientResponse.Status;

public class TimeConstraintHelper implements Helper<TimeConstraintDocument, AgreementType>
{
	protected static Logger LOGGER = Logger.getLogger(TimeConstraintHelper.class);

	static private TimeConstraintHelper _instance = null;

	/**
	 * @return
	 */
	static public TimeConstraintHelper instance()
	{
		if (_instance == null)
		{
			_instance = new TimeConstraintHelper();
		}
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.lang.Object)
	 */
	@Override
	public TimeConstraintDocument get(AgreementType pAgreement)
	{
		ServiceDescriptionTermType[] serviceDescriptionArray = pAgreement.getTerms().getAll().getServiceDescriptionTermArray();
		return getTimeConstraint(serviceDescriptionArray);
	}

	/**
	 * @param pAgreementProperties
	 * @return
	 */
	public TimeConstraintDocument get(AgreementPropertiesDocument pAgreementProperties)
	{
		ServiceDescriptionTermType[] serviceDescriptionArray = pAgreementProperties.getAgreementProperties().getTerms().getAll()
				.getServiceDescriptionTermArray();
		return getTimeConstraint(serviceDescriptionArray);
	}
	
	/**
	 * @param pStartTime
	 * @param pEndTime
	 * @return
	 * @throws Exception
	 */
	public TimeConstraintDocument get(Calendar pStartTime, Calendar pEndTime) throws Exception
	{
		TimeConstraintDocument document = TimeConstraintDocument.Factory.newInstance();
		document.addNewTimeConstraint();
		document.getTimeConstraint().setStartTime(pStartTime);
		document.getTimeConstraint().setEndTime(pEndTime);
		return document;
	}
	
	/**
	 * @param pAgreementOffer
	 * @param pTimeConstraint
	 */
	public void setTimeConstraint(AgreementOfferDocument pAgreementOffer, TimeConstraintDocument pTimeConstraint)
	{
		ServiceDescriptionTermType[] serviceDescriptionArray = pAgreementOffer.getAgreementOffer().getTerms().getAll().getServiceDescriptionTermArray();
		
		for (int i = 0; i < serviceDescriptionArray.length; i++)
		{
			NodeList nodeList = serviceDescriptionArray[i].getDomNode().getChildNodes();
			for (int j = 0; j < nodeList.getLength(); j++)
			{
				if (nodeList.item(j).getLocalName() != null && nodeList.item(j).getLocalName().equalsIgnoreCase("TimeConstraint"))
				{
					serviceDescriptionArray[i].set(pTimeConstraint);
					serviceDescriptionArray[i].setName("TIME_CONSTRAINT_SDT");
					String serviceName = pAgreementOffer.getAgreementOffer().getTerms().getAll().getServiceReferenceArray()[0].getServiceName();
					serviceDescriptionArray[i].setServiceName(serviceName);
					return;
				}
			}
		}
		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
	}

	/**
	 * @param serviceDescriptionArray
	 * @return
	 */
	protected TimeConstraintDocument getTimeConstraint(ServiceDescriptionTermType[] serviceDescriptionArray)
	{
		Node node = null;
		for (int i = 0; i < serviceDescriptionArray.length; i++)
		{
			NodeList nodeList = serviceDescriptionArray[i].getDomNode().getChildNodes();
			for (int j = 0; j < nodeList.getLength(); j++)
			{
				if (nodeList.item(j).getLocalName() != null && nodeList.item(j).getLocalName().equalsIgnoreCase("TimeConstraint"))
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
		TimeConstraintDocument document;
		try
		{
			document = TimeConstraintDocument.Factory.parse(node, new XmlOptions());
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
	public String getXML(TimeConstraintDocument pTime) throws Exception
	{
		return PrettyPrinter.indent(pTime.xmlText());
	}
}
