/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.glue;

/**
 * Rectangle (geometry)
 *
 * @author Bernard Bou
 */
public class Rectangle2D implements treebolic.glue.iface.Rectangle2D<Point2D, Rectangle2D>
{
	/**
	 * Constructor
	 *
	 * @param x x
	 * @param y y
	 * @param w width
	 * @param h height
	 */
	public Rectangle2D(final int x, final int y, final int w, final int h)
	{
		throw new NotImplementedException();
	}

	/**
	 * Constructor
	 */
	public Rectangle2D()
	{
		throw new NotImplementedException();
	}

	@Override
	public void setFrame(final double x, final double y, final double width, final double height)
	{
		throw new NotImplementedException();
	}

	@Override
	public double getX()
	{
		throw new NotImplementedException();
	}

	@Override
	public double getY()
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
	public double getMinX()
	{
		throw new NotImplementedException();
	}

	@Override
	public double getMinY()
	{
		throw new NotImplementedException();
	}

	@Override
	public boolean intersects(final Rectangle2D rect)
	{
		throw new NotImplementedException();
	}

	@Override
	public int outcode(final Point2D point)
	{
		throw new NotImplementedException();
	}
}
