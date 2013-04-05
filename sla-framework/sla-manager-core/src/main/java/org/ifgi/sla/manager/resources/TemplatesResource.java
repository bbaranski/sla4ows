package org.ifgi.sla.manager.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.ggf.schemas.graap.x2007.x03.wsAgreement.TemplateDocument;
import org.ifgi.namespaces.wsag.rest.TemplateListDocument;
import org.ifgi.sla.manager.database.TemplateDatabase;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
@Path("/templates")
public class TemplatesResource
{
	@Context
	protected UriInfo uriInfo;

	/**
	 * With the GET method, this URI returns a list of all templates.
	 * 
	 * @return
	 */
	@GET
	@Produces({ "application/xml" })
	public TemplateListDocument showTemplates()
	{
		TemplateListDocument result = TemplateListDocument.Factory.newInstance();
		result.addNewTemplateList();
		TemplateDatabase db = new TemplateDatabase();
		for (TemplateDocument template : db.get())
		{
			UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
			String uri = uriBuilder.path(TemplateResource.class).path(template.getTemplate().getTemplateId()).build().toASCIIString();
			result.getTemplateList().addTemplateURI(uri);
		}
		return result;
	}

	/**
	 * The POST method allows the creation of a new template.
	 * 
	 * @return
	 */
	@POST
	@Consumes({ "application/xml" })
	public Response createTeamplet(TemplateDocument pTemplate)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/**
	 * With the GET method, this URI returns a list of templates that match the given query.
	 * 
	 * @param pQuery
	 * @return
	 */
	@GET
	@Path("/search/{query}")
	@Produces({ "application/xml" })
	public TemplateListDocument showTemplate(@PathParam("query") String pQuery)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}
}