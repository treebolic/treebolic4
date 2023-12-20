/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.tree;

import treebolic.annotations.NonNull;
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

	@NonNull
	@Override
	public String toString()
	{
		return Messages.getString("SettingsWrapper.label");
	}
}
