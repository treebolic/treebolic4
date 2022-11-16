/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import treebolic.IContext;
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
	 */
	public Context(final MainFrame application, final String source, final String base, final String imageBase)
	{
		this.application = application;
		this.source = source;
		this.base = base;
		this.imageBase = imageBase;
		// System.out.println("source: " + source); //$NON-NLS-1$
		// System.out.println("base: " + base); //$NON-NLS-1$
		// System.out.println("imagebase: " + imageBase); //$NON-NLS-1$
	}

	/**
	 * Get widget
	 * 
	 * @return widget
	 */
	public Widget getWidget()
	{
		return this.application == null ? null : this.application.getWidget();
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
					return file.toURI().toURL();
			}
			catch (final MalformedURLException e)
			{
				// do nothing
			}

			// 2-use 'base' parameter as path relative to default
			String base = this.base;
			if (!base.endsWith("/")) //$NON-NLS-1$
			{
				base += "/"; //$NON-NLS-1$
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

			// 2-use 'images' parameter as full-fledged directory
			try
			{
				final File file = new File(this.imageBase);
				if (file.exists() && file.isDirectory())
					return file.toURI().toURL();
			}
			catch (final MalformedURLException e)
			{
				// do nothing
			}

			String imageBase = this.imageBase;
			if (!imageBase.endsWith("/")) //$NON-NLS-1$
			{
				imageBase += "/"; //$NON-NLS-1$
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
			System.err.print("Can't handle 'images' parameter: " + imageBase); //$NON-NLS-1$
		}

		// 5- document base + "images/"
		try
		{
			if (documentUrl == null)
			{
				documentUrl = makeURL(this.source);
			}
			return new URL(documentUrl, "images/"); //$NON-NLS-1$
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
		// return new URL(defaultUrl, "images/"); //$NON-NLS-1$
		// }
		// catch (final Exception exception)
		// {
		// // do nothing
		// }

		// 7- default
		return defaultUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#getParameters()
	 */
	@Override
	public Properties getParameters()
	{
		return this.application.getParameters();
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#getStyle()
	 */
	@Override
	public String getStyle()
	{
		return ".content { }" + //$NON-NLS-1$
				".link {color: blue;font-size: small; }" + //$NON-NLS-1$
				".mount {color: red;}" + //$NON-NLS-1$
				".linking {color: #007D82; font-size: small; }" + //$NON-NLS-1$
				".mounting {color: #007D82; font-size: small; }" + //$NON-NLS-1$
				".searching {color: #007D82; font-size: small; }"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.component.Context#linkTo(java.lang.String)
	 */
	@Override
	public boolean linkTo(final String linkUrl, final String linkTarget)
	{
		if (Desktop.isDesktopSupported())
		{
			try
			{
				final File file = new File(linkUrl);
				boolean exists = file.exists();
				final URI uri = exists ? file.toURI() : new URI(linkUrl);
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
							System.err.println(e.getMessage() + ':' + linkUrl);
						}
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

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#status(java.lang.String)
	 */
	@Override
	public void status(final String string)
	{
		System.out.println(Messages.getString("Context.status") + string); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#warn(java.lang.String)
	 */
	@Override
	public void warn(final String message)
	{
		final String[] lines = message.split("\n"); //$NON-NLS-1$
		JOptionPane.showMessageDialog(null, lines, Messages.getString("Context.title"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.IContext#getInput()
	 */
	@Override
	public String getInput()
	{
		final Widget widget = getWidget();
		if (widget != null)
		{
			final Statusbar statusbar = widget.getStatusbar();
			if (statusbar != null)
				return statusbar.get();
		}
		return null;
	}

	// U R L F A C T O R Y

	/**
	 * Make url
	 *
	 * @param source
	 *        source
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
	 * @param source
	 *        source
	 * @return url
	 */
	protected URL makeURL(final String source)
	{
		if (source == null)
			return null;

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
		// base parameter
		// final String base = getParameters().getProperty("base"); //$NON-NLS-1$
		// String uRLString = base != null ? base : System.getProperty("user.dir"); //$NON-NLS-1$
		String uRLString = System.getProperty("user.dir"); //$NON-NLS-1$

		// tail
		if (!uRLString.endsWith("/")) //$NON-NLS-1$
		{
			uRLString += "/"; //$NON-NLS-1$
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
			return new URL(base, "images/"); //$NON-NLS-1$
		}
		catch (final MalformedURLException exception)
		{
			// do nothing
		}
		return null;
	}
}
