/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.glue.component;

import com.sun.istack.internal.NotNull;

import treebolic.annotations.Nullable;
import treebolic.glue.ActionListener;
import treebolic.glue.NotImplementedException;

/**
 * Toolbar
 *
 * @author Bernard Bou
 */
public class Toolbar implements Component, treebolic.glue.iface.component.Toolbar<ActionListener>
{
	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param ignoredHandle Handle required for component creation (unused)
	 */
	public Toolbar(@Nullable final Object ignoredHandle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void addButton(@NotNull Button button, ActionListener listener)
	{
		throw new NotImplementedException();
	}

	/**
	 * (Ordered) List of toolbar buttons
	 *
	 * @return list of buttons
	 */
	@Override
	public Button[] getButtons()
	{
		throw new NotImplementedException();
	}
}
