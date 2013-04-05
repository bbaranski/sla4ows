package org.ifgi.sla.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementOfferDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermStateType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.GuaranteeTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.TemplateDocument;
import org.ifgi.namespaces.wsag.ogc.ActiveMonitoringType;
import org.ifgi.namespaces.wsag.ogc.ContactDocument;
import org.ifgi.namespaces.wsag.ogc.CustomBusinessValueDocument;
import org.ifgi.namespaces.wsag.ogc.CustomServiceLevelDocument;
import org.ifgi.namespaces.wsag.ogc.PropertyType;
import org.ifgi.namespaces.wsag.ogc.ServiceDescriptionDocument;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.namespaces.wsag.ogc.ServiceReferenceDocument;
import org.ifgi.namespaces.wsag.rest.MeasurementHistoryListDocument;
import org.ifgi.namespaces.wsag.rest.MeasurementHistoryListType;
import org.ifgi.namespaces.wsag.rest.MeasurementType;
import org.ifgi.sla.client.SlaService;
import org.ifgi.sla.manager.client.ManagerClient;
import org.ifgi.sla.wsag.helper.BusinessValueHelper;
import org.ifgi.sla.wsag.helper.ContactDetailsHelper;
import org.ifgi.sla.wsag.helper.ServiceDescriptionHelper;
import org.ifgi.sla.wsag.helper.ServiceLevelHelper;
import org.ifgi.sla.wsag.helper.ServicePropertiesHelper;
import org.ifgi.sla.wsag.helper.ServiceReferenceHelper;
import org.ifgi.sla.wsag.helper.TemplateHelper;
import org.ifgi.sla.wsag.helper.TimeConstraintHelper;
import org.ifgi.sla.shared.Agreement;
import org.ifgi.sla.shared.Measurement;
import org.ifgi.sla.shared.Pair;
import org.ifgi.sla.shared.Property;
import org.ifgi.sla.shared.Template;
import org.wsag4J.schemas.x2009.x07.wsag4JSchedulingExtensions.TimeConstraintDocument;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SlaImpl extends RemoteServiceServlet implements SlaService {
	private String managerUri = "http://localhost:8088/sla-manager";

	@Override
	public ArrayList<Agreement> getAgreementList()
			throws IllegalArgumentException {

		ArrayList<Agreement> tl = new ArrayList<Agreement>();
		List<AgreementDocument> agreementList = ManagerClient.instance()
				.getAgreementList(managerUri);
		for (AgreementDocument agreement : agreementList) {

			tl.add((Agreement) fillContent(agreement));
		}

		return tl;
	}

	@Override
	public Boolean addAgreement(String id, String pName, String pSite,
			String pIndividualName, String pPositionName, String pVoice,
			String pFacsimile, String pUrl, String pDeliveryPoint,
			String pCity, String pPostalCode, String pCountry,
			String pMailAddress, String start, String end)
			throws IllegalArgumentException {
		AgreementDocument agreement = null;
		boolean flag = true;
		try {
			TemplateDocument template = ManagerClient.instance().getTemplate(
					managerUri, id);
			if (!template.isNil()) {

				ContactDocument serviceConsumer = ContactDetailsHelper
						.instance().get(pName, pSite, pIndividualName,
								pPositionName, pVoice, pFacsimile, pUrl,
								pDeliveryPoint, pCity, pPostalCode, pCountry,
								pMailAddress);

				DateFormat df = new SimpleDateFormat("dd/MM/yyyy H:m");

				Date s = df.parse(start);
				Date e = df.parse(end);
				if (s.before(e)) {
					Calendar startTime = new GregorianCalendar();

					startTime.setTime(s);
					Calendar endTime = new GregorianCalendar();
					endTime.setTime(e);

					TimeConstraintDocument timeConstraints = TimeConstraintHelper
							.instance().get(startTime, endTime);

					AgreementOfferDocument agreementOffer = TemplateHelper
							.instance().createAgreementOffer(template,
									timeConstraints, serviceConsumer);

					agreement = ManagerClient.instance().createAgreement(
							managerUri, agreementOffer);

				} else
					flag = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public String removeAgreement() throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agreement getAgreement(String id) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Template> getTemplateList() {

		/*
		 * get all available Templates
		 */

		ArrayList<Template> tl = new ArrayList<Template>();
		List<TemplateDocument> templateList = ManagerClient.instance()
				.getTemplateList(managerUri);
		for (TemplateDocument template : templateList) {

			tl.add(fillContent(template));
		}

		return tl;
	}

	@Override
	public Template getTemplate(String id) throws IllegalArgumentException {
		TemplateDocument template = ManagerClient.instance().getTemplate(
				managerUri, id);
		if (!template.isNil()) {

			return fillContent(template);

		} else
			return null;
	}

	private Template fillContent(TemplateDocument document) {

		Template tem = new Template();
		tem.setTemplateID(new Pair("Template ID", document.getTemplate()
				.getTemplateId()));
		tem.setTemplateName(new Pair("Template Name", document.getTemplate()
				.getContext().getTemplateName()));

		// tem.setname(template.getTemplate().getName());
		//		

		try {
			ContactDocument serviceProvider;

			serviceProvider = ContactDetailsHelper.instance()
					.getServiceProvider(document);

			getServiceProviderInformation(tem, serviceProvider);

			ServiceDescriptionDocument serviceDescription;

			serviceDescription = ServiceDescriptionHelper.instance().get(
					document.getTemplate());

			TimeConstraintDocument contractRuntime;

			contractRuntime = TimeConstraintHelper.instance().get(
					document.getTemplate());

			getServiceDescriptionInformation(tem, serviceDescription,
					contractRuntime);

			ServicePropertiesDocument serviceProperties;

			serviceProperties = ServicePropertiesHelper.instance().get(
					document.getTemplate());

			getServicePropertiesInformation(tem, serviceProperties);

			GuaranteeTermType[] guaranteeTerms;

			guaranteeTerms = (document.getTemplate().getTerms().getAll()
					.getGuaranteeTermArray());

			for (GuaranteeTermType guaranteeTerm : guaranteeTerms) {
				
				CustomBusinessValueDocument businessValue = BusinessValueHelper.instance().getBusinessValue(guaranteeTerm);
				
				if (businessValue != null)
				{
					
//				if (guaranteeTerm.getBusinessValueList() != null
//						&& guaranteeTerm.getBusinessValueList()
//								.sizeOfCustomBusinessValueArray() == 1) {
//					
//					
//					CustomBusinessValueDocument businessValue = BusinessValueHelper
//							.instance().get(
//									guaranteeTerm.getBusinessValueList()
//											.getCustomBusinessValueArray()[0]);

					try {
						System.out.println(BusinessValueHelper.instance()
								.getXML(businessValue));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					getServiceBusinessValueInformation(tem, businessValue);
				}
			}

			// // SHOW SERVICE REFERENCE OF TEMPLATE

			ServiceReferenceDocument serviceReference;

			serviceReference = ServiceReferenceHelper.instance().get(
					document.getTemplate());

			getServiceReferenceInformation(tem, serviceReference);

			// if (tem instanceof Agreement)
			//				
			// getAgreementState((Agreement)tem, ((AgreementDocument) document),
			// ((AgreementDocument) document).getAgreement().getAgreementId());
			//
			//			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tem;
	}

	//
	private Agreement fillContent(AgreementDocument document) {
		Agreement agreement = new Agreement();

		agreement.setTemplateID(new Pair("Template ID", document.getAgreement()
				.getAgreementId()));
		agreement.setTemplateName(new Pair("Template Name", document
				.getAgreement().getContext().getTemplateName()));

		try {
			ContactDocument serviceProvider;

			serviceProvider = ContactDetailsHelper.instance()
					.getServiceProvider(document);

			getServiceProviderInformation(agreement, serviceProvider);

			ContactDocument serviceConsumer = ContactDetailsHelper.instance()
					.getServiceConsumer(document);

			getServiceConsumerInformation(agreement, serviceConsumer);

			ServiceDescriptionDocument serviceDescription;

			serviceDescription = ServiceDescriptionHelper.instance().get(
					document.getAgreement());
			TimeConstraintDocument contractRuntime;

			contractRuntime = TimeConstraintHelper.instance().get(
					document.getAgreement());

			getServiceDescriptionInformation(agreement, serviceDescription,
					contractRuntime);

			ServicePropertiesDocument serviceProperties;

			serviceProperties = ServicePropertiesHelper.instance().get(
					document.getAgreement());

			getServicePropertiesInformation(agreement, serviceProperties);

			GuaranteeTermType[] guaranteeTerms;

			guaranteeTerms = document.getAgreement().getTerms().getAll()
					.getGuaranteeTermArray();

			
			
			for (GuaranteeTermType guaranteeTerm : guaranteeTerms) {
				
				CustomBusinessValueDocument businessValue = BusinessValueHelper.instance().getBusinessValue(guaranteeTerm);
				
				if (businessValue != null)
				{
					
				//if (guaranteeTerm.getBusinessValueList() != null && guaranteeTerm.getBusinessValueList().getCustomBusinessValueArray() != null && guaranteeTerm.getBusinessValueList().getCustomBusinessValueArray().length == 1) {
					
					// BusinessValueHelper helper = BusinessValueHelper.instance();
					// CustomBusinessValueDocument businessValue = helper.get(guaranteeTerm.getBusinessValueList().getCustomBusinessValueArray()[0]);

						
					try {
						System.out.println(BusinessValueHelper.instance()
								.getXML(businessValue));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					getServiceBusinessValueInformation(agreement, businessValue);
				}
			}

			// // SHOW SERVICE REFERENCE OF TEMPLATE

			ServiceReferenceDocument serviceReference;

			serviceReference = ServiceReferenceHelper.instance().get(
					document.getAgreement());

			getServiceReferenceInformation(agreement, serviceReference);

			getAgreementState(agreement, document, document.getAgreement()
					.getAgreementId());

			getGuaranteeTerms(agreement, document, document.getAgreement()
					.getAgreementId());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return agreement;

	}

	private Template getServiceConsumerInformation(Template tem,
			ContactDocument serviceConsumer) {

		tem.getServiceConsumer().add(
				new Pair("Provider Name", serviceConsumer.getContact()
						.getName()));
		tem.getServiceConsumer().add(
				new Pair("Provider Site", serviceConsumer.getContact()
						.getSite().getHref()));
		tem.getServiceConsumer().add(
				new Pair("Individual Name", serviceConsumer.getContact()
						.getContact().getIndividualName()));
		tem.getServiceConsumer().add(
				new Pair("Position Name", serviceConsumer.getContact()
						.getContact().getPositionName()));
		tem.getServiceConsumer().add(
				new Pair("Voice", serviceConsumer.getContact().getContact()
						.getContactInfo().getPhone().getVoiceArray(0)));
		tem.getServiceConsumer().add(
				new Pair("Facsimile", serviceConsumer.getContact().getContact()
						.getContactInfo().getPhone().getFacsimileArray(0)));
		tem.getServiceConsumer().add(
				new Pair("DeliveryPoint", serviceConsumer.getContact()
						.getContact().getContactInfo().getAddress()
						.getDeliveryPointArray(0)));
		tem.getServiceConsumer().add(
				new Pair("City", serviceConsumer.getContact().getContact()
						.getContactInfo().getAddress().getCity()));
		tem.getServiceConsumer().add(
				new Pair("PostalCode", serviceConsumer.getContact()
						.getContact().getContactInfo().getAddress()
						.getPostalCode()));
		tem.getServiceConsumer().add(
				new Pair("Country", serviceConsumer.getContact().getContact()
						.getContactInfo().getAddress().getCountry()));
		tem.getServiceConsumer().add(
				new Pair("ElectronicMailAddress", serviceConsumer.getContact()
						.getContact().getContactInfo().getAddress()
						.getElectronicMailAddressArray(0)));
		tem.getServiceConsumer().add(
				new Pair("HoursOfService", serviceConsumer.getContact()
						.getContact().getContactInfo().getHoursOfService()));
		tem.getServiceConsumer()
				.add(
						new Pair("ContactInstructions", serviceConsumer
								.getContact().getContact().getContactInfo()
								.getContactInstructions()));
		return tem;
	}

	private Template getServiceProviderInformation(Template tem,
			ContactDocument serviceProvider) {

		tem.getServiceProvider().add(
				new Pair("Provider Name", serviceProvider.getContact()
						.getName()));
		tem.getServiceProvider().add(
				new Pair("Provider Site", serviceProvider.getContact()
						.getSite().getHref()));
		tem.getServiceProvider().add(
				new Pair("Individual Name", serviceProvider.getContact()
						.getContact().getIndividualName()));
		tem.getServiceProvider().add(
				new Pair("Position Name", serviceProvider.getContact()
						.getContact().getPositionName()));
		tem.getServiceProvider().add(
				new Pair("Voice", serviceProvider.getContact().getContact()
						.getContactInfo().getPhone().getVoiceArray(0)));
		tem.getServiceProvider().add(
				new Pair("Facsimile", serviceProvider.getContact().getContact()
						.getContactInfo().getPhone().getFacsimileArray(0)));
		tem.getServiceProvider().add(
				new Pair("DeliveryPoint", serviceProvider.getContact()
						.getContact().getContactInfo().getAddress()
						.getDeliveryPointArray(0)));
		tem.getServiceProvider().add(
				new Pair("City", serviceProvider.getContact().getContact()
						.getContactInfo().getAddress().getCity()));
		tem.getServiceProvider().add(
				new Pair("PostalCode", serviceProvider.getContact()
						.getContact().getContactInfo().getAddress()
						.getPostalCode()));
		tem.getServiceProvider().add(
				new Pair("Country", serviceProvider.getContact().getContact()
						.getContactInfo().getAddress().getCountry()));
		tem.getServiceProvider().add(
				new Pair("ElectronicMailAddress", serviceProvider.getContact()
						.getContact().getContactInfo().getAddress()
						.getElectronicMailAddressArray(0)));
		tem.getServiceProvider().add(
				new Pair("HoursOfService", serviceProvider.getContact()
						.getContact().getContactInfo().getHoursOfService()));
		tem.getServiceProvider()
				.add(
						new Pair("ContactInstructions", serviceProvider
								.getContact().getContact().getContactInfo()
								.getContactInstructions()));

		return tem;
	}

	private Template getServiceDescriptionInformation(Template tem,
			ServiceDescriptionDocument serviceDescription,
			TimeConstraintDocument contractRuntime) {

		tem.getServiceDescription().add(
				new Pair("Service Title", serviceDescription
						.getServiceDescription().getTitle().trim()));
		tem.getServiceDescription().add(
				new Pair("Abstract", serviceDescription.getServiceDescription()
						.getAbstract().trim()));
		tem.getServiceDescription().add(
				new Pair("Type", serviceDescription.getServiceDescription()
						.getType().trim()));
		tem.getServiceDescription().add(
				new Pair("Keywords", serviceDescription.getServiceDescription()
						.getKeywords().trim()));
		tem.getServiceDescription().add(
				new Pair("Start Time", contractRuntime.getTimeConstraint()
						.getStartTime().toString().trim()));
		tem.getServiceDescription().add(
				new Pair("End Time", contractRuntime.getTimeConstraint()
						.getEndTime().toString().trim()));

		return tem;
	}

	private Template getServicePropertiesInformation(Template tem,
			ServicePropertiesDocument serviceProperties) {
		for (PropertyType property : serviceProperties.getServiceProperties()
				.getPropertyArray()) {

			System.out.println("#############################################");
			Property prop = new Property(property.getTitle());

			prop.getPropertyAttributes().add(
					new Pair(property.getTitle() + " - Title", property
							.getTitle()));
			prop.getPropertyAttributes().add(
					new Pair(property.getTitle() + " - Abstract", property
							.getAbstract()));
			prop.getPropertyAttributes().add(
					new Pair(property.getTitle() + " - Type", property
							.getType()));
			prop.getPropertyAttributes().add(
					new Pair(property.getTitle() + " - Value", property
							.getValue()));
			
			System.out.println(prop.getPropertyAttributes().get(0).getKey());
			System.out.println(prop.getPropertyAttributes().get(0).getValue());
			System.out.println(prop.getPropertyAttributes().get(1).getKey());
			System.out.println(prop.getPropertyAttributes().get(1).getValue());
			System.out.println(prop.getPropertyAttributes().get(2).getKey());
			System.out.println(prop.getPropertyAttributes().get(2).getValue());
			System.out.println(prop.getPropertyAttributes().get(3).getKey());
			System.out.println(prop.getPropertyAttributes().get(3).getValue());

			System.out.println("#############################################");
			
//			if (property.getActiveMonitoring() != null) {
			if (property.getMonitoring() != null && property.getMonitoring().getActiveMonitoring() != null) {
				
//				ActiveMonitoringType activeMonitoring = property
//						.getActiveMonitoring();
				ActiveMonitoringType activeMonitoring = property.getMonitoring().getActiveMonitoring();
				
				if (activeMonitoring.getStart() != null) 
				prop.getPropertyAttributes().add(
						new Pair(property.getTitle()
								+ " - Start Active Monitoring",
								activeMonitoring.getStart().toString()));
				if (activeMonitoring.getStop() != null) 
				prop.getPropertyAttributes().add(
						new Pair(property.getTitle()
								+ " - Stop Active Monitoring", activeMonitoring
								.getStop().toString()));
				if (Integer.valueOf(activeMonitoring.getPeriod()) != null) 
				prop.getPropertyAttributes().add(
						new Pair(property.getTitle()
								+ " - Interval Active Monitoring", Integer
								.toString(activeMonitoring.getPeriod())));
				if (activeMonitoring.getSession() != null && Integer.valueOf(activeMonitoring.getSession().getCapacity()) != null) 
				prop.getPropertyAttributes().add(
						new Pair(property.getTitle()
								+ " - Capacity Active Monitoring", Integer
								.toString(activeMonitoring.getSession().getCapacity())));
				if (activeMonitoring.getRequestArray() != null && activeMonitoring.getRequestArray().length > 0) 
				prop.getPropertyAttributes().add(
						new Pair(property.getTitle()
								+ " - Request Active Monitoring",
								activeMonitoring.getRequestArray()[0].getContent().toString()));
				if (activeMonitoring.getResponse() != null && activeMonitoring.getResponse().getContent() != null) 
				prop.getPropertyAttributes().add(
						new Pair(property.getTitle()
								+ " - Response Active Monitoring",
								activeMonitoring.getResponse().getContent().toString()));

			}
			tem.getProperties().add(prop);
		}
		return tem;
	}

	private void getAgreementState(Agreement agree,
			AgreementDocument agreement, String agreementId) {
		AgreementPropertiesDocument agreementProperties = ManagerClient
				.instance().getAgreementState(managerUri,
						agreement.getAgreement().getAgreementId());
		String state = agreementProperties.getAgreementProperties()
				.getAgreementState().getState().toString();

		Pair agreementState = new Pair("Agreement State", state);
		agree.setAgreementState(agreementState);

		for (int i = 0; i < agreementProperties.getAgreementProperties()
				.getServiceTermStateArray().length; i++) {
			ServiceTermStateType serviceTermState = agreementProperties
					.getAgreementProperties().getServiceTermStateArray()[i];
			Property serviceState = new Property(serviceTermState.getTermName());
			serviceState.getPropertyAttributes().add(
					new Pair(serviceTermState.getTermName() + " - Title",
							serviceTermState.getTermName()));
			serviceState.getPropertyAttributes().add(
					new Pair(serviceTermState.getTermName() + " - State",
							serviceTermState.getState().toString()));
			agree.getStates().add(serviceState);

		}
	}

	private void getGuaranteeTerms(Agreement agree,
			AgreementDocument agreement, String agreementId) {
		AgreementPropertiesDocument agreementProperties = ManagerClient
				.instance().getAgreementState(managerUri,
						agreement.getAgreement().getAgreementId());

		for (int i = 0; i < agreementProperties.getAgreementProperties()
				.getGuaranteeTermStateArray().length; i++) {
			GuaranteeTermStateType guaranteeTermState = agreementProperties
					.getAgreementProperties().getGuaranteeTermStateArray()[i];
			CustomServiceLevelDocument customServiceLevel = ServiceLevelHelper
					.instance().get(
							agreementProperties.getAgreementProperties()
									.getTerms().getAll()
									.getGuaranteeTermArray(),
							guaranteeTermState.getTermName());
			if (customServiceLevel != null) {

				Property gts = new Property(guaranteeTermState.getTermName());
				gts.getPropertyAttributes().add(
						new Pair(guaranteeTermState.getTermName() + " - Title",
								guaranteeTermState.getTermName()));
				gts.getPropertyAttributes().add(
						new Pair(guaranteeTermState.getTermName()
								+ " - Abstract", customServiceLevel
								.getCustomServiceLevel().getAbstract()));
				gts.getPropertyAttributes().add(
						new Pair(guaranteeTermState.getTermName() + " - State",
								guaranteeTermState.getState().toString()));
				agree.getGuaranteeTerms().add(gts);

			}
		}

	}

	private Template getServiceBusinessValueInformation(Template tem,
			CustomBusinessValueDocument businessValue) {

		Property bValue = new Property(businessValue.getCustomBusinessValue()
				.getTitle());
		bValue.getPropertyAttributes().add(
				new Pair(businessValue.getCustomBusinessValue().getTitle()
						+ " - Title", businessValue.getCustomBusinessValue()
						.getTitle()));
		bValue.getPropertyAttributes().add(
				new Pair(businessValue.getCustomBusinessValue().getTitle()
						+ " - Abstract", businessValue.getCustomBusinessValue()
						.getAbstract()));
		bValue.getPropertyAttributes().add(
				new Pair(businessValue.getCustomBusinessValue().getTitle()
						+ " - Type", businessValue.getCustomBusinessValue()
						.getType()));
		bValue.getPropertyAttributes().add(
				new Pair(businessValue.getCustomBusinessValue().getTitle()
						+ " - Value", businessValue.getCustomBusinessValue()
						.getValue()));
		tem.getBuisnessValues().add(bValue);

		return tem;
	}

	private Template getServiceReferenceInformation(Template tem,
			ServiceReferenceDocument serviceReference) {
		tem.setReference(new Pair("Service Reference", serviceReference
				.getServiceReference().getURL()));
		return tem;
	}

	@Override
	public ArrayList<Property> getPropertyList()
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair getPair() throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Measurement> getMeasurementList(String id)
			throws IllegalArgumentException {
		ArrayList<Measurement> measurements = new ArrayList<Measurement>();
		MeasurementHistoryListDocument doc = ManagerClient.instance()
				.getMeasurements(managerUri, id);

		MeasurementHistoryListType history = doc.getMeasurementHistoryList();

		for (MeasurementType measurement : history.getMeasurementArray()) {
			Measurement measure = new Measurement();
			measure.setAgreementID(id);
			measure.setTimeStamp(measurement.getTimestamp().toString());
			for (int i = 0; i < measurement.getServiceProperties()
					.sizeOfPropertyArray(); i++) {
				measure.getPropertyAttributes().add(
						new Pair(measurement.getServiceProperties()
								.getPropertyArray()[i].getTitle(), measurement
								.getServiceProperties().getPropertyArray()[i]
								.getValue()));

			}
			measurements.add(measure);
		}
		return measurements;
	}

	public String getManagerUri() {
		return managerUri;
	}

	public Boolean setManagerUri(String managerUri) {

		List<TemplateDocument> list = ManagerClient.instance().getTemplateList(
				managerUri);
		if (list.isEmpty())
			return false;
		else {
			this.managerUri = managerUri;
			return true;
		}

	}

}
