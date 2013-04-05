package org.ifgi.sla.manager.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.TemplateDocument;
import org.ifgi.sla.wsag.helper.TemplateHelper;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
public class TemplateDatabase extends AbstractDatabase<TemplateDocument>
{
	protected static Logger LOGGER = Logger.getLogger(TemplateDatabase.class);

	/**
	 * 
	 */
	public TemplateDatabase()
	{
		super(DatabaseType.TEMPLATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#get()
	 */
	@Override
	public synchronized List<TemplateDocument> get()
	{
		List<TemplateDocument> result = new ArrayList<TemplateDocument>();
		File agreementStore = new File(getDatabaseLocation());
		for (File file : agreementStore.listFiles(new XmlFileFilter()))
		{
			try
			{
				TemplateDocument template = TemplateHelper.instance().get(file);
				result.add(template);
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
	public synchronized TemplateDocument get(String pId)
	{
		List<TemplateDocument> templateList = get();
		for (TemplateDocument template : templateList)
		{
			if (template.getTemplate().getTemplateId().equalsIgnoreCase(pId))
			{
				return template;
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
	public synchronized TemplateDocument add(TemplateDocument pTemplate)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}
	
	/* (non-Javadoc)
	 * @see org.ifgi.sla.manager.database.AbstractDatabase#update(java.lang.String, java.lang.Object)
	 */
	@Override
	public synchronized TemplateDocument update(String pId, TemplateDocument pObject)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#remove(java.lang.String)
	 */
	@Override
	public synchronized TemplateDocument remove(String pId)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#remove(java.lang.Object)
	 */
	@Override
	public synchronized TemplateDocument remove(TemplateDocument pTemplate)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}
}
