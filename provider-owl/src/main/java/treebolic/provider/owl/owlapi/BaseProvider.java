/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.provider.owl.owlapi;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import treebolic.ILocator;
import treebolic.annotations.NonNull;
import treebolic.model.Model;
import treebolic.model.Tree;
import treebolic.model.Utils;
import treebolic.provider.IProvider;
import treebolic.provider.IProviderContext;
import treebolic.provider.ProviderUtils;

/**
 * Provider for OWL
 * <F> factory
 *
 * @author Bernard Bou
 */
public abstract class BaseProvider implements IProvider
{
	// S T A T I C . D A T A

	/**
	 * Provider context
	 */
	private IProviderContext context;

	/**
	 * Factory
	 */
	private OwlModelFactory factory;

	/**
	 * Get factory
	 */
	abstract protected OwlModelFactory factory(@NonNull Properties properties);

	/**
	 * Constructor
	 */
	public BaseProvider()
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
	@Override
	public Model makeModel(final String source0, final URL base, final Properties parameters)
	{
		// get owl file
		String source = source0;
		if (source == null)
		{
			source = parameters.getProperty("source");
		}

		// parse OWL file
		if (source != null)
		{
			// settings properties
			Properties properties = getSettings(base, parameters);

			// factory
			if (this.factory == null)
			{
				this.factory = factory(properties);
			}

			// URL
			final URL url = ProviderUtils.makeURL(source, base, parameters, this.context);

			// parse
			this.context.progress("Loading ..." + (url != null ? url : source), false);
			final Model model = this.factory.makeModel(url != null ? url.toString() : source);
			if (model != null)
			{
				this.context.progress("Loaded " + (url != null ? url : source), false);
				return model;
			}
			this.context.message("Cannot load OWL file <" + (url != null ? url : source) + ">");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.IProvider#makeTree(java.lang.String, java.net.URL, java.util.Properties, boolean)
	 */
	@Override
	public Tree makeTree(final String source0, final URL base, final Properties parameters, final boolean checkRecursion)
	{
		// get owl file
		String source = source0;
		if (source == null)
		{
			source = parameters.getProperty("source");
		}

		// parse owl file
		if (source != null)
		{
			// URL
			final URL url = ProviderUtils.makeURL(source, base, parameters, this.context);

			// settings properties
			final Properties properties = getSettings(base, parameters);

			// parser
			if (this.factory == null)
			{
				this.factory = factory(properties);
			}

			// parse
			this.context.progress("Loading ..." + (url != null ? url : source), false);
			final Tree tree = this.factory.makeTree(url != null ? url.toString() : source);
			if (tree != null)
			{
				this.context.progress("Loaded " + (url != null ? url : source), false);
				return tree;
			}
			this.context.message("Cannot load OWL file <" + (url != null ? url : source) + ">");
		}
		return null;
	}

	/**
	 * Get properties in settings file or command-line override
	 *
	 * @param base       base
	 * @param parameters parameters
	 * @return properties
	 */
	private Properties getSettings(final URL base, final Properties parameters)
	{
		// settings properties from configuration file set by settings=file
		Properties properties = null;
		final String location = parameters == null ? null : parameters.getProperty("settings");
		if (location != null && !location.isEmpty())
		{
			final URL url = ProviderUtils.makeURL(location, base, parameters, this.context);

			this.context.progress("Loading ..." + (url != null ? url : location), false);
			try
			{
				properties = url != null ? Utils.load(url) : Utils.load(location);
				this.context.progress("Loaded " + (url != null ? url : location), false);
			}
			catch (final IOException e)
			{
				this.context.message("Cannot load Settings file <" + (url != null ? url : location) + ">");
			}
		}

		// settings properties from command-line override
		assert parameters != null;
		for (String parameter : parameters.stringPropertyNames())
		{
			switch (parameter)
			{
				case "source":
				case "provider":
				case "base":
				case "imagebase":
				case "settings":
					continue;
			}

			if (properties == null)
			{
				properties = new Properties();
			}
			properties.setProperty(parameter, parameters.getProperty(parameter));
		}

		return properties;
	}
}
