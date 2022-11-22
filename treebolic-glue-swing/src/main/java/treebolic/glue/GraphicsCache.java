/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

import treebolic.annotations.NonNull;
import treebolic.glue.component.Component;

/**
 * Graphics, embeds awt's Image
 *
 * @author Bernard Bou
 */
public class GraphicsCache implements treebolic.glue.iface.GraphicsCache<Graphics>
{
	private final java.awt.Image image;

	/**
	 * Constructor
	 *
	 * @param component       component
	 * @param ignoredGraphics unused graphics context
	 * @param width           width
	 * @param height          height
	 */
	public GraphicsCache(final Component component, final Graphics ignoredGraphics, final int width, final int height)
	{
		final java.awt.Component awtComponent = (java.awt.Component) component;
		this.image = awtComponent.createImage(width, height);
	}

	@NonNull
	@Override
	public Graphics getGraphics()
	{
		return new Graphics(this.image.getGraphics());
	}

	@Override
	public void put(@NonNull final Graphics graphics)
	{
		graphics.g.drawImage(this.image, 0, 0, null);
	}
}
