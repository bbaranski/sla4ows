package org.ifgi.sla.client.agreements;

import java.util.ArrayList;

import org.ifgi.sla.client.templates.MyDataSourceField;
import org.ifgi.sla.shared.Agreement;
import org.ifgi.sla.shared.Pair;
import org.ifgi.sla.shared.Property;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceLinkField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.widgets.grid.ListGridRecord;


/**
 * Singelton Datasource for agreements. This Singleton is used in the @see AgreementsGridTab. For each attribute of all agreements a unique DataSourceField is created. 
 * The Primary field for each record is templateID. After creation of the Singleton for each agreement a record is added.
 * @author  Kristof Lange
 * @version 1.0
 * @see     TemplatesDS
 */

public class AgreementDS extends DataSource {

	private static AgreementDS instance = null;
	
	/**
	 * templatesIDs holds the IDs of all agreements in the Datasource to check if a agreement already has been added.
	 */
	private ArrayList<String> templatesIDs = null;
	
	/**
	 * Lists of all child-datasources, the number of properties and states is variable.
	 */
	private static ArrayList<DataSource> dataSources = null;
	private static DataSource properties = null;
	private static DataSource states = null;
	private static DataSource buisnessValues = null;
	private static DataSource guaranteeTerms = null;

	
	/**
	 * Child-datasources
	 */
	private static DataSource template = null;
	private static DataSource serviceProvider = null;
	private static DataSource consumer = null;
	private static DataSource serviceDescription = null;
	private static DataSource serviceReference = null;
	private static DataSource agreementState = null;
	
	private static ArrayList<String> propertyNames = new ArrayList<String>();
	private static ArrayList<String> stateNames = new ArrayList<String>();
	private static ArrayList<String> guaranteeNames = new ArrayList<String>();
	private static ArrayList<String> buisnessNames = new ArrayList<String>();
	private static ArrayList<MyDataSourceField> fields = new ArrayList<MyDataSourceField>();

	
	/**
	 * @param templates is the result of the rpc-call
	 */
	public static AgreementDS getInstance(ArrayList<Agreement> result) {

		if (instance == null) {

			instance = new AgreementDS("AgreementDS", result);

		}
		return instance;
	}

	public static AgreementDS getInstance() {

		return instance;
	}
	/**
	 * @param templates is the result of the @see SlaImpl-RPC method getAgreementList()
	 * @param id identifies the datasource
	 */

	public AgreementDS(String id, ArrayList<Agreement> templates) {
		setID(id);
		setTitle(id);
		setClientOnly(true);

		template = new DataSource("Template");
		template.setTitle("Template");
		template.setClientOnly(true);

		serviceProvider = new DataSource("ServiceProvider");
		serviceProvider.setTitle("ServiceProvider");
		serviceProvider.setClientOnly(true);

		consumer = new DataSource("Consumer");
		consumer.setTitle("Consumer");
		consumer.setClientOnly(true);
		
		agreementState = new DataSource("AgreementState");
		agreementState.setTitle("AgreementState");
		agreementState.setClientOnly(true);

		serviceDescription = new DataSource("ServiceDescription");
		serviceDescription.setTitle("ServiceDescription");
		serviceDescription.setClientOnly(true);
		
		properties = new DataSource("Properties");
		properties.setTitle("Properties");
		properties.setClientOnly(true);
		
		states = new DataSource("States");
		states.setTitle("States");
		states.setClientOnly(true);
		
		buisnessValues = new DataSource("BuisnessValues");
		buisnessValues.setTitle("BuisnessValues");
		buisnessValues.setClientOnly(true);
		
		guaranteeTerms = new DataSource("GuaranteeTerms");
		guaranteeTerms.setTitle("GuaranteeTerms");
		guaranteeTerms.setClientOnly(true);

		serviceReference = new DataSource("ServiceReference");
		serviceReference.setTitle("ServiceReference");
		serviceReference.setClientOnly(true);

		dataSources = new ArrayList<DataSource>();
		
	
		
		
		dataSources.add(template);
		dataSources.add(serviceProvider);
		dataSources.add(consumer);
		dataSources.add(serviceDescription);
		dataSources.add(serviceReference);
		dataSources.add(agreementState);
		dataSources.add(properties);
		dataSources.add(states);
		dataSources.add(buisnessValues);
		dataSources.add(guaranteeTerms);
		
		
		templatesIDs = new ArrayList<String>();
		/**
		 * Lists are used to proof if fields are already created and added to datasources
		 */
		
		/**
		 * Primary key: tempID
		 */
		MyDataSourceField tempID=null;
		
		for (Agreement agreement : templates) {
			
			/**
			 * creation of the @see MyDataSourceField 's
			 */

			for (Pair temp : agreement.getServiceProvider()) {

				MyDataSourceField dField = new MyDataSourceField(temp.getKey(),FieldType.TEXT,
						serviceProvider.getTitle());
				
				/**
				 * "Provider Name" must be visible in AgreementDS because @see AgreeGridTab shows it as column 
				 */

				
					
				if (!fields.contains(dField)){
					if(dField.getName().equals("Provider Name"))
					template.addField(dField);
					fields.add(dField);}
			}

			for (Pair temp : agreement.getServiceConsumer()) {

				MyDataSourceField dField = new MyDataSourceField(temp.getKey(),FieldType.TEXT,
						consumer.getTitle());
				
				if (!fields.contains(dField))
					fields.add(dField);
			}
			for (Pair attribute : agreement.getServiceDescription()) {
				
				MyDataSourceField dField = new MyDataSourceField(attribute
						.getKey(),FieldType.TEXT, serviceDescription.getTitle());
				
				if (!fields.contains(dField)){
					if(dField.getName().equals("Service Title"))
					template.addField(dField);
					fields.add(dField);}
			
			}

			MyDataSourceField name = new MyDataSourceField(agreement
					.getTemplateName().getKey(),FieldType.TEXT, AgreementDS.template
					.getTitle());
			tempID = new MyDataSourceField(agreement
					.getTemplateID().getKey(),FieldType.TEXT, AgreementDS.template.getTitle());
			tempID.setPrimaryKey(true);
			tempID.setHidden(true);
			MyDataSourceField reference = new MyDataSourceField(agreement
					.getReference().getKey(),FieldType.LINK, serviceReference.getTitle());
			
			/**
			 * only add each field once
			 */
			if (!fields.contains(reference)){template.addField(reference);fields.add(reference);}
			if (!fields.contains(name)){template.addField(name);fields.add(name);}
			
			

		
			MyDataSourceField agreeState = new MyDataSourceField(agreement.getAgreementState().getKey(),FieldType.TEXT, agreementState.getTitle());
			if (!fields.contains(agreeState))fields.add(agreeState);
			
			
			
			for (Property property : agreement.getProperties()) {
				if ((!propertyNames.contains(property.getName()))&& (property.getName()!=null)) {
					propertyNames.add(property.getName());
					for (Pair attribute : property.getPropertyAttributes()) {
						MyDataSourceField e = new MyDataSourceField(attribute
								.getKey(),FieldType.TEXT, property.getName());
						if (!fields.contains(e))properties.addField(e);
						}

					}
					
				}
			

			for (Property state : agreement.getStates()) {
				if (!stateNames.contains(state.getName())) {
					stateNames.add(state.getName());

					for (Pair attribute : state.getPropertyAttributes()) {
						MyDataSourceField e = new MyDataSourceField(attribute
								.getKey(),FieldType.TEXT, state.getName());
						
						if (state.getName() != null) {
							if (!fields.contains(e))states.addField(e);
						
						}
					}

					
				}

			}
		}
			
			/**
			 * insert @see MyDataSourceField 's to the dataSources where they belongs to
			 */

			for (DataSource dataSource : dataSources) {
				dataSource.addField(tempID);
				for (MyDataSourceField dataSourceTextField : fields) {
					if (dataSource.getTitle().equals(
							dataSourceTextField.getDataSourceName())) {
						dataSource.addField(dataSourceTextField);
					}

				}
			}
		

		addAgreementsToDataSource(templates);

	}
	
	/**
	 * add agreement records to the above created dataSource structure
	 * @param templates is an ArrayList which holds the values of the agreements
	 */

	public void addAgreementsToDataSource(ArrayList<Agreement> templates) {

		if (!templates.isEmpty()) {
			for (Agreement template : templates) {
				if (!this.templatesIDs.contains(template.getTemplateID()
						.getValue())) {

					ListGridRecord record = new ListGridRecord();
					for (Pair temp : template.getServiceProvider()) {

						record.setAttribute(temp.getKey(), temp.getValue());
					}
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					serviceProvider.addData(record);

					record = new ListGridRecord();
					for (Pair temp : template.getServiceConsumer()) {

						record.setAttribute(temp.getKey(), temp.getValue());

					}
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					consumer.addData(record);

					record = new ListGridRecord();

					for (Pair attribute : template.getServiceDescription()) {
						record.setAttribute(attribute.getKey(), attribute
								.getValue());
					}
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					for (int i = 0; i < record.getAttributes().length; i++) {

					}
					serviceDescription.addData(record);

					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					record.setAttribute(template.getReference().getKey(),
							template.getReference().getValue());
					for (Pair temp : template.getServiceProvider()) {
						if (temp.getKey().equals("Provider Name"))
							record.setAttribute(temp.getKey(), temp.getValue());
					}
					for (Pair temp : template.getServiceDescription()) {
						if (temp.getKey().equals("Service Title"))
							record.setAttribute(temp.getKey(), temp.getValue());
					}

				

					AgreementDS.template.addData(record);

					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					record.setAttribute(template.getReference().getKey(),
							template.getReference().getValue());
					serviceReference.addData(record);
					
					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					record.setAttribute(template.getAgreementState().getKey(),
							template.getAgreementState().getValue());
					agreementState.addData(record);

					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					for (Property property : template.getProperties()) {
						
						
						for (Pair attribute : property.getPropertyAttributes()) {

							record.setAttribute(attribute.getKey(), attribute
									.getValue());
						}
						}
					properties.addData(record);
					
					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					for (Property property : template.getStates()) {
						
						
						for (Pair attribute : property.getPropertyAttributes()) {

							record.setAttribute(attribute.getKey(), attribute
									.getValue());

						}
					

					}	states.addData(record);

					this.templatesIDs.add(template.getTemplateID().getValue());

				}
			}
		}

	}

	public void updateAgreement(ArrayList<Agreement> templates) {

		if (!templates.isEmpty()) {
			for (Agreement template : templates) {
				if (!this.templatesIDs.contains(template.getTemplateID()
						.getValue())) {

					ListGridRecord record = new ListGridRecord();
					for (Pair temp : template.getServiceProvider()) {

						record.setAttribute(temp.getKey(), temp.getValue());
					}
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					serviceProvider.updateData(record);

					record = new ListGridRecord();
					for (Pair temp : template.getServiceConsumer()) {

						record.setAttribute(temp.getKey(), temp.getValue());

					}
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					consumer.updateData(record);

					record = new ListGridRecord();

					for (Pair attribute : template.getServiceDescription()) {
						record.setAttribute(attribute.getKey(), attribute
								.getValue());
					}
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					for (int i = 0; i < record.getAttributes().length; i++) {

					}
					serviceDescription.updateData(record);

					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					record.setAttribute(template.getReference().getKey(),
							template.getReference().getValue());
					for (Pair temp : template.getServiceProvider()) {
						if (temp.getKey().equals("Provider Name"))
							record.setAttribute(temp.getKey(), temp.getValue());
					}
					for (Pair temp : template.getServiceDescription()) {
						if (temp.getKey().equals("Service Title"))
							record.setAttribute(temp.getKey(), temp.getValue());
					}

					AgreementDS.template.updateData(record);

					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					record.setAttribute(template.getReference().getKey(),
							template.getReference().getValue());
					for (int i = 0; i < record.getAttributes().length; i++) {

					}

					serviceReference.updateData(record);
					
					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					record.setAttribute(template.getAgreementState().getKey(),
							template.getAgreementState().getValue());
					agreementState.updateData(record);

					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					for (Property property : template.getProperties()) {
						
						
						for (Pair attribute : property.getPropertyAttributes()) {

							record.setAttribute(attribute.getKey(), attribute
									.getValue());

						}
						
						

					}properties.updateData(record);
					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					for (Property property : template.getStates()) {
						
						
						for (Pair attribute : property.getPropertyAttributes()) {

							record.setAttribute(attribute.getKey(), attribute
									.getValue());

						}
					

					}	states.updateData(record);

					this.templatesIDs.add(template.getTemplateID().getValue());

				}
			}
		}
	}

	public static DataSource getServiceProvider() {
		return serviceProvider;
	}

	public static DataSource getServiceDescription() {
		return serviceDescription;
	}

	public static DataSource getServiceReference() {
		return serviceReference;
	}

	public static DataSource getTemplate() {
		return template;
	}

	public static DataSource getServiceConsumer() {
		// TODO Auto-generated method stub
		return consumer;
	}

	public static DataSource getProperties() {
		return properties;
	}

	public static DataSource getStates() {
		return states;
	}

	public ArrayList<String> getPropertyNames() {
		return propertyNames;
	}

	public ArrayList<String> getStateNames() {
		return stateNames;
	}

	public static void setProperties(DataSource properties) {
		AgreementDS.properties = properties;
	}

	public static DataSource getAgreementState() {
		return agreementState;
	}

}
