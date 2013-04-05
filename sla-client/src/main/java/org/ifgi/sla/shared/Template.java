package org.ifgi.sla.shared;

import java.io.Serializable;
import java.util.ArrayList;








public class Template  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ArrayList<Pair> serviceProvider = new ArrayList<Pair>();
	ArrayList<Pair> serviceConsumer = new ArrayList<Pair>();
	ArrayList<Property> properties= new ArrayList<Property>();
	Pair agreementState=new Pair();
	ArrayList<Property> states= new ArrayList<Property>();
	ArrayList<Property> guaranteeTerms= new ArrayList<Property>();
	ArrayList<Property> buisnessValues= new ArrayList<Property>();
	ArrayList<Pair> serviceDescription = new ArrayList<Pair>();
	private Pair templateName;
	private Pair reference;
	private Pair ID;
	
	public Template(){
		
	}

	public Pair getTemplateID() {
		return ID;
	}
	public void setTemplateID(Pair ID) {
		this.ID = ID;
	
	}
	public ArrayList<Pair> getServiceDescription() {
		return serviceDescription;
	}
	public ArrayList<Property> getProperties() {
		return properties;
	}
	public ArrayList<Property> getStates() {
		return states;
	}
	public ArrayList<Pair>  getServiceProvider() {
		return serviceProvider;
	}
	public ArrayList<Pair>  getServiceConsumer() {
		return serviceConsumer;
	}
	public Pair getTemplateName() {
		return templateName;
	}
	public void setTemplateName(Pair templateName) {
		this.templateName = templateName;
	}
	public Pair getReference() {
		return reference;
	}
	public void setReference(Pair reference) {
		this.reference = reference;
	}

	public Pair getAgreementState() {
		return agreementState;
	}

	public void setAgreementState(Pair agreementState) {
		this.agreementState = agreementState;
	}

	public ArrayList<Property> getGuaranteeTerms() {
		return guaranteeTerms;
	}

	public void setGuaranteeTerms(ArrayList<Property> guaranteeTerms) {
		this.guaranteeTerms = guaranteeTerms;
	}

	public ArrayList<Property> getBuisnessValues() {
		return buisnessValues;
	}

	public void setBuisnessValues(ArrayList<Property> buisnessValues) {
		this.buisnessValues = buisnessValues;
	}

	
	
	
}
