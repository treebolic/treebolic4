/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.xml.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.*;

/**
 * Document adapter to model/graph
 *
 * @author Bernard Bou
 */
@SuppressWarnings("WeakerAccess")
public class ModelFactory
{
	// D A T A

	/**
	 * Protracted mount tasks
	 */
	@Nullable
	List<MountTask> mountTasks = null;

	/**
	 * Id to node map
	 */
	private Map<String, MutableNode> idToNodeMap;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public ModelFactory()
	{
	}

	// A C C E S S

	/**
	 * Get id to node map
	 *
	 * @return id to node map
	 */
	public Map<String, MutableNode> getIdToNodeMap()
	{
		return this.idToNodeMap;
	}

	// M A K E

	/**
	 * Make model from document
	 *
	 * @param document document
	 * @return model
	 */
	@Nullable
	public Model makeModel(@NonNull final Document document)
	{
		this.idToNodeMap = new TreeMap<>();
		@Nullable final Tree tree = toTree(document);
		if (tree == null)
		{
			return null;
		}
		@NonNull final Settings settings = ModelFactory.toSettings(document);
		return new Model(tree, settings, null);
	}

	/**
	 * Make tree from document
	 *
	 * @param document document
	 * @return tree
	 */
	@Nullable
	public Tree makeTree(@NonNull final Document document)
	{
		this.idToNodeMap = new Hashtable<>();
		return toTree(document);
	}

	// P A R S E

	/**
	 * Make model node
	 *
	 * @param parent model parent
	 * @param id     id
	 * @return model node
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	protected MutableNode makeNode(final MutableNode parent, final String id)
	{
		return new MutableNode(parent, id);
	}

	/**
	 * Make model edge
	 *
	 * @param fromNode model from-node end
	 * @param toNode   model to-node end
	 * @return model edge
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	protected MutableEdge makeEdge(final MutableNode fromNode, final MutableNode toNode)
	{
		return new MutableEdge(fromNode, toNode);
	}

	/**
	 * Make graph
	 *
	 * @param document document
	 * @return graph
	 */
	@Nullable
	protected Tree toTree(@NonNull final Document document)
	{
		// nodes
		@Nullable final Element rootElement = ModelFactory.getFirstElementByTagName(document.getDocumentElement(), "node");
		if (rootElement == null)
		{
			return null;
		}
		@NonNull final MutableNode root = toNode(rootElement, null);

		// edges
		@Nullable final List<IEdge> edges = toEdges(document);

		return new Tree(root, edges, mountTasks);
	}

	/**
	 * Make model node
	 *
	 * @param nodeElement starting DOM element
	 * @param parent      model parent node
	 * @return model node
	 */
	@NonNull
	MutableNode toNode(@NonNull final Element nodeElement, final MutableNode parent)
	{
		// id
		@NonNull final String id = nodeElement.getAttribute("id");

		// make
		@NonNull final MutableNode node = makeNode(parent, id);
		this.idToNodeMap.put(id, node);

		// colors
		@Nullable final Integer backColor = Utils.parseColor(nodeElement.getAttribute("backcolor"));
		node.setBackColor(backColor);
		@Nullable final Integer foreColor = Utils.parseColor(nodeElement.getAttribute("forecolor"));
		node.setForeColor(foreColor);

		// weight
		@NonNull final String weight = nodeElement.getAttribute("weight");
		if (!weight.isEmpty())
		{
			node.setWeight(-Double.parseDouble(weight));
		}

		// label
		@Nullable Element element = ModelFactory.getFirstLevel1ElementByTagName(nodeElement, "label");
		if (element != null)
		{
			final String label = element.getTextContent();
			if (label != null && !label.isEmpty())
			{
				node.setLabel(label);
			}
		}

		// image
		@Nullable String imageSrc;
		element = ModelFactory.getFirstLevel1ElementByTagName(nodeElement, "img");
		if (element != null)
		{
			imageSrc = element.getAttribute("src");
			if (!imageSrc.isEmpty())
			{
				node.setImageFile(imageSrc);
			}
		}

		// content
		element = ModelFactory.getFirstLevel1ElementByTagName(nodeElement, "content");
		if (element != null)
		{
			final String content = element.getTextContent();
			if (content != null && !content.isEmpty())
			{
				node.setContent(content);
			}
		}

		// tree.edge
		element = ModelFactory.getFirstLevel1ElementByTagName(nodeElement, "treeedge");
		if (element != null)
		{
			// label
			@Nullable final Element labelElement = ModelFactory.getFirstLevel1ElementByTagName(element, "label");
			if (labelElement != null)
			{
				final String label = labelElement.getTextContent();
				if (label != null && !label.isEmpty())
				{
					node.setEdgeLabel(label);
				}
			}

			// image
			@Nullable final Element imageElement = ModelFactory.getFirstLevel1ElementByTagName(element, "img");
			if (imageElement != null)
			{
				@NonNull final String edgeImageSrc = imageElement.getAttribute("src");
				if (!edgeImageSrc.isEmpty())
				{
					node.setEdgeImageFile(edgeImageSrc);
				}
			}

			// color
			@Nullable final Integer color = Utils.parseColor(element.getAttribute("color"));
			if (color != null)
			{
				node.setEdgeColor(color);
			}

			// style
			@Nullable final Integer style = Utils.parseStyle(element.getAttribute("stroke"), element.getAttribute("fromterminator"), element.getAttribute("toterminator"), element.getAttribute("line"), element.getAttribute("hidden"));
			if (style != null)
			{
				node.setEdgeStyle(style);
			}
		}

		// link
		element = ModelFactory.getFirstLevel1ElementByTagName(nodeElement, "a");
		if (element != null)
		{
			@NonNull final String href = element.getAttribute("href");
			if (!href.isEmpty())
			{
				node.setLink(href);
			}
		}

		// mount
		element = ModelFactory.getFirstLevel1ElementByTagName(nodeElement, "mountpoint");
		if (element != null)
		{
			@Nullable final Element aElement = ModelFactory.getFirstElementByTagName(element, "a");
			if (aElement != null)
			{
				@NonNull final String href = aElement.getAttribute("href");
				if (!href.isEmpty())
				{
					@NonNull final MountPoint.Mounting mountPoint = new MountPoint.Mounting();
					mountPoint.url = href;

					// mount now ?
					@NonNull final String value = element.getAttribute("now");
					if (!value.isEmpty() && Boolean.parseBoolean(value))
					{
						mountPoint.now = true;

						@NonNull final MountTask task = new MountTask(mountPoint, node);
						if (this.mountTasks == null)
						{
							this.mountTasks = new ArrayList<>();
						}
						this.mountTasks.add(task);
					}
					node.setMountPoint(mountPoint);
				}
			}
		}

		// recurse to children
		@NonNull final List<Element> childElements = ModelFactory.getLevel1ChildElementsByTagName(nodeElement, "node");
		for (@NonNull final Element childElement : childElements)
		{
			toNode(childElement, node);
		}

		return node;
	}

	/**
	 * Make model edge
	 *
	 * @param edgeElement edge DOM element
	 * @return edge
	 */
	@NonNull
	private MutableEdge toEdge(@NonNull final Element edgeElement)
	{
		@NonNull final String fromId = edgeElement.getAttribute("from");
		@NonNull final String toId = edgeElement.getAttribute("to");
		final MutableNode fromNode = this.idToNodeMap.get(fromId);
		final MutableNode toNode = this.idToNodeMap.get(toId);
		@NonNull final MutableEdge edge = makeEdge(fromNode, toNode);

		// label
		@Nullable final Element labelElement = ModelFactory.getFirstLevel1ElementByTagName(edgeElement, "label");
		if (labelElement != null)
		{
			final String label = labelElement.getTextContent();
			if (label != null && !label.isEmpty())
			{
				edge.setLabel(label);
			}
		}

		// image
		@Nullable final Element imageElement = ModelFactory.getFirstLevel1ElementByTagName(edgeElement, "img");
		if (imageElement != null)
		{
			@NonNull final String imageSrc = imageElement.getAttribute("src");
			if (!imageSrc.isEmpty())
			{
				edge.setImageFile(imageSrc);
			}
		}

		// color
		@Nullable final Integer color = Utils.parseColor(edgeElement.getAttribute("color"));
		if (color != null)
		{
			edge.setColor(color);
		}

		// style
		@Nullable final Integer style = Utils.parseStyle(edgeElement.getAttribute("stroke"), edgeElement.getAttribute("fromterminator"), edgeElement.getAttribute("toterminator"), edgeElement.getAttribute("line"), edgeElement.getAttribute("hidden"));
		if (style != null)
		{
			edge.setStyle(style);
		}
		return edge;
	}

	/**
	 * Make list of model edges
	 *
	 * @param document DOM document
	 * @return list of edges
	 */
	@Nullable
	List<IEdge> toEdges(@NonNull final Document document)
	{
		@Nullable List<IEdge> edgeList = null;
		final NodeList children = document.getElementsByTagName("edge");
		for (int i = 0; i < children.getLength(); i++)
		{
			final Node node = children.item(i);
			final Element edgeElement = (Element) node;
			@NonNull final MutableEdge edge = toEdge(edgeElement);
			if (edgeList == null)
			{
				edgeList = new ArrayList<>();
			}
			edgeList.add(edge);
		}
		return edgeList;
	}

	/**
	 * Make settings
	 *
	 * @param document DOM document
	 * @return settings
	 */
	@NonNull
	static private Settings toSettings(@NonNull final Document document)
	{
		@NonNull final Settings settings = new Settings();

		// T O P
		@Nullable Element element = document.getDocumentElement();
		if (element != null && element.getNodeName().equals("treebolic"))
		{
			@NonNull String attribute = element.getAttribute("toolbar");
			if (!attribute.isEmpty())
			{
				settings.hasToolbarFlag = Boolean.valueOf(attribute);
			}
			attribute = element.getAttribute("statusbar");
			if (!attribute.isEmpty())
			{
				settings.hasStatusbarFlag = Boolean.valueOf(attribute);
			}
			attribute = element.getAttribute("popupmenu");
			if (!attribute.isEmpty())
			{
				settings.hasPopUpMenuFlag = Boolean.valueOf(attribute);
			}
			attribute = element.getAttribute("content-format");
			if (!attribute.isEmpty())
			{
				settings.contentFormat = attribute;
			}
			attribute = element.getAttribute("tooltip");
			if (!attribute.isEmpty())
			{
				settings.hasToolTipFlag = Boolean.valueOf(attribute);
			}
			attribute = element.getAttribute("tooltip-displays-content");
			if (!attribute.isEmpty())
			{
				settings.toolTipDisplaysContentFlag = Boolean.valueOf(attribute);
			}
			attribute = element.getAttribute("focus");
			if (!attribute.isEmpty())
			{
				settings.focus = attribute;
			}
			attribute = element.getAttribute("focus-on-hover");
			if (!attribute.isEmpty())
			{
				settings.focusOnHoverFlag = Boolean.valueOf(attribute);
			}
			attribute = element.getAttribute("xmoveto");
			if (!attribute.isEmpty())
			{
				settings.xMoveTo = Float.valueOf(attribute);
			}
			attribute = element.getAttribute("ymoveto");
			if (!attribute.isEmpty())
			{
				settings.yMoveTo = Float.valueOf(attribute);
			}
			attribute = element.getAttribute("xshift");
			if (!attribute.isEmpty())
			{
				settings.xShift = Float.valueOf(attribute);
			}
			attribute = element.getAttribute("yshift");
			if (!attribute.isEmpty())
			{
				settings.yShift = Float.valueOf(attribute);
			}
		}

		// T R E E
		element = ModelFactory.getFirstElementByTagName(document.getDocumentElement(), "tree");
		if (element != null)
		{
			// img
			@Nullable final Element imageElement = ModelFactory.getFirstLevel1ElementByTagName(element, "img");
			if (imageElement != null)
			{
				@NonNull final String src = imageElement.getAttribute("src");
				if (!src.isEmpty())
				{
					settings.backgroundImageFile = src;
				}
			}

			// colors
			@Nullable Integer color = Utils.parseColor(element.getAttribute("backcolor"));
			if (color != null)
			{
				settings.backColor = color;
			}
			color = Utils.parseColor(element.getAttribute("forecolor"));
			if (color != null)
			{
				settings.foreColor = color;
			}

			// attributes
			String attribute;
			attribute = element.getAttribute("orientation");
			if (!attribute.isEmpty())
			{
				settings.orientation = attribute;
			}
			attribute = element.getAttribute("expansion");
			if (!attribute.isEmpty())
			{
				settings.expansion = Float.valueOf(attribute);
			}
			attribute = element.getAttribute("sweep");
			if (!attribute.isEmpty())
			{
				settings.sweep = Float.valueOf(attribute);
			}
			attribute = element.getAttribute("preserve-orientation");
			if (!attribute.isEmpty())
			{
				settings.preserveOrientationFlag = Boolean.valueOf(attribute);
			}
			attribute = element.getAttribute("fontface");
			if (!attribute.isEmpty())
			{
				settings.fontFace = attribute;
			}
			attribute = element.getAttribute("fontsize");
			if (!attribute.isEmpty())
			{
				settings.fontSize = Integer.valueOf(attribute);
			}
			attribute = element.getAttribute("scalefonts");
			if (!attribute.isEmpty())
			{
				settings.downscaleFontsFlag = Boolean.valueOf(attribute);
			}
			attribute = element.getAttribute("fontscaler");
			if (!attribute.isEmpty())
			{
				settings.fontDownscaler = Utils.stringToFloats(attribute);
			}
			attribute = element.getAttribute("scaleimages");
			if (!attribute.isEmpty())
			{
				settings.downscaleImagesFlag = Boolean.valueOf(attribute);
			}
			attribute = element.getAttribute("imagescaler");
			if (!attribute.isEmpty())
			{
				settings.imageDownscaler = Utils.stringToFloats(attribute);
			}
		}

		// N O D E S
		element = ModelFactory.getFirstElementByTagName(document.getDocumentElement(), "nodes");
		if (element != null)
		{
			// img
			@Nullable final Element imageElement = ModelFactory.getFirstLevel1ElementByTagName(element, "img");
			if (imageElement != null)
			{
				@NonNull final String src = imageElement.getAttribute("src");
				if (!src.isEmpty())
				{
					settings.defaultNodeImage = src;
				}
			}

			// colors
			@Nullable Integer color = Utils.parseColor(element.getAttribute("backcolor"));
			if (color != null)
			{
				settings.nodeBackColor = color;
			}
			color = Utils.parseColor(element.getAttribute("forecolor"));
			if (color != null)
			{
				settings.nodeForeColor = color;
			}

			// label
			@NonNull String attribute = element.getAttribute("border");
			if (!attribute.isEmpty())
			{
				settings.borderFlag = Boolean.valueOf(attribute);
			}

			attribute = element.getAttribute("ellipsize");
			if (!attribute.isEmpty())
			{
				settings.ellipsizeFlag = Boolean.valueOf(attribute);
			}
		}

		// E D G E S
		element = ModelFactory.getFirstElementByTagName(document.getDocumentElement(), "edges");
		if (element != null)
		{
			// img
			@Nullable final Element imageElement = ModelFactory.getFirstLevel1ElementByTagName(element, "img");
			if (imageElement != null)
			{
				@NonNull final String src = imageElement.getAttribute("src");
				if (!src.isEmpty())
				{
					settings.defaultEdgeImage = src;
				}
			}

			// arc
			@NonNull final String attribute = element.getAttribute("arc");
			if (!attribute.isEmpty())
			{
				settings.edgesAsArcsFlag = Boolean.valueOf(attribute);
			}
		}

		// D E F A U L T . T R E E . E D G E
		element = ModelFactory.getFirstElementByTagName(document.getDocumentElement(), "default.treeedge");
		if (element != null)
		{
			// img
			@Nullable final Element imageElement = ModelFactory.getFirstElementByTagName(element, "img");
			if (imageElement != null)
			{
				@NonNull final String src = imageElement.getAttribute("src");
				if (!src.isEmpty())
				{
					settings.defaultTreeEdgeImage = src;
				}
			}

			// color
			@Nullable final Integer color = Utils.parseColor(element.getAttribute("color"));
			if (color != null)
			{
				settings.treeEdgeColor = color;
			}

			// style
			@Nullable final Integer style = Utils.parseStyle(element.getAttribute("stroke"), element.getAttribute("fromterminator"), element.getAttribute("toterminator"), element.getAttribute("line"), element.getAttribute("hidden"));
			if (style != null)
			{
				settings.treeEdgeStyle = style;
			}
		}

		// D E F A U L T . E D G E
		element = ModelFactory.getFirstElementByTagName(document.getDocumentElement(), "default.edge");
		if (element != null)
		{
			// img
			@Nullable final Element imageElement = ModelFactory.getFirstElementByTagName(element, "img");
			if (imageElement != null)
			{
				@NonNull final String src = imageElement.getAttribute("src");
				if (!src.isEmpty())
				{
					settings.defaultEdgeImage = src;
				}
			}

			// color
			@Nullable final Integer color = Utils.parseColor(element.getAttribute("color"));
			if (color != null)
			{
				settings.edgeColor = color;
			}

			// style
			@Nullable final Integer style = Utils.parseStyle(element.getAttribute("stroke"), element.getAttribute("fromterminator"), element.getAttribute("toterminator"), element.getAttribute("line"), element.getAttribute("hidden"));
			if (style != null)
			{
				settings.edgeStyle = style;
			}
		}

		// M E N U
		@Nullable List<MenuItem> menuItemList = null;
		final NodeList children = document.getElementsByTagName("menuitem");
		for (int i = 0; i < children.getLength(); i++)
		{
			final Node node = children.item(i);
			element = (Element) node;
			@NonNull final MenuItem menuItem = ModelFactory.toMenuItem(element);

			if (menuItemList == null)
			{
				menuItemList = new ArrayList<>();
			}
			menuItemList.add(menuItem);
		}
		settings.menu = menuItemList;

		return settings;
	}

	/**
	 * Make menu item
	 *
	 * @param element menu item DOM element
	 * @return menu item
	 */
	@NonNull
	static private MenuItem toMenuItem(@NonNull final Element element)
	{
		@NonNull final MenuItem menuItem = new MenuItem();
		Utils.parseMenuItem(menuItem, element.getAttribute("action"), element.getAttribute("match-scope"), element.getAttribute("match-mode"));

		// match target
		menuItem.matchTarget = element.getAttribute("match-target");

		// label
		@Nullable final Element labelElement = ModelFactory.getFirstElementByTagName(element, "label");
		if (labelElement != null)
		{
			menuItem.label = labelElement.getTextContent();
		}

		// link
		@Nullable final Element linkElement = ModelFactory.getFirstElementByTagName(element, "a");
		if (linkElement != null)
		{
			menuItem.link = linkElement.getAttribute("href");
			menuItem.target = linkElement.getAttribute("target");
		}

		return menuItem;
	}

	// H E L P E R S

	/**
	 * Find DOM element with given tag
	 *
	 * @param element starting DOM element
	 * @param tagName tag
	 * @return DOM element if found, null if none
	 */
	@Nullable
	static Element getFirstElementByTagName(@Nullable final Element element, final String tagName)
	{
		if (element != null)
		{
			@NonNull final NodeList elements = element.getElementsByTagName(tagName);
			if (elements.getLength() > 0)
			{
				return (Element) elements.item(0);
			}
		}
		return null;
	}

	/**
	 * Find DOM element with given tag among first level children
	 *
	 * @param element starting DOM element
	 * @param tagName tag
	 * @return DOM element if found, null if none
	 */
	@Nullable
	static private Element getFirstLevel1ElementByTagName(@NonNull final Element element, final String tagName)
	{
		@NonNull final List<Element> childElements = ModelFactory.getLevel1ChildElementsByTagName(element, tagName);
		if (!childElements.isEmpty())
		{
			return childElements.get(0);
		}
		return null;
	}

	/**
	 * Find DOM elements with given tag among first level children
	 *
	 * @param element starting DOM element
	 * @param tagName tag
	 * @return DOM element if found, empty list if none
	 */
	@NonNull
	static private List<Element> getLevel1ChildElementsByTagName(@NonNull final Element element, final String tagName)
	{
		@NonNull final ArrayList<Element> elements = new ArrayList<>();
		@NonNull final NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			final Node node = children.item(i);
			if (node instanceof Element)
			{
				@NonNull final Element childElement = (Element) node;
				if (childElement.getTagName().equals(tagName))
				{
					elements.add(childElement);
				}
			}
		}
		return elements;
	}
}
