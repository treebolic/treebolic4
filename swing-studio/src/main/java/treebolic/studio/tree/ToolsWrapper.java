/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.tree;

import treebolic.annotations.NonNull;
import treebolic.studio.Messages;
import treebolic.model.Settings;

/**
 * Tools wrapper
 *
 * @author Bernard Bou
 */
public class ToolsWrapper extends SettingsWrapper
{
	/**
	 * Constructor
	 *
	 * @param settings settings
	 */
	public ToolsWrapper(final Settings settings)
	{
		super(settings);
	}

	@NonNull
	@Override
	public String toString()
	{
		return Messages.getString("ToolsWrapper.label");
	}
}
