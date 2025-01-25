/*
 * Copyright (c) 2019-2025. Bernard Bou
 */

package treebolic.provider.wordnet.kwi.full;

import treebolic.annotations.NonNull;
import treebolic.model.Settings;
import treebolic.provider.IProvider;
import treebolic.provider.wordnet.kwi.BaseProvider;

import java.io.IOException;

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
		@NonNull final Settings settings = super.makeSettings(childrenCount, 3);
		settings.sweep = 1.F;
		settings.expansion = 1.F;
		return settings;
	}
}
