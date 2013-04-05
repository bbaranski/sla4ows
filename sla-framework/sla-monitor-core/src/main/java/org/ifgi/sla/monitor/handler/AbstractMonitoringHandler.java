package org.ifgi.sla.monitor.handler;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ifgi.namespaces.wsag.ogc.PropertyType;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesType;

public abstract class AbstractMonitoringHandler
{
	protected static Logger LOGGER = Logger.getLogger(AbstractMonitoringHandler.class);

	/**
	 * @param pAgreement
	 * @return
	 */
	abstract public boolean isResponsible(AgreementDocument pAgreement);

	/**
	 * @param pAgreement
	 * @param pServiceDescription
	 */
	abstract public void monitor(AgreementDocument pAgreement, ServicePropertiesDocument pServiceProperties);

//	/**
//	 * @param pAgreement
//	 * @param pServiceDescription
//	 */
//	abstract public void merge(AgreementDocument pAgreement, ServicePropertiesDocument pServiceProperties);

	/**
	 * @param pServiceProperties
	 * @param pProperty
	 * @param pValue
	 */
	protected void copyProperty(ServicePropertiesDocument pServiceProperties, PropertyType pProperty, String pValue)
	{
		PropertyType newProperty = pServiceProperties.getServiceProperties().addNewProperty();

		newProperty.setName(pProperty.getName());
		newProperty.setTitle(pProperty.getTitle());
		newProperty.setType(pProperty.getType());
		newProperty.setValue(pValue);
	}

	protected PropertyType getProperty(ServicePropertiesType pServiceProperties, String pType)
	{
		for (PropertyType property : pServiceProperties.getPropertyArray())
		{
			if (property.getType().equalsIgnoreCase(pType))
			{
				return property;
			}
		}
		LOGGER.error("Found no service property with type '" + pType + "'.");
		return null;
	}
}
