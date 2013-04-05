package org.ifgi.sla.client.login;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>LoginService</code>.
 */
public interface LoginServiceAsync {
	void loginToServer(String name, String password,
			AsyncCallback<Boolean> callback);

	void registerUser(String userID, String password, String name,
			String surname, String email, AsyncCallback<Boolean> callback);
}
