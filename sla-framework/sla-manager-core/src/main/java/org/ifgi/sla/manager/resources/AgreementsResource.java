package org.ifgi.sla.manager.resources;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementOfferDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ifgi.namespaces.wsag.rest.AgreementListDocument;
import org.ifgi.sla.infrastructure.client.InfrastructureClient;
import org.ifgi.sla.manager.Configuration;
import org.ifgi.sla.manager.database.AgreementDatabase;
import org.ifgi.sla.manager.database.AgreementStateDatabase;
import org.ifgi.sla.wsag.helper.AgreementHelper;
import org.ifgi.sla.wsag.helper.AgreementOfferHelper;
import org.ifgi.sla.wsag.urn.ServicePropertiesURN;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
@Path("/agreements")
public class AgreementsResource
{
	@Context
	protected UriInfo uriInfo;

	/**
	 * The GET method returns a list of all agreements.
	 * 
	 * @return
	 */
	@GET
	@Produces({ "application/xml" })
	public AgreementListDocument showAgreements()
	{
		AgreementListDocument result = AgreementListDocument.Factory.newInstance();
		result.addNewAgreementList();
		AgreementDatabase db = new AgreementDatabase();
		for (AgreementDocument agreement : db.get())
		{
			UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
			String uri = uriBuilder.path(AgreementResource.class).path(agreement.getAgreement().getAgreementId()).build().toASCIIString();
			result.getAgreementList().addAgreementURI(uri);
		}
		return result;
	}

	/**
	 * The POST method allows the creation of a new agreement.
	 * 
	 * @param pAgreementOffer
	 * @return
	 */
	@POST
	@Consumes({ "application/xml" })
	public Response createAgreement(AgreementOfferDocument pAgreementOffer)
	{
		// VALIDATE TEMPLATE
		if (!ServicePropertiesURN.instance().validate(pAgreementOffer.getAgreementOffer()))
		{
			throw new WebApplicationException(Status.BAD_REQUEST.getStatusCode());
		}

		// CREATE AGREEMENT
		AgreementDocument agreement = AgreementOfferHelper.instance().createAgreement(pAgreementOffer);
		AgreementDatabase db = new AgreementDatabase();
		agreement = db.add(agreement);

		// CREATE AGREEMENT PROPERTIES
		AgreementPropertiesDocument agreementProperties = AgreementHelper.instance().createAgreementProperties(agreement);
		AgreementStateDatabase db2 = new AgreementStateDatabase();
		agreementProperties = db2.add(agreementProperties);

		// SCHEDULE INFRASTRUCTURE
		InfrastructureClient.instance().schedule(Configuration.instance().getInfrastructure(), agreement);

		// RETURN AGREEMENT RESOURCE
		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
		URI uri = uriBuilder.path(AgreementResource.class).path(agreement.getAgreement().getAgreementId()).build();
		return Response.created(uri).build();
	}
}