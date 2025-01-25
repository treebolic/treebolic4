/*
 * Copyright (c) 2019-2025. Bernard Bou
 */

package treebolic.provider.wordnet.kwi.condensed;

import treebolic.annotations.NonNull;
import treebolic.model.Settings;

import java.io.IOException;

/**
 * Condensed provider for WordNet
 *
 * @author Bernard Bou
 */
public class Provider extends treebolic.provider.wordnet.kwi.simple.Provider
{
	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @throws IOException io exception
	 */
	@SuppressWarnings("WeakerAccess")
	public Provider() throws IOException
	{
		super();
		this.features |= FEATURE_COLLAPSE_RELATIONS;
	}

	// S E T T I N G S

	@NonNull
	@Override
	protected Settings makeSettings(final int childrenCount)
	{
		return super.makeSettings(childrenCount, 2);
	}
}
