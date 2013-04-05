package org.ifgi.sla.evaluator.handler;

import java.util.Date;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementStateDefinition;
import org.ifgi.sla.wsag.helper.TimeConstraintHelper;
import org.wsag4J.schemas.x2009.x07.wsag4JSchedulingExtensions.TimeConstraintDocument;

import com.sun.jersey.api.client.ClientResponse.Status;

public class AgreementStateHandler extends AbstractEvaluatorHandler
{
	protected static Logger LOGGER = Logger.getLogger(AgreementStateHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.evaluator.handler.AbstractEvaluatorHandler#isResponsible(org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument)
	 */
	@Override
	public boolean isResponsible(AgreementPropertiesDocument pAgreementProperties)
	{
		if (pAgreementProperties.getAgreementProperties().getAgreementState().getState() != AgreementStateDefinition.COMPLETE)
		{
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.evaluator.handler.AbstractEvaluatorHandler#evaluate(org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument)
	 */
	@Override
	public void evaluate(AgreementPropertiesDocument pAgreementProperties)
	{
		TimeConstraintDocument timeConstraint = TimeConstraintHelper.instance().get(pAgreementProperties);

		Date current = new Date(System.currentTimeMillis());

		if (current.before(timeConstraint.getTimeConstraint().getStartTime().getTime()))
		{
			LOGGER.info("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] agreement state is 'PENDING'");
			setAgreementState(pAgreementProperties, AgreementStateDefinition.PENDING);
		} else

		if (current.after(timeConstraint.getTimeConstraint().getEndTime().getTime()))
		{
			LOGGER.info("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] agreement state is 'COMPLETE'");
			setAgreementState(pAgreementProperties, AgreementStateDefinition.COMPLETE);
		} else

		if (current.after(timeConstraint.getTimeConstraint().getStartTime().getTime()) && current.before(timeConstraint.getTimeConstraint().getEndTime().getTime()))
		{
			LOGGER.info("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] agreement state is 'OBSERVED'");
			setAgreementState(pAgreementProperties, AgreementStateDefinition.OBSERVED);
		} else
		{
			LOGGER.error("[" + pAgreementProperties.getAgreementProperties().getAgreementId() + "] agreement state could not be determined.");
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}

	/**
	 * @param pAgreementProperties
	 * @param pState
	 */
	protected void setAgreementState(AgreementPropertiesDocument pAgreementProperties, AgreementStateDefinition.Enum pState)
	{
		pAgreementProperties.getAgreementProperties().getAgreementState().setState(pState);
	}
}
