/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.fungi;

import treebolic.annotations.NonNull;
import treebolic.commons.Laf;
import treebolic.fungi.browser.MainFrame;

/**
 * Browser
 *
 * @author Bernard Bou
 */
public class Browser
{
	// D A T A

	/**
	 * Version : 3.x
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
	 * @param args
	 *        arguments
	 */
	public static void main(@NonNull final String[] args)
	{
		Laf.lookAndFeel(args);
		new MainFrame(args);
	}
}
