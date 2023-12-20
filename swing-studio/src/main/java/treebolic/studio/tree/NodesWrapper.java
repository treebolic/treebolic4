/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.tree;

import treebolic.annotations.NonNull;
import treebolic.studio.Messages;
import treebolic.model.Settings;

/**
 * Nodes wrapper
 *
 * @author Bernard Bou
 */
public class NodesWrapper extends SettingsWrapper
{
	/**
	 * Constructor
	 *
	 * @param settings settings
	 */
	public NodesWrapper(final Settings settings)
	{
		super(settings);
	}

	@NonNull
	@Override
	public String toString()
	{
		return Messages.getString("NodesWrapper.label");
	}
}
