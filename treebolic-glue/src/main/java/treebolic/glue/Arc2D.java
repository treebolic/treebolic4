/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

/**
 * Arc 2D
 *
 * @author Bernard Bou
 */
public class Arc2D implements treebolic.glue.iface.Arc2D<Point2D>
{
	public float x;

	public float y;

	public float width;
	public float height;

	public float start;
	public float extent;

	public Arc2D()
	{
		throw new NotImplementedException();
	}

	@Override
	public void setFrameFromCenter(final double x, final double y, final double x1, final double y1)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setAngles(final Point2D from, final Point2D to)
	{
		throw new NotImplementedException();
	}

	@Override
	public double getCenterX()
	{
		throw new NotImplementedException();
	}

	@Override
	public double getCenterY()
	{
		throw new NotImplementedException();
	}

	@Override
	public double getWidth()
	{
		throw new NotImplementedException();
	}

	@Override
	public double getHeight()
	{
		throw new NotImplementedException();
	}

	@Override
	public void setCounterclockwise(final boolean flag)
	{
		throw new NotImplementedException();
	}

	@Override
	public boolean containsAngle(final double angle)
	{
		throw new NotImplementedException();
	}

	@Override
	public Point2D getStartPoint()
	{
		throw new NotImplementedException();
	}

	@Override
	public Point2D getEndPoint()
	{
		throw new NotImplementedException();
	}

	@Override
	public double getAngleExtent()
	{
		throw new NotImplementedException();
	}

	@Override
	public void setAngleExtent(final double extent)
	{
		throw new NotImplementedException();
	}

	@Override
	public double getAngleStart()
	{
		throw new NotImplementedException();
	}

	@Override
	public void setAngleStart(final double start)
	{
		throw new NotImplementedException();
	}

	@Override
	public boolean getCounterclockwise()
	{
		throw new NotImplementedException();
	}

	@Override
	public String toString()
	{
		throw new NotImplementedException();
	}
}
