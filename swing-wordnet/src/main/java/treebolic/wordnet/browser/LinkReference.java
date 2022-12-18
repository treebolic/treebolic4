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
	HYPERNYM("hypernym"), //
	HYPERNYM_INSTANCE(null), //
	HYPONYM("hyponym"), //
	HYPONYM_INSTANCE(null), //

	HOLONYM_MEMBER("holonym"), //
	HOLONYM_SUBSTANCE("holonym"), //
	HOLONYM_PART("holonym"), //

	MERONYM_MEMBER("meronym"), //
	MERONYM_SUBSTANCE("meronym"), //
	MERONYM_PART("meronym"), //

	ANTONYM("antonym"), //

	ENTAILS("entail"), //
	IS_ENTAILED_BY("entailed"), //
	CAUSES("cause"), //
	IS_CAUSED_BY("caused"), //

	SIMILAR_TO("similar"), //
	ALSO_SEE("alsosee"), //
	ATTRIBUTE("attribute"), //
	PERTAINYM("pertainym"), //
	DERIVATIONALLY_RELATED("derivation"), //
	DERIVED_FROM_ADJ("adjderived"), //

	VERB_GROUP("verbgroup"), //
	PARTICIPLE("participle"), //

	DOMAIN("domain"), //
	TOPIC(null), //
	USAGE(null), //
	REGION(null), //

	MEMBER("member"), //
	TOPIC_MEMBER(null), //
	USAGE_MEMBER(null), //
	REGION_MEMBER(null); //

	private final String helpKey;

	LinkReference(final String helpKey)
	{
		this.helpKey = helpKey;
	}

	public String getLabel()
	{
		return Messages.getString("LinkReference." + this.name().toLowerCase());
	}

	public String getHelpKey()
	{
		return this.helpKey;
	}

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

	public long mask()
	{
		return 1 << ordinal();
	}

	public boolean test(final long bitmap)
	{
		return (bitmap & (1 << ordinal())) != 0;
	}

	static public long all()
	{
		long m = 0;
		for (final LinkReference r : LinkReference.values())
		{
			m |= r.mask();
		}
		return m;
	}

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

	static public final String KEYRELATIONFILTER = "relation_filter";
}
