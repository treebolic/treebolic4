/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.IStemmer;
import edu.mit.jwi.morph.WordnetStemmer;
import treebolic.ILocator;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.iface.Colors;
import treebolic.model.*;
import treebolic.model.MenuItem.Action;
import treebolic.model.Types.MatchMode;
import treebolic.model.Types.MatchScope;
import treebolic.provider.IProvider;
import treebolic.provider.IProviderContext;
import treebolic.provider.LoadBalancer;

/**
 * Base provider for WordNet
 *
 * @author Bernard Bou
 */
@SuppressWarnings({"SameParameterValue"})
public abstract class BaseProvider implements IProvider, ImageDecorator
{
	// F E A T U R E S

	static protected final long FEATURE_COLLAPSE_CATEGORIES = 0x00000001; // forget 'category' node

	static protected final long FEATURE_COLLAPSE_LINKS = 0x00000002; // forget 'links' node

	static protected final long FEATURE_SYNSET_FORGET_MEMBERS_NODE = 0x00000010; // attach all members to parent without 'members' node

	static protected final long FEATURE_SYNSET_FORGET_MEMBERS_NODE_IF_SINGLE_MEMBER = 0x00000020; // attach single member to parent without 'members' node

	static protected final long FEATURE_SYNSET_MERGE_SINGLE_MEMBER_TO_PARENT_IF_NOT_BASE_LEVEL = 0x00000040; // raise and coalesce single member with parent

	static protected final long FEATURE_TYPEDLINK_RAISE_SINGLE_MEMBER_TO_SYNSET = 0x00000100;

	static protected final long FEATURE_TYPEDLINK_FORGET_LINK_NODE = 0x00000200;

	static protected final long FEATURE_TYPEDLINK_RAISE_RECURSE_AS_SIBLING = 0x00000400;

	static protected final long FEATURE_LINKEDSYNSET1_FORGET_LINK_NODE = 0x00001000; // attach linked synset to parent without 'link' node

	static protected final long FEATURE_SEMLINKS_MERGE_SINGLE_LINKED_SYNSET_TO_LINK = 0x00002000; // raise and coalesce single linked synset with 'link' node

	static protected final long FEATURE_MULTILINE_RAISE_MEMBERS_TO_SYNSET = 0x10000000; // multiple line label

	static protected final long FEATURE_MULTILINE_RAISE_MEMBERS_TO_LINKED_SYNSET = 0x20000000; // multiple line label

	// L A B E L S

	/**
	 * Label category
	 */
	static public final String LABEL_CATEGORY = "category";

	/**
	 * Label sense
	 */
	static public final String LABEL_SENSE = "sense";

	/**
	 * Label synset
	 */
	static public final String LABEL_SYNSET = "synset";

	/**
	 * Label links
	 */
	static public final String LABEL_LINKS = "links";

	/**
	 * Label words
	 */
	static public final String LABEL_MEMBERS = "words";

	/**
	 * Label word
	 */
	static public final String LABEL_MEMBER = "word";

	/**
	 * Label stem
	 */
	static public final String LABEL_STEM = "stem";

	// E D G E S T Y L E

	/**
	 * Edge style
	 */
	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_TREE = IEdge.SOLID | IEdge.FROMTRIANGLE | IEdge.FROMFILL;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_MEMBER = IEdge.DASH | IEdge.STROKEDEF | IEdge.TOCIRCLE | IEdge.TOFILL | IEdge.TODEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_MEMBERS = IEdge.SOLID | IEdge.STROKEDEF | IEdge.TOCIRCLE | IEdge.TOFILL | IEdge.TODEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_SYNSET = IEdge.SOLID | IEdge.STROKEDEF | IEdge.TOCIRCLE | IEdge.TOFILL | IEdge.TODEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_LINK = IEdge.SOLID | IEdge.STROKEDEF | IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.TODEF | IEdge.FROMDEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_LEX = IEdge.SOLID | IEdge.STROKEDEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_LINKS = IEdge.SOLID | IEdge.STROKEDEF | IEdge.FROMCIRCLE | IEdge.FROMFILL | IEdge.FROMDEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_ETC = IEdge.DOT | IEdge.STROKEDEF | IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.TODEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_SENSE = IEdge.SOLID | IEdge.STROKEDEF | IEdge.FROMTRIANGLE | IEdge.FROMFILL | IEdge.FROMDEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_POS = IEdge.SOLID | IEdge.STROKEDEF | IEdge.FROMTRIANGLE | IEdge.FROMFILL | IEdge.FROMDEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_CATEGORY = IEdge.SOLID | IEdge.STROKEDEF | IEdge.FROMTRIANGLE | IEdge.FROMFILL | IEdge.FROMDEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_STEM = IEdge.DOT | IEdge.STROKEDEF | IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.TODEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_EDGE = IEdge.DOT | IEdge.STROKEDEF | IEdge.TODEF | IEdge.TOTRIANGLE | IEdge.TOFILL;

	// L I N K S

	/**
	 * WordNet link/relation
	 */
	public enum Link
	{
		// @formatter:off
		/** Hypernym */ HYPERNYM(Pointer.HYPERNYM, "+ generic", "hypernym", ImageIndex.HYPERNYM.ordinal(), true), //
		/** Hypernym instance */ HYPERNYM_INSTANCE(Pointer.HYPERNYM_INSTANCE, "hypernym instance", "hypernym", ImageIndex.HYPERNYM_INSTANCE.ordinal(), false), //
		/** Hyponym */ HYPONYM(Pointer.HYPONYM, "+ specific", "hyponym", ImageIndex.HYPONYM.ordinal(), true), //
		/** Hyponym instance */ HYPONYM_INSTANCE(Pointer.HYPONYM_INSTANCE, "hyponym instance", "hyponym", ImageIndex.HYPONYM_INSTANCE.ordinal(), false), //

		/** Member holonym */ HOLONYM_MEMBER(Pointer.HOLONYM_MEMBER, "is member of", "holonym", ImageIndex.HOLONYM_MEMBER.ordinal(), true), //
		/** Substance holonym */ HOLONYM_SUBSTANCE(Pointer.HOLONYM_SUBSTANCE, "is substance of", "holonym", ImageIndex.HOLONYM_SUBSTANCE.ordinal(), true), //
		/** Part holonym */ HOLONYM_PART(Pointer.HOLONYM_PART, "is part of", "holonym", ImageIndex.HOLONYM_PART.ordinal(), true), //

		/** Member meronym */ MERONYM_MEMBER(Pointer.MERONYM_MEMBER, "member", "meronym", ImageIndex.MERONYM_MEMBER.ordinal(), true), //
		/** Substance meronym */ MERONYM_SUBSTANCE(Pointer.MERONYM_SUBSTANCE, "substance", "meronym", ImageIndex.MERONYM_SUBSTANCE.ordinal(), true), //
		/** Part meronym */ MERONYM_PART(Pointer.MERONYM_PART, "part", "meronym", ImageIndex.MERONYM_PART.ordinal(), true), //

		/** Antonym */ ANTONYM(Pointer.ANTONYM, "opposite", "antonym", ImageIndex.ANTONYM.ordinal(), false), //

		/** Entails */ ENTAILS(Pointer.ENTAILMENT, "entails", "entail", ImageIndex.ENTAILMENT.ordinal(), true), //
		/** Is entailed by */ IS_ENTAILED_BY(Pointer.IS_ENTAILED, "is entailed by", "entailed", ImageIndex.IS_ENTAILED_BY.ordinal(), true), //
		/** Causes */ CAUSES(Pointer.CAUSE, "causes", "cause", ImageIndex.CAUSE.ordinal(), true), //
		/** Is caused by */ IS_CAUSED_BY(Pointer.IS_CAUSED, "is caused by", "caused", ImageIndex.IS_CAUSED_BY.ordinal(), true), //

		/** Similar to */ SIMILAR_TO(Pointer.SIMILAR_TO, "similar to", "similar", ImageIndex.SIMILAR_TO.ordinal(), false), //
		/** Also see */ ALSO_SEE(Pointer.ALSO_SEE, "also see", "alsosee", ImageIndex.ALSO_SEE.ordinal(), false), //
		/** Attribute */ ATTRIBUTE(Pointer.ATTRIBUTE, "attribute", "attribute", ImageIndex.ATTRIBUTE.ordinal(), false), //
		/** Pertainym */ PERTAINYM(Pointer.PERTAINYM, "pertains to", "pertainym", ImageIndex.PERTAINYM.ordinal(), false), //
		/** Derivationally related */ DERIVATIONALLY_RELATED(Pointer.DERIVATIONALLY_RELATED, "derivation", "derivation", ImageIndex.DERIVATIONALLY_RELATED.ordinal(), false), //
		/** Derived from adjective */ DERIVED_FROM_ADJ(Pointer.DERIVED_FROM_ADJ, "derived from", "adjderived", ImageIndex.DERIVED_FROM_ADJ.ordinal(), false), //

		/** Verb group */ VERB_GROUP(Pointer.VERB_GROUP, "verb group", "verbgroup", ImageIndex.VERB_GROUP.ordinal(), false), //
		/** Participle */ PARTICIPLE(Pointer.PARTICIPLE, "participle", "participle", ImageIndex.PARTICIPLE.ordinal(), false), //

		/** Domain */ DOMAIN(Pointer.DOMAIN, "domain", "domain", ImageIndex.DOMAIN.ordinal(), false), //
		/** Topic */ TOPIC(Pointer.TOPIC, "domain-topic", "domain", ImageIndex.TOPIC.ordinal(), false), //
		/** Usage */ USAGE(Pointer.USAGE, "domain-usage", "domain", ImageIndex.USAGE.ordinal(), false), //
		/** Region */ REGION(Pointer.REGION, "domain-region", "domain", ImageIndex.REGION.ordinal(), false), //

		/** Member */ MEMBER(Pointer.MEMBER, "domain member", "member", ImageIndex.MEMBER.ordinal(), false), //
		/** Topic member */ TOPIC_MEMBER(Pointer.TOPIC_MEMBER, "member-topic", "member", ImageIndex.TOPIC_MEMBER.ordinal(), false), //
		/** Usage member */ USAGE_MEMBER(Pointer.USAGE_MEMBER, "member-usage", "member", ImageIndex.USAGE_MEMBER.ordinal(), false), //
		/** Region member */ REGION_MEMBER(Pointer.REGION_MEMBER, "member-region", "member", ImageIndex.REGION_MEMBER.ordinal(), false); //
		// @formatter:on

		/**
		 * Pointer
		 */
		public final Pointer pointer;

		/**
		 * Label
		 */
		public final String label;

		/**
		 * Tag
		 */
		public final String tag;

		/**
		 * Image index
		 */
		public final int imageIndex;

		/**
		 * Recurses flag
		 */
		public final boolean recurses;

		/**
		 * Constructor
		 *
		 * @param pointer    pointer
		 * @param label      label
		 * @param tag        tag
		 * @param imageIndex image index
		 * @param recurses   recurses flag
		 */
		Link(final Pointer pointer, final String label, final String tag, final int imageIndex, final boolean recurses)
		{
			this.pointer = pointer;
			this.label = label;
			this.tag = tag;
			this.imageIndex = imageIndex;
			this.recurses = recurses;
		}

		/**
		 * Mask
		 *
		 * @return link mask
		 */
		public long mask()
		{
			return 1 << ordinal();
		}

		/**
		 * Test
		 *
		 * @param bitmap bits
		 * @return true if the link's bit is set
		 */
		@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "WeakerAccess"})
		public boolean test(final long bitmap)
		{
			return (bitmap & (1 << ordinal())) != 0;
		}

		/**
		 * Parse pointer for link
		 *
		 * @param pointer pointer
		 * @return link
		 */
		@NonNull
		static public Link valueOf(@NonNull final IPointer pointer)
		{
			final String symbol = pointer.getSymbol();
			switch (symbol.charAt(0))
			{
				case '@':
					if (symbol.length() > 1 && symbol.charAt(1) == 'i')
					{
						return Link.HYPERNYM_INSTANCE;
					}
					return Link.HYPERNYM;

				case '~':
					if (symbol.length() > 1 && symbol.charAt(1) == 'i')
					{
						return Link.HYPONYM_INSTANCE;
					}
					return Link.HYPONYM;

				case '#':
					if (symbol.length() > 1)
					{
						switch (symbol.charAt(1))
						{
							case 'm':
								return Link.HOLONYM_MEMBER;
							case 's':
								return Link.HOLONYM_SUBSTANCE;
							case 'p':
								return Link.HOLONYM_PART;
						}
					}
					break;

				case '%':
					if (symbol.length() > 1)
					{
						switch (symbol.charAt(1))
						{
							case 'm':
								return Link.MERONYM_MEMBER;
							case 's':
								return Link.MERONYM_SUBSTANCE;
							case 'p':
								return Link.MERONYM_PART;
						}
					}
					break;

				case '^':
					return Link.ALSO_SEE;

				case '!':
					return Link.ANTONYM;

				case '>':
					if (symbol.length() > 1 && symbol.charAt(1) == '^')
					{
						return Link.IS_CAUSED_BY;
					}
					return Link.CAUSES;

				case '*':
					if (symbol.length() > 1 && symbol.charAt(1) == '^')
					{
						return Link.IS_ENTAILED_BY;
					}
					return Link.ENTAILS;

				case '<':
					return Link.PARTICIPLE;

				case '$':
					return Link.VERB_GROUP;

				case '&':
					return Link.SIMILAR_TO;

				case '=':
					return Link.ATTRIBUTE;

				case '+':
					return Link.DERIVATIONALLY_RELATED;

				case '\\':
					if (pointer.getName().startsWith("Pertainym"))
					{
						return Link.PERTAINYM;
					}
					else if (pointer.getName().startsWith("Derived from adjective"))
					{
						return Link.DERIVED_FROM_ADJ;
					}
					break;

				case ';':
					if (symbol.length() > 1)
					{
						switch (symbol.charAt(1))
						{
							case 'r':
								return Link.REGION;
							case 'c':
								return Link.TOPIC;
							case 'u':
								return Link.USAGE;
						}
					}
					return Link.DOMAIN;

				case '-':
					if (symbol.length() > 1)
					{
						switch (symbol.charAt(1))
						{
							case 'r':
								return Link.REGION_MEMBER;

							case 'c':
								return Link.TOPIC_MEMBER;

							case 'u':
								return Link.USAGE_MEMBER;
						}
					}
					return Link.MEMBER;

				default:
			}
			throw new IllegalArgumentException(pointer.toString());
		}
	}

	// I M A G E S

	/**
	 * Image index
	 */
	public enum ImageIndex
	{
		// @formatter:off
		/** Root */ ROOT, /** POS */ POS, /** Category */ CATEGORY, /** Sense */ SENSE, /** Synset */ SYNSET,/** Members */  MEMBERS, /** Links */ LINKS, /** Word */ WORD,

		/** Hypernym */ HYPERNYM, //
		/** Hypernym instance */ HYPERNYM_INSTANCE, //
		/** Hyponym */ HYPONYM, //
		/** Hyponym instance */ HYPONYM_INSTANCE, //

		/** Member holonym */ HOLONYM_MEMBER, //
		/** Substance holonym */ HOLONYM_SUBSTANCE, //
		/** Part holonym */ HOLONYM_PART, //

		/** Member meronym */ MERONYM_MEMBER, //
		/** Substance meronym */ MERONYM_SUBSTANCE, //
		/** Part meronym */ MERONYM_PART, //

		/** Antonym */ ANTONYM, //

		/** Entails */ ENTAILMENT, //
		/** Is entailed by */ IS_ENTAILED_BY, //
		/** Causes */ CAUSE, //
		/** Is caused by */ IS_CAUSED_BY, //
		/** Verb group */ VERB_GROUP, //
		/** Participle */ PARTICIPLE, //

		/** Similar to */ SIMILAR_TO, //
		/** Also see */ ALSO_SEE, //
		/** Attribute */ ATTRIBUTE, //
		/** Pertainym */ PERTAINYM, //
		/** Derivationally related */ DERIVATIONALLY_RELATED, //
		/** Derived from adjective */ DERIVED_FROM_ADJ, //

		/** Domain */ DOMAIN, //
		/** Topic */ TOPIC, //
		/** Usage */ USAGE, //
		/** Region */ REGION, //

		/** Member */ MEMBER, //
		/** Topic member */ TOPIC_MEMBER, //
		/** Usage member */ USAGE_MEMBER, //
		/** Region member */ REGION_MEMBER, //
		// @formatter:on
	}

	@Nullable
	static protected final String[] images = new String[]{ //
			"focus.png", // FOCUS

			"pos.png", // POS
			"category.png", // CATEGORY
			"sense.png", // SENSE
			"synset.png", // SYNSET
			"members.png", // MEMBERS
			"links.png", // LINKS
			"item.png", // WORD

			"hypernym.png", // HYPERNYM
			"instance.hypernym.png", // HYPERNYM_INSTANCE
			"hyponym.png", // HYPONYM
			"instance.hyponym.png", // HYPONYM_INSTANCE

			"member.holonym.png", // HOLONYM_MEMBER
			"substance.holonym.png", // HOLONYM_SUBSTANCE
			"part.holonym.png", // HOLONYM_PART

			"member.meronym.png", // MERONYM_MEMBER
			"substance.meronym.png", // MERONYM_SUBSTANCE
			"part.meronym.png", // MERONYM_PART

			"antonym.png", // ANTONYM

			"entail.png", // ENTAILS
			"entailed.png", // ENTAILED
			"cause.png", // CAUSE
			"caused.png", // CAUSED
			"verb.group.png", // VERB_GROUP
			"participle.png", // PARTICIPLE

			"similar.png", // SIMILAR_TO
			"also.png", // ALSO_SEE
			"attribute.png", // ATTRIBUTE
			"pertainym.png", // PERTAINYM
			"derivation.png", // DERIVATIONALLY_RELATED
			"adjderived.png", // DERIVED_FROM_ADJ

			"domain.png", // DOMAIN
			"domain.category.png", // TOPIC
			"domain.usage.png", // USAGE
			"domain.region.png", // REGION

			"domain.member.png", // MEMBER
			"domain.member.category.png", // TOPIC_MEMBER
			"domain.member.usage.png", // USAGE_MEMBER
			"domain.member.region.png",// REGION_MEMBER
	};

	/**
	 * Image scaling
	 */
	static protected final float[] IMAGESCALER = new float[]{1.F, .95F, .90F, .85F, .80F, .75F, .70F, .65F, .60F, .55F, .50F, .40F, .30F, .20F};

	// F O N T

	/**
	 * Font face
	 */
	static protected final String FONTFACE = "Serif";

	/**
	 * Font scaling
	 */
	static protected final float[] FONTSCALER = new float[]{1.F, .90F, .80F, .70F, .60F, .55F, .525F, .50F};

	static protected final float[] FONTSCALER_FAST = new float[]{1.F, .85F, .70F, .60F, .50F, .45F, .40F, .35F};

	/**
	 * Max char when synset members
	 */
	static private final int MAXCHARS = 20;

	// C O L O R S

	/**
	 * Background index
	 */
	static public final int BACK_IDX = 0;

	/**
	 * Tree edge index
	 */
	static public final int TREEEDGE_IDX = 1;

	/**
	 * Root background index
	 */
	static public final int ROOTBACKGROUND_IDX = 2;

	/**
	 * Root foreground index
	 */
	static public final int ROOTFOREGROUND_IDX = 3;

	/**
	 * Category background index
	 */
	static public final int CATEGORYBACKGROUND_IDX = 4;

	/**
	 * Category foreground index
	 */
	static public final int CATEGORYFOREGROUND_IDX = 5;

	/**
	 * Category edge index
	 */
	static public final int CATEGORYEDGE_IDX = 6;

	/**
	 * POS background index
	 */
	static public final int POSBACKGROUND_IDX = 7;

	/**
	 * POS foreground index
	 */
	static public final int POSFOREGROUND_IDX = 8;

	/**
	 * POS edge index
	 */
	static public final int POSEDGE_IDX = 9;

	/**
	 * Sense background index
	 */
	static public final int SENSEBACKGROUND_IDX = 10;

	/**
	 * Sense foreground index
	 */
	static public final int SENSEFOREGROUND_IDX = 11;

	/**
	 * Sense edge index
	 */
	static public final int SENSEEDGE_IDX = 12;

	/**
	 * Synset background index
	 */
	static public final int SYNSETBACKGROUND_IDX = 13;

	/**
	 * Synset foreground index
	 */
	static public final int SYNSETFOREGROUND_IDX = 14;

	/**
	 * Synset edge index
	 */
	static public final int SYNSETEDGE_IDX = 15;

	/**
	 * Links background index
	 */
	static public final int LINKSBACKGROUND_IDX = 16;

	/**
	 * Links foreground index
	 */
	static public final int LINKSFOREGROUND_IDX = 17;

	/**
	 * Links edge index
	 */
	static public final int LINKSEDGE_IDX = 18;

	/**
	 * Link background index
	 */
	static public final int LINKBACKGROUND_IDX = 19;

	/**
	 * Link foreground index
	 */
	static public final int LINKFOREGROUND_IDX = 20;

	/**
	 * Link edge index
	 */
	static public final int LINKEDGE_IDX = 21;

	/**
	 * Etc background index
	 */
	static public final int ETCBACKGROUND_IDX = 22;

	/**
	 * Etc foreground index
	 */
	static public final int ETCFOREGROUND_IDX = 23;

	/**
	 * Etc edge index
	 */
	static public final int ETCEDGE_IDX = 24;

	/**
	 * Stem background index
	 */
	static public final int STEMBACKGROUND_IDX = 25;

	/**
	 * Stem foreground index
	 */
	static public final int STEMFOREGROUND_IDX = 26;

	/**
	 * Stem edge index
	 */
	static public final int STEMEDGE_IDX = 27;

	/**
	 * Words background index
	 */
	static public final int WORDSBACKGROUND_IDX = 28;

	/**
	 * Words foreground index
	 */
	static public final int WORDSFOREGROUND_IDX = 29;

	/**
	 * Words edge index
	 */
	static public final int WORDSEDGE_IDX = 30;

	/**
	 * Word background index
	 */
	static public final int WORDBACKGROUND_IDX = 31;

	/**
	 * Word background1 index
	 */
	static public final int WORDBACKGROUND1_IDX = 32;

	/**
	 * Word background2 index
	 */
	static public final int WORDBACKGROUND2_IDX = 33;

	/**
	 * Word background3 index
	 */
	static public final int WORDBACKGROUND3_IDX = 34;

	/**
	 * Word background4 index
	 */
	static public final int WORDBACKGROUND4_IDX = 35;

	/**
	 * Word foreground index
	 */
	static public final int WORDFOREGROUND_IDX = 36;

	/**
	 * Word foreground1 index
	 */
	static public final int WORDFOREGROUND1_IDX = 37;

	/**
	 * Word foreground2 index
	 */
	static public final int WORDFOREGROUND2_IDX = 38;

	/**
	 * Word foreground3 index
	 */
	static public final int WORDFOREGROUND3_IDX = 39;

	/**
	 * Word foreground4 index
	 */
	static public final int WORDFOREGROUND4_IDX = 40;

	/**
	 * Word edge index
	 */
	static public final int WORDEDGE_IDX = 41;

	/**
	 * Edge index
	 */
	static public final int EDGE_IDX = 42;

	/**
	 * Count
	 */
	static public final int COLORS0_COUNT = 43;

	/**
	 * Initial colors
	 */
	static public final Integer[] COLORS0 = new Integer[COLORS0_COUNT];

	static
	{
		BaseProvider.COLORS0[BaseProvider.BACK_IDX] = 0xADC1CC; // 0xf3f1e2,0x9EB0BA
		BaseProvider.COLORS0[BaseProvider.TREEEDGE_IDX] = Colors.GRAY;
		BaseProvider.COLORS0[BaseProvider.ROOTBACKGROUND_IDX] = Colors.BLUE;
		BaseProvider.COLORS0[BaseProvider.ROOTFOREGROUND_IDX] = Colors.WHITE;
		BaseProvider.COLORS0[BaseProvider.CATEGORYBACKGROUND_IDX] = Colors.WHITE;
		BaseProvider.COLORS0[BaseProvider.CATEGORYFOREGROUND_IDX] = Colors.BLACK;
		BaseProvider.COLORS0[BaseProvider.CATEGORYEDGE_IDX] = Colors.BLACK;
		BaseProvider.COLORS0[BaseProvider.POSBACKGROUND_IDX] = Colors.WHITE;
		BaseProvider.COLORS0[BaseProvider.POSFOREGROUND_IDX] = Colors.BLUE;
		BaseProvider.COLORS0[BaseProvider.POSEDGE_IDX] = Colors.BLUE;
		BaseProvider.COLORS0[BaseProvider.SENSEBACKGROUND_IDX] = Colors.MAGENTA;
		BaseProvider.COLORS0[BaseProvider.SENSEFOREGROUND_IDX] = Colors.WHITE;
		BaseProvider.COLORS0[BaseProvider.SENSEEDGE_IDX] = Colors.MAGENTA;
		BaseProvider.COLORS0[BaseProvider.SYNSETBACKGROUND_IDX] = Colors.ORANGE;
		BaseProvider.COLORS0[BaseProvider.SYNSETFOREGROUND_IDX] = Colors.BLACK;
		BaseProvider.COLORS0[BaseProvider.SYNSETEDGE_IDX] = 0xFA8072;
		BaseProvider.COLORS0[BaseProvider.LINKSBACKGROUND_IDX] = Colors.RED;
		BaseProvider.COLORS0[BaseProvider.LINKSFOREGROUND_IDX] = Colors.WHITE;
		BaseProvider.COLORS0[BaseProvider.LINKSEDGE_IDX] = Colors.RED;
		BaseProvider.COLORS0[BaseProvider.LINKBACKGROUND_IDX] = Colors.RED;
		BaseProvider.COLORS0[BaseProvider.LINKFOREGROUND_IDX] = Colors.WHITE;
		BaseProvider.COLORS0[BaseProvider.LINKEDGE_IDX] = Colors.RED;
		BaseProvider.COLORS0[BaseProvider.ETCBACKGROUND_IDX] = Colors.ORANGE;
		BaseProvider.COLORS0[BaseProvider.ETCFOREGROUND_IDX] = Colors.GRAY;
		BaseProvider.COLORS0[BaseProvider.ETCEDGE_IDX] = Colors.ORANGE;
		BaseProvider.COLORS0[BaseProvider.STEMBACKGROUND_IDX] = Colors.RED;
		BaseProvider.COLORS0[BaseProvider.STEMFOREGROUND_IDX] = Colors.WHITE;
		BaseProvider.COLORS0[BaseProvider.STEMEDGE_IDX] = Colors.RED;
		BaseProvider.COLORS0[BaseProvider.WORDSBACKGROUND_IDX] = Colors.DARK_GRAY;
		BaseProvider.COLORS0[BaseProvider.WORDSFOREGROUND_IDX] = Colors.WHITE;
		BaseProvider.COLORS0[BaseProvider.WORDSEDGE_IDX] = Colors.YELLOW; // 0xFFCC00;

		BaseProvider.COLORS0[BaseProvider.WORDBACKGROUND_IDX] = Colors.ORANGE;
		BaseProvider.COLORS0[BaseProvider.WORDBACKGROUND1_IDX] = 0xFA8072;
		BaseProvider.COLORS0[BaseProvider.WORDBACKGROUND2_IDX] = 0xFF9686;
		BaseProvider.COLORS0[BaseProvider.WORDBACKGROUND3_IDX] = 0xFFB09D;
		BaseProvider.COLORS0[BaseProvider.WORDBACKGROUND4_IDX] = 0xFFCFB8;

		BaseProvider.COLORS0[BaseProvider.WORDFOREGROUND_IDX] = Colors.BLACK;
		BaseProvider.COLORS0[BaseProvider.WORDFOREGROUND1_IDX] = Colors.BLACK;
		BaseProvider.COLORS0[BaseProvider.WORDFOREGROUND2_IDX] = Colors.DARK_GRAY;
		BaseProvider.COLORS0[BaseProvider.WORDFOREGROUND3_IDX] = Colors.GRAY;
		BaseProvider.COLORS0[BaseProvider.WORDFOREGROUND4_IDX] = Colors.makeBrighter(Colors.GRAY);

		BaseProvider.COLORS0[BaseProvider.WORDEDGE_IDX] = Colors.YELLOW; // 0xFFCC00;

		BaseProvider.COLORS0[BaseProvider.EDGE_IDX] = Colors.MAGENTA; // 0xFFCC00;
	}

	// B E H A V I O U R

	/**
	 * Scheme prefix
	 */
	static public final String INTERNAL_URLSCHEME = "internal:";

	/**
	 * Scheme prefix
	 */
	static public final String URLSCHEME = "wordnet:";

	/**
	 * Scheme separator
	 */
	static public final char URLSCHEME_AT = '@';

	/**
	 * Data (WN31/OEWN/url)
	 */
	static private final String DATA_DEFAULT = "WN31";

	/**
	 * Max sibling links
	 */
	static private final int MAX_RELATED_DEFAULT = 32;

	/**
	 * Max recursion levels
	 */
	static private final int MAX_RECURSE_DEFAULT = 4;

	/**
	 * Expansion
	 */
	static protected final float EXPANSION = .8F;

	/**
	 * Sweep
	 */
	static protected final float SWEEP = 1.3F;

	/**
	 * Label ellipsize
	 */
	@Nullable
	static protected final Boolean ELLIPSIZE = null;

	/**
	 * Label max lines
	 */
	static protected final int LABEL_MAX_LINES = 0;

	/**
	 * Label extra line factor
	 */
	@Nullable
	static protected final Float LABEL_EXTRA_LINE_FACTOR = null;

	/**
	 * Label borders
	 */
	@Nullable
	static protected final Boolean BORDER = null;

	/**
	 * Relation filter default value
	 */
	@SuppressWarnings({"WeakerAccess"})
	static public final long FILTER_DEFAULT = Link.HYPERNYM.mask() | Link.HYPONYM.mask() | //
			Link.HOLONYM_MEMBER.mask() | Link.HOLONYM_SUBSTANCE.mask() | Link.HOLONYM_PART.mask() | //
			Link.MERONYM_MEMBER.mask() | Link.MERONYM_MEMBER.mask() | Link.MERONYM_MEMBER.mask() | //
			Link.CAUSES.mask() | Link.IS_CAUSED_BY.mask() | Link.ENTAILS.mask() | Link.IS_ENTAILED_BY.mask() | //
			Link.SIMILAR_TO.mask() | //
			Link.ANTONYM.mask();

	// L O A D B A L A N C I N G

	/**
	 * LoadBalancer : Members : Max children nodes at level 0, 1 .. n. Level 0 is just above leaves. Level > 0 is upward from leaves. Last value i holds for
	 * level i to n.
	 */
	static private final int[] MAX_MEMBERS_AT_LEVEL = {6, 3};

	/**
	 * LoadBalancer : Members : Truncation threshold
	 */
	static private final int MEMBERS_LABEL_TRUNCATE_AT = 3;

	/**
	 * LoadBalancer : Synsets : Max children nodes at level 0, 1 .. n. Level 0 is just above leaves. Level > 0 is upward from leaves. Last value i holds for
	 * level i to n.
	 */
	static private final int[] MAX_SEMLINKS_AT_LEVEL = {6, 3};

	/**
	 * LoadBalancer : Synsets : Truncation threshold
	 */
	static private final int SEMLINKS_LABEL_TRUNCATE_AT = 3;

	/*
	 * LoadBalancer : edge color
	 */
	// static private final Color LOADBALANCING_EDGE_COLOR = Color.WHITE;

	/**
	 * LoadBalancer : Edge style
	 */
	static private final int LOADBALANCING_EDGE_STYLE = IEdge.DOT | /* IEdge.FROMDEF | IEdge.FROMCIRCLE | */IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.STROKEDEF | IEdge.TODEF;

	// N O D E I D

	/**
	 * Randomize used to generate ids
	 */
	static private final Random randomizer = new Random();

	/**
	 * Make node id
	 *
	 * @return node id
	 */
	@NonNull
	static protected String makeNodeId()
	{
		return "@" + Long.toHexString(BaseProvider.randomizer.nextLong());
	}

	// C O M P A R A T O R S

	/**
	 * INode comparator
	 */
	@Nullable
	static protected final Comparator<INode> iNodeComparator = (n1, n2) -> {
		final String label1 = n1.getLabel();
		final String label2 = n2.getLabel();
		if (label1 != null && label2 != null)
		{
			return label1.compareTo(label2);
		}
		if (label1 == null)
		{
			return label2 == null ? 0 : -1;
		}
		return +1; // label2
		// is
		// null
	};

	/**
	 * Lexical file comparator
	 */

	static protected final Comparator<ILexFile> lexFileComparator = Comparator.comparing(ILexFile::getName);

	// M E M B E R S

	// data source

	/**
	 * Extended WordNet interface
	 */
	protected IDictionary dictionary;

	/**
	 * Stemmer
	 */
	protected IStemmer stemmer;

	// provider

	/**
	 * Provider client
	 */
	protected IProviderContext context;

	// data

	/**
	 * Data (WN31/OEWN)
	 */
	protected String data;

	// cache

	/**
	 * Cache
	 */
	protected File cache;

	// behaviour

	/**
	 * Features
	 */
	protected long features;

	/**
	 * Load balancing flag for members
	 */
	protected boolean loadBalanceMembers = true;

	/**
	 * Load balancing flag for semlinks
	 */
	protected boolean loadBalanceSemLinks = true;

	/**
	 * Members load balancer
	 */
	protected LoadBalancer membersLoadBalancer;

	/**
	 * Synsets load balancer
	 */
	protected LoadBalancer semLinksLoadBalancer;

	/**
	 * Font face
	 */
	@NonNull
	protected String fontFace = BaseProvider.FONTFACE;

	/**
	 * Font size factor
	 */
	@Nullable
	protected Float fontSizeFactor = null;

	/**
	 * Label max lines
	 */
	@Nullable
	protected Integer labelMaxLines = null;

	/**
	 * Sweep
	 */
	@Nullable
	protected Float expansion = null;

	/**
	 * Sweep
	 */
	@Nullable
	protected Float sweep = null;

	/**
	 * Relation filter
	 */
	protected long filter;

	/**
	 * Max links
	 */
	protected int maxLinks = BaseProvider.MAX_RELATED_DEFAULT;

	/**
	 * Max recursions
	 */
	protected int maxRecurse = BaseProvider.MAX_RECURSE_DEFAULT;

	// colors

	protected Integer backColor;

	protected Integer treeEdgeColor;

	protected Integer rootBackgroundColor;

	protected Integer rootForegroundColor;

	protected Integer categoryBackgroundColor;

	protected Integer categoryForegroundColor;

	protected Integer categoryEdgeColor;

	protected Integer posBackgroundColor;

	protected Integer posForegroundColor;

	protected Integer posEdgeColor;

	protected Integer senseBackgroundColor;

	protected Integer senseForegroundColor;

	protected Integer senseEdgeColor;

	protected Integer synsetBackgroundColor;

	protected Integer synsetForegroundColor;

	protected Integer synsetEdgeColor;

	protected Integer linksBackgroundColor;

	protected Integer linksForegroundColor;

	protected Integer linksEdgeColor;

	protected Integer linkBackgroundColor;

	protected Integer linkForegroundColor;

	protected Integer linkEdgeColor;

	protected Integer etcBackgroundColor;

	protected Integer etcForegroundColor;

	protected Integer etcEdgeColor;

	protected Integer wordsBackgroundColor;

	protected Integer wordsForegroundColor;

	protected Integer wordsEdgeColor;

	protected Integer wordEdgeColor;

	protected Integer stemBackgroundColor;

	protected Integer stemForegroundColor;

	protected Integer stemEdgeColor;

	protected final Integer[] wordForegroundColors = new Integer[]{ //
			BaseProvider.COLORS0[BaseProvider.WORDFOREGROUND_IDX], //
			BaseProvider.COLORS0[BaseProvider.WORDFOREGROUND1_IDX], //
			BaseProvider.COLORS0[BaseProvider.WORDFOREGROUND2_IDX], //
			BaseProvider.COLORS0[BaseProvider.WORDFOREGROUND3_IDX], //
			BaseProvider.COLORS0[BaseProvider.WORDFOREGROUND4_IDX], //
	};

	protected final Integer[] wordBackgroundColors = new Integer[]{ //
			BaseProvider.COLORS0[BaseProvider.WORDBACKGROUND_IDX], //
			BaseProvider.COLORS0[BaseProvider.WORDBACKGROUND1_IDX], //
			BaseProvider.COLORS0[BaseProvider.WORDBACKGROUND2_IDX], //
			BaseProvider.COLORS0[BaseProvider.WORDBACKGROUND3_IDX], //
			BaseProvider.COLORS0[BaseProvider.WORDBACKGROUND4_IDX], //
	};

	protected Integer edgeColor;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @throws IOException io exception
	 */
	@SuppressWarnings("RedundantThrows")
	protected BaseProvider() throws IOException
	{
		this.features = 0;
		this.filter = BaseProvider.FILTER_DEFAULT;
		this.membersLoadBalancer = new LoadBalancer(MAX_MEMBERS_AT_LEVEL, MEMBERS_LABEL_TRUNCATE_AT);
		this.membersLoadBalancer.setGroupNode(null, this.wordsBackgroundColor, this.wordsForegroundColor, this.wordsEdgeColor, LOADBALANCING_EDGE_STYLE, ImageIndex.MEMBERS.ordinal(), null, null);
		this.semLinksLoadBalancer = new LoadBalancer(MAX_SEMLINKS_AT_LEVEL, SEMLINKS_LABEL_TRUNCATE_AT);
		this.semLinksLoadBalancer.setGroupNode(null, this.linksBackgroundColor, this.linksForegroundColor, this.linksEdgeColor, LOADBALANCING_EDGE_STYLE, ImageIndex.SYNSET.ordinal(), null, null);
	}

	// P R O V I D E R

	@Override
	public void setLocator(@Nullable final ILocator locator)
	{
		if (locator == null)
		{
			return;
		}
		final URL base = locator.getBase();
		assert base != null;
		try
		{
			this.cache = new File(base.toURI());
		}
		catch (URISyntaxException ignored)
		{
			this.cache = new File(base.getPath());
		}
	}

	@Override
	public void setContext(final IProviderContext context)
	{
		this.context = context;
	}

	@SuppressWarnings("EmptyMethod")
	@Override
	public void setHandle(final Object handle)
	{
		// not needed
	}

	// S E T U P P A R A M E T E R S

	/**
	 * Setup parameters (direct configuration of provider)
	 *
	 * @param parameters parameters
	 */
	protected void setup(@NonNull final Properties parameters)
	{
		// data
		this.data = parameters.getProperty(Parameters.KEY_DATA, BaseProvider.DATA_DEFAULT);

		// filter
		this.filter = getLong(parameters, Parameters.KEY_RELATION_FILTER, BaseProvider.FILTER_DEFAULT);

		// max
		this.maxLinks = getInteger(parameters, Parameters.KEY_RELATED_MAX, BaseProvider.MAX_RELATED_DEFAULT);
		this.maxRecurse = getInteger(parameters, Parameters.KEY_RECURSE_MAX, BaseProvider.MAX_RECURSE_DEFAULT);

		// load balancing
		this.loadBalanceMembers = getBoolean(parameters, Parameters.KEY_LOADBALANCE_MEMBERS, true);
		this.loadBalanceSemLinks = getBoolean(parameters, Parameters.KEY_LOADBALANCE_SEMLINKS, true);

		// font
		this.fontFace = BaseProvider.FONTFACE;
		this.fontSizeFactor = getFloat(parameters, Parameters.KEY_FONTSIZE_FACTOR, -1F);

		// label max lines
		this.labelMaxLines = getInteger(parameters, Parameters.KEY_LABEL_MAX_LINES, BaseProvider.LABEL_MAX_LINES);

		// expansion/sweep
		this.expansion = getFloat(parameters, Parameters.KEY_EXPANSION, BaseProvider.EXPANSION);
		this.sweep = getFloat(parameters, Parameters.KEY_SWEEP, BaseProvider.SWEEP);

		// colors
		this.backColor = getColor(parameters, Parameters.KEY_BACKCOLOR, BaseProvider.COLORS0[BaseProvider.BACK_IDX]);
		this.treeEdgeColor = getColor(parameters, Parameters.KEY_TREE_ECOLOR, BaseProvider.COLORS0[BaseProvider.TREEEDGE_IDX]);

		this.rootBackgroundColor = getColor(parameters, Parameters.KEY_ROOT_BCOLOR, BaseProvider.COLORS0[BaseProvider.ROOTBACKGROUND_IDX]);
		this.rootForegroundColor = getColor(parameters, Parameters.KEY_ROOT_FCOLOR, BaseProvider.COLORS0[BaseProvider.ROOTFOREGROUND_IDX]);

		this.categoryBackgroundColor = getColor(parameters, Parameters.KEY_CATEGORY_BCOLOR, BaseProvider.COLORS0[BaseProvider.CATEGORYBACKGROUND_IDX]);
		this.categoryForegroundColor = getColor(parameters, Parameters.KEY_CATEGORY_FCOLOR, BaseProvider.COLORS0[BaseProvider.CATEGORYFOREGROUND_IDX]);
		this.categoryEdgeColor = getColor(parameters, Parameters.KEY_CATEGORY_ECOLOR, BaseProvider.COLORS0[BaseProvider.CATEGORYEDGE_IDX]);

		this.posBackgroundColor = getColor(parameters, Parameters.KEY_POS_BCOLOR, BaseProvider.COLORS0[BaseProvider.POSBACKGROUND_IDX]);
		this.posForegroundColor = getColor(parameters, Parameters.KEY_POS_FCOLOR, BaseProvider.COLORS0[BaseProvider.POSFOREGROUND_IDX]);
		this.posEdgeColor = getColor(parameters, Parameters.KEY_POS_ECOLOR, BaseProvider.COLORS0[BaseProvider.POSEDGE_IDX]);

		this.senseBackgroundColor = getColor(parameters, Parameters.KEY_SENSE_BCOLOR, BaseProvider.COLORS0[BaseProvider.SENSEBACKGROUND_IDX]);
		this.senseForegroundColor = getColor(parameters, Parameters.KEY_SENSE_FCOLOR, BaseProvider.COLORS0[BaseProvider.SENSEFOREGROUND_IDX]);
		this.senseEdgeColor = getColor(parameters, Parameters.KEY_SENSE_ECOLOR, BaseProvider.COLORS0[BaseProvider.SENSEEDGE_IDX]);

		this.synsetBackgroundColor = getColor(parameters, Parameters.KEY_SYNSET_BCOLOR, BaseProvider.COLORS0[BaseProvider.SYNSETBACKGROUND_IDX]);
		this.synsetForegroundColor = getColor(parameters, Parameters.KEY_SYNSET_FCOLOR, BaseProvider.COLORS0[BaseProvider.SYNSETFOREGROUND_IDX]);
		this.synsetEdgeColor = getColor(parameters, Parameters.KEY_SYNSET_ECOLOR, BaseProvider.COLORS0[BaseProvider.SYNSETEDGE_IDX]);

		this.linksBackgroundColor = getColor(parameters, Parameters.KEY_LINKS_BCOLOR, BaseProvider.COLORS0[BaseProvider.LINKSBACKGROUND_IDX]);
		this.linksForegroundColor = getColor(parameters, Parameters.KEY_LINKS_FCOLOR, BaseProvider.COLORS0[BaseProvider.LINKSFOREGROUND_IDX]);
		this.linksEdgeColor = getColor(parameters, Parameters.KEY_LINKS_ECOLOR, BaseProvider.COLORS0[BaseProvider.LINKSEDGE_IDX]);

		this.linkBackgroundColor = getColor(parameters, Parameters.KEY_LINK_BCOLOR, BaseProvider.COLORS0[BaseProvider.LINKBACKGROUND_IDX]);
		this.linkForegroundColor = getColor(parameters, Parameters.KEY_LINK_FCOLOR, BaseProvider.COLORS0[BaseProvider.LINKFOREGROUND_IDX]);
		this.linkEdgeColor = getColor(parameters, Parameters.KEY_LINK_ECOLOR, BaseProvider.COLORS0[BaseProvider.LINKEDGE_IDX]);

		this.stemBackgroundColor = getColor(parameters, Parameters.KEY_STEM_BCOLOR, BaseProvider.COLORS0[BaseProvider.STEMBACKGROUND_IDX]);
		this.stemForegroundColor = getColor(parameters, Parameters.KEY_STEM_FCOLOR, BaseProvider.COLORS0[BaseProvider.STEMFOREGROUND_IDX]);
		this.stemEdgeColor = getColor(parameters, Parameters.KEY_STEM_ECOLOR, BaseProvider.COLORS0[BaseProvider.STEMEDGE_IDX]);

		this.etcBackgroundColor = getColor(parameters, Parameters.KEY_ETC_BCOLOR, BaseProvider.COLORS0[BaseProvider.ETCBACKGROUND_IDX]);
		this.etcForegroundColor = getColor(parameters, Parameters.KEY_ETC_FCOLOR, BaseProvider.COLORS0[BaseProvider.ETCFOREGROUND_IDX]);
		this.etcEdgeColor = getColor(parameters, Parameters.KEY_ETC_ECOLOR, BaseProvider.COLORS0[BaseProvider.ETCEDGE_IDX]);

		this.wordsBackgroundColor = getColor(parameters, Parameters.KEY_MEMBERS_BCOLOR, BaseProvider.COLORS0[BaseProvider.WORDSBACKGROUND_IDX]);
		this.wordsForegroundColor = getColor(parameters, Parameters.KEY_MEMBERS_FCOLOR, BaseProvider.COLORS0[BaseProvider.WORDSFOREGROUND_IDX]);
		this.wordsEdgeColor = getColor(parameters, Parameters.KEY_MEMBERS_ECOLOR, BaseProvider.COLORS0[BaseProvider.WORDSEDGE_IDX]);

		// word colors
		final Integer wordBackgroundColor0 = getColor(parameters, Parameters.KEY_WORD_BCOLOR, null);
		if (wordBackgroundColor0 != null)
		{
			this.wordBackgroundColors[0] = wordBackgroundColor0;
		}
		final Integer wordForegroundColor0 = getColor(parameters, Parameters.KEY_WORD_FCOLOR, null);
		if (wordForegroundColor0 != null)
		{
			this.wordForegroundColors[0] = wordForegroundColor0;
		}

		// remote word colors
		if (this.wordBackgroundColors.length > 1)
		{
			final Integer remoteWordBackgroundColor = getColor(parameters, Parameters.KEY_LINKEDWORD_BCOLOR, null);
			// System.out.printf("new Color(0x%X),", remoteWordBackgroundColor.getRGB() & 0xFFFFFF);
			if (remoteWordBackgroundColor != null)
			{
				this.wordBackgroundColors[1] = remoteWordBackgroundColor;
				for (int i = 2; i < this.wordBackgroundColors.length; i++)
				{
					final Integer color = Colors.makeBrighter(this.wordBackgroundColors[i - 1]);
					// System.out.printf("new Color(0x%X), ", color.getRGB() & 0xFFFFFF);
					this.wordBackgroundColors[i] = color;
				}
			}
		}
		if (this.wordForegroundColors.length > 1)
		{
			final Integer remoteWordForegroundColor = getColor(parameters, Parameters.KEY_LINKEDWORD_FCOLOR, null);
			if (remoteWordForegroundColor != null)
			{
				this.wordForegroundColors[1] = remoteWordForegroundColor;
				for (int i = 2; i < this.wordForegroundColors.length; i++)
				{
					this.wordForegroundColors[i] = Colors.makeBrighter(this.wordForegroundColors[i - 1]);
				}
			}
		}

		// word edge
		this.wordEdgeColor = getColor(parameters, Parameters.KEY_WORD_ECOLOR, BaseProvider.COLORS0[BaseProvider.WORDEDGE_IDX]);

		this.edgeColor = getColor(parameters, Parameters.KEY_ECOLOR, BaseProvider.COLORS0[BaseProvider.EDGE_IDX]);

		// propagate to members load balancer
		this.membersLoadBalancer.setGroupNode(null, this.wordsBackgroundColor, this.wordsForegroundColor, this.wordsEdgeColor, LOADBALANCING_EDGE_STYLE, ImageIndex.MEMBERS.ordinal(), null, null);
		this.semLinksLoadBalancer.setGroupNode(null, this.linksBackgroundColor, this.linksForegroundColor, this.linksEdgeColor, LOADBALANCING_EDGE_STYLE, ImageIndex.SYNSET.ordinal(), null, null);
	}

	/**
	 * Get members load balancing
	 *
	 * @return members load balancing flag
	 */
	public boolean isLoadBalanceMembers()
	{
		return this.loadBalanceMembers;
	}

	/**
	 * Set members load balancing
	 *
	 * @param flag the members load balancing flag to set
	 */
	public void setLoadBalanceMembers(boolean flag)
	{
		this.loadBalanceMembers = flag;
	}

	/**
	 * Get semLinks load balancing
	 *
	 * @return semLinks load balancing flag
	 */
	public boolean isLoadBalanceSemLinks()
	{
		return this.loadBalanceSemLinks;
	}

	/**
	 * Set semLinks load balancing
	 *
	 * @param flag the semLinks load balancing flag to set
	 */
	public void setLoadBalanceSemLinks(boolean flag)
	{
		this.loadBalanceSemLinks = flag;
	}

	/**
	 * Parameter to color
	 *
	 * @param parameters   parameters
	 * @param key          parameter key
	 * @param defaultColor default value
	 * @return color value
	 */
	private Integer getColor(@NonNull final Properties parameters, final String key, final Integer defaultColor)
	{
		final String colorString = parameters.getProperty(key, null);
		if (colorString == null)
		{
			return defaultColor;
		}
		return Utils.parseColor(colorString);
	}

	/**
	 * Parameter to long
	 *
	 * @param parameters   parameters
	 * @param key          parameter key
	 * @param defaultValue default value
	 * @return long value
	 */
	private long getLong(@NonNull final Properties parameters, @SuppressWarnings("SameParameterValue") final String key, @SuppressWarnings("SameParameterValue") final long defaultValue)
	{
		final String valueString = parameters.getProperty(key, null);
		if (valueString != null)
		{
			if (valueString.startsWith("0x"))
			{
				try
				{
					return Long.valueOf(valueString.substring(2), 16);
				}
				catch (@NonNull final NumberFormatException ignored)
				{
					//
				}
			}
			else
			{
				try
				{
					return Long.parseLong(valueString);
				}
				catch (@NonNull final NumberFormatException ignored)
				{
					//
				}
			}
		}
		return defaultValue;
	}

	/**
	 * Parameter to int
	 *
	 * @param parameters   parameters
	 * @param key          parameter key
	 * @param defaultValue default value
	 * @return int value
	 */
	private int getInteger(@NonNull final Properties parameters, final String key, final int defaultValue)
	{
		final String valueString = parameters.getProperty(key, null);
		if (valueString == null)
		{
			return defaultValue;
		}
		try
		{
			return Integer.parseInt(valueString);
		}
		catch (@NonNull final NumberFormatException ignored)
		{
			try
			{
				return Integer.parseInt(valueString, 16);
			}
			catch (@NonNull final NumberFormatException ignored2)
			{
				//
			}
		}
		return defaultValue;
	}

	/**
	 * Parameter to boolean
	 *
	 * @param parameters   parameters
	 * @param key          parameter key
	 * @param defaultValue default value
	 * @return boolean value
	 */
	private boolean getBoolean(@NonNull final Properties parameters, final String key, final boolean defaultValue)
	{
		if (!parameters.containsKey(key))
		{
			return defaultValue;
		}
		try
		{
			final String valueString = parameters.getProperty(key, null);
			return Boolean.parseBoolean(valueString);
		}
		catch (@NonNull final NumberFormatException ignored)
		{
			//
		}
		return defaultValue;
	}

	/**
	 * Parameter to float
	 *
	 * @param parameters   parameters
	 * @param key          parameter key
	 * @param defaultValue default value
	 * @return float value
	 */
	private float getFloat(@NonNull final Properties parameters, final String key, final float defaultValue)
	{
		final String valueString = parameters.getProperty(key, null);
		if (valueString == null)
		{
			return defaultValue;
		}
		try
		{
			return Float.parseFloat(valueString);
		}
		catch (@NonNull final NumberFormatException ignored)
		{
			//
		}
		return defaultValue;
	}

	/*
	 * Parameter to string
	 * @param parameters parameters
	 * @param key parameter key
	 * @param defaultValue default value
	 * @return string value
	 */
	//@formatter:off
	//private String getString(final Properties parameters, @SuppressWarnings("SameParameterValue")final String key, @SuppressWarnings("SameParameterValue")final String defaultValue)
	//{
	//	final String valueString = parameters.getProperty(key, null);
	//	return valueString == null || valueString.isEmpty() ? defaultValue : valueString;
	//}
	//@formatter:on

	// I N T E R F A C E

	@Nullable
	@Override
	public Model makeModel(final String source, final URL base, @NonNull final Properties parameters)
	{
		// settings properties
		setup(parameters);

		// dictionary
		try
		{
			if (this.dictionary == null)
			{
				// construct the dictionary object and open it
				this.dictionary = new Dictionary(DataManager.getInstance().getDataDir(this.data, this.cache));
			}
			if (!this.dictionary.isOpen())
			{
				this.dictionary.open();
			}
		}
		catch (@NonNull final Exception ignored)
		{
			return null;
		}

		// stemmer
		if (this.stemmer == null)
		{
			this.stemmer = new WordnetStemmer(this.dictionary);
		}

		// tree
		final Tree tree = makeTree(source, base, parameters, false);
		if (tree == null)
		{
			return null;
		}

		// settings
		final List<INode> children = tree.getRoot().getChildren();
		final int size = children == null ? 0 : children.size();
		final Settings settings = makeSettings(size);

		this.dictionary.close();

		// result
		return new Model(tree, settings);
	}

	@Nullable
	@Override
	public Tree makeTree(@Nullable final String source, final URL base, @NonNull final Properties parameters, final boolean checkRecursion)
	{
		if (source == null)
		{
			return null;
		}

		// query
		String query = source.trim();

		// get 'source' param if empty
		if (query.isEmpty())
		{
			query = parameters.getProperty("source");
			if (query == null)
			{
				return null;
			}
		}

		// strip scheme if any
		if (query.startsWith(BaseProvider.URLSCHEME))
		{
			query = query.substring(BaseProvider.URLSCHEME.length());
		}

		// pass it to context
		this.context.progress(query, false);

		// walk
		final List<IEdge> edges = new ArrayList<>();
		final INode rootNode = walk(query, true, edges);

		// result
		return new Tree(rootNode, edges);
	}

	/**
	 * Make settings
	 *
	 * @param childrenCount root children count
	 * @return settings
	 */
	@NonNull
	abstract protected Settings makeSettings(final int childrenCount);

	/**
	 * Make settings
	 *
	 * @param childrenCount      root children count
	 * @param thresholdForRadial threshold for radial orientation
	 * @return settings
	 */
	@NonNull
	protected Settings makeSettings(final int childrenCount, final int thresholdForRadial)
	{
		final Settings settings = new Settings();
		settings.hasToolbarFlag = true;
		settings.hasStatusbarFlag = true;
		settings.focusOnHoverFlag = false;

		settings.expansion = this.expansion == null ? BaseProvider.EXPANSION : this.expansion;
		settings.sweep = this.sweep == null ? BaseProvider.SWEEP : this.sweep;

		settings.fontFace = this.fontFace;
		settings.fontSizeFactor = (this.fontSizeFactor == null || this.fontSizeFactor == -1F) ? null : this.fontSizeFactor;
		settings.downscaleFontsFlag = true;
		settings.fontDownscaler = BaseProvider.FONTSCALER;
		settings.downscaleImagesFlag = true;
		settings.imageDownscaler = BaseProvider.IMAGESCALER;

		settings.borderFlag = BaseProvider.BORDER;
		settings.ellipsizeFlag = BaseProvider.ELLIPSIZE;
		settings.labelMaxLines = this.labelMaxLines == null ? BaseProvider.LABEL_MAX_LINES : this.labelMaxLines;
		settings.labelExtraLineFactor = BaseProvider.LABEL_EXTRA_LINE_FACTOR;

		settings.backColor = this.backColor;
		settings.treeEdgeColor = this.treeEdgeColor;
		settings.treeEdgeStyle = BaseProvider.EDGE_STYLE_TREE;

		final boolean asTree = childrenCount < thresholdForRadial;
		settings.orientation = "radial";
		if (asTree)
		{
			settings.orientation = "south";
			settings.yMoveTo = -0.4F;
		}

		// menu
		settings.menu = new ArrayList<>();

		final MenuItem searchEqualMenuItem = new MenuItem();
		searchEqualMenuItem.action = Action.SEARCH;
		searchEqualMenuItem.label = "Search for item that matches '$e'";
		searchEqualMenuItem.matchMode = MatchMode.EQUALS;
		searchEqualMenuItem.matchScope = MatchScope.LABEL;
		searchEqualMenuItem.link = "$e";
		settings.menu.add(searchEqualMenuItem);

		final MenuItem searchLabelIncludesMenuItem = new MenuItem();
		searchLabelIncludesMenuItem.action = Action.SEARCH;
		searchLabelIncludesMenuItem.label = "Search for item that includes '$e'";
		searchLabelIncludesMenuItem.matchMode = MatchMode.INCLUDES;
		searchLabelIncludesMenuItem.matchScope = MatchScope.LABEL;
		searchLabelIncludesMenuItem.link = "$e";
		settings.menu.add(searchLabelIncludesMenuItem);

		final MenuItem searchContentIncludesMenuItem = new MenuItem();
		searchContentIncludesMenuItem.action = Action.SEARCH;
		searchContentIncludesMenuItem.label = "Search for content that includes '$e'";
		searchContentIncludesMenuItem.matchMode = MatchMode.INCLUDES;
		searchContentIncludesMenuItem.matchScope = MatchScope.CONTENT;
		searchContentIncludesMenuItem.link = "$e";
		settings.menu.add(searchContentIncludesMenuItem);

		final MenuItem gotoMenuItem = new MenuItem();
		gotoMenuItem.action = Action.GOTO;
		gotoMenuItem.label = "$u";
		gotoMenuItem.link = INTERNAL_URLSCHEME + "$u";
		settings.menu.add(gotoMenuItem);

		return settings;
	}

	// W A L K

	/**
	 * Walk data
	 *
	 * @param query   target word
	 * @param recurse whether to recurse
	 * @param edges   edges
	 * @return result node
	 */
	@Nullable
	protected INode walk(@NonNull final String query, @SuppressWarnings("SameParameterValue") final boolean recurse, @NonNull final List<IEdge> edges)
	{
		// synset id hook
		if (query.startsWith("@"))
		{
			// id
			final String id = query.substring(1);

			// synset
			final ISynset synset0 = this.dictionary.getSynset(SynsetID.parseSynsetID(id));
			if (synset0 == null)
			{
				return null;
			}
			final Synset synset = new Synset(synset0);

			// synset node
			final TreeMutableNode synsetNode = makeSynsetNode(null, synset);
			synsetNode.setLabel(BaseProvider.mangleString(BaseProvider.members(synset, ", ")));
			synsetNode.setImageIndex(ImageIndex.ROOT.ordinal());

			// synset node (ignore links)
			walkSynset(synsetNode, synset, 0, 0);

			// links
			walkSemLinks(synsetNode, synset, 0, 0, recurse, edges);

			return synsetNode;
		}

		// break query into components: lemma, pos filter, sensefilter
		final String[] queryPath = query.split(",");
		final POS posFilter = queryPath.length > 1 ? POS.getPartOfSpeech(queryPath[1].trim().charAt(0)) : null;
		final Integer senseFilter = queryPath.length > 2 ? Integer.valueOf(queryPath[2].trim()) : null;

		// query
		final INode root = walk(BaseProvider.normalize(queryPath[0].trim()), posFilter, senseFilter, recurse, edges);

		// stems if no results
		final List<INode> children = root.getChildren();
		if (children == null || children.isEmpty())
		{
			for (final POS pos : POS.values())
			{
				final List<String> stems = this.stemmer.findStems(BaseProvider.normalize(queryPath[0].trim()), pos);
				if (stems.isEmpty())
				{
					continue;
				}

				MutableNode posNode = null;
				for (final String stem : stems)
				{
					final IIndexWord idx = this.dictionary.getIndexWord(stem, pos); // a line in an index file
					if (idx == null)
					{
						continue;
					}
					if (posNode == null)
					{
						posNode = makePosNode(root, pos, null);
					}
					makeStemNode(posNode, stem, pos);
				}
			}
		}
		return root;
	}

	/**
	 * Walk data
	 *
	 * @param lemma       target word
	 * @param posFilter   pos filter
	 * @param senseFilter sense filter
	 * @param recurse     whether to recurse
	 * @param edges       edges
	 * @return result node
	 */
	@NonNull
	protected INode walk(@NonNull final String lemma, @Nullable final POS posFilter, @Nullable final Integer senseFilter, final boolean recurse, @NonNull final List<IEdge> edges)
	{
		int globalSenseIdx = 0;
		final INode rootNode = makeRootNode(lemma);

		// iterate on parts of speech
		for (final POS pos : POS.values())
		{
			if (posFilter != null && !pos.equals(posFilter))
			{
				continue;
			}

			final IIndexWord idx = this.dictionary.getIndexWord(lemma, pos); // a line in an index file
			if (idx == null)
			{
				continue;
			}
			int posSenseIdx = 0;
			int tagCountTotal = 0;

			// pos node
			final INode posNode = makePosNode(rootNode, pos, lemma);

			// sense map per lexfile/category
			final Map<ILexFile, List<Sense>> senseDataMap = new HashMap<>();
			final Map<ILexFile, Collection<IWord>> senseDataMap2 = hierarchize(idx.getWordIDs());
			for (final ILexFile lexFile : senseDataMap2.keySet())
			{
				final Collection<IWord> senses = senseDataMap2.get(lexFile);
				if (senses != null)
				{
					// sense
					for (final IWord sense : senses)
					{
						++globalSenseIdx;
						++posSenseIdx;

						// synset
						final ISynset synset = sense.getSynset();

						// lexid
						final int lexId = sense.getLexicalID();

						// sensekey
						final ISenseKey senseKey = sense.getSenseKey();

						// senseentry
						final ISenseEntry senseEntry = this.dictionary.getSenseEntry(senseKey);

						// sensenum, tagcount
						final int senseNum = senseEntry.getSenseNumber();
						final int tagCount = senseEntry.getTagCount();
						tagCountTotal += tagCount;

						// add to list
						final Sense senseData = new Sense(pos, senseKey, sense, synset, lexId, posSenseIdx, globalSenseIdx, senseNum, tagCount);
						List<Sense> senseDatas = senseDataMap.computeIfAbsent(lexFile, k -> new ArrayList<>());
						senseDatas.add(senseData);
					}
				}
			}

			// sense map
			for (final ILexFile lexFile : senseDataMap.keySet())
			{
				final Collection<Sense> senseDatas = senseDataMap.get(lexFile);
				if (senseDatas != null)
				{
					// category
					final INode categoryNode = makeCategoryNode(posNode, lexFile);

					// scan list
					int i = 0;
					for (final Sense senseData : senseDatas)
					{
						if (senseFilter != null && senseData.senseNum != senseFilter)
						{
							continue;
						}

						// sense node
						final TreeMutableNode senseNode = makeSenseNode(categoryNode, senseData, tagCountTotal);

						// synset node (ignore links)
						walkSynset(senseNode, senseData.synset, i, 0);

						// links
						final Map<IPointer, List<ISynsetID>> semLinks = senseData.synset.synset.getRelatedMap();
						final Map<IPointer, List<IWordID>> lexLinks = senseData.sense.getRelatedMap();
						walkLinks(senseNode, semLinks, lexLinks, i, 0, recurse, edges);

						// walkSemLinks(senseNode, senseData.synset, 0, recurse);
						// walkLexLinks(senseNode, senseData.sense, 0);

						i++;
					}
				}
			}
		}
		return rootNode;
	}

	/**
	 * Walk synset content (excluding links)
	 *
	 * @param parentNode parent node
	 * @param synset     synset
	 * @param index      index of synset
	 * @param level      recursion level
	 */
	protected void walkSynset(@NonNull final TreeMutableNode parentNode, @NonNull final Synset synset, @SuppressWarnings("unused") final int index, final int level)
	{
		if ((this.features & FEATURE_SYNSET_MERGE_SINGLE_MEMBER_TO_PARENT_IF_NOT_BASE_LEVEL) != 0)
		{
			if (level > 0)
			{
				final List<IWord> words = synset.synset.getWords();
				if (words != null && words.size() == 1)
				{
					// P = m1
					final IWord word = words.get(0);

					final String lemma = word.getLemma();
					parentNode.setLabel(BaseProvider.printable(lemma));
					parentNode.setContent(glossContent(synset.gloss));
					parentNode.setLink(BaseProvider.URLSCHEME + lemma);

					decorateAsWord(parentNode, level);
					parentNode.setImageIndex(ImageIndex.WORD.ordinal());
					parentNode.setEdgeStyle(EDGE_STYLE_SYNSET);
					parentNode.setEdgeColor(this.synsetEdgeColor);
					return;
				}
			}
		}

		if ((this.features & FEATURE_SYNSET_FORGET_MEMBERS_NODE) != 0)
		{
			// P < m1 m2 m3
			walkMembers(parentNode, synset, level);
			return;
		}

		if ((this.features & FEATURE_SYNSET_FORGET_MEMBERS_NODE_IF_SINGLE_MEMBER) != 0 && hasSingleMember(synset))
		{
			// P < m1
			final MutableNode node = buildSingleMemberNode(parentNode, synset, level);

			node.setEdgeLabel(LABEL_MEMBER);
			node.setEdgeStyle(EDGE_STYLE_MEMBER);
			node.setEdgeColor(this.wordsEdgeColor);
			return;
		}

		// P < M < m1 m2 m3
		final TreeMutableNode membersNode = makeSynsetMembersNode(parentNode, synset);
		walkMembers(membersNode, synset, level);
	}

	/**
	 * Set word decorator
	 *
	 * @param node  node
	 * @param level recursion level
	 */
	protected void decorateAsWord(@NonNull final MutableNode node, final int level)
	{
		final int colorIndex = level % this.wordBackgroundColors.length;
		node.setBackColor(this.wordBackgroundColors[colorIndex]);
		node.setForeColor(this.wordForegroundColors[colorIndex]);
	}

	/**
	 * Walk synset members
	 *
	 * @param parentNode parent node
	 * @param synset     synset
	 * @param level      recursion level
	 */
	protected void walkMembers(@NonNull final TreeMutableNode parentNode, @NonNull final Synset synset, final int level)
	{
		if (this.loadBalanceMembers)
		{
			walkMembersLoadBalancing(parentNode, synset, level);
			return;
		}
		walkMembersNoLoadBalancing(parentNode, synset, level);
	}

	/**
	 * Walk synset members (no load balancing)
	 *
	 * @param parentNode parent node
	 * @param synset     synset
	 * @param level      recursion level
	 */
	private void walkMembersNoLoadBalancing(final TreeMutableNode parentNode, @NonNull final Synset synset, final int level)
	{
		for (final IWord word : synset.synset.getWords())
		{
			final INode node = makeWordNode(parentNode, word, synset.gloss, level);
			node.setEdgeLabel(LABEL_MEMBER);
			node.setEdgeStyle(EDGE_STYLE_MEMBER);
			node.setEdgeColor(this.wordEdgeColor);
		}
	}

	/**
	 * Walk synset members (load balancing)
	 *
	 * @param parentNode parent node
	 * @param synset     synset
	 * @param level      recursion level
	 */
	private void walkMembersLoadBalancing(@NonNull final TreeMutableNode parentNode, @NonNull final Synset synset, final int level)
	{
		// make list
		List<INode> memberNodes = new ArrayList<>();
		for (final IWord word : synset.synset.getWords())
		{
			final TreeMutableNode node = makeWordNode(null, word, synset.gloss, level);
			node.setEdgeLabel(LABEL_MEMBER);
			node.setEdgeStyle(EDGE_STYLE_MEMBER);
			node.setEdgeColor(this.wordEdgeColor);

			node.setTarget(node.getLabel());
			memberNodes.add(node);
		}

		// sort list
		memberNodes.sort(iNodeComparator);

		// balance load
		memberNodes = this.membersLoadBalancer.buildHierarchy(memberNodes, 0);
		parentNode.addChildren(memberNodes);
	}

	/**
	 * Walk semantic and lexical links
	 *
	 * @param parentNode parent node
	 * @param semLinks   sem links
	 * @param lexLinks   lex links
	 * @param index      synset index
	 * @param level      level
	 * @param recurse    recurse
	 * @param edges      edges
	 */
	protected void walkLinks(final TreeMutableNode parentNode, @NonNull final Map<IPointer, List<ISynsetID>> semLinks, @NonNull final Map<IPointer, List<IWordID>> lexLinks, final int index, @SuppressWarnings("SameParameterValue") final int level, final boolean recurse, @NonNull final List<IEdge> edges)
	{
		if (semLinks.isEmpty() && lexLinks.isEmpty())
		{
			return;
		}

		TreeMutableNode anchorNode = parentNode;

		if ((this.features & FEATURE_COLLAPSE_LINKS) == 0)
		{
			// create links node
			// P < L < 11 l2 l3
			final String semLabel = links(semLinks.keySet());
			final String lexLabel = links(lexLinks.keySet());
			final String label = (semLabel.isEmpty() ? "-" : semLabel) + "<br/>/<br/>" + (lexLabel.isEmpty() ? "-" : lexLabel);
			anchorNode = makeSynsetLinksNode(parentNode, label);
		}

		// else
		// do not use links super node
		// P < 11 l2 l3

		walkSemLinks(anchorNode, semLinks, index, level, recurse, edges);
		walkLexLinks(anchorNode, lexLinks, level);
	}

	/**
	 * Walk semantic links of the same type
	 *
	 * @param parentNode parent node
	 * @param synset     synset
	 * @param level      recursion level
	 * @param recurse    whether to recurse
	 * @param edges      edges
	 */
	protected void walkSemLinks(final TreeMutableNode parentNode, @NonNull final Synset synset, @SuppressWarnings("SameParameterValue") final int index, final int level, final boolean recurse, @NonNull final List<IEdge> edges)
	{
		final Map<IPointer, List<ISynsetID>> semLinks = synset.synset.getRelatedMap();
		if (!semLinks.isEmpty())
		{
			final TreeMutableNode linksNode = makeSynsetLinksNode(parentNode, links(semLinks.keySet()));
			walkSemLinks(linksNode, semLinks, index, level, recurse, edges);
		}
	}

	/**
	 * Walk semantic links
	 *
	 * @param parentNode parent node
	 * @param semLinks   linked synset ids classified by pointer
	 * @param level      recursion level
	 * @param recurse    whether to recurse
	 * @param edges      edges
	 */
	protected void walkSemLinks(final TreeMutableNode parentNode, @NonNull final Map<IPointer, List<ISynsetID>> semLinks, final int index, final int level, final boolean recurse, @NonNull final List<IEdge> edges)
	{
		// P=links node L

		// iterate pointers
		for (final IPointer pointer : semLinks.keySet())
		{
			final Link link = Link.valueOf(pointer);
			if (!link.test(this.filter))
			{
				continue;
			}

			// linked synset(s)
			final List<ISynsetID> linkedSynsetIds = semLinks.get(link.pointer);
			if (linkedSynsetIds != null)
			{
				if ((this.features & FEATURE_SEMLINKS_MERGE_SINGLE_LINKED_SYNSET_TO_LINK) != 0)
				{
					// P < l=s1
					// do not make synset node when only one synset is linked, attach synset content to link node directly
					final boolean singleSynset = linkedSynsetIds.size() == 1;
					if (singleSynset)
					{
						// single synset (no load balancing)
						final Iterator<ISynsetID> it = linkedSynsetIds.iterator();
						if (it.hasNext())
						{
							// linked synset node
							final ISynsetID linkedSynsetId = it.next();
							final ISynset linkedSynset0 = this.dictionary.getSynset(linkedSynsetId);
							if (linkedSynset0 != null)
							{
								final Synset linkedSynset = new Synset(linkedSynset0);

								// link node l
								final TreeMutableNode linkNode = makeLinkNode(parentNode, link);

								// populate linked synset node
								walkLinkedSynset1(linkNode, linkedSynset, link, index, level + 1, recurse && link.recurses, edges);
							}
						}
						continue;
					}
				}

				// multiple synsets
				// P < l < s1 s2 s3
				INode etcNode = null;

				final int n = linkedSynsetIds.size();
				int i = 0;

				List<INode> childNodes = new ArrayList<>();
				final Iterator<ISynsetID> it = linkedSynsetIds.iterator();
				while (it.hasNext())
				{
					// linked synset node
					final ISynsetID linkedSynsetId = it.next();
					final ISynset linkedSynset0 = this.dictionary.getSynset(linkedSynsetId);
					if (linkedSynset0 == null)
					{
						continue;
					}
					final Synset linkedSynset = new Synset(linkedSynset0);
					final TreeMutableNode linkedSynsetNode = makeSynsetNode(null, linkedSynset);

					// populate linked synset node
					walkLinkedSynset1(linkedSynsetNode, linkedSynset, link, index, level + 1, recurse && link.recurses, edges);
					final String tag = Integer.toString(i + 1);
					linkedSynsetNode.setTarget(tag);
					// linkedSynsetNode.setLabel(tag);

					// list
					childNodes.add(linkedSynsetNode);

					// limit
					if (++i >= this.maxLinks && i < n)
					{
						etcNode = makeEtcLinkNode(null, link, n, it);
						break;
					}
				}

				// semlinks nodes
				if (this.loadBalanceSemLinks)
				{
					childNodes = this.semLinksLoadBalancer.buildHierarchy(childNodes, link.imageIndex);
				}

				// link node l
				final TreeMutableNode linkNode = makeLinkNode(parentNode, link);
				if (childNodes != null)
				{
					linkNode.addChildren(childNodes);
				}

				// etc node
				if (etcNode != null)
				{
					linkNode.addChild(etcNode);
				}
			}
		}
	}

	/**
	 * Walk linked synset called from walkSemLinks
	 *
	 * @param parentNode   parent node
	 * @param linkedSynset synset
	 * @param link         link
	 * @param index        synset index
	 * @param level        recursion level
	 * @param recurse      recurse
	 * @param edges        edges
	 */
	protected void walkLinkedSynset1(@NonNull final TreeMutableNode parentNode, @NonNull final Synset linkedSynset, @NonNull final Link link, final int index, final int level, final boolean recurse, @NonNull final List<IEdge> edges)
	{
		// synset : members
		walkSynset(parentNode, linkedSynset, index, level);

		// similar to
		if (link.pointer == Pointer.SIMILAR_TO && linkedSynset.synset.isAdjectiveHead())
		{
			walkAntonymFromHeadAdjective(parentNode, linkedSynset, level);
		}

		// recurse
		if (recurse && level < this.maxRecurse)
		{
			// iterate linked synsets
			final List<ISynsetID> linkedSynsetIds = linkedSynset.synset.getRelatedSynsets(link.pointer);
			if (!linkedSynsetIds.isEmpty())
			{
				TreeMutableNode anchorNode = parentNode;
				if ((this.features & FEATURE_LINKEDSYNSET1_FORGET_LINK_NODE) == 0)
				{
					// P < l < s
					anchorNode = makeLinkNode(parentNode, link);
				}

				// else if FEATURE_RAISE_RECURSE_TO_SYNSET_AT_BASE_LEVEL
				// P < s
				walkTypedLink(anchorNode, linkedSynsetIds, link, level + 1, edges);
			}
		}
	}

	/**
	 * Walk lexical links
	 *
	 * @param parentNode parent node
	 * @param sense      sense
	 * @param level      recursion level
	 */
	protected void walkLexLinks(final TreeMutableNode parentNode, @NonNull final IWord sense, final int level)
	{
		final Map<IPointer, List<IWordID>> lexLinks = sense.getRelatedMap();
		if (!lexLinks.isEmpty())
		{
			final TreeMutableNode linksNode = makeSynsetLinksNode(parentNode, links(lexLinks.keySet()));
			walkLexLinks(linksNode, lexLinks, level);
		}
	}

	/**
	 * Walk lexical links
	 *
	 * @param parentNode parent node
	 * @param lexLinks   linked sense ids classified by pointer
	 * @param level      current recursion level
	 */
	protected void walkLexLinks(final TreeMutableNode parentNode, @NonNull final Map<IPointer, List<IWordID>> lexLinks, final int level)
	{
		// iterate pointers
		for (final IPointer pointer : lexLinks.keySet())
		{
			final Link link = Link.valueOf(pointer);
			if (!link.test(this.filter))
			{
				continue;
			}

			final INode linkNode = makeLinkNode(parentNode, link);

			// iterate linked senses
			final List<IWordID> linkedSenseIds = lexLinks.get(link.pointer);
			if (linkedSenseIds != null)
			{
				for (final IWordID linkedSenseId : linkedSenseIds)
				{
					if (linkedSenseId == null)
					{
						continue;
					}

					// lex node
					/* final INode lexNode = */
					makeLexNode(linkNode, linkedSenseId, level);

					// synset
					// final Synset synset = new Synset(linkedSynset);
					// final INode synsetNode = makeSynsetNode(lexNode, synset);
					// walkSynset(synsetNode, synset, level);
				}
			}
		}
	}

	/**
	 * Walk links following only one type
	 *
	 * @param parentNode      parent node
	 * @param linkedSynsetIds linked synset ids
	 * @param link            link
	 * @param level           recursion level
	 * @param edges           edges
	 */
	protected void walkTypedLink(@NonNull final TreeMutableNode parentNode, @NonNull final List<ISynsetID> linkedSynsetIds, @NonNull final Link link, final int level, @NonNull final List<IEdge> edges)
	{
		// iterate linked synsets
		INode etcNode = null;

		int i = 0;
		final int n = linkedSynsetIds.size();
		List<INode> childNodes = new ArrayList<>();
		final Iterator<ISynsetID> it = linkedSynsetIds.iterator();
		while (it.hasNext())
		{
			final ISynsetID linkedSynsetId = it.next();
			final ISynset linkedSynset0 = this.dictionary.getSynset(linkedSynsetId);
			if (linkedSynset0 == null)
			{
				continue;
			}

			// synset
			final Synset linkedSynset = new Synset(linkedSynset0);
			final String tag = Integer.toString(i + 1);

			// node for synset
			TreeMutableNode linkedSynsetNode;
			if ((this.features & FEATURE_TYPEDLINK_RAISE_RECURSE_AS_SIBLING) != 0)
			{
				// l < s1(=M < m1 m2 m3)->l1 s2(=M < m1 m2 <m3)->l2
				// members super node
				linkedSynsetNode = buildMembersNode(linkedSynset, level);
			}
			else
			{
				if ((this.features & FEATURE_TYPEDLINK_RAISE_SINGLE_MEMBER_TO_SYNSET) != 0 && hasSingleMember(linkedSynset))
				{
					linkedSynsetNode = buildSingleMemberNode(null, linkedSynset, level);
					linkedSynsetNode.setEdgeLabel(null);
					linkedSynsetNode.setEdgeColor(this.linkBackgroundColor);
					linkedSynsetNode.setEdgeImageIndex(link.imageIndex);
				}
				else
				{
					// l < (s1 < l1) (s2 < l2) (s3 < l3)
					// synset node
					linkedSynsetNode = makeSynsetNode(null, linkedSynset);
					linkedSynsetNode.setEdgeLabel(null);
					linkedSynsetNode.setEdgeColor(this.linkBackgroundColor);
					linkedSynsetNode.setEdgeImageIndex(link.imageIndex);

					// members
					walkSynset(linkedSynsetNode, linkedSynset, i, level);
				}
			}

			// list
			linkedSynsetNode.setTarget(tag);
			childNodes.add(linkedSynsetNode);

			// recurse
			if (level < this.maxRecurse)
			{
				// iterate linked synsets
				final List<ISynsetID> childLinkedSynsetIds = linkedSynset.synset.getRelatedSynsets(link.pointer);
				if (!childLinkedSynsetIds.isEmpty())
				{
					if ((this.features & FEATURE_TYPEDLINK_RAISE_RECURSE_AS_SIBLING) != 0)
					{
						final TreeMutableNode linkNode = makeLinkNode(null, link);
						walkTypedLink(linkNode, childLinkedSynsetIds, link, level + 1, edges);
						linkNode.setLabel(tag);
						linkNode.setTarget(tag);
						childNodes.add(linkNode);

						final MutableEdge edge = new MutableEdge(linkedSynsetNode, linkNode);
						edge.setLabel(link.label);
						edge.setColor(this.edgeColor);
						edge.setStyle(EDGE_STYLE_EDGE);
						edges.add(edge);
					}
					else
					{
						TreeMutableNode anchorNode = linkedSynsetNode;
						if ((this.features & FEATURE_TYPEDLINK_FORGET_LINK_NODE) == 0)
						{
							anchorNode = makeLinkNode(linkedSynsetNode, link);
						}
						walkTypedLink(anchorNode, childLinkedSynsetIds, link, level + 1, edges);
						anchorNode.setTarget(tag);
					}
				}
			}

			// limit
			if (++i >= this.maxLinks && i < n)
			{
				etcNode = makeEtcLinkNode(null, link, n, it);
				break;
			}
		}

		// semlinks nodes
		if (this.loadBalanceSemLinks)

		{
			childNodes = this.semLinksLoadBalancer.buildHierarchy(childNodes, link.imageIndex);
		}

		parentNode.addChildren(childNodes);

		// etc node
		if (etcNode != null)
		{
			parentNode.addChild(etcNode);
		}
	}

	/**
	 * Walk antonym from adjective head synset
	 *
	 * @param parentNode parent node
	 * @param synset     adj head synset
	 * @param level      recursion level
	 */
	protected void walkAntonymFromHeadAdjective(final TreeMutableNode parentNode, @NonNull final Synset synset, final int level)
	{
		final boolean isHead = synset.synset.isAdjectiveHead();
		if (isHead)
		{
			final IWord head = synset.synset.getWord(1);
			final List<IWordID> antonymSenseIds = head.getRelatedWords(Pointer.ANTONYM);
			INode antonymLinkNode = null;

			// follow up antonyms
			for (final IWordID antonymSenseId : antonymSenseIds)
			{
				if (antonymLinkNode == null)
				{
					antonymLinkNode = makeLinkNode(parentNode, Link.ANTONYM);
				}
				makeLexNode(antonymLinkNode, antonymSenseId, level + 1);
			}
		}
	}

	// B U I L D E R S

	/**
	 * Build member nodes under members super-node
	 *
	 * @param synset synset
	 * @param level  recursion level
	 * @return members super-node
	 */
	@NonNull
	protected TreeMutableNode buildMembersNode(@NonNull final Synset synset, final int level)
	{
		final TreeMutableNode membersNode = makeSynsetNode(null, synset);
		for (final IWord word : synset.synset.getWords())
		{
			final INode node = makeWordNode(membersNode, word, synset.gloss, level);

			node.setEdgeLabel(LABEL_MEMBER);
			node.setEdgeStyle(EDGE_STYLE_MEMBER);
			node.setEdgeColor(this.wordEdgeColor);
		}
		return membersNode;
	}

	/**
	 * Build single member node
	 *
	 * @param parent parent to the node
	 * @param synset synset
	 * @param level  recursion level
	 * @return member node
	 */
	@NonNull
	protected TreeMutableNode buildSingleMemberNode(final INode parent, @NonNull final Synset synset, final int level)
	{
		final IWord word = synset.synset.getWords().get(0);
		final TreeMutableNode node = makeWordNode(parent, word, synset.gloss, level);

		// node.setEdgeLabel(LABEL_MEMBER);
		// node.setEdgeStyle(EDGE_STYLE_MEMBER);
		// node.setEdgeColor(this.wordEdgeColor);

		node.setTarget(node.getLabel());
		return node;
	}

	// N O D E F A C T O R Y

	/**
	 * Make root node
	 *
	 * @param word word
	 * @return node
	 */
	@NonNull
	protected MutableNode makeRootNode(@NonNull final String word)
	{
		return new MutableNode(null, BaseProvider.makeNodeId(), BaseProvider.printable(word), ImageIndex.ROOT.ordinal(), this.rootBackgroundColor, this.rootForegroundColor);
	}

	/**
	 * Make pos node
	 *
	 * @param parent parent node
	 * @param pos    pos
	 * @param word   word
	 * @return node
	 */
	@NonNull
	protected MutableNode makePosNode(final INode parent, @NonNull final POS pos, @Nullable final String word)
	{
		final MutableNode node = new MutableNode(parent, BaseProvider.makeNodeId(), pos.name().toLowerCase(Locale.ENGLISH), -1, this.posBackgroundColor, this.posForegroundColor);
		if (word != null)
		{
			node.setLink(BaseProvider.URLSCHEME + word + ',' + pos.getTag());
		}
		node.setEdgeColor(this.posEdgeColor);
		node.setEdgeStyle(EDGE_STYLE_POS);
		node.setEdgeImageIndex(ImageIndex.POS.ordinal());
		return node;
	}

	/**
	 * Make category node
	 *
	 * @param parent  parent node
	 * @param lexFile lexfile
	 * @return node
	 */
	@NonNull
	protected MutableNode makeCategoryNode(final INode parent, @NonNull final ILexFile lexFile)
	{
		final MutableNode node = new MutableNode(parent, BaseProvider.makeNodeId(), BaseProvider.category(lexFile), -1, this.categoryBackgroundColor, this.categoryForegroundColor);
		node.setContent(lexFile.getDescription());
		node.setEdgeLabel(LABEL_CATEGORY);
		node.setEdgeStyle(EDGE_STYLE_CATEGORY);
		node.setEdgeColor(this.categoryEdgeColor);
		// node.setEdgeImageIndex(ImageIndex.CATEGORY.ordinal());
		return node;
	}

	/**
	 * Make sense base node
	 *
	 * @param parent        parent node
	 * @param sense         sense
	 * @param tagCountTotal tag count total
	 * @return node
	 */
	@NonNull
	protected TreeMutableNode makeSenseBaseNode(final INode parent, @NonNull final Sense sense, final int tagCountTotal)
	{
		final float share = tagCountTotal == 0 ? 0F : sense.tagCount / (float) tagCountTotal * 100F;
		final String shareString = String.format(Locale.ENGLISH, "%.0f%%", share);
		final String shareString2 = String.format(Locale.ENGLISH, "%.2f%%", share);
		final String label = share != 0F ? shareString : String.format(Locale.ENGLISH, "#%d", sense.senseNum);
		final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), label, -1, this.senseBackgroundColor, this.senseForegroundColor);
		node.setContent(senseContent(sense, shareString2));
		node.setLink(BaseProvider.URLSCHEME + sense.sensekey.getLemma() + ',' + sense.pos.getTag() + ',' + sense.senseNum);
		node.setEdgeStyle(EDGE_STYLE_SENSE);
		node.setEdgeColor(this.senseEdgeColor);
		return node;
	}

	/**
	 * Make sense node
	 *
	 * @param parent        parent node
	 * @param sense         sense
	 * @param tagCountTotal tag count total
	 * @return node
	 */
	@NonNull
	protected TreeMutableNode makeSenseNode(final INode parent, @NonNull final Sense sense, final int tagCountTotal)
	{
		final TreeMutableNode node = makeSenseBaseNode(parent, sense, tagCountTotal);
		node.setEdgeLabel(LABEL_SENSE);
		node.setEdgeImageIndex(ImageIndex.SENSE.ordinal());
		return node;
	}

	/**
	 * Make synset node
	 *
	 * @param parent parent node
	 * @param synset synset
	 * @return node
	 */
	@NonNull
	protected TreeMutableNode makeSynsetNode(final INode parent, @NonNull final Synset synset)
	{
		final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), null, ImageIndex.SYNSET.ordinal(), this.synsetBackgroundColor, this.synsetForegroundColor);
		node.setContent(glossContent(synset.gloss));
		node.setLink(BaseProvider.URLSCHEME + BaseProvider.URLSCHEME_AT + synset.synset.getID().toString());
		node.setEdgeLabel(LABEL_SYNSET);
		node.setEdgeStyle(EDGE_STYLE_SYNSET);
		node.setEdgeColor(this.synsetEdgeColor);
		return node;
	}

	/**
	 * Make synset members group tree mutable node
	 *
	 * @param parent parent node
	 * @param synset synset
	 * @return node
	 */
	@NonNull
	protected TreeMutableNode makeSynsetMembersNode(final INode parent, @NonNull final Synset synset)
	{
		final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), null, ImageIndex.MEMBERS.ordinal(), this.wordsBackgroundColor, this.wordsForegroundColor);
		node.setContent("<div class='members'>" + BaseProvider.members(synset, ", ") + "</div>");
		node.setLink(BaseProvider.URLSCHEME + BaseProvider.URLSCHEME_AT + synset.synset.getID().toString());
		node.setEdgeLabel(LABEL_MEMBERS);
		node.setEdgeStyle(EDGE_STYLE_MEMBERS);
		node.setEdgeColor(this.wordsEdgeColor);
		return node;
	}

	/**
	 * Make word node
	 *
	 * @param parent parent node
	 * @param word   word
	 * @param gloss  gloss
	 * @param level  recursion level
	 * @return node
	 */
	@NonNull
	protected TreeMutableNode makeWordNode(final INode parent, @NonNull final IWord word, @NonNull final Gloss gloss, final int level)
	{
		final String lemma = word.getLemma();
		final int colorIndex = level % this.wordBackgroundColors.length;
		final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), BaseProvider.printable(lemma), ImageIndex.WORD.ordinal(), this.wordBackgroundColors[colorIndex], this.wordForegroundColors[colorIndex]);
		node.setContent(glossContent(gloss));
		node.setLink(BaseProvider.URLSCHEME + lemma);
		// do not assume anything about link
		return node;
	}

	/**
	 * Make links group node
	 *
	 * @param parent   parent node
	 * @param pointers pointers
	 * @return node
	 */
	@NonNull
	protected TreeMutableNode makeSynsetLinksNode(final INode parent, final String pointers)
	{
		final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), null, ImageIndex.LINKS.ordinal(), this.linksBackgroundColor, this.linksForegroundColor);
		node.setContent(pointers);
		node.setEdgeLabel(LABEL_LINKS);
		node.setEdgeStyle(EDGE_STYLE_LINKS);
		node.setEdgeColor(this.linksEdgeColor);
		return node;
	}

	/**
	 * Make link node
	 *
	 * @param parent parent node
	 * @param link   link
	 * @return node
	 */
	@NonNull
	protected TreeMutableNode makeLinkNode(final INode parent, @NonNull final Link link)
	{
		final int imageIndex = link.imageIndex;
		final String label = link.label;
		final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), imageIndex != -1 ? null : label, imageIndex, this.linkBackgroundColor, this.linkForegroundColor);
		node.setContent("<div class='reference'>" + link.label + "</div>");
		node.setLink("help:" + link.tag);
		node.setEdgeLabel(label);
		node.setEdgeStyle(EDGE_STYLE_LINK);
		node.setEdgeColor(this.linkEdgeColor);
		return node;
	}

	/**
	 * Make lex node
	 *
	 * @param parent  parent node
	 * @param senseId sense id
	 * @param level   recursion level
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private TreeMutableNode makeLexNode(final INode parent, @NonNull final IWordID senseId, final int level)
	{
		final String lemma = lemma(senseId);
		final int colorIndex = level % this.wordBackgroundColors.length;
		final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), lemma, ImageIndex.WORD.ordinal(), this.wordBackgroundColors[colorIndex], this.wordForegroundColors[colorIndex]);
		final ISynsetID synsetId = senseId.getSynsetID();
		final ISynset synset = this.dictionary.getSynset(synsetId);
		final Gloss gloss = new Gloss(synset.getGloss());
		node.setContent(glossContent(gloss));
		node.setLink(BaseProvider.URLSCHEME + BaseProvider.URLSCHEME_AT + synsetId.toString());
		node.setEdgeStyle(EDGE_STYLE_LEX);
		node.setEdgeColor(this.wordEdgeColor);
		return node;
	}

	/**
	 * Make stem node
	 *
	 * @param parent parent node
	 * @param stem   stem
	 * @param pos    pos
	 */
	@NonNull
	@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
	protected TreeMutableNode makeStemNode(final INode parent, final String stem, @NonNull final POS pos)
	{
		final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), stem, -1, this.stemBackgroundColor, this.stemForegroundColor);
		node.setLink(BaseProvider.URLSCHEME + stem + ',' + pos.getTag());
		node.setEdgeLabel(LABEL_STEM);
		node.setEdgeStyle(EDGE_STYLE_STEM);
		node.setEdgeColor(this.stemEdgeColor);
		return node;
	}

	/**
	 * Make etcetera link node
	 *
	 * @param parent parent node
	 * @param link   link
	 * @param count  count of links
	 * @param it     current iterator
	 * @return node
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	protected MutableNode makeEtcLinkNode(final INode parent, @NonNull final Link link, final int count, @NonNull final Iterator<ISynsetID> it)
	{
		final int imageIndex = link.imageIndex;
		final String label = link.label;
		final String content = makeEtcContent(link, count, it);
		final MutableNode node = new MutableNode(parent, BaseProvider.makeNodeId(), "+ " + (count - this.maxLinks), imageIndex, this.etcBackgroundColor, this.etcForegroundColor);
		node.setContent(content);
		node.setEdgeLabel(label);
		node.setEdgeStyle(EDGE_STYLE_ETC);
		node.setEdgeColor(this.etcEdgeColor);
		return node;
	}

	/**
	 * Make etc content
	 *
	 * @param link  link
	 * @param count linked synset count
	 * @param it    current iterator of linked synset
	 * @return content
	 */
	@NonNull
	protected String makeEtcContent(@NonNull final Link link, final int count, @NonNull final Iterator<ISynsetID> it)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='more'>") //
				.append(link.label) //
				.append(" links: ") //
				.append(count) //
				.append("</div>");
		while (it.hasNext())
		{
			final ISynsetID synsetId = it.next();
			final ISynset synset = this.dictionary.getSynset(synsetId);
			if (synset == null)
			{
				continue;
			}
			sb.append("<div class='members'><a href='") //
					.append(BaseProvider.INTERNAL_URLSCHEME) //
					.append(BaseProvider.URLSCHEME) //
					.append(BaseProvider.URLSCHEME_AT) //
					.append(synsetId.toString()) //
					.append("'>") //
					.append(BaseProvider.members(synset, ", ")) //
					.append("</a>") //
					.append("</div>") //
					.append('\n');
		}
		return sb.toString();
	}

	// H E L P E R S

	/**
	 * Sort senses per lexical domain
	 *
	 * @param senseIds senses
	 * @return map
	 */
	@NonNull
	protected Map<ILexFile, Collection<IWord>> hierarchize(@NonNull final List<IWordID> senseIds)
	{
		final Map<ILexFile, Collection<IWord>> map = new TreeMap<>(lexFileComparator);
		for (final IWordID senseId : senseIds)
		{
			// sense
			final IWord sense = this.dictionary.getWord(senseId);

			// category
			final ILexFile category = sense.getSenseKey().getLexicalFile();

			// map entry
			Collection<IWord> senses = map.computeIfAbsent(category, k -> new ArrayList<>());
			senses.add(sense);
		}
		return map;
	}

	/**
	 * Get category from lexfile
	 *
	 * @param lexFile lexfile
	 * @return short name
	 */
	@NonNull
	static protected String category(@NonNull final ILexFile lexFile)
	{
		final String name = lexFile.getName();
		final int cut = name.indexOf('.');
		return name.substring(cut + 1);
	}

	/**
	 * Get links
	 *
	 * @param pointers pointers
	 * @return links
	 */
	@NonNull
	static protected String links(@NonNull final Set<IPointer> pointers)
	{
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final IPointer pointer : pointers)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append("<br/>");
			}
			sb.append(pointer.getName().toLowerCase(Locale.ENGLISH));
		}
		return sb.toString();
	}

	/**
	 * Has single member
	 *
	 * @param synset synset
	 * @return true if synset has single member, false otherwise
	 */
	static protected boolean hasSingleMember(@NonNull final Synset synset)
	{
		final List<IWord> words = synset.synset.getWords();
		// noinspection SimplifiableConditionalExpression
		return words == null ? false : words.size() == 1;
	}

	/**
	 * Get synset members
	 *
	 * @param synset    synset
	 * @param separator separator
	 * @return synset members
	 */
	@NonNull
	static protected String members(@NonNull final Synset synset, @SuppressWarnings("SameParameterValue") final String separator)
	{
		return BaseProvider.members(synset.synset, separator);
	}

	/**
	 * Get synset members
	 *
	 * @param synset    synset
	 * @param separator separator
	 * @return synset members
	 */
	@NonNull
	static protected String members(@NonNull final ISynset synset, final String separator)
	{
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final IWord word : synset.getWords())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(separator);
			}
			sb.append(BaseProvider.printable(word.getLemma()));
		}
		return sb.toString();
	}

	/**
	 * Get lemma from sense id
	 *
	 * @param senseId sense id
	 * @return lemma
	 */
	@NonNull
	protected String lemma(@NonNull final IWordID senseId)
	{
		// sense
		String lemma = senseId.getLemma();
		if (lemma == null)
		{
			final IWord word = this.dictionary.getWord(senseId);
			lemma = word.getLemma();
		}
		return BaseProvider.printable(lemma);
	}

	/**
	 * Printable lemma
	 *
	 * @param lemma lemma
	 * @return printable lemma
	 */
	@NonNull
	static protected String printable(@NonNull final String lemma)
	{
		return lemma.replace('_', ' ');
	}

	/**
	 * Normalize lemma
	 *
	 * @param lemma lemma
	 * @return normalized lemma
	 */
	@NonNull
	static protected String normalize(@NonNull final String lemma)
	{
		return lemma.replace(' ', '_');
	}

	/**
	 * Members of synset
	 *
	 * @param synset synset
	 * @return space-separated string with enclosing braces
	 */
	@NonNull
	static protected String membersToString(@NonNull final Synset synset)
	{
		final StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (final IWord word : synset.synset.getWords())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(',');
			}
			sb.append(word.getLemma());
		}
		sb.append('}');
		return sb.toString();
	}

	/**
	 * Members of synset
	 *
	 * @param synset synset
	 * @return \n-separated string
	 */
	@NonNull
	static protected String membersAsLines(@NonNull final Synset synset)
	{
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final IWord word : synset.synset.getWords())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append('\n');
			}
			sb.append(BaseProvider.printable(word.getLemma()));
		}
		return sb.toString();
	}

	/**
	 * Members of synset
	 *
	 * @param synset synset
	 * @return array of members
	 */
	@NonNull
	static protected String[] membersOf(@NonNull final Synset synset)
	{
		final List<IWord> words = synset.synset.getWords();
		final int n = words.size();
		final String[] members = new String[n];

		for (int i = 0; i < n; i++)
		{
			final IWord word = words.get(i);
			members[i] = BaseProvider.printable(word.getLemma());
		}
		return members;
	}

	/**
	 * Make sense content
	 *
	 * @param sense sense
	 * @param share share
	 * @return content
	 */
	@NonNull
	protected String senseContent(@NonNull final Sense sense, final String share)
	{
		return glossContent(sense.synset.gloss) + //
				"<div class='data'>" + //
				"sense #" + sense.senseNum + "<br/>" + //
				"tag count: " + sense.tagCount + "<br/>" + //
				"share: " + share + "<br/>" + //
				"sensekey: " + sense.sensekey.toString() + "<br/>" + //
				"lexid: " + sense.lexId + //
				// "index: " + sense.posIdx + "<br/>" + //
				// "global: " + sense.globalIdx + "<br/>" +
				"</div>";
	}

	/**
	 * Make gloss content
	 *
	 * @param gloss gloss
	 * @return gloss string
	 */
	@NonNull
	static protected String glossContent(@NonNull final Gloss gloss)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='definition'>");
		sb.append(gloss.getDefinition());
		sb.append("</div>");

		final String[] samples = gloss.getSamples();
		if (samples.length > 0)
		{
			for (final String sample : gloss.getSamples())
			{
				sb.append("<div class='sample'>");
				// →•‣
				sb.append(" → ");
				sb.append(sample);
				sb.append("</div>");
			}
		}
		return sb.toString();
	}

	/**
	 * Mangle string to fit in
	 *
	 * @param str0 string
	 * @return mangled string or null
	 */
	@Nullable
	static private String mangleString(@Nullable final String str0)
	{
		if (str0 == null)
		{
			return null;
		}
		if (str0.length() <= BaseProvider.MAXCHARS)
		{
			return str0;
		}

		final StringBuilder sb = new StringBuilder(str0);
		sb.setLength(BaseProvider.MAXCHARS);
		final int lastComma = sb.lastIndexOf(",");
		if (lastComma != -1)
		{
			sb.setLength(lastComma + 1);
		}
		return sb.append('…').toString(); // …⋯
	}

	// D E C O R A T E

	protected void setNodeImage(@NonNull final MutableNode node, @Nullable final String imageFile, @Nullable final ImageIndex index)
	{
		if (imageFile != null)
		{
			node.setImageFile(imageFile);
		}
		else if (index != null)
		{
			setNodeImage(node, index.ordinal());
		}
	}

	protected void setNodeEdgeImage(@NonNull final MutableNode node, @Nullable final String edgeImageFile, @SuppressWarnings("SameParameterValue") @Nullable final ImageIndex index)
	{
		if (edgeImageFile != null)
		{
			node.setEdgeImageFile(edgeImageFile);
		}
		else if (index != null)
		{
			setTreeEdgeImage(node, index.ordinal());
		}
	}

	@Override
	public void setNodeImage(@NonNull final MutableNode node, final int index)
	{
		if (index != -1)
		{
			assert images != null;
			node.setImageFile(images[index]);
		}
	}

	@Override
	public void setTreeEdgeImage(@NonNull final MutableNode node, final int index)
	{
		if (index != -1)
		{
			assert images != null;
			node.setEdgeImageFile(images[index]);
		}
	}

	@Override
	public void setEdgeImage(@NonNull final MutableEdge edge, final int index)
	{
		if (index != -1)
		{
			assert images != null;
			edge.setImageFile(images[index]);
		}
	}
}
