/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

import treebolic.annotations.NonNull;

/**
 * Color
 *
 * @author Bernard Bou
 */
public class Color implements treebolic.glue.iface.Color<Color>
{
	/**
	 * White color
	 */
	public static final Color WHITE = new Color();

	/**
	 * Black color
	 */
	public static final Color BLACK = new Color();

	/**
	 * Red color
	 */
	public static final Color RED = new Color();

	/**
	 * Green color
	 */
	public static final Color GREEN = new Color();

	/**
	 * Blue color
	 */
	public static final Color BLUE = new Color();

	/**
	 * Orange color
	 */
	public static final Color ORANGE = new Color();

	/**
	 * Yellow color
	 */
	public static final Color YELLOW = new Color();

	/**
	 * Pink color
	 */
	public static final Color PINK = new Color();

	/**
	 * Cyan color
	 */
	public static final Color CYAN = new Color();

	/**
	 * Magenta color
	 */
	public static final Color MAGENTA = new Color();

	/**
	 * Gray color
	 */
	public static final Color GRAY = new Color();

	/**
	 * Light gray color
	 */
	public static final Color LIGHT_GRAY = new Color();

	/**
	 * Dark gray color
	 */
	public static final Color DARK_GRAY = new Color();

	/**
	 * Constructor
	 */
	public Color()
	{
		throw new NotImplementedException();
	}

	/**
	 * Constructor
	 *
	 * @param rgb rgb int value
	 */
	public Color(int rgb)
	{
		throw new NotImplementedException();
	}

	@Override
	public void set(final int r, final int g, final int b)
	{
		throw new NotImplementedException();
	}

	@Override
	public void set(final int rgb)
	{
		throw new NotImplementedException();
	}

	@Override
	public void parse(final String string)
	{
		throw new NotImplementedException();
	}

	@NonNull
	@Override
	public Color makeBrighter()
	{
		throw new NotImplementedException();
	}

	@NonNull
	@Override
	public Color makeDarker()
	{
		throw new NotImplementedException();
	}

	@Override
	public int getRGB()
	{
		throw new NotImplementedException();
	}

	@Override
	public boolean isNull()
	{
		throw new NotImplementedException();
	}
}
