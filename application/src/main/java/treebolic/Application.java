/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic;

import treebolic.application.Laf;
import treebolic.application.MainFrame;

/**
 * Application
 *
 * @author Bernard Bou
 */
public class Application
{
	// D A T A

	/**
	 * Version : 3.x
	 */
	static private final String VERSION = "3.9.0"; //$NON-NLS-1$

	/**
	 * Get version
	 *
	 * @return version
	 */
	static public String getVersion()
	{
		return Application.VERSION;
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
