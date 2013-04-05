package org.ifgi.sla.monitor;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceDescriptionTermType;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateType;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.sla.manager.client.ManagerClient;
import org.ifgi.sla.monitor.Configuration;
import org.ifgi.sla.wsag.helper.AgreementHelper;
import org.ifgi.sla.wsag.helper.ServicePropertiesHelper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sun.jersey.api.client.ClientResponse.Status;

public class MonitorJob implements Job
{
	protected static Logger LOGGER = Logger.getLogger(MonitorJob.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		String slaManagerUri = Configuration.instance().getConfiguration().getConfiguration().getManager().getURI();

		// GET ALL ACTIVE AGREEMENTS
		List<AgreementDocument> agreementList = ManagerClient.instance().getAgreementList(slaManagerUri, AgreementStateDefinition.OBSERVED);

		for (AgreementDocument agreement : agreementList)
		{
			AgreementPropertiesDocument agreementProperties = ManagerClient.instance().getAgreementState(slaManagerUri, agreement.getAgreement().getAgreementId());

			if (agreementProperties.getAgreementProperties().getAgreementState().getState().equals(AgreementStateDefinition.OBSERVED))
			{
				// MEASURE THE KEY PERFORMANCE INDICATORS
				ServicePropertiesDocument monitoringServiceProperties = Monitor.instance().monitor(agreement);

				// STORE THE MEASUREMENTS IN A DATABASE
				monitoringServiceProperties = ManagerClient.instance().addMeasurement(slaManagerUri, agreement.getAgreement().getAgreementId(), monitoringServiceProperties);

				// UPDATE THE SERVICE STATE TERMS
				for (int i = 0; i < agreementProperties.getAgreementProperties().getServiceTermStateArray().length; i++)
				{
					ServiceTermStateType serviceTermState = agreementProperties.getAgreementProperties().getServiceTermStateArray()[i];

					ServiceDescriptionTermType serviceDescriptionTerm = AgreementHelper.instance().getServiceDescriptionTermByName(agreement.getAgreement().getTerms().getAll().getServiceDescriptionTermArray(),
							serviceTermState.getTermName());

					// UPDATE SERVICE PROPERTIES
					if (ServicePropertiesHelper.instance().isServiceProperties(serviceDescriptionTerm))
					{
//						ServiceTermStateType newServiceTermState = updateServiceTermState(agreement, serviceTermState);
						serviceTermState.setState(ServiceTermStateDefinition.READY);
						agreementProperties.getAgreementProperties().setServiceTermStateArray(i, serviceTermState);
					}
				}

				ManagerClient.instance().updateAgreementState(slaManagerUri, agreement.getAgreement().getAgreementId(), agreementProperties);
			}
		}
	}

	/**
	 * @param monitoringServiceDescription
	 * @param serviceTermState
	 * @return
	 */
//	protected ServiceTermStateType updateServiceTermState(AgreementDocument pAgreement, ServiceTermStateType pServiceTermState)
//	{
//		// UPDATE MONITORING SERVICE PROPERTIES
////		ServicePropertiesDocument monitoringServicePropertiesUpdate = Monitor.instance().merge(pAgreement);
//
//		// INSERT MONITORING SERVICE PROPERTIES
//		ServiceTermStateType newServiceTermStat = ServiceTermStateType.Factory.newInstance();
//		newServiceTermStat.setTermName(pServiceTermState.getTermName());
//		newServiceTermStat.setState(ServiceTermStateDefinition.READY);
//		try
//		{
//			Document newServiceTermStateDocument = DocumentHelper.parseText(newServiceTermStat.xmlText());
//			// Document monitoringServiceDescriptionDocument = DocumentHelper.parseText(monitoringServicePropertiesUpdate.xmlText());
//			// newServiceTermStateDocument.getRootElement().add(monitoringServiceDescriptionDocument.getRootElement());
//			newServiceTermStat = ServiceTermStateType.Factory.parse(newServiceTermStateDocument.asXML());
//		}
//		catch (Exception e)
//		{
//			LOGGER.error(e);
//			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
//		}
//		return newServiceTermStat;
//	}
}