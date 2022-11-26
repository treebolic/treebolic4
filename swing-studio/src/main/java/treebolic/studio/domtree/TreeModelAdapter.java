/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio.domtree;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Tree model adapter
 */
public class TreeModelAdapter implements TreeModel
{
	// D A T A

	/**
	 * DOM document
	 */
	private final Document document;

	/**
	 * From node
	 */
	private final String fromNode;

	/**
	 * Whether to consider DOM elements
	 */
	private final boolean elementsOnly;

	/**
	 * Listener support
	 */
	private final Vector<TreeModelListener> listenerList = new Vector<>();

	// C O N S T R U C T

	/**
	 * Constructor
	 *
	 * @param document         document
	 * @param fromNode         from node
	 * @param elementsOnlyFlag whether to consider elements only
	 */
	public TreeModelAdapter(final Document document, final String fromNode, final boolean elementsOnlyFlag)
	{
		this.document = document;
		this.fromNode = fromNode;
		this.elementsOnly = elementsOnlyFlag;
	}

	// T R E E M O D E L

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	@Override
	public Object getRoot()
	{
		if (this.fromNode == null)
		{
			return this.document.getDocumentElement();
		}
		else
		{
			final NodeList elements = this.document.getElementsByTagName(this.fromNode);
			return elements == null || elements.getLength() < 1 ? null : elements.item(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	@Override
	public boolean isLeaf(final Object node)
	{
		return childCountOf((Node) node) == 0;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	@Override
	public int getChildCount(final Object node)
	{
		return childCountOf((Node) node);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	@Override
	public Object getChild(final Object node, final int index)
	{
		return nthChildOf((Node) node, index);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int getIndexOfChild(final Object node, final Object child)
	{
		return indexOf((Node) node, (Node) child);
	}

	// M O D I F I C A T I O N

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	@Override
	public void valueForPathChanged(final TreePath path, final Object newValue)
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void addTreeModelListener(final TreeModelListener listener)
	{
		if (listener != null && !this.listenerList.contains(listener))
		{
			this.listenerList.addElement(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void removeTreeModelListener(final TreeModelListener listener)
	{
		if (listener != null)
		{
			this.listenerList.removeElement(listener);
		}
	}

	// T R E E M O D E L . H E L P E R S

	/**
	 * Get nth child node
	 *
	 * @param node        node
	 * @param targetIndex index
	 * @return nth child
	 */
	private Node nthChildOf(final Node node, final int targetIndex)
	{
		if (!this.elementsOnly)
		{
			return node.getChildNodes().item(targetIndex);
		}

		// nth element node
		int index = 0;
		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			final Node child = node.getChildNodes().item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && index++ == targetIndex)
			{
				return child;
			}
		}
		return null;
	}

	/**
	 * Get child count
	 *
	 * @param node node
	 * @return number of children
	 */
	private int childCountOf(final Node node)
	{
		if (!this.elementsOnly)
		{
			return node.getChildNodes().getLength();
		}

		// number of element nodes
		int count = 0;
		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			final Node child = node.getChildNodes().item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE)
			{
				++count;
			}
		}
		return count;
	}

	/**
	 * Get index of child node
	 *
	 * @param node        node
	 * @param targetChild child
	 * @return index or -1 if not found
	 */
	private int indexOf(final Node node, final Node targetChild)
	{
		final int count = childCountOf(node);
		for (int i = 0; i < count; i++)
		{
			final Node child = nthChildOf(node, i);
			if (child == targetChild)
			{
				return i;
			}
		}
		return -1;
	}
}
