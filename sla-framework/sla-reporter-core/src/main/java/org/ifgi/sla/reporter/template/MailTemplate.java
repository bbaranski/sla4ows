package org.ifgi.sla.reporter.template;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementPropertiesDocument;

public class MailTemplate
{
	public String getDefault(AgreementPropertiesDocument pAgreementProperties) throws Exception
	{
		// INITALIZE ENGINE

		Properties p1 = new Properties();
		p1.put("resource.loader", "file");
		p1.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		p1.put("file.resource.loader.path", getTemplateLocation());

		VelocityEngine engine = new VelocityEngine();
		engine.init(p1);

		// GET TEMPLATE
		Template template = engine.getTemplate("default.txt");

		// CREATE DATA
		VelocityContext context = new VelocityContext();

		// RENDER TEMPLATE
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	protected String getTemplateLocation()
	{
		URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		String path = url.getPath().substring(0, url.getPath().indexOf("WEB-INF") - 1);

		path += File.separator + "data" + File.separator;
		path += "template" + File.separator;

		return path;
	}
}
