/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.wordnet.browser2;

import treebolic.annotations.NonNull;
import treebolic.wordnet.browser.MainFrame;

import java.util.Properties;

public class MainFrame2 extends MainFrame
{
    public MainFrame2(@NonNull String[] args)
    {
        super(args);
    }

    public MainFrame2(@NonNull Properties args)
    {
        super(args);
    }

    @NonNull
    @Override
    protected final String getDefaultProvider()
    {
        return "treebolic.provider.wordnet.jwi.compact.Provider2";
    }

    @NonNull
    @Override
    protected String getPersistName()
    {
        return "treebolic-wordnet-browser2";
    }
}
