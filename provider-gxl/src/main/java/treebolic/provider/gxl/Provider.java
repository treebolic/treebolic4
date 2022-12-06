/**
 * Title : Treebolic GXL provider
 * Description : Treebolic GXL provider
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use: see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 *
 */
package treebolic.provider.gxl;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import treebolic.ILocator;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.Model;
import treebolic.model.Tree;
import treebolic.provider.IProvider;
import treebolic.provider.IProviderContext;
import treebolic.provider.ProviderUtils;

/**
 * Provider for GXL
 *
 * @author Bernard Bou
 */
public class Provider implements IProvider
{
	/**
	 * Provider context
	 */
	private IProviderContext context;

	/**
	 * Constructor
	 */
	public Provider()
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#setContext(treebolic.provider.IProviderContext)
	 */
	@Override
	public void setContext(final IProviderContext context)
	{
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#setLocator(treebolic.ILocator)
	 */
	@Override
	public void setLocator(final ILocator locator)
	{
		// do not need
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#setHandle(java.lang.Object)
	 */
	@Override
	public void setHandle(final Object handle)
	{
		// do not need
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#makeModel(java.lang.String, java.net.URL, java.util.Properties)
	 */
	@Nullable
	@Override
	public Model makeModel(final String source0, final URL base, final Properties parameters)
	{
		// get xml file
		String source = source0;
		if (source == null)
		{
			source = parameters.getProperty("source"); //$NON-NLS-1$
		}

		// parse XML file
		if (source != null)
		{
			// URL
			@Nullable final URL url = ProviderUtils.makeURL(source, base, parameters, this.context);

			// parse
			this.context.progress("Loading ..." + (url != null ? url : source), false); //$NON-NLS-1$
			@Nullable final Model model = url != null ? GxlParser.parseModel(url) : GxlParser.parseModel(source);
			if (model != null)
			{
				this.context.progress("Loaded " + (url != null ? url : source), false); //$NON-NLS-1$
				return model;
			}
			this.context.message("Cannot load GXL file <" + (url != null ? url : source) + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#makeTree(java.lang.String, java.net.URL, java.util.Properties, boolean)
	 */
	@Nullable
	@Override
	public Tree makeTree(final String source0, final URL base, @NonNull final Properties parameters, final boolean checkRecursion)
	{
		// get xml file
		String source = source0;
		if (source == null)
		{
			source = parameters.getProperty("source"); //$NON-NLS-1$
		}

		// parse XML file
		if (source != null)
		{
			// URL
			@Nullable final URL url = ProviderUtils.makeURL(source, base, parameters, this.context);

			// parse
			this.context.progress("Loading ..." + (url != null ? url : source), false); //$NON-NLS-1$
			try
			{
				@Nullable Tree tree = url != null ? GxlParser.parseTree(url) : GxlParser.parseTree(source);
				if (tree != null)
				{
					this.context.progress("Loaded " + (url != null ? url : source), false); //$NON-NLS-1$
					return tree;
				}
			}
			catch (final IOException e)
			{
				this.context.warn("GXL parser IO: " + e); //$NON-NLS-1$
			}
			catch (final SAXException e)
			{
				this.context.warn("GXL parser SAX: " + e); //$NON-NLS-1$
			}
			catch (final ParserConfigurationException e)
			{
				this.context.warn("GXL parser CONFIG: " + e); //$NON-NLS-1$
			}
			this.context.message("Cannot load GXL file <" + (url != null ? url : source) + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}
}
