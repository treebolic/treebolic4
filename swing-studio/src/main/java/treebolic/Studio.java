/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic;

import treebolic.annotations.NonNull;
import treebolic.commons.Laf;
import treebolic.studio.MainFrame;

/**
 * Studio
 *
 * @author Bernard Bou
 */
public class Studio
{
	/**
	 * Version
	 */
	static private final String VERSION = "4.1-7";

	/**
	 * Constructor
	 *
	 * @param args program arguments
	 */
	public Studio(@NonNull final String[] args)
	{
		// System.out.println("CLASSPATH=<" + System.getProperty("java.class.path", ".") + ">");

		// laf
		Laf.lookAndFeel(args);

		new MainFrame(args);
	}

	/**
	 * Get version
	 *
	 * @return version
	 */
	@NonNull
	static public String getVersion()
	{
		return Studio.VERSION;
	}

	/**
	 * Main
	 *
	 * @param args arguments
	 */
	public static void main(@NonNull final String[] args)
	{
		Laf.lookAndFeel(args);
		//noinspection InstantiationOfUtilityClass
		new Studio(args);
	}
}