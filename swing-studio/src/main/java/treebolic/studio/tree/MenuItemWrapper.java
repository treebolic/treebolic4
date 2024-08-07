/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.tree;

import treebolic.model.MenuItem;
import treebolic.model.Settings;

/**
 * Menu item wrapper
 *
 * @author Bernard Bou
 */
public class MenuItemWrapper extends SettingsWrapper
{
	/**
	 * Menu item
	 */
	public final MenuItem menuItem;

	/**
	 * Constructor
	 *
	 * @param menuItem menu item
	 * @param settings settings
	 */
	public MenuItemWrapper(final MenuItem menuItem, final Settings settings)
	{
		super(settings);
		this.menuItem = menuItem;
	}
}
