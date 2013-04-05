package org.ifgi.sla.wsag.helper;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;

public class AgreementPropertiesHelper implements Helper<AgreementPropertiesDocument, File>
{
	protected static Logger LOGGER = Logger.getLogger(AgreementPropertiesHelper.class);

	static private AgreementPropertiesHelper _instance = null;

	static public AgreementPropertiesHelper instance()
	{
		if (_instance == null)
		{
			_instance = new AgreementPropertiesHelper();
		}
		return _instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#get(java.io.File)
	 */
	public AgreementPropertiesDocument get(File pFile) throws Exception
	{
		return AgreementPropertiesDocument.Factory.parse(pFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.wsag.utilities.Helper#getXML(java.lang.Object)
	 */
	public String getXML(AgreementPropertiesDocument pAgreementProperties) throws Exception
	{
		return PrettyPrinter.indent(pAgreementProperties.xmlText());
	}
}
