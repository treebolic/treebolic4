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
public enum RelationReference
{
	// @formatter:off
	/** Hypernym */ HYPERNYM("hypernym"), //
	/** Hypernym instance */ HYPERNYM_INSTANCE(null), //
	/** Hyponym */ HYPONYM("hyponym"), //
	/** Hyponym instance */ HYPONYM_INSTANCE(null), //

	/** Member holonym */ HOLONYM_MEMBER("holonym"), //
	/** Substance holonym */ HOLONYM_SUBSTANCE("holonym"), //
	/** Part holonym */ HOLONYM_PART("holonym"), //

	/** Member meronym */ MERONYM_MEMBER("meronym"), //
	/** Substance meronym */ MERONYM_SUBSTANCE("meronym"), //
	/** Part meronym */ MERONYM_PART("meronym"), //

	/** Antonym */ ANTONYM("antonym"), //

	/** Entails */ ENTAILS("entails"), //
	/** Is entailed by */ ENTAILED("entailed"), //
	/** Causes */ CAUSES("causes"), //
	/** Is caused by */ CAUSED("caused"), //

	/** Similar to */ SIMILAR("similar"), //
	/** Also see */ ALSO("also"), //
	/** Attribute */ ATTRIBUTE("attribute"), //
	/** Pertainym */ PERTAINYM("pertainym"), //
	/** Derivationally related */ DERIVATION("derivation"), //
	/** Derived from adjective */ DERIVATION_ADJ("derivation_adj"), //

	/** Verb group */ VERB_GROUP("verbgroup"), //
	/** Participle */ PARTICIPLE("participle"), //

	/** Domain */ DOMAIN("domain"), //
	/** Domain Topic */ DOMAIN_TOPIC(null), //
	/** Domain Usage */ DOMAIN_USAGE(null), //
	/** Domain Region */ DOMAIN_REGION(null), //

	/** Has domain */ HASDOMAIN("hasdomain"), //
	/** Has domain Topic member */ HASDOMAIN_TOPIC(null), //
	/** Has domain Usage */ HASDOMAIN_USAGE(null), //
	/** Has domain Region */ HASDOMAIN_REGION(null); //
	// @formatter:off

	private final String helpKey;

	RelationReference(final String helpKey)
	{
		this.helpKey = helpKey;
	}

	/**
	 * Get label
	 * @return label
	 */
	@NonNull public String getLabel()
	{
		return Messages.getString("RelationReference." + this.name().toLowerCase());
	}

	/**
	 * Get key to help
	 *
	 * @return help key
	 */
	public String getHelpKey()
	{
		return this.helpKey;
	}

	/**
	 * Get labels
	 *
	 * @param refs relation references
	 * @return array of labels
	 */
	@NonNull public static String[] getLabels(@NonNull final RelationReference... refs)
	{
		@NonNull String[] strings = new String[refs.length];
		int i = 0;
		for (@NonNull RelationReference ref : refs)
		{
			strings[i] = ref.getLabel();
			i++;
		}
		return strings;
	}

	/**
	 * Mask
	 *
	 * @return mask value
	 */
	public long mask()
	{
		return 1 << ordinal();
	}

	/**
	 * Test if bits are set
	 *
	 * @param bitmap bits
	 * @return true if relevant bits are set
	 */
	public boolean test(final long bitmap)
	{
		return (bitmap & (1 << ordinal())) != 0;
	}

	/**
	 * All filter
	 * @return All filter
	 */
	static public long all()
	{
		long m = 0;
		for (@NonNull final RelationReference r : RelationReference.values())
		{
			m |= r.mask();
		}
		return m;
	}

	/**
	 * Base-line filter
	 * @return base-line filter
	 */
	static public long baseline()
	{
		return HYPERNYM.mask() | //
				HYPONYM.mask() | //
				HOLONYM_MEMBER.mask() | //
				HOLONYM_SUBSTANCE.mask() | //
				HOLONYM_PART.mask() | //
				MERONYM_MEMBER.mask() | //
				MERONYM_SUBSTANCE.mask() | //
				MERONYM_PART.mask() | //
				ANTONYM.mask() | //
				ENTAILS.mask() | //
				ENTAILED.mask() | //
				CAUSES.mask() | //
				CAUSED.mask() | //
				SIMILAR.mask();
	}

	/**
	 * Key to relation filter
	 */
	static public final String KEYRELATIONFILTER = "relation_filter";
}
