package org.ifgi.sla.client;

import java.util.ArrayList;

import org.ifgi.sla.shared.Agreement;
import org.ifgi.sla.shared.Measurement;
import org.ifgi.sla.shared.Pair;
import org.ifgi.sla.shared.Property;
import org.ifgi.sla.shared.Template;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>SlaService</code>.
 */
public interface SlaServiceAsync {
	void getAgreementList(AsyncCallback<ArrayList<Agreement>> callback);

	void removeAgreement(AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getAgreement(String id, AsyncCallback<Agreement> callback)
			throws IllegalArgumentException;

	void getTemplate(String id, AsyncCallback<Template> callback)
			throws IllegalArgumentException;

	void getTemplateList(AsyncCallback<ArrayList<Template>> callback);

	void addAgreement(String id, String pName, String pSite,
			String pIndividualName, String pPositionName, String pVoice,
			String pFacsimile, String pUrl, String pDeliveryPoint,
			String pCity, String pPostalCode, String pCountry,
			String pMailAddress, String start, String end,
			AsyncCallback<Boolean> asyncCallback);

	void getPropertyList(AsyncCallback<ArrayList<Property>> callback);

	void getPair(AsyncCallback<Pair> callback);

	void getMeasurementList(String id,
			AsyncCallback<ArrayList<Measurement>> callback);

	void setManagerUri(String displayValue, AsyncCallback<Boolean> asyncCallback);
	void getManagerUri(AsyncCallback<String> callback);
}
