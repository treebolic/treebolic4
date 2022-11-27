/*
 * Copyright (c) 2019-2022. Bernard Bou
 */

package treebolic.glue.iface;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Glue interface for
 *
 * @author Bernard Bou
 */
public interface Colors
{
	/**
	 * White color
	 */
	int WHITE = 0xFFffffff;

	/**
	 * Black color
	 */
	int BLACK = 0xFF000000;

	/**
	 * Red color
	 */
	int RED = 0xFFff0000;

	/**
	 * Green color
	 */
	int GREEN = 0xFF00ff00;

	/**
	 * Blue color
	 */
	int BLUE = 0xFF0000ff;

	/**
	 * Orange color
	 */
	int ORANGE = 0xFFffc800;

	/**
	 * Yellow color
	 */
	int YELLOW = 0xFFffff00;

	/**
	 * Pink color
	 */
	int PINK = 0xFFffafaf;

	/**
	 * Cyan color
	 */
	int CYAN = 0xFF00ffff;

	/**
	 * Magenta color
	 */
	int MAGENTA = 0xFFff00ff;

	/**
	 * Gray color
	 */
	int GRAY = 0xFF808080;

	/**
	 * Light gray color
	 */
	int LIGHT_GRAY = 0xFFC0C0C0;

	/**
	 * Dark gray color
	 */
	int DARK_GRAY = 0xFF404040;

	/**
	 * Make from R,G,B values
	 *
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @return color int
	 */
	static int make(int r, int g, int b)
	{
		return 0xFF000000 | r << 16 | g << 8 | b;
	}

	/**
	 * Make from R,G,B, alpha values
	 *
	 * @param r     red
	 * @param g     green
	 * @param b     blue
	 * @param alpha alpha
	 * @return color int
	 */
	static int make(int r, int g, int b, int alpha)
	{
		return alpha << 24 | r << 16 | g << 8 | b;
	}

	/**
	 * Get red
	 *
	 * @param color color
	 * @return red
	 */
	static int getRed(@NonNull final int color)
	{
		return color >> 16 | 0x000000FF;
	}

	/**
	 * Get blue
	 *
	 * @param color color
	 * @return red
	 */
	static int getGreen(@NonNull final int color)
	{
		return color >> 8 | 0x000000FF;
	}

	/**
	 * Get blue
	 *
	 * @param color color
	 * @return red
	 */
	static int getBlue(@NonNull final int color)
	{
		return color | 0x000000FF;
	}

	/**
	 * Get alpha
	 *
	 * @param color color
	 * @return alpha
	 */
	static int getAlpha(@NonNull final int color)
	{
		return color >> 24 | 0x000000FF;
	}

	/**
	 * Parse color from string
	 *
	 * @param string string to parse
	 * @return color int
	 */
	@Nullable
	static Integer parse(@Nullable final String string)
	{
		if (string == null)
		{
			return null;
		}
		return Integer.parseInt(string, 16);
	}

	/**
	 * Darker factor
	 */
	float DARKERFACTOR = 0.85F;

	/**
	 * Make brighter color
	 *
	 * @param color color
	 * @return brighter color int
	 */
	@Nullable
	static Integer makeBrighter(@Nullable final Integer color)
	{
		if (color == null)
		{
			return null;
		}

		int r = getRed(color);
		int g = getGreen(color);
		int b = getBlue(color);
		final int alpha = getAlpha(color);

		final int i = (int) (1.0 / (1.0 - DARKERFACTOR));
		if (r == 0 && g == 0 && b == 0)
		{
			return make(i, i, i, alpha);
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

		return make( //
				Math.min((int) (r / DARKERFACTOR), 255), //
				Math.min((int) (g / DARKERFACTOR), 255), //
				Math.min((int) (b / DARKERFACTOR), 255), //
				alpha);
	}

	/**
	 * Make darker color
	 *
	 * @param color color
	 * @return darker color int
	 */
	@Nullable
	static Integer makeDarker(@Nullable final Integer color)
	{
		if (color == null)
		{
			return null;
		}
		return make( //
				Math.max((int) (getRed(color) * DARKERFACTOR), 0), //
				Math.max((int) (getGreen(color) * DARKERFACTOR), 0), //
				Math.max((int) (getBlue(color) * DARKERFACTOR), 0), //
				getAlpha(color));
	}
}
