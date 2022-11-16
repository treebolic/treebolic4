/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

/**
 * Event listener
 *
 * @author Bernard Bou
 */
public abstract class EventListener implements treebolic.glue.iface.EventListener
{
	@Override
	abstract public boolean onDown(int x, int y, boolean rotate);

	@Override
	abstract public boolean onUp(int x, int y);

	@Override
	abstract public boolean onHover(int x, int y);

	@Override
	abstract public boolean onDragged(int x, int y);
}
