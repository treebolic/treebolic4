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

/**
 * This class represents a node in a graph.
 *
 * @author Alexander Merz
 * @version $Id: Node.java,v 1.4 2006/04/05 22:39:30 Alexander Exp $
 */
public class Node
{
	/**
	 * The id object
	 */
	private Id id = null;

	/**
	 * Holds the attributes of the node
	 */
	private final Hashtable<String, String> attr = new Hashtable<>();

	/**
	 * If true this node represents a subgraph
	 */
	private boolean isSubgraph = false;

	/**
	 * Returns the id object for the node
	 *
	 * @return the Id object
	 */
	public Id getId()
	{
		return this.id;
	}

	/**
	 * Sets the Id object for this node
	 *
	 * @param id id
	 */
	public void setId(final Id id)
	{
		this.id = id;
	}

	/**
	 * Returns the attribute of the node
	 *
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public String getAttribute(final String key)
	{
		return this.attr.get(key);
	}

	/**
	 * Sets the attribute of the node
	 *
	 * @param key   the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setAttribute(final String key, final String value)
	{
		this.attr.put(key, value);
	}

	/**
	 * Returns all attributes of the edge.
	 *
	 * @return the edge attributes.
	 */
	public Hashtable<String, String> getAttributes()
	{
		return this.attr;
	}

	/**
	 * Returns true, if the node object represents a subgraph.
	 *
	 * @return true or false
	 */
	public boolean isSubgraph()
	{
		return this.isSubgraph;
	}

	/**
	 * Sets, if the node represents a subgraph
	 *
	 * @param isSubgraph true if subgraph is used in edge operation
	 */
	public void representsSubgraph(final boolean isSubgraph)
	{
		this.isSubgraph = isSubgraph;
	}

	/**
	 * Returns a string representation of this node If the node is a subgraph, an empty string will be returned
	 *
	 * @return the string representation
	 */
	@Override
	public String toString()
	{
		if (isSubgraph())
		{
			return "";
		}
		final StringBuilder r = new StringBuilder();
		if (!this.id.getId().equals(""))
		{
			r.append(this.id.getId());
		}
		else if (!this.id.getLabel().equals(""))
		{
			r.append("\"");
			r.append(this.id.getLabel());
			r.append("\"");
		}
		if (this.attr.size() > 0)
		{
			r.append(" [");
			final Enumeration<String> e = this.attr.keys();
			while (e.hasMoreElements())
			{
				final String k = e.nextElement();
				r.append(k);
				if (!this.attr.get(k).equals(""))
				{
					r.append("=");
					if (!this.attr.get(k).contains(" "))
					{
						r.append(this.attr.get(k));
					}
					else
					{
						r.append("\"");
						r.append(this.attr.get(k));
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
