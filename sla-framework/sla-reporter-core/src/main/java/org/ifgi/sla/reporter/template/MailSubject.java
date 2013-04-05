package org.ifgi.sla.reporter.template;

import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;

public class MailSubject
{
	public static String getDefault(AgreementPropertiesDocument pAgreementProperties)
	{
		return "SLA4OWS";
	}
}
