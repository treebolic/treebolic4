/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.browser2;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.swing.JOptionPane;

import treebolic.IContext;
import treebolic.IWidget;
import treebolic.Widget;
import treebolic.glue.component.Statusbar;

/**
 * Context
 *
 * @author Bernard Bou
 */
public class Context implements IContext
{
	/**
	 * Browser2
	 */
	protected final MainFrame browser;

	/**
	 * Connected Widget
	 */
	protected IWidget widget;

	/**
	 * Source
	 */
	protected final String source;

	/**
	 * Base
	 */
	protected final String base;

	/**
	 * Image base
	 */
	protected final String imageBase;

	/**
	 * URL scheme
	 */
	protected final String urlScheme;

	/**
	 * Constructor
	 *
	 * @param application application mainframe
	 * @param source      source
	 * @param base        base
	 * @param imageBase   image base
	 * @param urlScheme   url scheme
	 */
	public Context(final MainFrame application, final String source, final String base, final String imageBase, final String urlScheme)
	{
		this.browser = application;
		this.widget = null;
		this.source = source;
		this.base = base;
		this.imageBase = imageBase;
		this.urlScheme = urlScheme;
	}

	/**
	 * Connected Widget
	 *
	 * @param widget widget
	 */
	public void connect(final IWidget widget)
	{
		this.widget = widget;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#getBase()
	 */
	@Override
	public URL getBase()
	{
		URL defaultBase = null;
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
				final File file = new File(this.base);
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
			String base = this.base;
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

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#getImagesBase()
	 */
	@Override
	public URL getImagesBase()
	{
		URL documentUrl = null;
		URL defaultUrl = null;
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

			// 1b-use 'base' parameter as full-fledged directory
			try
			{
				final File file = new File(this.imageBase);
				if (file.exists() && file.isDirectory())
				{
					return file.toURI().toURL();
				}
			}
			catch (final MalformedURLException e)
			{
				// do nothing
			}

			String imageBase = this.imageBase;
			if (!imageBase.endsWith("/"))
			{
				imageBase += "/";
			}

			// 2- use 'images' parameter as relative to document base
			try
			{
				documentUrl = makeURL(this.source);
				return new URL(documentUrl, imageBase);
			}
			catch (final Exception exception)
			{
				// do nothing
			}

			// 3- use 'images' parameter as relative to default
			try
			{
				defaultUrl = getDefaultImagesBase();
				return new URL(defaultUrl, imageBase);
			}
			catch (final Exception exception)
			{
				// do nothing
			}
		}

		// 4- document base + "images/"
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

		// 5- default + "images/"
		try
		{
			if (defaultUrl == null)
			{
				defaultUrl = getDefaultImagesBase();
			}
			return new URL(defaultUrl, "images/");
		}
		catch (final Exception exception)
		{
			// do nothing
		}

		// default
		return defaultUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#getParameters()
	 */
	@Override
	public Properties getParameters()
	{
		return this.browser.getParameters();
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#getStyle()
	 */
	@Override
	public String getStyle()
	{
		return ".content { }" + ".link {color: blue;font-size: small; }" + ".mount {color: red;}" + ".linking {color: #007D82; font-size: small; }" + ".mounting {color: #007D82; font-size: small; }" + ".searching {color: #007D82; font-size: small; }";
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.component.Context#linkTo(java.lang.String)
	 */
	@Override
	public boolean linkTo(final String linkUrl, final String linkTarget)
	{
		if (this.urlScheme != null && linkUrl.startsWith(this.urlScheme))
		{
			this.browser.lookup(linkUrl.substring(this.urlScheme.length()));
			return true;
		}

		this.browser.linkTo(linkUrl, linkTarget, this.widget, this);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#status(java.lang.String)
	 */
	@Override
	public void status(final String string)
	{
		System.out.println(Messages.getString("Context.status") + string);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#warn(java.lang.String)
	 */
	@Override
	public void warn(final String message)
	{
		final String[] lines = message.split("\n");
		JOptionPane.showMessageDialog(null, lines, Messages.getString("Context.app"), JOptionPane.WARNING_MESSAGE);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#getInput()
	 */
	@Override
	public String getInput()
	{
		if (this.widget != null)
		{
			final Statusbar statusbar = ((Widget) this.widget).getStatusbar();
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
	protected URL makeURLAlt(final String source) throws MalformedURLException
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
		final File file = new File(source);
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
	protected URL makeURL(final String source)
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
	protected URL getDefaultBase()
	{
		Properties parameters = getParameters();

		// base parameter
		final String base = parameters == null ? null : parameters.getProperty("base");
		String uRLString = base != null ? base : System.getProperty("user.dir");

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
				final File folder = new File(uRLString);
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
	protected URL getDefaultImagesBase()
	{
		final URL base = getDefaultBase();
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
