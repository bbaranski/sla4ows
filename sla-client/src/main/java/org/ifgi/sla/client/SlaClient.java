package org.ifgi.sla.client;

import org.ifgi.sla.client.agreements.AgreementDS;
import org.ifgi.sla.client.login.LoginScreen;
import org.ifgi.sla.client.templates.TemplateDS;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SlaClient implements EntryPoint {

	private static SlaClient singleton;

	public static SlaClient get() {
		return singleton;
	}

	/**
	 * This is the entry point method.
	 */
	private void setLoginScreen() {
		
		/*
		 * Singleton Datasources for templates and Agreements is generated
		 */
		TemplateDS.getInstance();
		AgreementDS.getInstance();

		// Create the Login screen
		LoginScreen scrLogin = new LoginScreen(this);
		// Attach it to the root panel
		RootPanel.get("container").add(scrLogin);
	}

	public void onModuleLoad() {
		singleton = this;
		setLoginScreen();

	}
}
