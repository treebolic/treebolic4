/*
 * Copyright (c) 2019-2025. Bernard Bou
 */

package treebolic.provider.wordnet.kwi.packed;

import org.kwi.item.SynsetID;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.IEdge;
import treebolic.model.INode;
import treebolic.model.Settings;
import treebolic.model.TreeMutableNode;
import treebolic.provider.wordnet.kwi.BaseProvider;
import treebolic.provider.wordnet.kwi.SynsetData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provider for WordNet (packed)
 *
 * @author Bernard Bou
 */
public class Provider extends treebolic.provider.wordnet.kwi.condensed.Provider
{
	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @throws IOException io exception
	 */
	public Provider() throws IOException
	{
		super();
		this.features |= FEATURE_RELATEDSYNSET1_FORGET_RELATION_NODE | FEATURE_MULTILINE_RAISE_MEMBERS_TO_SYNSET | FEATURE_MULTILINE_RAISE_MEMBERS_TO_RELATED_SYNSET;
	}

	// S E T T I N G S

	@NonNull
	@Override
	protected Settings makeSettings(int childrenCount)
	{
		@NonNull final Settings settings = super.makeSettings(childrenCount, 2);
		// settings.orientation = "radial";
		settings.expansion = 1.2F;
		settings.sweep = 1.2F;
		settings.fontSizeFactor = (this.fontSizeFactor == null || this.fontSizeFactor == -1F) ? .8F : this.fontSizeFactor * .8F;
		settings.labelExtraLineFactor = 1.F;
		settings.labelMaxLines = this.labelMaxLines;
		settings.fontDownscaler = FONTSCALER_FAST;
		return settings;
	}

	// W A L K

	// FEATURE_MULTILINE_RAISE_MEMBERS_TO_SYNSET
	@Override
	protected void walkSynset(@NonNull final TreeMutableNode parentNode, @NonNull final SynsetData synset, final int index, final int level)
	{
		// System.out.println(level + " " + new String(new char[level]).replace('\0', '\t') + membersToString(synset));
		if (level == 0)
		{
			parentNode.setEdgeLabel(parentNode.getLabel() + " - " + parentNode.getEdgeLabel());
		}

		@NonNull final String lemmas = membersAsLines(synset);
		parentNode.setLabel(/* Integer.toString(index) + '\n' + */ lemmas);
		parentNode.setContent(glossContent(synset.gloss));
		parentNode.setLink(BaseProvider.URLSCHEME + BaseProvider.URLSCHEME_AT + synset.synset.getID());

		decorateAsWord(parentNode, level);
		setNodeImage(parentNode, null, null);
		// do not assume anything about tree edge
	}

	// FEATURE_MULTILINE_RAISE_MEMBERS_TO_RELATED_SYNSET
	@Override
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
			final org.kwi.item.Synset relatedSynset0 = this.dictionary.getSynset(relatedSynsetId);
			if (relatedSynset0 == null)
			{
				continue;
			}

			@NonNull final SynsetData relatedSynset = new SynsetData(relatedSynset0);
			// System.out.println(level + " " + new String(new char[level]).replace('\0', '\t') + membersToString(relatedSynset));

			// synset node
			@NonNull final String tag = Integer.toString(i + 1);

			@NonNull final TreeMutableNode relatedSynsetNode = makeSynsetNode(null, relatedSynset);
			relatedSynsetNode.setLabel(/* tag + '\n' + */ membersAsLines(relatedSynset));

			decorateAsWord(relatedSynsetNode, level);
			setNodeImage(relatedSynsetNode, null, null);
			relatedSynsetNode.setEdgeLabel(null);
			relatedSynsetNode.setEdgeColor(this.relationBackgroundColor);
			setTreeEdgeImage(relatedSynsetNode, null, ImageIndex.values()[relation.imageIndex]);

			relatedSynsetNode.setTarget(tag);
			childNodes.add(relatedSynsetNode);

			// recurse
			if (level < this.maxRecurse)
			{
				// iterate related synsets
				final List<SynsetID> childRelatedSynsetIds = relatedSynset.synset.getRelatedSynsetsFor(relation.pointer);
				if (!childRelatedSynsetIds.isEmpty())
				{
					walkTypedRelation(relatedSynsetNode, childRelatedSynsetIds, relation, level + 1, edges);
				}
			}

			// limit
			if (++i >= this.maxRelations && i < n)
			{
				etcNode = makeEtcRelationNode(null, relation, n, it);
				break;
			}
		}

		// semrelations nodes
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
}
