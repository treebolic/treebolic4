/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi.simple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.mit.jwi.item.*;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.*;
import treebolic.provider.IProvider;
import treebolic.provider.wordnet.jwi.BaseProvider;
import treebolic.provider.wordnet.jwi.Sense;

/**
 * Simple provider for WordNet
 *
 * @author Bernard Bou
 */
public class Provider extends BaseProvider implements IProvider
{
	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @throws IOException io exception
	 */
	@SuppressWarnings("WeakerAccess")
	public Provider() throws IOException
	{
		super();
		this.features |= FEATURE_COLLAPSE_CATEGORIES | FEATURE_SYNSET_FORGET_MEMBERS_NODE_IF_SINGLE_MEMBER | FEATURE_SEMLINKS_MERGE_SINGLE_LINKED_SYNSET_TO_LINK;
	}

	// S E T T I N G S

	@NonNull
	@Override
	protected Settings makeSettings(final int childrenCount)
	{
		return super.makeSettings(childrenCount, 3);
	}

	// W A L K

	// FEATURE_COLLAPSE_CATEGORIES
	@NonNull
	@Override
	protected INode walk(@NonNull final String lemma, @Nullable final POS posFilter, @Nullable final Integer senseFilter, final boolean recurse, @NonNull final List<IEdge> edges)
	{
		int globalSenseIdx = 0;
		final MutableNode rootNode = makeRootNode(lemma);

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
			final MutableNode posNode = posFilter != null ? rootNode : makePosNode(rootNode, pos, lemma);

			// sense
			final List<Sense> senseDatas = new ArrayList<>();
			for (final IWordID senseId : idx.getWordIDs())
			{
				++globalSenseIdx;
				++posSenseIdx;

				// sense
				final IWord sense = this.dictionary.getWord(senseId);

				// synset
				final ISynset synset = sense.getSynset();

				// lexid
				final int lexId = sense.getLexicalID();

				// sensekey
				final ISenseKey sensekey = sense.getSenseKey();

				// senseentry
				final ISenseEntry senseEntry = this.dictionary.getSenseEntry(sensekey);
				// TODO should not be null but happens if the sensekey is not in the index

				// sensenum, tagcount
				final int senseNum = senseEntry.getSenseNumber();
				final int tagCount = senseEntry.getTagCount();
				tagCountTotal += tagCount;

				// add to list
				final Sense senseData = new Sense(pos, sensekey, sense, synset, lexId, posSenseIdx, globalSenseIdx, senseNum, tagCount);
				senseDatas.add(senseData);
			}

			// scan list
			int i = 0;
			for (final Sense senseData : senseDatas)
			{
				if (senseFilter != null && senseData.senseNum != senseFilter)
				{
					continue;
				}

				// sense node
				final TreeMutableNode senseNode = makeSenseNode(posNode, senseData, tagCountTotal);

				// synset node (ignore links)
				walkSynset(senseNode, senseData.synset, i, 0);

				// links
				final Map<IPointer, List<ISynsetID>> semLinks = senseData.synset.synset.getRelatedMap();
				final Map<IPointer, List<IWordID>> lexLinks = senseData.sense.getRelatedMap();
				walkLinks(senseNode, semLinks, lexLinks, i, 0, recurse, edges);

				// sense filter
				if (senseFilter != null)
				{
					String label = lemma;
					if (posFilter != null)
					{
						label += " [" + posFilter.getTag() + ',' + senseFilter + ']';
					}
					senseNode.setLabel(label);
					senseNode.setImageIndex(ImageIndex.ROOT.ordinal());
					senseNode.setParent(null);
					return senseNode;
				}
				i++;
			}

			// pos filter
			if (posFilter != null)
			{
				posNode.setLabel(lemma + " [" + posFilter.getTag() + ']');
				posNode.setImageIndex(ImageIndex.ROOT.ordinal());
				posNode.setParent(null);
				return posNode;
			}
		}
		return rootNode;
	}

	// N O D E F A C T O R Y

	@NonNull
	@SuppressWarnings("WeakerAccess")
	@Override
	protected TreeMutableNode makeSenseNode(final INode parent, @NonNull final Sense sense, final int tagCountTotal)
	{
		final TreeMutableNode node = makeSenseBaseNode(parent, sense, tagCountTotal);
		node.setEdgeLabel(BaseProvider.category(sense.synset.synset.getLexicalFile()));
		return node;
	}

	// C O N T E N T

	@NonNull
	@Override
	protected String senseContent(@NonNull final Sense sense, final String share)
	{
		return glossContent(sense.synset.gloss) + "<div class='data'>" + //
				"sense #" + sense.senseNum + ' ' + //
				"tag count: " + sense.tagCount + ' ' + //
				"share: " + share + //
				"</div>";
	}
}
