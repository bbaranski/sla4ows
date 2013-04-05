package org.ifgi.sla.client.welcome;

/* 
  Smart GWT (GWT for SmartClient) 
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

import java.util.Iterator;
import java.util.Map;

import org.ifgi.sla.client.SlaClient;
import org.ifgi.sla.client.agreements.AgreementGridTab;
import org.ifgi.sla.client.config.ConfigurationTab;
import org.ifgi.sla.client.monitoring.MonitoringTab;
import org.ifgi.sla.client.templates.TemplatesGridTab;

import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.SearchForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

public class Welcome extends VLayout {

	private SlaClient client = null;
	private SearchForm form = null;
	private ButtonItem button = null;

	public Welcome(SlaClient c) {
		client = c;
		form = new SearchForm();
		button = new ButtonItem();
		init();

	}

	public void init() {

		final TabSet tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setWidth(800);
		tabSet.setHeight(800);

		final TemplatesGridTab templates = new TemplatesGridTab("Templates");

		final AgreementGridTab agreements = new AgreementGridTab("Agreements",
				tabSet);

		MonitoringTab monitoring = new MonitoringTab("Monitoring");
		ConfigurationTab configuration = new ConfigurationTab("Configuration");
		tabSet.addTabSelectedHandler(new TabSelectedHandler() {

			@Override
			public void onTabSelected(TabSelectedEvent event) {

				if (event.getTab().getTitle().equals("Templates")) {
					form.enable();
					TemplatesGridTab help = (TemplatesGridTab) event.getTab();
					help.updateTemplates();
				} else {
					if (event.getTab().getTitle().equals("Agreements")) {
						form.enable();
						AgreementGridTab help = (AgreementGridTab) event
								.getTab();
						help.updateAgreements();
					} else
						form.disable();
				}

			}
		});

		tabSet.addTab(templates);
		tabSet.addTab(agreements);
		tabSet.addTab(monitoring);
		tabSet.addTab(configuration);
		form.setTop(50);
		form.setNumCols(3);
		TextItem query = new TextItem();
		query.setName("query");
		query.setTitle("Query");

		button = new ButtonItem();
		button.setTitle("Search");
		button.setStartRow(false);
		final FilterBuilder filterTemplates = new FilterBuilder();
		filterTemplates.setDataSource(templates.getListGrid().getDataSource());

		final FilterBuilder filterAgreements = new FilterBuilder();
		filterAgreements
				.setDataSource(agreements.getListGrid().getDataSource());

		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Criteria crit = form.getValuesAsCriteria();
				FilterBuilder tempFilter;
				DataSource temp;
				ListGrid tempGrid;
				int length;
				if (tabSet.getSelectedTab().getTitle().equals("Templates")) {
					tempFilter = filterTemplates;
					tempGrid = templates.getListGrid();
					temp = templates.getListGrid().getDataSource();
				} else {
					tempFilter = filterAgreements;
					tempGrid = agreements.getListGrid();
					temp = agreements.getListGrid().getDataSource();
				}

				length = temp.getFields().length;
				AdvancedCriteria[] criterias = new AdvancedCriteria[length];
				Iterator iter = crit.getValues().entrySet().iterator();
				while (iter.hasNext()) {

					Map.Entry entry = (Map.Entry) iter.next();
					entry.getValue();

					for (int i = 0; i < length; i++) {

						String title = temp.getFields()[i].getTitle();

						criterias[i] = new AdvancedCriteria(title,
								OperatorId.ICONTAINS, (String) entry.getValue());

					}
					AdvancedCriteria criteria = new AdvancedCriteria(
							OperatorId.OR, criterias);
					tempFilter.setCriteria(criteria);
				}
				tempGrid.fetchData(tempFilter.getCriteria());

			}

		});

		HLayout hlayout = new HLayout();
		hlayout.setAlign(Alignment.RIGHT);
		addMember(hlayout);
		form.setItems(query, button);
		form.setPadding(10);
		addMember(form);
		addMember(tabSet);
		draw();
	}

}
