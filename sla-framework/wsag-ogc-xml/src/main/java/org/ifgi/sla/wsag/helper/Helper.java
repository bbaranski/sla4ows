package org.ifgi.sla.wsag.helper;

public interface Helper<T, U>
{
	T get(U pSource) throws Exception;

	String getXML(T pTemplate) throws Exception;
}
