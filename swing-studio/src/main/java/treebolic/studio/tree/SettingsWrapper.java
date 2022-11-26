/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio.tree;

import treebolic.studio.Messages;
import treebolic.model.Settings;

/**
 * Settings wrapper
 *
 * @author Bernard Bou
 */
public class SettingsWrapper
{
	/**
	 * Settings
	 */
	public final Settings settings;

	/**
	 * Constructor
	 *
	 * @param settings settings
	 */
	protected SettingsWrapper(final Settings settings)
	{
		this.settings = settings;
	}

	@Override
	public String toString()
	{
		return Messages.getString("SettingsWrapper.label");
	}
}
