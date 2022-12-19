/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi.full;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.Model;
import treebolic.model.MutableEdge;
import treebolic.model.MutableNode;
import treebolic.provider.wordnet.jwi.ImageFactory;

/**
 * Provider for WordNet
 *
 * @author Bernard Bou
 */
public class Provider2 extends treebolic.provider.wordnet.jwi.full.Provider
{
	/**
	 * Constructor
	 *
	 * @throws IOException io exception
	 */
	public Provider2() throws IOException
	{
		super();
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
			node.setImage(ImageFactory.images[index]);
		}
	}

	@Override
	public void setTreeEdgeImage(@NonNull final MutableNode node, final int index)
	{
		if (index != -1)
		{
			node.setEdgeImage(ImageFactory.images[index]);
		}
	}

	@Override
	public void setEdgeImage(@NonNull final MutableEdge edge, final int index)
	{
		if (index != -1)
		{
			edge.setImage(ImageFactory.images[index]);
		}
	}
}
