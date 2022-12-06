/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio.tree;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.*;

/**
 * Tree adapter (converts treebolic model to DefaultTreeModel)
 *
 * @author Bernard Bou
 */
public class TreeAdapter extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 *
	 * @param model treebolic model
	 */
	public TreeAdapter(final Model model)
	{
		super(TreeAdapter.makeTreeModel(model));
	}

	/**
	 * Make tree model out of treebolic model
	 *
	 * @param model treebolic model
	 * @return tree model
	 */
	public static TreeNode makeTreeModel(@Nullable final Model model)
	{
		if (model == null)
		{
			return null;
		}

		// root
		final Node node = (Node) model.tree.getRoot();
		@NonNull final MutableTreeNode rootTreeNode = TreeAdapter.makeNode(node, null);

		// nodes
		@NonNull final DefaultMutableTreeNode nodesTreeNode = new DefaultMutableTreeNode();
		nodesTreeNode.setUserObject(new NodesWrapper(model.settings));
		nodesTreeNode.add(rootTreeNode);
		nodesTreeNode.add(rootTreeNode);

		// edges
		final List<IEdge> edges = model.tree.getEdges();
		@NonNull final MutableTreeNode edgesTreeNode = TreeAdapter.makeEdges(edges, model, model.settings);

		// tree
		@NonNull final DefaultMutableTreeNode treeTreeNode = new DefaultMutableTreeNode();
		treeTreeNode.setUserObject(new TreeWrapper(model.settings));
		treeTreeNode.add(nodesTreeNode);
		treeTreeNode.add(edgesTreeNode);

		// tools
		@NonNull final MutableTreeNode toolsTreeNode = TreeAdapter.makeTools(model.settings.menu, model.settings);

		// top
		@NonNull final DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
		treeNode.setUserObject(new TopWrapper(model.settings));
		treeNode.add(treeTreeNode);
		treeNode.add(toolsTreeNode);
		return treeNode;
	}

	/**
	 * Make tree node for treebolic node
	 *
	 * @param node                  treebolic node
	 * @param ignoredParentTreeNode parent tree node
	 * @return tree node
	 */
	@NonNull
	private static MutableTreeNode makeNode(@NonNull final Node node, final MutableTreeNode ignoredParentTreeNode)
	{
		@NonNull final DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
		treeNode.setUserObject(node);

		// recurse
		for (final INode iChild : node.getChildren())
		{
			final Node child = (Node) iChild;
			@NonNull final MutableTreeNode childTreeNode = TreeAdapter.makeNode(child, treeNode);

			// attach child to parent
			treeNode.add(childTreeNode);
		}
		return treeNode;
	}

	/**
	 * Make tree node for edges
	 *
	 * @param edgeList edge list
	 * @param model    model
	 * @param settings settings
	 * @return tree node
	 */
	@NonNull
	private static MutableTreeNode makeEdges(@Nullable final List<IEdge> edgeList, final Model model, final Settings settings)
	{
		// container
		@NonNull final DefaultMutableTreeNode edgesTreeNode = new DefaultMutableTreeNode();
		edgesTreeNode.setUserObject(new EdgesWrapper(edgeList, model, settings));

		// iterate
		if (edgeList != null)
		{
			for (final IEdge edge : edgeList)
			{
				@NonNull final DefaultMutableTreeNode edgeTreeNode = new DefaultMutableTreeNode();
				edgeTreeNode.setUserObject(edge);

				// attach to container
				edgesTreeNode.add(edgeTreeNode);
			}
		}
		return edgesTreeNode;
	}

	/**
	 * Make tree node for menu
	 *
	 * @param ignoredMenu menu
	 * @param settings    settings
	 * @return tree node
	 */
	@NonNull
	private static MutableTreeNode makeTools(final List<MenuItem> ignoredMenu, @NonNull final Settings settings)
	{
		@NonNull final DefaultMutableTreeNode toolsTreeNode = new DefaultMutableTreeNode();
		toolsTreeNode.setUserObject(new ToolsWrapper(settings));

		@NonNull final DefaultMutableTreeNode menuTreeNode = new DefaultMutableTreeNode();
		menuTreeNode.setUserObject(new MenuWrapper(settings.menu, settings));
		toolsTreeNode.add(menuTreeNode);

		if (settings.menu != null)
		{
			for (final MenuItem menuItem : settings.menu)
			{
				@NonNull final DefaultMutableTreeNode menuItemTreeNode = new DefaultMutableTreeNode();
				menuItemTreeNode.setUserObject(new MenuItemWrapper(menuItem, settings));
				menuTreeNode.add(menuItemTreeNode);
			}
		}
		return toolsTreeNode;
	}
}
