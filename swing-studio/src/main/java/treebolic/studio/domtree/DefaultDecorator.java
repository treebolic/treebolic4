/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio.domtree;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import treebolic.annotations.NonNull;

/**
 * Node decorator
 *
 * @author Bernard Bou
 */
public class DefaultDecorator
{
	// D A T A

	/**
	 * DOM node
	 */
	protected final Node node;

	// C O N S T R U C T

	/**
	 * Constructor
	 *
	 * @param node DOM node to decorate
	 */
	public DefaultDecorator(final Node node)
	{
		this.node = node;
	}

	// S T R I N G S

	/**
	 * Get node name
	 *
	 * @return node name
	 */
	@NonNull
	public String getName()
	{
		return this.node.getNodeName();
	}

	/**
	 * Get node data
	 *
	 * @return node data
	 */
	public String getValue()
	{
		@NonNull final StringBuilder buffer = new StringBuilder();

		// value
		String value = this.node.getNodeValue();
		if (value != null)
		{
			buffer.append(": ");

			// trim the value to get rid of newlines at the front
			value = value.trim();
			if ("\n".compareTo(value) == 0)
			{
				buffer.append('\u00B6');
			}
			else
			{
				final int breakPos = value.indexOf("\n");
				if (breakPos >= 0)
				{
					value = value.substring(0, breakPos);
				}
				buffer.append(value);
			}
		}

		// children
		@NonNull final String childrenText = childrenToString();
		if (childrenText != null)
		{
			buffer.append(childrenText);
		}

		return buffer.toString();
	}

	/**
	 * Convert children's value to string
	 *
	 * @return string
	 */
	@NonNull
	protected String childrenToString()
	{
		@NonNull final StringBuilder buffer = new StringBuilder();
		@NonNull final NodeList list = this.node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
		{
			final Node node = list.item(i);
			if (node.getNodeType() == Node.ENTITY_REFERENCE_NODE)
			{// content is in the TEXT node under it
				buffer.append(new DefaultDecorator(node));
				// "value" has the text, same as a text node.
				// while EntityRef has it in a text node underneath.
				// (because EntityRef can contain multiple subelements)
				// convert angle brackets and ampersands for display
			}
			else
			{// ELEMENT_NODE -- handed separately
				// ATTR_TYPE -- not in the DOM tree
				// ENTITY_TYPE -- does not appear in the DOM
				// PROCINSTR_TYPE -- not "data"
				// COMMENT_TYPE -- not "data"
				// DOCUMENT_TYPE -- Root node only. No data to display.
				// DOCTYPE_TYPE -- Appears under the root only
				// DOCFRAG_TYPE -- equiv. to "document" for fragments
				// NOTATION_TYPE -- nothing but binary data in here
				buffer.append(node.getNodeValue());
			}
		}
		return buffer.toString();
	}
}