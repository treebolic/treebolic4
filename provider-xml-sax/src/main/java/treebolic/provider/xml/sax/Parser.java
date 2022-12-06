/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.xml.sax;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import treebolic.annotations.NonNull;
import treebolic.model.*;

public class Parser
{
	public static class SaxHandler extends DefaultHandler
	{
		private static final String NODES = "nodes";
		private static final String NODE = "node";
		private static final String EDGES = "edges";
		private static final String EDGE = "edge";

		private Deque<MutableNode> stack;

		private Map<String, MutableNode> nodes;

		private INode root;

		private List<IEdge> edges;

		private MutableNode node = null;

		private MutableEdge edge = null;

		private StringBuilder textSb = null;

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
		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			switch (qName)
			{
				case NODES:
					nodes = new HashMap<>();
					stack = new ArrayDeque<>();
					break;

				case NODE:
					String id = attributes.getValue("id");
					INode parent = stack.peek();
					node = new MutableNode(parent, id);
					nodes.put(id, node);
					stack.push(node);
					if (parent == null)
					{
						root = node;
					}
					break;

				case EDGES:
					edges = new ArrayList<>();
					break;

				case EDGE:
					String from = attributes.getValue("from");
					String to = attributes.getValue("to");
					INode fromNode = nodes.get(from);
					INode toNode = nodes.get(to);
					edge = new MutableEdge(fromNode, toNode);
					edges.add(edge);
					break;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
		{
			String text = textSb.toString();
			if (!text.isEmpty())
			{
				text = text.replace('\n', ' ');
				text = text.trim();
				if (!text.isEmpty())
				{
					if (node != null)
					{
						node.setLabel(text);
					}
					else if (edge != null)
					{
						edge.setLabel(text);
					}
					else
					{
						System.err.println("TEXT " + text);
					}
				}
			}
			textSb.setLength(0);

			switch (qName)
			{
				case NODE:
					stack.pop();
					break;

				case NODES:
					node = null;
					break;

				case EDGES:
					edge = null;
					break;
			}
		}

		public Model getResult()
		{
			return new Model(new Tree(root, edges), new Settings());
		}
	}

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

		SaxHandler handler = new SaxHandler();
		saxParser.parse(args[0], handler);
		Model model = handler.getResult();

		System.out.println(ModelDump.toString(model));
	}
}
