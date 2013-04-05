package org.ifgi.sla.manager.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ifgi.sla.manager.database.AgreementDatabase;
import org.ifgi.sla.manager.database.AgreementStateDatabase;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
@Path("/agreement")
public class AgreementResource
{
	@Context
	protected UriInfo uriInfo;

	/**
	 * The GET method invoked on this URI returns a specific agreement. Except for the GET method, only the service provider should be allowed to invoke the
	 * methods.
	 * 
	 * @param pId
	 * @return
	 */
	@GET
	@Path("/{id}")
	@Produces({ "application/xml" })
	public AgreementDocument showAgreement(@PathParam("id") String pId)
	{
		AgreementDatabase db = new AgreementDatabase();
		AgreementDocument result = db.get(pId);
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
	 * The GET method invoked on this URI returns the state of the agreement with the current id.
	 * 
	 * @param pId
	 * @return
	 */
	@GET
	@Path("/{id}/state")
	@Produces({ "application/xml" })
	public AgreementPropertiesDocument showAgreementState(@PathParam("id") String pId)
	{
		AgreementStateDatabase db = new AgreementStateDatabase();
		AgreementPropertiesDocument result = db.get(pId);
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
	 * The POST method allows the creation of a resource holding state for an agreement with the given id
	 * 
	 * @param pId
	 * @return
	 */
	@POST
	@Path("/{id}/state")
	@Produces({ "application/xml" })
	@Consumes({ "application/xml" })
	public AgreementPropertiesDocument createAgreementState(@PathParam("id") String pId, AgreementPropertiesDocument pAgreementProperties)
	{
		AgreementStateDatabase db = new AgreementStateDatabase();
		AgreementPropertiesDocument agreementProperties = db.add(pAgreementProperties);
		return agreementProperties;
	}

	/**
	 * The PUT method allows the update of a resource.
	 * 
	 * @param pId
	 * @return
	 */
	@PUT
	@Path("/{id}/state")
	@Produces({ "application/xml" })
	@Consumes({ "application/xml" })
	public AgreementPropertiesDocument updateAgreementState(@PathParam("id") String pId, AgreementPropertiesDocument pAgreementProperties)
	{
		AgreementStateDatabase db = new AgreementStateDatabase();
		AgreementPropertiesDocument agreementProperties = db.update(pId, pAgreementProperties);
		return agreementProperties;
	}

	/**
	 * The DELETE method removes a resource.
	 * 
	 * @param pId
	 * @return
	 */
	@DELETE
	@Path("/{id}/state")
	@Produces({ "application/xml" })
	public AgreementDocument deleteAgreementState(@PathParam("id") String pId)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}
}