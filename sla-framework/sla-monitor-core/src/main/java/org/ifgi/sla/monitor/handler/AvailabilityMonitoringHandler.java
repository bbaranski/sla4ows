package org.ifgi.sla.monitor.handler;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ifgi.namespaces.wsag.ogc.PropertyType;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.sla.proxy.helper.HttpClientHelper;
import org.ifgi.sla.wsag.helper.ServicePropertiesHelper;
import org.ifgi.sla.wsag.urn.ServicePropertiesURN;

public class AvailabilityMonitoringHandler extends AbstractMonitoringHandler
{
	protected static Logger LOGGER = Logger.getLogger(AvailabilityMonitoringHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.monitor.handler.AbstractMonitoringHandler#isResponsible(org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument)
	 */
	@Override
	public boolean isResponsible(AgreementDocument pAgreement)
	{
		ServicePropertiesDocument serviceProperties = ServicePropertiesHelper.instance().get(pAgreement.getAgreement());
		for (PropertyType property : serviceProperties.getServiceProperties().getPropertyArray())
		{
			if (property.getType().startsWith(ServicePropertiesURN.RUNTIME_AVAILABILITY))
			{
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.monitor.handler.AbstractMonitoringHandler#monitor(org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument, org.ifgi.namespaces.wsag.ogc.ServiceDescriptionDocument)
	 */
	@Override
	public void monitor(AgreementDocument pAgreement, ServicePropertiesDocument pServiceProperties)
	{
		ServicePropertiesDocument serviceProperties = ServicePropertiesHelper.instance().get(pAgreement.getAgreement());

		for (PropertyType property : serviceProperties.getServiceProperties().getPropertyArray())
		{
			if (property.getType().startsWith(ServicePropertiesURN.RUNTIME_AVAILABILITY))
			{
				HttpResponse response = HttpClientHelper.instance().execute(pAgreement, property);
				
				boolean availability = false;				
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					availability = true;
				}
				
				LOGGER.info("[" + pAgreement.getAgreement().getAgreementId() + "] service availability is " + availability + " (" + property.getName() + ")");
				
				copyProperty(pServiceProperties, property, Boolean.toString(availability));
			}
		}
	}
}
