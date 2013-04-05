package org.ifgi.sla.shared;

public class Measurement extends Property{
	
	private String agreementID=null;
	private String timeStamp=null;
	
	public Measurement(){
		super();
		
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getAgreementID() {
		return agreementID;
	}

	public void setAgreementID(String agreementID) {
		this.agreementID = agreementID;
	}
	
	

}
