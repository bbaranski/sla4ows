package org.ifgi.sla.infrastructure.sla4dgrid;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.groovy.control.CompilationFailedException;
import org.ifgi.sla.infrastructure.AbstractInfrastructureConnector;
import org.junit.Test;


public class InfrastructureConnectorTests {
	
	@Test
	public void testGetImageNames() {		
		AbstractInfrastructureConnector infraConnector = createConnector();

		assertTrue(infraConnector.getImageNames() != null);
    }

	@Test
	public void testGetParameters() {
		AbstractInfrastructureConnector infraConnector = createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		
		assertTrue(infraConnector.getParameters(imageNames.get(0)) != null);
	}

	@Test
	public void testCreateVm() {
		AbstractInfrastructureConnector infraConnector = createConnector();
		List<String> imageNames = infraConnector.getImageNames();

		assertTrue(infraConnector.createVm(imageNames.get(0)) != null);
	}

	@Test
	public void testStartVm() {
		AbstractInfrastructureConnector infraConnector = createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		String vmId = infraConnector.createVm(imageNames.get(0));

		infraConnector.start(vmId, null);
	}

	@Test
	public void testStopVm() {
		AbstractInfrastructureConnector infraConnector = createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		String vmId = infraConnector.createVm(imageNames.get(0));

		infraConnector.stop(vmId);
	}

	@Test
	public void testScheduleVm() {
		AbstractInfrastructureConnector infraConnector = createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		assertTrue(infraConnector.schedule(imageNames.get(0), null, new Date(), new Date()) != null);
	}

	@Test
	public void testGetFalseState() {
		AbstractInfrastructureConnector infraConnector = createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		String vmId = infraConnector.createVm(imageNames.get(0));

		assertTrue(!infraConnector.getState(vmId));
	}

	@Test
	public void testGetTrueState() {
		AbstractInfrastructureConnector infraConnector = createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		String vmId = infraConnector.createVm(imageNames.get(0));

		infraConnector.start(vmId, null);

		assertTrue(infraConnector.getState(vmId));
	}

	@Test
	public void testGetAuthority() {
		AbstractInfrastructureConnector infraConnector = createConnector();
		List<String> imageNames = infraConnector.getImageNames();
		String vmId = infraConnector.createVm(imageNames.get(0));

		String ip = infraConnector.getAuthority(vmId);

		// match an IP
		Pattern pattern = Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3}");
        Matcher matcher = pattern.matcher(ip);
        assertTrue(matcher.matches());
		//assertTrue(ip ==~ /(\d{1,3}\.){3}\d{1,3}/);
	}
		
	private AbstractInfrastructureConnector createConnector() {
		GroovyClassLoader gcl = new GroovyClassLoader();
		Class clazz;
		try {
//			clazz = gcl.parseClass(InfrastructureConnectorTests.class.getResource("InfrastructureConnector.groovy").getPath());
			clazz = gcl.parseClass(new File(getClass().getResource("InfrastructureConnector.groovy").getPath()));
			Object aScript = clazz.newInstance();
			AbstractInfrastructureConnector infraConnector = (AbstractInfrastructureConnector) aScript;
			Map params = new LinkedHashMap();
//			params.put("serverLocation", "http://localhost:8088");
			params.put("serverLocation", "http://134.76.9.84");
			infraConnector.setProperties(params);
			return infraConnector;
		} catch (CompilationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fail();
		
		return null;
	}

}
