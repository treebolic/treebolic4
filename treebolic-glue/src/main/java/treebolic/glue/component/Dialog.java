/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue.component;

import java.util.function.Function;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.NotImplementedException;
import treebolic.glue.iface.ActionListener;

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
	public void setHandle(@Nullable final Object handle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setListener(@Nullable final ActionListener actionListener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void display()
	{
		throw new NotImplementedException();
	}

	@Override
	public void set(@Nullable final CharSequence header, final CharSequence... contents)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setConverter(@NonNull final Function<CharSequence[], String> converter)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setStyle(@Nullable final String style)
	{
		throw new NotImplementedException();
	}
}
