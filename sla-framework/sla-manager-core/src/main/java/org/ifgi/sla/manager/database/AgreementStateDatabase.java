package org.ifgi.sla.manager.database;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ifgi.sla.wsag.helper.AgreementPropertiesHelper;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
public class AgreementStateDatabase extends AbstractDatabase<AgreementPropertiesDocument>
{
	protected static Logger LOGGER = Logger.getLogger(AgreementStateDatabase.class);

	/**
	 * 
	 */
	public AgreementStateDatabase()
	{
		super(DatabaseType.STATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#get()
	 */
	@Override
	public synchronized List<AgreementPropertiesDocument> get()
	{
		List<AgreementPropertiesDocument> result = new ArrayList<AgreementPropertiesDocument>();
		File agreementPropertiesStore = new File(getDatabaseLocation());
		for (File file : agreementPropertiesStore.listFiles(new XmlFileFilter()))
		{
			try
			{
				AgreementPropertiesDocument agreementProperties = AgreementPropertiesHelper.instance().get(file);
				result.add(agreementProperties);
			}
			catch (Exception e)
			{
				LOGGER.error(e);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#get(java.lang.String)
	 */
	@Override
	public synchronized AgreementPropertiesDocument get(String pId)
	{
		List<AgreementPropertiesDocument> agreementPropertiesList = get();
		for (AgreementPropertiesDocument agreementProperties : agreementPropertiesList)
		{
			if (agreementProperties.getAgreementProperties().getAgreementId().equalsIgnoreCase(pId))
			{
				return agreementProperties;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#add(java.lang.Object)
	 */
	@Override
	public synchronized AgreementPropertiesDocument add(AgreementPropertiesDocument pAgreementProperties)
	{
		try
		{
			String xml = AgreementPropertiesHelper.instance().getXML(pAgreementProperties);
			saveXML(getDatabaseLocation(), getDatabaseLocation() + pAgreementProperties.getAgreementProperties().getAgreementId() + ".xml", xml);
			return pAgreementProperties;
		}
		catch (Exception e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.AbstractDatabase#update(java.lang.String, java.lang.Object)
	 */
	@Override
	public synchronized AgreementPropertiesDocument update(String pId, AgreementPropertiesDocument pAgreementProperties)
	{
		try
		{
			String xml = AgreementPropertiesHelper.instance().getXML(pAgreementProperties);
			saveXML(getDatabaseLocation(), getDatabaseLocation() + pAgreementProperties.getAgreementProperties().getAgreementId() + ".xml", xml);
			return pAgreementProperties;
		}
		catch (Exception e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#remove(java.lang.String)
	 */
	@Override
	public synchronized AgreementPropertiesDocument remove(String pId)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#remove(java.lang.Object)
	 */
	@Override
	public synchronized AgreementPropertiesDocument remove(AgreementPropertiesDocument pObject)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}
}
