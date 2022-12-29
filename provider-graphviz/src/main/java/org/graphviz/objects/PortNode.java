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

/**
 * This class is an extended Node object containing Port information of a node in an edge. A PortNode does not hold an ID object, because it is only a wrapper
 * for a node as a part of an edge.
 *
 * @author Alexander Merz
 * @version $Id: PortNode.java,v 1.3 2006/03/20 16:45:41 Alexander Exp $
 */
public class PortNode
{
	/**
	 * holds the port information
	 */
	private String port;

	/**
	 * holds the port
	 */
	private Node node;

	/**
	 * Creates an empty PortNode object
	 */
	public PortNode()
	{
		this.node = null;
		this.port = "";
	}

	/**
	 * Creates a PortNode with a given Node and empty port
	 *
	 * @param n the node object
	 */
	public PortNode(final Node n)
	{
		this.node = n;
		this.port = "";
	}

	/**
	 * Creates a PortNode with the given Node and Port
	 *
	 * @param n    the node object
	 * @param port the port
	 */
	public PortNode(final Node n, final String port)
	{
		this.node = n;
		this.port = port;
	}

	/**
	 * Returns the port of the node
	 *
	 * @return the port
	 */
	public String getPort()
	{
		return this.port;
	}

	/**
	 * Sets the port of the node
	 *
	 * @param port port
	 */
	public void setPort(final String port)
	{
		this.port = port;
	}

	/**
	 * Returns the node
	 *
	 * @return the node object
	 */
	public Node getNode()
	{
		return this.node;
	}

	/**
	 * Sets the node
	 *
	 * @param node node
	 */
	public void setNode(final Node node)
	{
		this.node = node;
	}

	/**
	 * Returns the String representation
	 *
	 * @return the string representation
	 */
	@Override
	public String toString()
	{
		return this.node.toString() + ":" + this.port;
	}

}
