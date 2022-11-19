/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.domtree;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.w3c.dom.Document;

import treebolic.generator.Messages;

/**
 * DOM tree view
 *
 * @author Bernard Bou
 */
public class DomTreeView extends JTree
{
	private static final long serialVersionUID = 1L;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public DomTreeView(final TreeCellRenderer renderer)
	{
		super((TreeModel) null);

		setToggleClickCount(1);
		setScrollsOnExpand(true);
		setDragEnabled(false);
		setEditable(false);
		setToolTipText(Messages.getString("DomTreeView.tooltip")); //$NON-NLS-1$
		setRowHeight(17);
		setShowsRootHandles(true);
		putClientProperty("JTree.lineStyle", "Horizontal"); //$NON-NLS-1$ //$NON-NLS-2$
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// renderer
		setCellRenderer(renderer == null ? new Renderer() : renderer);
	}

	// M O D E L

	/**
	 * Set model from document
	 *
	 * @param document
	 *        document
	 */
	public void setDocument(final Document document)
	{
		if (document == null)
		{
			setModel(null);
		}
		else
		{
			// set
			final TreeModel model = new TreeModelAdapter(document, null, false);
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
			return;
		final TreePath path = new TreePath(root);
		expandAll(path);
	}

	/**
	 * Expand all subtree
	 *
	 * @param path
	 *        tree path
	 */
	public void expandAll(final TreePath path)
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
