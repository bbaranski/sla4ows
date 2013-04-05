package org.ifgi.sla.infrastructure.local;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.WebApplicationException;

import org.ifgi.namespaces.sla.infrastructure.PrivateInfrastructureType;
import org.ifgi.sla.infrastructure.AbstractInfrastructureConnector;

import com.sun.jersey.api.client.ClientResponse.Status;

public class PrivateInfrastructureConnector extends AbstractInfrastructureConnector
{
	protected String agreementId;
	protected String authority;

	/**
	 * @param pAgreement
	 * @param pPrivateInfrastructureConfiguration
	 */
	public PrivateInfrastructureConnector(String pAgreementId, PrivateInfrastructureType pPrivateInfrastructureConfiguration)
	{
		agreementId = pAgreementId;
		authority = pPrivateInfrastructureConfiguration.getTarget();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.AbstractInfrastructureConnector#getImageNames()
	 */
	@Override
	public List<String> getImageNames()
	{
		return new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.AbstractInfrastructureConnector#getParameter(java.lang.String)
	 */
	@Override
	public Map<String, String> getParameters(String pTemplateId)
	{
		return new HashMap<String, String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.AbstractInfrastructureConnector#start(java.lang.String, java.util.Map)
	 */
	@Override
	public void start(String pImageId, Map<String, String> pParameter)
	{
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.AbstractInfrastructureConnector#stop(java.lang.String)
	 */
	@Override
	public void stop(String pReservationId)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.AbstractInfrastructureConnector#schedule(java.lang.String, java.util.Map, java.util.Date, java.util.Date)
	 */
	@Override
	public String schedule(String pImageId, Map<String, String> pParameter, Date pStart, Date pEnd)
	{
		return agreementId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.AbstractInfrastructureConnector#getStatus(java.lang.String)
	 */
	@Override
	public boolean getState(String pReservationId)
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.AbstractInfrastructureConnector#getAuthority(java.lang.String)
	 */
	@Override
	public String getAuthority(String pReservationId)
	{
		return authority;
	}

	/**
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.infrastructure.AbstractInfrastructureConnector#getProperties()
	 */
	public Properties getProperties()
	{
		Properties properties = new Properties();
		properties.put("authority", authority);
		return properties;
	}

	@Override
	public String createVm(String pImageName)
	{
		return agreementId;
	}

	@Override
	public void setProperties(Map params)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

}
