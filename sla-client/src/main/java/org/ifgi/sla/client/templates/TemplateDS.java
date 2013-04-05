package org.ifgi.sla.client.templates;

import java.util.ArrayList;

import org.ifgi.sla.shared.Pair;
import org.ifgi.sla.shared.Property;
import org.ifgi.sla.shared.Template;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Singelton Datasource for templates. This Singleton is used in the @see
 * TemplatesGridTab. For each attribute of all templates a unique
 * DataSourceField is created. The Primary field for each record is templateID.
 * After creation of the Singleton for each template a record is added.
 * 
 * @author Kristof Lange
 * @version 1.0
 * @see AgreementDS
 */
public class TemplateDS extends DataSource {

	private static TemplateDS instance = null;

	/**
	 * templatesIDs holds the IDs of all agreements in the Datasource to check
	 * if a agreement already has been added.
	 */
	private ArrayList<String> templatesIDs = null;

	/**
	 * Lists of all child-datasources, the number of properties and states is
	 * variable.
	 */
	private static ArrayList<DataSource> dataSources = null;
	private static DataSource properties = null;
	private static DataSource states = null;

	/**
	 * Child-datasources
	 */
	private static DataSource template = null;
	private static DataSource serviceProvider = null;
	private static DataSource consumer = null;
	private static DataSource serviceDescription = null;
	private static DataSource serviceReference = null;
	
	ArrayList<String> propertyNames = new ArrayList<String>();
	ArrayList<String> stateNames = new ArrayList<String>();

	/**
	 * @param templates
	 *            is the result of the rpc-call
	 */

	public static TemplateDS getInstance(ArrayList<Template> templates) {

		if (instance == null) {

			instance = new TemplateDS("TemplateDS", templates);

		}
		return instance;
	}

	public static TemplateDS getInstance() {

		return instance;
	}

	/**
	 * @param templates
	 *            is the result of the @see SlaImpl-RPC method getTemplateList()
	 * @param id
	 *            identifies the datasource
	 */

	public TemplateDS(String id, ArrayList<Template> templates) {
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

		serviceDescription = new DataSource("ServiceDescription");
		serviceDescription.setTitle("ServiceDescription");
		serviceDescription.setClientOnly(true);

		serviceReference = new DataSource("ServiceReference");
		serviceReference.setTitle("ServiceReference");
		serviceReference.setClientOnly(true);
		
		

		dataSources = new ArrayList<DataSource>();
		properties = new DataSource("Properties");
		states = new DataSource("States");
		
		dataSources.add(template);
		dataSources.add(serviceProvider);
		dataSources.add(consumer);
		dataSources.add(serviceDescription);
		dataSources.add(serviceReference);

		templatesIDs = new ArrayList<String>();

		/**
		 * Lists are used to proof if fields are already created and added to
		 * datasources
		 */
		ArrayList<MyDataSourceField> fields = new ArrayList<MyDataSourceField>();
		
	
		for (Template template : templates) {

			/**
			 * creation of the @see MyDataSourceField 's
			 */

			for (Pair temp : template.getServiceProvider()) {

				MyDataSourceField dField = new MyDataSourceField(temp.getKey(),FieldType.TEXT,
						serviceProvider.getTitle());

				/**
				 * "Provider Name" must be visible in TemplatesDS because @see
				 * TemplatesGridTab shows it as column
				 */
				if (serviceProvider.getTitle().equals("Provider Name"))
					this.addField(dField);

				if (!fields.contains(dField))
					fields.add(dField);
			}

			for (Pair temp : template.getServiceConsumer()) {

				MyDataSourceField dField = new MyDataSourceField(temp.getKey(),FieldType.TEXT,
						consumer.getTitle());
				if (!fields.contains(dField))
					fields.add(dField);
			}

			for (Pair attribute : template.getServiceDescription()) {
				MyDataSourceField dField = new MyDataSourceField(attribute
						.getKey(), FieldType.TEXT,serviceDescription.getTitle());

				if (!fields.contains(dField))
					fields.add(dField);
			}

			MyDataSourceField name = new MyDataSourceField(template
					.getTemplateName().getKey(),FieldType.TEXT, TemplateDS.template.getTitle());
			MyDataSourceField tempID = new MyDataSourceField(template
					.getTemplateID().getKey(),FieldType.TEXT, TemplateDS.template.getTitle());
			tempID.setPrimaryKey(true);
			tempID.setHidden(true);

			MyDataSourceField reference = new MyDataSourceField(template
					.getReference().getKey(),FieldType.LINK, serviceReference.getTitle());

			fields.add(name);
			fields.add(reference);
			this.addField(tempID);
			this.addField(reference);
			
			
			
			
			
			for (Property property : template.getProperties()) {
				if (!propertyNames.contains(property.getName())) {
					propertyNames.add(property.getName());
					for (Pair attribute : property.getPropertyAttributes()) {
						MyDataSourceField e = new MyDataSourceField(attribute
								.getKey(),FieldType.TEXT, property.getName());

						if (property.getName() != null) {
							properties.addField(e);
						}

					}
					
				}
			}

			for (Property state : template.getStates()) {
				if (!stateNames.contains(state.getName())) {
					stateNames.add(state.getName());

					for (Pair attribute : state.getPropertyAttributes()) {
						MyDataSourceField e = new MyDataSourceField(attribute
								.getKey(),FieldType.TEXT, state.getName());

						if (state.getName() != null) {
							states.addField(e);
						}
					}

					
				}

			}
			properties.addField(tempID);
			states.addField(tempID);

			/**
			 * insert @see MyDataSourceField 's to the dataSources where they
			 * belongs to
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

		}

		addTemplatesToDataSource(templates);

	}

	/**
	 * add template records to the above created dataSource structure
	 * 
	 * @param templates
	 *            is an ArrayList which holds the values of the templates
	 */

	public void addTemplatesToDataSource(ArrayList<Template> templates) {

		if (!templates.isEmpty()) {
			for (Template template : templates) {

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
					record.setAttribute(template.getTemplateName().getKey(),
							template.getTemplateName().getValue());
					for (Pair temp : template.getServiceProvider()) {
						if (temp.getKey().equals("Provider Name"))
							record.setAttribute(temp.getKey(), temp.getValue());
					}

					this.addData(record);

					TemplateDS.template.addData(record);

					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					record.setAttribute(template.getReference().getKey(),
							template.getReference().getValue());
					serviceReference.addData(record);
					
					record = new ListGridRecord();
					record.setAttribute(template.getTemplateID().getKey(),
							template.getTemplateID().getValue());
					for (Property property : template.getProperties()) {
						
						
						for (Pair attribute : property.getPropertyAttributes()) {

							record.setAttribute(attribute.getKey(), attribute
									.getValue());

						}
						
						

					}properties.addData(record);
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

}
