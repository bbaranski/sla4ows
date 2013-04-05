package org.ifgi.sla.manager.resources;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.sla.manager.database.MeasurementDatabase;

/**
 * @author bastian
 * 
 */
@Path("/measurements")
public class MeasurementsResource
{
	@Context
	protected UriInfo uriInfo;

	/**
	 * The POST method allows the creation of a new measurement for an agreement.
	 * 
	 * @param pAgreementOffer
	 * @return
	 */
	@POST
	@Path("/{id}")
	@Consumes({ "application/xml" })
	public Response createMeasurement(@PathParam("id") String pId, ServicePropertiesDocument pServiceProperties)
	{
		MeasurementDatabase db = new MeasurementDatabase();
		db.add(pId, pServiceProperties);
		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
		URI uri = uriBuilder.path(MeasurementResource.class).path(pId).build();
		return Response.created(uri).build();
	}
}