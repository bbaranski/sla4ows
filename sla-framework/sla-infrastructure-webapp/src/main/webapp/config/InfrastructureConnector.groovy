package org.ifgi.sla.infrastructure.sla4dgrid

import org.ifgi.sla.infrastructure.AbstractInfrastructureConnector
import groovyx.net.http.*

class InfrastructureConnector extends AbstractInfrastructureConnector {
		def serverLocation

	public List<String> getImageNames() {
		def suClient = createClient()
		def resp
		resp = suClient.get(path: "/StackUp/vm/image", contentType: ContentType.JSON)

		resp.data
	}

	public Map<String, String> getParameters(String pImageName) {
		def suClient = createClient()
		def resp = suClient.get(path: "/StackUp/vm/image/${pImageName}", contentType: ContentType.JSON)
		resp.data
	}

	public String createVm(String pImageName) {
		def suClient = createClient()
		def resp = createVm(suClient)

		resp.data.vmId
	}

	public void start(String pImageId, Map<String, String> pParameter) {
		def suClient = createClient()
		def resp = startVm(suClient, pImageId)
	}
	
	public void stop(String pReservationId) {
		def suClient = createClient()
		def resp = stopVm(suClient, pReservationId)
	}

	public String schedule(String pImageName, Map<String, String> pParameter, Date pStart, Date pEnd) {
		// TODO
		throw new Exception("Not yet implemented")
	}

	public boolean getState(String pReservationId) {
		def suClient = createClient()
		def resp = suClient.get(path: "/StackUp/vm/${pReservationId}/state", contentType: ContentType.JSON)

		resp.data.state
	}

	public String getAuthority(String pReservationId) {
		def suClient = createClient()
		def resp = suClient.get(path: "/StackUp/vm/${pReservationId}/authority", contentType: ContentType.JSON)

		resp.data.ip
	}

	private def startVm(suClient, vmId) {
		suClient.put(path: "/StackUp/vm/${vmId}/state", contentType: ContentType.JSON, body: [newState: true])
	}

	private def stopVm(suClient, vmId) {
		suClient.put(path: "/StackUp/vm/${vmId}/state", contentType: ContentType.JSON, body: [newState: false])
	}

	private def createVm(suClient) {
		def body = [imageId:1, imageParams:[]]
		suClient.post(path: "/StackUp/vm", contentType: ContentType.JSON, body: body)
	}

	private def createClient() {
		new RESTClient(this.serverLocation)
	}

	void setProperties(Map params) {
		this.serverLocation = params.serverLocation
	}
}

