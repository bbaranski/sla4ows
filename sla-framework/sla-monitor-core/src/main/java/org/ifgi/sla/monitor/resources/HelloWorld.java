package org.ifgi.sla.monitor.resources;

import java.util.Random;

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
		myWait();
		return "Hello World";
	}

	@POST
	public String doPost()
	{
		myWait();
		return "Hello World";
	}
	
	protected void myWait()
	{
		Random rnd = new Random();
		int millis = rnd.nextInt(2000);
		try
		{
			Thread.currentThread().sleep(millis);
		}
		catch (InterruptedException e)
		{
		}
	}
}