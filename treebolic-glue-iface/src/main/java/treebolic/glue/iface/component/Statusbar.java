/*
 * Copyright (c) 2019-2023. Bernard Bou
 */

package treebolic.glue.iface.component;

import java.util.function.Function;

import treebolic.glue.iface.ActionListener;

/**
 * Glue interface for Statusbar
 *
 * @author Bernard Bou
 */
public interface Statusbar
{
	/**
	 * Image indices
	 */
	enum ImageIndices
	{
		/**
		 * Info
		 */
		INFO,
		/**
		 * Link
		 */
		LINK,
		/**
		 * Mount
		 */
		MOUNT,
		/**
		 * Search
		 */
		SEARCH,
		/**
		 * Count
		 */
		COUNT
	}

	// public Statusbar();

	/**
	 * Init
	 *
	 * @param image image
	 */
	void init(final int image);

	/**
	 * Set hyperlink listener
	 *
	 * @param actionListener listener
	 */
	void setListener(final ActionListener actionListener);

	/**
	 * Set colors
	 *
	 * @param backColor back color
	 * @param foreColor fore color
	 */
	@SuppressWarnings("EmptyMethod")
	void setColors(Integer backColor, Integer foreColor);

	/**
	 * Set style
	 *
	 * @param style style
	 */
	void setStyle(String style);

	/**
	 * Put status
	 *
	 * @param image     image
	 * @param converter converter
	 * @param label     label
	 * @param content   content
	 */
	void put(final int image, final Function<String[], String> converter, final String label, final String... content);

	/**
	 * Put message
	 *
	 * @param message message
	 */
	void put(final String message);

	/**
	 * Add listener
	 *
	 * @param listener listener
	 */
	@SuppressWarnings("EmptyMethod")
	void addListener(final ActionListener listener);
}
