package org.ifgi.sla.server;

public class UserInformation {
	
	private String userID;
	private String password;
	private String name;
	private String surname;
	private String email;
	private boolean active;
	
	public UserInformation(String id, String pass, String na,
	String sur, String em, boolean a){
		userID=id;
		password=pass;
		name=na;
		surname=sur;
		email=em;
		active=a;
		
	}

	public String getUserID() {
		return userID;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getEmail() {
		return email;
	}
	
	public String toString(){
		return userID+";"+password+";"+name+";"+surname+";"+email+";"+active+System.getProperty("line.separator");
	}

	public boolean isActive() {
		return active;
	}

}
