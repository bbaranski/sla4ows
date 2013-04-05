package org.ifgi.sla.evaluator.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.jexl2.JexlContext;
import org.ifgi.namespaces.wsag.ogc.PropertyType;
import org.ifgi.namespaces.wsag.rest.MeasurementHistoryListDocument;
import org.ifgi.namespaces.wsag.rest.MeasurementType;
import org.ifgi.sla.evaluator.Configuration;
import org.ifgi.sla.evaluator.type.AvailabilityType;
import org.ifgi.sla.evaluator.type.InitialResponseType;
import org.ifgi.sla.evaluator.type.PixelType;
import org.ifgi.sla.evaluator.type.ResourceOperationType;
import org.ifgi.sla.evaluator.type.ResponseType;
import org.ifgi.sla.manager.client.ManagerClient;
import org.ifgi.sla.proxy.client.ProxyClient;

public class JexlContextHelper
{

	/**
	 * @param pAgreementId
	 * @param pJexlContext
	 * @param pName
	 */
	public static void createOperation(String pAgreementId, JexlContext pJexlContext, String pName)
	{
		MeasurementHistoryListDocument measurements = ManagerClient.instance().getMeasurements(Configuration.instance().getManager(), pAgreementId);
		
		MeasurementType measurement = measurements.getMeasurementHistoryList().getMeasurementArray()[measurements.getMeasurementHistoryList().getMeasurementArray().length - 1];

		for (PropertyType property : measurement.getServiceProperties().getPropertyArray())
		{
			if (property.getName().equalsIgnoreCase(pName))
			{
				String[] operations = property.getValue().split(",");
				
				ResourceOperationType operation = new ResourceOperationType();
				operation.setName(operations);

				pJexlContext.set(pName, operation);
				
			}
		}
	}
	
	/**
	 * @param pAgreementId
	 * @param pJexlContext
	 * @param pName
	 */
	public static void createResponse(String pAgreementId, JexlContext pJexlContext, String pName)
	{
		MeasurementHistoryListDocument measurements = ManagerClient.instance().getMeasurements(Configuration.instance().getManager(), pAgreementId);

		CalendarHelper calendarHelper = new CalendarHelper();

		List<Integer> day = new ArrayList<Integer>();
		List<Integer> week = new ArrayList<Integer>();
		List<Integer> month = new ArrayList<Integer>();
		List<Integer> year = new ArrayList<Integer>();

		for (MeasurementType measurement : measurements.getMeasurementHistoryList().getMeasurementArray())
		{
			Calendar mesaurementCalendar = measurement.getTimestamp();

			for (PropertyType property : measurement.getServiceProperties().getPropertyArray())
			{
				if (property.getName().equalsIgnoreCase(pName))
				{
					if (calendarHelper.isDay(mesaurementCalendar))
					{
						day.add(new Integer(property.getValue()));
					}

					if (calendarHelper.isWeek(mesaurementCalendar))
					{
						week.add(new Integer(property.getValue()));
					}

					if (calendarHelper.isMonth(mesaurementCalendar))
					{
						month.add(new Integer(property.getValue()));
					}

					if (calendarHelper.isYear(mesaurementCalendar))
					{
						year.add(new Integer(property.getValue()));
					}
				}
			}

		}

		int[] rawDay = new int[day.size()];
		int[] rawWeek = new int[week.size()];
		int[] rawMonth = new int[month.size()];
		int[] rawYear = new int[year.size()];

		for (int i = 0; i < day.size(); i++)
		{
			rawDay[i] = day.get(i).intValue();
		}

		for (int i = 0; i < week.size(); i++)
		{
			rawWeek[i] = week.get(i).intValue();
		}

		for (int i = 0; i < month.size(); i++)
		{
			rawMonth[i] = month.get(i).intValue();
		}

		for (int i = 0; i < year.size(); i++)
		{
			rawYear[i] = year.get(i).intValue();
		}

		InitialResponseType initial = new InitialResponseType();

		initial.setDay(rawDay);
		initial.setWeek(rawWeek);
		initial.setMonth(rawMonth);
		initial.setYear(rawYear);

		ResponseType response = new ResponseType();
		response.setInitial(initial);

		pJexlContext.set(pName, response);
	}

	/**
	 * @param pAgreementId
	 * @param pJexlContext
	 * @param pName
	 */
	public static void createAvailability(String pAgreementId, JexlContext pJexlContext, String pName)
	{
		MeasurementHistoryListDocument measurements = ManagerClient.instance().getMeasurements(Configuration.instance().getManager(), pAgreementId);

		CalendarHelper calendarHelper = new CalendarHelper();

		List<Boolean> day = new ArrayList<Boolean>();
		List<Boolean> week = new ArrayList<Boolean>();
		List<Boolean> month = new ArrayList<Boolean>();
		List<Boolean> year = new ArrayList<Boolean>();

		double availabilityDayValue = 0;
		double availabilityDayCount = 0;

		double availabilityWeekValue = 0;
		double availabilityWeekCount = 0;

		double availabilityMonthValue = 0;
		double availabilityMonthCount = 0;

		double availabilityYearValue = 0;
		double availabilityYearCount = 0;

		for (MeasurementType measurement : measurements.getMeasurementHistoryList().getMeasurementArray())
		{
			Calendar mesaurementCalendar = measurement.getTimestamp();

			for (PropertyType property : measurement.getServiceProperties().getPropertyArray())
			{
				if (property.getName().equalsIgnoreCase(pName))
				{
					if (calendarHelper.isDay(mesaurementCalendar))
					{
						day.add(new Boolean(property.getValue()));
						availabilityDayCount += 1;
						if (Boolean.parseBoolean(property.getValue()))
						{
							availabilityDayValue += 1;
						}
					}

					if (calendarHelper.isWeek(mesaurementCalendar))
					{
						week.add(new Boolean(property.getValue()));
						availabilityWeekCount += 1;
						if (Boolean.parseBoolean(property.getValue()))
						{
							availabilityWeekValue += 1;
						}
					}

					if (calendarHelper.isMonth(mesaurementCalendar))
					{
						month.add(new Boolean(property.getValue()));
						availabilityMonthCount += 1;
						if (Boolean.parseBoolean(property.getValue()))
						{
							availabilityMonthValue += 1;
						}
					}

					if (calendarHelper.isYear(mesaurementCalendar))
					{
						year.add(new Boolean(property.getValue()));
						availabilityYearCount += 1;
						if (Boolean.parseBoolean(property.getValue()))
						{
							availabilityYearValue += 1;
						}
					}
				}
			}

		}

		double availabilityDayResult = availabilityDayValue / availabilityDayCount;
		double availabilityWeekResult = availabilityWeekValue / availabilityWeekCount;
		double availabilityMonthResult = availabilityMonthValue / availabilityMonthCount;
		double availabilityYearResult = availabilityYearValue / availabilityYearCount;

		AvailabilityType availability = new AvailabilityType();

		availability.setDay(availabilityDayResult);
		availability.setWeek(availabilityWeekResult);
		availability.setMonth(availabilityMonthResult);
		availability.setYear(availabilityYearResult);

		pJexlContext.set(pName, availability);
	}

	/**
	 * @param pAgreementId
	 * @param pJexlContext
	 * @param pName
	 */
	public static void createPixel(String pService, JexlContext pJexlContext, String pName, String pRequest)
	{
		String valueDay = ProxyClient.instance().get(pService + pRequest + "/day");
		String valueMonth = ProxyClient.instance().get(pService + pRequest + "/week");
		String valueWeek = ProxyClient.instance().get(pService + pRequest + "/month");
		String valueYear = ProxyClient.instance().get(pService + pRequest + "/year");

		PixelType pixel = new PixelType();

		pixel.setDay(Long.parseLong(valueDay));
		pixel.setMonth(Long.parseLong(valueMonth));
		pixel.setWeek(Long.parseLong(valueWeek));
		pixel.setYear(Long.parseLong(valueYear));

		pJexlContext.set(pName, pixel);
	}
}
