/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.glue;

import treebolic.annotations.Nullable;

/**
 * Color, embeds awt's Color
 *
 * @author Bernard Bou
 */
public class ColorKit
{
	/**
	 * Color from java.awt.color
	 *
	 * @param color java.awt.color
	 * @return color int
	 */
	public static Integer fromAWT(@Nullable final java.awt.Color color)
	{
		return color == null ? null : color.getRGB();
	}

	/**
	 * java.awt.Color from color int
	 *
	 * @param color color int
	 * @return AWT color
	 */
	public static java.awt.Color toAWT(@Nullable final Integer color)
	{
		return color == null ? null : new java.awt.Color(color);
	}
}
