/**
 * Title : Treebolic
 * Description : Treebolic
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.glue.component;

import treebolic.glue.ActionListener;
import treebolic.glue.NotImplementedException;

/**
 * Popup context menu
 *
 * @author Bernard Bou
 */
public class PopupMenu implements treebolic.glue.iface.component.PopupMenu<Component, ActionListener>
{
	public enum ImageIndices
	{
		IMAGE_CANCEL, IMAGE_INFO, IMAGE_FOCUS, IMAGE_LINK, IMAGE_MOUNT, IMAGE_GOTO, IMAGE_SEARCH
	}

	/**
	 * Labels
	 * indexes are public
	 */
	static public String[] labels = null;

	/**
	 * Constructor
	 *
	 * @param handle Opaque handle required for component creation
	 */
	protected PopupMenu(final Object handle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void addItem(final String label, final int imageIndex, final ActionListener listener)
	{
		throw new NotImplementedException();
	}

	@Override
	public void popup(final Component component, final int x, final int y)
	{
		throw new NotImplementedException();
	}
}
