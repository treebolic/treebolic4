/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

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
	@Nullable
	static public Properties load(@NonNull final URL url)
	{
		try
		{
			@NonNull final Properties properties = new Properties();
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
	static void save(@NonNull final Properties properties, @NonNull final String propertyFile)
	{
		try
		{
			properties.store(Files.newOutputStream(Paths.get(propertyFile)), "TREEBOLIC-SQL");
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
	@NonNull
	static public String toString(@NonNull final Properties properties)
	{
		@NonNull final StringBuilder buffer = new StringBuilder();
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
