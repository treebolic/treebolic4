/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic;

import treebolic.annotations.NonNull;
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
	static private final String VERSION = "4.1-8";

	/**
	 * Get version
	 *
	 * @return version
	 */
	@NonNull
	static public String getVersion()
	{
		return Application.VERSION;
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
