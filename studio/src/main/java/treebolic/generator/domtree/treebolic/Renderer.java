/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.domtree.treebolic;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import treebolic.generator.domtree.DefaultDecorator;

/**
 * Treebolic-specific renderer
 *
 * @author Bernard Bou
 */
public class Renderer extends treebolic.generator.domtree.Renderer
{
	// icons

	/**
	 * Node icon
	 */
	static protected ImageIcon nodeIcon;

	/**
	 * Edge icon
	 */
	static protected ImageIcon edgeIcon;

	/**
	 * Link icon
	 */
	static protected ImageIcon linkIcon;

	// styles

	/**
	 * Style for node name
	 */
	static private final SimpleAttributeSet nodeNameStyle = new SimpleAttributeSet();

	/**
	 * Style for node data
	 */
	static private final SimpleAttributeSet nodeValueStyle = new SimpleAttributeSet();

	/**
	 * Style for edge name
	 */
	static private final SimpleAttributeSet edgeNameStyle = new SimpleAttributeSet();

	/**
	 * Style for link name
	 */
	static private final SimpleAttributeSet linkNameStyle = new SimpleAttributeSet();

	/**
	 * Style for edge data
	 */
	static private final SimpleAttributeSet edgeValueStyle = new SimpleAttributeSet();

	/**
	 * Style for link data
	 */
	static private final SimpleAttributeSet linkValueStyle = new SimpleAttributeSet();

	/**
	 * Style for src data
	 */
	static private final SimpleAttributeSet srcStyle = new SimpleAttributeSet();

	/**
	 * Edge ends style
	 */
	static private final SimpleAttributeSet edgeEndsStyle = new SimpleAttributeSet();

	// patterns

	/**
	 * Src pattern
	 */
	private static final Pattern srcPattern = Pattern.compile("src=\"([^\"]*)\""); //$NON-NLS-1$

	/**
	 * From pattern
	 */
	private static final Pattern fromPattern = Pattern.compile("from=\"([^\"]*)\""); //$NON-NLS-1$

	/**
	 * To pattern
	 */
	private static final Pattern toPattern = Pattern.compile("to=\"([^\"]*)\""); //$NON-NLS-1$

	// maps

	/**
	 * Name styles for classes
	 */
	private final Map<String, SimpleAttributeSet> typeToNameStyleMap;

	/**
	 * Value styles for classes
	 */
	private final Map<String, SimpleAttributeSet> typeToValueStyleMap;

	/**
	 * Icons for classes
	 */
	private final Map<String, Icon> typeToIconMap;

	static
	{
		// name styles
		StyleConstants.setFontFamily(Renderer.nodeNameStyle, Font.DIALOG);
		StyleConstants.setFontSize(Renderer.nodeNameStyle, 11);
		StyleConstants.setBold(Renderer.nodeNameStyle, true);
		StyleConstants.setForeground(Renderer.nodeNameStyle, Color.BLACK);
		StyleConstants.setBackground(Renderer.nodeNameStyle, Color.ORANGE);

		StyleConstants.setFontFamily(Renderer.edgeNameStyle, Font.DIALOG);
		StyleConstants.setFontSize(Renderer.edgeNameStyle, 11);
		StyleConstants.setBold(Renderer.edgeNameStyle, true);
		StyleConstants.setForeground(Renderer.edgeNameStyle, Color.BLACK);
		StyleConstants.setBackground(Renderer.edgeNameStyle, Color.ORANGE);

		StyleConstants.setFontFamily(Renderer.linkNameStyle, Font.DIALOG);
		StyleConstants.setFontSize(Renderer.linkNameStyle, 11);
		StyleConstants.setBold(Renderer.linkNameStyle, true);
		StyleConstants.setForeground(Renderer.linkNameStyle, Color.WHITE);
		StyleConstants.setBackground(Renderer.linkNameStyle, Color.BLUE);

		// value styles
		StyleConstants.setFontFamily(Renderer.nodeValueStyle, Font.DIALOG);
		StyleConstants.setFontSize(Renderer.nodeValueStyle, 10);
		StyleConstants.setForeground(Renderer.nodeValueStyle, Color.BLACK);

		StyleConstants.setFontFamily(Renderer.edgeValueStyle, Font.DIALOG);
		StyleConstants.setFontSize(Renderer.edgeValueStyle, 10);
		StyleConstants.setForeground(Renderer.edgeValueStyle, Color.BLACK);

		StyleConstants.setFontFamily(Renderer.linkValueStyle, Font.DIALOG);
		StyleConstants.setFontSize(Renderer.linkValueStyle, 10);
		StyleConstants.setForeground(Renderer.linkValueStyle, Color.BLACK);

		// pattern styles
		StyleConstants.setForeground(Renderer.srcStyle, Color.MAGENTA);

		// images
		Renderer.nodeIcon = new ImageIcon(Renderer.class.getResource("images/treenode.gif")); //$NON-NLS-1$
		Renderer.edgeIcon = new ImageIcon(Renderer.class.getResource("images/treeedge.gif")); //$NON-NLS-1$
		Renderer.linkIcon = new ImageIcon(Renderer.class.getResource("images/treelink.gif")); //$NON-NLS-1$
	}

	/**
	 * Constructor
	 */
	public Renderer()
	{
		// styles for class
		this.typeToNameStyleMap = new HashMap<>();
		this.typeToNameStyleMap.put("node", Renderer.nodeNameStyle); //$NON-NLS-1$
		this.typeToNameStyleMap.put("edge", Renderer.nodeNameStyle); //$NON-NLS-1$
		this.typeToNameStyleMap.put("a", Renderer.linkNameStyle); //$NON-NLS-1$
		this.typeToValueStyleMap = new HashMap<>();
		this.typeToValueStyleMap.put("node", Renderer.edgeValueStyle); //$NON-NLS-1$
		this.typeToValueStyleMap.put("edge", Renderer.edgeValueStyle); //$NON-NLS-1$
		this.typeToValueStyleMap.put("a", Renderer.linkValueStyle); //$NON-NLS-1$

		// styles for patterns
		this.patterns.add(this.srcPattern);
		this.patterns.add(this.fromPattern);
		this.patterns.add(this.toPattern);
		this.patternToStyleMap.put(this.srcPattern, Renderer.srcStyle);
		this.patternToStyleMap.put(this.fromPattern, Renderer.edgeEndsStyle);
		this.patternToStyleMap.put(this.toPattern, Renderer.edgeEndsStyle);

		// icons for class
		this.typeToIconMap = new HashMap<>();
		this.typeToIconMap.put("node", Renderer.nodeIcon); //$NON-NLS-1$
		this.typeToIconMap.put("edge", Renderer.edgeIcon); //$NON-NLS-1$
		this.typeToIconMap.put("a", Renderer.linkIcon); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.domtree.Renderer#getIconStyle(org.w3c.dom.Node)
	 */
	@Override
	protected Icon getIconStyle(final Node node)
	{
		if (node instanceof Element)
		{
			final Element element = (Element) node;
			final Icon icon = this.typeToIconMap.get(element.getNodeName());
			if (icon != null)
				return icon;
		}
		return super.getIconStyle(node);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.domtree.Renderer#getNameStyle(org.w3c.dom.Node)
	 */
	@Override
	protected SimpleAttributeSet getNameStyle(final Node node)
	{
		if (node instanceof Element)
		{
			final Element element = (Element) node;
			final SimpleAttributeSet style = this.typeToNameStyleMap.get(element.getNodeName());
			if (style != null)
				return style;
		}
		return super.getNameStyle(node);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.domtree.Renderer#getValueStyle(org.w3c.dom.Node)
	 */
	@Override
	protected SimpleAttributeSet getValueStyle(final Node node)
	{
		if (node instanceof Element)
		{
			final Element element = (Element) node;
			final SimpleAttributeSet style = this.typeToValueStyleMap.get(element.getNodeName());
			if (style != null)
				return style;
		}
		return super.getValueStyle(node);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.domtree.Renderer#makeDecorator(org.w3c.dom.Node)
	 */
	@Override
	protected DefaultDecorator makeDecorator(final Node node)
	{
		if (node.getNodeType() != Node.ELEMENT_NODE)
			return super.makeDecorator(node);

		final Element element = (Element) node;
		final String tag = element.getTagName();
		switch (tag)
		{
			case "node":
				return new NodeDecorator(element);
			case "edge":
				return new EdgeDecorator(element);
			case "a":
				return new LinkDecorator(element);
		}
		return super.makeDecorator(node);
	}
}