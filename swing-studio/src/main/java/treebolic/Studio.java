/*
 * Copyright (c) 2022. Bernard Bou
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
	static private final String version = "4.0.0";

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
		return Studio.version;
	}

	/**
	 * Main
	 *
	 * @param args aruments
	 */
	public static void main(@NonNull final String[] args)
	{
		Laf.lookAndFeel(args);
		//noinspection InstantiationOfUtilityClass
		new Studio(args);
	}
}