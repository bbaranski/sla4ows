package org.ifgi.sla.manager.database;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.tool.PrettyPrinter;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesDocument;
import org.ifgi.namespaces.wsag.ogc.ServicePropertiesType;
import org.ifgi.namespaces.wsag.rest.MeasurementHistoryListDocument;
import org.ifgi.namespaces.wsag.rest.MeasurementType;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author bastian
 * 
 */
public class MeasurementDatabase extends AbstractDatabase<MeasurementHistoryListDocument>
{
	protected static Logger LOGGER = Logger.getLogger(MeasurementDatabase.class);

//	protected static int LOGGING_HISTORY = (60 * 24 * 14);
	protected static int LOGGING_HISTORY = (60 * 24);

	/**
	 * 
	 */
	public MeasurementDatabase()
	{
		super(DatabaseType.MEASUREMENT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.AbstractDatabase#get()
	 */
	@Override
	public List<MeasurementHistoryListDocument> get()
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.AbstractDatabase#get(java.lang.String)
	 */
	@Override
	public MeasurementHistoryListDocument get(String pId)
	{
		File measurement = new File(getDatabaseLocation() + pId + ".xml");
		if (measurement.exists() == false)
		{
			return null;
		}
		try
		{
			MeasurementHistoryListDocument measurementHistoryList = MeasurementHistoryListDocument.Factory.parse(measurement);
			return measurementHistoryList;
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.AbstractDatabase#add(java.lang.Object)
	 */
	@Override
	public MeasurementHistoryListDocument add(MeasurementHistoryListDocument pObject)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/**
	 * @param pAgreementId
	 * @param pObject
	 * @return
	 */
	public MeasurementHistoryListDocument add(String pAgreementId, ServicePropertiesDocument pServicePropertiesDocument)
	{
		try
		{
			// CREATE INITIAL MEASUREMENT LIST
			MeasurementHistoryListDocument measurementHistoryListDocument = get(pAgreementId);
			if (measurementHistoryListDocument == null)
			{
				measurementHistoryListDocument = MeasurementHistoryListDocument.Factory.newInstance();
				measurementHistoryListDocument.addNewMeasurementHistoryList();
			}

			// REMOVE OLD MEASUREMENTS
			if (measurementHistoryListDocument.getMeasurementHistoryList().getMeasurementArray().length > LOGGING_HISTORY)
			{
				MeasurementType[] measurementHistory = measurementHistoryListDocument.getMeasurementHistoryList().getMeasurementArray();
				
				MeasurementType[] measurements = new MeasurementType[measurementHistory.length - 1];
				for (int i = 0; i < measurementHistory.length - 1; i++)
				{
					measurements[i] = measurementHistory[i + 1];
				}
				
				measurementHistoryListDocument.getMeasurementHistoryList().setMeasurementArray(measurements);
			}

			// INSERT NEW MEASUREMENT
			MeasurementType measurement = measurementHistoryListDocument.getMeasurementHistoryList().addNewMeasurement();
			measurement.setTimestamp(Calendar.getInstance());

			ServicePropertiesType newServiceProperties = measurement.addNewServiceProperties();
			newServiceProperties.set(pServicePropertiesDocument.getServiceProperties().copy());

			String xml = PrettyPrinter.indent(measurementHistoryListDocument.xmlText());
			saveXML(getDatabaseLocation(), getDatabaseLocation() + pAgreementId + ".xml", xml);
			return measurementHistoryListDocument;
		} catch (Exception e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.AbstractDatabase#update(java.lang.String, java.lang.Object)
	 */
	@Override
	public MeasurementHistoryListDocument update(String pId, MeasurementHistoryListDocument pObject)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.AbstractDatabase#remove(java.lang.String)
	 */
	@Override
	public MeasurementHistoryListDocument remove(String pId)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ifgi.sla.manager.database.AbstractDatabase#remove(java.lang.Object)
	 */
	@Override
	public MeasurementHistoryListDocument remove(MeasurementHistoryListDocument pObject)
	{
		throw new WebApplicationException(Status.NOT_IMPLEMENTED.getStatusCode());
	}
}
