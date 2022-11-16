/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.glue.component;

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
	 * @param handle Opaque handle required for component creation
	 */
	public Progress(final Object handle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void put(final String message, final boolean fail)
	{
		throw new NotImplementedException();
	}
}
