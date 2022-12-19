/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi;

import treebolic.annotations.Nullable;
import treebolic.glue.Image;

/**
 * Base provider for WordNet
 *
 * @author Bernard Bou
 */
@SuppressWarnings({"SameParameterValue"})
public abstract class ImageFactory
{
	/**
	 * Built images
	 */
	@Nullable
	@SuppressWarnings("WeakerAccess")
	public static final Image[] images = new Image[]{ //
			new Image(DataManager.class.getResource("images/focus.png")), // FOCUS

			new Image(DataManager.class.getResource("images/pos.png")), // POS
			new Image(DataManager.class.getResource("images/category.png")), // CATEGORY
			new Image(DataManager.class.getResource("images/sense.png")), // SENSE
			new Image(DataManager.class.getResource("images/synset.png")), // SYNSET
			new Image(DataManager.class.getResource("images/members.png")), // MEMBERS
			new Image(DataManager.class.getResource("images/links.png")), // LINKS
			new Image(DataManager.class.getResource("images/item.png")), // WORD

			new Image(DataManager.class.getResource("images/hypernym.png")), // HYPERNYM
			new Image(DataManager.class.getResource("images/instance.hypernym.png")), // HYPERNYM_INSTANCE
			new Image(DataManager.class.getResource("images/hyponym.png")), // HYPONYM
			new Image(DataManager.class.getResource("images/instance.hyponym.png")), // HYPONYM_INSTANCE

			new Image(DataManager.class.getResource("images/member.holonym.png")), // HOLONYM_MEMBER
			new Image(DataManager.class.getResource("images/substance.holonym.png")), // HOLONYM_SUBSTANCE
			new Image(DataManager.class.getResource("images/part.holonym.png")), // HOLONYM_PART

			new Image(DataManager.class.getResource("images/member.meronym.png")), // MERONYM_MEMBER
			new Image(DataManager.class.getResource("images/substance.meronym.png")), // MERONYM_SUBSTANCE
			new Image(DataManager.class.getResource("images/part.meronym.png")), // MERONYM_PART

			new Image(DataManager.class.getResource("images/antonym.png")), // ANTONYM

			new Image(DataManager.class.getResource("images/entail.png")), // ENTAILS
			new Image(DataManager.class.getResource("images/entailed.png")), // ENTAILED
			new Image(DataManager.class.getResource("images/cause.png")), // CAUSE
			new Image(DataManager.class.getResource("images/caused.png")), // CAUSED
			new Image(DataManager.class.getResource("images/verb.group.png")), // VERB_GROUP
			new Image(DataManager.class.getResource("images/participle.png")), // PARTICIPLE

			new Image(DataManager.class.getResource("images/similar.png")), // SIMILAR_TO
			new Image(DataManager.class.getResource("images/also.png")), // ALSO_SEE
			new Image(DataManager.class.getResource("images/attribute.png")), // ATTRIBUTE
			new Image(DataManager.class.getResource("images/pertainym.png")), // PERTAINYM
			new Image(DataManager.class.getResource("images/derivation.png")), // DERIVATIONALLY_RELATED
			new Image(DataManager.class.getResource("images/adjderived.png")), // DERIVED_FROM_ADJ

			new Image(DataManager.class.getResource("images/domain.png")), // DOMAIN
			new Image(DataManager.class.getResource("images/domain.category.png")), // TOPIC
			new Image(DataManager.class.getResource("images/domain.usage.png")), // USAGE
			new Image(DataManager.class.getResource("images/domain.region.png")), // REGION

			new Image(DataManager.class.getResource("images/domain.member.png")), // MEMBER
			new Image(DataManager.class.getResource("images/domain.member.category.png")), // TOPIC_MEMBER
			new Image(DataManager.class.getResource("images/domain.member.usage.png")), // USAGE_MEMBER
			new Image(DataManager.class.getResource("images/domain.member.region.png")),// REGION_MEMBER
	};
}
