/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

import treebolic.annotations.NonNull;

/**
 * Point2D, derived from awt's Point2D
 *
 * @author Bernard Bou
 */
public class Point2D extends java.awt.geom.Point2D.Double implements treebolic.glue.iface.Point2D
{
	private static final long serialVersionUID = 2080063772680665125L;

	/**
	 * Constructor
	 *
	 * @param x x
	 * @param y y
	 */
	public Point2D(final double x, final double y)
	{
		super(x, y);
	}

	/**
	 * Constructor
	 *
	 * @param p awt.geom.Point2D
	 */
	public Point2D(@NonNull final java.awt.geom.Point2D p)
	{
		super(p.getX(), p.getY());
	}

	// public double getX();

	// public double getY();

	@Override
	public String toString()
	{
		return String.format("%.1f, %.1f", this.x, this.y);
	}
}
