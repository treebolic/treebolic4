/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.tree;

import treebolic.generator.Messages;
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
	 * @param settings
	 *        settings
	 */
	public ToolsWrapper(final Settings settings)
	{
		super(settings);
	}
	
	@Override
	public String toString()
	{
		return Messages.getString("ToolsWrapper.label"); //$NON-NLS-1$
	}
}
