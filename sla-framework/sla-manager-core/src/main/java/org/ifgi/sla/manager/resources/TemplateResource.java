package org.ifgi.sla.manager.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.ggf.schemas.graap.x2007.x03.wsAgreement.TemplateDocument;
import org.ifgi.sla.manager.database.TemplateDatabase;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
@Path("/template")
public class TemplateResource
{

	/**
	 * With the GET method, this URI returns a representation of a template with a unique identifier specified in the URI.
	 * 
	 * @return
	 */
	@GET
	@Path("/{id}")
	@Produces({ "application/xml" })
	public TemplateDocument showTemplate(@PathParam("id") String pId)
	{
		TemplateDatabase db = new TemplateDatabase();
		TemplateDocument result = db.get(pId);
		if (result == null)
		{
			throw new WebApplicationException(Status.NOT_FOUND.getStatusCode());
		}
		else
		{
			return result;
		}
	}

	/**
	 * With the PUT method, it updates a template.
	 * 
	 * @return
	 */
	@PUT
	@Path("/{id}")
	@Consumes({ "application/xml" })
	@Produces({ "application/xml" })
	public TemplateDocument updateTemplate(@PathParam("id") String pId, String pXml)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/**
	 * With the PUT method, it updates a template.
	 * 
	 * @return
	 */
	@DELETE
	@Path("/{id}")
	public Response deleteTemplate(@PathParam("id") String pId)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

}