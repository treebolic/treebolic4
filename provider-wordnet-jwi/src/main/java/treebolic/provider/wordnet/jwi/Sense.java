/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi;

import edu.mit.jwi.item.ISenseKey;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.POS;
import treebolic.annotations.NonNull;

/**
 * @author Bernard Bou
 */
public class Sense
{
	final public POS pos;

	final public ISenseKey sensekey;

	final public IWord sense;

	@NonNull
	final public Synset synset;

	@SuppressWarnings({"WeakerAccess", "unused"})
	final public int posIdx;

	@SuppressWarnings({"WeakerAccess", "unused"})
	final public int globalIdx;

	final public int senseNum;

	final public int tagCount;

	final public int lexId;

	/**
	 * Constructor
	 *
	 * @param pos       pos
	 * @param sensekey  sense key
	 * @param sense     sense
	 * @param synset    synset
	 * @param lexId     lex id
	 * @param posIdx    pos sense index
	 * @param globalIdx global sense index
	 * @param senseNum  sense num
	 * @param tagCount  tag count
	 */
	public Sense(final POS pos, final ISenseKey sensekey, final IWord sense, @NonNull final ISynset synset, final int lexId, final int posIdx, final int globalIdx, final int senseNum, final int tagCount)
	{
		super();
		this.pos = pos;
		this.sensekey = sensekey;
		this.lexId = lexId;

		this.posIdx = posIdx;
		this.globalIdx = globalIdx;
		this.senseNum = senseNum;
		this.tagCount = tagCount;

		this.sense = sense;
		this.synset = new Synset(synset);
	}
}
