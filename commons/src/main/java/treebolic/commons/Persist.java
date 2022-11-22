/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Persist
 *
 * @author Bernard Bou
 */
public class Persist
{
	/**
	 * Obtain settings (handles initial state)
	 *
	 * @param persistFile
	 *        persist file
	 * @return properties
	 */
	static public Properties getSettings(final String persistFile)
	{
		final Properties settings = Persist.loadSettings(persistFile);
		if (settings.size() == 0)
		{
			String location = CodeBase.getJarLocation();
			if (location == null)
			{
				final File file = new File(System.getProperty("user.dir")); 
				try
				{
					location = file.toURI().toURL().toString();
				}
				catch (final MalformedURLException exception)
				{
					return settings;
				}
			}
			if (!location.endsWith("/")) 
			{
				location += '/';
			}
			if (location.endsWith("/lib/")) 
			{
				location = location.substring(0, location.length() - 4);
			}
			settings.setProperty("base", location + "data/test/");  
			settings.setProperty("images", location + "data/test/images/");  
			settings.setProperty("help", location + "doc/");  
			settings.setProperty("browser", "firefox");  
		}
		return settings;
	}

	/**
	 * Load properties from file
	 *
	 * @param persistFile
	 *        persist file
	 * @return properties
	 */
	static public Properties loadSettings(final String persistFile)
	{
		final Properties settings = new Properties();

		try
		{
			final String filePath = System.getProperty("user.home") + File.separator + "." + persistFile;  
			final InputStream propStream = Files.newInputStream(Paths.get(filePath));
			settings.load(propStream);
			return settings;
		}
		catch (final Exception e)
		{
			// do nothing
		}
		try
		{
			final String filePath = System.getProperty("user.dir") + File.separator + "." + persistFile;  
			final InputStream propStream = Files.newInputStream(Paths.get(filePath));
			settings.load(propStream);
			return settings;
		}
		catch (final Exception e)
		{
			// do nothing
		}
		return settings;
	}

	/**
	 * Save persist data
	 *
	 * @param persistFile
	 *        persist file
	 * @param settings
	 *        settings to persist
	 * @return true if successful
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public boolean saveSettings(final String persistFile, final Properties settings)
	{
		try
		{
			final String filePath = System.getProperty("user.home") + File.separator + "." + persistFile;  
			final OutputStream propStream = Files.newOutputStream(Paths.get(filePath));
			settings.store(propStream, "treebolic"); 
			return true;
		}
		catch (final Exception e)
		{
			System.out.println("Cannot save persist file :" + e); 
		}
		return false;
	}
}
