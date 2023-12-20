/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.domtree;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreeCellRenderer;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Renderer
 *
 * @author Bernard Bou
 */
public class Renderer implements TreeCellRenderer
{
	// icons

	/**
	 * Element icon
	 */
	static private ImageIcon elementIcon;

	/**
	 * Text icon
	 */
	static private ImageIcon textIcon;

	/**
	 * Default icon
	 */
	static private ImageIcon defaultIcon;

	// styles

	/**
	 * Style for element name
	 */
	static private final SimpleAttributeSet elementNameStyle = new SimpleAttributeSet();

	/**
	 * Style for element data
	 */
	private static final SimpleAttributeSet elementValueStyle = new SimpleAttributeSet();

	/**
	 * Style for node name
	 */
	private static final SimpleAttributeSet defaultNameStyle = new SimpleAttributeSet();

	/**
	 * Style for node data
	 */
	private static final SimpleAttributeSet defaultValueStyle = new SimpleAttributeSet();

	/**
	 * Style for id data
	 */
	private static final SimpleAttributeSet idStyle = new SimpleAttributeSet();

	/**
	 * Patterns
	 */
	private static final Pattern idPattern = Pattern.compile("id=\"([^\"]*)\"");

	/**
	 * Pattern list
	 */
	protected List<Pattern> patterns;

	/**
	 * Styles for patterns
	 */
	protected Map<Pattern, SimpleAttributeSet> patternToStyleMap;

	static
	{
		// styles
		StyleConstants.setFontFamily(Renderer.elementNameStyle, Font.DIALOG);
		StyleConstants.setFontSize(Renderer.elementNameStyle, 10);
		StyleConstants.setBold(Renderer.elementNameStyle, true);
		StyleConstants.setBackground(Renderer.elementNameStyle, new Color(255, 255, 200));

		StyleConstants.setFontFamily(Renderer.elementValueStyle, Font.DIALOG);
		StyleConstants.setFontSize(Renderer.elementValueStyle, 10);

		StyleConstants.setFontFamily(Renderer.defaultNameStyle, Font.DIALOG);
		StyleConstants.setFontSize(Renderer.defaultNameStyle, 10);
		StyleConstants.setItalic(Renderer.defaultNameStyle, true);
		StyleConstants.setForeground(Renderer.defaultNameStyle, Color.GRAY);

		StyleConstants.setFontFamily(Renderer.defaultValueStyle, Font.DIALOG);
		StyleConstants.setFontSize(Renderer.defaultValueStyle, 10);
		StyleConstants.setForeground(Renderer.defaultValueStyle, Color.GRAY);

		StyleConstants.setForeground(Renderer.idStyle, Color.BLUE);

		// icons
		@Nullable final URL elementIconUrl = Renderer.class.getResource("images/treeelement.gif");
		@Nullable final URL textIcon = Renderer.class.getResource("images/treetext.gif");
		@Nullable final URL defaultIconUrl = Renderer.class.getResource("images/treedefault.gif");
		assert elementIconUrl != null;
		Renderer.elementIcon = new ImageIcon(elementIconUrl);
		assert textIcon != null;
		Renderer.textIcon = new ImageIcon(textIcon);
		assert defaultIconUrl != null;
		Renderer.defaultIcon = new ImageIcon(defaultIconUrl);
	}

	// components

	/**
	 * Renderer component
	 */
	protected JPanel panel;

	/**
	 * Icon subcomponent
	 */
	protected JLabel iconComponent;

	/**
	 * Text sub component
	 */
	protected JTextPane textComponent;

	/**
	 * Constructor
	 */
	public Renderer()
	{
		// components
		this.iconComponent = new JLabel();
		this.textComponent = new JTextPane();

		// assemble
		this.iconComponent.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		this.textComponent.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		this.panel = new JPanel();
		this.panel.setOpaque(false);
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.X_AXIS));
		this.panel.add(this.iconComponent);
		this.panel.add(this.textComponent);

		// styles for patterns
		this.patterns = new ArrayList<>();
		this.patterns.add(idPattern);
		this.patternToStyleMap = new HashMap<>();
		this.patternToStyleMap.put(idPattern, Renderer.idStyle);
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object nodeValue, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus)
	{
		final Node node = (Node) nodeValue;

		// decorator
		final DefaultDecorator decorator = makeDecorator(node);

		// strings
		@NonNull final String name = decorator.getName();
		final String value = decorator.getValue();
		final int nameLength = name.length();

		// document
		final StyledDocument styledDocument = this.textComponent.getStyledDocument();

		// set text
		try
		{
			styledDocument.remove(0, styledDocument.getLength());
			styledDocument.insertString(0, name, getNameStyle(node));
			if (value != null)
			{
				styledDocument.insertString(nameLength, " ", null);
				styledDocument.insertString(nameLength + 1, value, getValueStyle(node));
			}
		}
		catch (final BadLocationException exception)
		{
			// do nothing
		}

		// patterns
		applyStyleToPatterns(value, nameLength);

		// icon
		final Icon icon = getIconStyle(node);
		this.iconComponent.setIcon(icon);

		return this.panel;
	}

	/**
	 * Apply styles to patterns
	 *
	 * @param string value string
	 * @param offset offset of value in text
	 */
	protected void applyStyleToPatterns(@Nullable final String string, final int offset)
	{
		if (string != null)
		{
			final StyledDocument styledDocument = this.textComponent.getStyledDocument();
			for (@NonNull final Pattern pattern : this.patterns)
			{
				@NonNull final Matcher matcher = pattern.matcher(string);
				while (matcher.find())
				{
					for (int g = 1; g < matcher.groupCount(); g++)
					{
						final int from = matcher.start(g) + offset + 1;
						final int to = matcher.end(g) + offset + 1;
						styledDocument.setCharacterAttributes(from, to - from, this.patternToStyleMap.get(pattern), false);
					}
				}
			}
		}
	}

	/**
	 * Make node decorator
	 *
	 * @param node node
	 * @return node decorator
	 */
	protected DefaultDecorator makeDecorator(final Node node)
	{
		return node instanceof Element ? new ElementDecorator(node) : new DefaultDecorator(node);
	}

	/**
	 * Get name style for node
	 *
	 * @param node node
	 * @return style
	 */
	protected SimpleAttributeSet getNameStyle(final Node node)
	{
		return node instanceof Element ? Renderer.elementNameStyle : Renderer.defaultNameStyle;
	}

	/**
	 * Get value style for node
	 *
	 * @param node node
	 * @return style
	 */
	protected SimpleAttributeSet getValueStyle(final Node node)
	{
		return node instanceof Element ? Renderer.elementValueStyle : Renderer.defaultValueStyle;
	}

	/**
	 * Get icon for node
	 *
	 * @param node node
	 * @return style
	 */
	protected Icon getIconStyle(@NonNull final Node node)
	{
		switch (node.getNodeType())
		{
			case Node.ELEMENT_NODE:
				return Renderer.elementIcon;
			case Node.TEXT_NODE:
				return Renderer.textIcon;
			default:
				return Renderer.defaultIcon;
		}
	}
}