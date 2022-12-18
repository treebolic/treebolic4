/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi.full;

import java.io.IOException;

import treebolic.annotations.NonNull;
import treebolic.model.Settings;
import treebolic.provider.IProvider;
import treebolic.provider.wordnet.jwi.BaseProvider;

/**
 * Provider for WordNet
 *
 * @author Bernard Bou
 */
public class Provider extends BaseProvider implements IProvider
{
	/**
	 * Constructor
	 *
	 * @throws IOException io exception
	 */
	public Provider() throws IOException
	{
		super();
	}

	@NonNull
	@Override
	protected Settings makeSettings(int childrenCount)
	{
		final Settings settings = super.makeSettings(childrenCount, 3);
		settings.sweep = 1.F;
		settings.expansion = 1.F;
		return settings;
	}
}
