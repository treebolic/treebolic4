/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

import treebolic.annotations.NonNull;

/**
 * Arc2D, derived from awt's Arc2D
 *
 * @author Bernard Bou
 */
public class Arc2D extends java.awt.geom.Arc2D.Double implements treebolic.glue.iface.Arc2D<Point2D>
{
	private static final long serialVersionUID = -8430455905667334344L;

	private boolean counterclockwise;

	/**
	 * Constructor
	 */
	public Arc2D()
	{
		super();
		super.setArcType(java.awt.geom.Arc2D.OPEN);
	}

	// public double getX();

	// public double getY();

	// public double getWidth();

	// public double getHeight();

	// public void setFrameFromCenter(final double x1, final double y1, final double x2, final double y2);

	// public void setAngleExtent(final double extent);

	// public void setAngleStart(final double start);

	// public double getAngleStart();

	// public double getAngleExtent();

	@Override
	public void setAngles(final Point2D from, final Point2D to)
	{
		super.setAngles(from, to);
	}

	@Override
	public void setCounterclockwise(final boolean flag)
	{
		this.counterclockwise = flag;
	}

	// public double getCenterX();

	// public double getCenterY();

	@NonNull
	@Override
	public Point2D getStartPoint()
	{
		return new Point2D(super.getStartPoint());
	}

	@NonNull
	@Override
	public Point2D getEndPoint()
	{
		return new Point2D(super.getEndPoint());
	}

	// public boolean containsAngle(final double angle);

	@Override
	public boolean getCounterclockwise()
	{
		return this.counterclockwise;
	}

	@Override
	public String toString()
	{
		return String.format("arc2d x=%.0f y=%.0f w=%.0f, h=%.0f, s=%.1f°, e=%.1f° ccw=%s", this.x, this.y, this.width, this.height, this.start, this.extent, this.counterclockwise);
	}
}
