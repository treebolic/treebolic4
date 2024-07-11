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

import java.util.Properties;

import static treebolic.browser2.MainFrame.makeArgs;

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
    static private final String VERSION = "4.1-8";

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
        Properties args2 = makeArgs(args);
        if (!args2.containsKey("provider"))
            args2.put("provider", "treebolic.provider.wordnet.jwi.compact.Provider");
        if (!args2.containsKey("base"))
            args2.put("base", "database");
        if (!args2.containsKey("images"))
            args2.put("images", "database/images/");
        new MainFrame(args2);
    }
}
