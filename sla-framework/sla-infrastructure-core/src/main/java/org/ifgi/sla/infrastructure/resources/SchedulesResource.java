package org.ifgi.sla.infrastructure.resources;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ifgi.sla.infrastructure.AbstractInfrastructureConnector;
import org.ifgi.sla.infrastructure.Configuration;
import org.ifgi.sla.infrastructure.database.AbstractDatabase;
import org.ifgi.sla.infrastructure.database.DefaultDatabase;
import org.ifgi.sla.wsag.helper.ServicePropertiesHelper;
import org.ifgi.sla.wsag.urn.ServicePropertiesURN;

/**
 * @author bastian
 * 
 */
@Path("/schedules")
public class SchedulesResource
{
	protected static Logger LOGGER = Logger.getLogger(SchedulesResource.class);

	@Context
	protected UriInfo uriInfo;

	@POST
	@Consumes({ "application/xml" })
	public Response createSchedule(AgreementDocument pAgreement)
	{
		/* create infrastructure connector for provider */
		String infrastructureProviderName = ServicePropertiesHelper.instance().getPropertieValue(pAgreement.getAgreement(), ServicePropertiesURN.INFRASTRUCTURE_PROVIDER_NAME);
		
		AbstractInfrastructureConnector infrastructure = Configuration.instance().getInfrastructureConnector(pAgreement.getAgreement().getAgreementId(), infrastructureProviderName);

		/* create schedule information */
		String infrastructureVirtualMachineName = ServicePropertiesHelper.instance().getPropertieValue(pAgreement.getAgreement(), ServicePropertiesURN.INFRASTRUCTURE_VM_NAME);
		
		/* create VM */
		String virtualMachineId = infrastructure.createVm(infrastructureVirtualMachineName);
		
		/* start VM */
		Map parameters = new HashMap<String, String>();
		infrastructure.start(virtualMachineId, parameters);

		/* create infrastructure properties */
		Properties properties = infrastructure.getProperties();		
		
		properties.put(AbstractDatabase.VIRTUAL_MACHINE_ID, virtualMachineId);
		properties.put(AbstractDatabase.INFRASTRUCTURE_PROVIDER_NAME, infrastructureProviderName);
		
		/* store infrastructure properties */
		DefaultDatabase database = new DefaultDatabase();
		properties = database.add(pAgreement.getAgreement().getAgreementId(), properties);

		/* create resource */
		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
		URI uri = uriBuilder.path(ScheduleResource.class).path(pAgreement.getAgreement().getAgreementId()).build();
		return Response.created(uri).build();

	}
}