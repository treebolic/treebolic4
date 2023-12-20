/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.domtree;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Element decorator
 *
 * @author Bernard Bou
 */
public class ElementDecorator extends DefaultDecorator
{
	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param node DOM node
	 */
	public ElementDecorator(final Node node)
	{
		super(node);
	}

	// S T R I N G S

	@NonNull
	@Override
	public String getName()
	{
		return getElement().getNodeName();
	}

	@Override
	public String getValue()
	{
		return getAttributes();
	}

	// A C C E S S

	/**
	 * Get element
	 *
	 * @return element
	 */
	private Element getElement()
	{
		return (Element) this.node;
	}

	// H E L P E R S

	/**
	 * Get element text
	 *
	 * @return element text
	 */
	@Nullable
	protected String getText()
	{
		final Node child = this.node.getFirstChild();
		if (child != null)
		{
			return child.getNodeValue();
		}
		return null;
	}

	/**
	 * Get attribute string
	 *
	 * @return attribute string
	 */
	@NonNull
	protected String getAttributes()
	{
		@NonNull StringBuilder result = new StringBuilder();
		final NamedNodeMap map = getElement().getAttributes();
		if (map != null)
		{
			for (int i = 0; i < map.getLength(); i++)
			{
				if (i != 0)
				{
					result.append(" ");
				}
				final Node attribute = map.item(i);
				result.append(attribute.getNodeName()).append("=\"").append(attribute.getNodeValue()).append("\"");
			}
		}
		return result.toString();
	}
}
