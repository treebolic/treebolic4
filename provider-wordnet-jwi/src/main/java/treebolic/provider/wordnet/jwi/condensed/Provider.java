/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi.condensed;

import java.io.IOException;

import treebolic.annotations.NonNull;
import treebolic.model.Settings;

/**
 * Condensed provider for WordNet
 *
 * @author Bernard Bou
 */
public class Provider extends treebolic.provider.wordnet.jwi.simple.Provider
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
		this.features |= FEATURE_COLLAPSE_LINKS;
	}

	// S E T T I N G S

	@NonNull
	@Override
	protected Settings makeSettings(final int childrenCount)
	{
		return super.makeSettings(childrenCount, 2);
	}
}
