package org.ifgi.sla.client.agreements;

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
import org.ifgi.sla.client.monitoring.MonitoringTab;
import org.ifgi.sla.client.templates.TemplateDS;
import org.ifgi.sla.shared.Agreement;
import org.ifgi.sla.shared.Measurement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.viewer.DetailFormatter;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

/**
 * AgreementGridTab shows the attributes of the chosen agreement. The underlying
 * DataSource is @see AgreementDS It contains a set of DetailViewer where each
 * DetailViewer holds the attributes of a child datasource
 * 
 * @author Kristof Lange
 * @version 1.0
 * @see TemplatesGridTab
 */
public class AgreementGridTab extends Tab {
	private final SlaServiceAsync SlaService = GWT.create(SlaService.class);
	AgreementDS dataSource;
	final ListGrid listGrid;
	TabSet tabset;

	public AgreementGridTab(String title, final TabSet tabset) {
		super(title);
		this.tabset = tabset;

		dataSource = AgreementDS.getInstance();

		/**
		 * for each child datasource a DetailViewer is used
		 * 
		 */

		final DetailViewer properties = new DetailViewer();

		properties.setVisible(false);
		properties.setPadding(20);
		properties.setIsGroup(true);
		properties.setGroupTitle("Properties");
		properties.setDataSource(AgreementDS.getProperties());
		setFormatter(properties, AgreementDS.getInstance().getPropertyNames());

		final DetailViewer states = new DetailViewer();
		states.setVisible(false);
		states.setPadding(20);
		states.setIsGroup(true);
		states.setGroupTitle("States");
		states.setDataSource(AgreementDS.getStates());
		setFormatter(states, AgreementDS.getInstance().getStateNames());

		final DetailViewer agreementState = new DetailViewer();
		agreementState.setVisible(false);
		agreementState.setPadding(20);
		agreementState.setIsGroup(true);
		agreementState.setGroupTitle("Agreement State");
		agreementState.setDataSource(AgreementDS.getAgreementState());

		final DetailViewerField agree = new DetailViewerField(AgreementDS
				.getAgreementState().getField("Agreement State").getTitle());

		DetailFormatter format2 = new DetailFormatter() {

			@Override
			public String format(Object value, Record record,
					DetailViewerField field) {
				String state = record.getAttribute("Agreement State");
				String color = "black";
				String style = "style='font-family:Arial,Verdana,sans-serif; font-size:11px; border: 1px; background-color:white;";
				String help = "";

				if (state.equals("Observed") || state.equals("Completed")
						|| state.equals("Ready"))
					color = "green";
				else if (state.equals("NotReady")
						|| state.equals("NotDetermined"))
					color = "red";

				help = "<table><tbody><tr><td " + style + "color:" + color
						+ ";'>" + state + "</td></tbody></table>";

				return help;
			}

		};

		agree.setDetailFormatter(format2);
		agreementState.setFields(agree);

		final DetailViewer tv = new DetailViewer();
		tv.setVisible(false);
		tv.setPadding(20);
		tv.setIsGroup(true);
		tv.setGroupTitle("Template");
		tv.setDataSource(AgreementDS.getTemplate());

		final DetailViewer sprovV = new DetailViewer();
		sprovV.setPadding(20);
		sprovV.setVisible(false);
		sprovV.setIsGroup(true);
		sprovV.setGroupTitle("Service Provider");
		sprovV.setDataSource(AgreementDS.getServiceProvider());

		final DetailViewer sConsumer = new DetailViewer();
		sConsumer.setPadding(20);
		sConsumer.setVisible(false);
		sConsumer.setIsGroup(true);
		sConsumer.setGroupTitle("Service Consumer");
		sConsumer.setDataSource(AgreementDS.getServiceConsumer());

		final DetailViewer sdesV = new DetailViewer();
		sdesV.setVisible(false);
		sdesV.setPadding(20);
		sdesV.setIsGroup(true);
		sdesV.setGroupTitle("Service Description");
		sdesV.setDataSource(AgreementDS.getServiceDescription());

		/*
		 * DetailViewer with href-Formatting of the Service Reference
		 */
		final DetailViewer sRefV = new DetailViewer();
		sRefV.setVisible(false);
		sRefV.setPadding(20);
		sRefV.setIsGroup(true);
		sRefV.setGroupTitle("Service Reference");
		sRefV.setDataSource(AgreementDS.getServiceReference());

		final DetailViewerField ref = new DetailViewerField(AgreementDS
				.getServiceReference().getField("Service Reference").getTitle());

		DetailFormatter format = new DetailFormatter() {

			@Override
			public String format(Object value, Record record,
					DetailViewerField field) {
				return "<a href='" + record.getAttribute("Service Reference")
						+ "'> <span style=\"" + sRefV.getCellStyle()
						+ " color: blue;\"> " + "&nbsp;"
						+ record.getAttribute("Service Reference")
						+ "</span> </a>";

			}

		};

		ref.setDetailFormatter(format);
		sRefV.setFields(ref);

		/*
		 * end of Reference-DetailViewer
		 */

		VLayout layout = new VLayout();

		/**
		 * the listGrid contains a column of buttons to monitor the selected
		 * agreement
		 */

		listGrid = new ListGrid() {
			@Override
			protected Canvas createRecordComponent(final ListGridRecord record,
					Integer colNum) {

				String fieldName = this.getFieldName(colNum);
				if (fieldName.equals("buttonField")) {
					Button button = new Button();

					button.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							for (int i = 0; i < tabset.getTabs().length; i++) {
								String help = tabset.getTabs()[i].getTitle();
								if (help.equals("Monitoring")) {
									((MonitoringTab) tabset.getTabs()[i])
											.getChart().setVisibility(
													Visibility.HIDDEN);

									tabset.selectTab(tabset.getTabs()[i]);
								}
							}

							SlaService
									.getMeasurementList(
											record.getAttribute("Template ID"),
											new AsyncCallback<ArrayList<Measurement>>() {
												public void onFailure(
														Throwable caught) {
													// Show the RPC error
													// message to the
													// user

												}

												@Override
												public void onSuccess(
														ArrayList<Measurement> result) {
													MonitoringTab helperTab = null;

													for (int i = 0; i < tabset
															.getTabs().length; i++) {
														String help = tabset
																.getTabs()[i]
																.getTitle();
														if (help
																.equals("Monitoring")) {
															helperTab = (MonitoringTab) tabset
																	.getTabs()[i];
															helperTab
																	.updateMeasurement(result);
														}
													}
												}

											});
						}
					});
					button.setHeight(18);
					button.setWidth(65);
					button.setPadding(5);
					button.setTitle("monitor");

					return button;
				} else
					return null;
			}
		};
		listGrid.setSelectionType(SelectionStyle.SINGLE);
		listGrid.setSelectionAppearance(SelectionAppearance.ROW_STYLE);
		listGrid.setShowRecordComponents(true);
		listGrid.setShowRecordComponentsByCell(true);

		listGrid.setWidth100();
		listGrid.setHeight(200);
		listGrid.setDataSource(AgreementDS.getTemplate());

		listGrid.setAutoFetchData(true);

		listGrid.addRecordClickHandler(new RecordClickHandler() {

			public void onRecordClick(RecordClickEvent event) {
				updateAgreements();

				if (AgreementDS.getTemplate().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					tv.fetchData(crit);
					tv.setVisible(true);

				}

				if (AgreementDS.getServiceProvider().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					sprovV.fetchData(crit);
					sprovV.setVisible(true);

				}

				if (AgreementDS.getServiceDescription().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					sdesV.fetchData(crit);
					sdesV.setVisible(true);

				}

				if (AgreementDS.getServiceReference().getFieldNames(true).length > 0) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					sRefV.fetchData(crit);
					sRefV.setVisible(true);

				}
				if (AgreementDS.getAgreementState().getFieldNames(true).length > 0) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					agreementState.fetchData(crit);
					agreementState.setVisible(true);

				}
				if (AgreementDS.getProperties().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					properties.fetchData(crit);
					properties.setVisible(true);
				}
				if (AgreementDS.getStates().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					states.fetchData(crit);
					states.setVisible(true);
				}

				if (AgreementDS.getServiceConsumer().getFieldNames(true).length > 1) {
					Criteria crit = new Criteria("Template ID", listGrid
							.getSelectedRecord().getAttribute("Template ID"));
					sConsumer.fetchData(crit);
					sConsumer.setVisible(true);

				}
				String id = listGrid.getSelectedRecord().getAttribute(
						"Template ID");
				// for (final DetailViewer detailViewer : dvl) {
				// detailViewer.setVisible(true);
				// if (detailViewer.getDataSource().getFieldNames(true).length >
				// 0) {
				// Criteria crit = new Criteria("Template ID", id);
				// detailViewer.fetchData(crit, new DSCallback() {
				//
				// @Override
				// public void execute(DSResponse response,
				// Object rawData, DSRequest request) {
				// if (response.getData().length == 0)
				// detailViewer.setVisible(false);
				//
				// }
				// });
				// detailViewer.setShowEmptyMessage(false);
				//
				// }
				//
				// }

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
		layout.addMember(agreementState);
		layout.addMember(states);

		/**
		 * Definition of the listgrid fields
		 */

		ListGridField providerField = new ListGridField("Provider Name");
		ListGridField templateIDField = new ListGridField("Template ID");
		ListGridField serviceTitleField = new ListGridField("Service Title");
		ListGridField referenceField = new ListGridField("Service Reference");
		ListGridField buttonField = new ListGridField("buttonField", "Monitor",
				65);

		listGrid.setFields(providerField, templateIDField, serviceTitleField,
				referenceField, buttonField);

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
					String help = "<html><head><STYLE TYPE=\"text/css\">"
							+ "TABLE {width:100%; border-style: solid; border-spacing: 10px; border: 1px}TD{font-family:Arial,Verdana,sans-serif; font-size:11px; background-color:white;}</STYLE></head><body>";
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
					String style = "style='font-family:Arial,Verdana,sans-serif; font-size:11px; border: 1px; background-color:white;";

					if (state == "") {
						help = "<table><tbody>";
						if (!val.equals("")) {
							help += "<tr><td " + style + "'>" + val
									+ "</td></tr>";
						}
						if (!type.equals("")) {
							help += "<tr><td " + style + "'>" + type
									+ "</td></tr>";
						}
						if (!abs.equals("")) {
							help += "<tr><td " + style + "'>" + abs
									+ "</td></tr>";
						}
						help += "</tbody></table>";
					} else {

						color = "black";
						if (state.equals("Observed")
								|| state.equals("Completed")
								|| state.equals("Ready"))
							color = "green";
						else if (state.equals("NotReady")
								|| state.equals("NotDetermined"))
							color = "red";

						help = "<table><tbody><tr><td " + style + "color:"
								+ color + ";'>" + state + "</td>";
						if (!val.equals("")) {
							help += "<tr><td " + style + "'>" + val
									+ "</td></tr>";
						}
						if (!type.equals("")) {
							help += "<tr><td " + style + "'>" + type
									+ "</td></tr>";
						}
						if (!abs.equals("")) {
							help += "<tr><td " + style + "'>" + abs
									+ "</td></tr>";
						}

						help += "</tbody></table>";
					}
					return help + "</body></html>";

				}

			};
			dField.setDetailFormatter(format);
			fields.add(dField);
		}
		dv.setFields(fields.toArray(new DetailViewerField[fields.size()]));
	}

	public void updateAgreements() {

		SlaService.getAgreementList(new AsyncCallback<ArrayList<Agreement>>() {
			public void onFailure(Throwable caught) {

				SC.say("Error", "Service is not available");
			}

			@Override
			public void onSuccess(ArrayList<Agreement> result) {

				dataSource.addAgreementsToDataSource(result);

			}

		});
	}

	public ListGrid getListGrid() {
		return listGrid;
	}

}
