package org.ifgi.sla.client.templates;

/*
 * Smart GWT (GWT for SmartClient)
 * Copyright 2008 and beyond, Isomorphic Software, Inc.
 *
 * Smart GWT is free software; you can redistribute it and/or modify it
 * under the guaranteeTerms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.  Smart GWT is also
 * available under typical commercial license guaranteeTerms - see
 * http://smartclient.com/license
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

import java.util.ArrayList;

import org.ifgi.sla.client.SlaService;
import org.ifgi.sla.client.SlaServiceAsync;
import org.ifgi.sla.client.agreements.AgreementDS;
import org.ifgi.sla.shared.Template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.docs.Appearance;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.DateTimeItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.viewer.CellStyleHandler;
import com.smartgwt.client.widgets.viewer.DetailFormatter;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

/**
 * TemplatesGridTab shows the attributes of the chosen template. The underlying
 * DataSource is @see TemplatesDS It contains a set of DetailViewer where each
 * DetailViewer holds the attributes of a child datasource
 * 
 * @author Kristof Lange
 * @version 1.0
 * @see AgreementGridTab
 */
public class TemplatesGridTab extends Tab {
	private final SlaServiceAsync SlaService = GWT.create(SlaService.class);
	final TemplateDS dataSource;
	final ListGrid listGrid;
	Window winModal = null;

	public TemplatesGridTab(String title) {
		super(title);

		dataSource = TemplateDS.getInstance();

		VLayout layout = new VLayout();
		//final ArrayList<DetailViewer> dvl = new ArrayList<DetailViewer>();

		final DetailViewer properties = new DetailViewer();

		properties.setVisible(false);
		properties.setPadding(20);
		properties.setIsGroup(true);
		properties.setGroupTitle("Properties");
		properties.setDataSource(TemplateDS.getProperties());
		setFormatter(properties,TemplateDS.getInstance().getPropertyNames());

		final DetailViewer states = new DetailViewer();
		states.setVisible(false);
		states.setPadding(20);
		states.setIsGroup(true);
		states.setGroupTitle("States");
		states.setDataSource(TemplateDS.getStates());
		setFormatter(states,TemplateDS.getInstance().getStateNames());

		final DetailViewer tv = new DetailViewer();
		tv.setVisible(false);
		tv.setPadding(20);
		tv.setIsGroup(true);
		tv.setGroupTitle("Template");
		tv.setDataSource(TemplateDS.getTemplate());

		final DetailViewer sprovV = new DetailViewer();
		sprovV.setPadding(20);
		sprovV.setVisible(false);
		sprovV.setIsGroup(true);
		sprovV.setGroupTitle("Service Provider");
		sprovV.setDataSource(TemplateDS.getServiceProvider());

		final DetailViewer sConsumer = new DetailViewer();
		sConsumer.setPadding(20);
		sConsumer.setVisible(false);
		sConsumer.setIsGroup(true);
		sConsumer.setGroupTitle("Service Consumer");
		sConsumer.setDataSource(TemplateDS.getServiceConsumer());

		final DetailViewer sdesV = new DetailViewer();
		sdesV.setPadding(20);
		sdesV.setVisible(false);
		sdesV.setIsGroup(true);
		sdesV.setGroupTitle("Service Description");
		sdesV.setDataSource(TemplateDS.getServiceDescription());

		final DetailViewer sRefV = new DetailViewer();
		sRefV.setVisible(false);

		sRefV.setPadding(20);
		sRefV.setIsGroup(true);
		sRefV.setGroupTitle("Service Reference");
		sRefV.setDataSource(TemplateDS.getServiceReference());
		final DetailViewerField ref = new DetailViewerField(AgreementDS
				.getServiceReference().getField("Service Reference").getTitle());
		
		DetailFormatter format = new DetailFormatter() {

			@Override
			public String format(Object value, Record record,
					DetailViewerField field) {
				return "<a href='" + record.getAttribute("Service Reference") 
						+ "'> <span style=\""+sRefV.getCellStyle()+" color: blue;\"> "
						+ "&nbsp;" + record.getAttribute("Service Reference")  + "</span> </a>";

			}

		};

		ref.setDetailFormatter(format);
		sRefV.setFields(ref);

		/**
		 * listGrid contains a DynamicForm in which the agreement is negotiated.
		 * User parameters must be specified and a constraint of time in which
		 * the agreement is active
		 */

		listGrid = new ListGrid() {
			protected Canvas getExpansionComponent(final ListGridRecord record) {

				final ListGrid grid = this;

				VLayout layout = new VLayout(5);

				layout.setPadding(10);

				final DynamicForm df = new DynamicForm();

				IButton saveButton = new IButton("Add Agreement");
				saveButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {

						final DynamicForm form = new DynamicForm();
						form.setWidth(300);
						final ArrayList<TextItem> textItems = new ArrayList<TextItem>();
						final TextItem name = new TextItem();
						name.setName("Name");
						name.setTitle("Name");
						name.setDefaultValue("Mustermann GmbH");
						textItems.add(name);
						final TextItem indiName = new TextItem();
						indiName.setName("IndividualName");
						indiName.setTitle("Individual Name");
						indiName.setDefaultValue("Max Mustermann");
						textItems.add(indiName);
						final TextItem posName = new TextItem();
						posName.setName("PositionName");
						posName.setTitle("Position Name");
						posName.setDefaultValue("letterman");
						textItems.add(posName);
						final TextItem pSite = new TextItem();
						pSite.setName("Site");
						pSite.setTitle("Site");
						pSite.setDefaultValue("letterman");
						textItems.add(pSite);
						final TextItem pVoice = new TextItem();
						pVoice.setName("Voice");
						pVoice.setTitle("Voice");
						pVoice.setDefaultValue("letterman");
						textItems.add(pVoice);
						final TextItem pFacsimile = new TextItem();
						pFacsimile.setName("Facsimile");
						pFacsimile.setTitle("Facsimile");
						pFacsimile.setDefaultValue("letterman");
						textItems.add(pFacsimile);
						final TextItem pUrl = new TextItem();
						pUrl.setName("url");
						pUrl.setTitle("URL");
						pUrl.setDefaultValue("letterman");
						textItems.add(pUrl);
						final TextItem pDeliveryPoint = new TextItem();
						pDeliveryPoint.setName("DeliveryPoint");
						pDeliveryPoint.setTitle("Delivery Point");
						pDeliveryPoint.setDefaultValue("letterman");
						textItems.add(pDeliveryPoint);
						final TextItem pCity = new TextItem();
						pCity.setName("City");
						pCity.setTitle("City");
						pCity.setDefaultValue("letterman");
						textItems.add(pCity);
						final TextItem pPostalCode = new TextItem();
						pPostalCode.setName("PostalCode");
						pPostalCode.setTitle("Postal Code");
						pPostalCode.setDefaultValue("letterman");
						textItems.add(pPostalCode);
						final TextItem pCountry = new TextItem();
						pCountry.setName("Country");
						pCountry.setTitle("Country");
						pCountry.setDefaultValue("letterman");
						textItems.add(pCountry);
						final TextItem pMailAddress = new TextItem();
						pMailAddress.setName("Email");
						pMailAddress.setTitle("Email");
						pMailAddress.setDefaultValue("letterman");
						textItems.add(pMailAddress);

						final ButtonItem buttonItem = new ButtonItem();
						buttonItem.setName("ok");
						buttonItem.setTitle("OK");
						buttonItem.setStartRow(false);
						buttonItem.setEndRow(false);
						buttonItem.setWidth(100);
						buttonItem.setColSpan(2);
						buttonItem.setAlign(Alignment.CENTER);

						final DateTimeItem startT = new DateTimeItem();

						startT.setName("StartTime");
						startT.setTitle("Start Time");
						startT
								.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATETIME);

						final DateTimeItem endT = new DateTimeItem();
						endT
								.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATETIME);
						endT.setStartRow(false);
						endT.setEndRow(false);
						endT.setName("EndTime");
						endT.setTitle("End Time");

						buttonItem
								.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

									@Override
									public void onClick(
											com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
										boolean empty = false;
										for (TextItem item : textItems) {
											if (item.getValue().toString()
													.trim().isEmpty())
												empty = true;
										}
										if (empty != true) {

											SlaService
													.addAgreement(
															record
																	.getAttribute("Template ID"),
															name.getValue()
																	.toString(),
															pSite.getValue()
																	.toString(),
															indiName.getValue()
																	.toString(),
															posName.getValue()
																	.toString(),
															pVoice.getValue()
																	.toString(),
															pFacsimile
																	.getValue()
																	.toString(),
															pUrl.getValue()
																	.toString(),
															pDeliveryPoint
																	.getValue()
																	.toString(),
															pCity.getValue()
																	.toString(),
															pPostalCode
																	.getValue()
																	.toString(),
															pCountry.getValue()
																	.toString(),
															pMailAddress
																	.getValue()
																	.toString(),
															startT
																	.getDisplayValue(),
															endT
																	.getDisplayValue(),
															new AsyncCallback<Boolean>() {
																public void onFailure(
																		Throwable caught) {
																	SC
																			.say(
																					"Error",
																					"Service not available");

																}

																@Override
																public void onSuccess(
																		Boolean result) {
																	if (result == true)

																		SC
																				.say(
																						"OK",
																						new BooleanCallback() {

																							@Override
																							public void execute(
																									Boolean value) {
																								winModal
																										.clear();
																								winModal
																										.hide();
																								SC
																										.clearPrompt();

																							}
																						});

																	else
																		SC
																				.say(
																						"Error",
																						"Start Time must be before End Time");

																}
															});
										} else
											SC
													.say("Error",
															"Empty values are not allowed");
									}

								});

						form.setFields(name, pSite, indiName, posName, pVoice,
								pFacsimile, pUrl, pDeliveryPoint, pCity,
								pPostalCode, pCountry, pMailAddress, startT,
								endT, buttonItem);

						winModal = new Window();
						winModal.setWidth(315);
						winModal.setHeight(450);
						winModal.setTitle("Contact Details");
						winModal.setShowMinimizeButton(false);
						winModal.setIsModal(true);
						winModal.setShowModalMask(true);
						winModal.centerInPage();
						winModal.addCloseClickHandler(new CloseClickHandler() {

							@Override
							public void onCloseClick(CloseClientEvent event) {

								winModal.destroy();

							}
						});

						winModal.addItem(form);
						winModal.draw();

					}

				});
				saveButton.setWidth100();
				IButton cancelButton = new IButton("Done");
				cancelButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						grid.collapseRecord(record);
					}
				});
				cancelButton.setWidth100();

				HLayout hLayout = new HLayout(10);
				hLayout.setAlign(Alignment.CENTER);
				hLayout.addMember(saveButton);
				hLayout.addMember(cancelButton);

				layout.addMember(df);

				layout.addMember(hLayout);
				layout.setMaxHeight(400);
				return layout;
			}
		};
		listGrid.setCanExpandRecords(true);

		listGrid.setWidth100();
		listGrid.setHeight(200);
		listGrid.setDataSource(dataSource);

		listGrid.setAutoFetchData(true);

		listGrid.addRecordClickHandler(new RecordClickHandler() {
			public void onRecordClick(RecordClickEvent event) {

				if (TemplateDS.getTemplate().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					tv.fetchData(crit);
					tv.setVisible(true);
				}

				if (TemplateDS.getServiceProvider().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					sprovV.fetchData(crit);
					sprovV.setVisible(true);
				}

				if (TemplateDS.getProperties().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					properties.fetchData(crit);
					properties.setVisible(true);
				}
				if (TemplateDS.getStates().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					states.fetchData(crit);
					states.setVisible(true);
				}

				if (TemplateDS.getServiceDescription().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					sdesV.fetchData(crit);
					sdesV.setVisible(true);
				}

				if (TemplateDS.getServiceReference().getFieldNames(true).length > 0) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					sRefV.fetchData(crit);
					sRefV.setVisible(true);
				}

				if (TemplateDS.getServiceConsumer().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					sConsumer.fetchData(crit);
					sConsumer.setVisible(true);
				}

				String id = listGrid.getSelectedRecord().getAttribute(
						"Template ID");
//				for (final DetailViewer detailViewer : dvl) {
//					detailViewer.setVisible(true);
//					if (detailViewer.getDataSource().getFieldNames(true).length > 0) {
//						Criteria crit = new Criteria("Template ID", id);
//						detailViewer.fetchData(crit, new DSCallback() {
//
//							@Override
//							public void execute(DSResponse response,
//									Object rawData, DSRequest request) {
//
//								if (response.getData().length == 0)
//									detailViewer.setVisible(false);
//
//							}
//
//						});
//						detailViewer.setShowEmptyMessage(false);
//
//					}
//
//				}

			}
		});
		layout.setPadding(20);
		layout.addMember(listGrid);
		layout.addMember(tv);
		layout.addMember(sprovV);
		layout.addMember(sConsumer);
		layout.addMember(sdesV);
		layout.addMember(sRefV);
		layout.addMember(properties);
		layout.addMember(states);
		ListGridField providerField = new ListGridField("Provider Name");
		ListGridField templateIDField = new ListGridField("Template ID");
		ListGridField templateNameField = new ListGridField("Template Name");

		listGrid.setFields(providerField, templateIDField, templateNameField);

		listGrid.draw();

		this.setPane(layout);

	}

	private void setFormatter(final DetailViewer dv, ArrayList<String> list) {
		ArrayList<DetailViewerField> fields = new ArrayList<DetailViewerField>();
		
		for (final String name : list) {

			final DetailViewerField dField = new DetailViewerField();
			dField.setTitle(name);
			dField.setCellStyle(dv.getCellStyle());
			DetailFormatter format = new DetailFormatter() {

				@Override
				public String format(Object value, Record record,
						DetailViewerField dField) {
					String type = "", abs = "", val = "", state = "";
					String help = "<html><head><STYLE TYPE=\"text/css\">"+"TABLE {width:100%; border-style: solid; border-spacing: 10px; border: 1px}TD{font-family:Arial,Verdana,sans-serif; font-size:11px; background-color:white;}</STYLE></head><body>";
					for (int j = 0; j < record.getAttributes().length; j++) {
						String n = record.getAttributes()[j];
						if (n.startsWith(name) && n.contains("Type"))
							type = record.getAttribute(n);
						if (n.startsWith(name) && n.contains("Abstract"))
							abs = record.getAttribute(n);
						if (n.startsWith(name) && n.contains("Value"))
							val = record.getAttribute(n);
						if (n.startsWith(name) && n.contains("State"))
							state = record.getAttribute(n);
					}
					String color;
					String style="style='font-family:Arial,Verdana,sans-serif; font-size:11px; border: 1px; background-color:white;";
					
					if (state == "") {
						help = "<table><tbody>";
						if(!val.equals("")){help+="<tr><td "+style+"'>"+val+"</td></tr>";}
						if(!type.equals("")){help+="<tr><td "+style+"'>"+type+"</td></tr>";}
						if(!abs.equals("")){help+="<tr><td "+style+"'>"+abs+"</td></tr>";}
						help+= "</tbody></table>";
					} else
					{
						
					color="black";
						if (state.equals("Observed")||state.equals("Completed"))
							color="green";
						else
							if (state.equals("NotReady")||state.equals("NotDetermined"))
								color="red";
						
						help = "<table><tbody><tr><td "+style+"color:"+color+";'>"
							+ state +"</td>";
						if(!val.equals("")){help+="<tr><td "+style+"'>"+val+"</td></tr>";}
						if(!type.equals("")){help+="<tr><td "+style+"'>"+type+"</td></tr>";}
						if(!abs.equals("")){help+="<tr><td "+style+"'>"+abs+"</td></tr>";}

							help+= "</tbody></table>";
					}
					return help+"</body></html>";
					
				}

			};
			dField.setDetailFormatter(format);
			fields.add(dField);
		}
		dv.setFields(fields.toArray(new DetailViewerField[fields.size()]));
	}

	public void updateTemplates() {

		SlaService.getTemplateList(new AsyncCallback<ArrayList<Template>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				System.out.println("here we stay");
			}

			@Override
			public void onSuccess(ArrayList<Template> result) {

				dataSource.addTemplatesToDataSource(result);
			}
		});
	}

	public ListGrid getListGrid() {
		return listGrid;
	}
}
