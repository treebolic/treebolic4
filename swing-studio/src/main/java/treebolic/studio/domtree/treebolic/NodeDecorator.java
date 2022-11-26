/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio.domtree.treebolic;

import org.w3c.dom.Node;

import treebolic.studio.domtree.ElementDecorator;

/**
 * (Treebolic) node node decorator
 *
 * @author Bernard Bou
 */
public class NodeDecorator extends ElementDecorator
{
	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param node DOM node
	 */
	public NodeDecorator(final Node node)
	{
		super(node);
	}

	// S T R I N G S

	/*
	 * (non-Javadoc)
	 * @see treebolic.studio.domtree.ElementDecorator#getName()
	 */
	@Override
	public String getName()
	{
		return " " + super.getName() + " ";
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.studio.domtree.ElementDecorator#getValue()
	 */
	// @Override
	// public String getValue()
	// {
	// 	return super.getValue();
	// }
}
