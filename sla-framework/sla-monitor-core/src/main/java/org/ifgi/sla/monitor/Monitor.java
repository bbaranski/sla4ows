package org.ifgi.sla.monitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.sla.monitor.handler.AbstractMonitoringHandler;
import org.ifgi.sla.monitor.handler.AvailabilityMonitoringHandler;
import org.ifgi.sla.monitor.handler.PerformanceMonitoringHandler;
import org.ifgi.sla.monitor.handler.ResourceOperationMonitoringHandler;
import org.ifgi.sla.monitor.handler.UsagePixelMonitoringHandler;
import org.ifgi.sla.monitor.handler.UsageRequestMonitoringHandler;

public class Monitor
{
	protected static Logger LOGGER = Logger.getLogger(Monitor.class);

	static private Monitor _instance = null;

	protected List<AbstractMonitoringHandler> mMonitoringHandler;

	private Monitor()
	{
		mMonitoringHandler = new ArrayList<AbstractMonitoringHandler>();
		mMonitoringHandler.add(new ResourceOperationMonitoringHandler());
		mMonitoringHandler.add(new PerformanceMonitoringHandler());
		mMonitoringHandler.add(new AvailabilityMonitoringHandler());
		mMonitoringHandler.add(new UsageRequestMonitoringHandler());
		mMonitoringHandler.add(new UsagePixelMonitoringHandler());
	}

	/**
	 * @return
	 */
	static public Monitor instance()
	{
		if (null == _instance)
		{
			_instance = new Monitor();
		}
		return _instance;
	}

	/**
	 * @param pAgreement
	 * @return
	 */
	public ServicePropertiesDocument monitor(AgreementDocument pAgreement)
	{
		ServicePropertiesDocument serviceProperties = initServiceProperties();

		for (AbstractMonitoringHandler monitoringHandler : mMonitoringHandler)
		{
			if (monitoringHandler.isResponsible(pAgreement))
			{
				monitoringHandler.monitor(pAgreement, serviceProperties);
			}
		}
		return serviceProperties;
	}
	
	/**
	 * @return
	 */
	protected ServicePropertiesDocument initServiceProperties()
	{
		ServicePropertiesDocument serviceProperties = ServicePropertiesDocument.Factory.newInstance();
		serviceProperties.addNewServiceProperties();
		return serviceProperties;
	}
}
