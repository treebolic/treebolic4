/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.wordnet.browser2;

import treebolic.annotations.NonNull;
import treebolic.wordnet.browser.MainFrame;

import java.util.Properties;

/**
 * MainFrame that overrides provider and persist names
 */
public class MainFrame2 extends MainFrame
{
    /**
     * Constructor
     *
     * @param args command-line arguments
     */
    public MainFrame2(@NonNull String[] args)
    {
        super(args);
    }

    /**
     * Constructor
     *
     * @param args command-line arguments
     */
    public MainFrame2(@NonNull Properties args)
    {
        super(args);
    }

    @NonNull
    @Override
    protected final String getDefaultProvider()
    {
        return "treebolic.provider.wordnet.kwi.compact.Provider2";
    }

    @NonNull
    @Override
    protected String getPersistName()
    {
        return "treebolic-wordnet-browser2";
    }
}
