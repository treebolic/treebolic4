/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.glue.component;

import treebolic.glue.ActionListener;
import treebolic.glue.NotImplementedException;

/**
 * Popup context menu
 *
 * @author Bernard Bou
 */
public class PopupMenu implements treebolic.glue.iface.component.PopupMenu<Component, ActionListener>
{
	/**
	 * Constructor
	 *
	 * @param ignoredHandle Opaque handle required for component creation
	 */
	protected PopupMenu(final Object ignoredHandle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void addItem(final String label, final int imageIndex, final ActionListener listener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void addItem(final int labelIndex, final int imageIndex, final ActionListener listener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void popup(final Component component, final int x, final int y)
	{
		throw new NotImplementedException();
	}
}
