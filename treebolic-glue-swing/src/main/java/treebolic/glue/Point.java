/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.glue;

/**
 * Point, derived from awt's Point
 *
 * @author Bernard Bou
 */
public class Point extends java.awt.Point implements treebolic.glue.iface.Point
{
	private static final long serialVersionUID = 4L;

	/**
	 * Constructor
	 *
	 * @param x x
	 * @param y y
	 */
	public Point(final int x, final int y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public int x()
	{
		return x;
	}

	@Override
	public int y()
	{
		return y;
	}
}
