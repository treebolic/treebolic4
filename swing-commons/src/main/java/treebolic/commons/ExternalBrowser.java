/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * External browse
 *
 * @author Bernard Bou
 */
public class ExternalBrowser
{
	/**
	 * Browse
	 *
	 * @param browser browser
	 * @param url     url string
	 */
	static public void browse(@Nullable final String browser, @Nullable final String url)
	{
		if (url == null || url.isEmpty())
		{
			return;
		}
		if (browser != null && !browser.isEmpty())
		{
			ExternalBrowser.run(browser + ' ' + url);
			return;
		}
		browse(url);
	}

	/**
	 * Help
	 *
	 * @param browser  browser
	 * @param helpUrl0 help url string
	 */
	static public void help(final String browser, @Nullable final String helpUrl0)
	{
		if (helpUrl0 == null || helpUrl0.isEmpty())
		{
			return;
		}

		// help url
		String helpUrl = helpUrl0;
		if (!helpUrl.startsWith("file:"))
		{
			@NonNull final File folder = new File(helpUrl);
			try
			{
				helpUrl = folder.toURI().toURL().toString();
			}
			catch (final MalformedURLException exception)
			{
				return;
			}
		}
		if (!helpUrl.endsWith("/"))
		{
			helpUrl += "/";
		}
		helpUrl += "index.html";

		// browse
		ExternalBrowser.browse(browser, helpUrl);
	}

	/**
	 * Browse through desktop facility
	 *
	 * @param url link url
	 */
	static public void browse(@NonNull final String url)
	{
		if (Desktop.isDesktopSupported())
		{
			try
			{
				@NonNull final File file = new File(url);
				boolean exists = file.exists();
				@NonNull final URI uri = exists ? file.toURI() : new URI(url);
				System.out.println(Messages.getString("Context.linkto") + uri);

				// we are likely to be on the popup handler
				SwingUtilities.invokeLater(() -> {
					try
					{
						Desktop.getDesktop().browse(uri);
					}
					catch (IOException e)
					{
						System.err.println(e.getMessage() + ':' + url);
					}
				});
			}
			catch (URISyntaxException e)
			{
				System.err.println(e.getMessage() + ':' + url);
			}
		}
	}

	/**
	 * Run command as separate process
	 *
	 * @param command command line
	 */
	static private void run(final String command)
	{
		if (command != null)
		{
			try
			{
				Runtime.getRuntime().exec(command);
			}
			catch (final Exception e)
			{
				System.err.println("Cannot run " + command + " " + e);
			}
		}
	}
}
