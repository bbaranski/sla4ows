package org.ifgi.sla.infrastructure.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
public class DefaultDatabase extends AbstractDatabase<Properties>
{
	protected static Logger LOGGER = Logger.getLogger(DefaultDatabase.class);

	/**
	 * 
	 */
	public DefaultDatabase()
	{
		super(DatabaseType.DEFAULT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.DataStore#get()
	 */
	@Override
	public synchronized List<Properties> get()
	{
		List<Properties> result = new ArrayList<Properties>();
		File agreementStore = new File(getDatabaseLocation());
		for (File file : agreementStore.listFiles(new PropertiesFileFilter()))
		{
			try
			{
				Properties properties = new Properties();
				BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
				properties.load(stream);
				Properties configuration = new Properties(properties);
				result.add(configuration);
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
	 * @see org.ifgi.sla.infrastructure.database.AbstractDatabase#get(java.lang.String)
	 */
	@Override
	public Properties get(String pId)
	{
		File agreementStore = new File(getDatabaseLocation());
		for (File file : agreementStore.listFiles(new PropertiesFileFilter()))
		{
			if (file.getName().startsWith(pId))
			{
				try
				{
					Properties properties = new Properties();
					BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
					properties.load(stream);
					return properties;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
				}
			}
		}
		LOGGER.error("Could not find infrastructure configuration for '" + pId + "'.");
		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.database.AbstractDatabase#add(java.lang.Object)
	 */
	@Override
	public Properties add(String pId, Properties pObject)
	{
		try
		{
			pObject.store(new FileOutputStream(new File(getDatabaseLocation() + pId + ".properties")), null);
		}
		catch (FileNotFoundException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		catch (IOException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		return pObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.database.AbstractDatabase#update(java.lang.String, java.lang.Object)
	 */
	@Override
	public Properties update(String pId, Properties pObject)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.database.AbstractDatabase#remove(java.lang.String)
	 */
	@Override
	public Properties remove(String pId)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.database.AbstractDatabase#remove(java.lang.Object)
	 */
	@Override
	public Properties remove(Properties pObject)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

}
