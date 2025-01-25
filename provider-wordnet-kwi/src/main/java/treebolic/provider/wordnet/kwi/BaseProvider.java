/*
 * Copyright (c) 2019-2025. Bernard Bou
 */

package treebolic.provider.wordnet.kwi;

import org.kwi.Dictionary;
import org.kwi.IDictionary;
import org.kwi.item.*;
import org.kwi.morph.IStemmer;
import org.kwi.morph.WordnetStemmer;
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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

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

	static protected final long FEATURE_COLLAPSE_RELATIONS = 0x00000002; // forget 'relations' node

	static protected final long FEATURE_SYNSET_FORGET_MEMBERS_NODE = 0x00000010; // attach all members to parent without 'members' node

	static protected final long FEATURE_SYNSET_FORGET_MEMBERS_NODE_IF_SINGLE_MEMBER = 0x00000020; // attach single member to parent without 'members' node

	static protected final long FEATURE_SYNSET_MERGE_SINGLE_MEMBER_TO_PARENT_IF_NOT_BASE_LEVEL = 0x00000040; // raise and coalesce single member with parent

	static protected final long FEATURE_TYPEDRELATION_RAISE_SINGLE_MEMBER_TO_SYNSET = 0x00000100;

	static protected final long FEATURE_TYPEDRELATION_FORGET_RELATION_NODE = 0x00000200;

	static protected final long FEATURE_TYPEDRELATION_RAISE_RECURSE_AS_SIBLING = 0x00000400;

	static protected final long FEATURE_RELATEDSYNSET1_FORGET_RELATION_NODE = 0x00001000; // attach related synset to parent without 'relation' node

	static protected final long FEATURE_SEMRELATIONS_MERGE_SINGLE_RELATED_SYNSET_TO_RELATION = 0x00002000; // raise and coalesce single related synset with 'relation' node

	static protected final long FEATURE_MULTILINE_RAISE_MEMBERS_TO_SYNSET = 0x10000000; // multiple line label

	static protected final long FEATURE_MULTILINE_RAISE_MEMBERS_TO_RELATED_SYNSET = 0x20000000; // multiple line label

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
	 * Label relations
	 */
	static public final String LABEL_RELATIONS = "relations";

	/**
	 * Label words
	 */
	static public final String LABEL_WORDS = "words";

	/**
	 * Label word
	 */
	static public final String LABEL_WORD = "word";

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
	static protected final int EDGE_STYLE_WORD = IEdge.DASH | IEdge.STROKEDEF | IEdge.TOCIRCLE | IEdge.TOFILL | IEdge.TODEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_WORDS = IEdge.SOLID | IEdge.STROKEDEF | IEdge.TOCIRCLE | IEdge.TOFILL | IEdge.TODEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_SYNSET = IEdge.SOLID | IEdge.STROKEDEF | IEdge.TOCIRCLE | IEdge.TOFILL | IEdge.TODEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_RELATION = IEdge.SOLID | IEdge.STROKEDEF | IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.TODEF | IEdge.FROMDEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_LEX = IEdge.SOLID | IEdge.STROKEDEF;

	@SuppressWarnings({"WeakerAccess"})
	static protected final int EDGE_STYLE_RELATIONS = IEdge.SOLID | IEdge.STROKEDEF | IEdge.FROMCIRCLE | IEdge.FROMFILL | IEdge.FROMDEF;

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
	 * WordNet relation
	 */
	public enum Relation
	{
		// @formatter:off
		/** Hypernym */ HYPERNYM(Pointer.getHYPERNYM(), "+ generic", "hypernym", ImageIndex.HYPERNYM.ordinal(), true), //
		/** Hypernym instance */ HYPERNYM_INSTANCE(Pointer.getHYPERNYM_INSTANCE(), "instance hypernym", "hypernym", ImageIndex.HYPERNYM_INSTANCE.ordinal(), false), //
		/** Hyponym */ HYPONYM(Pointer.getHYPONYM(), "+ specific", "hyponym", ImageIndex.HYPONYM.ordinal(), true), //
		/** Hyponym instance */ HYPONYM_INSTANCE(Pointer.getHYPONYM_INSTANCE(), "instance hyponym", "hyponym", ImageIndex.HYPONYM_INSTANCE.ordinal(), false), //

		/** Member holonym */ HOLONYM_MEMBER(Pointer.getHOLONYM_MEMBER(), "is member of", "holonym", ImageIndex.HOLONYM_MEMBER.ordinal(), true), //
		/** Substance holonym */ HOLONYM_SUBSTANCE(Pointer.getHOLONYM_SUBSTANCE(), "is substance of", "holonym", ImageIndex.HOLONYM_SUBSTANCE.ordinal(), true), //
		/** Part holonym */ HOLONYM_PART(Pointer.getHOLONYM_PART(), "is part of", "holonym", ImageIndex.HOLONYM_PART.ordinal(), true), //

		/** Member meronym */ MERONYM_MEMBER(Pointer.getMERONYM_MEMBER(), "member", "meronym", ImageIndex.MERONYM_MEMBER.ordinal(), true), //
		/** Substance meronym */ MERONYM_SUBSTANCE(Pointer.getMERONYM_SUBSTANCE(), "substance", "meronym", ImageIndex.MERONYM_SUBSTANCE.ordinal(), true), //
		/** Part meronym */ MERONYM_PART(Pointer.getMERONYM_PART(), "part", "meronym", ImageIndex.MERONYM_PART.ordinal(), true), //

		/** Antonym */ ANTONYM(Pointer.getANTONYM(), "opposite", "antonym", ImageIndex.ANTONYM.ordinal(), false), //

		/** Entails */ ENTAILS(Pointer.getENTAILMENT(), "entails", "entails", ImageIndex.ENTAILS.ordinal(), true), //
		/** Is entailed by */ ENTAILED(Pointer.getIS_ENTAILED(), "is entailed by", "entailed", ImageIndex.IS_ENTAILED_BY.ordinal(), true), //
		/** Causes */ CAUSES(Pointer.getCAUSE(), "causes", "causes", ImageIndex.CAUSES.ordinal(), true), //
		/** Is caused by */ CAUSED(Pointer.getIS_CAUSED(), "is caused by", "caused", ImageIndex.CAUSED.ordinal(), true), //

		/** Similar to */ SIMILAR_TO(Pointer.getSIMILAR_TO(), "similar to", "similar", ImageIndex.SIMILAR.ordinal(), false), //
		/** Also */ ALSO(Pointer.getALSO_SEE(), "also see", "also", ImageIndex.ALSO.ordinal(), false), //
		/** Attribute */ ATTRIBUTE(Pointer.getATTRIBUTE(), "attribute", "attribute", ImageIndex.ATTRIBUTE.ordinal(), false), //
		/** Pertainym */ PERTAINYM(Pointer.getPERTAINYM(), "pertains to", "pertainym", ImageIndex.PERTAINYM.ordinal(), false), //
		/** Derivation */ DERIVATION(Pointer.getDERIVATIONALLY_RELATED(), "derivation", "derivation", ImageIndex.DERIVATION.ordinal(), false), //
		/** Derived from adjective */ DERIVATION_ADJ(Pointer.getDERIVED_FROM_ADJ(), "derived from adjective", "derivation_adj", ImageIndex.DERIVATION_ADJ.ordinal(), false), //

		/** Verb group */ VERB_GROUP(Pointer.getVERB_GROUP(), "verb group", "verbgroup", ImageIndex.VERBGROUP.ordinal(), false), //
		/** Participle */ PARTICIPLE(Pointer.getPARTICIPLE(), "participle", "participle", ImageIndex.PARTICIPLE.ordinal(), false), //

		/** Domain */ DOMAIN(Pointer.getDOMAIN(), "domain", "domain", ImageIndex.DOMAIN.ordinal(), false), //
		/** Topic */ DOMAIN_TOPIC(Pointer.getTOPIC(), "domain topic", "domain", ImageIndex.DOMAIN_TOPIC.ordinal(), false), //
		/** Usage */ DOMAIN_USAGE(Pointer.getUSAGE(), "is exemplified by", "domain", ImageIndex.DOMAIN_USAGE.ordinal(), false), //
		/** Region */ DOMAIN_REGION(Pointer.getREGION(), "domain region", "domain", ImageIndex.DOMAIN_REGION.ordinal(), false), //

		/** Member */ HASDOMAIN(Pointer.getMEMBER(), "has domain", "has domain", ImageIndex.HASDOMAIN.ordinal(), false), //
		/** Topic member */ HASDOMAIN_TOPIC(Pointer.getTOPIC_MEMBER(), "has topic", "hasdomain", ImageIndex.HASDOMAIN_TOPIC.ordinal(), false), //
		/** Usage member */ HASDOMAIN_USAGE(Pointer.getUSAGE_MEMBER(), "exemplifies", "hasdomain", ImageIndex.HASDOMAIN_USAGE.ordinal(), false), //
		/** Region member */ HASDOMAIN_REGION(Pointer.getREGION_MEMBER(), "has region", "hasdomain", ImageIndex.HASDOMAIN_REGION.ordinal(), false); //
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
		Relation(final Pointer pointer, final String label, final String tag, final int imageIndex, final boolean recurses)
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
		 * @return relation mask
		 */
		public long mask()
		{
			return 1 << ordinal();
		}

		/**
		 * Test
		 *
		 * @param bitmap bits
		 * @return true if the relation's bit is set
		 */
		@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "WeakerAccess"})
		public boolean test(final long bitmap)
		{
			return (bitmap & (1 << ordinal())) != 0;
		}

		/**
		 * Parse pointer for relation
		 *
		 * @param pointer pointer
		 * @return relation
		 */
		@NonNull
		static public Relation valueOf(@NonNull final Pointer pointer)
		{
			final String symbol = pointer.getSymbol();
			switch (symbol.charAt(0))
			{
				case '@':
					if (symbol.length() > 1 && symbol.charAt(1) == 'i')
					{
						return Relation.HYPERNYM_INSTANCE;
					}
					return Relation.HYPERNYM;

				case '~':
					if (symbol.length() > 1 && symbol.charAt(1) == 'i')
					{
						return Relation.HYPONYM_INSTANCE;
					}
					return Relation.HYPONYM;

				case '#':
					if (symbol.length() > 1)
					{
						switch (symbol.charAt(1))
						{
							case 'm':
								return Relation.HOLONYM_MEMBER;
							case 's':
								return Relation.HOLONYM_SUBSTANCE;
							case 'p':
								return Relation.HOLONYM_PART;
						}
					}
					break;

				case '%':
					if (symbol.length() > 1)
					{
						switch (symbol.charAt(1))
						{
							case 'm':
								return Relation.MERONYM_MEMBER;
							case 's':
								return Relation.MERONYM_SUBSTANCE;
							case 'p':
								return Relation.MERONYM_PART;
						}
					}
					break;

				case '^':
					return Relation.ALSO;

				case '!':
					return Relation.ANTONYM;

				case '>':
					if (symbol.length() > 1 && symbol.charAt(1) == '^')
					{
						return Relation.CAUSED;
					}
					return Relation.CAUSES;

				case '*':
					if (symbol.length() > 1 && symbol.charAt(1) == '^')
					{
						return Relation.ENTAILED;
					}
					return Relation.ENTAILS;

				case '<':
					return Relation.PARTICIPLE;

				case '$':
					return Relation.VERB_GROUP;

				case '&':
					return Relation.SIMILAR_TO;

				case '=':
					return Relation.ATTRIBUTE;

				case '+':
					return Relation.DERIVATION;

				case '\\':
					if (pointer.getName().startsWith("Pertainym"))
					{
						return Relation.PERTAINYM;
					}
					else if (pointer.getName().startsWith("Derived from adjective"))
					{
						return Relation.DERIVATION_ADJ;
					}
					break;

				case ';':
					if (symbol.length() > 1)
					{
						switch (symbol.charAt(1))
						{
							case 'r':
								return Relation.DOMAIN_REGION;
							case 'c':
								return Relation.DOMAIN_TOPIC;
							case 'u':
								return Relation.DOMAIN_USAGE;
						}
					}
					return Relation.DOMAIN;

				case '-':
					if (symbol.length() > 1)
					{
						switch (symbol.charAt(1))
						{
							case 'r':
								return Relation.HASDOMAIN_REGION;

							case 'c':
								return Relation.HASDOMAIN_TOPIC;

							case 'u':
								return Relation.HASDOMAIN_USAGE;
						}
					}
					return Relation.HASDOMAIN;

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
		/** Root */ ROOT, /** POS */ POS, /** Category */ CATEGORY, /** Sense */ SENSE, /** Synset */ SYNSET, /** Words */ WORDS, /** Word */ WORD,/** Relations */  RELATIONS,

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

		/** Entails */ ENTAILS, //
		/** Is entailed by */ IS_ENTAILED_BY, //
		/** Causes */ CAUSES, //
		/** Is caused by */ CAUSED, //
		/** Verb group */ VERBGROUP, //
		/** Participle */ PARTICIPLE, //

		/** Similar to */ SIMILAR, //
		/** Also see */ ALSO, //
		/** Attribute */ ATTRIBUTE, //
		/** Pertainym */ PERTAINYM, //
		/** Derivationally related */ DERIVATION, //
		/** Derived from adjective */ DERIVATION_ADJ, //

		/** Domain */ DOMAIN, //
		/** Topic */ DOMAIN_TOPIC, //
		/** Usage */ DOMAIN_USAGE, //
		/** Region */ DOMAIN_REGION, //

		/** Member */ HASDOMAIN, //
		/** Topic member */ HASDOMAIN_TOPIC, //
		/** Usage member */ HASDOMAIN_USAGE, //
		/** Region member */ HASDOMAIN_REGION, //
		// @formatter:on
	}

	@Nullable
	static protected final String[] images = new String[]{ //
			"root.png", // ROOT

			"pos.png", // POS
			"category.png", // CATEGORY
			"sense.png", // SENSE
			"synset.png", // SYNSET
			"words.png", // WORDS
			"word.png", // WORD
			"relations.png", // RELATIONS

			"hypernym.png", // HYPERNYM
			"hypernym_instance.png", // HYPERNYM_INSTANCE
			"hyponym.png", // HYPONYM
			"hyponym_instance.png", // HYPONYM_INSTANCE

			"holonym_member.png", // HOLONYM_MEMBER
			"holonym_substance.png", // HOLONYM_SUBSTANCE
			"holonym_part.png", // HOLONYM_PART

			"meronym_member.png", // MERONYM_MEMBER
			"meronym_substance.png", // MERONYM_SUBSTANCE
			"meronym_part.png", // MERONYM_PART

			"antonym.png", // ANTONYM

			"entails.png", // ENTAILS
			"entailed.png", // ENTAILED
			"causes.png", // CAUSE
			"caused.png", // CAUSED
			"verbgroup.png", // VERBGROUP
			"participle.png", // PARTICIPLE

			"similar.png", // SIMILAR_TO
			"also.png", // ALSO
			"attribute.png", // ATTRIBUTE
			"pertainym.png", // PERTAINYM
			"derivation.png", // DERIVATION
			"derivation_adj.png", // DERIVATION_ADJ

			"domain.png", // DOMAIN IS_DOMAIN
			"domain_topic.png", // TOPIC DOMAIN_TOPIC IS_DOMAIN_TOPIC ;c
			"domain_usage.png", // USAGE DOMAIN_USAGE IS_EXEMPLIFIED_BY ;u
			"domain_region.png", // REGION DOMAIN_REGION IS_DOMAIN_REGION ;r

			"hasdomain_png", // MEMBER HASDOMAIN
			"hasdomain_topic.png", // TOPIC_MEMBER HASDOMAIN_TOPIC -c
			"hasdomain_usage.png", // USAGE_MEMBER HASDOMAIN_USAGE EXEMPLIFIES -u
			"hasdomain_region.png",// REGION_MEMBER HASDOMAIN_REGION -r

			"role_agent.png", // ROLE_AGENT
			"role_bodypart.png", // ROLE_BODYPART
			"role_bymeansof.png", // ROLE_BYMEANSOF
			"role_destination.png", // ROLE_DESTINATION
			"role_event.png", // ROLE_EVENT
			"role_instrument.png", // ROLE_INSTRUMENT
			"role_location.png", // ROLE_LOCATION
			"role_material.png", // ROLE_MATERIAL
			"role_property.png", // ROLE_PROPERTY
			"role_result.png", // ROLE_RESULT
			"role_state.png", // ROLE_STATE
			"role_undergoer.png", // ROLE_UNDERGOER
			"role_uses.png", // ROLE_USES
			"role_vehicle.png", // ROLE_VEHICLE
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
	 * Relations background index
	 */
	static public final int RELATIONSBACKGROUND_IDX = 16;

	/**
	 * Relations foreground index
	 */
	static public final int RELATIONSFOREGROUND_IDX = 17;

	/**
	 * Relations edge index
	 */
	static public final int RELATIONSEDGE_IDX = 18;

	/**
	 * Relation background index
	 */
	static public final int RELATIONBACKGROUND_IDX = 19;

	/**
	 * Relation foreground index
	 */
	static public final int RELATIONFOREGROUND_IDX = 20;

	/**
	 * Relation edge index
	 */
	static public final int RELATIONEDGE_IDX = 21;

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
		BaseProvider.COLORS0[BaseProvider.RELATIONSBACKGROUND_IDX] = Colors.RED;
		BaseProvider.COLORS0[BaseProvider.RELATIONSFOREGROUND_IDX] = Colors.WHITE;
		BaseProvider.COLORS0[BaseProvider.RELATIONSEDGE_IDX] = Colors.RED;
		BaseProvider.COLORS0[BaseProvider.RELATIONBACKGROUND_IDX] = Colors.RED;
		BaseProvider.COLORS0[BaseProvider.RELATIONFOREGROUND_IDX] = Colors.WHITE;
		BaseProvider.COLORS0[BaseProvider.RELATIONEDGE_IDX] = Colors.RED;
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
	 * Help Scheme prefix
	 */
	static public final String HELP_URLSCHEME = "help:";

	/**
	 * Scheme separator
	 */
	static public final char URLSCHEME_AT = '@';

	/**
	 * Data (WN31/OEWN/url)
	 */
	static private final String DATA_DEFAULT = "OEWN";

	/**
	 * Max sibling relations
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
	@SuppressWarnings({"WeakerAccess", "PointlessBitwiseExpression"})
	static public final long FILTER_DEFAULT = Relation.HYPERNYM.mask() | Relation.HYPONYM.mask() | //
			Relation.HOLONYM_MEMBER.mask() | Relation.HOLONYM_SUBSTANCE.mask() | Relation.HOLONYM_PART.mask() | //
			Relation.MERONYM_MEMBER.mask() | Relation.MERONYM_MEMBER.mask() | Relation.MERONYM_MEMBER.mask() | //
			Relation.CAUSES.mask() | Relation.CAUSED.mask() | Relation.ENTAILS.mask() | Relation.ENTAILED.mask() | //
			Relation.SIMILAR_TO.mask() | //
			Relation.ANTONYM.mask();

	// L O A D B A L A N C I N G

	/**
	 * LoadBalancer : Members : Max children nodes at level 0, 1 ... n. Level 0 is just above leaves. Level > 0 is upward from leaves. Last value i holds for
	 * level i to n.
	 */
	static private final int[] MAX_MEMBERS_AT_LEVEL = {6, 3};

	/**
	 * LoadBalancer : Members : Truncation threshold
	 */
	static private final int MEMBERS_LABEL_TRUNCATE_AT = 3;

	/**
	 * LoadBalancer : Synsets : Max children nodes at level 0, 1 ... n. Level 0 is just above leaves. Level > 0 is upward from leaves. Last value i holds for
	 * level i to n.
	 */
	static private final int[] MAX_SEMRELATIONS_AT_LEVEL = {6, 3};

	/**
	 * LoadBalancer : Synsets : Truncation threshold
	 */
	static private final int SEMRELATIONS_LABEL_TRUNCATE_AT = 3;

	/*
	 * LoadBalancer : edge color
	 */
	// static private final Color LOADBALANCING_EDGE_COLOR = Color.WHITE;

	/**
	 * LoadBalancer : Edge style
	 */
	static protected final int LOADBALANCING_EDGE_STYLE = IEdge.DOT | /* IEdge.FROMDEF | IEdge.FROMCIRCLE | */IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.STROKEDEF | IEdge.TODEF;

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
		@Nullable final String label1 = n1.getLabel();
		@Nullable final String label2 = n2.getLabel();
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

	static protected final Comparator<LexFile> lexFileComparator = Comparator.comparing(LexFile::getName);

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
	 * Source URL
	 */
	protected URL sourceUrl;

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
	 * Load balancing flag for semrelations
	 */
	protected boolean loadBalanceSemRelations = true;

	/**
	 * Members load balancer
	 */
	protected LoadBalancer membersLoadBalancer;

	/**
	 * Synsets load balancer
	 */
	protected LoadBalancer semRelationsLoadBalancer;

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
	 * Max relations
	 */
	protected int maxRelations = BaseProvider.MAX_RELATED_DEFAULT;

	/**
	 * Max recursions
	 */
	protected int maxRecurse = BaseProvider.MAX_RECURSE_DEFAULT;

	// colors

	@Nullable
	protected Integer backColor;

	@Nullable
	protected Integer treeEdgeColor;

	@Nullable
	protected Integer rootBackgroundColor;

	@Nullable
	protected Integer rootForegroundColor;

	@Nullable
	protected Integer categoryBackgroundColor;

	@Nullable
	protected Integer categoryForegroundColor;

	@Nullable
	protected Integer categoryEdgeColor;

	@Nullable
	protected Integer posBackgroundColor;

	@Nullable
	protected Integer posForegroundColor;

	@Nullable
	protected Integer posEdgeColor;

	@Nullable
	protected Integer senseBackgroundColor;

	@Nullable
	protected Integer senseForegroundColor;

	@Nullable
	protected Integer senseEdgeColor;

	@Nullable
	protected Integer synsetBackgroundColor;

	@Nullable
	protected Integer synsetForegroundColor;

	@Nullable
	protected Integer synsetEdgeColor;

	@Nullable
	protected Integer relationsBackgroundColor;

	@Nullable
	protected Integer relationsForegroundColor;

	@Nullable
	protected Integer relationsEdgeColor;

	@Nullable
	protected Integer relationBackgroundColor;

	@Nullable
	protected Integer relationForegroundColor;

	@Nullable
	protected Integer relationEdgeColor;

	@Nullable
	protected Integer etcBackgroundColor;

	@Nullable
	protected Integer etcForegroundColor;

	@Nullable
	protected Integer etcEdgeColor;

	@Nullable
	protected Integer wordsBackgroundColor;

	@Nullable
	protected Integer wordsForegroundColor;

	@Nullable
	protected Integer wordsEdgeColor;

	@Nullable
	protected Integer wordEdgeColor;

	@Nullable
	protected Integer stemBackgroundColor;

	@Nullable
	protected Integer stemForegroundColor;

	@Nullable
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

	@Nullable
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
		this.semRelationsLoadBalancer = new LoadBalancer(MAX_SEMRELATIONS_AT_LEVEL, SEMRELATIONS_LABEL_TRUNCATE_AT);
		assert images != null;
		//noinspection ConstantValue
		this.membersLoadBalancer.setGroupNode(null, this.wordsBackgroundColor, this.wordsForegroundColor, this.wordsEdgeColor, LOADBALANCING_EDGE_STYLE, -1, null, images[ImageIndex.WORDS.ordinal()]);
		this.semRelationsLoadBalancer.setGroupNode(null, this.relationsBackgroundColor, this.relationsForegroundColor, this.relationsEdgeColor, LOADBALANCING_EDGE_STYLE, -1, null, images[ImageIndex.SYNSET.ordinal()]);
	}

	// P R O V I D E R

	@Override
	public void setLocator(@Nullable final ILocator locator)
	{
		if (locator == null)
		{
			return;
		}
		@Nullable final URL base = locator.getBase();
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
		this.sourceUrl = getURL(parameters, Parameters.KEY_DATA);

		// filter
		this.filter = getLong(parameters, Parameters.KEY_RELATION_FILTER, BaseProvider.FILTER_DEFAULT);

		// max
		this.maxRelations = getInteger(parameters, Parameters.KEY_RELATED_MAX, BaseProvider.MAX_RELATED_DEFAULT);
		this.maxRecurse = getInteger(parameters, Parameters.KEY_RECURSE_MAX, BaseProvider.MAX_RECURSE_DEFAULT);

		// load balancing
		this.loadBalanceMembers = getBoolean(parameters, Parameters.KEY_LOADBALANCE_WORDS, true);
		this.loadBalanceSemRelations = getBoolean(parameters, Parameters.KEY_LOADBALANCE_SEMRELATIONS, true);

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

		this.relationsBackgroundColor = getColor(parameters, Parameters.KEY_RELATIONS_BCOLOR, BaseProvider.COLORS0[BaseProvider.RELATIONSBACKGROUND_IDX]);
		this.relationsForegroundColor = getColor(parameters, Parameters.KEY_RELATIONS_FCOLOR, BaseProvider.COLORS0[BaseProvider.RELATIONSFOREGROUND_IDX]);
		this.relationsEdgeColor = getColor(parameters, Parameters.KEY_RELATIONS_ECOLOR, BaseProvider.COLORS0[BaseProvider.RELATIONSEDGE_IDX]);

		this.relationBackgroundColor = getColor(parameters, Parameters.KEY_RELATION_BCOLOR, BaseProvider.COLORS0[BaseProvider.RELATIONBACKGROUND_IDX]);
		this.relationForegroundColor = getColor(parameters, Parameters.KEY_RELATION_FCOLOR, BaseProvider.COLORS0[BaseProvider.RELATIONFOREGROUND_IDX]);
		this.relationEdgeColor = getColor(parameters, Parameters.KEY_RELATION_ECOLOR, BaseProvider.COLORS0[BaseProvider.RELATIONEDGE_IDX]);

		this.stemBackgroundColor = getColor(parameters, Parameters.KEY_STEM_BCOLOR, BaseProvider.COLORS0[BaseProvider.STEMBACKGROUND_IDX]);
		this.stemForegroundColor = getColor(parameters, Parameters.KEY_STEM_FCOLOR, BaseProvider.COLORS0[BaseProvider.STEMFOREGROUND_IDX]);
		this.stemEdgeColor = getColor(parameters, Parameters.KEY_STEM_ECOLOR, BaseProvider.COLORS0[BaseProvider.STEMEDGE_IDX]);

		this.etcBackgroundColor = getColor(parameters, Parameters.KEY_ETC_BCOLOR, BaseProvider.COLORS0[BaseProvider.ETCBACKGROUND_IDX]);
		this.etcForegroundColor = getColor(parameters, Parameters.KEY_ETC_FCOLOR, BaseProvider.COLORS0[BaseProvider.ETCFOREGROUND_IDX]);
		this.etcEdgeColor = getColor(parameters, Parameters.KEY_ETC_ECOLOR, BaseProvider.COLORS0[BaseProvider.ETCEDGE_IDX]);

		this.wordsBackgroundColor = getColor(parameters, Parameters.KEY_WORDS_BCOLOR, BaseProvider.COLORS0[BaseProvider.WORDSBACKGROUND_IDX]);
		this.wordsForegroundColor = getColor(parameters, Parameters.KEY_WORDS_FCOLOR, BaseProvider.COLORS0[BaseProvider.WORDSFOREGROUND_IDX]);
		this.wordsEdgeColor = getColor(parameters, Parameters.KEY_WORDS_ECOLOR, BaseProvider.COLORS0[BaseProvider.WORDSEDGE_IDX]);

		// word colors
		@Nullable final Integer wordBackgroundColor0 = getColor(parameters, Parameters.KEY_WORD_BCOLOR, null);
		if (wordBackgroundColor0 != null)
		{
			this.wordBackgroundColors[0] = wordBackgroundColor0;
		}
		@Nullable final Integer wordForegroundColor0 = getColor(parameters, Parameters.KEY_WORD_FCOLOR, null);
		if (wordForegroundColor0 != null)
		{
			this.wordForegroundColors[0] = wordForegroundColor0;
		}

		// remote word colors
		if (this.wordBackgroundColors.length > 1)
		{
			@Nullable final Integer remoteWordBackgroundColor = getColor(parameters, Parameters.KEY_RELATEDWORD_BCOLOR, null);
			// System.out.printf("new Color(0x%X),", remoteWordBackgroundColor.getRGB() & 0xFFFFFF);
			if (remoteWordBackgroundColor != null)
			{
				this.wordBackgroundColors[1] = remoteWordBackgroundColor;
				for (int i = 2; i < this.wordBackgroundColors.length; i++)
				{
					@Nullable final Integer color = Colors.makeBrighter(this.wordBackgroundColors[i - 1]);
					// System.out.printf("new Color(0x%X), ", color.getRGB() & 0xFFFFFF);
					this.wordBackgroundColors[i] = color;
				}
			}
		}
		if (this.wordForegroundColors.length > 1)
		{
			@Nullable final Integer remoteWordForegroundColor = getColor(parameters, Parameters.KEY_RELATEDWORD_FCOLOR, null);
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
		this.membersLoadBalancer.setGroupNode(null, this.wordsBackgroundColor, this.wordsForegroundColor, this.wordsEdgeColor, LOADBALANCING_EDGE_STYLE, ImageIndex.WORDS.ordinal(), null, null);
		this.semRelationsLoadBalancer.setGroupNode(null, this.relationsBackgroundColor, this.relationsForegroundColor, this.relationsEdgeColor, LOADBALANCING_EDGE_STYLE, ImageIndex.SYNSET.ordinal(), null, null);
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
	 * Get semRelations load balancing
	 *
	 * @return semRelations load balancing flag
	 */
	public boolean isLoadBalanceSemRelations()
	{
		return this.loadBalanceSemRelations;
	}

	/**
	 * Set semRelations load balancing
	 *
	 * @param flag the semRelations load balancing flag to set
	 */
	public void setLoadBalanceSemRelations(boolean flag)
	{
		this.loadBalanceSemRelations = flag;
	}

	/**
	 * Parameter to source data set
	 *
	 * @param dataKey data key, values are 'OEWN' or 'WN31' or URL string
	 * @return url
	 */
	@NonNull
	private URL getURL(@NonNull final Properties parameters, final String dataKey)
	{
		@NonNull final String dataValue = parameters.getProperty(dataKey, DATA_DEFAULT);
		try
		{
			@Nullable URL zipUrl = DataManager.getSourceZipURL(dataValue);
			if (zipUrl == null)
			{
				throw new IOException("No resource for " + dataValue);
			}

			return zipUrl;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parameter to color
	 *
	 * @param parameters   parameters
	 * @param key          parameter key
	 * @param defaultColor default value
	 * @return color value
	 */
	@Nullable
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

	// D A T A M A N A G E R

	/**
	 * Get data manager
	 *
	 * @return data manager
	 */
	protected BaseDataManager getDataManager()
	{
		return DataManager.getInstance();
	}

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
				this.dictionary = new Dictionary(getDataManager().getDataDir(this.sourceUrl, this.cache));
			}
			if (!this.dictionary.isOpen())
			{
				this.dictionary.open();
			}
		}
		catch (@NonNull final Exception e)
		{
			e.printStackTrace();
			return null;
		}

		// stemmer
		if (this.stemmer == null)
		{
			this.stemmer = new WordnetStemmer(this.dictionary);
		}

		// tree
		@Nullable final Tree tree = makeTree(source, base, parameters, false);
		if (tree == null)
		{
			return null;
		}

		// settings
		@Nullable final List<INode> children = tree.getRoot().getChildren();
		final int size = children == null ? 0 : children.size();
		@NonNull final Settings settings = makeSettings(size);

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
		@NonNull final List<IEdge> edges = new ArrayList<>();
		@Nullable final INode rootNode = walk(query, true, edges);

		// result
		if (rootNode == null)
			return null;
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
		@NonNull final Settings settings = new Settings();
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

		@NonNull final MenuItem searchEqualMenuItem = new MenuItem();
		searchEqualMenuItem.action = Action.SEARCH;
		searchEqualMenuItem.label = "Search for item that matches '$e'";
		searchEqualMenuItem.matchMode = MatchMode.EQUALS;
		searchEqualMenuItem.matchScope = MatchScope.LABEL;
		searchEqualMenuItem.link = "$e";
		settings.menu.add(searchEqualMenuItem);

		@NonNull final MenuItem searchLabelIncludesMenuItem = new MenuItem();
		searchLabelIncludesMenuItem.action = Action.SEARCH;
		searchLabelIncludesMenuItem.label = "Search for item that includes '$e'";
		searchLabelIncludesMenuItem.matchMode = MatchMode.INCLUDES;
		searchLabelIncludesMenuItem.matchScope = MatchScope.LABEL;
		searchLabelIncludesMenuItem.link = "$e";
		settings.menu.add(searchLabelIncludesMenuItem);

		@NonNull final MenuItem searchContentIncludesMenuItem = new MenuItem();
		searchContentIncludesMenuItem.action = Action.SEARCH;
		searchContentIncludesMenuItem.label = "Search for content that includes '$e'";
		searchContentIncludesMenuItem.matchMode = MatchMode.INCLUDES;
		searchContentIncludesMenuItem.matchScope = MatchScope.CONTENT;
		searchContentIncludesMenuItem.link = "$e";
		settings.menu.add(searchContentIncludesMenuItem);

		@NonNull final MenuItem gotoMenuItem = new MenuItem();
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
			@NonNull final String id = query.substring(1);

			// synset
			final Synset synset0 = this.dictionary.getSynset(SynsetID.parseSynsetID(id));
			if (synset0 == null)
			{
				return null;
			}
			@NonNull final SynsetData synset = new SynsetData(synset0);

			// synset node
			@NonNull final TreeMutableNode synsetNode = makeSynsetNode(null, synset);
			synsetNode.setLabel(BaseProvider.mangleString(BaseProvider.members(synset, ", ")));
			setNodeImage(synsetNode, null, ImageIndex.ROOT);

			// synset node (ignore relations)
			walkSynset(synsetNode, synset, 0, 0);

			// relations
			walkSemRelations(synsetNode, synset, 0, 0, recurse, edges);

			return synsetNode;
		}

		// break query into components: lemma, pos filter, sensefilter
		@NonNull final String[] queryPath = query.split(",");
		final POS posFilter = queryPath.length > 1 ? POS.getPartOfSpeech(queryPath[1].trim().charAt(0)) : null;
		@Nullable final Integer senseFilter = queryPath.length > 2 ? Integer.valueOf(queryPath[2].trim()) : null;

		// query
		@NonNull final INode root = walk(BaseProvider.normalize(queryPath[0].trim()), posFilter, senseFilter, recurse, edges);

		// stems if no results
		@Nullable final List<INode> children = root.getChildren();
		if (children == null || children.isEmpty())
		{
			for (@NonNull final POS pos : POS.values())
			{
				final List<String> stems = this.stemmer.findStems(BaseProvider.normalize(queryPath[0].trim()), pos);
				if (stems.isEmpty())
				{
					continue;
				}

				@Nullable MutableNode posNode = null;
				for (final String stem : stems)
				{
					final Index idx = this.dictionary.getIndex(stem, pos); // a line in an index file
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
		@NonNull final INode rootNode = makeRootNode(lemma);

		// iterate on parts of speech
		for (@NonNull final POS pos : POS.values())
		{
			if (posFilter != null && !pos.equals(posFilter))
			{
				continue;
			}

			final Index idx = this.dictionary.getIndex(lemma, pos); // a line in an index file
			if (idx == null)
			{
				continue;
			}
			int posSenseIdx = 0;
			int tagCountTotal = 0;

			// pos node
			@NonNull final INode posNode = makePosNode(rootNode, pos, lemma);

			// sense map per lexfile/category
			@NonNull final Map<LexFile, List<SenseData>> senseDataMap = new HashMap<>();
			@NonNull final Map<LexFile, Collection<Synset.Sense>> senseDataMap2 = hierarchize(idx.getSenseIDs());
			for (final LexFile lexFile : senseDataMap2.keySet())
			{
				final Collection<Synset.Sense> senses = senseDataMap2.get(lexFile);
				if (senses != null)
				{
					// sense
					for (@NonNull final Synset.Sense sense : senses)
					{
						++globalSenseIdx;
						++posSenseIdx;

						// synset
						final Synset synset = sense.getSynset();

						// lexid
						final int lexId = sense.getLexicalID();

						// sensekey
						final SenseKey senseKey = sense.getSenseKey();

						// senseentry
						final SenseEntry senseEntry = this.dictionary.getSenseEntry(senseKey);

						// sensenum, tagcount
						assert senseEntry != null;
						final int senseNum = senseEntry.getSenseNumber();
						final int tagCount = senseEntry.getTagCount();
						tagCountTotal += tagCount;

						// add to list
						@NonNull final SenseData senseData = new SenseData(pos, senseKey, sense, synset, lexId, posSenseIdx, globalSenseIdx, senseNum, tagCount);
						@NonNull List<SenseData> senseDatas = senseDataMap.computeIfAbsent(lexFile, k -> new ArrayList<>());
						senseDatas.add(senseData);
					}
				}
			}

			// sense map
			for (@NonNull final LexFile lexFile : senseDataMap.keySet())
			{
				final Collection<SenseData> senseDatas = senseDataMap.get(lexFile);
				if (senseDatas != null)
				{
					// category
					@NonNull final INode categoryNode = makeCategoryNode(posNode, lexFile);

					// scan list
					int i = 0;
					for (@NonNull final SenseData senseData : senseDatas)
					{
						if (senseFilter != null && senseData.senseNum != senseFilter)
						{
							continue;
						}

						// sense node
						@NonNull final TreeMutableNode senseNode = makeSenseNode(categoryNode, senseData, tagCountTotal);

						// synset node (ignore relations)
						walkSynset(senseNode, senseData.synset, i, 0);

						// relations
						final Map<Pointer, List<SynsetID>> semRelations = senseData.synset.synset.getRelatedSynsets();
						final Map<Pointer, List<SenseID>> lexRelations = senseData.sense.getRelatedSenses();
						walkRelations(senseNode, semRelations, lexRelations, i, 0, recurse, edges);

						// walkSemRelations(senseNode, senseData.synset, 0, recurse);
						// walkLexRelations(senseNode, senseData.sense, 0);

						i++;
					}
				}
			}
		}
		return rootNode;
	}

	/**
	 * Walk synset content (excluding relations)
	 *
	 * @param parentNode parent node
	 * @param synset     synset
	 * @param index      index of synset
	 * @param level      recursion level
	 */
	protected void walkSynset(@NonNull final TreeMutableNode parentNode, @NonNull final SynsetData synset, @SuppressWarnings("unused") final int index, final int level)
	{
		if ((this.features & FEATURE_SYNSET_MERGE_SINGLE_MEMBER_TO_PARENT_IF_NOT_BASE_LEVEL) != 0)
		{
			if (level > 0)
			{
				final Synset.Sense[] words = synset.synset.getSenses();
				if (words.length == 1)
				{
					// P = m1
					final Synset.Sense word = words[0];

					final String lemma = word.getLemma();
					parentNode.setLabel(BaseProvider.printable(lemma));
					parentNode.setContent(glossContent(synset.gloss));
					parentNode.setLink(BaseProvider.URLSCHEME + lemma);

					decorateAsWord(parentNode, level);
					setNodeImage(parentNode, null, ImageIndex.WORD);
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
			@NonNull final MutableNode node = buildSingleMemberNode(parentNode, synset, level);

			node.setEdgeLabel(LABEL_WORD);
			node.setEdgeStyle(EDGE_STYLE_WORD);
			node.setEdgeColor(this.wordsEdgeColor);
			return;
		}

		// P < M < m1 m2 m3
		@NonNull final TreeMutableNode membersNode = makeSynsetMembersNode(parentNode, synset);
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
	protected void walkMembers(@NonNull final TreeMutableNode parentNode, @NonNull final SynsetData synset, final int level)
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
	private void walkMembersNoLoadBalancing(final TreeMutableNode parentNode, @NonNull final SynsetData synset, final int level)
	{
		for (@NonNull final Synset.Sense word : synset.synset.getSenses())
		{
			@NonNull final INode node = makeWordNode(parentNode, word, synset.gloss, level);
			node.setEdgeLabel(LABEL_WORD);
			node.setEdgeStyle(EDGE_STYLE_WORD);
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
	private void walkMembersLoadBalancing(@NonNull final TreeMutableNode parentNode, @NonNull final SynsetData synset, final int level)
	{
		// make list
		@Nullable List<INode> memberNodes = new ArrayList<>();
		for (@NonNull final Synset.Sense word : synset.synset.getSenses())
		{
			@NonNull final TreeMutableNode node = makeWordNode(null, word, synset.gloss, level);
			node.setEdgeLabel(LABEL_WORD);
			node.setEdgeStyle(EDGE_STYLE_WORD);
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
	 * Walk semantic and lexical relations
	 *
	 * @param parentNode   parent node
	 * @param semRelations sem relations
	 * @param lexRelations lex relations
	 * @param index        synset index
	 * @param level        level
	 * @param recurse      recurse
	 * @param edges        edges
	 */
	protected void walkRelations(final TreeMutableNode parentNode, @NonNull final Map<Pointer, List<SynsetID>> semRelations, @NonNull final Map<Pointer, List<SenseID>> lexRelations, final int index, @SuppressWarnings("SameParameterValue") final int level, final boolean recurse, @NonNull final List<IEdge> edges)
	{
		if (semRelations.isEmpty() && lexRelations.isEmpty())
		{
			return;
		}

		TreeMutableNode anchorNode = parentNode;

		if ((this.features & FEATURE_COLLAPSE_RELATIONS) == 0)
		{
			// create relations node
			// P < L < 11 l2 l3
			@NonNull final String semLabel = relations(semRelations.keySet());
			@NonNull final String lexLabel = relations(lexRelations.keySet());
			@NonNull final String label = (semLabel.isEmpty() ? "-" : semLabel) + "<br/>/<br/>" + (lexLabel.isEmpty() ? "-" : lexLabel);
			anchorNode = makeSynsetRelationsNode(parentNode, label);
		}

		// else
		// do not use relations super node
		// P < 11 l2 l3

		walkSemRelations(anchorNode, semRelations, index, level, recurse, edges);
		walkLexRelations(anchorNode, lexRelations, level);
	}

	/**
	 * Walk semantic relations of the same type
	 *
	 * @param parentNode parent node
	 * @param synset     synset
	 * @param index      index
	 * @param level      recursion level
	 * @param recurse    whether to recurse
	 * @param edges      edges
	 */
	protected void walkSemRelations(final TreeMutableNode parentNode, @NonNull final SynsetData synset, @SuppressWarnings("SameParameterValue") final int index, final int level, final boolean recurse, @NonNull final List<IEdge> edges)
	{
		final Map<Pointer, List<SynsetID>> semRelations = synset.synset.getRelatedSynsets();
		if (!semRelations.isEmpty())
		{
			@NonNull final TreeMutableNode relationsNode = makeSynsetRelationsNode(parentNode, relations(semRelations.keySet()));
			walkSemRelations(relationsNode, semRelations, index, level, recurse, edges);
		}
	}

	/**
	 * Walk semantic relations
	 *
	 * @param parentNode   parent node
	 * @param semRelations related synset ids classified by pointer
	 * @param index        index
	 * @param level        recursion level
	 * @param recurse      whether to recurse
	 * @param edges        edges
	 */
	protected void walkSemRelations(final TreeMutableNode parentNode, @NonNull final Map<Pointer, List<SynsetID>> semRelations, final int index, final int level, final boolean recurse, @NonNull final List<IEdge> edges)
	{
		// P=relations node L

		// iterate pointers
		for (@NonNull final Pointer pointer : semRelations.keySet())
		{
			@NonNull final Relation relation = Relation.valueOf(pointer);
			if (!relation.test(this.filter))
			{
				continue;
			}

			// related synset(s)
			final List<SynsetID> relatedSynsetIds = semRelations.get(relation.pointer);
			if (relatedSynsetIds != null)
			{
				if ((this.features & FEATURE_SEMRELATIONS_MERGE_SINGLE_RELATED_SYNSET_TO_RELATION) != 0)
				{
					// P < l=s1
					// do not make synset node when only one synset is related, attach synset content to relation node directly
					final boolean singleSynset = relatedSynsetIds.size() == 1;
					if (singleSynset)
					{
						// single synset (no load balancing)
						@NonNull final Iterator<SynsetID> it = relatedSynsetIds.iterator();
						if (it.hasNext())
						{
							// related synset node
							final SynsetID relatedSynsetId = it.next();
							final Synset relatedSynset0 = this.dictionary.getSynset(relatedSynsetId);
							if (relatedSynset0 != null)
							{
								@NonNull final SynsetData relatedSynset = new SynsetData(relatedSynset0);

								// relation node l
								@NonNull final TreeMutableNode relationNode = makeRelationNode(parentNode, relation);

								// populate related synset node
								walkRelatedSynset1(relationNode, relatedSynset, relation, index, level + 1, recurse && relation.recurses, edges);
							}
						}
						continue;
					}
				}

				// multiple synsets
				// P < l < s1 s2 s3
				@Nullable INode etcNode = null;

				final int n = relatedSynsetIds.size();
				int i = 0;

				@Nullable List<INode> childNodes = new ArrayList<>();
				@NonNull final Iterator<SynsetID> it = relatedSynsetIds.iterator();
				while (it.hasNext())
				{
					// related synset node
					final SynsetID relatedSynsetId = it.next();
					final Synset relatedSynset0 = this.dictionary.getSynset(relatedSynsetId);
					if (relatedSynset0 == null)
					{
						continue;
					}
					@NonNull final SynsetData relatedSynset = new SynsetData(relatedSynset0);
					@NonNull final TreeMutableNode relatedSynsetNode = makeSynsetNode(null, relatedSynset);

					// populate related synset node
					walkRelatedSynset1(relatedSynsetNode, relatedSynset, relation, index, level + 1, recurse && relation.recurses, edges);
					@NonNull final String tag = Integer.toString(i + 1);
					relatedSynsetNode.setTarget(tag);
					// relatedSynsetNode.setLabel(tag);

					// list
					childNodes.add(relatedSynsetNode);

					// limit
					if (++i >= this.maxRelations && i < n)
					{
						etcNode = makeEtcRelationNode(null, relation, n, it);
						break;
					}
				}

				// semRelations nodes
				if (this.loadBalanceSemRelations)
				{
					childNodes = this.semRelationsLoadBalancer.buildHierarchy(childNodes, relation.imageIndex);
				}

				// relation node l
				@NonNull final TreeMutableNode relationNode = makeRelationNode(parentNode, relation);
				if (childNodes != null)
				{
					relationNode.addChildren(childNodes);
				}

				// etc node
				if (etcNode != null)
				{
					relationNode.addChild(etcNode);
				}
			}
		}
	}

	/**
	 * Walk related synset called from walkSemRelations
	 *
	 * @param parentNode    parent node
	 * @param relatedSynset synset
	 * @param relation      relation
	 * @param index         synset index
	 * @param level         recursion level
	 * @param recurse       recurse
	 * @param edges         edges
	 */
	protected void walkRelatedSynset1(@NonNull final TreeMutableNode parentNode, @NonNull final SynsetData relatedSynset, @NonNull final Relation relation, final int index, final int level, final boolean recurse, @NonNull final List<IEdge> edges)
	{
		// synset : members
		walkSynset(parentNode, relatedSynset, index, level);

		// similar to
		if (relation.pointer == Pointer.getSIMILAR_TO() && relatedSynset.synset.isAdjectiveHead())
		{
			walkAntonymFromHeadAdjective(parentNode, relatedSynset, level);
		}

		// recurse
		if (recurse && level < this.maxRecurse)
		{
			// iterate related synsets
			final List<SynsetID> relatedSynsetIds = relatedSynset.synset.getRelatedSynsetsFor(relation.pointer);
			if (!relatedSynsetIds.isEmpty())
			{
				@NonNull TreeMutableNode anchorNode = parentNode;
				if ((this.features & FEATURE_RELATEDSYNSET1_FORGET_RELATION_NODE) == 0)
				{
					// P < l < s
					anchorNode = makeRelationNode(parentNode, relation);
				}

				// else if FEATURE_RAISE_RECURSE_TO_SYNSET_AT_BASE_LEVEL
				// P < s
				walkTypedRelation(anchorNode, relatedSynsetIds, relation, level + 1, edges);
			}
		}
	}

	/**
	 * Walk lexical relations
	 *
	 * @param parentNode parent node
	 * @param sense      sense
	 * @param level      recursion level
	 */
	protected void walkLexRelations(final TreeMutableNode parentNode, @NonNull final Synset.Sense sense, final int level)
	{
		final Map<Pointer, List<SenseID>> lexRelations = sense.getRelatedSenses();
		if (!lexRelations.isEmpty())
		{
			@NonNull final TreeMutableNode relationsNode = makeSynsetRelationsNode(parentNode, relations(lexRelations.keySet()));
			walkLexRelations(relationsNode, lexRelations, level);
		}
	}

	/**
	 * Walk lexical relations
	 *
	 * @param parentNode   parent node
	 * @param lexRelations related sense ids classified by pointer
	 * @param level        current recursion level
	 */
	protected void walkLexRelations(final TreeMutableNode parentNode, @NonNull final Map<Pointer, List<SenseID>> lexRelations, final int level)
	{
		// iterate pointers
		for (@NonNull final Pointer pointer : lexRelations.keySet())
		{
			@NonNull final Relation relation = Relation.valueOf(pointer);
			if (!relation.test(this.filter))
			{
				continue;
			}

			@NonNull final INode relationNode = makeRelationNode(parentNode, relation);

			// iterate related senses
			final List<SenseID> relatedSenseIds = lexRelations.get(relation.pointer);
			if (relatedSenseIds != null)
			{
				for (@Nullable final SenseID relatedSenseId : relatedSenseIds)
				{
					if (relatedSenseId == null)
					{
						continue;
					}

					// lex node
					/* final INode lexNode = */
					makeLexNode(relationNode, relatedSenseId, level);

					// synset
					// final Synset synset = new Synset(relatedSynset);
					// final INode synsetNode = makeSynsetNode(lexNode, synset);
					// walkSynset(synsetNode, synset, level);
				}
			}
		}
	}

	/**
	 * Walk relations following only one type
	 *
	 * @param parentNode       parent node
	 * @param relatedSynsetIds related synset ids
	 * @param relation         relation
	 * @param level            recursion level
	 * @param edges            edges
	 */
	protected void walkTypedRelation(@NonNull final TreeMutableNode parentNode, @NonNull final List<SynsetID> relatedSynsetIds, @NonNull final Relation relation, final int level, @NonNull final List<IEdge> edges)
	{
		// iterate related synsets
		@Nullable INode etcNode = null;

		int i = 0;
		final int n = relatedSynsetIds.size();
		@Nullable List<INode> childNodes = new ArrayList<>();
		@NonNull final Iterator<SynsetID> it = relatedSynsetIds.iterator();
		while (it.hasNext())
		{
			final SynsetID relatedSynsetId = it.next();
			final Synset relatedSynset0 = this.dictionary.getSynset(relatedSynsetId);
			if (relatedSynset0 == null)
			{
				continue;
			}

			// synset
			@NonNull final SynsetData relatedSynset = new SynsetData(relatedSynset0);
			@NonNull final String tag = Integer.toString(i + 1);

			// node for synset
			TreeMutableNode relatedSynsetNode;
			if ((this.features & FEATURE_TYPEDRELATION_RAISE_RECURSE_AS_SIBLING) != 0)
			{
				// l < s1(=M < m1 m2 m3)->l1 s2(=M < m1 m2 <m3)->l2
				// members super node
				relatedSynsetNode = buildMembersNode(relatedSynset, level);
			}
			else
			{
				if ((this.features & FEATURE_TYPEDRELATION_RAISE_SINGLE_MEMBER_TO_SYNSET) != 0 && hasSingleMember(relatedSynset))
				{
					relatedSynsetNode = buildSingleMemberNode(null, relatedSynset, level);
					relatedSynsetNode.setEdgeLabel(null);
					relatedSynsetNode.setEdgeColor(this.relationBackgroundColor);
					setTreeEdgeImage(relatedSynsetNode, null, ImageIndex.values()[relation.imageIndex]);
				}
				else
				{
					// l < (s1 < l1) (s2 < l2) (s3 < l3)
					// synset node
					relatedSynsetNode = makeSynsetNode(null, relatedSynset);
					relatedSynsetNode.setEdgeLabel(null);
					relatedSynsetNode.setEdgeColor(this.relationBackgroundColor);
					setTreeEdgeImage(relatedSynsetNode, null, ImageIndex.values()[relation.imageIndex]);

					// members
					walkSynset(relatedSynsetNode, relatedSynset, i, level);
				}
			}

			// list
			relatedSynsetNode.setTarget(tag);
			childNodes.add(relatedSynsetNode);

			// recurse
			if (level < this.maxRecurse)
			{
				// iterate related synsets
				final List<SynsetID> childRelatedSynsetIds = relatedSynset.synset.getRelatedSynsetsFor(relation.pointer);
				if (!childRelatedSynsetIds.isEmpty())
				{
					if ((this.features & FEATURE_TYPEDRELATION_RAISE_RECURSE_AS_SIBLING) != 0)
					{
						@NonNull final TreeMutableNode relationNode = makeRelationNode(null, relation);
						walkTypedRelation(relationNode, childRelatedSynsetIds, relation, level + 1, edges);
						relationNode.setLabel(tag);
						relationNode.setTarget(tag);
						childNodes.add(relationNode);

						@NonNull final MutableEdge edge = new MutableEdge(relatedSynsetNode, relationNode);
						edge.setLabel(relation.label);
						edge.setColor(this.edgeColor);
						edge.setStyle(EDGE_STYLE_EDGE);
						edges.add(edge);
					}
					else
					{
						@NonNull TreeMutableNode anchorNode = relatedSynsetNode;
						if ((this.features & FEATURE_TYPEDRELATION_FORGET_RELATION_NODE) == 0)
						{
							anchorNode = makeRelationNode(relatedSynsetNode, relation);
						}
						walkTypedRelation(anchorNode, childRelatedSynsetIds, relation, level + 1, edges);
						anchorNode.setTarget(tag);
					}
				}
			}

			// limit
			if (++i >= this.maxRelations && i < n)
			{
				etcNode = makeEtcRelationNode(null, relation, n, it);
				break;
			}
		}

		// semRelations nodes
		if (this.loadBalanceSemRelations)

		{
			childNodes = this.semRelationsLoadBalancer.buildHierarchy(childNodes, relation.imageIndex);
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
	protected void walkAntonymFromHeadAdjective(final TreeMutableNode parentNode, @NonNull final SynsetData synset, final int level)
	{
		final boolean isHead = synset.synset.isAdjectiveHead();
		if (isHead)
		{
			final Synset.Sense head = synset.synset.getSenses()[1];
			final List<SenseID> antonymSenseIds = head.getRelatedSenseFor(Pointer.getANTONYM());
			@Nullable INode antonymRelationNode = null;

			// follow up on antonyms
			for (@NonNull final SenseID antonymSenseId : antonymSenseIds)
			{
				if (antonymRelationNode == null)
				{
					antonymRelationNode = makeRelationNode(parentNode, Relation.ANTONYM);
				}
				makeLexNode(antonymRelationNode, antonymSenseId, level + 1);
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
	protected TreeMutableNode buildMembersNode(@NonNull final SynsetData synset, final int level)
	{
		@NonNull final TreeMutableNode membersNode = makeSynsetNode(null, synset);
		for (@NonNull final Synset.Sense word : synset.synset.getSenses())
		{
			@NonNull final INode node = makeWordNode(membersNode, word, synset.gloss, level);

			node.setEdgeLabel(LABEL_WORD);
			node.setEdgeStyle(EDGE_STYLE_WORD);
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
	protected TreeMutableNode buildSingleMemberNode(final INode parent, @NonNull final SynsetData synset, final int level)
	{
		final Synset.Sense word = synset.synset.getSenses()[0];
		@NonNull final TreeMutableNode node = makeWordNode(parent, word, synset.gloss, level);

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
		@NonNull final MutableNode node = new MutableNode(parent, BaseProvider.makeNodeId(), pos.name().toLowerCase(Locale.ENGLISH), -1, this.posBackgroundColor, this.posForegroundColor);
		if (word != null)
		{
			node.setLink(BaseProvider.URLSCHEME + word + ',' + pos.getTag());
		}
		node.setEdgeColor(this.posEdgeColor);
		node.setEdgeStyle(EDGE_STYLE_POS);
		setTreeEdgeImage(node, null, ImageIndex.POS);
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
	protected MutableNode makeCategoryNode(final INode parent, @NonNull final LexFile lexFile)
	{
		@NonNull final MutableNode node = new MutableNode(parent, BaseProvider.makeNodeId(), BaseProvider.category(lexFile), -1, this.categoryBackgroundColor, this.categoryForegroundColor);
		node.setContent(lexFile.getDescription());
		node.setEdgeLabel(LABEL_CATEGORY);
		node.setEdgeStyle(EDGE_STYLE_CATEGORY);
		node.setEdgeColor(this.categoryEdgeColor);
		// setTreeEdgeImage(node, null, ImageIndex.CATEGORY);
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
	protected TreeMutableNode makeSenseBaseNode(final INode parent, @NonNull final SenseData sense, final int tagCountTotal)
	{
		final float share = tagCountTotal == 0 ? 0F : sense.tagCount / (float) tagCountTotal * 100F;
		@NonNull final String shareString = String.format(Locale.ENGLISH, "%.0f%%", share);
		@NonNull final String shareString2 = String.format(Locale.ENGLISH, "%.2f%%", share);
		@NonNull final String label = share != 0F ? shareString : String.format(Locale.ENGLISH, "#%d", sense.senseNum);
		@NonNull final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), label, -1, this.senseBackgroundColor, this.senseForegroundColor);
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
	protected TreeMutableNode makeSenseNode(final INode parent, @NonNull final SenseData sense, final int tagCountTotal)
	{
		@NonNull final TreeMutableNode node = makeSenseBaseNode(parent, sense, tagCountTotal);
		node.setEdgeLabel(LABEL_SENSE);
		setTreeEdgeImage(node, null, ImageIndex.SENSE);
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
	protected TreeMutableNode makeSynsetNode(final INode parent, @NonNull final SynsetData synset)
	{
		@NonNull final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), null, ImageIndex.SYNSET.ordinal(), this.synsetBackgroundColor, this.synsetForegroundColor);
		node.setContent(glossContent(synset.gloss));
		node.setLink(BaseProvider.URLSCHEME + BaseProvider.URLSCHEME_AT + synset.synset.getID());
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
	protected TreeMutableNode makeSynsetMembersNode(final INode parent, @NonNull final SynsetData synset)
	{
		@NonNull final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), null, ImageIndex.WORDS.ordinal(), this.wordsBackgroundColor, this.wordsForegroundColor);
		node.setContent("<div class='members'>" + BaseProvider.members(synset, ", ") + "</div>");
		node.setLink(BaseProvider.URLSCHEME + BaseProvider.URLSCHEME_AT + synset.synset.getID());
		node.setEdgeLabel(LABEL_WORDS);
		node.setEdgeStyle(EDGE_STYLE_WORDS);
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
	protected TreeMutableNode makeWordNode(final INode parent, @NonNull final Synset.Sense word, @NonNull final Gloss gloss, final int level)
	{
		final String lemma = word.getLemma();
		final int colorIndex = level % this.wordBackgroundColors.length;
		@NonNull final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), BaseProvider.printable(lemma), ImageIndex.WORD.ordinal(), this.wordBackgroundColors[colorIndex], this.wordForegroundColors[colorIndex]);
		node.setContent(glossContent(gloss));
		node.setLink(BaseProvider.URLSCHEME + lemma);
		// do not assume anything about link
		return node;
	}

	/**
	 * Make relations group node
	 *
	 * @param parent   parent node
	 * @param pointers pointers
	 * @return node
	 */
	@NonNull
	protected TreeMutableNode makeSynsetRelationsNode(final INode parent, final String pointers)
	{
		@NonNull final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), null, ImageIndex.RELATIONS.ordinal(), this.relationsBackgroundColor, this.relationsForegroundColor);
		node.setContent(pointers);
		node.setEdgeLabel(LABEL_RELATIONS);
		node.setEdgeStyle(EDGE_STYLE_RELATIONS);
		node.setEdgeColor(this.relationsEdgeColor);
		return node;
	}

	/**
	 * Make relation node
	 *
	 * @param parent   parent node
	 * @param relation relation
	 * @return node
	 */
	@NonNull
	protected TreeMutableNode makeRelationNode(final INode parent, @NonNull final Relation relation)
	{
		final int imageIndex = relation.imageIndex;
		final String label = relation.label;
		@NonNull final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), imageIndex != -1 ? null : label, imageIndex, this.relationBackgroundColor, this.relationForegroundColor);
		node.setContent("<div class='reference'>" + relation.label + "</div>");
		node.setLink(HELP_URLSCHEME + relation.tag);
		node.setEdgeLabel(label);
		node.setEdgeStyle(EDGE_STYLE_RELATION);
		node.setEdgeColor(this.relationEdgeColor);
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
	private TreeMutableNode makeLexNode(final INode parent, @NonNull final SenseID senseId, final int level)
	{
		@NonNull final String lemma = lemma(senseId);
		final int colorIndex = level % this.wordBackgroundColors.length;
		@NonNull final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), lemma, ImageIndex.WORD.ordinal(), this.wordBackgroundColors[colorIndex], this.wordForegroundColors[colorIndex]);
		final SynsetID synsetId = senseId.getSynsetID();
		final Synset synset = this.dictionary.getSynset(synsetId);
		assert synset != null;
		@NonNull final Gloss gloss = new Gloss(synset.getGloss());
		node.setContent(glossContent(gloss));
		node.setLink(BaseProvider.URLSCHEME + BaseProvider.URLSCHEME_AT + synsetId);
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
	 * @return node
	 */
	@NonNull
	@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
	protected TreeMutableNode makeStemNode(final INode parent, final String stem, @NonNull final POS pos)
	{
		@NonNull final TreeMutableNode node = new TreeMutableNode(parent, BaseProvider.makeNodeId(), stem, -1, this.stemBackgroundColor, this.stemForegroundColor);
		node.setLink(BaseProvider.URLSCHEME + stem + ',' + pos.getTag());
		node.setEdgeLabel(LABEL_STEM);
		node.setEdgeStyle(EDGE_STYLE_STEM);
		node.setEdgeColor(this.stemEdgeColor);
		return node;
	}

	/**
	 * Make etcetera relation node
	 *
	 * @param parent   parent node
	 * @param relation relation
	 * @param count    count of relations
	 * @param it       current iterator
	 * @return node
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	protected MutableNode makeEtcRelationNode(final INode parent, @NonNull final Relation relation, final int count, @NonNull final Iterator<SynsetID> it)
	{
		final int imageIndex = relation.imageIndex;
		final String label = relation.label;
		@NonNull final String content = makeEtcContent(relation, count, it);
		@NonNull final MutableNode node = new MutableNode(parent, BaseProvider.makeNodeId(), "+ " + (count - this.maxRelations), imageIndex, this.etcBackgroundColor, this.etcForegroundColor);
		node.setContent(content);
		node.setEdgeLabel(label);
		node.setEdgeStyle(EDGE_STYLE_ETC);
		node.setEdgeColor(this.etcEdgeColor);
		return node;
	}

	/**
	 * Make etc content
	 *
	 * @param relation relation
	 * @param count    related synset count
	 * @param it       current iterator of related synset
	 * @return content
	 */
	@NonNull
	protected String makeEtcContent(@NonNull final Relation relation, final int count, @NonNull final Iterator<SynsetID> it)
	{
		@NonNull final StringBuilder sb = new StringBuilder();
		sb.append("<div class='more'>") //
				.append(relation.label) //
				.append(" relations: ") //
				.append(count) //
				.append("</div>");
		while (it.hasNext())
		{
			final SynsetID synsetId = it.next();
			final Synset synset = this.dictionary.getSynset(synsetId);
			if (synset == null)
			{
				continue;
			}
			sb.append("<div class='members'><a href='") //
					.append(BaseProvider.INTERNAL_URLSCHEME) //
					.append(BaseProvider.URLSCHEME) //
					.append(BaseProvider.URLSCHEME_AT) //
					.append(synsetId) //
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
	protected Map<LexFile, Collection<Synset.Sense>> hierarchize(@NonNull final List<SenseID> senseIds)
	{
		@NonNull final Map<LexFile, Collection<Synset.Sense>> map = new TreeMap<>(lexFileComparator);
		for (final SenseID senseId : senseIds)
		{
			// sense
			final Synset.Sense sense = this.dictionary.getSense(senseId);

			// category
			assert sense != null;
			int lexFileNum = sense.getSenseKey().getLexicalFileNum();
			final LexFile category = LexFile.getLexicalFile(lexFileNum);

			// map entry
			@NonNull Collection<Synset.Sense> senses = map.computeIfAbsent(category, k -> new ArrayList<>());
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
	static protected String category(@NonNull final LexFile lexFile)
	{
		final String name = lexFile.getName();
		final int cut = name.indexOf('.');
		return name.substring(cut + 1);
	}

	/**
	 * Get relations
	 *
	 * @param pointers pointers
	 * @return relations
	 */
	@NonNull
	static protected String relations(@NonNull final Set<Pointer> pointers)
	{
		@NonNull final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (@NonNull final Pointer pointer : pointers)
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
	static protected boolean hasSingleMember(@NonNull final SynsetData synset)
	{
		final Synset.Sense[] words = synset.synset.getSenses();
		return words.length == 1;
	}

	/**
	 * Get synset members
	 *
	 * @param synset    synset
	 * @param separator separator
	 * @return synset members
	 */
	@NonNull
	static protected String members(@NonNull final SynsetData synset, @SuppressWarnings("SameParameterValue") final String separator)
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
	static protected String members(@NonNull final Synset synset, final String separator)
	{
		@NonNull final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (@NonNull final Synset.Sense word : synset.getSenses())
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
	protected String lemma(@NonNull final SenseID senseId)
	{
		// sense
		Synset.Sense sense = this.dictionary.getSense(senseId);
		assert sense != null;
		String lemma = sense.getLemma();
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
	static protected String membersToString(@NonNull final SynsetData synset)
	{
		@NonNull final StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (@NonNull final Synset.Sense word : synset.synset.getSenses())
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
	static protected String membersAsLines(@NonNull final SynsetData synset)
	{
		@NonNull final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (@NonNull final Synset.Sense word : synset.synset.getSenses())
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
	static protected String[] membersOf(@NonNull final SynsetData synset)
	{
		final Synset.Sense[] words = synset.synset.getSenses();
		final int n = words.length;
		@NonNull final String[] members = new String[n];

		for (int i = 0; i < n; i++)
		{
			final Synset.Sense word = words[i];
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
	protected String senseContent(@NonNull final SenseData sense, final String share)
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
		@NonNull final StringBuilder sb = new StringBuilder();
		sb.append("<div class='definition'>");
		sb.append(gloss.getDefinition());
		sb.append("</div>");

		@NonNull final String[] samples = gloss.getSamples();
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

		@NonNull final StringBuilder sb = new StringBuilder(str0);
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

	protected void setTreeEdgeImage(@NonNull final MutableNode node, @Nullable final String edgeImageFile, @SuppressWarnings("SameParameterValue") @Nullable final ImageIndex index)
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

	protected void setEdgeImage(@NonNull final MutableEdge edge, @Nullable final String edgeImageFile, @SuppressWarnings("SameParameterValue") @Nullable final ImageIndex index)
	{
		if (edgeImageFile != null)
		{
			edge.setImageFile(edgeImageFile);
		}
		else if (index != null)
		{
			setEdgeImage(edge, index.ordinal());
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
