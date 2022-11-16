/**
 * Title : Treebolic
 * Description : Treebolic
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 * <p>
 * Update : Mon Mar 10 00:00:00 CEST 2008
 */
package treebolic.glue.component;

import treebolic.glue.NotImplementedException;

/**
 * Progress
 *
 * @author Bernard Bou
 */
public class Progress implements Component, treebolic.glue.iface.component.Progress
{
	/**
	 * Constructor
	 *
	 * @param handle Opaque handle required for component creation
	 */
	public Progress(final Object handle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void put(final String message, final boolean fail)
	{
		throw new NotImplementedException();
	}
}
