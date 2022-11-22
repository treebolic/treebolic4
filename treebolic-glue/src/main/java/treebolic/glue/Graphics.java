/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

import treebolic.annotations.NonNull;

/**
 * Graphics context
 *
 * @author Bernard Bou
 */
public class Graphics implements treebolic.glue.iface.Graphics<Color, Image>
{
	// D R A W

	@Override
	public void drawBackgroundColor(final Color color, final int left, final int top, final int width, final int height)
	{
		throw new NotImplementedException();
	}

	@Override
	public void drawLine(final int x, final int y, final int x2, final int y2)
	{
		throw new NotImplementedException();
	}

	@Override
	public void drawArc(final float x, final float y, final float w, final float h, final float startAngle, final float extentAngle)
	{
		throw new NotImplementedException();
	}

	@Override
	public void drawPolyline(final int[] x, final int[] y, final int length)
	{
		throw new NotImplementedException();
	}

	@Override
	public void fillRectangle(final int left, final int top, final int width, final int height)
	{
		throw new NotImplementedException();
	}

	@Override
	public void fillRoundRectangle(final int x, final int y, final int w, final int h, final int rx, final int ry)
	{
		throw new NotImplementedException();
	}

	@Override
	public void drawRoundRectangle(final int x, final int y, final int w, final int h, final int rx, final int ry)
	{
		throw new NotImplementedException();
	}

	@Override
	public void fillPolygon(final int[] x, final int[] y, final int length)
	{
		throw new NotImplementedException();
	}

	@Override
	public void drawPolygon(final int[] x, final int[] y, final int length)
	{
		throw new NotImplementedException();
	}

	@Override
	public void fillOval(final float x, final float y, final float xradius, final float yradius)
	{
		throw new NotImplementedException();
	}

	@Override
	public void drawOval(final float x, final float y, final float xradius, final float yradius)
	{
		throw new NotImplementedException();
	}

	@Override
	public void drawString(final String str, final int x, final int y)
	{
		throw new NotImplementedException();
	}

	@Override
	public void drawImage(final Image image, final int x, final int y)
	{
		throw new NotImplementedException();
	}

	@Override
	public void drawImage(final Image image, final int x, final int y, final int w, final int h)
	{
		throw new NotImplementedException();
	}

	// S E T T I N G S

	@Override
	public void setColor(final Color color)
	{
		throw new NotImplementedException();
	}

	@NonNull
	@Override
	public Color getColor()
	{
		throw new NotImplementedException();
	}

	@Override
	public void setFont(final String fontface, final int style)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setTextSize(final float size)
	{
		throw new NotImplementedException();
	}

	@Override
	public int getDescent()
	{
		throw new NotImplementedException();
	}

	@Override
	public int getAscent()
	{
		throw new NotImplementedException();
	}

	@Override
	public int stringWidth(final String string)
	{
		throw new NotImplementedException();
	}

	@Override
	public void setStroke(final int stroke0, final int width)
	{
		throw new NotImplementedException();
	}

	@Override
	public void pushStroke()
	{
		throw new NotImplementedException();
	}

	@Override
	public void popStroke()
	{
		throw new NotImplementedException();
	}

	// T R A N S F O R M

	@Override
	public void pushMatrix()
	{
		throw new NotImplementedException();
	}

	@Override
	public void popMatrix()
	{
		throw new NotImplementedException();
	}

	@Override
	public void translate(final float x, final float y)
	{
		throw new NotImplementedException();
	}

	@Override
	public void rotate(final float theta, final float x, final float y)
	{
		throw new NotImplementedException();
	}

	@Override
	public void scale(final float factor, final float x, final float y)
	{
		throw new NotImplementedException();
	}
}
