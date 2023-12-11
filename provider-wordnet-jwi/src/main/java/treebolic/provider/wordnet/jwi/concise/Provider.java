/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi.concise;

import java.io.IOException;

/**
 * Provider for WordNet
 *
 * @author Bernard Bou
 */
@SuppressWarnings("WeakerAccess")
public class Provider extends treebolic.provider.wordnet.jwi.condensed.Provider
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
