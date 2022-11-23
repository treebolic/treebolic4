/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.glue.component;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
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
	public PopupMenu(@Nullable final Object ignoredHandle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void addItem(@NonNull final String label, final int imageIndex, @NonNull final ActionListener listener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void addItem(final int labelIndex, final int imageIndex, @NonNull final ActionListener listener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void popup(@NonNull final Component component, final int x, final int y)
	{
		throw new NotImplementedException();
	}
}
