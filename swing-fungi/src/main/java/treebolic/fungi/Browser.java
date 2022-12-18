/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.fungi;

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
	public static void main(final String[] args)
	{
		Laf.lookAndFeel(args);
		new MainFrame(args);
	}
}
