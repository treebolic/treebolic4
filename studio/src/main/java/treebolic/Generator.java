/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic;

import treebolic.commons.Laf;
import treebolic.generator.MainFrame;

/**
 * Generator
 *
 * @author Bernard Bou
 */
public class Generator
{
	/**
	 * Version : 3.x
	 */
	static private final String version = "3.9.0"; 

	/**
	 * Constructor
	 *
	 * @param args
	 *        program arguments
	 */
	public Generator(final String[] args)
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
	static public String getVersion()
	{
		return Generator.version;
	}

	/**
	 * Main
	 *
	 * @param args
	 *        aruments
	 */
	public static void main(final String[] args)
	{
		Laf.lookAndFeel(args);
		new Generator(args);
	}
}