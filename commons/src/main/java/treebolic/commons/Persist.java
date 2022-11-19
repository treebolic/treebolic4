/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
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
				final File file = new File(System.getProperty("user.dir")); //$NON-NLS-1$
				try
				{
					location = file.toURI().toURL().toString();
				}
				catch (final MalformedURLException exception)
				{
					return settings;
				}
			}
			if (!location.endsWith("/")) //$NON-NLS-1$
			{
				location += '/';
			}
			if (location.endsWith("/lib/")) //$NON-NLS-1$
			{
				location = location.substring(0, location.length() - 4);
			}
			settings.setProperty("base", location + "data/test/"); //$NON-NLS-1$ //$NON-NLS-2$
			settings.setProperty("images", location + "data/test/images/"); //$NON-NLS-1$ //$NON-NLS-2$
			settings.setProperty("help", location + "doc/"); //$NON-NLS-1$ //$NON-NLS-2$
			settings.setProperty("browser", "firefox"); //$NON-NLS-1$ //$NON-NLS-2$
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
			final String filePath = System.getProperty("user.home") + File.separator + "." + persistFile; //$NON-NLS-1$ //$NON-NLS-2$
			final InputStream propStream = new FileInputStream(filePath);
			settings.load(propStream);
			return settings;
		}
		catch (final Exception e)
		{
			// do nothing
		}
		try
		{
			final String filePath = System.getProperty("user.dir") + File.separator + "." + persistFile; //$NON-NLS-1$ //$NON-NLS-2$
			final InputStream propStream = new FileInputStream(filePath);
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
	static public boolean saveSettings(final String persistFile, final Properties settings)
	{
		try
		{
			final String filePath = System.getProperty("user.home") + File.separator + "." + persistFile; //$NON-NLS-1$ //$NON-NLS-2$
			final OutputStream propStream = new FileOutputStream(filePath);
			settings.store(propStream, "treebolic"); //$NON-NLS-1$
			return true;
		}
		catch (final Exception e)
		{
			System.out.println("Cannot save persist file :" + e); //$NON-NLS-1$
		}
		return false;
	}
}
