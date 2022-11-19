/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import treebolic.glue.Color;
import treebolic.model.IEdge;
import treebolic.model.INode;
import treebolic.model.MenuItem;
import treebolic.model.Model;
import treebolic.model.MountPoint;
import treebolic.model.Utils;

/**
 * Model to document transformer
 *
 * @author Bernard Bou
 */
public class ModelToDocumentTransformer
{
	static private final boolean validate = true;

	/**
	 * Transform model to document
	 *
	 * @param model
	 *        model
	 * @return DOM document
	 */
	public static Document transform(final Model model)
	{
		if (model == null)
			return null;

		DocumentBuilder builder;
		try
		{
			builder = ModelToDocumentTransformer.makeDocumentBuilder();
			final Document document = builder.newDocument();
			document.appendChild(document.createComment("created " + new Date())); //$NON-NLS-1$
			final Map<String, Object> attributes = new HashMap<String, Object>()
			{
				/**
				 *
				 */
				private static final long serialVersionUID = -2918948477463933969L;

				/*
				 * (non-Javadoc)
				 * @see java.util.Hashtable#put(java.lang.String, java.lang.Object)
				 */
				@Override
				public Object put(final String key, final Object value)
				{
					if (value == null)
						return null;
					return super.put(key, value);
				}
			};

			// top
			final Element topElement = document.createElement("treebolic"); //$NON-NLS-1$
			attributes.put("toolbar", model.settings.hasToolbarFlag); //$NON-NLS-1$
			attributes.put("statusbar", model.settings.hasStatusbarFlag); //$NON-NLS-1$
			attributes.put("popupmenu", model.settings.hasPopUpMenuFlag); //$NON-NLS-1$
			attributes.put("tooltip", model.settings.hasToolTipFlag); //$NON-NLS-1$
			attributes.put("tooltip-displays-content", model.settings.toolTipDisplaysContentFlag); //$NON-NLS-1$
			attributes.put("focus-on-hover", model.settings.focusOnHoverFlag); //$NON-NLS-1$
			attributes.put("focus", model.settings.focus); //$NON-NLS-1$
			attributes.put("xmoveto", model.settings.xMoveTo); //$NON-NLS-1$
			attributes.put("ymoveto", model.settings.yMoveTo); //$NON-NLS-1$
			attributes.put("xshift", model.settings.xShift); //$NON-NLS-1$
			attributes.put("yshift", model.settings.yShift); //$NON-NLS-1$
			ModelToDocumentTransformer.setAttributes(topElement, attributes);
			document.appendChild(topElement);

			// tree
			final Element treeElement = document.createElement("tree"); //$NON-NLS-1$
			attributes.put("backcolor", model.settings.backColor); //$NON-NLS-1$
			attributes.put("forecolor", model.settings.foreColor); //$NON-NLS-1$
			attributes.put("orientation", model.settings.orientation); //$NON-NLS-1$
			attributes.put("expansion", model.settings.expansion); //$NON-NLS-1$
			attributes.put("sweep", model.settings.sweep); //$NON-NLS-1$
			attributes.put("preserve-orientation", model.settings.preserveOrientationFlag); //$NON-NLS-1$
			attributes.put("fontface", model.settings.fontFace); //$NON-NLS-1$
			attributes.put("fontsize", model.settings.fontSize); //$NON-NLS-1$
			attributes.put("scalefonts", model.settings.downscaleFontsFlag); //$NON-NLS-1$
			attributes.put("fontscaler", model.settings.fontDownscaler); //$NON-NLS-1$
			attributes.put("scaleimages", model.settings.downscaleImagesFlag); //$NON-NLS-1$
			attributes.put("imagescaler", model.settings.imageDownscaler); //$NON-NLS-1$
			ModelToDocumentTransformer.setAttributes(treeElement, attributes);
			topElement.appendChild(treeElement);
			// image
			if (model.settings.backgroundImageFile != null)
			{
				treeElement.appendChild(ModelToDocumentTransformer.makeImage(document, model.settings.backgroundImageFile));
			}

			// nodes
			final Element nodesElement = document.createElement("nodes"); //$NON-NLS-1$
			attributes.put("backcolor", model.settings.nodeBackColor); //$NON-NLS-1$
			attributes.put("forecolor", model.settings.nodeForeColor); //$NON-NLS-1$
			attributes.put("border", model.settings.borderFlag); //$NON-NLS-1$
			attributes.put("ellipsize", model.settings.ellipsizeFlag); //$NON-NLS-1$
			attributes.put("max-lines", model.settings.labelMaxLines); //$NON-NLS-1$
			attributes.put("extra-line-factor", model.settings.labelExtraLineFactor); //$NON-NLS-1$
			ModelToDocumentTransformer.setAttributes(nodesElement, attributes);
			treeElement.appendChild(nodesElement);

			if (model.settings.defaultNodeImage != null)
			{
				nodesElement.appendChild(ModelToDocumentTransformer.makeImage(document, model.settings.defaultNodeImage));
			}

			// default.treeedge
			if (model.settings.defaultTreeEdgeImage != null || model.settings.treeEdgeColor != null || model.settings.treeEdgeStyle != null)
			{
				final Element defaultTreeEdgeElement = document.createElement("default.treeedge"); //$NON-NLS-1$
				// color, stroke,toterminator,fromterminator,hidden
				if (model.settings.treeEdgeColor != null)
				{
					defaultTreeEdgeElement.setAttribute("color", Utils.colorToString(model.settings.treeEdgeColor)); //$NON-NLS-1$
				}
				if (model.settings.treeEdgeStyle != null)
				{
					ModelToDocumentTransformer.setStyleAttributes(defaultTreeEdgeElement, model.settings.treeEdgeStyle);
				}
				// image
				if (model.settings.defaultTreeEdgeImage != null)
				{
					defaultTreeEdgeElement.appendChild(ModelToDocumentTransformer.makeImage(document, model.settings.defaultTreeEdgeImage));
				}
				nodesElement.appendChild(defaultTreeEdgeElement);
			}

			// node iteration
			nodesElement.appendChild(ModelToDocumentTransformer.makeNode(document, model.tree.getRoot()));

			// edges
			final List<IEdge> edges = model.tree.getEdges();
			if (edges != null && !edges.isEmpty() || model.settings != null && model.settings.edgesAsArcsFlag != null)
			{
				final Element edgesElement = document.createElement("edges"); //$NON-NLS-1$
				treeElement.appendChild(edgesElement);

				// as arcs
				attributes.put("arcs", model.settings.edgesAsArcsFlag); //$NON-NLS-1$
				ModelToDocumentTransformer.setAttributes(edgesElement, attributes);

				// default edge
				if (model.settings.defaultEdgeImage != null || model.settings.edgeColor != null || model.settings.edgeStyle != null)
				{
					final Element defaultEdgeElement = document.createElement("default.edge"); //$NON-NLS-1$
					// attributes
					// color, stroke,toterminator,fromterminator,hidden
					if (model.settings.edgeColor != null)
					{
						defaultEdgeElement.setAttribute("color", Utils.colorToString(model.settings.edgeColor)); //$NON-NLS-1$
					}
					if (model.settings.edgeStyle != null)
					{
						ModelToDocumentTransformer.setStyleAttributes(defaultEdgeElement, model.settings.edgeStyle);
					}
					// image
					if (model.settings.defaultEdgeImage != null)
					{
						defaultEdgeElement.appendChild(ModelToDocumentTransformer.makeImage(document, model.settings.defaultEdgeImage));
					}
					edgesElement.appendChild(defaultEdgeElement);
				}

				// edge iteration
				if (edges != null)
				{
					for (final IEdge edge : edges)
						if (edge.getFrom() != null && edge.getTo() != null)
						{
							edgesElement.appendChild(ModelToDocumentTransformer.makeEdge(document, edge));
						}
				}
			}

			// menu item iteration
			if (model.settings.menu != null && !model.settings.menu.isEmpty())
			{
				// menu
				final Element toolsElement = document.createElement("tools"); //$NON-NLS-1$
				topElement.appendChild(toolsElement);
				final Element menuElement = document.createElement("menu"); //$NON-NLS-1$
				toolsElement.appendChild(menuElement);

				for (final MenuItem menuItem : model.settings.menu)
				{
					final Element menuItemElement = ModelToDocumentTransformer.makeMenuItem(document, menuItem);
					menuElement.appendChild(menuItemElement);
				}
			}
			return document;
		}
		catch (final ParserConfigurationException exception)
		{
			// do nothing
		}
		return null;
	}

	/**
	 * Make document builder
	 *
	 * @return document builder
	 * @throws ParserConfigurationException parser configuration exception
	 */
	static private DocumentBuilder makeDocumentBuilder() throws ParserConfigurationException
	{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setCoalescing(true);
		factory.setIgnoringComments(true);
		factory.setNamespaceAware(false);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setValidating(ModelToDocumentTransformer.validate);
		return factory.newDocumentBuilder();
	}

	/**
	 * Make node element
	 *
	 * @param document
	 *        document
	 * @param node0
	 *        node
	 * @return node element
	 */
	static private Element makeNode(final Document document, final INode node0)
	{
		INode node = node0;

		// mounted node : substitute mounting node
		MountPoint mountPoint = node.getMountPoint();
		if (mountPoint instanceof MountPoint.Mounted)
		{
			final MountPoint.Mounted mountedPoint = (MountPoint.Mounted) mountPoint;
			node = mountedPoint.mountingNode;
			mountPoint = node.getMountPoint();
		}

		final Element nodeElement = document.createElement("node"); //$NON-NLS-1$

		// attributes
		// id, backcolor, forecolor
		nodeElement.setAttribute("id", node.getId()); //$NON-NLS-1$
		Color color = node.getBackColor();
		if (color != null)
		{
			nodeElement.setAttribute("backcolor", Utils.colorToString(color)); //$NON-NLS-1$
		}
		color = node.getForeColor();
		if (color != null)
		{
			nodeElement.setAttribute("forecolor", Utils.colorToString(color)); //$NON-NLS-1$
		}
		final double weight = node.getWeight();
		if (weight < 0.)
		{
			nodeElement.setAttribute("weight", Double.toString(-weight)); //$NON-NLS-1$
		}

		// label
		String string;
		string = node.getLabel();
		if (string != null && !string.isEmpty())
		{
			nodeElement.appendChild(ModelToDocumentTransformer.makeLabel(document, string));
		}

		// content
		string = node.getContent();
		if (string != null && !string.isEmpty())
		{
			nodeElement.appendChild(ModelToDocumentTransformer.makeContent(document, string));
		}

		// treeedge
		final String edgeLabel = node.getEdgeLabel();
		final String edgeImage = node.getEdgeImageFile();
		final Color edgeColor = node.getEdgeColor();
		final Integer edgeStyle = node.getEdgeStyle();
		if (edgeLabel != null || edgeImage != null || edgeColor != null || edgeStyle != null)
		{
			final Element treeEdgeElement = document.createElement("treeedge"); //$NON-NLS-1$
			// attributes
			// color, stroke,toterminator,fromterminator,hidden
			if (edgeColor != null)
			{
				treeEdgeElement.setAttribute("color", Utils.colorToString(edgeColor)); //$NON-NLS-1$
			}
			if (edgeStyle != null)
			{
				ModelToDocumentTransformer.setStyleAttributes(treeEdgeElement, edgeStyle);
			}
			// label
			if (edgeLabel != null)
			{
				treeEdgeElement.appendChild(ModelToDocumentTransformer.makeLabel(document, edgeLabel));
			}
			// image
			if (edgeImage != null)
			{
				treeEdgeElement.appendChild(ModelToDocumentTransformer.makeImage(document, edgeImage));
			}
			nodeElement.appendChild(treeEdgeElement);
		}

		// image
		string = node.getImageFile();
		if (string != null && !string.isEmpty())
		{
			nodeElement.appendChild(ModelToDocumentTransformer.makeImage(document, string));
		}

		// link
		string = node.getLink();
		if (string != null && !string.isEmpty())
		{
			nodeElement.appendChild(ModelToDocumentTransformer.makeLink(document, string, node.getTarget()));
		}

		// mount
		if (mountPoint instanceof MountPoint.Mounting)
		{
			final MountPoint.Mounting mountingPoint = (MountPoint.Mounting) mountPoint;
			nodeElement.appendChild(ModelToDocumentTransformer.makeMountPoint(document, mountingPoint));
		}

		// recurse
		for (final INode child : node.getChildren())
		{
			nodeElement.appendChild(ModelToDocumentTransformer.makeNode(document, child));
		}

		return nodeElement;
	}

	/**
	 * Make edge element
	 *
	 * @return edge element
	 */
	static private Element makeEdge(final Document document, final IEdge edge)
	{
		final Element edgeElement = document.createElement("edge"); //$NON-NLS-1$

		// attributes
		// from,to,color,stroke,toterminator,fromterminator,hidden
		edgeElement.setAttribute("from", edge.getFrom().getId()); //$NON-NLS-1$
		edgeElement.setAttribute("to", edge.getTo().getId()); //$NON-NLS-1$
		final Integer style = edge.getStyle();
		if (style != null)
		{
			ModelToDocumentTransformer.setStyleAttributes(edgeElement, style);
		}
		final Color color = edge.getColor();
		if (color != null)
		{
			edgeElement.setAttribute("color", Utils.colorToString(color)); //$NON-NLS-1$
		}

		// label
		final String label = edge.getLabel();
		if (label != null && !label.isEmpty())
		{
			edgeElement.appendChild(ModelToDocumentTransformer.makeLabel(document, label));
		}

		// img
		final String image = edge.getImageFile();
		if (image != null)
		{
			edgeElement.appendChild(ModelToDocumentTransformer.makeImage(document, image));
		}

		return edgeElement;
	}

	/**
	 * Make label element
	 *
	 * @param document
	 *        document
	 * @param string
	 *        label
	 * @return label element
	 */
	static private Element makeLabel(final Document document, final String string)
	{
		final Element element = document.createElement("label"); //$NON-NLS-1$
		final Text text = document.createTextNode(string);
		element.appendChild(text);
		return element;
	}

	/**
	 * Make content element
	 *
	 * @param document
	 *        document
	 * @param string
	 *        content
	 * @return content element
	 */
	static private Element makeContent(final Document document, final String string)
	{
		final Element contentElement = document.createElement("content"); //$NON-NLS-1$
		final CDATASection contentCData = document.createCDATASection(string);
		contentElement.appendChild(contentCData);
		return contentElement;
	}

	/**
	 * Make image element
	 *
	 * @param document
	 *        document
	 * @param imageSrc
	 *        image source
	 * @return image element
	 */
	static public Element makeImage(final Document document, final String imageSrc)
	{
		final Element element = document.createElement("img"); //$NON-NLS-1$
		element.setAttribute("src", imageSrc); //$NON-NLS-1$
		return element;
	}

	/**
	 * Make link element
	 *
	 * @param document
	 *        document
	 * @param href
	 *        link href
	 * @return link element
	 */
	static private Element makeLink(final Document document, final String href, final String target)
	{
		final Element element = document.createElement("a"); //$NON-NLS-1$
		element.setAttribute("href", href); //$NON-NLS-1$
		if (target != null && !target.isEmpty())
		{
			element.setAttribute("target", target); //$NON-NLS-1$
		}
		return element;
	}

	/**
	 * Make mountpoint element
	 *
	 * @param document
	 *        document
	 * @param mountPoint
	 *        mountpoint
	 * @return mountpoint element
	 */
	static private Element makeMountPoint(final Document document, final MountPoint.Mounting mountPoint)
	{
		final Element mountPointElement = document.createElement("mountpoint"); //$NON-NLS-1$

		// attributes
		if (mountPoint.now != null)
		{
			mountPointElement.setAttribute("now", mountPoint.now.toString()); //$NON-NLS-1$
		}

		// a
		final Element aElement = document.createElement("a"); //$NON-NLS-1$
		aElement.setAttribute("href", mountPoint.url); //$NON-NLS-1$
		mountPointElement.appendChild(aElement);
		return mountPointElement;
	}

	/**
	 * Make menuitem element
	 *
	 * @param document
	 *        document
	 * @param menuItem
	 *        menuitem
	 * @return menuitem
	 */
	static private Element makeMenuItem(final Document document, final MenuItem menuItem)
	{
		final Element menuItemElement = document.createElement("menuitem"); //$NON-NLS-1$

		// attributes
		// action (goto|search|focus), match-target, match-scope, match-mode (equals|startswith|includes)
		final String[] strings = Utils.toStrings(menuItem);
		if (strings[0] != null)
		{
			menuItemElement.setAttribute("action", strings[0]); //$NON-NLS-1$
		}
		if (strings[1] != null)
		{
			menuItemElement.setAttribute("match-scope", strings[1]); //$NON-NLS-1$
		}
		if (strings[2] != null)
		{
			menuItemElement.setAttribute("match-mode", strings[2]); //$NON-NLS-1$
		}
		if (menuItem.matchTarget != null && !menuItem.matchTarget.isEmpty())
		{
			menuItemElement.setAttribute("match-target", menuItem.matchTarget); //$NON-NLS-1$
		}

		// label
		final Element menuItemLabelElement = document.createElement("label"); //$NON-NLS-1$
		menuItemLabelElement.appendChild(document.createTextNode(menuItem.label));
		menuItemElement.appendChild(menuItemLabelElement);

		// a
		if (menuItem.link != null && !menuItem.link.isEmpty())
		{
			final Element aElement = document.createElement("a"); //$NON-NLS-1$
			aElement.setAttribute("href", menuItem.link); //$NON-NLS-1$
			menuItemElement.appendChild(aElement);
		}

		return menuItemElement;
	}

	/**
	 * Set style attributes
	 *
	 * @param element
	 *        element
	 * @param style
	 *        style
	 */
	static private void setStyleAttributes(final Element element, final Integer style)
	{
		if (style != null)
		{
			String string = Utils.toString(style, Utils.StyleComponent.STROKE);
			if (string != null && !string.isEmpty())
			{
				element.setAttribute("stroke", string); //$NON-NLS-1$
			}
			String string2 = Utils.toString(style, Utils.StyleComponent.STROKEWIDTH);
			if (string2 != null && !string2.isEmpty())
			{
				StringBuilder builder = new StringBuilder();
				if (string != null && !string.isEmpty())
				{
					builder.append(string);
					builder.append(' ');
				}
				builder.append(string2);
				element.setAttribute("stroke", builder.toString()); //$NON-NLS-1$
			}
			string = Utils.toString(style, Utils.StyleComponent.FROMTERMINATOR);
			if (string != null && !string.isEmpty())
			{
				element.setAttribute("fromterminator", string); //$NON-NLS-1$
			}
			string = Utils.toString(style, Utils.StyleComponent.TOTERMINATOR);
			if (string != null && !string.isEmpty())
			{
				element.setAttribute("toterminator", string); //$NON-NLS-1$
			}
			if ((style & IEdge.LINE) != 0)
			{
				element.setAttribute("line", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if ((style & IEdge.HIDDEN) != 0)
			{
				element.setAttribute("hidden", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	/**
	 * Set attributes
	 *
	 * @param element
	 *        element
	 * @param attributes
	 *        attributes
	 */
	static private void setAttributes(final Element element, final Map<String, Object> attributes)
	{
		for (final String key : attributes.keySet())
		{
			final Object value = attributes.get(key);
			if (value != null)
			{
				String string;
				if (value instanceof Color)
				{
					string = Utils.colorToString((Color) value);
				}
				else if (value instanceof float[])
				{
					string = Utils.floatsToString((float[]) value);
				}
				else
				{
					string = value.toString();
				}
				element.setAttribute(key, string);
			}
		}
		attributes.clear();
	}
}
