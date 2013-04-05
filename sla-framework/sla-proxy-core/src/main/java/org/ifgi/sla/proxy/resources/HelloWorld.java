package org.ifgi.sla.proxy.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author bastian
 * 
 */
@Path("/HelloWorld")
public class HelloWorld
{
	@GET
	public String doGet()
	{
		return "Hello World";
	}

	@POST
	public String doPost()
	{
		return "Hello World";
	}
}