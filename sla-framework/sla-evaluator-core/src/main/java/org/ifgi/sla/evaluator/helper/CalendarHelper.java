package org.ifgi.sla.evaluator.helper;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarHelper
{

	GregorianCalendar currentCalendar;
	GregorianCalendar dayCalendar;
	GregorianCalendar weekCalendar;
	GregorianCalendar monthCalendar;
	GregorianCalendar yearCalendar;
	
	/**
	 * 
	 */
	public CalendarHelper()
	{
		currentCalendar = new GregorianCalendar();
		
		dayCalendar = new GregorianCalendar(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
		
		weekCalendar = new GregorianCalendar(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));		
		weekCalendar.add(Calendar.DAY_OF_MONTH, -7);
		
		monthCalendar = new GregorianCalendar(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
		monthCalendar.add(Calendar.MONTH, -1);
		
		yearCalendar = new GregorianCalendar(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
		yearCalendar.add(Calendar.YEAR, -1);	
	}
	
	/**
	 * @param pCalendar
	 * @return
	 */
	public boolean isDay(Calendar pCalendar)
	{
		
		return pCalendar.after(dayCalendar);
	}
	
	/**
	 * @param pCalendar
	 * @return
	 */
	public boolean isWeek(Calendar pCalendar)
	{
		
		return pCalendar.after(weekCalendar);
	}
	
	/**
	 * @param pCalendar
	 * @return
	 */
	public boolean isMonth(Calendar pCalendar)
	{
		
		return pCalendar.after(monthCalendar);
	}
	
	/**
	 * @param pCalendar
	 * @return
	 */
	public boolean isYear(Calendar pCalendar)
	{
		
		return pCalendar.after(yearCalendar);
	}	
}
