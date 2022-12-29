/**
 * Title : Treebolic GraphViz provider
 * Description : Treebolic GraphViz provider
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.provider.graphviz;

import java.net.URL;
import java.util.Properties;

import treebolic.ILocator;
import treebolic.model.Model;
import treebolic.model.Tree;
import treebolic.provider.IProvider;
import treebolic.provider.IProviderContext;
import treebolic.provider.ProviderUtils;

/**
 * Provider factory implementation for GraphViz provider
 *
 * @author Bernard Bou
 */
public class Provider implements IProvider
{
	/**
	 * Provider context
	 */
	private IProviderContext context;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public Provider()
	{
		// do nothing
	}

	@Override
	public void setContext(final IProviderContext context)
	{
		this.context = context;
	}

	@Override
	public void setLocator(final ILocator locator)
	{
		// do not need
	}

	@Override
	public void setHandle(final Object handle)
	{
		// do not need
	}

	@Override
	public Model makeModel(final String source0, final URL base, final Properties parameters)
	{
		// get graphviz file
		String source = source0;
		if (source == null)
		{
			source = parameters.getProperty("source"); 
		}

		if (source != null)
		{
			// URL
			final URL url = ProviderUtils.makeURL(source, base, parameters, this.context);

			// parse
			this.context.progress("Loading ..." + (url != null ? url : source), false); 
			final Model model = url != null ? GraphvizParser.parseModel(url) : GraphvizParser.parseModel(source);
			if (model != null)
			{
				this.context.progress("Loaded " + (url != null ? url : source), false); 
				return model;
			}
			else
			{
				this.context.warn("Cannot load GraphViz file <" + (url != null ? url : source) + ">");  //$NON-NLS-2$
			}
		}
		return null;
	}

	@Override
	public Tree makeTree(final String source0, final URL base, final Properties parameters, final boolean checkRecursion)
	{
		// get graphviz file
		String source = source0;
		if (source == null)
		{
			source = parameters.getProperty("source"); 
		}

		if (source != null)
		{
			// URL
			final URL url = ProviderUtils.makeURL(source, base, parameters, this.context);

			// parse
			this.context.progress("Loading ..." + (url != null ? url : source), false); 
			final Tree tree = url != null ? GraphvizParser.parseTree(url) : GraphvizParser.parseTree(source);
			if (tree != null)
			{
				this.context.progress("Loaded " + (url != null ? url : source), false); 
				return tree;
			}
			else
			{
				this.context.warn("Cannot load GraphViz file <" + (url != null ? url : source) + ">");  //$NON-NLS-2$
			}
		}
		return null;
	}
}
