/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio.tree;

import treebolic.annotations.NonNull;
import treebolic.studio.Messages;
import treebolic.model.Settings;

/**
 * Top element wrapper
 *
 * @author Bernard Bou
 */
public class TopWrapper extends SettingsWrapper
{
	/**
	 * Constructor
	 *
	 * @param settings settings
	 */
	public TopWrapper(final Settings settings)
	{
		super(settings);
	}

	@NonNull
	@Override
	public String toString()
	{
		return Messages.getString("TopWrapper.label");
	}
}
