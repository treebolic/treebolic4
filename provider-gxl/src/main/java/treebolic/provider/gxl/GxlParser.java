/**
 * Title : Treebolic GXL provider
 * Description : Treebolic GXL provider
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use: see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.provider.gxl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.*;
import treebolic.provider.xml.Parser;

/**
 * GXL parser
 *
 * @author Bernard Bou
 */
public class GxlParser
{
	/**
	 * Parse model
	 * @param url url
	 * @return model if successful
	 */
	@Nullable
	static public Model parseModel(@NonNull final URL url)
	{
		// DOM document
		try
		{
			Document document = new Parser().makeDocument(url, null);
			if (document == null)
			{
				return null;
			}

			@Nullable final Tree tree = GxlParser.parseTree(document);
			if (tree == null)
			{
				return null;
			}

			@NonNull final Settings settings = GxlParser.parseSettings(document);
			return new Model(tree, settings);
		}
		catch (ParserConfigurationException | SAXException | IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Parse model
	 *
	 * @param path path to file
	 * @return model if successful
	 */
	@Nullable
	static public Model parseModel(@NonNull final String path)
	{
		// DOM document
		try
		{
			Document document = new Parser().makeDocument(new File(path).toURI().toURL(), null);
			if (document == null)
			{
				return null;
			}

			@Nullable final Tree tree = GxlParser.parseTree(document);
			if (tree == null)
			{
				return null;
			}

			@NonNull final Settings settings = GxlParser.parseSettings(document);
			return new Model(tree, settings);
		}
		catch (ParserConfigurationException | SAXException | IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Parse tree from url
	 * @param url url
	 *
	 * @return tree if successful
	 * @throws IOException                  io exception
	 * @throws SAXException                 sax exception
	 * @throws ParserConfigurationException parser configuration exception
	 */
	@Nullable
	static public Tree parseTree(@NonNull final URL url) throws ParserConfigurationException, SAXException, IOException
	{
		// DOM document
		final Document document = new Parser().makeDocument(url, null);
		if (document == null)
		{
			return null;
		}

		return GxlParser.parseTree(document);
	}

	/**
	 * Parse tree from location
	 *
	 * @param path path to file
	 * @return tree if successful
	 * @throws IOException                  io exception
	 * @throws SAXException                 sax exception
	 * @throws ParserConfigurationException parser configuration exception
	 */
	@Nullable
	static public Tree parseTree(@NonNull final String path) throws ParserConfigurationException, SAXException, IOException
	{
		// DOM document
		final Document document = new Parser().makeDocument(new File(path).toURI().toURL(), null);
		if (document == null)
		{
			return null;
		}

		return GxlParser.parseTree(document);
	}

	/**
	 * Parse tree
	 *
	 * @return tree if successful
	 */
	@Nullable
	static private Tree parseTree(@NonNull final Document document)
	{
		// map
		@NonNull final Hashtable<String, MutableNode> nodesById = new Hashtable<>();

		// enumerate nodes
		final NodeList gxlNodes = document.getElementsByTagName("node"); 
		for (int i = 0; i < gxlNodes.getLength(); i++)
		{
			final Node gxlNode = gxlNodes.item(i);
			final Element gxlNodeElement = (Element) gxlNode;

			// get id
			@NonNull final String id = gxlNodeElement.getAttribute("id"); 

			// create node
			@NonNull final MutableNode node = new MutableNode(null, id);
			nodesById.put(id, node);

			// attributes
			@NonNull final NodeList gxlAttrs = gxlNodeElement.getElementsByTagName("attr"); 
			for (int j = 0; j < gxlAttrs.getLength(); j++)
			{
				final Node gxlAttr = gxlAttrs.item(j);
				final Element gxlAttrElement = (Element) gxlAttr;
				@NonNull final String attributeName = gxlAttrElement.getAttribute("name"); 

				if (attributeName.equalsIgnoreCase("label")) 
				{
					@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
					if (element != null)
					{
						node.setLabel(element.getTextContent());
					}
				}
				else if (attributeName.equalsIgnoreCase("content")) 
				{
					@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
					if (element != null)
					{
						node.setContent(element.getTextContent());
					}
				}
				else if (attributeName.equalsIgnoreCase("link")) 
				{
					@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); 
					if (element != null)
					{
						@NonNull final String hRef = element.getAttribute("xlink:href"); 
						node.setLink(hRef);
					}
				}
				else if (attributeName.equalsIgnoreCase("img-src")) 
				{
					@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); 
					if (element != null)
					{
						@NonNull final String hRef = element.getAttribute("xlink:href"); 
						node.setImageFile(hRef);
					}
				}
				else if (attributeName.equalsIgnoreCase("mountpoint")) 
				{
					@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); 
					if (element != null)
					{
						@NonNull final String hRef = element.getAttribute("xlink:href"); 
						@NonNull final MountPoint.Mounting mountPoint = new MountPoint.Mounting();
						mountPoint.url = hRef;
						node.setMountPoint(mountPoint);
					}
				}
				else if (attributeName.equalsIgnoreCase("mountpoint-weight")) 
				{
					@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
					if (element != null)
					{
						@Nullable final MountPoint.Mounting mountPoint = (MountPoint.Mounting) node.getMountPoint();
						if (mountPoint != null)
						{
							node.setWeight(Double.parseDouble(element.getTextContent()));
						}
					}
				}
				else if (attributeName.equalsIgnoreCase("backcolor")) 
				{
					@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
					if (element != null)
					{
						node.setBackColor(Utils.stringToColor(element.getTextContent()));
					}
				}
				else if (attributeName.equalsIgnoreCase("forecolor")) 
				{
					@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
					if (element != null)
					{
						node.setForeColor(Utils.stringToColor(element.getTextContent()));
					}
				}
			}
		}

		// enumerate edges
		@Nullable List<IEdge> edges = null;
		final NodeList gxlEdges = document.getElementsByTagName("edge"); 
		for (int i = 0; i < gxlEdges.getLength(); i++)
		{
			final Node gxlEdge = gxlEdges.item(i);
			final Element gxlEdgeElement = (Element) gxlEdge;

			// get end ids
			@NonNull final String fromId = gxlEdgeElement.getAttribute("from"); 
			if (fromId.isEmpty())
			{
				continue;
			}
			@NonNull final String toId = gxlEdgeElement.getAttribute("to"); 
			if (toId.isEmpty())
			{
				continue;
			}

			// get ends
			final MutableNode fromNode = nodesById.get(fromId);
			if (fromNode == null)
			{
				continue;
			}
			final MutableNode toNode = nodesById.get(toId);
			if (toNode == null)
			{
				continue;
			}

			// type
			@Nullable final Element gxlTypeElement = GxlParser.getFirstLevel1ElementByTagName(gxlEdgeElement, "type"); 
			if (gxlTypeElement != null)
			{
				@NonNull final String type = gxlTypeElement.getAttribute("xlink:href"); 
				if (!type.isEmpty())
				{
					if (type.equals("schema.xml#TreeEdge")) 
					{
						fromNode.getChildren().add(toNode);
						toNode.setParent(fromNode);

						// attributes
						@NonNull final NodeList gxlEdgeAttrs = gxlEdgeElement.getElementsByTagName("attr"); 
						for (int j = 0; j < gxlEdgeAttrs.getLength(); j++)
						{
							final Node gxlAttr = gxlEdgeAttrs.item(j);
							final Element gxlAttrElement = (Element) gxlAttr;
							@NonNull final String attributeName = gxlAttrElement.getAttribute("name"); 
							if (attributeName.equalsIgnoreCase("label")) 
							{
								@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
								if (element != null)
								{
									toNode.setEdgeLabel(element.getTextContent());
								}
							}
							else if (attributeName.equalsIgnoreCase("img-src")) 
							{
								@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); 
								if (element != null)
								{
									@NonNull final String hRef = element.getAttribute("xlink:href"); 
									toNode.setEdgeImageFile(hRef);
								}
							}
							else if (attributeName.equalsIgnoreCase("stroke")) 
							{
								@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
								if (element != null)
								{
									@Nullable final Integer style = Utils.modifyStyle(toNode.getEdgeStyle(), element.getTextContent(), Utils.StyleComponent.STROKE);
									toNode.setEdgeStyle(style);
								}
							}
							else if (attributeName.equalsIgnoreCase("fromterminator")) 
							{
								@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
								if (element != null)
								{
									@Nullable final Integer style = Utils.modifyStyle(toNode.getEdgeStyle(), element.getTextContent(), Utils.StyleComponent.FROMTERMINATOR);
									toNode.setEdgeStyle(style);
								}
							}
							else if (attributeName.equalsIgnoreCase("toterminator")) 
							{
								@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
								if (element != null)
								{
									@Nullable final Integer style = Utils.modifyStyle(toNode.getEdgeStyle(), element.getTextContent(), Utils.StyleComponent.TOTERMINATOR);
									toNode.setEdgeStyle(style);
								}
							}
						}
					}
					else if (type.equals("schema.xml#NonTreeEdge")) 
					{
						@NonNull final MutableEdge edge = new MutableEdge(fromNode, toNode);
						if (edges == null)
						{
							edges = new ArrayList<>();
						}
						edges.add(edge);

						// attributes
						@NonNull final NodeList gxlAttrs = gxlEdgeElement.getElementsByTagName("attr"); 
						for (int j = 0; j < gxlAttrs.getLength(); j++)
						{
							final Node gxlAttr = gxlAttrs.item(j);
							final Element gxlAttrElement = (Element) gxlAttr;
							@NonNull final String attributeName = gxlAttrElement.getAttribute("name"); 
							if (attributeName.equalsIgnoreCase("label")) 
							{
								@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
								if (element != null)
								{
									edge.setLabel(element.getTextContent());
								}
							}
							else if (attributeName.equalsIgnoreCase("img-src")) 
							{
								@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); 
								if (element != null)
								{
									@NonNull final String hRef = element.getAttribute("xlink:href"); 
									edge.setImageFile(hRef);
								}
							}
							else if (attributeName.equalsIgnoreCase("color")) 
							{
								@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
								if (element != null)
								{
									edge.setColor(Utils.stringToColor(element.getTextContent()));
								}
							}
							else if (attributeName.equals("stroke")) 
							{
								@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
								if (element != null)
								{
									edge.setStyle(Utils.modifyStyle(edge.getStyle(), element.getTextContent(), Utils.StyleComponent.STROKE));
								}
							}
							else if (attributeName.equals("fromterminator")) 
							{
								@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
								if (element != null)
								{
									edge.setStyle(Utils.modifyStyle(edge.getStyle(), element.getTextContent(), Utils.StyleComponent.FROMTERMINATOR));
								}
							}
							else if (attributeName.equals("toterminator")) 
							{
								@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
								if (element != null)
								{
									edge.setStyle(Utils.modifyStyle(edge.getStyle(), element.getTextContent(), Utils.StyleComponent.TOTERMINATOR));
								}
							}
						}
					}
				}
			}
		}

		// get root : take node with 'root' id
		final INode root = nodesById.get("root"); 
		if (root == null)
		{
			return null;
		}

		// return result
		return new Tree(root, edges);
	}

	/**
	 * Parse
	 *
	 * @return graph if successful
	 */
	@NonNull
	static private Settings parseSettings(@NonNull final Document document)
	{
		@NonNull final Settings settings = new Settings();
		settings.hasToolbarFlag = true;
		settings.hasStatusbarFlag = true;

		@Nullable final Element gxlGraphElement = GxlParser.getFirstElementByTagName(document.getDocumentElement(), "graph"); 
		if (gxlGraphElement != null)
		{
			// enumerate attrs
			@NonNull final List<Element> gxlAttrElements = GxlParser.getLevel1ChildElementsByTagName(gxlGraphElement, "attr"); 
			for (@NonNull final Element gxlAttrElement : gxlAttrElements)
			{
				@NonNull final String name = gxlAttrElement.getAttribute("name"); 

				// locator
				@Nullable final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); 
				if (element != null)
				{
					@NonNull final String value = element.getAttribute("xlink:href"); 
					switch (name)
					{
						case "nodes-img-src":
							settings.defaultNodeImage = value;
							break;
						case "tree-img-src":
							settings.backgroundImageFile = value;
							break;
						case "nodes-default-treeedge-img-src":
							settings.defaultTreeEdgeImage = value;
							break;
						case "edges-default-edge-img-src":
							settings.defaultEdgeImage = value;
							break;
					}
				}

				// string
				@Nullable final Element element2 = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); 
				if (element2 != null)
				{
					final String value = element2.getTextContent();
					switch (name)
					{
						case "treebolic-toolbar":
							settings.hasToolbarFlag = Boolean.valueOf(value);
							break;
						case "treebolic-statusbar":
							settings.hasStatusbarFlag = Boolean.valueOf(value);
							break;
						case "treebolic-popupmenu":
							settings.hasPopUpMenuFlag = Boolean.valueOf(value);
							break;
						case "treebolic-tooltip":
							settings.hasToolTipFlag = Boolean.valueOf(value);
							break;
						case "treebolic-focus-on-hover":
							settings.focusOnHoverFlag = Boolean.valueOf(value);
							break;
						case "tree-backcolor":
							settings.backColor = Utils.stringToColor(value);
							break;
						case "tree-forecolor":
							settings.foreColor = Utils.stringToColor(value);
							break;
						case "tree-fontface":
							settings.fontFace = value;
							break;
						case "tree-fontsize":
							settings.fontSize = Integer.valueOf(value);
							break;
						case "tree-scalefonts":
							settings.downscaleFontsFlag = Boolean.valueOf(value);
							break;
						case "tree-fontscaler":
							settings.fontDownscaler = Utils.stringToFloats(value);
							break;
						case "tree-scaleimages":
							settings.downscaleImagesFlag = Boolean.valueOf(value);
							break;
						case "tree-imagescaler":
							settings.imageDownscaler = Utils.stringToFloats(value);
							break;
						case "tree-expansion":
							settings.expansion = Float.valueOf(value);
							break;
						case "tree-sweep":
							settings.sweep = Float.valueOf(value);
							break;
						case "tree-orientation":
							settings.orientation = value;
							break;
						case "tree-preserve-orientation":
							settings.preserveOrientationFlag = Boolean.valueOf(value);
							break;
						case "nodes-backcolor":
							settings.nodeBackColor = Utils.stringToColor(value);
							break;
						case "nodes-forecolor":
							settings.nodeForeColor = Utils.stringToColor(value);
							break;
						case "nodes-default-treeedge-color":
							settings.treeEdgeColor = Utils.stringToColor(value);
							break;
						case "nodes-default-treeedge-stroke":
							settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, value, Utils.StyleComponent.STROKE);
							break;
						case "nodes-default-treeedge-fromterminator":
							settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, value, Utils.StyleComponent.FROMTERMINATOR);
							break;
						case "nodes-default-treeedge-toterminator":
							settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, value, Utils.StyleComponent.TOTERMINATOR);
							break;
						case "nodes-border":
							settings.borderFlag = Boolean.valueOf(value);
							break;
						case "nodes-ellipsize":
							settings.ellipsizeFlag = Boolean.valueOf(value);
							break;
						case "nodes-label-max-lines":
							settings.labelMaxLines = Integer.valueOf(value);
							break;
						case "nodes-label-extra-line-factor":
							settings.labelExtraLineFactor = Float.valueOf(value);
							break;
						case "edges-arcs":
							settings.edgesAsArcsFlag = Boolean.valueOf(value);
							break;
						case "edges-default-edge-color":
							settings.edgeColor = Utils.stringToColor(value);
							break;
						case "edges-default-edge-fromterminator":
							settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, value, Utils.StyleComponent.FROMTERMINATOR);
							break;
						case "edges-default-edge-toterminator":
							settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, value, Utils.StyleComponent.TOTERMINATOR);
							break;
						case "edges-default-edge-stroke":
							settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, value, Utils.StyleComponent.STROKE);
							break;
						default:
							System.err.println("gxl: Unhandled attribute: " + name);
							break;
					}
				}

				// tuples
				@Nullable final Element tupElement = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "tup"); 
				if (tupElement != null)
				{
					@NonNull final List<Element> gxlTupleElements = GxlParser.getLevel1ChildElementsByTagName(tupElement, "string"); 
					int k = 0;
					for (@Nullable final Element gxlTupleElement : gxlTupleElements)
					{
						if (gxlTupleElement != null)
						{
							final String value = gxlTupleElement.getTextContent();
							if (value == null)
							{
								continue;
							}

							switch (name)
							{
								case "menuitem-action":
								{
									settings.menu = GxlParser.allocate(settings.menu, k);
									final MenuItem menuItem = settings.menu.get(k);
									menuItem.action = Utils.stringToAction(value);
									break;
								}
								case "menuitem-label":
								{
									settings.menu = GxlParser.allocate(settings.menu, k);
									final MenuItem menuItem = settings.menu.get(k);
									menuItem.label = value;
									break;
								}
								case "menuitem-match-target":
								{
									settings.menu = GxlParser.allocate(settings.menu, k);
									final MenuItem menuItem = settings.menu.get(k);
									menuItem.matchTarget = value;
									break;
								}
								case "menuitem-match-scope":
								{
									settings.menu = GxlParser.allocate(settings.menu, k);
									final MenuItem menuItem = settings.menu.get(k);
									menuItem.matchScope = Utils.stringToScope(value);
									break;
								}
								case "menuitem-match-mode":
								{
									settings.menu = GxlParser.allocate(settings.menu, k);
									final MenuItem menuItem = settings.menu.get(k);
									menuItem.matchMode = Utils.stringToMode(value);
									break;
								}
							}
							k++;
						}
					}
				}
			}
		}
		return settings;
	}

	/**
	 * Allocate
	 *
	 * @param list0 menu item list
	 * @param i     index
	 * @return same list with ith slot having an allocated menu item
	 */
	@NonNull
	static private List<MenuItem> allocate(final List<MenuItem> list0, final int i)
	{
		List<MenuItem> list = list0;
		if (list == null)
		{
			list = new ArrayList<>();
		}
		if (i > list.size() - 1)
		{
			list.add(new MenuItem());
		}
		return list;
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
	static private Element getFirstElementByTagName(@NonNull final Element element, @SuppressWarnings("SameParameterValue") final String tagName)
	{
		@NonNull final NodeList list = element.getElementsByTagName(tagName);
		if (list.getLength() > 0)
		{
			return (Element) list.item(0);
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
		@NonNull final List<Element> childElements = GxlParser.getLevel1ChildElementsByTagName(element, tagName);
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
	 * @return DOM element if found, null if none
	 */
	@NonNull
	static private List<Element> getLevel1ChildElementsByTagName(@NonNull final Element element, final String tagName)
	{
		@NonNull final ArrayList<Element> list = new ArrayList<>();
		@NonNull final NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			final Node childNode = childNodes.item(i);
			if (childNode instanceof Element)
			{
				@NonNull final Element childElement = (Element) childNode;
				if (childElement.getTagName().equals(tagName))
				{
					list.add(childElement);
				}
			}
		}
		return list;
	}
}
