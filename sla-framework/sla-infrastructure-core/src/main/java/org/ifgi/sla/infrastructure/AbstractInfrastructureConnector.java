package org.ifgi.sla.infrastructure;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractInfrastructureConnector
{
	/**
	 * This method creates a new VM without starting it.
	 *
	 * @param pImageName The name of the VM image to use
	 * @return the reservationId of the VM
	 */
	public abstract String createVm(String pImageName);
	/**
	 * This functions return a list of available Virtual Machine (VM) image names.
	 * 
	 * @return a list of image names
	 */
	public abstract List<String> getImageNames();

	/**
	 * This function returns the configuration parameters and default values (key-value pair) for launching a VM image.  
	 * 
	 * @param pImageName The name of the VM image.
	 * @return The configuration parameters and default values for launching a VM image. 
	 */
	public abstract Map<String, String> getParameters(String pImageName);

	/**
	 * This function immediately starts the VM image with the specified configuration parameters. 
	 * 
	 * @param pImageId The name of the VM image.
	 * @param pParameter The configuration parameters and values for launching a VM image.
	 * @return An unique reservation id for identifying a launched VM instance.
	 */
	public abstract void start(String pImageId, Map<String, String> pParameter);

	/**
	 * This function immediately stops a running VM instance.
	 *
	 * @param pReservationId An unique reservation id for identifying a running VM instance.
	 */
	public abstract void stop(String pReservationId);

	/**
	 * This function starts/ends the VM image with the specified configuration parameters at specific time points.
	 * 
	 * @param pImageName The name of the VM image.
	 * @param pParameter The configuration parameters and values for launching a VM image.
	 * @param pStart The start date for launching the VM instance.
	 * @param pEnd The end date for stopping a launched VM instance.
	 * @return An unique reservation id for identifying a scheduled/launched VM instance.
	 */
	public abstract String schedule(String pImageName, Map<String, String> pParameter, Date pStart, Date pEnd);

	/**
	 * This function returns the status of a scheduled/launched VM instance ('true' = running, 'false' = not running).
	 * 
	 * @param pReservationId An unique reservation id for identifying an already launched VM instance.
	 * @return Returns 'true' if a VM instance is running, and 'false' if not.
	 */
	public abstract boolean getState(String pReservationId);

	/**
	 * This function returns the authority part (IP) of a running instance (e.g. '192.0.2.16:80'). 
	 * 
	 * @param pReservationId An unique reservation id for identifying an already launched VM instance.
	 * @return The authority part of a running instance.
	 */
	public abstract String getAuthority(String pReservationId);
	
	/**
	 * This (optional) function returns infrastructure properties that are required for remote managing the infrastructure.
	 *  
	 * @return Properties for managing the infrastructure.
	 */
	public Properties getProperties()
	{
		Properties properties = new Properties();
		return properties;
	}
	
	/**
	 * Sets properties of the implementation.
	 *
	 * @param params A map containing properties for the implementation.
	 */
	public abstract void setProperties(Map params);

}
