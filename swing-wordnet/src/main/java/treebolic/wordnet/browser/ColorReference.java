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
	BACKCOLOR("backcolor"), ROOTBGCOLOR("root_bcolor"), ROOTFGCOLOR("root_fcolor"), CATEGORYBGCOLOR("category_bcolor"), CATEGORYFGCOLOR("category_fcolor"), CATEGORYEDGECOLOR("category_ecolor"), POSBGCOLOR("pos_bcolor"), POSFGCOLOR("pos_fcolor"), POSEDGECOLOR("pos_ecolor"), SENSEBGCOLOR("sense_bcolor"), SENSEFGCOLOR("sense_fcolor"), SENSEEDGECOLOR("sense_ecolor"), // SYNSETBGCOLOR("synset_bcolor"),
// SYNSETFGCOLOR("synset_fcolor"),
SYNSETEDGECOLOR("synset_ecolor"), // LINKSBGCOLOR("links_bcolor"),
// LINKSFGCOLOR("links_fcolor","),
LINKSEDGECOLOR("links_ecolor"), // LINKBGCOLOR("link_bcolor"),
// LINKFGCOLOR("link_fcolor"),
LINKEDGECOLOR("link_ecolor"), // MEMBERSBGCOLOR("members_bcolor"),
// MEMBERSFGCOLOR("members_fcolor"),
MEMBERSEDGECOLOR("members_ecolor"), WORDBGCOLOR("word_bcolor"), WORDFGCOLOR("word_fcolor"), WORDEDGECOLOR("word_ecolor"), STEMBGCOLOR("stem_bcolor"), STEMFGCOLOR("stem_fcolor"), STEMEDGECOLOR("stem_ecolor");

	public final String key;

	public final String getLabel()
	{
		return Messages.getString("ColorReference." + this.key);
	}

	private ColorReference(final String key)
	{
		this.key = key;
	}
}
