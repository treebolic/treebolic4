/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.tree;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import treebolic.generator.Pair;
import treebolic.model.INode;
import treebolic.model.MenuItem;
import treebolic.model.TreeMutableNode;

/**
 * Transfer handler for drag and drop operations
 *
 * @author Bernard Bou
 */
public class TreeTransferHandler extends TransferHandler
{
	private static final long serialVersionUID = 1L;

	// F L A V O R

	/**
	 * Data flavour
	 */
	private static DataFlavor flavor;

	static
	{
		try
		{
			TreeTransferHandler.flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + DefaultMutableTreeNode.class.getName() + "\"");
		}
		catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	// T R A N S F E R A B L E . O B J E C T

	/**
	 * Transferable data
	 */
	static class TransferableNode implements Transferable
	{
		/**
		 * Node
		 */
		private final DefaultMutableTreeNode node;

		/**
		 * Constructor
		 *
		 * @param node node object to transfer
		 */
		public TransferableNode(final Object node)
		{
			this.node = (DefaultMutableTreeNode) node;
		}

		// flavor
		/*
		 * (non-Javadoc)
		 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public DataFlavor[] getTransferDataFlavors()
		{
			return new DataFlavor[]{flavor};
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor)
		{
			return flavor.equals(TreeTransferHandler.flavor);
		}

		// data
		/*
		 * (non-Javadoc)
		 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
		 */
		@Override
		public Object getTransferData(final DataFlavor flavor)
		{
			return this.node;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int getSourceActions(final JComponent c)
	{
		return TransferHandler.MOVE;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(final JComponent sourceComponent)
	{
		final JTree sourceTree = (JTree) sourceComponent;
		return new TransferableNode(sourceTree.getSelectionModel().getSelectionPath().getLastPathComponent());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean canImport(final TransferSupport support)
	{
		final Pair<TreePath, TreePath> nodes = getNodes(support);
		if (nodes != null)
		{
			return testMove(nodes.first, nodes.second);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean importData(final TransferSupport support)
	{
		final Pair<TreePath, TreePath> nodes = getNodes(support);
		if (nodes != null)
		{
			if (support.isDrop())
			{
				return move((Tree) support.getComponent(), nodes.first, nodes.second, true);
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
	 */
	@Override
	protected void exportDone(final JComponent component, final Transferable data, final int action)
	{
		// do nothing
	}

	// I M P L E M E N T

	/**
	 * Get pair of source tree and destination treepaths
	 *
	 * @param support transfer support
	 * @return source tree and destination treepaths
	 */
	private Pair<TreePath, TreePath> getNodes(final TransferSupport support)
	{
		// source flavour
		if (!support.isDataFlavorSupported(TreeTransferHandler.flavor))
		{
			return null;
		}

		// destination tree
		final Component component = support.getComponent();
		final JTree destinationTree = (JTree) component;

		// destination data
		final DropLocation dropLocation = support.getDropLocation();
		final TreePath destinationPath = destinationTree.getClosestPathForLocation(dropLocation.getDropPoint().x, dropLocation.getDropPoint().y);
		final DefaultMutableTreeNode destinationNode = (DefaultMutableTreeNode) destinationPath.getLastPathComponent();
		final Object destinationObject = destinationNode.getUserObject();

		// source data
		TreePath sourcePath;
		Object data;
		final Transferable transferable = support.getTransferable();
		try
		{
			data = transferable.getTransferData(TreeTransferHandler.flavor);
		}
		catch (final UnsupportedFlavorException | IOException exception)
		{
			return null;
		}
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) data;
		final Object sourceObject = node.getUserObject();
		sourcePath = new TreePath(node.getPath());

		// type check
		if (sourceObject instanceof TreeMutableNode && destinationObject instanceof TreeMutableNode)
		{
			return new Pair<>(sourcePath, destinationPath);
		}
		if (sourceObject instanceof MenuItemWrapper && destinationObject instanceof MenuWrapper)
		{
			return new Pair<>(sourcePath, destinationPath);
		}

		return null;
	}

	/**
	 * Whether source node can drag and drop to destination node
	 *
	 * @param source      source node
	 * @param destination destination node
	 * @return true if source node can drag and drop to destination node
	 */
	private boolean testMove(final TreePath source, final TreePath destination)
	{
		if (destination != null && source != null)
		{
			final DefaultMutableTreeNode destinationNode = (DefaultMutableTreeNode) destination.getLastPathComponent();
			final DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) source.getLastPathComponent();
			final Object destinationObject = destinationNode.getUserObject();
			final Object sourceObject = sourceNode.getUserObject();
			if (destinationObject instanceof TreeMutableNode && sourceObject instanceof TreeMutableNode)
			{
				return !source.isDescendant(destination);
			}
			else if (destinationObject instanceof MenuWrapper && sourceObject instanceof MenuItemWrapper)
			{
				return !source.isDescendant(destination);
			}
		}
		return false;
	}

	/**
	 * Move source node as child of destination node
	 *
	 * @param source      source node
	 * @param destination destination node
	 * @return true if successful
	 */
	@SuppressWarnings("SameReturnValue")
	private boolean move(final Tree tree, final TreePath source, final TreePath destination, @SuppressWarnings("SameParameterValue") boolean prepend)
	{
		// tree
		final DefaultMutableTreeNode parentTreeNode = (DefaultMutableTreeNode) destination.getLastPathComponent();
		final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) source.getLastPathComponent();

		// tree remove
		tree.removeFromParent(treeNode);

		// tree add
		if (prepend)
		{
			tree.prependToParent(parentTreeNode, treeNode);
		}
		else
		{
			tree.addToParent(parentTreeNode, treeNode);
		}

		// objects
		final Object parentObject = parentTreeNode.getUserObject();
		final Object object = treeNode.getUserObject();

		if (object instanceof TreeMutableNode && parentObject instanceof TreeMutableNode)
		{
			final TreeMutableNode parentNode = (TreeMutableNode) parentObject;
			final INode node = (INode) object;

			// object remove
			TreeMutableNode.remove(parentNode, node);

			// object add
			if (prepend)
			{
				parentNode.prependChild(node);
			}
			else
			{
				parentNode.addChild(node);
			}
		}
		else
		{
			final MenuWrapper parentNode = (MenuWrapper) parentObject;
			assert object instanceof MenuItemWrapper;
			final MenuItemWrapper node = (MenuItemWrapper) object;
			final List<MenuItem> menu = parentNode.menu;
			final MenuItem menuItem = node.menuItem;
			menu.remove(menuItem);
			if (prepend)
			{
				menu.add(0, menuItem);
			}
			else
			{
				menu.add(menu.size(), menuItem);
			}
		}
		return true;
	}
}
