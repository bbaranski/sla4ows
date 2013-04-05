package org.ifgi.sla.evaluator;

import java.util.List;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementStateDefinition;
import org.ifgi.sla.manager.client.ManagerClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class EvaluatorJob implements Job
{
	protected static Logger LOGGER = Logger.getLogger(EvaluatorJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		String slaManagerUri = Configuration.instance().getConfiguration().getConfiguration().getManager().getURI();

		// EVALUATE THE STATUS OF OBSERVED AGREEMENTS
		List<AgreementDocument> observedAgreementList = ManagerClient.instance().getAgreementList(slaManagerUri, AgreementStateDefinition.OBSERVED);
		
		for (AgreementDocument agreement : observedAgreementList)
		{
			AgreementPropertiesDocument agreementProperties = ManagerClient.instance().getAgreementState(slaManagerUri, agreement.getAgreement().getAgreementId());
			agreementProperties = Evaluator.instance().evaluate(agreementProperties, false);
			ManagerClient.instance().updateAgreementState(slaManagerUri, agreement.getAgreement().getAgreementId(), agreementProperties);
		}
		
		// EVALUATE THE STATUS OF PENDING AGREEMENTS
		List<AgreementDocument> pendingAgreementList = ManagerClient.instance().getAgreementList(slaManagerUri, AgreementStateDefinition.PENDING);

		for (AgreementDocument agreement : pendingAgreementList)
		{
			AgreementPropertiesDocument agreementProperties = ManagerClient.instance().getAgreementState(slaManagerUri, agreement.getAgreement().getAgreementId());
			agreementProperties = Evaluator.instance().evaluate(agreementProperties, true);
			ManagerClient.instance().updateAgreementState(slaManagerUri, agreement.getAgreement().getAgreementId(), agreementProperties);
		}
	}
}
