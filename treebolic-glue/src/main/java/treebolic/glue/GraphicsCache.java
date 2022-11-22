/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

import treebolic.annotations.NonNull;
import treebolic.glue.component.Component;

/**
 * Graphics cache
 *
 * @author Bernard Bou
 */
public class GraphicsCache implements treebolic.glue.iface.GraphicsCache<Graphics>
{
	/**
	 * Constructor
	 *
	 * @param component component
	 * @param graphics  graphics context
	 * @param width     width
	 * @param height    height
	 */
	public GraphicsCache(@NonNull final Component component, @NonNull final Graphics graphics, final int width, final int height)
	{
		throw new NotImplementedException();
	}

	@NonNull
	@Override
	public Graphics getGraphics()
	{
		throw new NotImplementedException();
	}

	@Override
	public void put(@NonNull final Graphics graphics)
	{
		throw new NotImplementedException();
	}
}
