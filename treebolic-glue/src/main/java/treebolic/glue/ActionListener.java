/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.glue;

/**
 * Action listener
 *
 * @author Bernard Bou
 */
public abstract class ActionListener implements treebolic.glue.iface.ActionListener
{
	@Override
	abstract public boolean onAction(Object... params);
}
