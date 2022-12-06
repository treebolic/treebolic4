/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio.tree;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.*;

import treebolic.annotations.NonNull;
import treebolic.studio.Controller;
import treebolic.model.INode;
import treebolic.model.TreeMutableNode;

/**
 * Tree
 *
 * @author Bernard Bou
 */
public class Tree extends JTree implements TreeSelectionListener // , DragGestureListener, DragSourceListener, DropTargetListener
{
	static private final long serialVersionUID = 1L;

	/**
	 * Whether newly-created node receives focus
	 */
	static public boolean focusParent = true;

	/**
	 * Whether data have been changed
	 */
	public boolean isDirty;

	/**
	 * Controller (reference)
	 */
	private Controller controller;

	/**
	 * Basic UI to cover whole row
	 */
	public static class MyBasicTreeUI extends BasicTreeUI
	{
		@Override
		protected void paintVerticalLine(final Graphics g, final JComponent c, final int x, final int top, final int bottom)
		{
			//
		}

		@Override
		protected void paintHorizontalLine(final Graphics g, final JComponent c, final int y, final int left, final int right)
		{
			//
		}
	}

	// C O N S T R U C T

	/**
	 * Constructor
	 */
	public Tree()
	{
		super((TreeModel) null);
		this.isDirty = false;

		setEditable(false);

		// rendering
		setBackground(Color.WHITE);
		setShowsRootHandles(true);
		setToggleClickCount(2);
		setScrollsOnExpand(true);
		setFont(new java.awt.Font(Font.DIALOG, Font.PLAIN, 10));
		putClientProperty("JTree.lineStyle", "Horizontal");
		setToolTipText("Tree");
		setLargeModel(true); // A large model can be used when the cell height is the same for all nodes. The UI will then cache very little information and instead continually message the model.
		setRowHeight(18);

		// renderer
		setUI(new MyBasicTreeUI());
		setCellRenderer(new Renderer());

		// selection handler
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		addTreeSelectionListener(this);

		// drag and drop
		setDragEnabled(true);
		setTransferHandler(new TreeTransferHandler());

		// key listener
		addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				//
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				//
			}

			@Override
			public void keyPressed(@NonNull KeyEvent keyEvent)
			{
				if ((keyEvent.getKeyCode() == KeyEvent.VK_UP) && ((keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0))
				{
					Tree.this.move(-1);
					keyEvent.consume();
				}
				else if ((keyEvent.getKeyCode() == KeyEvent.VK_DOWN) && ((keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0))
				{
					Tree.this.move(1);
					keyEvent.consume();
				}
			}
		});
	}

	/**
	 * Connect to controller
	 *
	 * @param controller controller
	 */
	public void connect(final Controller controller)
	{
		this.controller = controller;
	}

	// E X P A N D

	/**
	 * Expand tree
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
	 * Collapse tree
	 */
	public void collapseAll()
	{
		final TreeModel model = getModel();
		final TreeNode root = (TreeNode) model.getRoot();
		if (root == null)
		{
			return;
		}
		@NonNull final DefaultMutableTreeNode[] nodes = { //
				(DefaultMutableTreeNode) root.getChildAt(0).getChildAt(0).getChildAt(0), // root node
				(DefaultMutableTreeNode) root.getChildAt(0).getChildAt(1), // edges
				(DefaultMutableTreeNode) root.getChildAt(1) // tools
		};
		for (final DefaultMutableTreeNode node : nodes)
		{
			@NonNull final TreePath path = new TreePath(node.getPath());
			collapseAll(path);
		}
	}

	/**
	 * Expand tree
	 *
	 * @param path tree path to expand
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

	/**
	 * Collapse tree
	 *
	 * @param path tree path to collapse
	 */
	public void collapseAll(final TreePath path)
	{
		// recursive call for each child
		final Object node = path.getLastPathComponent();
		final int count = getModel().getChildCount(node);
		for (int i = 0; i < count; i++)
		{
			collapseAll(path.pathByAddingChild(getModel().getChild(node, i)));
		}

		// this
		collapsePath(path);
	}

	// M O D I F I C A T I O N

	/**
	 * Add node to parent
	 *
	 * @param parentNode node
	 * @param node       node
	 */
	public void addToParent(final DefaultMutableTreeNode parentNode, final DefaultMutableTreeNode node)
	{
		// model
		final DefaultTreeModel model = (DefaultTreeModel) getModel();

		// tree insert (model insert for events to be fired)
		model.insertNodeInto(node, parentNode, model.getChildCount(parentNode));

		// select
		final TreePath path = new TreePath(model.getPathToRoot(node));
		makeVisible(path);
		scrollPathToVisible(path);
		if (!Tree.focusParent)
		{
			addSelectionPath(path);
		}
		this.isDirty = true;
	}

	/**
	 * Add node to parent
	 *
	 * @param parentNode node
	 * @param node       node
	 */
	public void prependToParent(final DefaultMutableTreeNode parentNode, final DefaultMutableTreeNode node)
	{
		// model
		final DefaultTreeModel model = (DefaultTreeModel) getModel();

		// tree insert (model insert for events to be fired)
		model.insertNodeInto(node, parentNode, 0);

		// select
		@NonNull final TreePath path = new TreePath(model.getPathToRoot(node));
		makeVisible(path);
		scrollPathToVisible(path);
		if (!Tree.focusParent)
		{
			addSelectionPath(path);
		}
		this.isDirty = true;
	}

	/**
	 * Remove from parent
	 *
	 * @param node node
	 */
	public void removeFromParent(@NonNull final DefaultMutableTreeNode node)
	{
		// model
		final DefaultTreeModel model = (DefaultTreeModel) getModel();

		// point to parent
		final javax.swing.tree.TreeNode parent = node.getParent();
		int index = -1;
		if (parent != null)
		{
			index = parent.getIndex(node);
		}

		// tree remove (model remove for events to be fired)
		model.removeNodeFromParent(node);

		// select
		if (parent != null)
		{
			index = Math.min(index, parent.getChildCount() - 1);
			final javax.swing.tree.TreeNode selection = !Tree.focusParent && index > -1 ? parent.getChildAt(index) : parent;
			addSelectionPath(new TreePath(model.getPathToRoot(selection)));
		}
		this.isDirty = true;
	}

	/**
	 * Move node up/down
	 *
	 * @param step step
	 */
	public void move(int step)
	{
		// tree
		final TreePath selected = getSelectionPath();
		assert selected != null;
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selected.getLastPathComponent();
		DefaultMutableTreeNode parentTreeNode = (DefaultMutableTreeNode) treeNode.getParent();
		// System.out.println((step>0 ? "down" : "up") + " move " + treeNode);

		// objects
		final Object parentObject = parentTreeNode.getUserObject();
		final Object object = treeNode.getUserObject();
		if (!(object instanceof TreeMutableNode) || !(parentObject instanceof TreeMutableNode))
		{
			return;
		}

		// model
		final DefaultTreeModel model = (DefaultTreeModel) getModel();
		int n = parentTreeNode.getChildCount();
		int i = parentTreeNode.getIndex(treeNode);
		int j = i + step;
		if (j > n - 1 || j < 0)
		{
			return;
		}

		// model remove + add
		model.removeNodeFromParent(treeNode);
		model.insertNodeInto(treeNode, parentTreeNode, j);

		// objects
		@NonNull final TreeMutableNode parentNode = (TreeMutableNode) parentObject;
		@NonNull final INode node = (INode) object;

		// object remove + add
		TreeMutableNode.remove(parentNode, node);
		parentNode.insertChild(node, j);

		// select
		setSelectionPath(selected);

		this.isDirty = true;
	}

	// T R E E . S E L E C T I O N . L I S T E N E R

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(final TreeSelectionEvent event)
	{
		// System.err.println("TREE: selection " + toString(event)); 

		// return if null selection
		final TreePath path = event.getNewLeadSelectionPath();
		if (path == null)
		{
			return;
		}

		// get new selected element
		final Object object = path.getLastPathComponent();
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
		fireSelect(node);
	}

	@NonNull
	@SuppressWarnings("unused")
	static private String toString(final TreeSelectionEvent event)
	{

		@NonNull final StringBuilder buffer = new StringBuilder();
		buffer.append("source:");
		buffer.append(event.getSource());
		// TreePath path = event.getPath();
		// buffer.append(path.getLastPathComponent());
		buffer.append(" changed:");
		TreePath[] paths = event.getPaths();
		for (TreePath path2 : paths)
		{
			buffer.append(path2.getLastPathComponent());
			buffer.append(' ');
		}
		TreePath oldPath = event.getOldLeadSelectionPath();
		buffer.append("old-new:");
		if (oldPath != null)
		{
			buffer.append(oldPath.getLastPathComponent());
		}
		buffer.append('>');
		TreePath newPath = event.getNewLeadSelectionPath();
		if (newPath != null)
		{
			buffer.append(newPath.getLastPathComponent());
		}

		return buffer.toString();
	}

	// S E L E C T I O N . F I R I N G

	/**
	 * Select node
	 *
	 * @param node node
	 */
	public void fireSelect(@NonNull final DefaultMutableTreeNode node)
	{
		// System.err.println("TREE: fire select " + node.getUserObject());
		this.controller.onSelected(node.getUserObject());
	}
}
