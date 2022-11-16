/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

/**
 * Point, derived from awt's Point
 *
 * @author Bernard Bou
 */
public class Point extends java.awt.Point implements treebolic.glue.iface.Point
{
	private static final long serialVersionUID = 1086461697280710461L;

	// public int x;

	// public int y;

	public Point(final int x, final int y)
	{
		this.x = x;
		this.y = y;
	}
}
