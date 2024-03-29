/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.glue;

import java.awt.event.ActionEvent;

/**
 * Action listener, derived from awt's ActionListener
 */
public abstract class ActionListener implements java.awt.event.ActionListener, treebolic.glue.iface.ActionListener
{
	@Override
	abstract public boolean onAction(Object... params);

	@Override
	public void actionPerformed(final ActionEvent arg0)
	{
		onAction();
	}
}
