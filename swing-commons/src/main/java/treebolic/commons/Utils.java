/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;

import treebolic.annotations.NonNull;

/**
 * Utilities
 *
 * @author Bernard Bou
 */
public class Utils
{
	// C E N T E R

	/**
	 * Center on screen
	 *
	 * @param component component to center
	 */
	static public void center(@NonNull final Component component)
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
