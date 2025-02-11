/**
 * Title : Treebolic
 * Description: Treebolic
 * Version: 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 * Update : Dec 6, 2014
 */

package treebolic.wordnet.browser;

import treebolic.annotations.NonNull;

/**
 * @author Bernard Bou
 */
public enum ColorReference
{
	// @formatter:off
	/** Background color */ BACKCOLOR("backcolor"), /** Root background color  */ ROOTBGCOLOR("root_bcolor"), /** Root foreground color */ ROOTFGCOLOR("root_fcolor"),
	/** Category background color */ CATEGORYBGCOLOR("category_bcolor"), /** Category foreground color */ CATEGORYFGCOLOR("category_fcolor"), /** Category edge color */ CATEGORYEDGECOLOR("category_ecolor"),
	/** POS background color */ POSBGCOLOR("pos_bcolor"), /** POS foreground color */ POSFGCOLOR("pos_fcolor"), /** POS edge color */ POSEDGECOLOR("pos_ecolor"),
	/** Sense background color */ SENSEBGCOLOR("sense_bcolor"), /** Sense foreground color */ SENSEFGCOLOR("sense_fcolor"), /** Sense edge color */ SENSEEDGECOLOR("sense_ecolor"),
	// /** Synset background color */ SYNSETBGCOLOR("synset_bcolor"),
	// /** Synset foreground color */ SYNSETFGCOLOR("synset_fcolor"),
	/** Synset edge color */ SYNSETEDGECOLOR("synset_ecolor"),
	// /** Relations background color */ RELATIONSBGCOLOR("relations_bcolor"),
	// /** Relations foreground color */ RELATIONSFGCOLOR("relations_fcolor","),
	/** Relations edge color */ RELATIONSEDGECOLOR("relations_ecolor"),
	// /** Semantic Relation background color */ SEMRELATIONBGCOLOR("sem_relation_bcolor"),
	// /** Semantic Relation foreground color */ SEMRELATIONFGCOLOR("sem_relation_fcolor"),
	/** Semantic Relation edge color */ SEMRELATIONEDGECOLOR("sem_relation_ecolor"),
	// /** Lexical Relation background color */ LEXRELATIONBGCOLOR("lex_relation_bcolor"),
	// /** Lexical Relation foreground color */ LEXRELATIONFGCOLOR("lex_relation_fcolor"),
	/** Lexical Relation edge color */ LEXRELATIONEDGECOLOR("lex_relation_ecolor"),
	// /** Words background color */ WORDSBGCOLOR("words_bcolor"),
	// /** Words foreground color */ WORDSFGCOLOR("words_fcolor"),
	/** Words edge color */ WORDSEDGECOLOR("words_ecolor"),
	/** Word background color */ WORDBGCOLOR("word_bcolor"), /** Word foreground color */ WORDFGCOLOR("word_fcolor"), /** Word edge color */ WORDEDGECOLOR("word_ecolor"),
	/** Stem background color */ STEMBGCOLOR("stem_bcolor"), /** Stem foreground color */ STEMFGCOLOR("stem_fcolor"), /** Stem edge color */ STEMEDGECOLOR("stem_ecolor");
	// @formatter:on

	/**
	 * Key
	 */
	public final String key;

	/**
	 * Get label
	 *
	 * @return label
	 */
	@NonNull
	public final String getLabel()
	{
		return Messages.getString("ColorReference." + this.key);
	}

	ColorReference(final String key)
	{
		this.key = key;
	}
}
