/**
 * Title : Treebolic browser
 * Description : Treebolic browser
 * Version : 3.x
 * Copyright :	(c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.wordnet.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.wordnet.browser;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.wordnet.Browser;

/**
 * Context
 *
 * @author Bernard Bou
 */
public class Context extends treebolic.browser2.Context
{
    /**
     * URL template
     */
    static private final String URL_TEMPLATE_WIKTIONARY = "https://en.wiktionary.org/wiki/%s";

    // static public final String URL_TEMPLATE_WIKTIONARY = "http://en.wiktionary.org/w/index.php?title=%s&printable=yes";

    /**
     * Data directory
     */
    @Nullable
    private URL dataDirUrl;

    /**
     * Constructor
     *
     * @param application application mainframe
     * @param source      source
     * @param base        base
     * @param imageBase   image base
     * @param userHome    user home as base
     */
    public Context(final MainFrame application, final String source, final String base, final String imageBase, final boolean userHome)
    {
        super(application, source, base, imageBase, "internal:wordnet:");

        // data URL
        try
        {
            this.dataDirUrl = makeDataDir(base, userHome).toURI().toURL();
        }
        catch (MalformedURLException mue)
        {
            this.dataDirUrl = null;
            mue.printStackTrace();
        }
    }

    /**
     * Make data dir
     *
     * @param base     base
     * @param userHome use user home
     * @return data dir
     */
    @NonNull
    public static File makeDataDir(@Nullable final String base, final boolean userHome)
    {
        if (base == null || base.isEmpty())
        {
            if (userHome)
            {
                return new File(System.getProperty("user.home"));
            }
            else
            {
                String location = Browser.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                try
                {
                    location = URLDecoder.decode(location, "UTF-8");
                }
                catch (UnsupportedEncodingException exception)
                {
                    System.err.println(exception + " " + location);
                }
                location = new File(location).getParent();
                // System.out.println("[data] " + location);
                return new File(location);
            }
        }
        else
        {
            return new File(base);
        }
    }

    @Override
    public Properties getParameters()
    {
        return this.browser.getParameters();
    }

    @Override
    public URL getBase()
    {
        return this.dataDirUrl;
    }

    @Override
    public URL getImagesBase()
    {
        return this.dataDirUrl;
    }

    @Override
    public String getStyle()
    {
        return ".content { }" + ".members {color: black; background-color: #FFD700; font-weight: bold;}" + //
                ".def {color: white; background-color: #5988A8; font-weight: normal;}" + //
                ".sample {color: gray; font-family: serif; }" + //
                ".data {color: gray; font-size: small;}" + //
                ".ref {color: #8B3A62; font-size: small; }" + //
                ".link {color: #007D82; font-size: small; }" + //
                ".linking {color: #007D82; font-size: small; }" + //
                ".searching {color: #007D82; font-size: small; }" + //
                ".more {color: gray; margin-bottom: 5px; }" + //
                "a.active_link {color: black; text-decoration: none; }" + //
                "";
    }

    @Override
    public boolean linkTo(@NonNull final String linkUrl, @Nullable final String linkTarget)
    {
        // help
        if (linkUrl.startsWith("internal:help:"))
        {
            @NonNull final String link = linkUrl.substring(14);
            @NonNull final JComponent pane = this.browser.makeBrowserPane(this.getClass().getResource("doc/" + link + ".html"), true);
            this.browser.addTab(pane, link, linkUrl);
            return true;
        }

        // wiktionary hook
        if (linkUrl.startsWith("wordnet:") && (linkTarget == null || !linkTarget.startsWith("#")))
        {
            try
            {
                final String link = URLEncoder.encode(linkUrl.substring(8).replace('_', ' '), "UTF-8");
                final String urlString = String.format(URL_TEMPLATE_WIKTIONARY, link);
                @NonNull final URL uRL = new URL(urlString);
                @NonNull final JComponent pane = this.browser.makeBrowserPane(uRL, true);
                this.browser.addTab(pane, link, linkUrl);
            }
            catch (MalformedURLException | UnsupportedEncodingException exception)
            {
                exception.printStackTrace();
            }
            return true;
        }

        // standard workflow
        return super.linkTo(linkUrl, linkTarget);
    }
}
