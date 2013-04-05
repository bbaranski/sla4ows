package org.ifgi.sla.wsag.helper;

import javax.ws.rs.WebApplicationException;

import net.opengis.ows.x20.OnlineResourceType;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementContextType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.TemplateDocument;
import org.ifgi.namespaces.wsag.ogc.ContactDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientResponse.Status;

public class ContactDetailsHelper implements Helper<ContactDocument, AgreementType>
{
	protected static Logger LOGGER = Logger.getLogger(ContactDetailsHelper.class);

	static private ContactDetailsHelper _instance = null;

	/**
	 * @return
	 */
	static public ContactDetailsHelper instance()
	{
		if (_instance == null)
		{
			_instance = new ContactDetailsHelper();
		}
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.lang.Object)
	 */
	@Override
	public ContactDocument get(AgreementType pAgreement) throws Exception
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/**
	 * @param pTemplate
	 * @return
	 * @throws Exception
	 */
	public ContactDocument getServiceProvider(TemplateDocument pTemplate) throws Exception
	{
		NodeList nodeList = pTemplate.getTemplate().getContext().getAgreementResponder().getDomNode().getChildNodes();
		Node node = null;
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			if (nodeList.item(i).getLocalName() != null && nodeList.item(i).getLocalName().equalsIgnoreCase("Contact"))
			{
				node = nodeList.item(i);
				break;
			}
		}
		ContactDocument document = ContactDocument.Factory.parse(node, new XmlOptions());
		return document;
	}

	/**
	 * @param pAgreement
	 * @return
	 * @throws Exception
	 */
	public ContactDocument getServiceProvider(AgreementDocument pAgreement) throws Exception
	{
		return getServiceProvider(pAgreement.getAgreement().getContext());
	}

	/**
	 * @param pAgreement
	 * @return
	 * @throws Exception
	 */
	public ContactDocument getServiceProvider(AgreementPropertiesDocument pAgreement) throws Exception
	{
		return getServiceProvider(pAgreement.getAgreementProperties().getContext());
	}

	/**
	 * @param pContext
	 * @return
	 * @throws Exception
	 */
	public ContactDocument getServiceProvider(AgreementContextType pContext) throws Exception
	{
		NodeList nodeList = pContext.getAgreementResponder().getDomNode().getChildNodes();
		Node node = null;
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			if (nodeList.item(i).getLocalName() != null && nodeList.item(i).getLocalName().equalsIgnoreCase("Contact"))
			{
				node = nodeList.item(i);
				break;
			}
		}
		ContactDocument document = ContactDocument.Factory.parse(node, new XmlOptions());
		return document;
	}
	
	/**
	 * @param pAgreement
	 * @return
	 * @throws Exception
	 */
	public ContactDocument getServiceConsumer(AgreementDocument pAgreement) throws Exception
	{
		NodeList nodeList = pAgreement.getAgreement().getContext().getAgreementInitiator().getDomNode().getChildNodes();
		Node node = null;
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			if (nodeList.item(i).getLocalName() != null && nodeList.item(i).getLocalName().equalsIgnoreCase("Contact"))
			{
				node = nodeList.item(i);
				break;
			}
		}
		ContactDocument document = ContactDocument.Factory.parse(node, new XmlOptions());
		return document;
	}

	/**
	 * @param pName
	 * @param pSite
	 * @param pIndividualName
	 * @param pPositionName
	 * @param pVoice
	 * @param pFacsimile
	 * @param pUrl
	 * @param pDeliveryPoint
	 * @param pCity
	 * @param pPostalCode
	 * @param pCountry
	 * @param pMailAddress
	 * @return
	 * @throws Exception
	 */
	public ContactDocument get(String pName, String pSite, String pIndividualName, String pPositionName, String pVoice, String pFacsimile, String pUrl, String pDeliveryPoint, String pCity, String pPostalCode, String pCountry,
			String pMailAddress) throws Exception
	{
		ContactDocument document = ContactDocument.Factory.newInstance();
		document.addNewContact();
		document.getContact().setName(pName);
		document.getContact().setSite(OnlineResourceType.Factory.newInstance());
		document.getContact().getSite().setHref(pSite);
		document.getContact().addNewContact();
		document.getContact().getContact().setIndividualName(pIndividualName);
		document.getContact().getContact().setPositionName(pPositionName);
		document.getContact().getContact().addNewContactInfo();
		document.getContact().getContact().getContactInfo().addNewPhone();
		String voiceArray[] = { pVoice };
		document.getContact().getContact().getContactInfo().getPhone().setVoiceArray(voiceArray);
		String facsimileArray[] = { pFacsimile };
		document.getContact().getContact().getContactInfo().getPhone().setFacsimileArray(facsimileArray);
		document.getContact().getContact().getContactInfo().addNewOnlineResource();
		document.getContact().getContact().getContactInfo().getOnlineResource().setHref(pUrl);
		document.getContact().getContact().getContactInfo().addNewAddress();
		String deliveryPointArray[] = { pDeliveryPoint };
		document.getContact().getContact().getContactInfo().getAddress().setDeliveryPointArray(deliveryPointArray);
		document.getContact().getContact().getContactInfo().getAddress().setCity(pCity);
		document.getContact().getContact().getContactInfo().getAddress().setPostalCode(pPostalCode);
		document.getContact().getContact().getContactInfo().getAddress().setCountry(pCountry);
		document.getContact().getContact().getContactInfo().getAddress().addElectronicMailAddress(pMailAddress);
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#getXML(java.lang.Object)
	 */
	@Override
	public String getXML(ContactDocument pContactDetails) throws Exception
	{
		return PrettyPrinter.indent(pContactDetails.xmlText());
	}
}
