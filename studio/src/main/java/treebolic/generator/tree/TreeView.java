/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.tree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import treebolic.generator.Controller;
import treebolic.generator.model.ModelUtils;
import treebolic.model.IEdge;
import treebolic.model.MenuItem;
import treebolic.model.TreeMutableEdge;
import treebolic.model.TreeMutableNode;

/**
 * Tree view
 *
 * @author Bernard Bou
 */
public class TreeView extends JScrollPane
{
	private static final long serialVersionUID = 1L;

	// D A T A

	/**
	 * Tree component
	 */
	private final Tree tree;

	/**
	 * Dirty flag
	 */
	public boolean dirty;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public TreeView()
	{
		this.tree = new Tree();
		setViewportView(this.tree);
	}

	// C O N N E C T

	/**
	 * Connect to controller
	 *
	 * @param controller controller
	 */
	public void connect(final Controller controller)
	{
		this.tree.connect(controller);
	}

	// M O D E L

	/**
	 * Set tree model
	 *
	 * @param treeModel model
	 */
	public void set(final TreeModel treeModel)
	{
		// add listener to tree model
		if (treeModel != null)
		{
			treeModel.addTreeModelListener(new TreeModelListener()
			{
				@Override
				public void treeNodesChanged(final TreeModelEvent event)
				{
					TreeView.this.dirty = true;
				}

				@Override
				public void treeNodesInserted(final TreeModelEvent e)
				{
					TreeView.this.dirty = true;
				}

				@Override
				public void treeNodesRemoved(final TreeModelEvent e)
				{
					TreeView.this.dirty = true;
				}

				@Override
				public void treeStructureChanged(final TreeModelEvent e)
				{
					TreeView.this.dirty = true;
				}
			});
		}
		this.tree.setModel(treeModel);

		// visibility
		if (treeModel != null)
		{
			this.tree.expandAll();
		}
	}

	/**
	 * Add object
	 */
	public void editAdd()
	{
		// selected tree node
		final TreePath parentPath = this.tree.getSelectionPath();
		if (parentPath == null)
		{
			return;
		}

		// parent tree node
		final DefaultMutableTreeNode parentTreeNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
		if (parentTreeNode == null)
		{
			return;
		}

		// parent's number of children
		final int pos = parentTreeNode.getChildCount();

		// object
		final Object object = parentTreeNode.getUserObject();
		if (object instanceof treebolic.model.MutableNode)
		{
			// parent
			final TreeMutableNode parentNode = (TreeMutableNode) object;

			// id
			final String id = ModelUtils.makeNodeId();

			// label
			String label = parentNode.getLabel();
			if (label == null)
			{
				label = ""; 
			}
			else
			{
				label += "-"; 
			}
			label += pos;

			// new node (handle parent with addToParent)
			final TreeMutableNode node = new TreeMutableNode(null, id);
			node.setLabel(label);

			// object add to parent
			parentNode.addChild(node);

			// new tree node
			final DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
			treeNode.setUserObject(node);

			// tree add to parent
			this.tree.addToParent(parentTreeNode, treeNode);
		}
		else if (object instanceof EdgesWrapper)
		{
			final EdgesWrapper wrapper = (EdgesWrapper) object;
			List<IEdge> edgeList = wrapper.edgeList;
			if (edgeList == null)
			{
				edgeList = new ArrayList<>();
				wrapper.edgeList = edgeList;
				wrapper.model.tree.setEdges(edgeList);
			}

			// new edge
			final TreeMutableEdge edge = new TreeMutableEdge(null, null);

			// object add
			edgeList.add(edge);

			// new tree node
			final DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
			treeNode.setUserObject(edge);

			// tree add to parent
			this.tree.addToParent(parentTreeNode, treeNode);
		}
		else if (object instanceof MenuWrapper)
		{
			final MenuWrapper wrapper = (MenuWrapper) object;
			List<MenuItem> menu = wrapper.menu;
			if (menu == null)
			{
				menu = new ArrayList<>();
				wrapper.menu = menu;
				wrapper.settings.menu = menu;
			}

			// new menu item
			final MenuItem menuItem = new MenuItem();

			// object add
			menu.add(menuItem);

			// new tree node
			final DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
			treeNode.setUserObject(new MenuItemWrapper(menuItem, wrapper.settings));

			// tree add to parent
			this.tree.addToParent(parentTreeNode, treeNode);
		}
	}

	/**
	 * Remove object
	 */
	public void editRemove()
	{
		// selected tree node
		final TreePath path = this.tree.getSelectionPath();
		if (path == null)
		{
			return;
		}
		final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		if (treeNode == null)
		{
			return;
		}

		// parent tree node
		final DefaultMutableTreeNode parentTreeNode = (DefaultMutableTreeNode) treeNode.getParent();
		if (parentTreeNode == null)
		{
			return;
		}

		// object remove
		final Object object = treeNode.getUserObject();
		final Object parentObject = parentTreeNode.getUserObject();
		if (parentObject instanceof TreeMutableNode)
		{
			final TreeMutableNode node = (TreeMutableNode) object;
			TreeMutableNode.removeFromParent(node);
		}
		else if (parentObject instanceof EdgesWrapper)
		{
			final EdgesWrapper wrapper = (EdgesWrapper) parentObject;
			wrapper.edgeList.remove(object);
		}
		else if (parentObject instanceof MenuWrapper)
		{
			final MenuWrapper menuWrapper = (MenuWrapper) parentObject;
			final MenuItemWrapper menuItemWrapper = (MenuItemWrapper) object;
			menuWrapper.menu.remove(menuItemWrapper.menuItem);
		}
		else
		{
			return;
		}

		// tree remove
		this.tree.removeFromParent(treeNode);
	}

	/**
	 * Search for tree node whose user object has given class name
	 *
	 * @param className class name
	 * @return tree node
	 */
	public DefaultMutableTreeNode search(final String className)
	{
		final TreeModel model = this.tree.getModel();
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
		return search(node, className);
	}

	// S E A R C H

	/**
	 * Search for tree node whose user object has given class name
	 *
	 * @param node      start node
	 * @param className class name
	 * @return tree node
	 */
	@SuppressWarnings("unchecked")
	private DefaultMutableTreeNode search(final DefaultMutableTreeNode node, final String className)
	{
		if (node.getUserObject().getClass().getName().endsWith(className))
		{
			return node;
		}

		for (final Enumeration<TreeNode> children = node.children(); children.hasMoreElements(); )
		{
			final DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
			final DefaultMutableTreeNode result = search(child, className);
			if (result != null)
			{
				return result;
			}
		}
		return null;
	}

	// L I S T E N E R

	public void onEditingStopped()
	{
		// System.err.println("TREEVIEW: editing stopped");
		final DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
		final TreePath path = this.tree.getSelectionPath();
		if (path != null)
		{
			final TreeNode node = (TreeNode) path.getLastPathComponent();
			model.reload(node);
		}
		else
		{
			model.reload();
		}
	}

	// S E L E C T

	/**
	 * Select tree node
	 *
	 * @param node node
	 */
	public void select(final DefaultMutableTreeNode node)
	{
		// System.err.println("TREEVIEW: select");

		final TreePath path = new TreePath(node.getPath());
		this.tree.setSelectionPath(path);
	}

	// E X P A N D / C O L L A P S E

	/**
	 * Expand
	 */
	public void expand()
	{
		this.tree.expandAll();
	}

	/**
	 * Collapse
	 */
	public void collapse()
	{
		this.tree.collapseAll();
	}
}
