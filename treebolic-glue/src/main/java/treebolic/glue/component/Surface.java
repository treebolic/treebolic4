package treebolic.glue.component;

import treebolic.glue.EventListener;
import treebolic.glue.Graphics;
import treebolic.glue.NotImplementedException;

/**
 * Surface
 *
 * @author Bernard Bou
 */
public abstract class Surface implements Component, treebolic.glue.iface.component.Surface<Graphics, EventListener>
{
	/**
	 * Error margin when finding node
	 */
	static public final float FINDERRORMARGINFACTOR = 1.5F;

	/**
	 * Constructor
	 *
	 * @param handle
	 *        Handle required for component creation (unused)
	 */
	public Surface(final Object handle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void repaint()
	{
		throw new NotImplementedException();
	}

	@Override
	abstract public void paint(final Graphics g);

	@Override
	public int getWidth()
	{
		throw new NotImplementedException();
	}

	@Override
	public int getHeight()
	{
		throw new NotImplementedException();
	}

	@Override
	public void setFireHover(final boolean flag)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setCursor(final int cursor)
	{
		throw new NotImplementedException();
	}

	@Override
	public void addEventListener(final EventListener listener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setToolTipText(String message)
	{
		throw new NotImplementedException();
	}
}
