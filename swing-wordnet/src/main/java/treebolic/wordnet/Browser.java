/**
 * Title : Treebolic browser
 * Description : Treebolic browser
 * Version : 3.x
 * Copyright :	(c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.wordnet.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.wordnet;

import treebolic.annotations.NonNull;
import treebolic.commons.Laf;
import treebolic.wordnet.browser.MainFrame;

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
