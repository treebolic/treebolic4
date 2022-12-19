/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi.packed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import treebolic.annotations.NonNull;
import treebolic.model.IEdge;
import treebolic.model.INode;
import treebolic.model.Settings;
import treebolic.model.TreeMutableNode;
import treebolic.provider.wordnet.jwi.BaseProvider;
import treebolic.provider.wordnet.jwi.Synset;

/**
 * Provider for WordNet (packed)
 *
 * @author Bernard Bou
 */
public class Provider extends treebolic.provider.wordnet.jwi.condensed.Provider
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
		this.features |= FEATURE_LINKEDSYNSET1_FORGET_LINK_NODE | FEATURE_MULTILINE_RAISE_MEMBERS_TO_SYNSET | FEATURE_MULTILINE_RAISE_MEMBERS_TO_LINKED_SYNSET;
	}

	// S E T T I N G S

	@NonNull
	@Override
	protected Settings makeSettings(int childrenCount)
	{
		final Settings settings = super.makeSettings(childrenCount, 2);
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
	protected void walkSynset(@NonNull final TreeMutableNode parentNode, @NonNull final Synset synset, final int index, final int level)
	{
		// System.out.println(level + " " + new String(new char[level]).replace('\0', '\t') + membersToString(synset));
		if (level == 0)
		{
			parentNode.setEdgeLabel(parentNode.getLabel() + " - " + parentNode.getEdgeLabel());
		}

		final String lemmas = membersAsLines(synset);
		parentNode.setLabel(/* Integer.toString(index) + '\n' + */ lemmas);
		parentNode.setContent(glossContent(synset.gloss));
		parentNode.setLink(BaseProvider.URLSCHEME + BaseProvider.URLSCHEME_AT + synset.synset.getID().toString());

		decorateAsWord(parentNode, level);
		setNodeImage(parentNode, null, null);
		// do not assume anything about tree edge
	}

	// FEATURE_MULTILINE_RAISE_MEMBERS_TO_LINKED_SYNSET
	@Override
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

			final Synset linkedSynset = new Synset(linkedSynset0);
			// System.out.println(level + " " + new String(new char[level]).replace('\0', '\t') + membersToString(linkedSynset));

			// synset node
			final String tag = Integer.toString(i + 1);

			final TreeMutableNode linkedSynsetNode = makeSynsetNode(null, linkedSynset);
			linkedSynsetNode.setLabel(/* tag + '\n' + */ membersAsLines(linkedSynset));

			decorateAsWord(linkedSynsetNode, level);
			setNodeImage(linkedSynsetNode, null, null);
			linkedSynsetNode.setEdgeLabel(null);
			linkedSynsetNode.setEdgeColor(this.linkBackgroundColor);
			setTreeEdgeImage(linkedSynsetNode, null, ImageIndex.values()[link.imageIndex]);

			linkedSynsetNode.setTarget(tag);
			childNodes.add(linkedSynsetNode);

			// recurse
			if (level < this.maxRecurse)
			{
				// iterate linked synsets
				final List<ISynsetID> childLinkedSynsetIds = linkedSynset.synset.getRelatedSynsets(link.pointer);
				if (!childLinkedSynsetIds.isEmpty())
				{
					walkTypedLink(linkedSynsetNode, childLinkedSynsetIds, link, level + 1, edges);
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
}
