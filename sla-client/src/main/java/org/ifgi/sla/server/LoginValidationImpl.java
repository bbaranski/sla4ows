package org.ifgi.sla.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.ifgi.sla.client.login.LoginService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.smartgwt.client.util.SC;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LoginValidationImpl extends RemoteServiceServlet implements
		LoginService {
	ArrayList<UserInformation> userList = new ArrayList<UserInformation>();

	public LoginValidationImpl() {
		super();
	
		File users = new File("files/users.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(users));

			String s = null;
			Scanner scan = null;

			while ((s = reader.readLine()) != null) {

				scan = new Scanner(s);
				scan.useDelimiter(";");

				String user = scan.next();
				String pass = scan.next();
				String nam = scan.next();
				String surn = scan.next();
				String em = scan.next();
				String a = scan.next();
				UserInformation userInformation = new UserInformation(user,
						pass, nam, surn, em, Boolean.valueOf(a));
				userList.add(userInformation);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean loginToServer(String name, String password)
			throws IllegalArgumentException {

		boolean flag = true;
		for (UserInformation userInformation : userList) {
			if (userInformation.getUserID().equals(name)
					&& userInformation.getPassword().equals(password)
					&& userInformation.isActive()) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	@Override
	public boolean registerUser(String userID, String password, String name,
			String surname, String email) throws IllegalArgumentException {

		final Pattern rfc2822 = Pattern
				.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

		if (!rfc2822.matcher(email).matches())
			return false;
		else
			return insertUser(userID, password, name, surname, email);

	}

	private boolean insertUser(String userID, String password, String name,
			String surname, String email) {

		boolean flag = true;
		for (UserInformation userInformation : userList) {
			if (userInformation.getUserID().equals(userID)
					|| userInformation.getEmail().equals(email)) {
				flag = false;
				break;
			}
		}

		try {
			if (flag == true) {
				userList.add(new UserInformation(userID, password, name,
						surname, email, false));
				File users = new File("files/users.txt");
				FileWriter wr = new FileWriter(users);
				for (UserInformation userInformation : userList) {

					wr.write(userInformation.toString());
				}
				wr.close();
			} else
				SC.say("User already registered");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

}
