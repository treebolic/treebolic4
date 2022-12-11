/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.xml.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.*;

/**
 * XML SAX parser
 */
public class Parser
{
	/**
	 * SAX handler
	 */
	public static class SaxHandler extends DefaultHandler
	{
		private static final String TREEBOLIC = "treebolic";

		private static final String TREE = "tree";
		private static final String NODES = "nodes";
		private static final String NODE = "node";

		private static final String TREEEDGE = "treeedge";

		private static final String DEFAULT_TREEEDGE = "default.treeedge";

		private static final String EDGES = "edges";
		private static final String EDGE = "edge";

		private static final String DEFAULT_EDGE = "default.edge";

		private static final String LABEL = "label";
		private static final String CONTENT = "content";
		private static final String IMG = "img";
		private static final String A = "a";

		private static final String MOUNTPOINT = "mountpoint";

		private static final String TOOLS = "tools";

		private static final String MENU = "menu";

		private static final String MENUITEM = "menuitem";

		private Stack<MutableNode> stack;

		private Map<String, MutableNode> nodes;

		private INode root;

		private List<IEdge> edges;

		private Settings settings = new Settings();

		@Nullable
		private MutableEdge edge = null;

		@Nullable
		private StringBuilder textSb = null;

		@Nullable
		String label;

		@Nullable
		String content;

		@Nullable
		String link;

		@Nullable
		String img;

		@Nullable
		MountPoint mountpoint;

		@Nullable
		MenuItem menuItem;

		@Override
		public void characters(char[] ch, int start, int length)
		{
			if (textSb == null)
			{
				textSb = new StringBuilder();
			}
			else
			{
				textSb.append(ch, start, length);
			}
		}

		@Override
		public void startDocument()
		{
		}

		@Override
		public void startElement(String uri, String localName, @NonNull String qName, @NonNull Attributes attributes)
		{
			switch (qName)
			{
				case TREEBOLIC:
				{
					String toolbar = attributes.getValue("toolbar"); // toolbar (true|false) #IMPLIED
					String statusbar = attributes.getValue("statusbar"); // statusbar (true|false) #IMPLIED
					String popupmenu = attributes.getValue("popupmenu"); // popupmenu (true|false) #IMPLIED
					String tooltip = attributes.getValue("tooltip"); // tooltip (true|false) #IMPLIED
					String tooltip_displays_content = attributes.getValue("tooltip-displays-content"); // tooltip-displays-content (true|false) #IMPLIED
					String focus_on_hover = attributes.getValue("focus-on-hover"); // focus-on-hover (true|false) #IMPLIED
					String focus = attributes.getValue("focus"); // focus IDREF #IMPLIED
					String xmoveto = attributes.getValue("xmoveto"); // xmoveto CDATA #IMPLIED
					String ymoveto = attributes.getValue("ymoveto"); // ymoveto CDATA #IMPLIED
					String xshift = attributes.getValue("xshift"); // xshift CDATA #IMPLIED
					String yshift = attributes.getValue("yshift"); // yshift CDATA #IMPLIED
					settings.hasToolbarFlag = parseBoolean(toolbar);
					settings.hasStatusbarFlag = parseBoolean(statusbar);
					settings.hasPopUpMenuFlag = parseBoolean(popupmenu);
					settings.hasToolTipFlag = parseBoolean(tooltip);
					settings.toolTipDisplaysContentFlag = parseBoolean(tooltip_displays_content);
					settings.focusOnHoverFlag = parseBoolean(focus_on_hover);
					settings.focus = focus;
					settings.xMoveTo = parseFloat(xmoveto, 0F);
					settings.xMoveTo = parseFloat(ymoveto, 0F);
					settings.xShift = parseFloat(xshift, 0F);
					settings.yShift = parseFloat(yshift, 0F);
					break;
				}

				case TREE:
				{
					String backcolor = attributes.getValue("backcolor"); // backcolor CDATA #IMPLIED
					String forecolor = attributes.getValue("forecolor"); // forecolor CDATA #IMPLIED
					String orientation = attributes.getValue("orientation"); // orientation CDATA #IMPLIED
					String expansion = attributes.getValue("expansion"); // expansion CDATA #IMPLIED
					String sweep = attributes.getValue("sweep"); // sweep CDATA #IMPLIED
					String preserve_orientation = attributes.getValue("preserve-orientation"); // preserve-orientation (true|false) #IMPLIED
					String fontface = attributes.getValue("fontface"); // fontface CDATA #IMPLIED
					String fontsize = attributes.getValue("fontsize"); // fontsize CDATA #IMPLIED
					String scalefonts = attributes.getValue("scalefonts"); // scalefonts CDATA #IMPLIED
					String fontscaler = attributes.getValue("fontscaler"); // fontscaler CDATA #IMPLIED
					String scaleimages = attributes.getValue("scaleimages"); // scaleimages CDATA #IMPLIED
					String imagescaler = attributes.getValue("imagescaler"); // imagescaler CDATA #IMPLIED
					settings.backColor = parseColor(backcolor);
					settings.foreColor = parseColor(forecolor);
					settings.orientation = orientation;
					settings.expansion = parseFloat(expansion, 1.F);
					settings.sweep = parseFloat(sweep, 1.F);
					settings.preserveOrientationFlag = parseBoolean(preserve_orientation);
					settings.fontFace = fontface;
					settings.fontSize = parseInt(fontsize);
					settings.fontDownscaler = fontscaler == null ? null : Utils.stringToFloats(fontscaler);
					settings.downscaleFontsFlag = parseBoolean(scalefonts);
					settings.imageDownscaler = imagescaler == null ? null : Utils.stringToFloats(imagescaler);
					settings.downscaleImagesFlag = parseBoolean(scaleimages);
					break;
				}

				case NODES:
				{
					String backcolor = attributes.getValue("backcolor"); // backcolor CDATA #IMPLIED
					String forecolor = attributes.getValue("forecolor"); // forecolor CDATA #IMPLIED
					String border = attributes.getValue("border"); // border (true|false) #IMPLIED
					String ellipsize = attributes.getValue("ellipsize"); // ellipsize (true|false) #IMPLIED

					nodes = new HashMap<>();
					stack = new Stack<>();
					settings.nodeBackColor = parseColor(backcolor);
					settings.foreColor = parseColor(forecolor);
					settings.borderFlag = parseBoolean(border);
					settings.ellipsizeFlag = parseBoolean(ellipsize);
					break;
				}

				case NODE:
				{
					String backcolor = attributes.getValue("backcolor"); // backcolor CDATA #IMPLIED
					String forecolor = attributes.getValue("forecolor"); // forecolor CDATA #IMPLIED
					String weight = attributes.getValue("weight"); // weight CDATA #IMPLIED

					String id = attributes.getValue("id");
					INode parent = stack.empty() ? null : stack.peek();
					@Nullable MutableNode node = new MutableNode(parent, id);
					nodes.put(id, node);
					stack.push(node);
					if (parent == null)
					{
						root = node;
					}
					node.setBackColor(parseColor(backcolor));
					node.setForeColor(parseColor(forecolor));
					node.setWeight(parseDouble(weight, 1.));
					break;
				}

				case EDGES:
				{
					String arc = attributes.getValue("arcs"); // arcs (true|false) #IMPLIED
					settings.edgesAsArcsFlag = parseBoolean(arc);
					edges = new ArrayList<>();
					break;
				}

				case EDGE:
				{
					String color = attributes.getValue("color"); // color CDATA #IMPLIED
					String stroke = attributes.getValue("stroke"); // stroke CDATA #IMPLIED
					String toterminator = attributes.getValue("toterminator"); // toterminator (z|a|c|d|t|h|cf|df|tf) #IMPLIED
					String fromterminator = attributes.getValue("fromterminator"); // fromterminator (z|a|c|d|t|h|cf|df|tf) #IMPLIED
					String line = attributes.getValue("line"); // line (true|false) #IMPLIED
					String hidden = attributes.getValue("hidden"); // hidden (true|false) #IMPLIED

					String from = attributes.getValue("from");
					String to = attributes.getValue("to");
					INode fromNode = nodes.get(from);
					INode toNode = nodes.get(to);
					edge = new MutableEdge(fromNode, toNode);
					edge.setColor(parseColor(color));
					edge.setStyle(Utils.parseStyle(stroke, fromterminator, toterminator, line, hidden));
					edges.add(edge);
					break;
				}

				case TREEEDGE:
				{
					break;
				}

				case DEFAULT_TREEEDGE:
				{
					break;
				}

				case DEFAULT_EDGE:
				{
					break;
				}

				case A:
				{
					String href = attributes.getValue("href"); // href CDATA #REQUIRED
					String target = attributes.getValue("target"); // target CDATA #IMPLIED
					link = href;
					break;
				}

				case IMG:
				{
					String src = attributes.getValue("src"); // src CDATA #REQUIRED
					img = src;
					break;
				}

				case LABEL:
				case CONTENT:
				{
					break;
				}

				case MOUNTPOINT:
				{
					String now = attributes.getValue("now"); // now (true|false) #IMPLIED
					mountpoint = new MountPoint.Mounting();
					break;
				}

				case TOOLS:
				{
					break;
				}

				case MENU:
				{
					settings.menu = new ArrayList<>();
					break;
				}

				case MENUITEM:
				{
					String action = attributes.getValue("action"); // action (goto|search|focus|GOTO|SEARCH|FOCUS) #REQUIRED
					String match_target = attributes.getValue("match-target"); // match-target CDATA #IMPLIED
					String match_scope = attributes.getValue("match-scope"); // match-scope (label|content|link|id|LABEL|CONTENT|LINK|ID) #IMPLIED
					String match_mode = attributes.getValue("match-mode"); // match-mode (equals|startswith|includes|EQUALS|STARTSWITH|INCLUDES) #IMPLIED
					MenuItem menuItem = new MenuItem();
					menuItem.action = MenuItem.Action.valueOf(action);
					menuItem.target = match_target;
					menuItem.matchScope = Utils.stringToScope(match_scope);
					menuItem.matchMode = Utils.stringToMode(match_mode);
					assert settings.menu != null;
					settings.menu.add(menuItem);
					// label
					// link
					break;
				}
			}
		}

		private Integer parseColor(final String color)
		{
			if (color == null)
			{
				return null;
			}
			return Integer.parseInt(color, 16);
		}

		private Integer parseInt(final String val)
		{
			if (val == null)
			{
				return null;
			}
			return Integer.parseInt(val);
		}

		private float parseFloat(final String val, float defaultValue)
		{
			if (val == null)
			{
				return defaultValue;
			}
			return Float.parseFloat(val);
		}

		private double parseDouble(final String val, double defaultValue)
		{
			if (val == null)
			{
				return defaultValue;
			}
			return Double.parseDouble(val);
		}

		private Boolean parseBoolean(final String val)
		{
			if (val == null)
			{
				return null;
			}
			return Boolean.parseBoolean(val);
		}

		@NonNull
		private String getText()
		{
			assert textSb != null;
			String text = textSb.toString().trim();
			textSb.setLength(0);
			return text;
		}

		@Override
		public void endElement(String uri, String localName, @NonNull String qName)
		{
			switch (qName)
			{
				case NODE:
				{
					@Nullable MutableNode node = stack.empty() ? null : stack.peek();
					stack.pop();
					if (node != null)
					{
						if (label != null)
						{
							node.setLabel(label);
							label = null;
						}
						if (content != null)
						{
							node.setContent(content);
							content = null;
						}
						if (link != null)
						{
							node.setLink(link);
							link = null;
						}
						if (mountpoint != null)
						{
							node.setMountPoint(mountpoint);
							mountpoint = null;
						}
						if (img != null)
						{
							node.setImageFile(img);
							img = null;
						}
					}
					break;
				}
				case EDGE:
				{
					if (edge != null)
					{
						String text = getText();
						if (!text.isEmpty())
						{
							edge.setLabel(text);
						}
					}
					break;
				}

				case NODES:
				{
					break;
				}

				case EDGES:
				{
					edge = null;
					break;
				}

				case LABEL:
				{
					String text = getText();
					label = text;
					break;
				}
				case CONTENT:
				{
					String text = getText();
					content = text;
					break;
				}

				case MENUITEM:
				{
					if (menuItem != null)
					{
						if (label != null)
						{
							menuItem.label = label;
							label = null;
						}
						if (link != null)
						{
							menuItem.link = link;
							link = null;
						}
					}
					menuItem = null;
					break;
				}
			}
		}

		/**
		 * Get result
		 *
		 * @return model
		 */
		@NonNull
		public Model getResult()
		{
			return new Model(new Tree(root, edges), settings);
		}
	}

	/**
	 * Main
	 *
	 * @param args command-line arguments
	 * @throws ParserConfigurationException parser configuration exception
	 * @throws SAXException                 sax exception
	 * @throws IOException                  io exception
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		// @formatter:off
		try	{ factory.setFeature(XMLConstants.ACCESS_EXTERNAL_DTD, false);} catch(@NonNull final Exception ignored){}
		try	{ factory.setFeature(XMLConstants.ACCESS_EXTERNAL_SCHEMA, false);} catch(@NonNull final Exception ignored){}
		try	{ factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); } catch(@NonNull final Exception ignored){}
		// @formatter:on
		SAXParser saxParser = factory.newSAXParser();

		@NonNull SaxHandler handler = new SaxHandler();
		saxParser.parse(args[0], handler);
		@NonNull Model model = handler.getResult();

		System.out.println(ModelDump.toString(model));
	}
}
