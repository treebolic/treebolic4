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

import treebolic.annotations.NonNull;

/**
 * This object is used to identify graphs and nodes Note, that an object may contain an id or a label.
 *
 * @author Alexander Merz
 * @version $Id: Id.java,v 1.3 2006/03/20 16:45:41 Alexander Exp $
 */

public class Id
{
	/**
	 * The id
	 */
	private String id = "";

	/**
	 * The label
	 */
	private String label = "";

	/**
	 * Returns the ID of the object Empty string means, that this object has no ID.
	 *
	 * @return the object id
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * Sets the object id. An empty string deletes the ID.
	 *
	 * @param id id
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * Returns the label of the object
	 *
	 * @return the object label
	 */
	public String getLabel()
	{
		return this.label;
	}

	/**
	 * Sets the label of the object
	 *
	 * @param label label
	 */
	public void setLabel(final String label)
	{
		this.label = label;
	}

	/**
	 * Compares the Ids. Two Id objects are equal, if
	 * <ul>
	 * <li>both are the same, or</li>
	 * <li>Id and Label attribute are equal</li>
	 * <li>both ids are empty and the labels are equal</li>
	 * </ul>
	 *
	 * @param eid id
	 * @return true if both Ids are equal
	 */
	public boolean isEqual(@NonNull final Id eid)
	{
		if (eid == this)
		{
			return true;
		}
		else
		{
			// ID and label are the same
			if (eid.getId().equals(this.id) && eid.getLabel().equals(this.label))
			{
				return true;
			}
			// both ids are empty, but labels are the same
			else if (eid.getId().equals("") && this.id.equals("") && eid.getLabel().equals(this.label))
			{
				return true;
			}
			// both ids are empty, and label differs
			else if (eid.getId().equals("") && this.id.equals("") && !eid.getLabel().equals(this.label))
			{
				return false;
			}
			return false;
		}
	}

	/**
	 * Returns the String representation of this ID
	 *
	 * @return the string representation
	 */
	@NonNull
	@Override
	public String toString()
	{
		return "(id=" + this.id + ", label=" + this.label + ")";
	}
}
