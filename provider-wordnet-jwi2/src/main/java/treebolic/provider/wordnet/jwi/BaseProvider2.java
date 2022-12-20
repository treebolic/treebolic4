/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.Model;
import treebolic.model.MutableEdge;
import treebolic.model.MutableNode;

/**
 * Base provider for WordNet
 *
 * @author Bernard Bou
 */
@SuppressWarnings({"SameParameterValue"})
public abstract class BaseProvider2 extends BaseProvider
{

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @throws IOException io exception
	 */
	@SuppressWarnings("RedundantThrows")
	protected BaseProvider2() throws IOException
	{
		super();
		this.membersLoadBalancer.setGroupNode(null, this.wordsBackgroundColor, this.wordsForegroundColor, this.wordsEdgeColor, LOADBALANCING_EDGE_STYLE, ImageIndex.MEMBERS.ordinal(), null, null);
		this.semLinksLoadBalancer.setGroupNode(null, this.linksBackgroundColor, this.linksForegroundColor, this.linksEdgeColor, LOADBALANCING_EDGE_STYLE, ImageIndex.SYNSET.ordinal(), null, null);
	}

	// I N T E R F A C E

	@Nullable
	@Override
	public Model makeModel(final String source, final URL base, @NonNull final Properties parameters)
	{
		Model model = super.makeModel(source, base, parameters);
		if (model == null)
		{
			return null;
		}

		// result
		return new Model(model.tree, model.settings, ImageFactory.images);
	}

	// I M A G E

	@Override
	public void setNodeImage(@NonNull final MutableNode node, final int index)
	{
		if (index != -1)
		{
			node.setImageIndex(index);
		}
	}

	@Override
	public void setTreeEdgeImage(@NonNull final MutableNode node, final int index)
	{
		if (index != -1)
		{
			node.setEdgeImageIndex(index);
		}
	}

	@Override
	public void setEdgeImage(@NonNull final MutableEdge edge, final int index)
	{
		if (index != -1)
		{
			edge.setImageIndex(index);
		}
	}
}
