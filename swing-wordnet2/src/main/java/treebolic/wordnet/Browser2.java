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
import treebolic.wordnet.browser2.MainFrame2;

import java.util.Properties;

/**
 * Browser
 *
 * @author Bernard Bou
 */
public class Browser2 extends Browser
{
    /**
     * Main
     *
     * @param args arguments
     */
    public static void main(@NonNull final String[] args)
    {
        Laf.lookAndFeel(args);
        Properties args2 = treebolic.browser2.MainFrame.makeArgs(args);
        if (!args2.containsKey("provider"))
            args2.put("provider", "treebolic.provider.wordnet.kwi.compact.Provider2");
        if (!args2.containsKey("base"))
            args2.put("base", "database");
        new MainFrame2(args2);
    }
}
