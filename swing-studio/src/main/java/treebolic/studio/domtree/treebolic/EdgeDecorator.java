/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.domtree.treebolic;

import org.w3c.dom.Node;

import treebolic.annotations.NonNull;
import treebolic.studio.domtree.ElementDecorator;

/**
 * Edge node decorator
 *
 * @author Bernard Bou
 */
public class EdgeDecorator extends ElementDecorator
{
	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param node DOM node
	 */
	public EdgeDecorator(final Node node)
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

	// @Override
	// public String getValue()
	// {
	// 	return super.getValue();
	// }
}
