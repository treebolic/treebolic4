/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.glue.component;

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
	 * @param handle Opaque handle required for component creation
	 */
	public Statusbar(final Object handle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void init(final int operationImage)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setColors(final Color backColor, final Color foreColor)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setStyle(final String style)
	{
		throw new NotImplementedException();
	}

	@Override
	public void addListener(final ActionListener listener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setListener(final treebolic.glue.iface.ActionListener actionListener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void put(final int image, final Converter converter, final String label, final String... contents)
	{
		throw new NotImplementedException();
	}

	@Override
	public void put(final String message)
	{
		throw new NotImplementedException();
	}

	/**
	 * Get input
	 *
	 * @return input
	 */
	public String get()
	{
		throw new NotImplementedException();
	}
}
