/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * SQL properties
 *
 * @author Bernard Bou
 */
public class SqlProperties
{
	/**
	 * Load properties
	 *
	 * @param url properties url
	 * @return properties
	 */
	static public Properties load(final URL url)
	{
		try
		{
			final Properties properties = new Properties();
			final InputStream inputStream = url.openStream();
			properties.load(inputStream);
			return properties;
		}
		catch (final IOException e)
		{
			System.err.println("Sql: Cannot load <" + url + ">");
			return null;
		}
	}

	/**
	 * Save properties
	 */
	static void save(final Properties properties, final String propertyFile)
	{
		try
		{
			properties.store(new FileOutputStream(propertyFile), "TREEBOLIC-SQL");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Make default property
	 *
	 * @param properties properties
	 * @return string for properties
	 */
	static public String toString(final Properties properties)
	{
		final StringBuilder buffer = new StringBuilder();
		for (final Enumeration<?> it = properties.propertyNames(); it.hasMoreElements(); )
		{
			final String name = (String) it.nextElement();
			final String value = properties.getProperty(name);
			buffer.append(name);
			buffer.append("=");
			buffer.append(value);
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
