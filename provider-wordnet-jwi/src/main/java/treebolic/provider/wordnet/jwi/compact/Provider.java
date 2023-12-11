/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi.compact;

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
		this.features |= FEATURE_SYNSET_FORGET_MEMBERS_NODE | FEATURE_SYNSET_MERGE_SINGLE_MEMBER_TO_PARENT_IF_NOT_BASE_LEVEL | FEATURE_RELATEDSYNSET1_FORGET_RELATION_NODE | FEATURE_TYPEDRELATION_RAISE_SINGLE_MEMBER_TO_SYNSET | FEATURE_TYPEDRELATION_FORGET_RELATION_NODE;
	}
}
