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

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
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
	@Nullable
	public static Model parseModel(@NonNull final URL url)
	{
		try (@NonNull InputStream is = url.openStream(); @NonNull InputStreamReader reader = new InputStreamReader(is))
		{
			@NonNull final Parser parser = new Parser();
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
	@Nullable
	public static Model parseModel(@NonNull final String location)
	{
		try (@NonNull InputStream is = new FileInputStream(location); @NonNull InputStreamReader reader = new InputStreamReader(is))
		{
			@NonNull final Parser parser = new Parser();
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
	@Nullable
	public static Tree parseTree(@NonNull final URL url)
	{
		try (@NonNull InputStream is = url.openStream(); @NonNull InputStreamReader reader = new InputStreamReader(is))
		{
			@NonNull final Parser parser = new Parser();
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
	@Nullable
	public static Tree parseTree(@NonNull final String location)
	{
		try (@NonNull InputStream is = new FileInputStream(location); @NonNull InputStreamReader reader = new InputStreamReader(is))
		{
			@NonNull final Parser parser = new Parser();
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
	@NonNull
	private static Model makeModel(@NonNull final org.graphviz.objects.Graph graphvizGraph)
	{
		@NonNull final Tree tree = GraphvizParser.makeTree(graphvizGraph);
		@NonNull final Settings settings = GraphvizParser.makeSettings(graphvizGraph);
		return new Model(tree, settings);
	}

	/**
	 * Make tree
	 *
	 * @param graphvizGraph graphviz graph graph
	 * @return tree
	 */
	@NonNull
	private static Tree makeTree(@NonNull final org.graphviz.objects.Graph graphvizGraph)
	{
		@NonNull final Graph graph = GraphvizParser.parseGraph(graphvizGraph);
		// System.out.println(graph.toString());

		// determine root node
		@Nullable GraphNode rootNode = null;
		@Nullable final List<GraphNode> graphRootNodes = graph.getNodesWithZeroDegree();
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
	@NonNull
	private static Graph parseGraph(@NonNull final org.graphviz.objects.Graph graphvizGraph)
	{
		// treebolic graph
		@NonNull final MutableGraph graph = new MutableGraph();

		// nodes
		@NonNull final Hashtable<String, MutableGraphNode> nodesById = new Hashtable<>();
		for (@NonNull final Node graphvizNode : graphvizGraph.getNodes(false))
		{
			@NonNull final String id = GraphvizParser.nodeId(graphvizNode);

			// node
			@NonNull final MutableGraphNode node = new MutableGraphNode(id);
			nodesById.put(id, node);
			graph.add(node);

			// node attributes

			// label
			@Nullable String attribute;
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
				@NonNull final MountPoint.Mounting mountPoint = new MountPoint.Mounting();
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
		for (@NonNull final Edge graphvizEdge : graphvizGraph.getEdges())
		{
			// get ends
			@Nullable final PortNode from = graphvizEdge.getSource();
			assert from != null;
			@Nullable final PortNode to = graphvizEdge.getTarget();
			assert to != null;

			// get end ids
			@Nullable Node from2 = from.getNode();
			assert from2 != null;
			@NonNull final String fromId = GraphvizParser.nodeId(from2);

			@Nullable Node to2 = to.getNode();
			assert to2 != null;
			@NonNull final String toId = GraphvizParser.nodeId(to2);

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
			@Nullable Boolean isTreeEdge = null;
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
			@NonNull final MutableEdge edge = new MutableEdge(fromNode, toNode);
			@NonNull final GraphEdge graphEdge = new GraphEdge(fromNode, toNode, isTreeEdge);
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
	@NonNull
	private static String nodeId(@NonNull final Node graphvizNode)
	{
		@Nullable final Id graphvizId = graphvizNode.getId();
		String id = graphvizId == null ? null : graphvizId.getId();
		if (id != null && !id.isEmpty())
		{
			return id;
		}
		id = graphvizId == null ? null : graphvizId.getLabel();
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
	@Nullable
	private static String nodeLabel(@NonNull final Node graphvizNode)
	{
		String label = graphvizNode.getAttribute("label");
		if (label != null && !label.isEmpty())
		{
			return label;
		}

		@Nullable final Id graphvizId = graphvizNode.getId();
		label = graphvizId == null ? null : graphvizId.getLabel();
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
	@NonNull
	private static Settings makeSettings(@NonNull final org.graphviz.objects.Graph graphvizGraph)
	{
		@NonNull final Settings settings = new Settings();
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
	private static void dump(@NonNull final FileReader reader)
	{
		try
		{
			// parser
			@NonNull final Parser parser = new Parser();
			parser.parse(reader);

			// graph
			for (@NonNull final org.graphviz.objects.Graph graph : parser.getGraphs())
			{
				System.out.println(graph);
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
	static public void main(@NonNull final String[] args)
	{
		@NonNull final File file = new File(args[0]);
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
