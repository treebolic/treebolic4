/**
 *
 */
package treebolic.glue;

import treebolic.glue.component.Component;

/**
 * Graphics cache
 *
 * @author Bernard Bou
 */
public class GraphicsCache implements treebolic.glue.iface.GraphicsCache<Graphics>
{
	/**
	 * Cosntructor
	 *
	 * @param component component
	 * @param graphics  graphics context
	 * @param width     width
	 * @param height    height
	 */
	public GraphicsCache(final Component component, final Graphics graphics, final int width, final int height)
	{
		throw new NotImplementedException();
	}

	@Override
	public Graphics getGraphics()
	{
		throw new NotImplementedException();
	}

	@Override
	public void put(final Graphics graphics)
	{
		throw new NotImplementedException();
	}
}
