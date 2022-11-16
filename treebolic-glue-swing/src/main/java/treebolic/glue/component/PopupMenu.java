/**
 * Title : Treebolic
 * Description : Treebolic
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 *
 * Update : Mon Mar 10 00:00:00 CEST 2008
 */
package treebolic.glue.component;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import treebolic.glue.ActionListener;

/**
 * Popup context menu
 *
 * @author Bernard Bou
 */
public class PopupMenu extends JPopupMenu implements treebolic.glue.iface.component.PopupMenu<Component, ActionListener>
{
	private static final long serialVersionUID = 1L;

	static public enum ImageIndices
	{
		IMAGE_CANCEL, IMAGE_INFO, IMAGE_FOCUS, IMAGE_LINK, IMAGE_MOUNT, IMAGE_GOTO, IMAGE_SEARCH
	}

	/**
	 * Icon array
	 */
	static ImageIcon[] icons = new ImageIcon[] { 
			new ImageIcon(PopupMenu.class.getResource("images/menu_cancel.png")), //$NON-NLS-1$
			new ImageIcon(PopupMenu.class.getResource("images/menu_info.png")), //$NON-NLS-1$
			new ImageIcon(PopupMenu.class.getResource("images/menu_focus.png")), //$NON-NLS-1$
			new ImageIcon(PopupMenu.class.getResource("images/menu_link.png")), //$NON-NLS-1$
			new ImageIcon(PopupMenu.class.getResource("images/menu_mount.png")), //$NON-NLS-1$
			new ImageIcon(PopupMenu.class.getResource("images/menu_goto.png")), //$NON-NLS-1$
			new ImageIcon(PopupMenu.class.getResource("images/menu_search.png")), //$NON-NLS-1$
	};

	/**
	 * Labels
	 * indexes are public 
	 */
	static public String[] labels = Messages.getStrings(
			"PopupMenu.label_cancel", //$NON-NLS-1$
			"PopupMenu.label_info", //$NON-NLS-1$
			"PopupMenu.label_focus", //$NON-NLS-1$
			"PopupMenu.label_link", //$NON-NLS-1$
			"PopupMenu.label_mount", //$NON-NLS-1$
			"PopupMenu.label_unmount", //$NON-NLS-1$
			"PopupMenu.label_goto", //$NON-NLS-1$
			"PopupMenu.label_search" //$NON-NLS-1$
	);

	/**
	 * Constructor
	 */
	protected PopupMenu(final Object handle)
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
		menuItem.addActionListener(new java.awt.event.ActionListener()
		{
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e)
			{
				listener.actionPerformed(null);
			}
		});
		add(menuItem);
	}

	@Override
	public void popup(final Component component, final int x, final int y)
	{
		super.show((java.awt.Component) component, x, y);
	}
}
