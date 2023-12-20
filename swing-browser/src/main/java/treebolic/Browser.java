/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic;

import treebolic.annotations.NonNull;
import treebolic.browser.MainFrame;
import treebolic.commons.Laf;

/**
 * Browser
 *
 * @author Bernard Bou
 */
public class Browser
{
	// D A T A

	/**
	 * Version
	 */
	static private final String VERSION = "4.0.0";

	/**
	 * Get version
	 *
	 * @return version
	 */
	@NonNull
	static public String getVersion()
	{
		return Browser.VERSION;
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
