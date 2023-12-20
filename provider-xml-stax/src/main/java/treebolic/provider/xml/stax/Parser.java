/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.provider.xml.stax;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.*;


/**
 * StAX parser
 */
public class Parser
{
	private static final String TREEBOLIC = "treebolic";
	private static final String TREE = "tree";
	private static final String NODES = "nodes";
	private static final String NODE = "node";
	private static final String EDGES = "edges";
	private static final String TREEEDGE = "treeedge";
	private static final String EDGE = "edge";
	private static final String LABEL = "label";
	private static final String CONTENT = "content";
	private static final String IMG = "img";
	private static final String A = "a";
	private static final String MOUNTPOINT = "mountpoint";
	private static final String DEFAULT_TREEEDGE = "default.treeedge";
	private static final String DEFAULT_EDGE = "default.edge";
	private static final String MENU = "menu";
	private static final String MENUITEM = "menuitem";

	/**
	 * StAX handler
	 *
	 * @param reader reader
	 * @return model
	 * @throws XMLStreamException xml stream exception
	 */
	@NonNull
	public static Model parse(@NonNull XMLEventReader reader) throws XMLStreamException
	{
		// node stack

		@Nullable Stack<MutableNode> stack = new Stack<>();

		// nodes by ID

		@Nullable Map<String, MutableNode> nodes = null;

		// result

		@Nullable INode root = null;

		@Nullable List<IEdge> edges = null;

		@NonNull Settings settings = new Settings();

		@Nullable List<MountTask> mountTasks = null;

		// drive event loop

		while (reader.hasNext())
		{
			XMLEvent event = reader.nextEvent();

			if (event.isStartElement())
			{
				StartElement startElement = event.asStartElement();
				switch (startElement.getName().getLocalPart())
				{
					case TREEBOLIC:
					{
						setAttribute(startElement, "toolbar", Parser::parseBoolean, (v) -> settings.hasToolbarFlag = v);
						setAttribute(startElement, "statusbar", Parser::parseBoolean, (v) -> settings.hasStatusbarFlag = v);
						setAttribute(startElement, "popupmenu", Parser::parseBoolean, (v) -> settings.hasPopUpMenuFlag = v);
						setAttribute(startElement, "content-format", (v) -> settings.contentFormat = v);
						setAttribute(startElement, "tooltip", Parser::parseBoolean, (v) -> settings.hasToolTipFlag = v);
						setAttribute(startElement, "tooltip-displays-content", Parser::parseBoolean, (v) -> settings.toolTipDisplaysContentFlag = v);
						setAttribute(startElement, "focus-on-hover", Parser::parseBoolean, (v) -> settings.focusOnHoverFlag = v);
						setAttribute(startElement, "focus", (v) -> settings.focus = v);
						setAttribute(startElement, "xmoveto", Parser::parseFloat, (v) -> settings.xMoveTo = v);
						setAttribute(startElement, "ymoveto", Parser::parseFloat, (v) -> settings.xMoveTo = v);
						setAttribute(startElement, "xshift", Parser::parseFloat, (v) -> settings.xShift = v);
						setAttribute(startElement, "yshift", Parser::parseFloat, (v) -> settings.yShift = v);
						break;
					}

					case TREE:
					{
						setAttribute(startElement, "backcolor", Parser::parseColor, (v) -> settings.backColor = v);
						setAttribute(startElement, "forecolor", Parser::parseColor, (v) -> settings.foreColor = v);
						setAttribute(startElement, "orientation", (v) -> settings.orientation = v);
						setAttribute(startElement, "expansion", Parser::parseFloat, (v) -> settings.expansion = v);
						setAttribute(startElement, "sweep", Parser::parseFloat, (v) -> settings.sweep = v);
						setAttribute(startElement, "preserve-orientation", Parser::parseBoolean, (v) -> settings.preserveOrientationFlag = v);
						setAttribute(startElement, "fontface", (v) -> settings.fontFace = v);
						setAttribute(startElement, "fontsize", Parser::parseInt, (v) -> settings.fontSize = v);
						setAttribute(startElement, "scalefonts", Parser::parseBoolean, (v) -> settings.downscaleFontsFlag = v);
						setAttribute(startElement, "fontscaler", Parser::parseFloats, (v) -> settings.fontDownscaler = v);
						setAttribute(startElement, "scaleimages", Parser::parseBoolean, (v) -> settings.downscaleImagesFlag = v);
						setAttribute(startElement, "imagescaler", Parser::parseFloats, (v) -> settings.imageDownscaler = v);

						XMLEvent event2 = reader.peek();
						if (event2.isStartElement())
						{
							StartElement startElement2 = event2.asStartElement();
							if (IMG.equals(startElement2.getName().getLocalPart()))
							{
								reader.nextEvent(); // consume
								setAttribute(startElement2, "src", val -> settings.backgroundImageFile = val);
							}
						}
						break;
					}

					case NODES:
					{
						nodes = new HashMap<>();
						stack = new Stack<>();

						setAttribute(startElement, "backcolor", Parser::parseColor, (v) -> settings.nodeBackColor = v);
						setAttribute(startElement, "forecolor", Parser::parseColor, (v) -> settings.nodeForeColor = v);
						setAttribute(startElement, "border", Parser::parseBoolean, (v) -> settings.borderFlag = v);
						setAttribute(startElement, "ellipsize", Parser::parseBoolean, (v) -> settings.ellipsizeFlag = v);

						XMLEvent event3 = reader.peek();
						// if (event3.isStartElement())
						// {
						// 	StartElement startElement3 = event3.asStartElement();
						// 	if (LABEL.equals(startElement3.getName().getLocalPart()))
						// 	{
						// 		reader.nextEvent(); // consume
						// 		String label = reader.getElementText();
						// 		if (!label.isEmpty())
						// 		{
						// 			settings.treeedgeLabel = label;
						// 		}
						// 	}
						// }
						// event3 = reader.peek();
						if (event3.isStartElement())
						{
							StartElement startElement3 = event3.asStartElement();
							if (IMG.equals(startElement3.getName().getLocalPart()))
							{
								reader.nextEvent(); // consume
								setAttribute(startElement3, "src", (v) -> settings.defaultNodeImage = v);
							}
						}
						break;
					}

					case NODE:
					{
						String id = startElement.getAttributeByName(new QName("id")).getValue();
						INode parent = stack.empty() ? null : stack.peek();
						@Nullable MutableNode node = new MutableNode(parent, id);

						setAttribute(startElement, "backcolor", Parser::parseColor, node::setBackColor);
						setAttribute(startElement, "forecolor", Parser::parseColor, node::setForeColor);
						setAttribute(startElement, "weight", Parser::parseDouble, node::setWeight);

						assert nodes != null;
						nodes.put(id, node);

						stack.push(node);

						if (parent == null)
						{
							root = node;
						}

						// node content
						parseNode(reader, node);
						break;
					}

					case EDGES:
					{
						edges = new ArrayList<>();

						setAttribute(startElement, "arcs", Parser::parseBoolean, (v) -> settings.edgesAsArcsFlag = v);
						break;
					}

					case EDGE:
					{
						Attribute fromAttribute = startElement.getAttributeByName(new QName("from"));
						Attribute toAttribute = startElement.getAttributeByName(new QName("to"));
						String from = fromAttribute.getValue();
						String to = toAttribute.getValue();
						assert nodes != null;
						INode fromNode = nodes.get(from);
						INode toNode = nodes.get(to);
						@NonNull MutableEdge edge = new MutableEdge(fromNode, toNode);
						assert edges != null;
						edges.add(edge);

						setStyleAttribute(startElement, "stroke", "fromterminator", "toterminator", "line", "hidden", edge::setStyle);
						setAttribute(startElement, "color", Parser::parseColor, edge::setColor);

						XMLEvent event3 = reader.peek();
						if (event3.isStartElement())
						{
							StartElement startElement3 = event3.asStartElement();
							if (LABEL.equals(startElement3.getName().getLocalPart()))
							{
								reader.nextEvent(); // consume
								String label = reader.getElementText();
								if (!label.isEmpty())
								{
									edge.setLabel(label);
								}
							}
						}
						event3 = reader.peek();
						if (event3.isStartElement())
						{
							StartElement startElement3 = event3.asStartElement();
							if (IMG.equals(startElement3.getName().getLocalPart()))
							{
								reader.nextEvent(); // consume
								setAttribute(startElement3, "src", edge::setImageFile);
							}
						}

						break;
					}

					case DEFAULT_TREEEDGE:
					{
						setStyleAttribute(startElement, "stroke", "fromterminator", "toterminator", "line", "hidden", v -> settings.treeEdgeStyle = v);
						setAttribute(startElement, "color", Parser::parseColor, (v) -> settings.treeEdgeColor = v);

						XMLEvent event3 = reader.peek();
						// if (event3.isStartElement())
						// {
						// 	StartElement startElement3 = event3.asStartElement();
						// 	if (LABEL.equals(startElement3.getName().getLocalPart()))
						// 	{
						// 		reader.nextEvent(); // consume
						// 		String label = reader.getElementText();
						// 		if (!label.isEmpty())
						// 		{
						// 			settings.treeedgeLabel = label;
						// 		}
						// 	}
						// }
						// event3 = reader.peek();
						if (event3.isStartElement())
						{
							StartElement startElement3 = event3.asStartElement();
							if (IMG.equals(startElement3.getName().getLocalPart()))
							{
								reader.nextEvent(); // consume
								setAttribute(startElement3, "src", (v) -> settings.defaultTreeEdgeImage = v);
							}
						}
						break;
					}

					case DEFAULT_EDGE:
					{
						setStyleAttribute(startElement, "stroke", "fromterminator", "toterminator", "line", "hidden", (v) -> settings.edgeStyle = v);
						setAttribute(startElement, "color", Parser::parseColor, (v) -> settings.edgeColor = v);

						XMLEvent event3 = reader.peek();
						// if (event3.isStartElement())
						// {
						// 	StartElement startElement3 = event3.asStartElement();
						// 	if (LABEL.equals(startElement3.getName().getLocalPart()))
						// 	{
						// 		reader.nextEvent(); // consume
						// 		String label = reader.getElementText();
						// 		if (!label.isEmpty())
						// 		{
						// 			settings.edgeLabel = label;
						// 		}
						// 	}
						// }
						// event3 = reader.peek();
						if (event3.isStartElement())
						{
							StartElement startElement3 = event3.asStartElement();
							if (IMG.equals(startElement3.getName().getLocalPart()))
							{
								reader.nextEvent(); // consume
								setAttribute(startElement3, "src", (v) -> settings.defaultEdgeImage = v);
							}
						}
						break;
					}

					case MENU:
					{
						settings.menu = new ArrayList<>();
						break;
					}

					case MENUITEM:
					{
						@NonNull MenuItem menuItem = new MenuItem();
						setAttribute(startElement, "action", s -> MenuItem.Action.valueOf(s.toUpperCase()), (v) -> menuItem.action = v);
						setAttribute(startElement, "match-target", (v) -> menuItem.matchTarget = v);
						setAttribute(startElement, "match-scope", Utils::stringToScope, (v) -> menuItem.matchScope = v);
						setAttribute(startElement, "match-mode", Utils::stringToMode, (v) -> menuItem.matchMode = v);
						assert settings.menu != null;
						settings.menu.add(menuItem);

						XMLEvent event3 = reader.peek();
						if (event3.isStartElement())
						{
							StartElement startElement3 = event3.asStartElement();
							if (LABEL.equals(startElement3.getName().getLocalPart()))
							{
								reader.nextEvent(); // consume
								String label = reader.getElementText();
								if (!label.isEmpty())
								{
									menuItem.label = label;
								}
							}
						}
						event3 = reader.peek();
						if (event3.isStartElement())
						{
							StartElement startElement3 = event3.asStartElement();
							if (A.equals(startElement3.getName().getLocalPart()))
							{
								reader.nextEvent(); // consume
								setAttribute(startElement3, "href", (v) -> menuItem.link = v);
								setAttribute(startElement3, "target", (v) -> menuItem.target = v);
							}
						}
						break;
					}
				}
			}

			else if (event.isEndElement())
			{
				EndElement endElement = event.asEndElement();
				if (NODE.equals(endElement.getName().getLocalPart()))
				{
					assert !stack.empty();
					INode node = stack.peek();
					@Nullable MountPoint.Mounting mountpoint2 = (MountPoint.Mounting) node.getMountPoint();
					if (mountpoint2 != null && mountpoint2.now != null && mountpoint2.now)
					{
						@NonNull MountTask task = new MountTask(mountpoint2, node);
						if (mountTasks == null)
						{
							mountTasks = new ArrayList<>();
						}
						mountTasks.add(task);
					}

					stack.pop();
				}
			}

			else if (event.isCharacters())
			{
				throw new RuntimeException("[" + event.asCharacters().getData() + "] caught: setup filter");
			}
		}
		return new Model(new Tree(root, edges, mountTasks), settings, null);
	}

	/**
	 * Parse node content (label, content, img, a, treeedge, mountpoint
	 *
	 * @param reader event reader positioned on 'node' start element
	 * @param node   current node
	 * @throws XMLStreamException XML stream exception
	 */
	public static void parseNode(@NonNull XMLEventReader reader, @NonNull MutableNode node) throws XMLStreamException
	{
		// enter node mode
		while (reader.hasNext())
		{
			XMLEvent event2 = reader.peek();

			if (event2.isStartElement())
			{
				StartElement startElement2 = event2.asStartElement();
				switch (startElement2.getName().getLocalPart())
				{
					case LABEL:
					{
						reader.nextEvent(); // consume
						String label = reader.getElementText();
						if (!label.isEmpty())
						{
							node.setLabel(label);
						}
						break;
					}

					case CONTENT:
					{
						reader.nextEvent(); // consume
						String content = reader.getElementText();
						if (!content.isEmpty())
						{
							node.setContent(content);
						}
						break;
					}

					case IMG:
					{
						reader.nextEvent(); // consume
						setAttribute(startElement2, "src", node::setImageFile);
						break;
					}

					case A:
					{
						reader.nextEvent(); // consume
						setAttribute(startElement2, "href", node::setLink);
						break;
					}

					case TREEEDGE:
					{
						reader.nextEvent(); // consume
						setStyleAttribute(startElement2, "stroke", "fromterminator", "toterminator", "line", "hidden", node::setEdgeStyle);
						setAttribute(startElement2, "color", Parser::parseColor, node::setEdgeColor);

						XMLEvent event3 = reader.peek();
						if (event3.isStartElement())
						{
							StartElement startElement3 = event3.asStartElement();
							if (LABEL.equals(startElement3.getName().getLocalPart()))
							{
								reader.nextEvent(); // consume
								String label = reader.getElementText();
								if (!label.isEmpty())
								{
									node.setEdgeLabel(label);
								}
							}
						}
						event3 = reader.peek();
						if (event3.isStartElement())
						{
							StartElement startElement3 = event3.asStartElement();
							if (IMG.equals(startElement3.getName().getLocalPart()))
							{
								reader.nextEvent(); // consume
								setAttribute(startElement3, "src", node::setEdgeImageFile);
							}
						}
						break;
					}

					case MOUNTPOINT:
					{
						@NonNull MountPoint.Mounting mountPoint = new MountPoint.Mounting();
						StartElement mountpointElement = reader.nextEvent().asStartElement(); // consume
						setAttribute(mountpointElement, "now", Parser::parseBoolean, (v) -> mountPoint.now = v);

						StartElement aElement = reader.nextEvent().asStartElement();
						assert A.equals(aElement.getName().getLocalPart());
						setAttribute(aElement, "href", (v) -> mountPoint.url = v);
						node.setMountPoint(mountPoint);
						break;
					}

					default:
					{
						return;
					}
				}
			}
			else if (event2.isEndElement())
			{
				EndElement endElement2 = event2.asEndElement();
				switch (endElement2.getName().getLocalPart())
				{
					case LABEL:
					case CONTENT:
					case IMG:
					case A:
					case TREEEDGE:
					case MOUNTPOINT:
					{
						reader.nextEvent(); // consume
						break;
					}
					default:
					{
						return;
					}
				}
			}
			else if (event2.isCharacters())
			{
				throw new RuntimeException("[" + event2.asCharacters().getData() + "] caught: setup filter");
			}
		}
	}

	@Nullable
	private static Integer parseColor(final String color)
	{
		return Utils.parseColor(color);
	}

	private static Integer parseInt(@Nullable final String val)
	{
		if (val == null)
		{
			return null;
		}
		return Integer.parseInt(val);
	}

	private static Float parseFloat(@Nullable final String val)
	{
		if (val == null)
		{
			return null;
		}
		return Float.parseFloat(val);
	}

	private static float[] parseFloats(@Nullable final String val)
	{
		if (val == null)
		{
			return null;
		}
		return Utils.stringToFloats(val);
	}

	private static Double parseDouble(@Nullable final String val)
	{
		if (val == null)
		{
			return null;
		}
		return Double.parseDouble(val);
	}

	private static Boolean parseBoolean(@Nullable final String val)
	{
		if (val == null)
		{
			return null;
		}
		return Boolean.parseBoolean(val);
	}

	private static void setAttribute(@NonNull final StartElement element, @NonNull final String qName, @NonNull final Consumer<String> consumer)
	{
		Attribute attribute = element.getAttributeByName(new QName(qName));
		if (attribute != null)
		{
			String val = attribute.getValue();
			if (val != null)
			{
				consumer.accept(val);
			}
		}
	}

	private static <T> void setAttribute(@NonNull final StartElement element, @NonNull final String qName, @NonNull final Function<String, T> transformer, @NonNull final Consumer<T> consumer)
	{
		Attribute attribute = element.getAttributeByName(new QName(qName));
		if (attribute != null)
		{
			String val = attribute.getValue();
			if (val != null)
			{
				T val2 = transformer.apply(val);
				if (val2 != null)
				{
					consumer.accept(val2);
				}
			}
		}
	}

	/**
	 * Set style attribute
	 *
	 * @param element             element
	 * @param strokeQName         stroke qname
	 * @param fromTerminatorQName from-terminator qname
	 * @param toTerminatorQName   to-terminator qname
	 * @param lineQName           line qname
	 * @param hiddenQName         hidden qname
	 * @param consumer            consumer of produced value
	 */
	private static void setStyleAttribute(@NonNull final StartElement element, @NonNull @SuppressWarnings("SameParameterValue") final String strokeQName, @NonNull @SuppressWarnings("SameParameterValue") final String fromTerminatorQName, @NonNull @SuppressWarnings("SameParameterValue") final String toTerminatorQName, @NonNull @SuppressWarnings("SameParameterValue") final String lineQName, @NonNull @SuppressWarnings("SameParameterValue") final String hiddenQName, @NonNull final Consumer<Integer> consumer)
	{
		Attribute strokeAttribute = element.getAttributeByName(new QName(strokeQName));
		Attribute fromTerminatorAttribute = element.getAttributeByName(new QName(fromTerminatorQName));
		Attribute toTerminatorAttribute = element.getAttributeByName(new QName(toTerminatorQName));
		Attribute lineAttribute = element.getAttributeByName(new QName(lineQName));
		Attribute hiddenAttribute = element.getAttributeByName(new QName(hiddenQName));

		String stroke = strokeAttribute == null ? null : strokeAttribute.getValue();
		String fromTerminator = fromTerminatorAttribute == null ? null : fromTerminatorAttribute.getValue();
		String toTerminator = toTerminatorAttribute == null ? null : toTerminatorAttribute.getValue();
		String line = lineAttribute == null ? null : lineAttribute.getValue();
		String hidden = hiddenAttribute == null ? null : hiddenAttribute.getValue();

		@Nullable Integer sval = Utils.parseStyle(stroke, fromTerminator, toTerminator, line, hidden);
		if (sval != null)
		{
			consumer.accept(sval);
		}
	}

	private static XMLEventReader makeReader(@NonNull final Reader reader) throws XMLStreamException
	{
		XMLInputFactory factory = XMLInputFactory.newInstance();
		return factory.createFilteredReader(factory.createXMLEventReader(reader), event -> event.isEndElement() || event.isStartElement());
	}

	/**
	 * Make model
	 *
	 * @param filePath file path
	 * @return model
	 * @throws IOException        io exception
	 * @throws XMLStreamException xml stream exception
	 */
	@NonNull
	public static Model makeModel(@NonNull final String filePath) throws IOException, XMLStreamException
	{
		try (@NonNull Reader reader = new FileReader(filePath))
		{
			XMLEventReader eventReader = makeReader(reader);
			return parse(eventReader);
		}
	}

	/**
	 * Main
	 *
	 * @param args command-line arguments
	 * @throws IOException        io exception
	 * @throws XMLStreamException xml stream exception
	 */
	public static void main(@NonNull final String[] args) throws IOException, XMLStreamException
	{
		@NonNull Model model = makeModel(args[0]);
		System.out.println(ModelDump.toString(model));
	}
}
