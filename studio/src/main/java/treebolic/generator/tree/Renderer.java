/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import treebolic.generator.Messages;
import treebolic.model.Edge;
import treebolic.model.MenuItem;
import treebolic.model.MutableEdge;
import treebolic.model.MutableNode;
import treebolic.model.Node;

/**
 * XML tree renderer
 *
 * @author Bernard Bou
 */
public class Renderer extends DefaultTreeCellRenderer
{
	private static final long serialVersionUID = 1L;

	// icons to use

	/**
	 * Treebolic icon
	 */
	static protected final ImageIcon treebolicIcon;

	/**
	 * Tree icon
	 */
	static protected final ImageIcon treeIcon;

	/**
	 * Nodes icon
	 */
	static protected final ImageIcon nodesIcon;

	/**
	 * Root icon
	 */
	static protected final ImageIcon rootIcon;

	/**
	 * Node icon
	 */
	static protected final ImageIcon nodeIcon;

	/**
	 * Node special icon
	 */
	static protected final ImageIcon nodeSpecialIcon;

	/**
	 * Edges icon
	 */
	static protected final ImageIcon edgesIcon;

	/**
	 * Edge icon
	 */
	static protected final ImageIcon edgeIcon;

	/**
	 * Tools icon
	 */
	static protected final ImageIcon toolsIcon;

	/**
	 * Menu icon
	 */
	static protected final ImageIcon menuIcon;

	/**
	 * Default icon
	 */
	static protected final ImageIcon defaultIcon;

	// font used

	/**
	 * Default font
	 */
	static protected final Font defaultFont;

	/**
	 * Bold font
	 */
	static protected final Font boldFont;

	/**
	 * Italic font
	 */
	static protected final Font italicFont;

	static
	{
		// icons
		treebolicIcon = new ImageIcon(Renderer.class.getResource("images/treehome.png")); //$NON-NLS-1$
		treeIcon = new ImageIcon(Renderer.class.getResource("images/treetree.png")); //$NON-NLS-1$
		nodesIcon = new ImageIcon(Renderer.class.getResource("images/treenodes.png")); //$NON-NLS-1$
		rootIcon = new ImageIcon(Renderer.class.getResource("images/treeroot.png")); //$NON-NLS-1$
		nodeIcon = new ImageIcon(Renderer.class.getResource("images/treenode.png")); //$NON-NLS-1$
		nodeSpecialIcon = new ImageIcon(Renderer.class.getResource("images/treenodespecial.png")); //$NON-NLS-1$
		edgesIcon = new ImageIcon(Renderer.class.getResource("images/treeedges.png")); //$NON-NLS-1$
		edgeIcon = new ImageIcon(Renderer.class.getResource("images/treeedge.png")); //$NON-NLS-1$
		toolsIcon = new ImageIcon(Renderer.class.getResource("images/treetools.png")); //$NON-NLS-1$
		menuIcon = new ImageIcon(Renderer.class.getResource("images/treemenuitem.png")); //$NON-NLS-1$
		defaultIcon = new ImageIcon(Renderer.class.getResource("images/treedefault.png")); //$NON-NLS-1$

		// fonts
		defaultFont = new Font(Font.DIALOG, Font.PLAIN, 12);
		boldFont = new Font(Font.DIALOG, Font.BOLD, 12);
		italicFont = new Font(Font.DIALOG, Font.ITALIC, 12);
	}

	/**
	 * Constructor
	 */
	public Renderer()
	{
		setOpenIcon(Renderer.defaultIcon);
		setClosedIcon(Renderer.defaultIcon);
		setLeafIcon(Renderer.nodeIcon);
		setBorderSelectionColor(Color.LIGHT_GRAY);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean isSelected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus)
	{
		final Component component = super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);

		final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
		final Object object = treeNode.getUserObject();
		if (object instanceof MutableNode)
		{
			final Node node = (Node) treeNode.getUserObject();
			final String label = node.getLabel().replaceAll("\n", "\\\\n");
			final String tooltip = node.getLabel().replaceAll("\n", "<br>");
			final String content = node.getContent();
			final boolean isSpecial = node.getLink() != null || node.getMountPoint() != null;
			final boolean isRoot = node.getParent() == null;
			setText(label);
			setIcon(isRoot ? Renderer.rootIcon : isSpecial ? Renderer.nodeSpecialIcon : Renderer.nodeIcon);
			setFont(Renderer.defaultFont);
			setForeground(node.getMountPoint() != null ? Color.RED : Color.BLACK);
			setToolTipText(getNodeTooltip(tooltip, content));
		}
		else if (object instanceof MutableEdge)
		{
			final Edge edge = (Edge) treeNode.getUserObject();
			setText(edge.toString());
			setFont(Renderer.defaultFont);
			setIcon(Renderer.edgeIcon);
			setForeground(Color.BLACK);
			setToolTipText(Messages.getString("Renderer.tooltip_edge")); //$NON-NLS-1$
		}
		else if (object instanceof TopWrapper)
		{
			setText(Messages.getString("Renderer.top")); //$NON-NLS-1$
			setFont(Renderer.defaultFont);
			setIcon(Renderer.treebolicIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_top")); //$NON-NLS-1$
		}
		else if (object instanceof TreeWrapper)
		{
			setText(Messages.getString("Renderer.tree")); //$NON-NLS-1$
			setFont(Renderer.defaultFont);
			setIcon(Renderer.treeIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_tree")); //$NON-NLS-1$
		}
		else if (object instanceof NodesWrapper)
		{
			setText(Messages.getString("Renderer.nodes")); //$NON-NLS-1$
			setFont(Renderer.defaultFont);
			setIcon(Renderer.nodesIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_nodes")); //$NON-NLS-1$
		}
		else if (object instanceof EdgesWrapper)
		{
			setText(Messages.getString("Renderer.edges")); //$NON-NLS-1$
			setFont(Renderer.defaultFont);
			setIcon(Renderer.edgesIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_edges")); //$NON-NLS-1$
		}
		else if (object instanceof ToolsWrapper)
		{
			setText(Messages.getString("Renderer.tools")); //$NON-NLS-1$
			setFont(Renderer.defaultFont);
			setIcon(Renderer.toolsIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_tools")); //$NON-NLS-1$
		}
		else if (object instanceof MenuWrapper)
		{
			setText(Messages.getString("Renderer.menu")); //$NON-NLS-1$
			setFont(Renderer.defaultFont);
			setIcon(Renderer.menuIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_menu")); //$NON-NLS-1$
		}
		else if (object instanceof MenuItemWrapper)
		{
			final MenuItemWrapper wrapper = (MenuItemWrapper) treeNode.getUserObject();
			final MenuItem menuItem = wrapper.menuItem;
			setText(menuItem.label);
			setFont(Renderer.defaultFont);
			setIcon(Renderer.menuIcon);
			setForeground(Color.BLACK);
			setToolTipText(Messages.getString("Renderer.tooltip_menuitem")); //$NON-NLS-1$
		}
		else
		{
			setText("?"); //$NON-NLS-1$
			setFont(Renderer.defaultFont);
			setIcon(Renderer.defaultIcon);
			setForeground(Color.GRAY);
			setToolTipText(""); //$NON-NLS-1$
		}

		// selection
		if (isSelected)
		{
			// System.out.println("*"+getText()+"*");
			setFont(Renderer.boldFont);
			setForeground(Color.BLACK);
		}
		return component;
	}

	/**
	 * Get node tooltip string
	 *
	 * @param label
	 *        node label
	 * @param content
	 *        node content
	 * @return tooltip string
	 */
	private String getNodeTooltip(final String label, final String content)
	{
		final StringBuilder buffer = new StringBuilder();
		buffer.append("<html><strong>Node</strong><br>"); //$NON-NLS-1$
		buffer.append(label);
		buffer.append("<br>"); //$NON-NLS-1$
		if (content != null)
		{
			buffer.append("<i>"); //$NON-NLS-1$
			if (content.length() < 16)
			{
				buffer.append(content);
			}
			else
			{
				buffer.append(content, 0, 16);
				buffer.append(" ..."); //$NON-NLS-1$
			}
			buffer.append("</i>"); //$NON-NLS-1$
		}
		buffer.append("</html>"); //$NON-NLS-1$
		return buffer.toString();
	}
}