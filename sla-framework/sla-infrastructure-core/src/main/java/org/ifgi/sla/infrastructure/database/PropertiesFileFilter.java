package org.ifgi.sla.infrastructure.database;

import java.io.File;
import java.io.FilenameFilter;

public class PropertiesFileFilter implements FilenameFilter
{
	/**
	 * @param f
	 * @param s
	 * @return
	 */
	public boolean accept(File f, String s)
	{
		return s.toLowerCase().endsWith(".properties");
	}
}
