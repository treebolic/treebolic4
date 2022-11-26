/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio.tree;

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

	@Override
	public String toString()
	{
		return Messages.getString("TopWrapper.label");
	}
}
