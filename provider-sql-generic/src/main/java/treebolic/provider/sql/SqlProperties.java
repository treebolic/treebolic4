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
	 * Properties in 'more' bundle
	 */
	public static final String SOURCE = "source"; 

	/**
	 * Prune
	 */
	public static final String PRUNE = "prune"; 

	/**
	 * Truncate nodes
	 */
	public static final String TRUNCATE_NODES = "truncate.nodes.where"; 

	/**
	 * Prune nodes
	 */
	public static final String PRUNE_NODES = "prune.nodes.where"; 

	/**
	 * Truncate tree edges
	 */
	public static final String TRUNCATE_TREEEDGES = "truncate.treeedges.where"; 

	/**
	 * Prune tree edges
	 */
	public static final String PRUNE_TREEEDGES = "prune.treeedges.where"; 

	/**
	 * Truncate edges
	 */
	public static final String TRUNCATE_EDGES = "truncate.edges.where"; 

	/**
	 * Prune edges
	 */
	public static final String PRUNE_EDGES = "prune.edges.where"; 

	/**
	 * Balance load
	 */
	public static final String BALANCE_LOAD = "balance"; 

	/**
	 * Load properties
	 *
	 * @param file property files
	 * @return properties
	 */
	@Nullable
	static public Properties load(@NonNull final File file)
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
	@Nullable
	static public Properties load(@NonNull final URL url)
	{
		try (@NonNull InputStream is = url.openStream())
		{
			@NonNull final Properties properties = new Properties();
			properties.load(is);
			return properties;
		}
		catch (final IOException e)
		{
			System.err.println("Sql: Cannot load <" + url + ">");
			return null;
		}
	}

	/**
	 * Load properties
	 *
	 * @param location location
	 * @return properties
	 */
	@Nullable
	static public Properties load(@NonNull final String location)
	{
		try (@NonNull InputStream is = Files.newInputStream(Paths.get(location)))
		{
			@NonNull final Properties properties = new Properties();
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
	static void save(@NonNull final Properties properties, @NonNull final String propertyFileLocation)
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
	@NonNull
	static public String toString(@NonNull final Properties properties)
	{
		@NonNull final StringBuilder sb = new StringBuilder();
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
