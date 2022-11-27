/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.provider.sql;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	 * Properties in 'more' bundle
	 */
	public static final String SOURCE = "source"; //$NON-NLS-1$

	public static final String PRUNE = "prune"; //$NON-NLS-1$

	public static final String TRUNCATE_NODES = "truncate.nodes.where"; //$NON-NLS-1$

	public static final String TRUNCATE_TREEEDGES = "truncate.treeedges.where"; //$NON-NLS-1$

	public static final String PRUNE_NODES = "prune.nodes.where"; //$NON-NLS-1$

	public static final String PRUNE_TREEEDGES = "prune.treeedges.where"; //$NON-NLS-1$

	public static final String TRUNCATE_EDGES = "truncate.edges.where"; //$NON-NLS-1$

	public static final String PRUNE_EDGES = "prune.edges.where"; //$NON-NLS-1$

	public static final String BALANCE_LOAD = "balance"; //$NON-NLS-1$

	/**
	 * Load properties
	 *
	 * @param file property files
	 * @return properties
	 */
	static public Properties load(final File file)
	{
		try
		{
			return load(file.toURI().toURL());
		}
		catch (MalformedURLException e)
		{
			//
		}
		return null;
	}

	/**
	 * Load properties
	 *
	 * @param url url
	 * @return properties
	 */
	static public Properties load(final URL url)
	{
		try (InputStream is = url.openStream())
		{
			final Properties properties = new Properties();
			properties.load(is);
			return properties;
		}
		catch (final IOException e)
		{
			System.err.println("Sql: Cannot load <" + url.toString() + ">");
			return null;
		}
	}

	/**
	 * Load properties
	 *
	 * @param location location
	 * @return properties
	 */
	static public Properties load(final String location)
	{
		try (InputStream is = Files.newInputStream(Paths.get(location)))
		{
			final Properties properties = new Properties();
			properties.load(is);
			return properties;
		}
		catch (final IOException e)
		{
			System.err.println("Sql: Cannot load <" + location + ">");
			return null;
		}
	}

	/**
	 * Save properties
	 *
	 * @param properties           properties to save
	 * @param propertyFileLocation property file path
	 */
	static void save(final Properties properties, final String propertyFileLocation)
	{
		try (OutputStream os = Files.newOutputStream(Paths.get(propertyFileLocation)))
		{
			properties.store(os, "TREEBOLIC-SQL");
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Make default property
	 *
	 * @param properties properties to save
	 * @return string
	 */
	static public String toString(final Properties properties)
	{
		final StringBuilder sb = new StringBuilder();
		for (final Enumeration<?> names = properties.propertyNames(); names.hasMoreElements(); )
		{
			final String name = (String) names.nextElement();
			final String value = properties.getProperty(name);
			sb.append(name) //
					.append("=") //
					.append(value) //
					.append("\n");
		}
		return sb.toString();
	}
}
