/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.glue.component;

import java.util.function.Function;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.iface.ActionListener;
import treebolic.glue.NotImplementedException;

/**
 * Status bar
 *
 * @author Bernard Bou
 */
public class Statusbar implements Component, treebolic.glue.iface.component.Statusbar
{
	/**
	 * Constructor
	 *
	 * @param ignoredHandle Opaque handle required for component creation
	 */
	public Statusbar(@Nullable final Object ignoredHandle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void init(final int operationImage)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setColors(@Nullable final Integer backColor, @Nullable final Integer foreColor)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setStyle(@Nullable final String style)
	{
		throw new NotImplementedException();
	}

	@Override
	public void addListener(@NonNull final ActionListener listener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setListener(@Nullable final ActionListener actionListener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void put(final int image, @Nullable final Function<String[], String> converter, @Nullable final String label, final String... contents)
	{
		throw new NotImplementedException();
	}

	@Override
	public void put(@Nullable final String message)
	{
		throw new NotImplementedException();
	}
}
