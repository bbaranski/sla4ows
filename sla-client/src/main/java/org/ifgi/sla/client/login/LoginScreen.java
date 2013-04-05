package org.ifgi.sla.client.login;

import java.util.ArrayList;

import org.ifgi.sla.client.SlaClient;
import org.ifgi.sla.client.SlaService;
import org.ifgi.sla.client.SlaServiceAsync;
import org.ifgi.sla.client.agreements.AgreementDS;
import org.ifgi.sla.client.templates.TemplateDS;
import org.ifgi.sla.client.welcome.Welcome;
import org.ifgi.sla.shared.Agreement;
import org.ifgi.sla.shared.FieldVerifier;
import org.ifgi.sla.shared.Template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;


/**
 * LoginScreen is used for login to the Sla-Client after registration is completed.
 * @author Kristof Lange
 * @version 1.0
 */
public class LoginScreen extends VLayout {

	private SlaClient client = null;
	private final SlaServiceAsync SlaService = GWT.create(SlaService.class);
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private RegistrationWindow regWin = null;
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Login
	 * service.
	 */
	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);
	// TextBox for the User Name

	final TextItem login;
	final PasswordItem password;
	final IButton loginButton;
	final IButton register;

	public LoginScreen(SlaClient slaClient) {
		
		

		client = slaClient;
		final DynamicForm form = new DynamicForm();
		form.setWidth100();
		form.setHeight100();
		form.setAlign(Alignment.CENTER);

		login = new TextItem();
		login.setName("Login");
		login.setTitleOrientation(TitleOrientation.LEFT);
		login.setTitle("Login");
		
		form.setCellPadding(15);

		password = new PasswordItem();
		password.setTitleOrientation(TitleOrientation.LEFT);

		password.setName("Password");
		password.setTitle("Password");

		form.setFields(login, password);

		addMember(form);

		loginButton = new IButton();

		loginButton.setTitle("Login");

		loginButton.addClickHandler(new MyHandler());
		loginButton.setAlign(Alignment.CENTER);
		
		register = new IButton();
		
		register.setTitle("Register");
		register.setAlign(Alignment.CENTER);
		register.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				regWin = RegistrationWindow.getInstance();
				regWin.show();
				
			}

		

		});
		HLayout layout = new HLayout();

		layout.setAlign(Alignment.CENTER);
		layout.setPadding(10);
		layout.addMember(loginButton);
		

		layout.addMember(register);
		addMember(layout);

	}

	// Add a handler to close the DialogBox

	// Create a handler for the sendButton and nameField
	class MyHandler implements ClickHandler, KeyUpHandler {
		/**
		 * Fired when the user clicks on the sendButton.
		 */
		public void onClick(ClickEvent event) {
			register.setDisabled(true);
			loginButton.setDisabled(true);
			setCursor(Cursor.WAIT);
			sendNameToServer();
		}

		/**
		 * Fired when the user types in the nameField.
		 */
		public void onKeyUp(KeyUpEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				sendNameToServer();
			}
		}

		/**
		 * Send the name from the nameField to the server and wait for a
		 * response.
		 */
		private void sendNameToServer() {
			// First, we validate the input.

			String userName = login.getValue().toString();
			String pass = password.getValue().toString();
			if (!FieldVerifier.isValidName(userName)
					|| !FieldVerifier.isValidName(pass)) {
				SC.say("Username must be at least four characters");
				return;
			} else if (!FieldVerifier.isValidName(pass)) {
				SC.say("Password must be at least four characters");
				return;
			}

			// Then, we send the input to the server.
			login.setDisabled(true);
			password.setDisabled(true);

			loginService.loginToServer(userName, pass,
					new AsyncCallback<Boolean>() {
						public void onFailure(Throwable caught) {
							// Show the RPC error message to the user

						}

						public void onSuccess(final Boolean resul) {
							// if (resul == true) {
								SlaService
										.getAgreementList(new AsyncCallback<ArrayList<Agreement>>() {
											public void onFailure(
													Throwable caught) {
												// Show the RPC error message to
												// the
												// user

											}

											@Override
											public void onSuccess(
													ArrayList<Agreement> result) {

												AgreementDS.getInstance(result);

												SlaService
														.getTemplateList(new AsyncCallback<ArrayList<Template>>() {
															public void onFailure(
																	Throwable caught) {

															}

															@Override
															public void onSuccess(
																	ArrayList<Template> result) {
																setCursor(Cursor.ARROW);
																TemplateDS
																		.getInstance(result);
																RootPanel
																		.get(
																				"container")
																		.clear();

																RootPanel
																		.get(
																				"container")
																		.add(
																				new Welcome(
																						client));

															}
														});

											}
										});

//							} else {
//								login.setDisabled(false);
//								password.setDisabled(false);
//								SC
//										.say("User not registered/activated by admin or wrong password");
//							}
						}
					});

		}
	}

}
