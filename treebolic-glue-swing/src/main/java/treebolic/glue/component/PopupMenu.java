/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue.component;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import treebolic.glue.ActionListener;

/**
 * Popup context menu, derived from JPopupMenu
 *
 * @author Bernard Bou
 */
public class PopupMenu extends JPopupMenu implements treebolic.glue.iface.component.PopupMenu<Component, ActionListener>
{
	private static final long serialVersionUID = 1L;

	public enum ImageIndices
	{
		IMAGE_CANCEL, IMAGE_INFO, IMAGE_FOCUS, IMAGE_LINK, IMAGE_MOUNT, IMAGE_GOTO, IMAGE_SEARCH
	}

	/**
	 * Icon array
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
	 * Labels
	 * indexes are public
	 */
	static public String[] labels = Messages.getStrings("PopupMenu.label_cancel", "PopupMenu.label_info", "PopupMenu.label_focus", "PopupMenu.label_link", "PopupMenu.label_mount", "PopupMenu.label_unmount", "PopupMenu.label_goto", "PopupMenu.label_search");

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
	public void addItem(final String label, final int imageIndex, final ActionListener listener)
	{
		final JMenuItem menuItem = new JMenuItem(label);
		if (imageIndex != -1)
		{
			menuItem.setIcon(PopupMenu.icons[imageIndex]);
		}
		menuItem.addActionListener(e -> listener.actionPerformed(null));
		add(menuItem);
	}

	@Override
	public void popup(final Component component, final int x, final int y)
	{
		super.show((java.awt.Component) component, x, y);
	}
}
