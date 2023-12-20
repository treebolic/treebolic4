/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.model;

import treebolic.model.MutableEdge;
import treebolic.model.MutableNode;

/**
 * Decorates nodes and images with images
 */
public interface ImageDecorator
{
	/**
	 * Set node image decoration
	 *
	 * @param node  node
	 * @param index index
	 */
	void setNodeImage(final MutableNode node, int index);

	/**
	 * Set node tree edge image decoration
	 *
	 * @param node  node
	 * @param index index
	 */
	void setTreeEdgeImage(final MutableNode node, int index);

	/**
	 * Set edge image decoration
	 *
	 * @param edge  edge
	 * @param index index
	 */
	void setEdgeImage(final MutableEdge edge, int index);
}
