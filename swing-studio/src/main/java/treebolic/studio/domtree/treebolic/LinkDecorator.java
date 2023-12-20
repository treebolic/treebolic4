/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.domtree.treebolic;

import org.w3c.dom.Node;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import treebolic.annotations.NonNull;
import treebolic.studio.domtree.ElementDecorator;

/**
 * Link node decorator
 *
 * @author Bernard Bou
 */
public class LinkDecorator extends ElementDecorator
{
	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param node DOM node
	 */
	public LinkDecorator(final Node node)
	{
		super(node);
	}

	// S T R I N G S

	@NonNull
	@Override
	public String getName()
	{
		return " " + super.getName() + " ";
	}

	@Override
	public String getValue()
	{
		final String string = super.getValue();
		try
		{
			return URLDecoder.decode(string, "UTF8");
		}
		catch (final UnsupportedEncodingException exception)
		{
			// do nothing
		}
		return string;
	}
}
