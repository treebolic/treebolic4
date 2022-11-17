/*
 * Copyright (c) 2019-2022. Bernard Bou
 */

package treebolic.glue.iface.component;

/**
 * Glue interface for PopupMenu
 *
 * @author Bernard Bou
 */
public interface PopupMenu<C, L>
{
	/**
	 * Indexes to labels
	 */
	enum LabelIndices
	{
		LABEL_CANCEL, LABEL_INFO, LABEL_FOCUS, LABEL_LINKTO, LABEL_MOUNT, LABEL_UNMOUNT, LABEL_GOTO, LABEL_SEARCH
	}

	/**
	 * Indexes to images
	 */
	enum ImageIndices
	{
		IMAGE_CANCEL, IMAGE_INFO, IMAGE_FOCUS, IMAGE_LINK, IMAGE_MOUNT, IMAGE_GOTO, IMAGE_SEARCH
	}

	/**
	 * Add item
	 *
	 * @param label      label String
	 * @param imageIndex image index (as per ImageIndices ordinals)
	 * @param listener   listener
	 */
	void addItem(final String label, final int imageIndex, final L listener);

	/**
	 * Add item
	 *
	 * @param labelIndex label index (as per LabelIndices ordinals)
	 * @param imageIndex image index (as per ImageIndices ordinals)
	 * @param listener   listener
	 */
	void addItem(final int labelIndex, final int imageIndex, final L listener);

	/**
	 * Popup component at position
	 *
	 * @param component component to popup
	 * @param x         x-position
	 * @param y         y-position
	 */
	void popup(C component, int x, int y);
}
