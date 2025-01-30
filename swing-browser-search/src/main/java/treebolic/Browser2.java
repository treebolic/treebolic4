/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic;

import treebolic.annotations.NonNull;
import treebolic.browser2.MainFrame;
import treebolic.commons.Laf;

/**
 * Browser
 *
 * @author Bernard Bou
 */
public class Browser2
{
	// D A T A

	/**
	 * Version
	 */
	static private final String VERSION = "4.2-0";

	/**
	 * Get version
	 *
	 * @return version
	 */
	@NonNull
	static public String getVersion()
	{
		return Browser2.VERSION;
	}

	// M A I N

	/**
	 * Main
	 *
	 * @param args arguments
	 */
	public static void main(@NonNull final String[] args)
	{
		Laf.lookAndFeel(args);
		new MainFrame(args);
	}
}
