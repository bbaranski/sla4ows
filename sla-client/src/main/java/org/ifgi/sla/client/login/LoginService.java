package org.ifgi.sla.client.login;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
	boolean loginToServer(String name, String password) throws IllegalArgumentException;
	boolean registerUser(String userID, String password, String name, String surname,String email ) throws IllegalArgumentException;

}
