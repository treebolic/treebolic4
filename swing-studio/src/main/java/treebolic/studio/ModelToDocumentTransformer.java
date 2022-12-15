/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.*;

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
	 * @param model model
	 * @return DOM document
	 */
	public static Document transform(@Nullable final Model model)
	{
		if (model == null)
		{
			return null;
		}

		DocumentBuilder builder;
		try
		{
			builder = ModelToDocumentTransformer.makeDocumentBuilder();
			final Document document = builder.newDocument();
			document.appendChild(document.createComment("created " + new Date()));
			@NonNull final Map<String, Object> attributes = new HashMap<String, Object>()
			{
				/*
				 * (non-Javadoc)
				 * @see java.util.Hashtable#put(java.lang.String, java.lang.Object)
				 */
				@Override
				public Object put(final String key, @Nullable final Object value)
				{
					if (value == null)
					{
						return null;
					}
					return super.put(key, value);
				}
			};

			// top
			final Element topElement = document.createElement("treebolic");
			attributes.put("toolbar", model.settings.hasToolbarFlag);
			attributes.put("statusbar", model.settings.hasStatusbarFlag);
			attributes.put("popupmenu", model.settings.hasPopUpMenuFlag);
			attributes.put("tooltip", model.settings.hasToolTipFlag);
			attributes.put("tooltip-displays-content", model.settings.toolTipDisplaysContentFlag);
			attributes.put("focus-on-hover", model.settings.focusOnHoverFlag);
			attributes.put("focus", model.settings.focus);
			attributes.put("xmoveto", model.settings.xMoveTo);
			attributes.put("ymoveto", model.settings.yMoveTo);
			attributes.put("xshift", model.settings.xShift);
			attributes.put("yshift", model.settings.yShift);
			ModelToDocumentTransformer.setAttributes(topElement, attributes);
			document.appendChild(topElement);

			// tree
			final Element treeElement = document.createElement("tree");
			attributes.put("backcolor", model.settings.backColor);
			attributes.put("forecolor", model.settings.foreColor);
			attributes.put("orientation", model.settings.orientation);
			attributes.put("expansion", model.settings.expansion);
			attributes.put("sweep", model.settings.sweep);
			attributes.put("preserve-orientation", model.settings.preserveOrientationFlag);
			attributes.put("fontface", model.settings.fontFace);
			attributes.put("fontsize", model.settings.fontSize);
			attributes.put("scalefonts", model.settings.downscaleFontsFlag);
			attributes.put("fontscaler", model.settings.fontDownscaler);
			attributes.put("scaleimages", model.settings.downscaleImagesFlag);
			attributes.put("imagescaler", model.settings.imageDownscaler);
			ModelToDocumentTransformer.setAttributes(treeElement, attributes);
			topElement.appendChild(treeElement);
			// image
			if (model.settings.backgroundImageFile != null)
			{
				treeElement.appendChild(ModelToDocumentTransformer.makeImage(document, model.settings.backgroundImageFile));
			}

			// nodes
			final Element nodesElement = document.createElement("nodes");
			attributes.put("backcolor", model.settings.nodeBackColor);
			attributes.put("forecolor", model.settings.nodeForeColor);
			attributes.put("border", model.settings.borderFlag);
			attributes.put("ellipsize", model.settings.ellipsizeFlag);
			attributes.put("max-lines", model.settings.labelMaxLines);
			attributes.put("extra-line-factor", model.settings.labelExtraLineFactor);
			ModelToDocumentTransformer.setAttributes(nodesElement, attributes);
			treeElement.appendChild(nodesElement);

			if (model.settings.defaultNodeImage != null)
			{
				nodesElement.appendChild(ModelToDocumentTransformer.makeImage(document, model.settings.defaultNodeImage));
			}

			// default.treeedge
			if (model.settings.defaultTreeEdgeImage != null || model.settings.treeEdgeColor != null || model.settings.treeEdgeStyle != null)
			{
				final Element defaultTreeEdgeElement = document.createElement("default.treeedge");
				// color, stroke,toterminator,fromterminator,hidden
				if (model.settings.treeEdgeColor != null)
				{
					defaultTreeEdgeElement.setAttribute("color", Utils.colorToString(model.settings.treeEdgeColor));
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
			if (edges != null && !edges.isEmpty() || model.settings.edgesAsArcsFlag != null)
			{
				final Element edgesElement = document.createElement("edges");
				treeElement.appendChild(edgesElement);

				// as arcs
				attributes.put("arcs", model.settings.edgesAsArcsFlag);
				ModelToDocumentTransformer.setAttributes(edgesElement, attributes);

				// default edge
				if (model.settings.defaultEdgeImage != null || model.settings.edgeColor != null || model.settings.edgeStyle != null)
				{
					final Element defaultEdgeElement = document.createElement("default.edge");
					// attributes
					// color, stroke,toterminator,fromterminator,hidden
					if (model.settings.edgeColor != null)
					{
						defaultEdgeElement.setAttribute("color", Utils.colorToString(model.settings.edgeColor));
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
					for (@NonNull final IEdge edge : edges)
					{
						if (edge.getFrom() != null && edge.getTo() != null)
						{
							edgesElement.appendChild(ModelToDocumentTransformer.makeEdge(document, edge));
						}
					}
				}
			}

			// menu item iteration
			if (model.settings.menu != null && !model.settings.menu.isEmpty())
			{
				// menu
				final Element toolsElement = document.createElement("tools");
				topElement.appendChild(toolsElement);
				final Element menuElement = document.createElement("menu");
				toolsElement.appendChild(menuElement);

				for (@NonNull final MenuItem menuItem : model.settings.menu)
				{
					@NonNull final Element menuItemElement = ModelToDocumentTransformer.makeMenuItem(document, menuItem);
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
	 * @param document document
	 * @param node0    node
	 * @return node element
	 */
	@NonNull
	static private Element makeNode(@NonNull final Document document, @NonNull final INode node0)
	{
		@Nullable INode node = node0;

		// mounted node : substitute mounting node
		@Nullable MountPoint mountPoint = node.getMountPoint();
		if (mountPoint instanceof MountPoint.Mounted)
		{
			@NonNull final MountPoint.Mounted mountedPoint = (MountPoint.Mounted) mountPoint;
			node = mountedPoint.mountingNode;
			assert node != null;
			mountPoint = node.getMountPoint();
		}

		final Element nodeElement = document.createElement("node");

		// attributes
		// id, backcolor, forecolor
		nodeElement.setAttribute("id", node.getId());
		@Nullable Integer color = node.getBackColor();
		if (color != null)
		{
			nodeElement.setAttribute("backcolor", Utils.colorToString(color));
		}
		color = node.getForeColor();
		if (color != null)
		{
			nodeElement.setAttribute("forecolor", Utils.colorToString(color));
		}
		final double weight = node.getWeight();
		if (weight < 0.)
		{
			nodeElement.setAttribute("weight", Double.toString(-weight));
		}

		// label
		@Nullable String string;
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
		@Nullable final String edgeLabel = node.getEdgeLabel();
		@Nullable final String edgeImage = node.getEdgeImageFile();
		@Nullable final Integer edgeColor = node.getEdgeColor();
		@Nullable final Integer edgeStyle = node.getEdgeStyle();
		if (edgeLabel != null || edgeImage != null || edgeColor != null || edgeStyle != null)
		{
			final Element treeEdgeElement = document.createElement("treeedge");
			// attributes
			// color, stroke,toterminator,fromterminator,hidden
			if (edgeColor != null)
			{
				treeEdgeElement.setAttribute("color", Utils.colorToString(edgeColor));
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
			@NonNull final MountPoint.Mounting mountingPoint = (MountPoint.Mounting) mountPoint;
			nodeElement.appendChild(ModelToDocumentTransformer.makeMountPoint(document, mountingPoint));
		}

		// recurse
		@Nullable List<INode> children = node.getChildren();
		if (children != null)
		{
			for (final INode child : children)
			{
				nodeElement.appendChild(ModelToDocumentTransformer.makeNode(document, child));
			}
		}

		return nodeElement;
	}

	/**
	 * Make edge element
	 *
	 * @return edge element
	 */
	@NonNull
	static private Element makeEdge(@NonNull final Document document, @NonNull final IEdge edge)
	{
		final Element edgeElement = document.createElement("edge");

		// attributes
		// from,to,color,stroke,toterminator,fromterminator,hidden
		edgeElement.setAttribute("from", edge.getFrom().getId());
		edgeElement.setAttribute("to", edge.getTo().getId());
		@Nullable final Integer style = edge.getStyle();
		if (style != null)
		{
			ModelToDocumentTransformer.setStyleAttributes(edgeElement, style);
		}
		@Nullable final Integer color = edge.getColor();
		if (color != null)
		{
			edgeElement.setAttribute("color", Utils.colorToString(color));
		}

		// label
		@Nullable final String label = edge.getLabel();
		if (label != null && !label.isEmpty())
		{
			edgeElement.appendChild(ModelToDocumentTransformer.makeLabel(document, label));
		}

		// img
		@Nullable final String image = edge.getImageFile();
		if (image != null)
		{
			edgeElement.appendChild(ModelToDocumentTransformer.makeImage(document, image));
		}

		return edgeElement;
	}

	/**
	 * Make label element
	 *
	 * @param document document
	 * @param string   label
	 * @return label element
	 */
	@NonNull
	static private Element makeLabel(@NonNull final Document document, final String string)
	{
		final Element element = document.createElement("label");
		final Text text = document.createTextNode(string);
		element.appendChild(text);
		return element;
	}

	/**
	 * Make content element
	 *
	 * @param document document
	 * @param string   content
	 * @return content element
	 */
	@NonNull
	static private Element makeContent(@NonNull final Document document, final String string)
	{
		final Element contentElement = document.createElement("content");
		final CDATASection contentCData = document.createCDATASection(string);
		contentElement.appendChild(contentCData);
		return contentElement;
	}

	/**
	 * Make image element
	 *
	 * @param document document
	 * @param imageSrc image source
	 * @return image element
	 */
	@NonNull
	static public Element makeImage(@NonNull final Document document, final String imageSrc)
	{
		final Element element = document.createElement("img");
		element.setAttribute("src", imageSrc);
		return element;
	}

	/**
	 * Make link element
	 *
	 * @param document document
	 * @param href     link href
	 * @return link element
	 */
	@NonNull
	static private Element makeLink(@NonNull final Document document, final String href, @Nullable final String target)
	{
		final Element element = document.createElement("a");
		element.setAttribute("href", href);
		if (target != null && !target.isEmpty())
		{
			element.setAttribute("target", target);
		}
		return element;
	}

	/**
	 * Make mountpoint element
	 *
	 * @param document   document
	 * @param mountPoint mountpoint
	 * @return mountpoint element
	 */
	@NonNull
	static private Element makeMountPoint(@NonNull final Document document, @NonNull final MountPoint.Mounting mountPoint)
	{
		final Element mountPointElement = document.createElement("mountpoint");

		// attributes
		if (mountPoint.now != null)
		{
			mountPointElement.setAttribute("now", mountPoint.now.toString());
		}

		// a
		final Element aElement = document.createElement("a");
		aElement.setAttribute("href", mountPoint.url);
		mountPointElement.appendChild(aElement);
		return mountPointElement;
	}

	/**
	 * Make menuitem element
	 *
	 * @param document document
	 * @param menuItem menuitem
	 * @return menuitem
	 */
	@NonNull
	static private Element makeMenuItem(@NonNull final Document document, @NonNull final MenuItem menuItem)
	{
		final Element menuItemElement = document.createElement("menuitem");

		// attributes
		// action (goto|search|focus), match-target, match-scope, match-mode (equals|startswith|includes)
		@NonNull final String[] strings = Utils.toStrings(menuItem);
		if (strings[0] != null)
		{
			menuItemElement.setAttribute("action", strings[0]);
		}
		if (strings[1] != null)
		{
			menuItemElement.setAttribute("match-scope", strings[1]);
		}
		if (strings[2] != null)
		{
			menuItemElement.setAttribute("match-mode", strings[2]);
		}
		if (menuItem.matchTarget != null && !menuItem.matchTarget.isEmpty())
		{
			menuItemElement.setAttribute("match-target", menuItem.matchTarget);
		}

		// label
		final Element menuItemLabelElement = document.createElement("label");
		menuItemLabelElement.appendChild(document.createTextNode(menuItem.label));
		menuItemElement.appendChild(menuItemLabelElement);

		// a
		if (menuItem.link != null && !menuItem.link.isEmpty())
		{
			final Element aElement = document.createElement("a");
			aElement.setAttribute("href", menuItem.link);
			menuItemElement.appendChild(aElement);
		}

		return menuItemElement;
	}

	/**
	 * Set style attributes
	 *
	 * @param element element
	 * @param style   style
	 */
	static private void setStyleAttributes(@NonNull final Element element, @Nullable final Integer style)
	{
		if (style != null)
		{
			@Nullable String string = Utils.toString(style, Utils.StyleComponent.STROKE);
			if (string != null && !string.isEmpty())
			{
				element.setAttribute("stroke", string);
			}
			@Nullable String string2 = Utils.toString(style, Utils.StyleComponent.STROKEWIDTH);
			if (string2 != null && !string2.isEmpty())
			{
				@NonNull StringBuilder builder = new StringBuilder();
				if (string != null && !string.isEmpty())
				{
					builder.append(string);
					builder.append(' ');
				}
				builder.append(string2);
				element.setAttribute("stroke", builder.toString());
			}
			string = Utils.toString(style, Utils.StyleComponent.FROMTERMINATOR);
			if (string != null && !string.isEmpty())
			{
				element.setAttribute("fromterminator", string);
			}
			string = Utils.toString(style, Utils.StyleComponent.TOTERMINATOR);
			if (string != null && !string.isEmpty())
			{
				element.setAttribute("toterminator", string);
			}
			if ((style & IEdge.LINE) != 0)
			{
				element.setAttribute("line", "true");
			}
			if ((style & IEdge.HIDDEN) != 0)
			{
				element.setAttribute("hidden", "true");
			}
		}
	}

	/**
	 * Set attributes
	 *
	 * @param element    element
	 * @param attributes attributes
	 */
	static private void setAttributes(@NonNull final Element element, @NonNull final Map<String, Object> attributes)
	{
		for (final String key : attributes.keySet())
		{
			final Object value = attributes.get(key);
			if (value != null)
			{
				String string;
				//TODO color
				if (value instanceof Integer)
				{
					string = Utils.colorToString((Integer) value);
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
