/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.glue.component;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
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
	 * Constructor
	 *
	 * @param handle Handle required for component creation (unused)
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
	abstract public void paint(@NonNull final Graphics g);

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
	public void addEventListener(@NonNull final EventListener listener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setToolTipText(@Nullable final String message)
	{
		throw new NotImplementedException();
	}

	@Override
	public float getFinderDistanceEpsilonFactor()
	{
		throw new NotImplementedException();
	}
}
