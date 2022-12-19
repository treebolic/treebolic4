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
public enum LinkReference
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

	/** Entails */ ENTAILS("entail"), //
	/** Is entailed by */ IS_ENTAILED_BY("entailed"), //
	/** Causes */ CAUSES("cause"), //
	/** Is caused by */ IS_CAUSED_BY("caused"), //

	/** Similar to */ SIMILAR_TO("similar"), //
	/** Also see */ ALSO_SEE("alsosee"), //
	/** Attribute */ ATTRIBUTE("attribute"), //
	/** Pertainym */ PERTAINYM("pertainym"), //
	/** Derivationally related */ DERIVATIONALLY_RELATED("derivation"), //
	/** Derived from adjective */ DERIVED_FROM_ADJ("adjderived"), //

	/** Verb group */ VERB_GROUP("verbgroup"), //
	/** Participle */ PARTICIPLE("participle"), //

	/** Domain */ DOMAIN("domain"), //
	/** Topic */ TOPIC(null), //
	/** Usage */ USAGE(null), //
	/** Region */ REGION(null), //

	/** Member */ MEMBER("member"), //
	/** Topic member */ TOPIC_MEMBER(null), //
	/** Usage member */ USAGE_MEMBER(null), //
	/** Region member */ REGION_MEMBER(null); //
	// @formatter:off

	private final String helpKey;

	LinkReference(final String helpKey)
	{
		this.helpKey = helpKey;
	}

	/**
	 * Get label
	 * @return label
	 */
	public String getLabel()
	{
		return Messages.getString("LinkReference." + this.name().toLowerCase());
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
	 * @param refs link references
	 * @return array of labels
	 */
	public static String[] getLabels(final LinkReference... refs)
	{
		String[] strings = new String[refs.length];
		int i = 0;
		for (LinkReference ref : refs)
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
		for (final LinkReference r : LinkReference.values())
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
				IS_ENTAILED_BY.mask() | //
				CAUSES.mask() | //
				IS_CAUSED_BY.mask() | //
				SIMILAR_TO.mask();
	}

	/**
	 * Key to relation filter
	 */
	static public final String KEYRELATIONFILTER = "relation_filter";
}
