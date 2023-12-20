/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.glue;

import treebolic.annotations.NonNull;

/**
 * Animator implements animation
 *
 * @author Bernard Bou
 */
public class Animator implements treebolic.glue.iface.Animator<ActionListener>
{
	/**
	 * Constructor
	 */
	public Animator()
	{
		throw new NotImplementedException();
	}

	@Override
	public boolean run(@NonNull final ActionListener animation, final int steps, final int startDelay)
	{
		throw new NotImplementedException();
	}

	@Override
	public boolean isRunning()
	{
		throw new NotImplementedException();
	}
}
