/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.domtree;

import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.studio.Messages;

/**
 * DOM tree view
 *
 * @author Bernard Bou
 */
public class DomTreeView extends JTree
{
	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param renderer renderer
	 */
	public DomTreeView(@Nullable final TreeCellRenderer renderer)
	{
		super((TreeModel) null);

		setToggleClickCount(1);
		setScrollsOnExpand(true);
		setDragEnabled(false);
		setEditable(false);
		setToolTipText(Messages.getString("DomTreeView.tooltip"));
		setRowHeight(17);
		setShowsRootHandles(true);
		putClientProperty("JTree.lineStyle", "Horizontal");
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// renderer
		setCellRenderer(renderer == null ? new Renderer() : renderer);
	}

	// M O D E L

	/**
	 * Set model from document
	 *
	 * @param document document
	 */
	public void setDocument(@Nullable final Document document)
	{
		if (document == null)
		{
			setModel(null);
		}
		else
		{
			// set
			@NonNull final TreeModel model = new TreeModelAdapter(document, null, false);
			setModel(model);

			// visibility
			expandAll();
		}
	}

	// E X P A N D

	/**
	 * Expand all
	 */
	public void expandAll()
	{
		final Object root = getModel().getRoot();
		if (root == null)
		{
			return;
		}
		@NonNull final TreePath path = new TreePath(root);
		expandAll(path);
	}

	/**
	 * Expand all subtree
	 *
	 * @param path tree path
	 */
	public void expandAll(@NonNull final TreePath path)
	{
		// this
		expandPath(path);

		// recursive call for each child
		final Object node = path.getLastPathComponent();
		final int count = getModel().getChildCount(node);
		for (int i = 0; i < count; i++)
		{
			expandAll(path.pathByAddingChild(getModel().getChild(node, i)));
		}
	}
}
