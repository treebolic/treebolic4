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

import java.util.Enumeration;
import java.util.Hashtable;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * This class represents an Edge in a Graph. Each Edge has a source Node and a target Node. Both nodes may contain port information. The type attribute is
 * currently a nice-to-have because only directed edges are only allowed in 'Digraphs', and undirected in 'Graphs'.
 *
 * @author Alexander Merz
 * @version $Id: Edge.java,v 1.4 2006/04/05 22:39:16 Alexander Exp $
 */
public class Edge
{
	/**
	 * The source node object
	 */
	@Nullable
	private PortNode source;

	/**
	 * The target node object
	 */
	@Nullable
	private PortNode target;

	/**
	 * The type of the edge (directed or undirected)
	 */
	private int type;

	/**
	 * The hashtable having the attributes of this edge
	 */
	private final Hashtable<String, String> attributes = new Hashtable<>();

	/**
	 * Creates an empty edge object
	 */
	public Edge()
	{
		this.source = null;
		this.target = null;
		this.type = 0;
	}

	/**
	 * Creates an Edge
	 *
	 * @param source
	 *        the source Node
	 * @param target
	 *        the target Node
	 * @param type
	 *        the edge type
	 */
	public Edge(@Nullable final PortNode source, @Nullable final PortNode target, final int type)
	{
		this.source = source;
		this.target = target;
		this.type = type;
	}

	/**
	 * Returns the source node of the edge
	 *
	 * @return the source node
	 */
	@Nullable
	public PortNode getSource()
	{
		return this.source;
	}

	/**
	 * Sets the source node of the edge
	 *
	 * @param source
	 *        the source node
	 */
	public void setSource(@Nullable final PortNode source)
	{
		this.source = source;
	}

	/**
	 * Returns the target node of the edge
	 *
	 * @return the target node
	 */
	@Nullable
	public PortNode getTarget()
	{
		return this.target;
	}

	/**
	 * Sets the target node of the edge
	 *
	 * @param target
	 *        the target Node
	 */
	public void setTarget(@Nullable final PortNode target)
	{
		this.target = target;
	}

	/**
	 * Returns the type of the edge.
	 *
	 * @return the type of the edge
	 * @see Graph#UNDIRECTED
	 * @see Graph#DIRECTED
	 */
	public int getType()
	{
		return this.type;
	}

	/**
	 * Sets the type of the edge
	 *
	 * @param type
	 *        the type of the edge
	 * @see Graph#UNDIRECTED
	 * @see Graph#DIRECTED
	 */
	public void setType(final int type)
	{
		this.type = type;
	}

	/**
	 * Returns the value of an edge attribute
	 *
	 * @param key
	 *        the name of the attribute
	 * @return the value of the attribute
	 */
	public String getAttribute(final String key)
	{
		return this.attributes.get(key);
	}

	/**
	 * Sets the value of an attribute
	 *
	 * @param key
	 *        the name of the attribute
	 * @param value
	 *        the value of the attribute
	 */
	public void setAttribute(final String key, final String value)
	{
		this.attributes.put(key, value);
	}

	/**
	 * Returns all attributes of this edge
	 *
	 * @return the attributes
	 */
	@NonNull
	public Hashtable<String, String> getAttributes()
	{
		return this.attributes;
	}

	/**
	 * Returns the String representation of the edge
	 *
	 * @return the string representation
	 */
	@NonNull
	@Override
	public String toString()
	{
		@NonNull final StringBuilder r = new StringBuilder();
		assert this.source != null;
		Node sourceNode = this.source.getNode();
		assert sourceNode != null;
		Id sourceId = sourceNode.getId();
		assert sourceId != null;
		if (!"".equals(sourceId.getId()))
		{
			r.append(sourceId.getId());
		}
		else if (!"".equals(sourceId.getLabel()))
		{
			r.append("\"");
			r.append(sourceId.getLabel());
			r.append("\"");
		}
		if (!this.source.getPort().equals(""))
		{
			r.append(":\"");
			r.append(this.source.getPort());
			r.append("\"");
		}
		if (Graph.DIRECTED == getType())
		{
			r.append(" -> ");
		}
		else
		{
			r.append(" -- ");
		}

		assert this.target != null;
		Node targetNode = this.target.getNode();
		assert targetNode != null;
		Id targetId = targetNode.getId();
		assert targetId != null;
		if (!targetId.getId().equals(""))
		{
			r.append(targetId.getId());
		}
		else if (!targetId.getLabel().equals(""))
		{
			r.append("\"");
			r.append(targetId.getLabel());
			r.append("\"");
		}
		if (!this.target.getPort().equals(""))
		{
			r.append(":\"");
			r.append(this.target.getPort());
			r.append("\"");
		}
		if (this.attributes.size() > 0)
		{
			r.append(" [");
			final Enumeration<String> e = this.attributes.keys();
			while (e.hasMoreElements())
			{
				final String k = e.nextElement();
				r.append(k);
				if (!this.attributes.get(k).equals(""))
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
				r.append(", ");
			}
			r.delete(r.length() - 2, r.length());
			r.append("]");
		}
		r.append(";\n");
		return r.toString();
	}
}
