/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.glue.component;

import java.awt.*;

import javax.swing.*;

import treebolic.annotations.NonNull;

/**
 * Common Utilities
 */
public class Utils
{
	/**
	 * Center on screen
	 *
	 * @param component component
	 */
	public static void center(@NonNull final JDialog component)
	{
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension componentSize = component.getSize();
		if (componentSize.height > screenSize.height)
		{
			componentSize.height = screenSize.height;
		}
		if (componentSize.width > screenSize.width)
		{
			componentSize.width = screenSize.width;
		}
		component.setLocation((screenSize.width - componentSize.width) / 2, (screenSize.height - componentSize.height) / 2);
	}
}
