/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.glue.component;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.NotImplementedException;

/**
 * Progress
 *
 * @author Bernard Bou
 */
public class Progress implements Component, treebolic.glue.iface.component.Progress
{
	/**
	 * Constructor
	 *
	 * @param ignoredHandle Opaque handle required for component creation
	 */
	public Progress(@Nullable final Object ignoredHandle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void put(@NonNull final String message, final boolean fail)
	{
		throw new NotImplementedException();
	}
}
