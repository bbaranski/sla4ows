package org.ifgi.sla.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Property implements Serializable  {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6390055458462863993L;
	
	ArrayList<Pair> propertyAttributes = new ArrayList<Pair>();
	String name="";
	
	public Property(){
		super();
		
	}
	
	public Property(String n){
		super();
		name=n;
	}



	public ArrayList<Pair> getPropertyAttributes() {
		return propertyAttributes;
	}



	public String getName() {
		return name;
	}
	


	

	
}
