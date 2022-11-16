/*
 * Copyright (c) 2022. Bernard Bou
 */

/**
 *
 */
package treebolic.glue.component;

import treebolic.glue.NotImplementedException;
import treebolic.glue.iface.ActionListener;
import treebolic.glue.iface.component.Converter;

/**
 * Dialog
 *
 * @author Bernard Bou
 */
public class Dialog implements treebolic.glue.iface.component.Dialog
{
	/**
	 * Constructor
	 */
	public Dialog()
	{
		throw new NotImplementedException();
	}

	@Override
	public void setHandle(final Object handle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setListener(final ActionListener actionListener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void display()
	{
		throw new NotImplementedException();
	}

	@Override
	public void set(final CharSequence header, final CharSequence... contents)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setConverter(final Converter converter)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setStyle(final String style)
	{
		throw new NotImplementedException();
	}
}
