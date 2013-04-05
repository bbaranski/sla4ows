package org.ifgi.sla.reporter.resources;

import java.net.URI;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ifgi.namespaces.wsag.ogc.ContactDocument;
import org.ifgi.sla.reporter.Configuration;
import org.ifgi.sla.reporter.template.MailSubject;
import org.ifgi.sla.reporter.template.MailTemplate;
import org.ifgi.sla.wsag.helper.ContactDetailsHelper;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
@Path("/reports")
public class ReportsResource
{
	protected static Logger LOGGER = Logger.getLogger(ReportsResource.class);

	@Context
	protected UriInfo uriInfo;

	@POST
	@Consumes({ "application/xml" })
	public Response createAgreementState(AgreementPropertiesDocument pAgreementProperties)
	{
		String uuid = UUID.randomUUID().toString();

		ContactDocument contact = null;
		try
		{
			contact = ContactDetailsHelper.instance().getServiceProvider(pAgreementProperties);
		}
		catch (Exception e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}

		// TODO STORE NOTIFICATION IN DATABASE
		try
		{
			if (Configuration.instance().getMail().getEnable())
			{
				LOGGER.info("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] send notification to '" + contact.getContact().getContact().getContactInfo().getAddress().getElectronicMailAddressArray(0) + "'");
				
				String subject = MailSubject.getDefault(pAgreementProperties);
				
				MailTemplate mailTemplate = new MailTemplate();
				String message = mailTemplate.getDefault(pAgreementProperties);
				
				postMail(contact.getContact().getContact().getContactInfo().getAddress().getElectronicMailAddressArray(0), subject, message);
			}
		}
		catch (Exception e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}

		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
		URI uri = uriBuilder.path(ReportResource.class).path(uuid).build();
		return Response.created(uri).build();
	}

	private static void postMail(String recipient, String subject, String message) throws MessagingException
	{
		Properties props = new Properties();

		props.setProperty("mail.transport.protocol", Configuration.instance().getMail().getReporterMailTransportProtocol());
		props.setProperty("mail.smtp.port", Configuration.instance().getMail().getReporterMailSmtpPort());
		props.setProperty("mail.smtp.starttls.enable", Configuration.instance().getMail().getReporterMailSmtpStarttlsEnable());
		props.setProperty("mail.smtp.auth", Configuration.instance().getMail().getReporterMailSmtpAuth());
		props.setProperty("mail.host", Configuration.instance().getMail().getReporterMailHost());
		props.setProperty("mail.user", Configuration.instance().getMail().getReporterMailUser());
		props.setProperty("mail.password", Configuration.instance().getMail().getReporterMailPassword());

		Authenticator auth = new SMTPAuthenticator();

		Session session = Session.getDefaultInstance(props, auth);

		Message msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress(Configuration.instance().getMail().getReporterMailFrom());
		msg.setFrom(addressFrom);
		InternetAddress addressTo = new InternetAddress(recipient);
		msg.setRecipient(Message.RecipientType.TO, addressTo);
		msg.setSubject(subject);
		msg.setContent(message, "text/plain");

		Transport.send(msg);
	}

	private static class SMTPAuthenticator extends javax.mail.Authenticator
	{
		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(Configuration.instance().getMail().getReporterMailUser(), Configuration.instance().getMail().getReporterMailPassword());
		}
	}
}