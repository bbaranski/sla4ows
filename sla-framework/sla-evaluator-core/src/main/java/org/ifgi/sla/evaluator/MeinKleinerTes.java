package org.ifgi.sla.evaluator;

import java.beans.XMLEncoder;
import java.io.File;
import java.io.IOException;

import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.Script;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;

public class MeinKleinerTes
{

//	public static void main(String[] args) throws XmlException, IOException
//	{
//		AgreementPropertiesDocument agreement = AgreementPropertiesDocument.Factory.parse(new File("/Users/bastian/Workspace/phd/sla-framework/sla-manager-webapp/target/sla-manager-webapp-1.0-SNAPSHOT/data/state/5c58a557-1b6d-449e-9c24-5e3bc902d3e6.xml"));
////		System.out.println(PrettyPrinter.indent(agreement.xmlText()));
//		
//		String queryText = "declare namespace ws='http://schemas.ggf.org/graap/2007/03/ws-agreement';declare namespace wsag-ogc='http://www.ifgi.org/namespaces/wsag/ogc';declare namespace wsag='http://schemas.ggf.org/graap/2007/03/ws-agreement';/ws:AgreementProperties/ws:Terms/ws:All/wsag:ServiceDescriptionTerm/wsag-ogc:ServiceProperties/wsag-ogc:Property[./wsag-ogc:Type/text()='urn:ogc:def:sla:runtime:performance:response']/wsag-ogc:Value";
//		XmlCursor itemCursor = agreement.getAgreementProperties().newCursor().execQuery(queryText);
//		System.out.println(itemCursor.xmlText());
//
//		
//	}
	
	public static void main(String[] args)
	{
		String script = "fulfilled = 0; fulfilled < 10;";
		
		
		JexlEngine jexlEngine = new JexlEngine();
		Script jexlScript = jexlEngine.createScript(script);
		
		System.out.println(jexlScript.getText());
	}
}
