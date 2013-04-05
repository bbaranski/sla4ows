package org.ifgi.sla.infrastructure.resources;

import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.ifgi.sla.infrastructure.AbstractInfrastructureConnector;
import org.ifgi.sla.infrastructure.Configuration;
import org.ifgi.sla.infrastructure.database.AbstractDatabase;
import org.ifgi.sla.infrastructure.database.DefaultDatabase;

/**
 * @author bastian
 * 
 */
@Path("/schedule")
public class ScheduleResource
{
	protected static Logger LOGGER = Logger.getLogger(ScheduleResource.class);

	@Context
	protected UriInfo uriInfo;

	@GET
	@Path("/{id}")
	@Produces({ "text/plain" })
	public String getSchedule(@PathParam("id") String pId)
	{
		DefaultDatabase database = new DefaultDatabase();
		Properties properties = database.get(pId);

		String virtualMachineId = properties.getProperty(AbstractDatabase.VIRTUAL_MACHINE_ID);
		String infrastructureProviderName = properties.getProperty(AbstractDatabase.INFRASTRUCTURE_PROVIDER_NAME);

		AbstractInfrastructureConnector infrastructure = Configuration.instance().getInfrastructureConnector(pId, infrastructureProviderName);

		return infrastructure.getAuthority(virtualMachineId);

	}

}