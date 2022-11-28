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
	static public Model parseModel(final URL url)
	{
		// DOM document
		try
		{
			Document document = new Parser().makeDocument(url, null);
			if (document == null)
			{
				return null;
			}

			final Tree tree = GxlParser.parseTree(document);
			if (tree == null)
			{
				return null;
			}

			final Settings settings = GxlParser.parseSettings(document);
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
	static public Model parseModel(final String path)
	{
		// DOM document
		try
		{
			Document document = new Parser().makeDocument(new File(path).toURI().toURL(), null);
			if (document == null)
			{
				return null;
			}

			final Tree tree = GxlParser.parseTree(document);
			if (tree == null)
			{
				return null;
			}

			final Settings settings = GxlParser.parseSettings(document);
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
	static public Tree parseTree(final URL url) throws ParserConfigurationException, SAXException, IOException
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
	static public Tree parseTree(final String path) throws ParserConfigurationException, SAXException, IOException
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
	static private Tree parseTree(final Document document)
	{
		// map
		final Hashtable<String, MutableNode> nodesById = new Hashtable<>();

		// enumerate nodes
		final NodeList gxlNodes = document.getElementsByTagName("node"); //$NON-NLS-1$
		for (int i = 0; i < gxlNodes.getLength(); i++)
		{
			final Node gxlNode = gxlNodes.item(i);
			final Element gxlNodeElement = (Element) gxlNode;

			// get id
			final String id = gxlNodeElement.getAttribute("id"); //$NON-NLS-1$

			// create node
			final MutableNode node = new MutableNode(null, id);
			nodesById.put(id, node);

			// attributes
			final NodeList gxlAttrs = gxlNodeElement.getElementsByTagName("attr"); //$NON-NLS-1$
			for (int j = 0; j < gxlAttrs.getLength(); j++)
			{
				final Node gxlAttr = gxlAttrs.item(j);
				final Element gxlAttrElement = (Element) gxlAttr;
				final String attributeName = gxlAttrElement.getAttribute("name"); //$NON-NLS-1$

				if (attributeName.equalsIgnoreCase("label")) //$NON-NLS-1$
				{
					final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
					if (element != null)
					{
						node.setLabel(element.getTextContent());
					}
				}
				else if (attributeName.equalsIgnoreCase("content")) //$NON-NLS-1$
				{
					final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
					if (element != null)
					{
						node.setContent(element.getTextContent());
					}
				}
				else if (attributeName.equalsIgnoreCase("link")) //$NON-NLS-1$
				{
					final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); //$NON-NLS-1$
					if (element != null)
					{
						final String hRef = element.getAttribute("xlink:href"); //$NON-NLS-1$
						node.setLink(hRef);
					}
				}
				else if (attributeName.equalsIgnoreCase("img-src")) //$NON-NLS-1$
				{
					final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); //$NON-NLS-1$
					if (element != null)
					{
						final String hRef = element.getAttribute("xlink:href"); //$NON-NLS-1$
						node.setImageFile(hRef);
					}
				}
				else if (attributeName.equalsIgnoreCase("mountpoint")) //$NON-NLS-1$
				{
					final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); //$NON-NLS-1$
					if (element != null)
					{
						final String hRef = element.getAttribute("xlink:href"); //$NON-NLS-1$
						final MountPoint.Mounting mountPoint = new MountPoint.Mounting();
						mountPoint.url = hRef;
						node.setMountPoint(mountPoint);
					}
				}
				else if (attributeName.equalsIgnoreCase("mountpoint-weight")) //$NON-NLS-1$
				{
					final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
					if (element != null)
					{
						final MountPoint.Mounting mountPoint = (MountPoint.Mounting) node.getMountPoint();
						if (mountPoint != null)
						{
							node.setWeight(Double.parseDouble(element.getTextContent()));
						}
					}
				}
				else if (attributeName.equalsIgnoreCase("backcolor")) //$NON-NLS-1$
				{
					final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
					if (element != null)
					{
						node.setBackColor(Utils.stringToColor(element.getTextContent()));
					}
				}
				else if (attributeName.equalsIgnoreCase("forecolor")) //$NON-NLS-1$
				{
					final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
					if (element != null)
					{
						node.setForeColor(Utils.stringToColor(element.getTextContent()));
					}
				}
			}
		}

		// enumerate edges
		List<IEdge> edges = null;
		final NodeList gxlEdges = document.getElementsByTagName("edge"); //$NON-NLS-1$
		for (int i = 0; i < gxlEdges.getLength(); i++)
		{
			final Node gxlEdge = gxlEdges.item(i);
			final Element gxlEdgeElement = (Element) gxlEdge;

			// get end ids
			final String fromId = gxlEdgeElement.getAttribute("from"); //$NON-NLS-1$
			if (fromId.isEmpty())
			{
				continue;
			}
			final String toId = gxlEdgeElement.getAttribute("to"); //$NON-NLS-1$
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
			final Element gxlTypeElement = GxlParser.getFirstLevel1ElementByTagName(gxlEdgeElement, "type"); //$NON-NLS-1$
			if (gxlTypeElement != null)
			{
				final String type = gxlTypeElement.getAttribute("xlink:href"); //$NON-NLS-1$
				if (!type.isEmpty())
				{
					if (type.equals("schema.xml#TreeEdge")) //$NON-NLS-1$
					{
						fromNode.getChildren().add(toNode);
						toNode.setParent(fromNode);

						// attributes
						final NodeList gxlEdgeAttrs = gxlEdgeElement.getElementsByTagName("attr"); //$NON-NLS-1$
						for (int j = 0; j < gxlEdgeAttrs.getLength(); j++)
						{
							final Node gxlAttr = gxlEdgeAttrs.item(j);
							final Element gxlAttrElement = (Element) gxlAttr;
							final String attributeName = gxlAttrElement.getAttribute("name"); //$NON-NLS-1$
							if (attributeName.equalsIgnoreCase("label")) //$NON-NLS-1$
							{
								final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
								if (element != null)
								{
									toNode.setEdgeLabel(element.getTextContent());
								}
							}
							else if (attributeName.equalsIgnoreCase("img-src")) //$NON-NLS-1$
							{
								final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); //$NON-NLS-1$
								if (element != null)
								{
									final String hRef = element.getAttribute("xlink:href"); //$NON-NLS-1$
									toNode.setEdgeImageFile(hRef);
								}
							}
							else if (attributeName.equalsIgnoreCase("stroke")) //$NON-NLS-1$
							{
								final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
								if (element != null)
								{
									final Integer style = Utils.modifyStyle(toNode.getEdgeStyle(), element.getTextContent(), Utils.StyleComponent.STROKE);
									toNode.setEdgeStyle(style);
								}
							}
							else if (attributeName.equalsIgnoreCase("fromterminator")) //$NON-NLS-1$
							{
								final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
								if (element != null)
								{
									final Integer style = Utils.modifyStyle(toNode.getEdgeStyle(), element.getTextContent(), Utils.StyleComponent.FROMTERMINATOR);
									toNode.setEdgeStyle(style);
								}
							}
							else if (attributeName.equalsIgnoreCase("toterminator")) //$NON-NLS-1$
							{
								final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
								if (element != null)
								{
									final Integer style = Utils.modifyStyle(toNode.getEdgeStyle(), element.getTextContent(), Utils.StyleComponent.TOTERMINATOR);
									toNode.setEdgeStyle(style);
								}
							}
						}
					}
					else if (type.equals("schema.xml#NonTreeEdge")) //$NON-NLS-1$
					{
						final MutableEdge edge = new MutableEdge(fromNode, toNode);
						if (edges == null)
						{
							edges = new ArrayList<>();
						}
						edges.add(edge);

						// attributes
						final NodeList gxlAttrs = gxlEdgeElement.getElementsByTagName("attr"); //$NON-NLS-1$
						for (int j = 0; j < gxlAttrs.getLength(); j++)
						{
							final Node gxlAttr = gxlAttrs.item(j);
							final Element gxlAttrElement = (Element) gxlAttr;
							final String attributeName = gxlAttrElement.getAttribute("name"); //$NON-NLS-1$
							if (attributeName.equalsIgnoreCase("label")) //$NON-NLS-1$
							{
								final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
								if (element != null)
								{
									edge.setLabel(element.getTextContent());
								}
							}
							else if (attributeName.equalsIgnoreCase("img-src")) //$NON-NLS-1$
							{
								final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); //$NON-NLS-1$
								if (element != null)
								{
									final String hRef = element.getAttribute("xlink:href"); //$NON-NLS-1$
									edge.setImageFile(hRef);
								}
							}
							else if (attributeName.equalsIgnoreCase("color")) //$NON-NLS-1$
							{
								final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
								if (element != null)
								{
									edge.setColor(Utils.stringToColor(element.getTextContent()));
								}
							}
							else if (attributeName.equals("stroke")) //$NON-NLS-1$
							{
								final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
								if (element != null)
								{
									edge.setStyle(Utils.modifyStyle(edge.getStyle(), element.getTextContent(), Utils.StyleComponent.STROKE));
								}
							}
							else if (attributeName.equals("fromterminator")) //$NON-NLS-1$
							{
								final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
								if (element != null)
								{
									edge.setStyle(Utils.modifyStyle(edge.getStyle(), element.getTextContent(), Utils.StyleComponent.FROMTERMINATOR));
								}
							}
							else if (attributeName.equals("toterminator")) //$NON-NLS-1$
							{
								final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
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
		final INode root = nodesById.get("root"); //$NON-NLS-1$
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
	static private Settings parseSettings(final Document document)
	{
		final Settings settings = new Settings();
		settings.hasToolbarFlag = true;
		settings.hasStatusbarFlag = true;

		final Element gxlGraphElement = GxlParser.getFirstElementByTagName(document.getDocumentElement(), "graph"); //$NON-NLS-1$
		if (gxlGraphElement != null)
		{
			// enumerate attrs
			final List<Element> gxlAttrElements = GxlParser.getLevel1ChildElementsByTagName(gxlGraphElement, "attr"); //$NON-NLS-1$
			for (final Element gxlAttrElement : gxlAttrElements)
			{
				final String name = gxlAttrElement.getAttribute("name"); //$NON-NLS-1$

				// locator
				final Element element = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "locator"); //$NON-NLS-1$
				if (element != null)
				{
					final String value = element.getAttribute("xlink:href"); //$NON-NLS-1$
					switch (name)
					{
						case "nodes-img-src":
							//$NON-NLS-1$

							settings.defaultNodeImage = value;
							break;
						case "tree-img-src":
							//$NON-NLS-1$

							settings.backgroundImageFile = value;
							break;
						case "nodes-default-treeedge-img-src":
							//$NON-NLS-1$

							settings.defaultTreeEdgeImage = value;
							break;
						case "edges-default-edge-img-src":
							//$NON-NLS-1$

							settings.defaultEdgeImage = value;
							break;
					}
				}

				// string
				final Element element2 = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "string"); //$NON-NLS-1$
				if (element2 != null)
				{
					final String value = element2.getTextContent();
					switch (name)
					{
						case "treebolic-toolbar":
							//$NON-NLS-1$

							settings.hasToolbarFlag = Boolean.valueOf(value);
							break;
						case "treebolic-statusbar":
							//$NON-NLS-1$

							settings.hasStatusbarFlag = Boolean.valueOf(value);
							break;
						case "treebolic-popupmenu":
							//$NON-NLS-1$

							settings.hasPopUpMenuFlag = Boolean.valueOf(value);
							break;
						case "treebolic-tooltip":
							//$NON-NLS-1$

							settings.hasToolTipFlag = Boolean.valueOf(value);
							break;
						case "treebolic-focus-on-hover":
							//$NON-NLS-1$

							settings.focusOnHoverFlag = Boolean.valueOf(value);
							break;
						case "tree-backcolor":
							//$NON-NLS-1$

							settings.backColor = Utils.stringToColor(value);
							break;
						case "tree-forecolor":
							//$NON-NLS-1$

							settings.foreColor = Utils.stringToColor(value);
							break;
						case "tree-fontface":
							//$NON-NLS-1$

							settings.fontFace = value;
							break;
						case "tree-fontsize":
							//$NON-NLS-1$

							settings.fontSize = Integer.valueOf(value);
							break;
						case "tree-scalefonts":
							//$NON-NLS-1$

							settings.downscaleFontsFlag = Boolean.valueOf(value);
							break;
						case "tree-fontscaler":
							//$NON-NLS-1$

							settings.fontDownscaler = Utils.stringToFloats(value);
							break;
						case "tree-scaleimages":
							//$NON-NLS-1$

							settings.downscaleImagesFlag = Boolean.valueOf(value);
							break;
						case "tree-imagescaler":
							//$NON-NLS-1$

							settings.imageDownscaler = Utils.stringToFloats(value);
							break;
						case "tree-expansion":
							//$NON-NLS-1$

							settings.expansion = Float.valueOf(value);
							break;
						case "tree-sweep":
							//$NON-NLS-1$

							settings.sweep = Float.valueOf(value);
							break;
						case "tree-orientation":
							//$NON-NLS-1$

							settings.orientation = value;
							break;
						case "tree-preserve-orientation":
							//$NON-NLS-1$

							settings.preserveOrientationFlag = Boolean.valueOf(value);
							break;
						case "nodes-backcolor":
							//$NON-NLS-1$

							settings.nodeBackColor = Utils.stringToColor(value);
							break;
						case "nodes-forecolor":
							//$NON-NLS-1$

							settings.nodeForeColor = Utils.stringToColor(value);
							break;
						case "nodes-default-treeedge-color":
							//$NON-NLS-1$

							settings.treeEdgeColor = Utils.stringToColor(value);
							break;
						case "nodes-default-treeedge-stroke":
							//$NON-NLS-1$

							settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, value, Utils.StyleComponent.STROKE);
							break;
						case "nodes-default-treeedge-fromterminator":
							//$NON-NLS-1$

							settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, value, Utils.StyleComponent.FROMTERMINATOR);
							break;
						case "nodes-default-treeedge-toterminator":
							//$NON-NLS-1$

							settings.treeEdgeStyle = Utils.modifyStyle(settings.treeEdgeStyle, value, Utils.StyleComponent.TOTERMINATOR);
							break;
						case "nodes-border":
							//$NON-NLS-1$

							settings.borderFlag = Boolean.valueOf(value);
							break;
						case "nodes-ellipsize":
							//$NON-NLS-1$

							settings.ellipsizeFlag = Boolean.valueOf(value);
							break;
						case "nodes-label-max-lines":
							//$NON-NLS-1$

							settings.labelMaxLines = Integer.valueOf(value);
							break;
						case "nodes-label-extra-line-factor":
							//$NON-NLS-1$

							settings.labelExtraLineFactor = Float.valueOf(value);
							break;
						case "edges-arcs":
							//$NON-NLS-1$

							settings.edgesAsArcsFlag = Boolean.valueOf(value);
							break;
						case "edges-default-edge-color":
							//$NON-NLS-1$

							settings.edgeColor = Utils.stringToColor(value);
							break;
						case "edges-default-edge-fromterminator":
							//$NON-NLS-1$

							settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, value, Utils.StyleComponent.FROMTERMINATOR);
							break;
						case "edges-default-edge-toterminator":
							//$NON-NLS-1$

							settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, value, Utils.StyleComponent.TOTERMINATOR);
							break;
						case "edges-default-edge-stroke":
							//$NON-NLS-1$

							settings.edgeStyle = Utils.modifyStyle(settings.edgeStyle, value, Utils.StyleComponent.STROKE);
							break;
						default:
							System.err.println("gxl: Unhandled attribute: " + name); //$NON-NLS-1$

							break;
					}
				}

				// tuples
				final Element tupElement = GxlParser.getFirstLevel1ElementByTagName(gxlAttrElement, "tup"); //$NON-NLS-1$
				if (tupElement != null)
				{
					final List<Element> gxlTupleElements = GxlParser.getLevel1ChildElementsByTagName(tupElement, "string"); //$NON-NLS-1$
					if (gxlTupleElements != null)
					{
						int k = 0;
						for (final Element gxlTupleElement : gxlTupleElements)
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
										//$NON-NLS-1$
									{
										settings.menu = GxlParser.allocate(settings.menu, k);
										final MenuItem menuItem = settings.menu.get(k);
										menuItem.action = Utils.stringToAction(value);
										break;
									}
									case "menuitem-label":
										//$NON-NLS-1$
									{
										settings.menu = GxlParser.allocate(settings.menu, k);
										final MenuItem menuItem = settings.menu.get(k);
										menuItem.label = value;
										break;
									}
									case "menuitem-match-target":
										//$NON-NLS-1$
									{
										settings.menu = GxlParser.allocate(settings.menu, k);
										final MenuItem menuItem = settings.menu.get(k);
										menuItem.matchTarget = value;
										break;
									}
									case "menuitem-match-scope":
										//$NON-NLS-1$
									{
										settings.menu = GxlParser.allocate(settings.menu, k);
										final MenuItem menuItem = settings.menu.get(k);
										menuItem.matchScope = Utils.stringToScope(value);
										break;
									}
									case "menuitem-match-mode":
										//$NON-NLS-1$
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
	static private Element getFirstElementByTagName(final Element element, @SuppressWarnings("SameParameterValue") final String tagName)
	{
		final NodeList list = element.getElementsByTagName(tagName);
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
	static private Element getFirstLevel1ElementByTagName(final Element element, final String tagName)
	{
		final List<Element> childElements = GxlParser.getLevel1ChildElementsByTagName(element, tagName);
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
	static private List<Element> getLevel1ChildElementsByTagName(final Element element, final String tagName)
	{
		final ArrayList<Element> list = new ArrayList<>();
		final NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			final Node childNode = childNodes.item(i);
			if (childNode instanceof Element)
			{
				final Element childElement = (Element) childNode;
				if (childElement.getTagName().equals(tagName))
				{
					list.add(childElement);
				}
			}
		}
		return list;
	}
}
