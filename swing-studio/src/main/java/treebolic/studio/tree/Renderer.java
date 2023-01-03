/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio.tree;

import java.awt.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.studio.Messages;
import treebolic.model.MenuItem;
import treebolic.model.*;

/**
 * XML tree renderer
 *
 * @author Bernard Bou
 */
public class Renderer extends DefaultTreeCellRenderer
{
	// icons to use

	/**
	 * Treebolic icon
	 */
	@NonNull
	static protected final ImageIcon treebolicIcon;

	/**
	 * Tree icon
	 */
	@NonNull
	static protected final ImageIcon treeIcon;

	/**
	 * Nodes icon
	 */
	@NonNull
	static protected final ImageIcon nodesIcon;

	/**
	 * Root icon
	 */
	@NonNull
	static protected final ImageIcon rootIcon;

	/**
	 * Node icon
	 */
	@NonNull
	static protected final ImageIcon nodeIcon;

	/**
	 * Node special icon
	 */
	@NonNull
	static protected final ImageIcon nodeSpecialIcon;

	/**
	 * Edges icon
	 */
	@NonNull
	static protected final ImageIcon edgesIcon;

	/**
	 * Edge icon
	 */
	@NonNull
	static protected final ImageIcon edgeIcon;

	/**
	 * Tools icon
	 */
	@NonNull
	static protected final ImageIcon toolsIcon;

	/**
	 * Menu icon
	 */
	@NonNull
	static protected final ImageIcon menuIcon;

	/**
	 * Default icon
	 */
	@NonNull
	static protected final ImageIcon defaultIcon;

	// font used

	/**
	 * Default font
	 */
	@NonNull
	static protected final Font defaultFont;

	/**
	 * Bold font
	 */
	@NonNull
	static protected final Font boldFont;

	/**
	 * Italic font
	 */
	@NonNull
	static protected final Font italicFont;

	static
	{
		// icons
		final URL treebolicIconUrl = Renderer.class.getResource("images/treehome.png");
		final URL treeIconUrl = Renderer.class.getResource("images/treetree.png");
		final URL nodesIconUrl = Renderer.class.getResource("images/treenodes.png");
		final URL rootIconUrl = Renderer.class.getResource("images/treeroot.png");
		final URL nodeIconUrl =Renderer.class.getResource("images/treenode.png");
		final URL nodeSpecialIconUrl = Renderer.class.getResource("images/treenodespecial.png");
		final URL edgesIconUrl = Renderer.class.getResource("images/treeedges.png");
		final URL edgeIconUrl = Renderer.class.getResource("images/treeedge.png");
		final URL toolsIconUrl = Renderer.class.getResource("images/treetools.png");
		final URL menuIconUrl = Renderer.class.getResource("images/treemenuitem.png");
		final URL defaultIconUrl = Renderer.class.getResource("images/treedefault.png");

		assert treebolicIconUrl != null;
		treebolicIcon = new ImageIcon(treebolicIconUrl);
		assert treeIconUrl != null;
		treeIcon = new ImageIcon(treeIconUrl);
		assert nodesIconUrl != null;
		nodesIcon = new ImageIcon(nodesIconUrl);
		assert rootIconUrl != null;
		rootIcon = new ImageIcon(rootIconUrl);
		assert nodeIconUrl != null;
		nodeIcon = new ImageIcon(nodeIconUrl);
		assert nodeSpecialIconUrl != null;
		nodeSpecialIcon = new ImageIcon(nodeSpecialIconUrl);
		assert edgesIconUrl != null;
		edgesIcon = new ImageIcon(edgesIconUrl);
		assert edgeIconUrl != null;
		edgeIcon = new ImageIcon(edgeIconUrl);
		assert toolsIconUrl != null;
		toolsIcon = new ImageIcon(toolsIconUrl);
		assert menuIconUrl != null;
		menuIcon = new ImageIcon(menuIconUrl);
		assert defaultIconUrl != null;
		defaultIcon = new ImageIcon(defaultIconUrl);

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

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean isSelected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus)
	{
		final Component component = super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);

		final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
		final Object object = treeNode.getUserObject();
		if (object instanceof MutableNode)
		{
			final Node node = (Node) treeNode.getUserObject();
			@Nullable String label = node.getLabel();
			if (label != null)
			{
				label = label.replaceAll("\n", "\\\\n");
			}
			@Nullable String tooltip = node.getLabel();
			if (tooltip != null)
			{
				tooltip = tooltip.replaceAll("\n", "<br>");
			}
			@Nullable final String content = node.getContent();
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
			setToolTipText(Messages.getString("Renderer.tooltip_edge"));
		}
		else if (object instanceof TopWrapper)
		{
			setText(Messages.getString("Renderer.top"));
			setFont(Renderer.defaultFont);
			setIcon(Renderer.treebolicIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_top"));
		}
		else if (object instanceof TreeWrapper)
		{
			setText(Messages.getString("Renderer.tree"));
			setFont(Renderer.defaultFont);
			setIcon(Renderer.treeIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_tree"));
		}
		else if (object instanceof NodesWrapper)
		{
			setText(Messages.getString("Renderer.nodes"));
			setFont(Renderer.defaultFont);
			setIcon(Renderer.nodesIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_nodes"));
		}
		else if (object instanceof EdgesWrapper)
		{
			setText(Messages.getString("Renderer.edges"));
			setFont(Renderer.defaultFont);
			setIcon(Renderer.edgesIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_edges"));
		}
		else if (object instanceof ToolsWrapper)
		{
			setText(Messages.getString("Renderer.tools"));
			setFont(Renderer.defaultFont);
			setIcon(Renderer.toolsIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_tools"));
		}
		else if (object instanceof MenuWrapper)
		{
			setText(Messages.getString("Renderer.menu"));
			setFont(Renderer.defaultFont);
			setIcon(Renderer.menuIcon);
			setForeground(Color.GRAY);
			setToolTipText(Messages.getString("Renderer.tooltip_menu"));
		}
		else if (object instanceof MenuItemWrapper)
		{
			final MenuItemWrapper wrapper = (MenuItemWrapper) treeNode.getUserObject();
			final MenuItem menuItem = wrapper.menuItem;
			setText(menuItem.label);
			setFont(Renderer.defaultFont);
			setIcon(Renderer.menuIcon);
			setForeground(Color.BLACK);
			setToolTipText(Messages.getString("Renderer.tooltip_menuitem"));
		}
		else
		{
			setText("?");
			setFont(Renderer.defaultFont);
			setIcon(Renderer.defaultIcon);
			setForeground(Color.GRAY);
			setToolTipText("");
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
	 * @param label   node label
	 * @param content node content
	 * @return tooltip string
	 */
	@NonNull
	private String getNodeTooltip(final String label, @Nullable final String content)
	{
		@NonNull final StringBuilder buffer = new StringBuilder();
		buffer.append("<html><strong>Node</strong><br>");
		buffer.append(label);
		buffer.append("<br>");
		if (content != null)
		{
			buffer.append("<i>");
			if (content.length() < 16)
			{
				buffer.append(content);
			}
			else
			{
				buffer.append(content, 0, 16);
				buffer.append(" ...");
			}
			buffer.append("</i>");
		}
		buffer.append("</html>");
		return buffer.toString();
	}
}