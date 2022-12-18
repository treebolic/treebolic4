/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.application;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.swing.*;

import treebolic.IContext;
import treebolic.Widget;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.component.Statusbar;

/**
 * Context
 *
 * @author Bernard Bou
 */
public class Context implements IContext
{
	/**
	 * Browser
	 */
	protected final MainFrame application;

	/**
	 * Source
	 */
	protected String source;

	/**
	 * Base
	 */
	private final String base;

	/**
	 * Image base
	 */
	private final String imageBase;

	/**
	 * Constructor
	 *
	 * @param application application
	 * @param source      source
	 * @param base        base
	 * @param imageBase   image base
	 */
	public Context(final MainFrame application, final String source, final String base, final String imageBase)
	{
		this.application = application;
		this.source = source;
		this.base = base;
		this.imageBase = imageBase;
		// System.out.println("source: " + source);
		// System.out.println("base: " + base);
		// System.out.println("imagebase: " + imageBase);
	}

	/**
	 * Get widget
	 *
	 * @return widget
	 */
	@Nullable
	public Widget getWidget()
	{
		return this.application == null ? null : this.application.getWidget();
	}

	@Override
	public URL getBase()
	{
		@Nullable URL defaultBase = null;
		if (this.base != null && !this.base.isEmpty())
		{
			// 1-use 'base' parameter as full-fledged
			try
			{
				return new URL(this.base);
			}
			catch (final MalformedURLException e)
			{
				// do nothing
			}

			// 1b-use 'base' parameter as full-fledged directory
			try
			{
				@NonNull final File file = new File(this.base);
				if (file.exists() && file.isDirectory())
				{
					return file.toURI().toURL();
				}
			}
			catch (final MalformedURLException e)
			{
				// do nothing
			}

			// 2-use 'base' parameter as path relative to default
			@NonNull String base = this.base;
			if (!base.endsWith("/"))
			{
				base += "/";
			}
			defaultBase = getDefaultBase();
			try
			{
				return new URL(defaultBase, base);
			}
			catch (final MalformedURLException e)
			{
				// do nothing
			}
		}

		// 3-default
		if (defaultBase == null)
		{
			defaultBase = getDefaultBase();
		}
		return defaultBase;
	}

	@Override
	public URL getImagesBase()
	{
		@Nullable URL documentUrl = null;
		@Nullable URL defaultUrl = null;
		if (this.imageBase != null && !this.imageBase.isEmpty())
		{
			// 1-use 'images' parameter as full-fledged url
			try
			{
				return new URL(this.imageBase);
			}
			catch (final Exception exception)
			{
				// do nothing
			}

			// 2-use 'images' parameter as full-fledged directory
			try
			{
				@NonNull final File file = new File(this.imageBase);
				if (file.exists() && file.isDirectory())
				{
					return file.toURI().toURL();
				}
			}
			catch (final MalformedURLException e)
			{
				// do nothing
			}

			@NonNull String imageBase = this.imageBase;
			if (!imageBase.endsWith("/"))
			{
				imageBase += "/";
			}

			// 3- use 'images' parameter as relative to document
			try
			{
				documentUrl = makeURL(this.source);
				return new URL(documentUrl, imageBase);
			}
			catch (final Exception exception)
			{
				// do nothing
			}

			// unlikely
			// 4- use 'images' parameter as relative to default
			try
			{
				defaultUrl = getDefaultImagesBase();
				return new URL(defaultUrl, imageBase);
			}
			catch (final Exception exception)
			{
				// do nothing
			}

			// don't know what to do with it
			System.err.print("Can't handle 'images' parameter: " + imageBase);
		}

		// 5- document base + "images/"
		try
		{
			if (documentUrl == null)
			{
				documentUrl = makeURL(this.source);
			}
			return new URL(documentUrl, "images/");
		}
		catch (final Exception exception)
		{
			// do nothing
		}

		// default already ends with /images
		// // 6- default + "images/"
		// try
		// {
		// if (defaultUrl == null)
		// {
		// defaultUrl = getDefaultImagesBase();
		// }
		// return new URL(defaultUrl, "images/");
		// }
		// catch (final Exception exception)
		// {
		// // do nothing
		// }

		// 7- default
		return defaultUrl;
	}

	@Override
	public Properties getParameters()
	{
		return this.application.getParameters();
	}

	@Override
	public String getStyle()
	{
		return ".content { }" + ".link {color: blue;font-size: small; }" + ".mount {color: red;}" + ".linking {color: #007D82; font-size: small; }" + ".mounting {color: #007D82; font-size: small; }" + ".searching {color: #007D82; font-size: small; }";
	}

	@Override
	public boolean linkTo(@NonNull final String linkUrl, final String linkTarget)
	{
		if (Desktop.isDesktopSupported())
		{
			try
			{
				@NonNull final File file = new File(linkUrl);
				boolean exists = file.exists();
				@NonNull final URI uri = exists ? file.toURI() : new URI(linkUrl);
				System.out.println(Messages.getString("Context.linkto") + uri);

				// we are likely to be on the popup handler
				SwingUtilities.invokeLater(() -> {
					try
					{
						Desktop.getDesktop().browse(uri);
					}
					catch (IOException e)
					{
						System.err.println(e.getMessage() + ':' + linkUrl);
					}
				});
				return true;
			}
			catch (URISyntaxException e)
			{
				System.err.println(e.getMessage() + ':' + linkUrl);
			}
		}
		return false;
	}

	@Override
	public void status(final String string)
	{
		System.out.println(Messages.getString("Context.status") + string);
	}

	@Override
	public void warn(@NonNull final String message)
	{
		@NonNull final String[] lines = message.split("\n");
		JOptionPane.showMessageDialog(null, lines, Messages.getString("Context.title"), JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public String getInput()
	{
		@Nullable final Widget widget = getWidget();
		if (widget != null)
		{
			final Statusbar statusbar = widget.getStatusbar();
			if (statusbar != null)
			{
				return statusbar.get();
			}
		}
		return null;
	}

	// U R L F A C T O R Y

	/**
	 * Make url
	 *
	 * @param source source
	 * @return url
	 * @throws MalformedURLException malformed URL exception
	 */
	@NonNull
	protected URL makeURLAlt(@NonNull final String source) throws MalformedURLException
	{
		// try to consider it well-formed full-fledged url
		try
		{
			return new URL(source);
		}
		catch (final MalformedURLException e)
		{
			// do nothing
		}

		// try to consider it file
		@NonNull final File file = new File(source);
		if (file.exists() && file.canRead())
		{
			try
			{
				return file.toURI().toURL();
			}
			catch (final MalformedURLException exception)
			{
				// do nothing
			}
		}

		// default to source relative to a base
		return new URL(getDefaultBase(), source);
	}

	/**
	 * Make url
	 *
	 * @param source source
	 * @return url
	 */
	@Nullable
	protected URL makeURL(@Nullable final String source)
	{
		if (source == null)
		{
			return null;
		}

		// try to consider it well-formed full-fledged url
		try
		{
			return new URL(source);
		}
		catch (final MalformedURLException e)
		{
			// do nothing
		}

		// default to source relative to a base
		try
		{
			return new URL(getBase(), source);
		}
		catch (final MalformedURLException e)
		{
			// do nothing
		}
		return null;
	}

	/**
	 * Get base default
	 *
	 * @return base default;
	 */
	@Nullable
	protected URL getDefaultBase()
	{
		// base parameter
		// final String base = getParameters().getProperty("base");
		// String uRLString = base != null ? base : System.getProperty("user.dir");
		String uRLString = System.getProperty("user.dir");

		// tail
		if (!uRLString.endsWith("/"))
		{
			uRLString += "/";
		}

		// make
		try
		{
			return new URL(uRLString);
		}
		catch (final MalformedURLException e)
		{
			// make from folder
			try
			{
				@NonNull final File folder = new File(uRLString);
				return folder.toURI().toURL();
			}
			catch (final MalformedURLException exception)
			{
				// do nothing
			}
		}
		return null;
	}

	/**
	 * Get images base default
	 *
	 * @return image base default;
	 */
	@Nullable
	protected URL getDefaultImagesBase()
	{
		@Nullable final URL base = getDefaultBase();
		try
		{
			return new URL(base, "images/");
		}
		catch (final MalformedURLException exception)
		{
			// do nothing
		}
		return null;
	}
}
