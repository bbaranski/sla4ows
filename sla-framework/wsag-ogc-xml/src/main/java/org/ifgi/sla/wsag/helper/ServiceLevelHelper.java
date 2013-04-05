package org.ifgi.sla.wsag.helper;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermType;
import org.ifgi.namespaces.wsag.ogc.CustomServiceLevelDocument;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientResponse.Status;

public class ServiceLevelHelper implements Helper<CustomServiceLevelDocument, AgreementType>
{
	protected static Logger LOGGER = Logger.getLogger(ServiceLevelHelper.class);

	static private ServiceLevelHelper _instance = null;

	/**
	 * @return
	 */
	static public ServiceLevelHelper instance()
	{
		if (_instance == null)
		{
			_instance = new ServiceLevelHelper();
		}
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.lang.Object)
	 */
	@Override
	public CustomServiceLevelDocument get(AgreementType pAgreement)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/**
	 * @param pGuaranteeTermTypeArray
	 * @param pTermName
	 * @return
	 * @throws XmlException
	 */
	public CustomServiceLevelDocument get(GuaranteeTermType[] pGuaranteeTermTypeArray, String pTermName)
	{
		for (GuaranteeTermType guaranteeTerm : pGuaranteeTermTypeArray)
		{
			if (guaranteeTerm.getName().equalsIgnoreCase(pTermName))
			{
				return getServiceLevel(guaranteeTerm);
			}
		}
		return null;
	}

	/**
	 * @param pGuaranteeTermTypeArray
	 * @param pTermName
	 * @return
	 */
	public GuaranteeTermType getGuaranteeTermByName(GuaranteeTermType[] pGuaranteeTermTypeArray, String pTermName)
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
	 * @see org.ifgi.sla.wsag.utilities.Helper#getXML(java.lang.Object)
	 */
	@Override
	public String getXML(CustomServiceLevelDocument pCustomServiceLevel) throws Exception
	{
		return PrettyPrinter.indent(pCustomServiceLevel.xmlText());
	}

	/**
	 * @param pGuaranteeTerm
	 * @return
	 * @throws XmlException
	 */
	public CustomServiceLevelDocument getServiceLevel(GuaranteeTermType pGuaranteeTerm)
	{
		if (pGuaranteeTerm.getServiceLevelObjective() != null && pGuaranteeTerm.getServiceLevelObjective().getCustomServiceLevel() != null)
		{
			XmlObject xmlObject = pGuaranteeTerm.getServiceLevelObjective().getCustomServiceLevel();
			NodeList childNodes = xmlObject.getDomNode().getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++)
			{
				if (childNodes.item(i).getLocalName() != null && childNodes.item(i).getLocalName().equalsIgnoreCase("CustomServiceLevel"))
				{
					try
					{
						return CustomServiceLevelDocument.Factory.parse(childNodes.item(i));
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
