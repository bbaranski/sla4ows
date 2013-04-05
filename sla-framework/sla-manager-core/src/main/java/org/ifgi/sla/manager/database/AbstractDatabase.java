package org.ifgi.sla.manager.database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.ClientResponse.Status;

public abstract class AbstractDatabase<T extends Object>
{
	protected static Logger LOGGER = Logger.getLogger(AbstractDatabase.class);

	public enum DatabaseType
	{
		TEMPLATE, AGREEMENT, STATE, MEASUREMENT
	}

	protected DatabaseType mDatabaseType;

	/**
	 * @param pDatabaseType
	 * @param pModelType
	 */
	public AbstractDatabase(DatabaseType pDatabaseType)
	{
		mDatabaseType = pDatabaseType;
	}

	/**
	 * @return
	 */
	public abstract List<T> get();

	/**
	 * @param pId
	 * @return
	 */
	public abstract T get(String pId);

	/**
	 * @param pObject
	 * @return
	 */
	public abstract T add(T pObject);

	/**
	 * @param pId
	 * @param pObject
	 * @return
	 */
	public abstract T update(String pId, T pObject);

	/**
	 * @param pId
	 * @return
	 */
	public abstract T remove(String pId);

	/**
	 * @param pObject
	 * @return
	 */
	public abstract T remove(T pObject);

	/**
	 * @param xml
	 * @param folderName
	 * @param fileName
	 * @throws IOException
	 */
	protected void saveXML(String pFolderName, String pFileName, String pXml) throws IOException
	{
		File folder = new File(pFolderName);
		if (folder.exists() == false)
		{
			if (folder.mkdirs() == false)
			{
				LOGGER.error("Could not create directory '" + pFolderName + "'");
				throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
			}
		}
		File file = new File(pFileName);
		if (file.exists() == false && file.createNewFile() == false)
		{
			LOGGER.error("Could not create file '" + pFileName + "'");
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		FileWriter writer = new FileWriter(file);
		writer.write(pXml);
		writer.close();
	}

	/**
	 * @return
	 */
	protected String getDatabaseLocation()
	{
		URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		String path = url.getPath().substring(0, url.getPath().indexOf("WEB-INF") - 1);

		path += File.separator + "data" + File.separator;

		if (mDatabaseType == DatabaseType.TEMPLATE)
		{
			path += "template" + File.separator;
		}
		else if (mDatabaseType == DatabaseType.AGREEMENT)
		{
			path += "agreement" + File.separator;
		}
		else if (mDatabaseType == DatabaseType.STATE)
		{
			path += "state" + File.separator;
		}
		else if (mDatabaseType == DatabaseType.MEASUREMENT)
		{
			path += "measurement" + File.separator;
		}
		else
		{
			throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
		}

		return path;
	}
}
