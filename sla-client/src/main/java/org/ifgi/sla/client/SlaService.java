package org.ifgi.sla.client;

import java.util.ArrayList;

import org.ifgi.sla.shared.Agreement;
import org.ifgi.sla.shared.Measurement;
import org.ifgi.sla.shared.Pair;
import org.ifgi.sla.shared.Property;
import org.ifgi.sla.shared.Template;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("sla")
public interface SlaService extends RemoteService {
	ArrayList<Template> getTemplateList() throws IllegalArgumentException;

	ArrayList<Agreement> getAgreementList() throws IllegalArgumentException;

	ArrayList<Measurement> getMeasurementList(String id)
			throws IllegalArgumentException;

	Agreement getAgreement(String id) throws IllegalArgumentException;

	String removeAgreement() throws IllegalArgumentException;

	Template getTemplate(String id) throws IllegalArgumentException;

	ArrayList<Property> getPropertyList() throws IllegalArgumentException;

	Pair getPair() throws IllegalArgumentException;

	Boolean addAgreement(String id, String pName, String pSite,
			String pIndividualName, String pPositionName, String pVoice,
			String pFacsimile, String pUrl, String pDeliveryPoint,
			String pCity, String pPostalCode, String pCountry,
			String pMailAddress, String start, String end)
			throws IllegalArgumentException;

	String getManagerUri()throws IllegalArgumentException;

	
	Boolean setManagerUri(String managerUri)throws IllegalArgumentException;
}
