/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue.component;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.ActionListener;

/**
 * Popup context menu, derived from JPopupMenu
 *
 * @author Bernard Bou
 */
public class PopupMenu extends JPopupMenu implements treebolic.glue.iface.component.PopupMenu<Component, ActionListener>
{
	/**
	 * Icon array as per ImageIndices ordinals:
	 * IMAGE_CANCEL, IMAGE_INFO, IMAGE_FOCUS, IMAGE_LINK, IMAGE_MOUNT, IMAGE_GOTO, IMAGE_SEARCH
	 */
	@SuppressWarnings("ConstantConditions")
	static final ImageIcon[] icons = new ImageIcon[]{ //
			new ImageIcon(PopupMenu.class.getResource("images/menu_cancel.png")), //
			new ImageIcon(PopupMenu.class.getResource("images/menu_info.png")), //
			new ImageIcon(PopupMenu.class.getResource("images/menu_focus.png")), //
			new ImageIcon(PopupMenu.class.getResource("images/menu_link.png")), //
			new ImageIcon(PopupMenu.class.getResource("images/menu_mount.png")), //
			new ImageIcon(PopupMenu.class.getResource("images/menu_goto.png")), //
			new ImageIcon(PopupMenu.class.getResource("images/menu_search.png")), //
	};

	/**
	 * Labels as per LabelIndices ordinals:
	 * LABEL_CANCEL, LABEL_INFO, LABEL_FOCUS, LABEL_LINKTO, LABEL_MOUNT, LABEL_UNMOUNT, LABEL_GOTO, LABEL_SEARCH
	 */
	static final String[] labels = Messages.getStrings( //
			"PopupMenu.label_cancel", //
			"PopupMenu.label_info", //
			"PopupMenu.label_focus", //
			"PopupMenu.label_link", //
			"PopupMenu.label_mount", //
			"PopupMenu.label_unmount", //
			"PopupMenu.label_goto", //
			"PopupMenu.label_search" //
	);

	/**
	 * Constructor
	 *
	 * @param ignoredHandle Opaque handle required for component creation
	 */
	protected PopupMenu(final Object ignoredHandle)
	{
		super();
	}

	@Override
	public void addItem(final int labelIdx, @Nullable final String label2, final int imageIndex, @NonNull final ActionListener listener)
	{
		String label = labelIdx == -1 ? "" : labels[labelIdx];
		if (label2 != null)
		{
			label += ' ' + label2;
		}
		@NonNull final JMenuItem menuItem = new JMenuItem(label);
		if (imageIndex != -1)
		{
			menuItem.setIcon(PopupMenu.icons[imageIndex]);
		}
		menuItem.addActionListener(e -> listener.actionPerformed(null));
		add(menuItem);
	}

	@Override
	public void addItem(final int labelIndex, final int imageIndex, @NonNull final ActionListener listener)
	{
		addItem(labelIndex, null, imageIndex, listener);
	}

	@Override
	public void popup(final Component component, final int x, final int y)
	{
		super.show((java.awt.Component) component, x, y);
	}
}
