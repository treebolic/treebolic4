/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.tree;

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
	 * @param menuItem
	 *        menu item
	 * @param settings
	 *        settings
	 */
	public MenuItemWrapper(final MenuItem menuItem, final Settings settings)
	{
		super(settings);
		this.menuItem = menuItem;
	}
}
