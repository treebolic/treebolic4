/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio.tree;

import java.util.List;

import treebolic.studio.Messages;
import treebolic.model.MenuItem;
import treebolic.model.Settings;

/**
 * Menu wrapper
 *
 * @author Bernard Bou
 */
public class MenuWrapper extends SettingsWrapper
{
	/**
	 * Menu
	 */
	public List<MenuItem> menu;

	/**
	 * Constructor
	 *
	 * @param menu     menu
	 * @param settings settings
	 */
	public MenuWrapper(final List<MenuItem> menu, final Settings settings)
	{
		super(settings);
		this.menu = menu;
	}

	@Override
	public String toString()
	{
		return Messages.getString("MenuWrapper.label");
	}
}
