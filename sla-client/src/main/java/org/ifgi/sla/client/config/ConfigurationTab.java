package org.ifgi.sla.client.config;

import java.util.LinkedHashMap;

import org.ifgi.sla.client.SlaService;
import org.ifgi.sla.client.SlaServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectOtherItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

public class ConfigurationTab extends Tab {

	private final SlaServiceAsync SlaService = GWT.create(SlaService.class);
	private VLayout layout;
	private static LinkedHashMap<String, String> urls = new LinkedHashMap<String, String>();

	public ConfigurationTab(String title) {
		super(title);

		layout = new VLayout();
		this.setPane(layout);
		urls.put("localhost","http://localhost:8088/sla-manager");

		selectUrl();
	}

	private void selectUrl() {

		final DynamicForm form = new DynamicForm();
		form.setWidth(300);

		final SelectOtherItem selectOtherItem = new SelectOtherItem();
		selectOtherItem.setOtherTitle("Other..");
		selectOtherItem.setTitle("URL");
		selectOtherItem.addChangedHandler(new ChangedHandler() {
			
			@Override
			public void onChanged(ChangedEvent event) {
				try {
					
					
					SlaService.setManagerUri(selectOtherItem.getDisplayValue(),
							new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									SC.say("Error", "no service available");

								}

								@Override
								public void onSuccess(Boolean result) {
									if(result==true){
										SC.say("OK", "URL changed");
									urls.put(selectOtherItem.getDisplayValue(), selectOtherItem.getDisplayValue());
									}else SC.say("Error", "URL not valid");
								}

							});
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
		

		selectOtherItem.setValueMap(urls);
		
		form.setFields(selectOtherItem);

		form.draw();
		layout.addMember(form);

	}
}
