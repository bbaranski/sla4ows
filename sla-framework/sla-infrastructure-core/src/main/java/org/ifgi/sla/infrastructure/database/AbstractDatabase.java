package org.ifgi.sla.infrastructure.database;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.ClientResponse.Status;

public abstract class AbstractDatabase<T extends Object>
{
	protected static Logger LOGGER = Logger.getLogger(AbstractDatabase.class);

//	public static String AGREEMENT_ID = "agreementId";
//	public static String RESERVATION_ID = "reservationId";
//	public static String RESERVATION_AUTHORITY = "reservationAuthority";
	
	public static String VIRTUAL_MACHINE_ID = "virtualMachineId";
	public static String INFRASTRUCTURE_PROVIDER_NAME = "infrastructureProviderName";

	public enum DatabaseType
	{
		DEFAULT, AMAZON
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
	public abstract T add(String pId, T pObject);

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
	 * @return
	 */
	protected String getDatabaseLocation()
	{
		URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		String path = url.getPath().substring(0, url.getPath().indexOf("WEB-INF") - 1);

		path += File.separator + "data" + File.separator;

		if (mDatabaseType == DatabaseType.AMAZON)
		{
			path += "amazon" + File.separator;
		}
		else if (mDatabaseType == DatabaseType.DEFAULT)
		{
			path += "default" + File.separator;
		}
		else
		{
			throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
		}

		File folder = new File(path);
		if (!folder.exists())
			folder.mkdirs();

		return path;
	}
}
