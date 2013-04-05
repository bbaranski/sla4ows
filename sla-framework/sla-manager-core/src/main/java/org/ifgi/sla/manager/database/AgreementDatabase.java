package org.ifgi.sla.manager.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ifgi.sla.manager.database.AbstractDatabase;
import org.ifgi.sla.wsag.helper.AgreementHelper;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
public class AgreementDatabase extends AbstractDatabase<AgreementDocument>
{
	protected static Logger LOGGER = Logger.getLogger(AgreementDatabase.class);

	/**
	 * 
	 */
	public AgreementDatabase()
	{
		super(DatabaseType.AGREEMENT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#get()
	 */
	@Override
	public synchronized List<AgreementDocument> get()
	{
		List<AgreementDocument> result = new ArrayList<AgreementDocument>();
		File agreementStore = new File(getDatabaseLocation());
		for (File file : agreementStore.listFiles(new XmlFileFilter()))
		{
			try
			{
				AgreementDocument agreement = AgreementHelper.instance().get(file);
				result.add(agreement);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
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
	public synchronized AgreementDocument get(String pId)
	{
		List<AgreementDocument> agreementList = get();
		for (AgreementDocument agreement : agreementList)
		{
			if (agreement.getAgreement().getAgreementId().equalsIgnoreCase(pId))
			{
				return agreement;
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
	public synchronized AgreementDocument add(AgreementDocument pAgreement)
	{
		try
		{
			String xml = AgreementHelper.instance().getXML(pAgreement);
			saveXML(getDatabaseLocation(), getDatabaseLocation() + pAgreement.getAgreement().getAgreementId() + ".xml", xml);
			return pAgreement;
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
	public synchronized AgreementDocument update(String pId, AgreementDocument pObject)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#remove(java.lang.String)
	 */
	@Override
	public synchronized AgreementDocument remove(String pId)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#remove(java.lang.Object)
	 */
	@Override
	public synchronized AgreementDocument remove(AgreementDocument pObject)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}
}
