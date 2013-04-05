package org.ifgi.sla.evaluator.handler;

import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;

public abstract class AbstractEvaluatorHandler
{
	abstract public boolean isResponsible(AgreementPropertiesDocument pAgreementProperties);
	
	abstract public void evaluate(AgreementPropertiesDocument pAgreementProperties);
}
