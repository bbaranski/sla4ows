package org.ifgi.sla.monitor.handler;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ifgi.namespaces.wsag.ogc.PropertyType;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.sla.wsag.helper.ServicePropertiesHelper;
import org.ifgi.sla.wsag.urn.ServicePropertiesURN;

public class UsageRequestMonitoringHandler extends AbstractMonitoringHandler
{
	protected static Logger LOGGER = Logger.getLogger(UsageRequestMonitoringHandler.class);

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
			if (property.getType().startsWith(ServicePropertiesURN.USAGE_REQUEST))
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

	}
}
