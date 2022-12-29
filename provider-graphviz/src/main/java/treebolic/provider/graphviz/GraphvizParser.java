/**
 *
 */
package treebolic.provider.graphviz;

import org.graphviz.ParseException;
import org.graphviz.Parser;
import org.graphviz.objects.Edge;
import org.graphviz.objects.Id;
import org.graphviz.objects.Node;
import org.graphviz.objects.PortNode;

import java.io.*;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import treebolic.model.Tree;
import treebolic.model.*;
import treebolic.model.graph.*;

/**
 * Graphview parser
 *
 * @author Bernard Bou
 */
public class GraphvizParser
{
	/**
	 * Make model
	 *
	 * @param url graphviz file url
	 * @return model
	 */
	public static Model parseModel(final URL url)
	{
		try (InputStream is = url.openStream(); InputStreamReader reader = new InputStreamReader(is))
		{
			final Parser parser = new Parser();
			if (parser.parse(reader))
			{
				final org.graphviz.objects.Graph graphvizGraph = parser.getGraphs().get(0);
				// System.err.println(graphvizGraph.toString());
				return GraphvizParser.makeModel(graphvizGraph);
			}
		}
		catch (final IOException | ParseException e)
		{
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * Make model
	 *
	 * @param location graphviz file location
	 * @return model
	 */
	public static Model parseModel(final String location)
	{
		try (InputStream is = new FileInputStream(location); InputStreamReader reader = new InputStreamReader(is))
		{
			final Parser parser = new Parser();
			if (parser.parse(reader))
			{
				final org.graphviz.objects.Graph graphvizGraph = parser.getGraphs().get(0);
				// System.err.println(graphvizGraph.toString());
				return GraphvizParser.makeModel(graphvizGraph);
			}
		}
		catch (final IOException | ParseException e)
		{
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * Make tree
	 *
	 * @param url graphviz file url
	 * @return tree
	 */
	public static Tree parseTree(final URL url)
	{
		try (InputStream is = url.openStream(); InputStreamReader reader = new InputStreamReader(is))
		{
			final Parser parser = new Parser();
			parser.parse(reader);
			if (parser.parse(reader))
			{
				final org.graphviz.objects.Graph graphvizGraph = parser.getGraphs().get(0);
				// System.out.println(graphvizGraph.toString());
				return GraphvizParser.makeTree(graphvizGraph);
			}
		}
		catch (final IOException | ParseException e)
		{
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * Make tree
	 *
	 * @param location graphviz file location
	 * @return tree
	 */
	public static Tree parseTree(final String location)
	{
		try (InputStream is = new FileInputStream(location); InputStreamReader reader = new InputStreamReader(is))
		{
			final Parser parser = new Parser();
			parser.parse(reader);
			if (parser.parse(reader))
			{
				final org.graphviz.objects.Graph graphvizGraph = parser.getGraphs().get(0);
				// System.out.println(graphvizGraph.toString());
				return GraphvizParser.makeTree(graphvizGraph);
			}
		}
		catch (final IOException | ParseException e)
		{
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * Make model
	 *
	 * @param graphvizGraph graph
	 * @return model
	 */
	private static Model makeModel(final org.graphviz.objects.Graph graphvizGraph)
	{
		final Tree tree = GraphvizParser.makeTree(graphvizGraph);
		final Settings settings = GraphvizParser.makeSettings(graphvizGraph);
		return new Model(tree, settings);
	}

	/**
	 * Make tree
	 *
	 * @param graphvizGraph graphviz graph graph
	 * @return tree
	 */
	private static Tree makeTree(final org.graphviz.objects.Graph graphvizGraph)
	{
		final Graph graph = GraphvizParser.parseGraph(graphvizGraph);
		// System.out.println(graph.toString());

		// determine root node
		GraphNode rootNode = null;
		final List<GraphNode> graphRootNodes = graph.getNodesWithZeroDegree();
		if (graphRootNodes != null)
		{
			if (graphRootNodes.size() == 1)
			{
				rootNode = graphRootNodes.get(0);
			}
			else if (graphRootNodes.size() > 1)
			{
				int rootIndex = Integer.MAX_VALUE;
				for (final GraphNode graphRootNode : graphRootNodes)
				{
					// selection based on index
					final MutableGraphNode mutableNode = (MutableGraphNode) graphRootNode;
					final int index = mutableNode.getIndex();
					// System.err.println(mutableNode + " idx=" + mutableNode.getIndex());
					if (index < rootIndex)
					{
						rootNode = graphRootNode;
						rootIndex = index;
					}
				}
			}
			else
			{
				throw new RuntimeException("No root " + graphRootNodes);
			}
		}
		else
		{
			rootNode = graph.getNodeWithMinimumIncomingDegree();
		}

		return new Converter<MutableGraphNode>().graphToTree(graph, rootNode);
	}

	/**
	 * Parse graph
	 *
	 * @param graphvizGraph graphviz graph
	 * @return graph
	 */
	private static Graph parseGraph(final org.graphviz.objects.Graph graphvizGraph)
	{
		// treebolic graph
		final MutableGraph graph = new MutableGraph();

		// nodes
		final Hashtable<String, MutableGraphNode> nodesById = new Hashtable<>();
		for (final Node graphvizNode : graphvizGraph.getNodes(false))
		{
			final String id = GraphvizParser.nodeId(graphvizNode);

			// node
			final MutableGraphNode node = new MutableGraphNode(id);
			nodesById.put(id, node);
			graph.add(node);

			// node attributes

			// label
			String attribute;
			attribute = GraphvizParser.nodeLabel(graphvizNode);
			if (attribute != null && !attribute.isEmpty())
			{
				node.setLabel(attribute);
			}

			// content
			attribute = graphvizNode.getAttribute("content");
			if (attribute != null)
			{
				node.setContent(attribute);
			}

			// content
			attribute = graphvizNode.getAttribute("link_href");
			if (attribute != null)
			{
				node.setLink(attribute);
			}

			// image
			attribute = graphvizNode.getAttribute("img_src");
			if (attribute != null)
			{
				node.setImageFile(attribute);
			}

			// mountpoint
			attribute = graphvizNode.getAttribute("mountpoint_href");
			if (attribute != null)
			{
				final MountPoint.Mounting mountPoint = new MountPoint.Mounting();
				mountPoint.url = attribute;
				node.setMountPoint(mountPoint);
			}

			// color
			attribute = graphvizNode.getAttribute("backcolor");
			if (attribute != null)
			{
				node.setBackColor(Utils.parseColor(attribute));
			}
			attribute = graphvizNode.getAttribute("forecolor");
			if (attribute != null)
			{
				node.setForeColor(Utils.parseColor(attribute));
			}
		}

		// edges
		for (final Edge graphvizEdge : graphvizGraph.getEdges())
		{
			// get ends
			final PortNode from = graphvizEdge.getSource();
			final PortNode to = graphvizEdge.getTarget();

			// get end ids
			final String fromId = GraphvizParser.nodeId(from.getNode());
			final String toId = GraphvizParser.nodeId(to.getNode());

			// get end nodes
			final MutableGraphNode fromNode = nodesById.get(fromId);
			if (fromNode == null)
			{
				System.err.println("Graphviz: Node not found with id=" + fromId);
				continue;
			}
			final MutableGraphNode toNode = nodesById.get(toId);
			if (toNode == null)
			{
				System.err.println("Graphviz: Node not found with id=" + toId);
				continue;
			}

			String attribute;

			// whether this edge is tree edge
			Boolean isTreeEdge = null;
			attribute = graphvizEdge.getAttribute("type");
			if (attribute != null)
			{
				if (attribute.equals("tree"))
				{
					isTreeEdge = true;
				}
				else if (attribute.equals("nontree"))
				{
					isTreeEdge = false;
				}
			}

			// create edge
			final MutableEdge edge = new MutableEdge(fromNode, toNode);
			final GraphEdge graphEdge = new GraphEdge(fromNode, toNode, isTreeEdge);
			graphEdge.setUserData(edge);
			graph.add(graphEdge);

			// edge attributes
			attribute = graphvizEdge.getAttribute("label");
			if (attribute != null)
			{
				edge.setLabel(attribute);
			}
			attribute = graphvizEdge.getAttribute("img_src");
			if (attribute != null)
			{
				edge.setImageFile(attribute);
			}
			attribute = graphvizEdge.getAttribute("edge_color");
			if (attribute != null)
			{
				edge.setColor(Utils.parseColor(attribute));
			}
			edge.setStyle(Utils.parseStyle(graphvizEdge.getAttribute("stroke"), graphvizEdge.getAttribute("fromterminator"), graphvizEdge.getAttribute("toterminator"), graphvizEdge.getAttribute("line"), graphvizEdge.getAttribute("hidden")));  //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}
		return graph;
	}

	/**
	 * Node id
	 *
	 * @param graphvizNode node
	 * @return node id
	 */
	private static String nodeId(final Node graphvizNode)
	{
		final Id graphvizId = graphvizNode.getId();
		String id = graphvizId.getId();
		if (id != null && !id.isEmpty())
		{
			return id;
		}
		id = graphvizId.getLabel();
		if (id != null && !id.isEmpty())
		{
			return id;
		}
		throw new RuntimeException(graphvizNode + " has no id");
	}

	/**
	 * Node label
	 *
	 * @param graphvizNode node
	 * @return node id
	 */
	private static String nodeLabel(final Node graphvizNode)
	{
		String label = graphvizNode.getAttribute("label");
		if (label != null && !label.isEmpty())
		{
			return label;
		}

		final Id graphvizId = graphvizNode.getId();

		label = graphvizId.getLabel();
		if (label != null && !label.isEmpty())
		{
			return label;
		}

		// label = graphvizId.getId();
		// if (label != null && !label.isEmpty())
		// return tlabel;

		return null;
	}

	/**
	 * Make settings
	 *
	 * @param graphvizGraph graph
	 * @return settings
	 */
	private static Settings makeSettings(final org.graphviz.objects.Graph graphvizGraph)
	{
		final Settings settings = new Settings();
		settings.focus = graphvizGraph.getGenericGraphAttribute("");
		settings.backColor = Utils.parseColor(graphvizGraph.getGenericGraphAttribute(""));
		settings.defaultNodeImage = graphvizGraph.getGenericNodeAttribute("");
		settings.defaultEdgeImage = graphvizGraph.getGenericEdgeAttribute("");

		String attribute;

		// treebolic

		attribute = graphvizGraph.getGenericGraphAttribute("treebolic_toolbar");
		if (attribute != null)
		{
			settings.hasToolbarFlag = Boolean.valueOf(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("treebolic_statusbar");
		if (attribute != null)
		{
			settings.hasStatusbarFlag = Boolean.valueOf(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("treebolic_popupmenu");
		if (attribute != null)
		{
			settings.hasPopUpMenuFlag = Boolean.valueOf(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("treebolic_focus_on_hover");
		if (attribute != null)
		{
			settings.focusOnHoverFlag = Boolean.valueOf(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("treebolic_tooltip");
		if (attribute != null)
		{
			settings.toolTipDisplaysContentFlag = Boolean.valueOf(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("treebolic_tooltip_displays_content");
		if (attribute != null)
		{
			settings.hasToolTipFlag = Boolean.valueOf(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("treebolic_focus");
		if (attribute != null)
		{
			settings.focus = attribute;
		}

		attribute = graphvizGraph.getGenericGraphAttribute("treebolic_xmoveto");
		if (attribute != null)
		{
			settings.xMoveTo = Float.parseFloat(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("treebolic_ymoveto");
		if (attribute != null)
		{
			settings.yMoveTo = Float.parseFloat(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("treebolic_xshift");
		if (attribute != null)
		{
			settings.xShift = Float.parseFloat(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("treebolic_yshift");
		if (attribute != null)
		{
			settings.yShift = Float.parseFloat(attribute);
		}

		// tree

		attribute = graphvizGraph.getGenericGraphAttribute("tree_backcolor");
		if (attribute != null)
		{
			settings.backColor = Utils.parseColor(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_forecolor");
		if (attribute != null)
		{
			settings.foreColor = Utils.parseColor(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_expansion");
		if (attribute != null)
		{
			settings.expansion = Float.parseFloat(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_sweep");
		if (attribute != null)
		{
			settings.sweep = Float.parseFloat(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_orientation");
		if (attribute != null)
		{
			settings.orientation = attribute;
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_preserve_orientation");
		if (attribute != null)
		{
			settings.preserveOrientationFlag = Boolean.valueOf(attribute);
		}

		attribute = graphvizGraph.getGenericEdgeAttribute("edges_arcs");
		if (attribute != null)
		{
			settings.edgesAsArcsFlag = Boolean.valueOf(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_fontface");
		if (attribute != null)
		{
			settings.fontFace = attribute;
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_fontsize");
		if (attribute != null)
		{
			settings.fontSize = Integer.parseInt(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_scale_fonts");
		if (attribute != null)
		{
			settings.downscaleFontsFlag = Boolean.valueOf(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_font_scaler");
		if (attribute != null)
		{
			settings.fontDownscaler = Utils.stringToFloats(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_scale_images");
		if (attribute != null)
		{
			settings.downscaleImagesFlag = Boolean.valueOf(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_image_scaler");
		if (attribute != null)
		{
			settings.imageDownscaler = Utils.stringToFloats(attribute);
		}

		attribute = graphvizGraph.getGenericGraphAttribute("tree_img_src");
		if (attribute != null)
		{
			settings.backgroundImageFile = attribute;
		}

		// nodes

		attribute = graphvizGraph.getGenericNodeAttribute("nodes_backcolor");
		if (attribute != null)
		{
			settings.nodeBackColor = Utils.parseColor(attribute);
		}

		attribute = graphvizGraph.getGenericNodeAttribute("nodes_forecolor");
		if (attribute != null)
		{
			settings.nodeForeColor = Utils.parseColor(attribute);
		}

		attribute = graphvizGraph.getGenericNodeAttribute("nodes_img_src");
		if (attribute != null)
		{
			settings.defaultNodeImage = attribute;
		}

		attribute = graphvizGraph.getGenericNodeAttribute("nodes_default_treeedge_img_src");
		if (attribute != null)
		{
			settings.defaultTreeEdgeImage = attribute;
		}

		attribute = graphvizGraph.getGenericNodeAttribute("nodes_default_treeedge_color");
		if (attribute != null)
		{
			settings.treeEdgeColor = Utils.parseColor(attribute);
		}

		settings.treeEdgeStyle = Utils.parseStyle(graphvizGraph.getGenericNodeAttribute("nodes_default_treeedge_stroke"), graphvizGraph.getGenericNodeAttribute("nodes_default_treeedge_fromterminator"),  //$NON-NLS-2$
				graphvizGraph.getGenericNodeAttribute("nodes_default_treeedge_toterminator"), graphvizGraph.getGenericNodeAttribute("nodes_default_treeedge_line"), graphvizGraph.getGenericNodeAttribute("nodes_default_treeedge_hidden"));  //$NON-NLS-2$ //$NON-NLS-3$

		attribute = graphvizGraph.getGenericNodeAttribute("nodes_border");
		if (attribute != null)
		{
			settings.borderFlag = Boolean.valueOf(attribute);
		}
		attribute = graphvizGraph.getGenericNodeAttribute("nodes_ellipsize");
		if (attribute != null)
		{
			settings.ellipsizeFlag = Boolean.valueOf(attribute);
		}
		attribute = graphvizGraph.getGenericNodeAttribute("nodes_label_max_lines");
		if (attribute != null)
		{
			settings.labelMaxLines = Integer.valueOf(attribute);
		}
		attribute = graphvizGraph.getGenericNodeAttribute("nodes_label_extra_line_factor");
		if (attribute != null)
		{
			settings.labelExtraLineFactor = Float.valueOf(attribute);
		}

		// edges

		attribute = graphvizGraph.getGenericEdgeAttribute("edges_default_edge_img_src");
		if (attribute != null)
		{
			settings.defaultEdgeImage = attribute;
		}

		attribute = graphvizGraph.getGenericEdgeAttribute("edges_default_edge_color");
		if (attribute != null)
		{
			settings.edgeColor = Utils.parseColor(attribute);
		}

		settings.edgeStyle = Utils.parseStyle(graphvizGraph.getGenericEdgeAttribute("edges_default_edge_stroke"), graphvizGraph.getGenericEdgeAttribute("edges_default_edge_fromterminator"),  //$NON-NLS-2$
				graphvizGraph.getGenericEdgeAttribute("edges_default_edge_toterminator"), graphvizGraph.getGenericEdgeAttribute("edges_default_edge_line"), graphvizGraph.getGenericEdgeAttribute("edges_default_edge_hidden"));  //$NON-NLS-2$ //$NON-NLS-3$

		return settings;
	}

	/**
	 * Dump
	 *
	 * @param reader reader
	 */
	private static void dump(final FileReader reader)
	{
		try
		{
			// parser
			final Parser parser = new Parser();
			parser.parse(reader);

			// graph
			for (final org.graphviz.objects.Graph graph : parser.getGraphs())
			{
				System.out.println(graph.toString());
				System.out.print("%%\n");
			}
		}
		catch (final ParseException e)
		{
			System.err.println("Error while processing " + e.getMessage());
		}
	}

	/**
	 * Main
	 *
	 * @param args file to parse
	 */
	static public void main(final String[] args)
	{
		final File file = new File(args[0]);
		FileReader reader;
		try
		{
			reader = new FileReader(file);
			GraphvizParser.dump(reader);
		}
		catch (final FileNotFoundException e)
		{
			System.err.println("Could not find " + file.getAbsolutePath());
		}
	}
}
