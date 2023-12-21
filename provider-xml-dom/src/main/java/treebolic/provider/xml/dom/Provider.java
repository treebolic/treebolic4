/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.provider.xml.dom;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import treebolic.ILocator;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.Model;
import treebolic.model.Tree;
import treebolic.provider.IProvider;
import treebolic.provider.IProviderContext;
import treebolic.provider.ProviderUtils;

/**
 * XML provider
 */
public class Provider implements IProvider
{
    // D A T A

    /**
     * Context used to query for Url
     */
    @SuppressWarnings("WeakerAccess")
    protected IProviderContext context;

    /**
     * Url
     */
    @Nullable
    @SuppressWarnings("WeakerAccess")
    protected URL url;

    // C O N S T R U C T O R

    /**
     * Constructor
     */
    public Provider()
    {
        this.url = null;
    }

    // M A K E

    @Override
    public void setContext(final IProviderContext context)
    {
        this.context = context;
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void setLocator(final ILocator locator)
    {
        // do not need
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void setHandle(final Object handle)
    {
        // do not need
    }

    @Override
    public Tree makeTree(final String source, final URL base, final Properties parameters, final boolean checkRecursion)
    {
        @Nullable final URL url = ProviderUtils.makeURL(source, base, parameters, this.context);
        if (url == null)
        {
            return null;
        }

        // direct recursion prevention
        if (checkRecursion && sameURL(url, this.url))
        {
            this.context.message("Recursion: " + url);
            return null;
        }

        this.url = url;
        this.context.progress("Loading ..." + url, false);
        @Nullable final Tree tree = makeTree(url, base, parameters);
        if (tree != null)
        {
            this.context.progress("Loaded ..." + url, false);
        }
        return tree;
    }

    @Override
    public Model makeModel(final String source, final URL base, final Properties parameters)
    {
        @Nullable final URL url = ProviderUtils.makeURL(source, base, parameters, this.context);
        if (url == null)
        {
            return null;
        }

        this.url = url;
        this.context.progress("Loading ..." + url, false);
        @Nullable final Model model = makeModel(url, base, parameters);
        if (model != null)
        {
            this.context.progress("Loaded ..." + url, false);
        }
        return model;
    }

    // P A R S E

    /**
     * Make model from url
     *
     * @param url        url
     * @param base       base
     * @param parameters parameters
     * @return model
     */
    @Nullable
    @SuppressWarnings("WeakerAccess")
    protected Model makeModel(@NonNull final URL url, final URL base, final Properties parameters)
    {
        @Nullable final Document document = makeDocument(url);
        if (document == null)
        {
            return null;
        }
        return new ModelFactory().makeModel(document);
    }

    /**
     * Make tree from url
     *
     * @param url        url
     * @param base       base
     * @param parameters parameters
     * @return tree
     */
    @Nullable
    @SuppressWarnings("WeakerAccess")
    protected Tree makeTree(@NonNull final URL url, final URL base, final Properties parameters)
    {
        @Nullable final Document document = makeDocument(url);
        if (document == null)
        {
            return null;
        }
        return new ModelFactory().makeTree(document);
    }

    /**
     * Make DOM document from its Url
     *
     * @param url document url
     * @return DOM document
     */
    @Nullable
    @SuppressWarnings("WeakerAccess")
    protected Document makeDocument(@NonNull final URL url)
    {
        try
        {
            return new Parser().makeDocument(url, (publicId, systemId) -> {
                if (systemId.contains("Treebolic.dtd"))
                {
                    return new InputSource(new StringReader(""));
                }
                else
                {
                    return null;
                }
            });
        }
        catch (@NonNull final IOException e)
        {
            this.context.warn("DOM parser IO: " + e);
        }
        catch (@NonNull final SAXException e)
        {
            this.context.warn("DOM parser SAX: " + e);
        }
        catch (@NonNull final ParserConfigurationException e)
        {
            this.context.warn("DOM parser CONFIG: " + e);
        }
        return null;
    }

    // H E L P E R S
    private static boolean sameURL(@NonNull final URL url1, @Nullable final URL url2)
    {
        if (url2 == null)
            return false;
        try
        {
            return url1.toURI().equals(url2.toURI());
        }
        catch (URISyntaxException e)
        {
            return false;
        }
    }
}
