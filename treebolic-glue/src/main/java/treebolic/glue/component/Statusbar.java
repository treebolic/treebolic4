/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.glue.component;

import com.sun.istack.internal.NotNull;

import treebolic.annotations.Nullable;
import treebolic.glue.ActionListener;
import treebolic.glue.Color;
import treebolic.glue.NotImplementedException;
import treebolic.glue.iface.component.Converter;

/**
 * Status bar
 *
 * @author Bernard Bou
 */
public class Statusbar implements Component, treebolic.glue.iface.component.Statusbar<Color, ActionListener>
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
	public void setColors(@Nullable final Color backColor, @Nullable final Color foreColor)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setStyle(@Nullable final String style)
	{
		throw new NotImplementedException();
	}

	@Override
	public void addListener(@NotNull final ActionListener listener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setListener(@Nullable final treebolic.glue.iface.ActionListener actionListener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void put(final int image, @Nullable final Converter converter, @Nullable final String label, final String... contents)
	{
		throw new NotImplementedException();
	}

	@Override
	public void put(@Nullable final String message)
	{
		throw new NotImplementedException();
	}
}
