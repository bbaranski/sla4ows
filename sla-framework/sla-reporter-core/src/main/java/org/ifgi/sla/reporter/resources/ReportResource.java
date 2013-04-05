package org.ifgi.sla.reporter.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
@Path("/report")
public class ReportResource
{
	protected static Logger LOGGER = Logger.getLogger(ReportResource.class);

	@Context
	protected UriInfo uriInfo;

	@GET
	@Path("/{id}")
	@Produces({ "application/xml" })
	public AgreementPropertiesDocument createAgreementState(@PathParam("id") String pId)
	{
		// TODO READ NOTIFICATION FROM DATABASE
		
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}
}