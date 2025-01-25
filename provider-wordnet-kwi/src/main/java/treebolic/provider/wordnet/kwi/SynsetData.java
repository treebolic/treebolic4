/*
 * Copyright (c) 2019-2025. Bernard Bou
 */

package treebolic.provider.wordnet.kwi;

import org.kwi.item.Synset;
import treebolic.annotations.NonNull;

/**
 * Synset wrapper
 *
 * @author Bernard Bou
 */
public class SynsetData
{
	/**
	 * Embedded synset
	 */
	@NonNull
	final public Synset synset;

	/**
	 * Synset gloss
	 */
	@NonNull
	final public Gloss gloss;

	/**
	 * Constructor
	 *
	 * @param synset synset
	 */
	public SynsetData(@NonNull final Synset synset)
	{
		super();

		this.synset = synset;
		this.gloss = new Gloss(synset.getGloss());
	}
}
