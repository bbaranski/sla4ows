package org.ifgi.sla.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateDefinition;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.ServiceTermStateType;
import org.ifgi.sla.evaluator.handler.AbstractEvaluatorHandler;
import org.ifgi.sla.evaluator.handler.AgreementStateHandler;
import org.ifgi.sla.evaluator.handler.GuaranteeTermStateHandler;
import org.ifgi.sla.evaluator.handler.ServiceDescriptionStateHandler;
import org.ifgi.sla.evaluator.handler.TimeConstraintStateHandler;

public class Evaluator
{
	protected static Logger LOGGER = Logger.getLogger(Evaluator.class);

	static private Evaluator _instance = null;

	protected List<AbstractEvaluatorHandler> mEvaluatorHandler;

	private Evaluator()
	{
		mEvaluatorHandler = new ArrayList<AbstractEvaluatorHandler>();

		mEvaluatorHandler.add(new AgreementStateHandler());
		mEvaluatorHandler.add(new ServiceDescriptionStateHandler());
		mEvaluatorHandler.add(new TimeConstraintStateHandler());
		mEvaluatorHandler.add(new GuaranteeTermStateHandler());
	}

	/**
	 * @return
	 */
	static public Evaluator instance()
	{
		if (null == _instance)
		{
			_instance = new Evaluator();
		}
		return _instance;
	}

	/**
	 * @param pAgreement
	 * @return
	 */
	public AgreementPropertiesDocument evaluate(AgreementPropertiesDocument pAgreementProperties, boolean pPendingAgreements)
	{
		for (AbstractEvaluatorHandler evaluatorHandler : mEvaluatorHandler)
		{
			if (pPendingAgreements && evaluatorHandler instanceof GuaranteeTermStateHandler)
			{

			}
			else
			{
				// EXECUTE EVALUATOR HANDLER
				if (evaluatorHandler.isResponsible(pAgreementProperties))
				{
					evaluatorHandler.evaluate(pAgreementProperties);
				}

				// CHECK STATUS OF AGREEMENT
				if (pAgreementProperties.getAgreementProperties().getAgreementState().getState() == AgreementStateDefinition.COMPLETE)
				{
					markAllAsComplete(pAgreementProperties);
					break;
				}
			}
		}
		return pAgreementProperties;
	}

	/**
	 * @param pAgreementProperties
	 */
	protected void markAllAsComplete(AgreementPropertiesDocument pAgreementProperties)
	{
		for (ServiceTermStateType serviceTermState : pAgreementProperties.getAgreementProperties().getServiceTermStateArray())
		{
			serviceTermState.setState(ServiceTermStateDefinition.COMPLETED);
		}
	}
}
