package treebolic.provider.xml.sax;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
        @Nullable final Tree tree;
        try
        {
            tree = makeTree(url);
            if (tree != null)
            {
                this.context.progress("Loaded ..." + url, false);
            }
            return tree;
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            throw new RuntimeException(e);
        }
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
        @Nullable final Model model;
        try
        {
            model = makeModel(url);
            if (model != null)
            {
                this.context.progress("Loaded ..." + url, false);
            }
            return model;
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    // P A R S E

    /**
     * Make model from url
     *
     * @param url url
     * @return model
     * @throws ParserConfigurationException parser configuration exception
     * @throws SAXException                 sax exception
     * @throws IOException                  io exception
     */
    @Nullable
    @SuppressWarnings("WeakerAccess")
    protected Model makeModel(@NonNull final URL url) throws ParserConfigurationException, SAXException, IOException
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        // @formatter:off
		try	{ factory.setFeature(XMLConstants.ACCESS_EXTERNAL_DTD, false);} catch(@NonNull final Exception ignored){}
		try	{ factory.setFeature(XMLConstants.ACCESS_EXTERNAL_SCHEMA, false);} catch(@NonNull final Exception ignored){}
		try	{ factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); } catch(@NonNull final Exception ignored){}
		// @formatter:on
        SAXParser saxParser = factory.newSAXParser();

        @NonNull Parser.SaxHandler handler = new Parser.SaxHandler();
        try (@NonNull InputStream is = url.openStream())
        {
            saxParser.parse(is, handler);
            return handler.getResult();
        }
    }

    /**
     * Make tree from url
     *
     * @param url url
     * @return tree
     * @throws ParserConfigurationException parser configuration exception
     * @throws SAXException                 sax exception
     * @throws IOException                  io exception
     */
    @Nullable
    @SuppressWarnings("WeakerAccess")
    protected Tree makeTree(@NonNull final URL url) throws ParserConfigurationException, SAXException, IOException
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        // @formatter:off
		try	{ factory.setFeature(XMLConstants.ACCESS_EXTERNAL_DTD, false);} catch(@NonNull final Exception ignored){}
		try	{ factory.setFeature(XMLConstants.ACCESS_EXTERNAL_SCHEMA, false);} catch(@NonNull final Exception ignored){}
		try	{ factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); } catch(@NonNull final Exception ignored){}
		// @formatter:on
        SAXParser saxParser = factory.newSAXParser();

        @NonNull Parser.SaxHandler handler = new Parser.SaxHandler();
        try (@NonNull InputStream is = url.openStream())
        {
            saxParser.parse(is, handler);
            return handler.getResult().tree;
        }
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
