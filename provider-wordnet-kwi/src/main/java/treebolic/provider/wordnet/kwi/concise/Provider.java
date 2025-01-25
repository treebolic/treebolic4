/*
 * Copyright (c) 2019-2025. Bernard Bou
 */

package treebolic.provider.wordnet.kwi.concise;

import java.io.IOException;

/**
 * Provider for WordNet
 *
 * @author Bernard Bou
 */
@SuppressWarnings("WeakerAccess")
public class Provider extends treebolic.provider.wordnet.kwi.condensed.Provider
{
	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @throws IOException io exception
	 */
	public Provider() throws IOException
	{
		super();
		this.features |= FEATURE_TYPEDRELATION_RAISE_RECURSE_AS_SIBLING;
	}
}
