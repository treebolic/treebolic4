/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.xml.dom;

import org.w3c.dom.Document;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.IEdge;
import treebolic.model.Mounter;
import treebolic.model.Tree;
import treebolic.provider.IProvider;

/**
 * Document adapter to model/graph
 *
 * @author Bernard Bou
 */
@SuppressWarnings("WeakerAccess")
public class DocumentAdapter extends BaseDocumentAdapter
{
	/**
	 * Provider (used to generate mounted trees)
	 */
	private final IProvider provider;

	/**
	 * Base
	 */
	private final URL base;

	/**
	 * Parameters
	 */
	private final Properties parameters;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param provider   provider (used in recursion)
	 * @param base       base
	 * @param parameters parameters
	 */
	public DocumentAdapter(final IProvider provider, final URL base, final Properties parameters)
	{
		this.provider = provider;
		this.base = base;
		this.parameters = parameters;
	}

	/**
	 * Constructor
	 */
	public DocumentAdapter()
	{
		this(null, null, null);
	}

	// P A R S E

	/**
	 * Make graph
	 *
	 * @param document document
	 * @return graph
	 */
	@Override
	@Nullable
	protected Tree toTree(@NonNull final Document document)
	{
		@Nullable final Tree tree = super.toTree(document);
		if (tree == null)
		{
			return null;
		}

		// edges
		@Nullable final List<IEdge> edges = tree.getEdges();

		// run protracted mount tasks (had to be protracted until edges become available)
		if (this.mountTasks != null)
		{
			for (@NonNull final MountTask task : this.mountTasks)
			{
				graft(task, provider, base, parameters, edges);
			}
			this.mountTasks.clear();
			this.mountTasks = null;
		}

		return tree;
	}

	/**
	 * Graft mounted tree
	 *
	 * @param task       mount task to perform
	 * @param provider   provider
	 * @param base       document base
	 * @param parameters parameters
	 * @param edges      edges in grafting tree
	 */
	public void graft(@NonNull final MountTask task, @Nullable final IProvider provider, @Nullable final URL base, @Nullable final Properties parameters, @Nullable final List<IEdge> edges)
	{
		if (provider == null)
		{
			System.err.println("Mount not performed: " + task.mountPoint + " @ " + task.mountingNode);
			return;
		}
		@Nullable final Tree tree = provider.makeTree(task.mountPoint.url, base, parameters, true);
		if (tree != null)
		{
			Mounter.graft(task.mountingNode, tree.getRoot(), edges, tree.getEdges());
		}
	}
}
