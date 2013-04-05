package org.ifgi.sla.manager.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.ifgi.namespaces.wsag.rest.MeasurementHistoryListDocument;
import org.ifgi.sla.manager.database.MeasurementDatabase;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
@Path("/measurement")
public class MeasurementResource
{
	@Context
	protected UriInfo uriInfo;

	/**
	 * Text
	 * 
	 * @param pId
	 * @return
	 */
	@GET
	@Path("/{id}")
	@Produces({ "application/xml" })
	public MeasurementHistoryListDocument showMeasurements(@PathParam("id") String pId)
	{
		MeasurementDatabase db = new MeasurementDatabase();
		MeasurementHistoryListDocument result = db.get(pId);
		if (result == null)
		{
			throw new WebApplicationException(Status.NOT_FOUND.getStatusCode());
		}
		else
		{
			return result;
		}
	}
}