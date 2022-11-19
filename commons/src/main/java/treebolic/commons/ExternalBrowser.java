/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.SwingUtilities;

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
	 * @param browser
	 *        browser
	 * @param url
	 *        url string
	 */
	static public void browse(final String browser, final String url)
	{
		if (url == null || url.isEmpty())
			return;
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
	 * @param browser
	 *        browser
	 * @param helpUrl0
	 *        help url string
	 */
	static public void help(final String browser, final String helpUrl0)
	{
		if (helpUrl0 == null || helpUrl0.isEmpty())
			return;

		// help url
		String helpUrl = helpUrl0;
		if (!helpUrl.startsWith("file:")) //$NON-NLS-1$
		{
			final File folder = new File(helpUrl);
			try
			{
				helpUrl = folder.toURI().toURL().toString();
			}
			catch (final MalformedURLException exception)
			{
				return;
			}
		}
		if (!helpUrl.endsWith("/")) //$NON-NLS-1$
		{
			helpUrl += "/"; //$NON-NLS-1$
		}
		helpUrl += "index.html"; //$NON-NLS-1$

		// browse
		ExternalBrowser.browse(browser, helpUrl);
	}

	/**
	 * Browse through desktop facility
	 *
	 * @param url
	 *        link url
	 */
	static public void browse(final String url)
	{
		if (Desktop.isDesktopSupported())
		{
			try
			{
				final File file = new File(url);
				boolean exists = file.exists();
				final URI uri = exists ? file.toURI() : new URI(url);
				if (uri != null)
				{
					System.out.println(Messages.getString("Context.linkto") + uri); //$NON-NLS-1$

					// we are likely to be on the popup handler
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								Desktop.getDesktop().browse(uri);
							}
							catch (IOException e)
							{
								System.err.println(e.getMessage() + ':' + url);
							}
						}
					});
				}
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
	 * @param command
	 *        command line
	 */
	static private void run(final String command)
	{
		if (command != null && command != null)
		{
			try
			{
				Runtime.getRuntime().exec(command);
			}
			catch (final Exception e)
			{
				System.err.println("Cannot run " + command + " " + e.toString()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
}
