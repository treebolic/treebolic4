/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi;

import edu.mit.jwi.item.ISynset;
import treebolic.annotations.NonNull;

/**
 * @author Bernard Bou
 */
public class Synset
{
	@NonNull
	final public ISynset synset;

	@NonNull
	final public Gloss gloss;

	/**
	 * Constructor
	 *
	 * @param synset synset
	 */
	public Synset(@NonNull final ISynset synset)
	{
		super();

		this.synset = synset;
		this.gloss = new Gloss(synset.getGloss());
	}
}
