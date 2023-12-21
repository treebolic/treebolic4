/***************************************************************
 *    JPGD - Java-based Parser for Graphviz Documents
 *    Copyright : (c) 2006  Alexander Merz
 * <p>
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License.
 * <p>
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 * <p>
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.graphviz.objects;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * This class represents a graph. A Graph contains Nodes, Edges and Subgraphs
 *
 * @author Alexander
 * @version $Id: Graph.java,v 1.5 2006/04/05 22:39:23 Alexander Exp $
 */
public class Graph
{

	/* Constants */
	/**
	 * Constant for an undirected graph
	 */
	public static final int UNDIRECTED = 1;

	/**
	 * Constant for a directed graph
	 */
	public static final int DIRECTED = 2;

	/**
	 * Identifier object for the graph
	 */
	@Nullable
	private Id id = null;

	/**
	 * Holds all Node objects of this graph
	 */
	private final ArrayList<Node> nodes = new ArrayList<>();

	/**
	 * Holds all Edge objects of this graph
	 */
	private final ArrayList<Edge> edges = new ArrayList<>();

	/**
	 * Holds all Subgraphs
	 */
	private final ArrayList<Graph> graphs = new ArrayList<>();

	/**
	 * The type of this graph
	 */
	private int type = 0;

	/**
	 * Whether the graph is strict or not
	 */
	private boolean strictgraph = false;

	/**
	 * Holds the graph attributes
	 */
	private final Hashtable<String, String> attributes = new Hashtable<>();

	/**
	 * Holds generic attributes for all edges
	 */
	private final Hashtable<String, String> genericEdgeAttributes = new Hashtable<>();

	/**
	 * Holds generic attributes for all nodes
	 */
	private final Hashtable<String, String> genericNodeAttributes = new Hashtable<>();

	/**
	 * Holds generic attributes for graphs
	 */
	private final Hashtable<String, String> genericGraphAttributes = new Hashtable<>();

	/**
	 * Sets a generic attribute for all edges of this graph. This attribute is NOT additionally stored in the Edge objects.
	 *
	 * @param key   the attribute name
	 * @param value the attribute value
	 */
	public void addGenericEdgeAttribute(final String key, final String value)
	{
		this.genericEdgeAttributes.put(key, value);
	}

	/**
	 * Returns a generic attribute of an edge
	 *
	 * @param key the attribute name
	 * @return the value of the attribute or null, if the attribute does not exist.
	 */
	public String getGenericEdgeAttribute(final String key)
	{
		return this.genericEdgeAttributes.get(key);
	}

	/**
	 * Sets a generic attribute for clusters of this graph. This attribute is NOT additionally stored in the Edge objects.
	 *
	 * @param key   the attribute name
	 * @param value the attribute value
	 */
	public void addGenericGraphAttribute(final String key, final String value)
	{
		this.genericGraphAttributes.put(key, value);
	}

	/**
	 * Returns a generic attribute of the clusters in the graph
	 *
	 * @param key the attribute name
	 * @return the value of the attribute or null, if the attribute does not exist.
	 */
	public String getGenericGraphAttribute(final String key)
	{
		return this.genericGraphAttributes.get(key);
	}

	/**
	 * Sets a generic attribute for all nodes of this graph This attribute is NOT additionally stored in the Node objects.
	 *
	 * @param key   the attribute name
	 * @param value the attribute value
	 */
	public void addGenericNodeAttribute(final String key, final String value)
	{
		this.genericNodeAttributes.put(key, value);
	}

	/**
	 * Returns a generic attribute of a node
	 *
	 * @param key the attribute name
	 * @return the value of the attribute or null, if the attribute does not exist.
	 */
	public String getGenericNodeAttribute(final String key)
	{
		return this.genericNodeAttributes.get(key);
	}

	/**
	 * Adds a graph attribute. This attribute is NOT inherited to the attributes of sub graphs.
	 *
	 * @param key   the name of the attribute
	 * @param value the value of the attribute
	 */
	public void addAttribute(final String key, final String value)
	{
		this.attributes.put(key, value);
	}

	/**
	 * Returns an attribute of the Graph or null if not found.
	 *
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public String getAttribute(final String key)
	{
		return this.attributes.get(key);
	}

	/**
	 * Adds a Node object to the graph
	 *
	 * @param n the Node Object to add
	 */
	public void addNode(final Node n)
	{
		this.nodes.add(n);
	}

	/**
	 * Adds an Edge object to the graph
	 *
	 * @param e the Edge object to add
	 */
	public void addEdge(final Edge e)
	{
		this.edges.add(e);
	}

	/**
	 * Returns the ID object to the Graph
	 *
	 * @return the ID object
	 */
	@Nullable
	public Id getId()
	{
		return this.id;
	}

	/**
	 * Sets the ID object for this graph
	 *
	 * @param id id
	 */
	public void setId(@Nullable final Id id)
	{
		this.id = id;
	}

	/**
	 * Returns the type of the graph
	 *
	 * @return the type of the graph
	 * @see Graph#DIRECTED
	 * @see Graph#UNDIRECTED
	 */
	public int getType()
	{
		return this.type;
	}

	/**
	 * Sets the type of the graph
	 *
	 * @param type the type of the graph
	 * @see Graph#DIRECTED
	 * @see Graph#UNDIRECTED
	 */
	public void setType(final int type)
	{
		if (!this.graphs.isEmpty())
		{
			for (@NonNull Graph graph : this.graphs)
			{
				graph.setType(type);
			}
		}
		this.type = type;
	}

	/**
	 * Returns whether the graph is strict or not
	 *
	 * @return the static state
	 */
	public boolean isStrict()
	{
		return this.strictgraph;
	}

	/**
	 * Sets if the graph is strict or not.
	 *
	 * @param isStrict strict flag
	 */
	public void setStrict(final boolean isStrict)
	{
		this.strictgraph = isStrict;
	}

	/**
	 * Returns a String representation of the graph
	 *
	 * @return the string representation
	 */
	@NonNull
	@Override
	public String toString()
	{
		@NonNull final StringBuilder r = new StringBuilder();
		if (isStrict())
		{
			r.append("strict ");
		}
		if (Graph.DIRECTED == getType())
		{
			r.append("digraph ");
		}
		else
		{
			r.append("graph ");
		}
		assert this.id != null;
		if (!this.id.getId().isEmpty())
		{
			r.append(this.id.getId());
		}
		else if (!this.id.getLabel().isEmpty())
		{
			r.append(this.id.getLabel());
		}
		r.append(" {\n");

		if (!this.attributes.isEmpty())
		{
			final Enumeration<String> e = this.attributes.keys();
			String k;
			while (e.hasMoreElements())
			{
				k = e.nextElement();
				r.append(k);
				if (!this.attributes.get(k).isEmpty())
				{
					r.append("=");
					if (!this.attributes.get(k).contains(" "))
					{
						r.append(this.attributes.get(k));
					}
					else
					{
						r.append("\"");
						r.append(this.attributes.get(k));
						r.append("\"");
					}
				}
				r.append(";\n");
			}
		}

		if (!this.genericNodeAttributes.isEmpty())
		{
			final Enumeration<String> e = this.genericNodeAttributes.keys();
			String k;
			r.append("node [");
			while (e.hasMoreElements())
			{
				k = e.nextElement();
				r.append(k);
				if (!this.genericNodeAttributes.get(k).isEmpty())
				{
					r.append("=");
					if (!this.genericNodeAttributes.get(k).contains(" "))
					{
						r.append(this.genericNodeAttributes.get(k));
					}
					else
					{
						r.append("\"");
						r.append(this.genericNodeAttributes.get(k));
						r.append("\"");
					}
				}
				r.append(", ");
			}
			r.delete(r.length() - 2, r.length());
			r.append("];\n");
		}

		if (!this.genericEdgeAttributes.isEmpty())
		{
			final Enumeration<String> e = this.genericEdgeAttributes.keys();
			String k;
			r.append("edge [");
			while (e.hasMoreElements())
			{
				k = e.nextElement();
				r.append(k);
				if (!this.genericEdgeAttributes.get(k).isEmpty())
				{
					r.append("=");
					if (!this.genericEdgeAttributes.get(k).contains(" "))
					{
						r.append(this.genericEdgeAttributes.get(k));
					}
					else
					{
						r.append("\"");
						r.append(this.genericEdgeAttributes.get(k));
						r.append("\"");
					}
				}
				r.append(", ");
			}
			r.delete(r.length() - 2, r.length());
			r.append("];\n");
		}

		if (!this.genericGraphAttributes.isEmpty())
		{
			final Enumeration<String> e = this.genericGraphAttributes.keys();
			String k;
			r.append("graph [");
			while (e.hasMoreElements())
			{
				k = e.nextElement();
				r.append(k);
				if (!this.genericGraphAttributes.get(k).isEmpty())
				{
					r.append("=");
					if (!this.genericGraphAttributes.get(k).contains(" "))
					{
						r.append(this.genericGraphAttributes.get(k));
					}
					else
					{
						r.append("\"");
						r.append(this.genericGraphAttributes.get(k));
						r.append("\"");
					}
				}
				r.append(", ");
			}
			r.delete(r.length() - 2, r.length());
			r.append("];\n");
		}
		if (!this.nodes.isEmpty())
		{
			for (@NonNull Node node : this.nodes)
			{
				r.append(node);
			}
		}
		if (!this.edges.isEmpty())
		{
			for (@NonNull Edge edge : this.edges)
			{
				r.append(edge);
			}
		}
		if (!this.graphs.isEmpty())
		{
			for (@NonNull Graph graph : this.graphs)
			{
				r.append(graph);
				r.append("\n");
			}
		}
		r.append(" }");
		return r.toString();
		/*
		 * return "Graph: " + ((id!=null)?id.toString():"") + ", "+ "static="+Boolean.toString(strictgraph)+", "+ ((type==DIRECTED)?"directed":"undirected")+
		 * ", Attributes: "+attributes.toString()+ ", Nodes: "+nodes.toString()+ ", Edges: "+edges.toString()+ ", Subgraphs: "+graphs.toString();
		 */
	}

	/**
	 * Returns a list of all sub graphs.
	 *
	 * @return the subgraphs
	 */
	@NonNull
	public ArrayList<Graph> getSubgraphs()
	{
		return this.graphs;
	}

	/**
	 * Adds a sub graph to a graph
	 *
	 * @param graph graph
	 */
	public void addSubgraph(final Graph graph)
	{
		this.graphs.add(graph);
	}

	/**
	 * Tries to find a node of a Graph depending on the given ID object.
	 *
	 * @param id the id object to identify the node
	 * @return the node or null if not found
	 */
	@Nullable
	public Node findNode(@NonNull final Id id)
	{
		@Nullable Node n;
		@Nullable Id nid;

		for (Node node : this.nodes)
		{
			n = node;
			nid = n.getId();
			if (nid != null && nid.isEqual(id))
			{
				return n;
			}
		}
		Graph g;
		for (Graph graph : this.graphs)
		{
			g = graph;
			n = g.findNode(id);
			if (n != null)
			{
				return n;
			}
		}
		return null;
	}

	/**
	 * Returns all Nodes of the graph. If the graph contains subgraphs, you can use the onlyGraph parameter to decide if nodes defined in subgraphs should be
	 * added to the list also.
	 *
	 * @param onlyGraph if true, also include nodes in subgraphs also, else exclude them
	 * @return the nodes of the graph
	 */
	@NonNull
	public ArrayList<Node> getNodes(final boolean onlyGraph)
	{
		if (onlyGraph)
		{
			return this.nodes;
		}
		else
		{
			@NonNull final ArrayList<Node> n = new ArrayList<>(this.nodes);
			Graph g;
			for (Graph graph : this.graphs)
			{
				g = graph;
				n.addAll(g.getNodes(false));
			}
			return n;
		}
	}

	/**
	 * Returns all attributes of the graph.
	 *
	 * @return the attributes
	 */
	@NonNull
	public Hashtable<String, String> getAttributes()
	{
		return this.attributes;
	}

	/**
	 * Returns all edges of this graph.
	 *
	 * @return a list of Edge objects
	 */
	@NonNull
	public ArrayList<Edge> getEdges()
	{
		return this.edges;
	}
}
