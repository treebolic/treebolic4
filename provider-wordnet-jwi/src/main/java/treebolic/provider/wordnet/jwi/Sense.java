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
 * Sense wrapper
 *
 * @author Bernard Bou
 */
public class Sense
{
	/**
	 * Part of speech
	 */
	final public POS pos;

	/**
	 * Sensekey
	 */
	final public ISenseKey sensekey;

	/**
	 * Word
	 */
	final public IWord sense;

	/**
	 * Synset
	 */
	@NonNull
	final public Synset synset;

	/**
	 * POS index
	 */
	@SuppressWarnings({"WeakerAccess", "unused"})
	final public int posIdx;

	/**
	 * Global index
	 */
	@SuppressWarnings({"WeakerAccess", "unused"})
	final public int globalIdx;

	/**
	 * Sense num
	 */
	final public int senseNum;

	/**
	 * Tag count
	 */
	final public int tagCount;

	/**
	 * Lexid
	 */
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
