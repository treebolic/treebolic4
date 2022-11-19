/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.tree;

import treebolic.generator.Messages;
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
	public Settings settings;

	/**
	 * Constructor
	 *
	 * @param settings
	 *        settings
	 */
	protected SettingsWrapper(final Settings settings)
	{
		this.settings = settings;
	}

	@Override
	public String toString()
	{
		return Messages.getString("SettingsWrapper.label"); //$NON-NLS-1$
	}
}
