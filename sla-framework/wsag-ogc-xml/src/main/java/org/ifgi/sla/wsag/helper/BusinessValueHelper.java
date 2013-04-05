package org.ifgi.sla.wsag.helper;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermType;
import org.ifgi.namespaces.wsag.ogc.CustomBusinessValueDocument;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientResponse.Status;

public class BusinessValueHelper implements Helper<CustomBusinessValueDocument, AgreementType>
{
	protected static Logger LOGGER = Logger.getLogger(BusinessValueHelper.class);

	static private BusinessValueHelper _instance = null;

	/**
	 * @return
	 */
	static public BusinessValueHelper instance()
	{
		if (_instance == null)
		{
			_instance = new BusinessValueHelper();
		}
		return _instance;
	}

	/**
	 * @param pObject
	 * @return
	 */
	public CustomBusinessValueDocument get(XmlObject pObject)
	{
		try
		{
			return CustomBusinessValueDocument.Factory.parse(pObject.getDomNode());
		}
		catch (XmlException e)
		{
			e.printStackTrace();
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}

	/**
	 * @param pGuaranteeTermTypeArray
	 * @param pTermName
	 * @return
	 * @throws XmlException
	 */
	public CustomBusinessValueDocument get(GuaranteeTermType[] pGuaranteeTermTypeArray, String pTermName)
	{
		for (GuaranteeTermType guaranteeTerm : pGuaranteeTermTypeArray)
		{
			if (guaranteeTerm.getName().equalsIgnoreCase(pTermName))
			{
				return getBusinessValue(guaranteeTerm);
			}
		}
		return null;
	}

	/**
	 * @param pGuaranteeTermTypeArray
	 * @param pTermName
	 * @return
	 */
	protected GuaranteeTermType getGuaranteeTermByName(GuaranteeTermType[] pGuaranteeTermTypeArray, String pTermName)
	{
		for (GuaranteeTermType guarenteeTerm : pGuaranteeTermTypeArray)
		{
			if (guarenteeTerm.getName().equalsIgnoreCase(pTermName))
			{
				return guarenteeTerm;
			}
		}
		LOGGER.error("Found no guarantee term with name '" + pTermName + "' in agreement properties document.");
		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.lang.Object)
	 */
	@Override
	public CustomBusinessValueDocument get(AgreementType pAgreement)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#getXML(java.lang.Object)
	 */
	@Override
	public String getXML(CustomBusinessValueDocument pCustomeBusinessValue) throws Exception
	{
		return PrettyPrinter.indent(pCustomeBusinessValue.xmlText());
	}

	/**
	 * @param pGuaranteeTerm
	 * @return
	 * @throws XmlException
	 */
	public CustomBusinessValueDocument getBusinessValue(GuaranteeTermType pGuaranteeTerm)
	{
		if (pGuaranteeTerm.getBusinessValueList() != null && pGuaranteeTerm.getBusinessValueList().getCustomBusinessValueArray() != null
				&& pGuaranteeTerm.getBusinessValueList().getCustomBusinessValueArray().length == 1)
		{
			XmlObject xmlObject = pGuaranteeTerm.getBusinessValueList().getCustomBusinessValueArray()[0];
			NodeList childNodes = xmlObject.getDomNode().getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++)
			{
				if (childNodes.item(i).getLocalName() != null && childNodes.item(i).getLocalName().equalsIgnoreCase("CustomBusinessValue"))
				{
					try
					{
						return CustomBusinessValueDocument.Factory.parse(childNodes.item(i));
					}
					catch (XmlException e)
					{
						LOGGER.error(e);
						throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
					}
				}
			}
		}
		return null;
	}
}
