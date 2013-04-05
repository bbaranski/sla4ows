package org.ifgi.sla.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.apache.xmlbeans.XmlOptions;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementOfferDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermStateType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.TemplateDocument;
import org.ifgi.namespaces.wsag.ogc.ActiveMonitoringRequestType;
import org.ifgi.namespaces.wsag.ogc.ActiveMonitoringType;
import org.ifgi.namespaces.wsag.ogc.ContactDocument;
import org.ifgi.namespaces.wsag.ogc.CustomBusinessValueDocument;
import org.ifgi.namespaces.wsag.ogc.CustomServiceLevelDocument;
import org.ifgi.namespaces.wsag.ogc.PropertyType;
import org.ifgi.namespaces.wsag.ogc.ServiceDescriptionDocument;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.namespaces.wsag.ogc.ServiceReferenceDocument;
import org.ifgi.sla.manager.client.ManagerClient;
import org.ifgi.sla.wsag.helper.BusinessValueHelper;
import org.ifgi.sla.wsag.helper.ContactDetailsHelper;
import org.ifgi.sla.wsag.helper.ServiceDescriptionHelper;
import org.ifgi.sla.wsag.helper.ServiceLevelHelper;
import org.ifgi.sla.wsag.helper.ServicePropertiesHelper;
import org.ifgi.sla.wsag.helper.ServiceReferenceHelper;
import org.ifgi.sla.wsag.helper.TemplateHelper;
import org.ifgi.sla.wsag.helper.TimeConstraintHelper;
import org.wsag4J.schemas.x2009.x07.wsag4JSchedulingExtensions.TimeConstraintDocument;

public class AgreementNegotiationTest extends TestCase
{
	public static void testWorkflow() throws Exception
	{
		String managerUri = "http://localhost:8088/sla-manager";

		// ############################################################
		// SHOW ID OF ALL AVAILABLE TEMPLATES
		// ############################################################

		TemplateDocument template = null;

		List<TemplateDocument> templateList = ManagerClient.instance().getTemplateList(managerUri);
		for (TemplateDocument tmpTemplate : templateList)
		{
			System.out.println("TEMPLATE ID : " + tmpTemplate.getTemplate().getTemplateId());
			// if (tmpTemplate.getTemplate().getTemplateId().equalsIgnoreCase("WSAG_DEFAULT_TEMPLATE_8_SLA4DGRID"))
			if (tmpTemplate.getTemplate().getTemplateId().equalsIgnoreCase("WSAG_DEFAULT_TEMPLATE"))
			{
				template = tmpTemplate;
			}
		}
		System.out.println("");

		// ############################################################
		// SHOW XML OF SELECTED TEMPLATE
		// ############################################################

		// System.out.println(template.xmlText());
		// System.out.println("");

		// ############################################################
		// SHOW CONTACT DETAILS OF SERVICE PROVIDER
		// ############################################################

		ContactDocument serviceProvider = ContactDetailsHelper.instance().getServiceProvider(template);

		// System.out.println(ContactDetailsHelper.instance().getXML(serviceProvider));
		// System.out.println("");

		System.out.println("Name: " + serviceProvider.getContact().getName());
		System.out.println("Site: " + serviceProvider.getContact().getSite().getHref());
		System.out.println("");

		System.out.println("IndividualName: " + serviceProvider.getContact().getContact().getIndividualName());
		System.out.println("PositionName: " + serviceProvider.getContact().getContact().getPositionName());
		System.out.println("Voice: " + serviceProvider.getContact().getContact().getContactInfo().getPhone().getVoiceArray(0));
		System.out.println("Facsimile: " + serviceProvider.getContact().getContact().getContactInfo().getPhone().getFacsimileArray(0));
		System.out.println("");

		System.out.println("DeliveryPoint: " + serviceProvider.getContact().getContact().getContactInfo().getAddress().getDeliveryPointArray(0));
		System.out.println("City: " + serviceProvider.getContact().getContact().getContactInfo().getAddress().getCity());
		System.out.println("PostalCode: " + serviceProvider.getContact().getContact().getContactInfo().getAddress().getPostalCode());
		System.out.println("Country: " + serviceProvider.getContact().getContact().getContactInfo().getAddress().getCountry());
		System.out.println("ElectronicMailAddress: " + serviceProvider.getContact().getContact().getContactInfo().getAddress().getElectronicMailAddressArray(0));
		System.out.println("");

		System.out.println("HoursOfService: " + serviceProvider.getContact().getContact().getContactInfo().getHoursOfService());
		System.out.println("ContactInstructions: " + serviceProvider.getContact().getContact().getContactInfo().getContactInstructions());
		System.out.println("");

		// ############################################################
		// SHOW SERVICE DESCRIPTION OF TEMPLATE
		// ############################################################

		ServiceDescriptionDocument serviceDescription = ServiceDescriptionHelper.instance().get(template.getTemplate());

		// System.out.println(ServiceDescriptionHelper.instance().getXML(serviceDescription));
		// System.out.println("");

		System.out.println("ServiceDescription - Title: " + serviceDescription.getServiceDescription().getTitle());
		System.out.println("ServiceDescription - Abstract: " + serviceDescription.getServiceDescription().getAbstract());
		System.out.println("ServiceDescription - Type: " + serviceDescription.getServiceDescription().getType());
		System.out.println("ServiceDescription - Keywords: " + serviceDescription.getServiceDescription().getKeywords());
		System.out.println("");

		// ############################################################
		// SHOW SERVICE PROPERTIES OF TEMPLATE
		// ############################################################

		ServicePropertiesDocument serviceProperties = ServicePropertiesHelper.instance().get(template.getTemplate());

		// System.out.println(ServicePropertiesHelper.instance().getXML(serviceProperties));
		// System.out.println("");

		for (PropertyType property : serviceProperties.getServiceProperties().getPropertyArray())
		{

			System.out.println("Property - Name: " + property.getName());
			System.out.println("Property - Title: " + property.getTitle());
			System.out.println("Property - Abstract: " + property.getAbstract());
			System.out.println("Property - Type: " + property.getType());
			System.out.println("Property - Value: " + property.getValue());

			if (property.getMonitoring() != null && property.getMonitoring().getActiveMonitoring() != null)
			{
				ActiveMonitoringType activeMonitoring = property.getMonitoring().getActiveMonitoring();

				System.out.println("Property - Active Monitoring - Start: " + activeMonitoring.getStart().toString());
				System.out.println("Property - Active Monitoring - Stop: " + activeMonitoring.getStop().toString());
				System.out.println("Property - Active Monitoring - Period: " + activeMonitoring.getPeriod());

				if (activeMonitoring.getSession() != null)
				{
					System.out.println("Property - Active Monitoring Session - Chance: " + activeMonitoring.getSession().getCapacity());
					System.out.println("Property - Active Monitoring Session - Duration: " + activeMonitoring.getSession().getDuration());
					System.out.println("Property - Active Monitoring Session - Period: " + activeMonitoring.getSession().getPeriod());
				}

				if (activeMonitoring.getRequestArray() != null && activeMonitoring.getRequestArray().length > 0)
				{
					for (ActiveMonitoringRequestType request : activeMonitoring.getRequestArray())
					{
						System.out.println("Property - Active Monitoring Request - Chance: " + request.getChance());
						System.out.println("Property - Active Monitoring Request - Resource: " + request.getResource());
						System.out.println("Property - Active Monitoring Request - Method: " + request.getMethod());
						System.out.println("Property - Active Monitoring Request - Content: " + request.getContent());
					}
				}

				if (activeMonitoring.getResponse() != null)
				{
					System.out.println("Property - Active Monitoring Response - Period: " + activeMonitoring.getResponse().getStatus());
					System.out.println("Property - Active Monitoring Response - Period: " + activeMonitoring.getResponse().getContent());
				}

			}
			System.out.println("");
		}

		// ############################################################
		// SHOW CONTRACT RUNTIME OF TEMPLATE
		// ############################################################

		TimeConstraintDocument contractRuntime = TimeConstraintHelper.instance().get(template.getTemplate());

		// System.out.println(TimeConstraintHelper.instance().getXML(contractRuntime));
		// System.out.println("");

		System.out.println("TimeConstraint - Start Time: " + contractRuntime.getTimeConstraint().getStartTime().toString());
		System.out.println("TimeConstraint - End Time: " + contractRuntime.getTimeConstraint().getEndTime().toString());
		System.out.println("");

		// ############################################################
		// SHOW SERVICE REFERENCE OF TEMPLATE
		// ############################################################

		ServiceReferenceDocument serviceReference = ServiceReferenceHelper.instance().get(template.getTemplate());

		// System.out.println(ServiceReferenceHelper.instance().getXML(serviceReference));
		// System.out.println("");

		System.out.println("ServiceReference - URI: " + serviceReference.getServiceReference().getURL());
		System.out.println("");

		// ############################################################
		// SHOW BUSINES VALUES OF TEMPLATE
		// ############################################################

		for (GuaranteeTermType guaranteeTerm : template.getTemplate().getTerms().getAll().getGuaranteeTermArray())
		{
			CustomBusinessValueDocument customBusinessValue = BusinessValueHelper.instance().getBusinessValue(guaranteeTerm);

			if (customBusinessValue != null)
			{
				// System.out.println(BusinessValueHelper.instance().getXML(customBusinessValue));
				// System.out.println("");

				System.out.println("Business Value - Name: " + customBusinessValue.getCustomBusinessValue().getTitle());
				System.out.println("Business Value - Abstract: " + customBusinessValue.getCustomBusinessValue().getAbstract());
				System.out.println("Business Value - Type: " + customBusinessValue.getCustomBusinessValue().getType());
				System.out.println("Business Value - Value: " + customBusinessValue.getCustomBusinessValue().getValue());
				System.out.println("");
			}
		}

		// ############################################################
		// SEND AN AGREEMENT OFFER AND CREATE AN AGREEMENT
		// ############################################################

		Calendar startTime = new GregorianCalendar(2011, 4, 27, 11, 00);
		Calendar endTime = new GregorianCalendar(2013, 4, 27, 11, 00);
		TimeConstraintDocument timeConstraints = TimeConstraintHelper.instance().get(startTime, endTime);

		ContactDocument serviceConsumer = ContactDetailsHelper.instance().get("Instititute for Geoinformatics", "http://www.ifgi.de", "Kristof Lange", "Student Assistance", "+49 251 833307", "+49 251 8339763", "http://www.ifgi.de",
				"Weseler Strasse 253", "Muenster", "48151", "Germany", "kristof.lange@uni-muenster.de");

		AgreementOfferDocument agreementOffer = TemplateHelper.instance().createAgreementOffer(template, timeConstraints, serviceConsumer);

		BufferedWriter out = new BufferedWriter(new FileWriter(new File("/Users/bastian/Workspace/phd/sla-framework/sla-site/src/site/resources/examples/agreement-offer.xml")));
		XmlOptions options = new XmlOptions();
		options.setSavePrettyPrint();
		options.setSavePrettyPrintIndent(2);
		out.write(agreementOffer.xmlText(options));
		out.close();

		// System.out.println(agreementOffer.xmlText());
		// System.out.println("");

		AgreementDocument agreement = ManagerClient.instance().createAgreement(managerUri, agreementOffer);

		out = new BufferedWriter(new FileWriter(new File("/Users/bastian/Workspace/phd/sla-framework/sla-site/src/site/resources/examples/agreement.xml")));
		out.write(agreementOffer.xmlText(options));
		out.close();
		
		// System.out.println(agreement.xmlText());
		// System.out.println("");

		// ############################################################
		// GET STATUS OF NEGOTIATED AGREEMENT
		// ############################################################

		AgreementPropertiesDocument agreementProperties = ManagerClient.instance().getAgreementState(managerUri, agreement.getAgreement().getAgreementId());
		
		System.out.println("Agreement State: " + agreementProperties.getAgreementProperties().getAgreementState().getState());
		System.out.println("");

		// ############################################################
		// GET SERVICE TERM STATE OF NEGOTIATED AGREEMENT
		// ############################################################

		for (ServiceTermStateType serviceTermState : agreementProperties.getAgreementProperties().getServiceTermStateArray())
		{
			System.out.println("Service Term - Name: " + serviceTermState.getTermName());
			System.out.println("Service Term - State: " + serviceTermState.getState());
			System.out.println("");
		}

		// ############################################################
		// GET GUARANTEE TERM STATE
		// ############################################################

		for (GuaranteeTermStateType guaranteeTermState : agreementProperties.getAgreementProperties().getGuaranteeTermStateArray())
		{
			System.out.println("Guarantee Term - Name: " + guaranteeTermState.getTermName());

			CustomServiceLevelDocument customServiceLevel = ServiceLevelHelper.instance().get(agreementProperties.getAgreementProperties().getTerms().getAll().getGuaranteeTermArray(), guaranteeTermState.getTermName());

			if (customServiceLevel != null)
			{
				System.out.println("Guarantee Term - Title: " + customServiceLevel.getCustomServiceLevel().getTitle());
				System.out.println("Guarantee Term - Abstract: " + customServiceLevel.getCustomServiceLevel().getAbstract());
			}

			CustomBusinessValueDocument customBusinessValue = BusinessValueHelper.instance().get(agreementProperties.getAgreementProperties().getTerms().getAll().getGuaranteeTermArray(), guaranteeTermState.getTermName());

			if (customBusinessValue != null)
			{
				System.out.println("Guarantee Term - Title: " + customBusinessValue.getCustomBusinessValue().getTitle());
				System.out.println("Guarantee Term - Abstract: " + customBusinessValue.getCustomBusinessValue().getAbstract());
			}

			System.out.println("Guarantee Term - State: " + guaranteeTermState.getState());
			System.out.println("");
		}
	}
}
