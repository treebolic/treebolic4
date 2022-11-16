/*
 * Copyright (c) 2022. Bernard Bou
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

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent arg0)
	{
		onAction();
	}
}
