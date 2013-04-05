package org.ifgi.sla.proxy;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.ggf.schemas.graap.x2007.x03.wsAgreement.AgreementDocument;
import org.ifgi.namespaces.wsag.ogc.ServiceReferenceDocument;
import org.ifgi.sla.manager.client.ManagerClient;
import org.ifgi.sla.proxy.helper.HttpClientHelper;
import org.ifgi.sla.wsag.helper.ServiceReferenceHelper;

public class ServiceExecutionTest extends TestCase
{
	public static void testWorkflow() throws Exception
	{
//		String managerUri = "http://localhost:8088/sla-manager";
//		String serviceQuery = "service=wfs&version=1.1.0&request=GetCapabilities";
//
//		List<AgreementDocument> agreementList = ManagerClient.instance().getAgreementList(managerUri);
//		AgreementDocument agreement = agreementList.get(0);
//		ServiceReferenceDocument serviceReference = ServiceReferenceHelper.instance().get(agreement.getAgreement());
//
//		String serviceUri = serviceReference.getServiceReference().getURI();
//
//		HttpResponse response = HttpClientHelper.instance().doGet(serviceUri, serviceQuery);
//
//		InputStream is = response.getEntity().getContent();
//		byte[] buffer = new byte[1024];
//		int length = is.read(buffer);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		while (length > 0)
//		{
//			baos.write(buffer, 0, length);
//			length = is.read(buffer);
//		}
//
//		assertTrue(baos.toString().equalsIgnoreCase("Hello World"));
	}
}
