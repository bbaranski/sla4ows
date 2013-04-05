package org.ifgi.sla.proxy.resources;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ifgi.namespaces.sla.proxy.ServiceType;
import org.ifgi.sla.infrastructure.client.InfrastructureClient;
import org.ifgi.sla.manager.client.ManagerClient;
import org.ifgi.sla.proxy.Configuration;
import org.ifgi.sla.proxy.helper.HttpClientHelper;
import org.ifgi.sla.wsag.helper.AgreementHelper;
import org.ifgi.sla.wsag.helper.TimeConstraintHelper;
import org.wsag4J.schemas.x2009.x07.wsag4JSchedulingExtensions.TimeConstraintDocument;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
@Path("/{service}")
public class ServiceResource
{
	protected static Logger LOGGER = Logger.getLogger(ServiceResource.class);

	@Context
	protected UriInfo uriInfo;

	/**
	 * @param pService
	 * @param pAgreement
	 * @return
	 */
	@GET
	@Path("/{agreement}")
	@Produces({ "application/xml" })
	public StreamingOutput executeGet(@PathParam("service") String pService, @PathParam("agreement") String pAgreement)
	{

		/* GET SERVICE CONFIGURATION */
		ServiceType service = Configuration.instance().getServiceConfiguration(pService);

		/* GET AGREEMENT FROM SLA MANAGER */
		AgreementDocument agreement = ManagerClient.instance().getAgreement(Configuration.instance().getConfiguration().getConfiguration().getManager().getURI(), pAgreement);

		/* CHECK IF THE AGREEMENT COVERS THE CALLED SERVICE */
		// String agreementServiceReferenceUri = ServiceReferenceHelper.instance().get(agreement.getAgreement()).getServiceReference().getURI();
		// String requestUri = uriInfo.getRequestUri().toString();
		//
		// if (!agreementServiceReferenceUri.equalsIgnoreCase(requestUri))
		// {
		// LOGGER.error("Agreement with id " + agreement.getAgreement().getAgreementId() + " does not cover the service '" + requestUri + "'.");
		// throw new WebApplicationException(Status.NOT_FOUND.getStatusCode());
		// }

		/* CHECK CONTRACT RUNTIME */
		checkContractRuntime(agreement);

		/* GET INFRASTRUCTURE CONFIGURATION */
		String infrastructureAuthority = InfrastructureClient.instance().getAuthority(Configuration.instance().getConfiguration().getConfiguration().getInfrastructure().getURI(), pAgreement);

		if (infrastructureAuthority.equalsIgnoreCase("134.76.21.119"))
		{
			infrastructureAuthority = infrastructureAuthority + ":8081";
		}
		
		/* COMPOSE URI OF ORIGINAL SERVICE */
		String serviceUri = infrastructureAuthority + service.getPath();
		String serviceQuery = uriInfo.getRequestUri().getRawQuery();

		/* FORWARD ORIGINAL REQUEST */
		final HttpResponse response = HttpClientHelper.instance().doGet(serviceUri, serviceQuery);

		/* ANALYZE RESPONSE */
		if (response.getStatusLine().getStatusCode() != Status.OK.getStatusCode())
		{
			LOGGER.error("HTPP status code of service '" + serviceUri + "' and query '" + serviceQuery + "' is not 'OK'");
			throw new WebApplicationException(response.getStatusLine().getStatusCode());
		}

		/* MANIPULATE RESPONSE */
		if (response.getEntity().getContentType().getValue().equalsIgnoreCase("application/xml") || response.getEntity().getContentType().getValue().equalsIgnoreCase("text/xml"))
		{
			try
			{
				String xml = getContent(response);
				xml = xml.replaceAll(serviceUri, uriInfo.getRequestUri().getScheme() + "://" + uriInfo.getRequestUri().getAuthority() + uriInfo.getRequestUri().getPath());
				return createOutput(xml);
			}
			catch (Exception e)
			{
				LOGGER.error(e);
				throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
			}

		}

		/* FORWARD ORIGINAL RESPONSE */
		StreamingOutput output = createOutput(response);

		return output;
	}
	
	@GET
	@Path("/{agreement}/state/{urn}/day")
	@Produces({ "text/plain" })
	public String passiveMonitoringDay(@PathParam("service") String pService, @PathParam("agreement") String pAgreement, @PathParam("urn") String pUrn)
	{
		return "1234";
	}
	
	@GET
	@Path("/{agreement}/state/{urn}/week")
	@Produces({ "text/plain" })
	public String passiveMonitoringWeek(@PathParam("service") String pService, @PathParam("agreement") String pAgreement, @PathParam("urn") String pUrn)
	{
		return "1234";
	}
	
	@GET
	@Path("/{agreement}/state/{urn}/month")
	@Produces({ "text/plain" })
	public String passiveMonitoringMonth(@PathParam("service") String pService, @PathParam("agreement") String pAgreement, @PathParam("urn") String pUrn)
	{
		return "1234";
	}
	
	@GET
	@Path("/{agreement}/state/{urn}/year")
	@Produces({ "text/plain" })
	public String passiveMonitoringYear(@PathParam("service") String pService, @PathParam("agreement") String pAgreement, @PathParam("urn") String pUrn)
	{
		return "1234";
	}
	

	/**
	 * @param pResponse
	 * @return
	 * @throws IOException
	 */
	private String getContent(final HttpResponse pResponse) throws IOException
	{
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		Reader reader = new BufferedReader(new InputStreamReader(pResponse.getEntity().getContent()));
		int n;
		while ((n = reader.read(buffer)) != -1)
		{
			writer.write(buffer, 0, n);
		}

		String xml = writer.toString();
		return xml;
	}

	/**
	 * @param agreement
	 */
	protected void checkContractRuntime(AgreementDocument pAgreement)
	{
		if (AgreementHelper.instance().isContractRuntime(pAgreement) == false)
		{
			TimeConstraintDocument timeConstraint = TimeConstraintHelper.instance().get(pAgreement.getAgreement());
			LOGGER.error("Agreement with id " + pAgreement.getAgreement().getAgreementId() + " is not in negotiated contract runtime (from " + timeConstraint.getTimeConstraint().getStartTime() + " to "
					+ timeConstraint.getTimeConstraint().getEndTime() + ")");
			throw new WebApplicationException(Status.NOT_FOUND.getStatusCode());
		}
	}

	/**
	 * @param response
	 * @return
	 */
	protected StreamingOutput createOutput(final HttpResponse pResponse)
	{
		return new StreamingOutput()
		{
			@Override
			public void write(OutputStream pOutputStream) throws IOException, WebApplicationException
			{
				if (pResponse.getEntity() != null)
				{
					InputStream is = pResponse.getEntity().getContent();
					byte[] buffer = new byte[1024];
					int length = is.read(buffer);
					while (length > 0)
					{
						pOutputStream.write(buffer, 0, length);
						length = is.read(buffer);
					}
				}

			}
		};
	}

	/**
	 * @param response
	 * @return
	 */
	protected StreamingOutput createOutput(final String pResponse)
	{
		return new StreamingOutput()
		{
			@Override
			public void write(OutputStream pOutputStream) throws IOException, WebApplicationException
			{
				InputStream is = new ByteArrayInputStream(pResponse.getBytes());
				byte[] buffer = new byte[1024];
				int length = is.read(buffer);
				while (length > 0)
				{
					pOutputStream.write(buffer, 0, length);
					length = is.read(buffer);
				}

			}
		};
	}
}