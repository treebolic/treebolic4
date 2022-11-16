/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue.component;

import java.awt.*;

import javax.swing.*;

public class Utils
{
	/**
	 * Center on screen
	 */
	public static void center(final JDialog component)
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
