/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.tree;

import treebolic.generator.Messages;
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

	@Override
	public String toString()
	{
		return Messages.getString("NodesWrapper.label");
	}
}
