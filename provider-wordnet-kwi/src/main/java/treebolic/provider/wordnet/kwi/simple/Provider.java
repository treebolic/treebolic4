/*
 * Copyright (c) 2019-2025. Bernard Bou
 */

package treebolic.provider.wordnet.kwi.simple;

import org.kwi.item.*;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.*;
import treebolic.provider.IProvider;
import treebolic.provider.wordnet.kwi.BaseProvider;
import treebolic.provider.wordnet.kwi.SenseData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		this.features |= FEATURE_COLLAPSE_CATEGORIES | FEATURE_SYNSET_FORGET_MEMBERS_NODE_IF_SINGLE_MEMBER | FEATURE_SEMRELATIONS_MERGE_SINGLE_RELATED_SYNSET_TO_RELATION;
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
		@NonNull final MutableNode rootNode = makeRootNode(lemma);

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
			@NonNull final MutableNode posNode = posFilter != null ? rootNode : makePosNode(rootNode, pos, lemma);

			// sense
			@NonNull final List<SenseData> senseDatas = new ArrayList<>();
			for (final SenseID senseId : idx.getSenseIDs())
			{
				++globalSenseIdx;
				++posSenseIdx;

				// sense
				final Synset.Sense sense = this.dictionary.getSense(senseId);

				// synset
				assert sense != null;
				final Synset synset = sense.getSynset();

				// lexid
				final int lexId = sense.getLexicalID();

				// sensekey
				final SenseKey sensekey = sense.getSenseKey();

				// senseentry
				final SenseEntry senseEntry = this.dictionary.getSenseEntry(sensekey);
				// should not be null but happens if the sensekey is not in the index
				assert senseEntry != null;

				// sensenum, tagcount
				final int senseNum = senseEntry.getSenseNumber();
				final int tagCount = senseEntry.getTagCount();
				tagCountTotal += tagCount;

				// add to list
				@NonNull final SenseData senseData = new SenseData(pos, sensekey, sense, synset, lexId, posSenseIdx, globalSenseIdx, senseNum, tagCount);
				senseDatas.add(senseData);
			}

			// scan list
			int i = 0;
			for (@NonNull final SenseData senseData : senseDatas)
			{
				if (senseFilter != null && senseData.sense.getNumber() != senseFilter)
				{
					continue;
				}

				// sense node
				@NonNull final TreeMutableNode senseNode = makeSenseNode(posNode, senseData, tagCountTotal);

				// synset node (ignore relations)
				walkSynset(senseNode, senseData.synset, i, 0);

				// relations
				final Map<Pointer, List<SynsetID>> semRelations = senseData.synset.synset.getRelatedSynsets();
				final Map<Pointer, List<SenseID>> lexRelations = senseData.sense.getRelatedSenses();
				walkRelations(senseNode, semRelations, lexRelations, i, 0, recurse, edges);

				// sense filter
				if (senseFilter != null)
				{
					@NonNull String label = lemma;
					if (posFilter != null)
					{
						label += " [" + posFilter.getTag() + ',' + senseFilter + ']';
					}
					senseNode.setLabel(label);
					setNodeImage(senseNode, null, ImageIndex.ROOT);
					senseNode.setParent(null);
					return senseNode;
				}
				i++;
			}

			// pos filter
			if (posFilter != null)
			{
				posNode.setLabel(lemma + " [" + posFilter.getTag() + ']');
				setNodeImage(posNode, null, ImageIndex.ROOT);
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
	protected TreeMutableNode makeSenseNode(final INode parent, @NonNull final SenseData sense, final int tagCountTotal)
	{
		@NonNull final TreeMutableNode node = makeSenseBaseNode(parent, sense, tagCountTotal);
		node.setEdgeLabel(BaseProvider.category(sense.synset.synset.getLexicalFile()));
		return node;
	}

	// C O N T E N T

	@NonNull
	@Override
	protected String senseContent(@NonNull final SenseData sense, final String share)
	{
		return glossContent(sense.synset.gloss) + "<div class='data'>" + //
				"sense #" + sense.senseNum + ' ' + //
				"tag count: " + sense.tagCount + ' ' + //
				"share: " + share + //
				"</div>";
	}
}
