/*
 * Copyright (c) 2019-2023. Bernard Bou
 */

package treebolic.component;

import java.util.function.Function;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.iface.Colors;
import treebolic.glue.component.Component;
import treebolic.model.Settings;

/**
 * Status bar
 *
 * @author Bernard Bou
 */
public class Statusbar extends treebolic.glue.component.Statusbar implements Component
{
	/**
	 * Put type
	 */
	public enum PutType
	{
		/**
		 * Tag as info
		 */
		INFO,
		/**
		 * Tag as link
		 */
		LINK,
		/**
		 * Tag as mount info
		 */
		MOUNT,
		/**
		 * Tag as search info
		 */
		SEARCH
	}

	// colors

	/**
	 * Label background color
	 */
	static private final Integer[] backColor = new Integer[PutType.values().length];

	/**
	 * Label foreground color
	 */
	static private final Integer[] foreColor = new Integer[PutType.values().length];

	// init
	static
	{
		for (int i = 0; i < PutType.values().length; i++)
		{
			Statusbar.backColor[i] = Colors.WHITE;
			Statusbar.foreColor[i] = Colors.BLACK;
		}
	}

	/**
	 * Constructor
	 *
	 * @param handle Handle required for component creation
	 */
	public Statusbar(final Object handle)
	{
		super(handle);

		init(null);
	}

	/**
	 * Init
	 *
	 * @param settings settings
	 */
	@SuppressWarnings("WeakerAccess")
	public void init(@Nullable @SuppressWarnings("SameParameterValue") final Settings settings)
	{
		// colors
		if (settings != null)
		{
			if (settings.backColor != null)
			{
				Statusbar.backColor[PutType.INFO.ordinal()] = settings.backColor;
				Statusbar.backColor[PutType.LINK.ordinal()] = settings.backColor;
				Statusbar.backColor[PutType.MOUNT.ordinal()] = settings.backColor;
				Statusbar.backColor[PutType.SEARCH.ordinal()] = settings.backColor;
			}
			if (settings.foreColor != null)
			{
				Statusbar.foreColor[PutType.INFO.ordinal()] = settings.foreColor;
				Statusbar.foreColor[PutType.LINK.ordinal()] = settings.foreColor;
				Statusbar.foreColor[PutType.MOUNT.ordinal()] = settings.foreColor;
				Statusbar.foreColor[PutType.SEARCH.ordinal()] = settings.foreColor;
			}
		}

		// super
		super.init(PutType.INFO.ordinal());
	}

	private void setColors(@NonNull final PutType type)
	{
		final Integer backColor = Statusbar.backColor[type.ordinal()];
		final Integer foreColor = Statusbar.foreColor[type.ordinal()];
		setColors(backColor, foreColor);
	}

	/**
	 * Put status
	 *
	 * @param type      status type as per below
	 * @param converter converter
	 * @param label     label
	 * @param contents  contents
	 */
	public void put(@NonNull final PutType type, @Nullable final Function<String[], String> converter, final String label, final String[] contents)
	{
		setColors(type);
		super.put(type.ordinal(), converter, label, contents);
	}
}
