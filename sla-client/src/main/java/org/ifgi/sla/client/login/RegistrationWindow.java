package org.ifgi.sla.client.login;

import org.ifgi.sla.shared.FieldVerifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.layout.HLayout;


/**
 * The RegistrationWindow allowes the registration to the Sla-Client. After the registration the user must be confirmed by
 * an admin.
 * @author Kristof Lange
 * @version 1.0
 */

public class RegistrationWindow extends Window {

	private final LoginServiceAsync LoginService = GWT
			.create(LoginService.class);
	final TextItem uName;
	final PasswordItem password1;
	final PasswordItem password2;
	final TextItem name;
	final TextItem surName;
	final TextItem email;

	private static RegistrationWindow instance = null;

	public static RegistrationWindow getInstance() {
		if (instance == null) {

			instance = new RegistrationWindow();

		}
		return instance;
	}




	private RegistrationWindow() {
		super();
		
		final DynamicForm form = new DynamicForm();
		form.setWidth("100%");
		
		uName = new TextItem();
		uName.setName("Username");
		uName.setTitle("Username");
		uName.setWidth("100%");
		
		 password1 = new PasswordItem();
		password1.setName("Password1");
		password1.setTitle("Enter Password");
		password1.setWidth("100%");
		 password2 = new PasswordItem();
		password2.setName("Password2");
		password2.setTitle("Confirm Password");
		password2.setWidth("100%");
		 name = new TextItem();
		name.setName("Name");
		name.setTitle("Name");
		name.setWidth("100%");
		
		surName = new TextItem();
		surName.setName("Surname");
		surName.setTitle("Surname");
		surName.setWidth("100%");
		email = new TextItem();
		email.setName("Email");
		email.setTitle("Email");
		email.setWidth("100%");
		
		final IButton buttonItem = new IButton();
		final IButton cancelItem = new IButton();
		cancelItem.setTitle("Cancel");
		cancelItem.setWidth("100%");
		cancelItem.setAlign(Alignment.CENTER);
		cancelItem.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				RegistrationWindow.getInstance().hide();
			}
		});
		buttonItem.setTitle("Register");
		buttonItem.setWidth("100%");
		buttonItem.setAlign(Alignment.CENTER);
		buttonItem.addClickHandler(new MyHandler());
		
		form.setFields(uName,password1,password2,name, surName, email);

		HLayout layout=new HLayout();
		layout.addMember(buttonItem);
		layout.addMember(cancelItem);
		layout.setAlign(Alignment.CENTER);
		setWidth(360);
		setHeight(250);
		setTitle("Contact Details");
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		addCloseClickHandler(new CloseClickHandler() {

			@Override
			public void onCloseClick(CloseClientEvent event) {

				instance.hide();

			}
		});

		addItem(form);
		addItem(layout);
		draw();
		


	
	
	}

	

	class MyHandler implements ClickHandler, KeyUpHandler {
		/**
		 * Fired when the user clicks on the sendButton.
		 */
		public void onClick(ClickEvent event) {
			registerUser();
		}

		/**
		 * Fired when the user types in the nameField.
		 */
		public void onKeyUp(KeyUpEvent event) {
			if (event.getKeyName().equals("Enter")) {
				registerUser();
			}
		}

		/**
		 * Send the name from the nameField to the server and wait for a
		 * response.
		 */
		private void registerUser() {

			// First, we validate the input.

			String userName = (String) uName.getValue();
			String passw1 = (String) password1.getValue();
			String passw2 = (String) password2.getValue();
			String n = (String) name.getValue();
			String surame = (String) surName.getValue();
			String em = (String) email.getValue();

			if (!FieldVerifier.isValidName(userName)) {
				SC.say("Username must be at least four characters");
			} else {

				if (!FieldVerifier.isValidName(n)) {
					SC.say("Name is invalid");

				} else {
					if (!FieldVerifier.isValidName(surame)) {
						SC.say("Surname is invalid");

					} else {

						if (!FieldVerifier.isValidPassword(passw1, passw2)) {
							SC.say("Password is invalid, check the spelling");

						} else {

							LoginService.registerUser(userName, passw1, n,
									surame, em, new AsyncCallback<Boolean>() {
										@Override
										public void onFailure(Throwable caught) {
											
											
										}

										@Override
										public void onSuccess(Boolean result) {
											if (result==false){
												instance.show();
												SC.say("Email is invalid, check the spelling");
											}
											
											else{
												instance.show();
												SC.say("Registered, You can login after the administrator has activated your account.");
												
												
											}
				

				};

									});

						}
					}
				}
			}
		}
	}

}
