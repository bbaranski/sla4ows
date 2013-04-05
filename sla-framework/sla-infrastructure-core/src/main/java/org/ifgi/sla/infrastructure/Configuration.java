package org.ifgi.sla.infrastructure;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.codehaus.groovy.control.CompilationFailedException;
import org.ifgi.namespaces.sla.infrastructure.ConfigurationDocument;
import org.ifgi.namespaces.sla.infrastructure.PrivateInfrastructureType;
import org.ifgi.namespaces.sla.infrastructure.Sla4DgridInfrastructureType;
import org.ifgi.sla.infrastructure.local.PrivateInfrastructureConnector;

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
	 * @param pInfrastructureProviderName
	 * @return
	 */
	public AbstractInfrastructureConnector getInfrastructureConnector(String pAgreementId, String pInfrastructureProviderName)
	{
		// PRIVATE INFRASTRUCTURE
		for (PrivateInfrastructureType infrastructure : getConfiguration().getConfiguration().getPrivateInfrastructureArray())
		{
			if (infrastructure.getName().equalsIgnoreCase(pInfrastructureProviderName))
			{
				return new PrivateInfrastructureConnector(pAgreementId, infrastructure);
			}
		}

		// SLA4D-GRID INFRASTRUCTURE
		for (Sla4DgridInfrastructureType infrastructure : getConfiguration().getConfiguration().getSla4DgridInfrastructureArray())
		{
			if (infrastructure.getName().equalsIgnoreCase(pInfrastructureProviderName))
			{
				AbstractInfrastructureConnector infraConnector = null;

				try
				{
					GroovyClassLoader gcl = new GroovyClassLoader();
					Class clazz;
					clazz = gcl.parseClass(new File(getConfigurationLocation() + "InfrastructureConnector.groovy"));
					// clazz = gcl.parseClass(new File(getClass().getResource("InfrastructureConnector.groovy").getPath()));
					Object aScript = clazz.newInstance();
					infraConnector = (AbstractInfrastructureConnector) aScript;
					Map params = new LinkedHashMap();
					params.put("serverLocation", infrastructure.getURI());
					infraConnector.setProperties(params);
				}
				catch (CompilationFailedException e)
				{
					LOGGER.error(e);
					throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
				}
				catch (IOException e)
				{
					LOGGER.error(e);
					throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
				}
				catch (InstantiationException e)
				{
					LOGGER.error(e);
					throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
				}
				catch (IllegalAccessException e)
				{
					LOGGER.error(e);
					throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
				}

				return infraConnector;
			}
		}

		LOGGER.error("No infrastructure configuration found for provider '" + pInfrastructureProviderName + "'.");
		throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
	}

}
