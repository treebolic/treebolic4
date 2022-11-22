/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Color, embeds awt's Color
 *
 * @author Bernard Bou
 */
public class Color implements treebolic.glue.iface.Color<Color>, Serializable
{
	private static final long serialVersionUID = 5704334480899935769L;

	/**
	 * White
	 */
	public static final Color WHITE = new Color(java.awt.Color.WHITE);

	/**
	 * Black
	 */
	public static final Color BLACK = new Color(java.awt.Color.BLACK);

	/**
	 * Red
	 */
	public static final Color RED = new Color(java.awt.Color.RED);

	/**
	 * Green
	 */
	public static final Color GREEN = new Color(java.awt.Color.GREEN);

	/**
	 * Blue
	 */
	public static final Color BLUE = new Color(java.awt.Color.BLUE);

	/**
	 * Orange
	 */
	public static final Color ORANGE = new Color(java.awt.Color.ORANGE);

	/**
	 * Yellow
	 */
	public static final Color YELLOW = new Color(java.awt.Color.YELLOW);

	/**
	 * Pink
	 */
	public static final Color PINK = new Color(java.awt.Color.PINK);

	/**
	 * Cyan
	 */
	public static final Color CYAN = new Color(java.awt.Color.CYAN);

	/**
	 * Magenta
	 */
	public static final Color MAGENTA = new Color(java.awt.Color.MAGENTA);

	/**
	 * Grey
	 */
	public static final Color GRAY = new Color(java.awt.Color.GRAY);

	/**
	 * Light Grey
	 */
	public static final Color LIGHT_GRAY = new Color(java.awt.Color.LIGHT_GRAY);

	/**
	 * Dark grey
	 */
	public static final Color DARK_GRAY = new Color(java.awt.Color.DARK_GRAY);

	/**
	 * AWT color
	 */
	@Nullable
	transient public java.awt.Color color;

	/**
	 * Constructor from java.awt.color
	 *
	 * @param color java.awt.color
	 */
	public Color(@Nullable final java.awt.Color color)
	{
		this.color = color;
	}

	/**
	 * Constructor
	 *
	 * @param rgb rgb int value
	 */
	public Color(final int rgb)
	{
		this.color = new java.awt.Color(rgb);
	}

	/**
	 * No color constructor
	 */
	public Color()
	{
		this.color = null;
	}

	/**
	 * Constructor
	 *
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	@Override
	public void set(final int r, final int g, final int b)
	{
		this.color = new java.awt.Color(r, g, b);
	}

	@Override
	public void set(final int rgb)
	{
		this.color = new java.awt.Color(rgb);
	}

	@Override
	public void parse(final String string)
	{
		this.color = java.awt.Color.decode("0x" + string);
	}

	private static final float DARKERFACTOR = 0.85F;

	/*
	 * (non-Javadoc)
	 * @see treebolic.glue.iface.Color#makeBrighter()
	 */
	@NonNull
	@Override
	public Color makeBrighter()
	{
		if (this.color == null)
		{
			return new Color();
		}

		int r = this.color.getRed();
		int g = this.color.getGreen();
		int b = this.color.getBlue();
		final int alpha = this.color.getAlpha();

		final int i = (int) (1.0 / (1.0 - Color.DARKERFACTOR));
		if (r == 0 && g == 0 && b == 0)
		{
			return new Color(new java.awt.Color(i, i, i, alpha));
		}
		if (r > 0 && r < i)
		{
			r = i;
		}
		if (g > 0 && g < i)
		{
			g = i;
		}
		if (b > 0 && b < i)
		{
			b = i;
		}

		return new Color(new java.awt.Color(Math.min((int) (r / Color.DARKERFACTOR), 255), Math.min((int) (g / Color.DARKERFACTOR), 255), Math.min((int) (b / Color.DARKERFACTOR), 255), alpha));
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.glue.iface.Color#makeDarker()
	 */
	@NonNull
	@Override
	public Color makeDarker()
	{
		if (this.color == null)
		{
			return new Color();
		}
		return new Color(new java.awt.Color(Math.max((int) (this.color.getRed() * Color.DARKERFACTOR), 0), Math.max((int) (this.color.getGreen() * Color.DARKERFACTOR), 0), Math.max((int) (this.color.getBlue() * Color.DARKERFACTOR), 0), this.color.getAlpha()));
	}

	@Override
	public int getRGB()
	{
		return this.color == null ? 0 : this.color.getRGB();
	}

	@Override
	public boolean isNull()
	{
		return this.color == null;
	}

	// O V E R R I D E S E R I A L I Z A T I O N

	private void writeObject(@NonNull final ObjectOutputStream out) throws IOException
	{
		out.writeObject(this.color == null ? null : this.color.getRGB());
	}

	private void readObject(@NonNull final ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		this.color = new java.awt.Color((Integer) in.readObject());
	}
}
