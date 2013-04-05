package org.ifgi.sla.evaluator;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.ifgi.namespaces.sla.evaluator.ConfigurationDocument;

public class Configuration
{
	protected static Logger LOGGER = Logger.getLogger(Configuration.class);

	protected static String CONFIG_FILE_NAME = "configuration.xml";

	static private Configuration _instance = null;

	protected ConfigurationDocument mConfiguration;

	/**
	 * 
	 */
	protected Configuration()
	{
		try
		{
			mConfiguration = ConfigurationDocument.Factory.parse(new File(getConfigurationLocation() + CONFIG_FILE_NAME));
		}
		catch (XmlException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		catch (IOException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}
	
	/**
	 * @return
	 */
	protected String getConfigurationLocation()
	{
		URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		String path = url.getPath().substring(0, url.getPath().indexOf("WEB-INF") - 1);
		path += File.separator + "config" + File.separator;
		return path;
	}

	/**
	 * @return
	 */
	static public Configuration instance()
	{
		if (null == _instance)
		{
			_instance = new Configuration();
		}
		return _instance;
	}

	/**
	 * @return
	 */
	public ConfigurationDocument getConfiguration()
	{
		return mConfiguration;
	}

	/**
	 * @return
	 */
	public int getInterval()
	{
		return mConfiguration.getConfiguration().getEvaluator().getInterval();
	}

	/**
	 * @return
	 */
	public String getManager()
	{
		return mConfiguration.getConfiguration().getManager().getURI();
	}
	
	/**
	 * @return
	 */
	public String getProxy()
	{
		return mConfiguration.getConfiguration().getProxy().getURI();
	}
	
	/**
	 * @return
	 */
	public String getReporter()
	{
		return mConfiguration.getConfiguration().getReporter().getURI();
	}	
}
