package org.ifgi.sla.manager.transform;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.ifgi.namespaces.wsag.rest.MeasurementHistoryListDocument;
import org.ifgi.namespaces.wsag.rest.MeasurementListDocument;
import org.ifgi.namespaces.wsag.rest.impl.MeasurementHistoryListDocumentImpl;

import com.sun.jersey.spi.resource.Singleton;

@Provider
@Singleton
public class MeasurementHistoryListMessageBodyReaderXml implements MessageBodyReader<MeasurementHistoryListDocument>
{
	protected static Logger LOGGER = Logger.getLogger(MeasurementHistoryListMessageBodyReaderXml.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[],
	 * javax.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		boolean mediaTypeCompatible = mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE) || mediaType.isCompatible(MediaType.TEXT_XML_TYPE);
		boolean classTypeCompatibel = type.getName().equalsIgnoreCase(MeasurementHistoryListDocument.class.getName()) || type.getName().equalsIgnoreCase(MeasurementHistoryListDocumentImpl.class.getName());
		return (mediaTypeCompatible && classTypeCompatibel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType,
	 * javax.ws.rs.core.MultivaluedMap, java.io.InputStream)
	 */
	@Override
	public MeasurementHistoryListDocument readFrom(Class<MeasurementHistoryListDocument> arg0, Type arg1, Annotation[] arg2, MediaType arg3,
			MultivaluedMap<String, String> arg4, InputStream arg5) throws IOException, WebApplicationException
	{
		try
		{
			return MeasurementHistoryListDocument.Factory.parse(arg5);
		}
		catch (XmlException e)
		{
			LOGGER.error(e);
			throw new WebApplicationException(e);
		}
	}
}
