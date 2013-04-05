package org.ifgi.sla.wsag.urn;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementType;
import org.ifgi.namespaces.wsag.ogc.PropertyType;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.sla.wsag.helper.ServicePropertiesHelper;

public class ServicePropertiesURN
{
	/* ##### RESOURCE ##### */
	
	public static final String RESOURCE_OPERATION = "urn:ogc:def:sla:property:resource:operation";
	
	/* ##### RUNTIME ##### */

	public static final String RUNTIME_AVAILABILITY = "urn:ogc:def:sla:property:runtime:availability";
	public static final String RUNTIME_RESPONSE = "urn:ogc:def:sla:property:runtime:response";

	/* ##### USAGE ##### */

	public static final String USAGE_REQUEST = "urn:ogc:def:sla:property:usage:request";
	public static final String USAGE_PIXEL = "urn:ogc:def:sla:property:usage:pixel";

	/* ##### INFRASTRUCTURE ##### */

	public static final String INFRASTRUCTURE_PROVIDER_NAME = "urn:ogc:def:sla:property:infrastructure:provider:name";
	public static final String INFRASTRUCTURE_VM_NAME = "urn:ogc:def:sla:property:infrastructure:vm:name";

	protected static Logger LOGGER = Logger.getLogger(ServicePropertiesURN.class);

	static private ServicePropertiesURN _instance = null;

	protected List<String> sUrnList;

	public ServicePropertiesURN()
	{
		sUrnList = new ArrayList<String>();

		sUrnList.add(RESOURCE_OPERATION);
		
		sUrnList.add(RUNTIME_AVAILABILITY);
		sUrnList.add(RUNTIME_RESPONSE);

		sUrnList.add(USAGE_REQUEST);
		sUrnList.add(USAGE_PIXEL);

		sUrnList.add(INFRASTRUCTURE_PROVIDER_NAME);
		sUrnList.add(INFRASTRUCTURE_VM_NAME);
	}

	static public ServicePropertiesURN instance()
	{
		if (_instance == null)
		{
			_instance = new ServicePropertiesURN();
		}
		return _instance;
	}

	public boolean validate(AgreementType pAgreement)
	{
		boolean result = true;

		ServicePropertiesDocument serviceProperties = ServicePropertiesHelper.instance().get(pAgreement);

		for (PropertyType property : serviceProperties.getServiceProperties().getPropertyArray())
		{
			if (!sUrnList.contains(property.getType()))
			{
				LOGGER.error("[" + pAgreement.getAgreementId() + "] property type '" + property.getType() + "' is not registered.");
				result = false;
			}
		}

		return result;
	}

	/**
	 * @param pType
	 * @return
	 */
	public boolean validate(String pType)
	{
		for (String urn : sUrnList)
		{
			if (urn.equalsIgnoreCase(pType))
			{
				return true;
			}
		}
		return false;
	}
}
