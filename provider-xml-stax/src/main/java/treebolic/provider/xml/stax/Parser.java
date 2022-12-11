/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.xml.stax;

import com.sun.istack.internal.NotNull;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

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

	private static final String DEFAULTTREEEDGE = "default.treeedge";

	private static final String DEFAULTEDGE = "default.edge";

	private static void setAttribute(final StartElement element, final String qName, final Consumer<String> consumer)
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

	private static <T> void setAttribute(final StartElement element, final String qName, final Function<String, T> transformer, final Consumer<T> consumer)
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

	public static String parseText(@NonNull XMLEventReader reader) throws XMLStreamException
	{
		StringBuilder sb = new StringBuilder();
		while (reader.hasNext() && reader.peek().isCharacters())
		{
			// consume
			XMLEvent event = reader.nextEvent();

			String text = event.asCharacters().getData();
			if (!text.isEmpty())
			{
				//text = text.replace('\n', ' ');
				text = text.trim();
				sb.append(text);
			}
		}
		return sb.toString();
	}

	public static Model parse00(@NonNull XMLEventReader reader) throws XMLStreamException
	{
		while (reader.hasNext())
		{
			System.out.println(reader.nextEvent());
		}
		return null;
	}

	public static Model parse0(@NonNull XMLEventReader reader) throws XMLStreamException
	{
		while (reader.hasNext())
		{
			XMLEvent event = reader.nextEvent();

			if (event.isStartElement())
			{
				StartElement startElement = event.asStartElement();
				System.out.println("<" + startElement.getName().getLocalPart() + ">");
			}
			else if (event.isEndElement())
			{
				EndElement endElement = event.asEndElement();
				System.out.println("</" + endElement.getName().getLocalPart() + ">");
			}
			else if (event.isCharacters())
			{
				Characters chars = event.asCharacters();
				throw new RuntimeException("[" + chars.getData() + "]");
			}
		}
		return null;
	}

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
		@Nullable Stack<MutableNode> stack = new Stack<>();

		@Nullable Map<String, MutableNode> nodes = null;

		@Nullable INode root = null;

		@Nullable List<IEdge> edges = null;

		@NonNull Settings settings = new Settings();

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
								setAttribute(startElement2, "src", val -> settings.backgroundImageFile = val);
								reader.nextEvent(); // consume
							}
						}
						break;
					}

					case NODES:
					{
						nodes = new HashMap<>();
						stack = new Stack<>();

						setAttribute(startElement, "backcolor", Parser::parseColor, (v) -> settings.nodeBackColor = v);
						setAttribute(startElement, "forecolor", Parser::parseColor, (v) -> settings.foreColor = v);
						setAttribute(startElement, "border", Parser::parseBoolean, (v) -> settings.borderFlag = v);
						setAttribute(startElement, "ellipsize", Parser::parseBoolean, (v) -> settings.ellipsizeFlag = v);
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

						parseNode(reader, node);
						break;
					}

					case EDGES:
					{
						edges = new ArrayList<>();
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
						MutableEdge edge = new MutableEdge(fromNode, toNode);
						assert edges != null;
						edges.add(edge);
						break;
					}

					// case LABEL:
					// {
					// 	break;
					// }
				}
			}

			else if (event.isEndElement())
			{
				EndElement endElement = event.asEndElement();
				switch (endElement.getName().getLocalPart())
				{
					case NODE:
					{
						assert !stack.empty();
						stack.pop();
						break;
					}
					case NODES:
					{
						break;
					}
					case EDGES:
					{
						break;
					}
				}
			}

			else if (event.isCharacters())
			{
				throw new RuntimeException("[" + event.asCharacters().getData() + "] caught: setup filter");
			}
		}
		return new Model(new Tree(root, edges), settings);
	}

	public static void parseNode(@NonNull XMLEventReader reader, @NotNull MutableNode node) throws XMLStreamException
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
						reader.nextEvent();
						String label = reader.getElementText();
						if (!label.isEmpty())
						{
							node.setLabel(label);
						}
						break;
					}
					case CONTENT:
					{
						reader.nextEvent();
						String content = reader.getElementText();
						if (!content.isEmpty())
						{
							node.setContent(content);
						}
						break;
					}
					case IMG:
					{
						reader.nextEvent();
						setAttribute(startElement2, "src", node::setImageFile);
						break;
					}
					case A:
					{
						reader.nextEvent();
						setAttribute(startElement2, "href", node::setLink);
						break;
					}
					case TREEEDGE:
					{
						reader.nextEvent();
						break;
					}
					case MOUNTPOINT:
					{
						reader.nextEvent();
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
						reader.nextEvent();
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

	private static Integer parseColor(final String color)
	{
		if (color == null)
		{
			return null;
		}
		return Integer.parseInt(color, 16);
	}

	private static Integer parseInt(final String val)
	{
		if (val == null)
		{
			return null;
		}
		return Integer.parseInt(val);
	}

	private static Float parseFloat(final String val)
	{
		if (val == null)
		{
			return null;
		}
		return Float.parseFloat(val);
	}

	private static float[] parseFloats(final String val)
	{
		if (val == null)
		{
			return null;
		}
		return Utils.stringToFloats(val);
	}

	private static Double parseDouble(final String val)
	{
		if (val == null)
		{
			return null;
		}
		return Double.parseDouble(val);
	}

	private static Boolean parseBoolean(final String val)
	{
		if (val == null)
		{
			return null;
		}
		return Boolean.parseBoolean(val);
	}

	/**
	 * Main
	 *
	 * @param args command-line arguments
	 * @throws IOException        io exception
	 * @throws XMLStreamException xml stream exception
	 */
	public static void main(@NonNull String[] args) throws IOException, XMLStreamException
	{
		try (@NonNull Reader fr = new FileReader(args[0]))
		{
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader reader = factory.createFilteredReader(factory.createXMLEventReader(fr), event -> event.isEndElement() || event.isStartElement());

			@NonNull Model model = parse(reader);

			System.out.println(ModelDump.toString(model));
		}
	}
}
