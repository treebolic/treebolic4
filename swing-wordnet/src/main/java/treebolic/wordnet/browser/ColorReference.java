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
	// /** Links background color */ LINKSBGCOLOR("links_bcolor"),
	// /** Links foreground color */ LINKSFGCOLOR("links_fcolor","),
	/** Links edge color */ LINKSEDGECOLOR("links_ecolor"),
	// /** Link background color */ LINKBGCOLOR("link_bcolor"),
	// /** Link foreground color */ LINKFGCOLOR("link_fcolor"),
	/** Link edge color */ LINKEDGECOLOR("link_ecolor"),
	// /** Members background color */ MEMBERSBGCOLOR("members_bcolor"),
	// /** Members foreground color */ MEMBERSFGCOLOR("members_fcolor"),
	/** Members edge color */ MEMBERSEDGECOLOR("members_ecolor"),
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
	public final String getLabel()
	{
		return Messages.getString("ColorReference." + this.key);
	}

	ColorReference(final String key)
	{
		this.key = key;
	}
}
