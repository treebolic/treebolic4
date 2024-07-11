/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.fungi;

import treebolic.annotations.NonNull;
import treebolic.commons.Laf;
import treebolic.fungi.browser.MainFrame;

import java.util.Properties;

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
        Properties args2 = MainFrame.processArgs(args);
        if (args2 == null)
        {
            args2 = new Properties();
        }

        if (!args2.containsKey("provider"))
            args2.put("provider", "treebolic.provider.sql.jdbc.Provider");
        if (!args2.containsKey("base"))
            args2.put("base", "database");
        if (!args2.containsKey("images"))
            args2.put("images", "database/images/");
        new MainFrame(args2);
    }
}
