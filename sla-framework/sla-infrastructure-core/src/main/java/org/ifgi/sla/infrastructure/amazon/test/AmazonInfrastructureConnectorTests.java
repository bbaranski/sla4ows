package org.ifgi.sla.infrastructure.amazon.test;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.codehaus.groovy.control.CompilationFailedException;
import org.ifgi.sla.infrastructure.AbstractInfrastructureConnector;
import org.ifgi.sla.infrastructure.amazon.AmazonInfrastructureConnector;
import org.junit.Test;

public class AmazonInfrastructureConnectorTests extends TestCase{

	@Test
	public void testGetImageNames() {
		AbstractInfrastructureConnector infraConnector = createConnector();

		assertTrue(infraConnector.getImageNames() != null);
	}

//	@Test
//	public void testGetParameters() {
//		AbstractInfrastructureConnector infraConnector = createConnector();
//		List<String> imageNames = infraConnector.getImageNames();
//
//		assertTrue(infraConnector.getParameters(imageNames.get(0)) != null);
//	}

	@Test
	public void testCreateVm() {
		AbstractInfrastructureConnector infraConnector = createConnector();
		List<String> imageNames = infraConnector.getImageNames();

		assertTrue(infraConnector.createVm("ami-59f9c62d") != null);
	}

	@Test
	public void testStartVm() {
		AmazonInfrastructureConnector infraConnector = (AmazonInfrastructureConnector) createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		String vmId = infraConnector.createVm("ami-59f9c62d");
		while(!"running".equalsIgnoreCase(infraConnector.getStateName(vmId))){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		infraConnector.stop(vmId);
		while(!"stopped".equalsIgnoreCase(infraConnector.getStateName(vmId))){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		infraConnector.start(vmId, null);
	}

	@Test
	public void testStopVm() {
		AmazonInfrastructureConnector infraConnector = (AmazonInfrastructureConnector) createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		String vmId = infraConnector.createVm("ami-59f9c62d");
		while(!"running".equalsIgnoreCase(infraConnector.getStateName(vmId))){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		infraConnector.stop(vmId);
	}

//	@Test
//	public void testScheduleVm() {
//		AbstractInfrastructureConnector infraConnector = createConnector();
//		List<String> imageNames = infraConnector.getImageNames();
//		assertTrue(infraConnector.schedule(imageNames.get(0), null, new Date(),
//				new Date()) != null);
//	}

	@Test
	public void testGetFalseState() {
		AmazonInfrastructureConnector infraConnector = (AmazonInfrastructureConnector) createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		String vmId = infraConnector.createVm("ami-59f9c62d");
		while(!"running".equalsIgnoreCase(infraConnector.getStateName(vmId))){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		infraConnector.stop(vmId);
		while("running".equalsIgnoreCase(infraConnector.getStateName(vmId))){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertTrue(!infraConnector.getState(vmId));
	}

	@Test
	public void testGetTrueState() {
		AmazonInfrastructureConnector infraConnector = (AmazonInfrastructureConnector) createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		String vmId = infraConnector.createVm("ami-59f9c62d");

	while(!"running".equalsIgnoreCase(infraConnector.getStateName(vmId))){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertTrue(infraConnector.getState(vmId));
	}

	@Test
	public void testGetAuthority() {
		AmazonInfrastructureConnector infraConnector = (AmazonInfrastructureConnector) createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		String vmId = infraConnector.createVm("ami-59f9c62d");
		while(!"running".equalsIgnoreCase(infraConnector.getStateName(vmId))){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String ip = infraConnector.getAuthority(vmId);
		String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		Pattern pattern = Pattern.compile( "^(?:" + _255 + "\\.){3}" + _255 + "$");
		// match an IP
	//	Pattern pattern = Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3}");
		Matcher matcher = pattern.matcher(ip);
		System.out.println(ip);
		assertTrue(matcher.matches());
		// assertTrue(ip ==~ /(\d{1,3}\.){3}\d{1,3}/);
	}
	
	 protected void tearDown() {
		 AmazonInfrastructureConnector infraConnector =(AmazonInfrastructureConnector) createConnector();
		 infraConnector.terminateAll();
	  }

	private AbstractInfrastructureConnector createConnector() {
		
	
		try {
			
			AbstractInfrastructureConnector infraConnector = (AbstractInfrastructureConnector) new AmazonInfrastructureConnector();
			Map<String,String> params = new LinkedHashMap<String,String>();
			
			params.put("securityGroup", "SECURITYGROUP");
			params.put("key_pair", "KEYPAIR");
			params.put("owner", "OWNER");
			params.put("serverLocation", "https://ec2.eu-west-1.amazonaws.com");
			
			infraConnector.setProperties(params);
			return infraConnector;
		} catch (CompilationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		fail();

		return null;
	}
	

}
