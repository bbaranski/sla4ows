package org.ifgi.sla.infrastructure.amazon;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.ifgi.sla.infrastructure.AbstractInfrastructureConnector;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;


public class AmazonInfrastructureConnector extends
		AbstractInfrastructureConnector {

	private static AmazonEC2 ec2;

	private LinkedHashMap<String, String> params = null;
	private CreateKeyPairRequest createKeyPairRequest = null;

	public AmazonInfrastructureConnector() {

	}

	@Override
	public String createVm(String pImageName) throws NullPointerException {
		RunInstancesRequest runInstancesReq = new RunInstancesRequest();
		runInstancesReq.setImageId(pImageName);
		runInstancesReq.setMaxCount(1);
		runInstancesReq.setMinCount(1);
		Collection<String> securityGroups = new ArrayList<String>();
		securityGroups.add((String) params.get("securityGroup"));
		runInstancesReq.setSecurityGroups(securityGroups);
		runInstancesReq.setKeyName(params.get("key_pair"));
		RunInstancesResult result = ec2.runInstances(runInstancesReq);
		return result.getReservation().getInstances().get(0).getInstanceId();
	}

	@Override
	public String getAuthority(String pReservationId)
			throws NullPointerException {

		DescribeInstancesResult describeInstancesResult = ec2
				.describeInstances();
		List<Reservation> reservations = describeInstancesResult
				.getReservations();
		for (Reservation reservation : reservations) {
			List<Instance> instances = reservation.getInstances();
			for (Instance instance : instances) {
				if (pReservationId.equals(instance.getInstanceId()))
					return instance.getPrivateIpAddress();
				
			}
		}
		return null;
	}

	@Override
	public List<String> getImageNames() throws NullPointerException {
		DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest();

		if (params.get("owner") != null)
			describeImagesRequest.withOwners(params.get("owner"));
		DescribeImagesResult describeImagesResult = ec2
				.describeImages(describeImagesRequest);
		List<Image> images = describeImagesResult.getImages();
		List<String> images_out = new ArrayList<String>();
		for (Image image : images) {
			images_out.add(image.getImageId());
		}
		return images_out;
	}

	@Override
	public Map<String, String> getParameters(String pImageName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getState(String pReservationId) throws NullPointerException {
		DescribeInstancesResult describeInstancesResult = ec2
				.describeInstances();
		List<Reservation> reservations = describeInstancesResult
				.getReservations();
		for (Reservation reservation : reservations) {
			List<Instance> instances = reservation.getInstances();
			for (Instance instance : instances) {
				if (pReservationId.equals(instance.getInstanceId()))
					if (instance.getState().getName().equalsIgnoreCase(
							"running")) {
						return true;
					}
			}
		}
		return false;
	}
	
	public String getStateName(String pReservationId) throws NullPointerException {
		DescribeInstancesResult describeInstancesResult = ec2
				.describeInstances();
		List<Reservation> reservations = describeInstancesResult
				.getReservations();
		for (Reservation reservation : reservations) {
			List<Instance> instances = reservation.getInstances();
			for (Instance instance : instances) {
				if (pReservationId.equals(instance.getInstanceId()))
					return instance.getState().getName();
					}
			
		}
		return null;
	}

	@Override
	public String schedule(String pImageName, Map<String, String> pParameter,
			Date pStart, Date pEnd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperties(Map params) throws NullPointerException {
		AWSCredentials credentials;
		new StringReader("sdf");
		InputStream is= new InputStream() {
			
			@Override
			public int read() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		try {
			
			credentials = new PropertiesCredentials(
					AmazonInfrastructureConnector.class
							.getResourceAsStream("AwsCredentials.properties"));
			ec2 = new AmazonEC2Client(credentials);
			ec2.setEndpoint((String) params.get("serverLocation"));

			this.params = (LinkedHashMap<String, String>) params;

			// if (createKeyPairRequest == null) {
			// CreateKeyPairRequest createKeyPairRequest = new
			// CreateKeyPairRequest();
			// createKeyPairRequest.setKeyName((String)params.get("key_pair"));
			// ec2.createKeyPair(createKeyPairRequest);
			// }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void start(String pImageId, Map<String, String> pParameter)
			throws NullPointerException {
		StartInstancesRequest startInstancesReq = new StartInstancesRequest();
		startInstancesReq.withInstanceIds(pImageId);
		ec2.startInstances(startInstancesReq);

	}

	@Override
	public void stop(String pReservationId) throws NullPointerException {

		StopInstancesRequest stopInstancesReq = (new StopInstancesRequest())
				.withInstanceIds(pReservationId);
		ec2.stopInstances(stopInstancesReq);

	}

	public void terminateAll() throws NullPointerException {
		DescribeInstancesResult describeInstancesResult = ec2
				.describeInstances();
		List<Reservation> reservations = describeInstancesResult
				.getReservations();
		for (Reservation reservation : reservations) {
			List<Instance> instances = reservation.getInstances();
			for (Instance instance : instances) {
				TerminateInstancesRequest terminateInstancesReq = (new TerminateInstancesRequest())
						.withInstanceIds(instance.getInstanceId());
				ec2.terminateInstances(terminateInstancesReq);
			}

		}

	}
	
	public void terminate(String pReservationId) throws NullPointerException {
		DescribeInstancesResult describeInstancesResult = ec2
				.describeInstances();
		List<Reservation> reservations = describeInstancesResult
				.getReservations();
		for (Reservation reservation : reservations) {
			List<Instance> instances = reservation.getInstances();
			for (Instance instance : instances) {
				if (pReservationId.equals(instance.getInstanceId())){
				TerminateInstancesRequest terminateInstancesReq = (new TerminateInstancesRequest())
						.withInstanceIds(instance.getInstanceId());
				ec2.terminateInstances(terminateInstancesReq);}
			}

		}

	}

}
