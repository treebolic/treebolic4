/*
 * Copyright (c) 2019-2025. Bernard Bou
 */

package treebolic.provider.wordnet.kwi;

import org.kwi.item.POS;
import org.kwi.item.SenseKey;
import org.kwi.item.Synset;
import treebolic.annotations.NonNull;

/**
 * Sense wrapper
 *
 * @author Bernard Bou
 */
public class SenseData
{
	/**
	 * Part of speech
	 */
	final public POS pos;

	/**
	 * Sensekey
	 */
	final public SenseKey sensekey;

	/**
	 * Word
	 */
	final public Synset.Sense sense;

	/**
	 * Synset
	 */
	@NonNull
	final public SynsetData synset;

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
	public SenseData(final POS pos, final SenseKey sensekey, final Synset.Sense sense, @NonNull final Synset synset, final int lexId, final int posIdx, final int globalIdx, final int senseNum, final int tagCount)
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
		this.synset = new SynsetData(synset);
	}
}
