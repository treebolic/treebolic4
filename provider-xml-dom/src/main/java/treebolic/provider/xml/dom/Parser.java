/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.xml.dom;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.Model;
import treebolic.model.ModelDump;

/**
 * DOM Parser
 *
 * @author Bernard Bou
 */
@SuppressWarnings("WeakerAccess")
public class Parser
{
	/**
	 * Validate XML
	 */
	@SuppressWarnings("WeakerAccess")
	protected final boolean validate;

	/**
	 * Constructor
	 */
	public Parser()
	{
		this(false);
	}

	/**
	 * Constructor
	 *
	 * @param validate whether to validate XML
	 */
	@SuppressWarnings("WeakerAccess")
	public Parser(@SuppressWarnings("SameParameterValue") final boolean validate)
	{
		this.validate = validate;
	}

	/**
	 * Make Document builder
	 *
	 * @return document builder
	 * @throws ParserConfigurationException parser configuration exception
	 */
	private DocumentBuilder makeDocumentBuilder() throws ParserConfigurationException
	{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setCoalescing(true);
		factory.setIgnoringComments(true);
		factory.setNamespaceAware(false);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setValidating(this.validate);
		// @formatter:off
		try	{ factory.setFeature(XMLConstants.ACCESS_EXTERNAL_DTD, this.validate);} catch(@NonNull final Exception ignored){}
		try	{ factory.setFeature(XMLConstants.ACCESS_EXTERNAL_SCHEMA, this.validate);} catch(@NonNull final Exception ignored){}
		try	{ factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", this.validate); } catch (@NonNull final Exception ignored) {}
		// @formatter:on
		return factory.newDocumentBuilder();
	}

	/**
	 * Make document
	 *
	 * @param url      in data url
	 * @param resolver entity resolver
	 * @return DOM document
	 * @throws ParserConfigurationException parser configuration exception
	 * @throws IOException                  io exception
	 * @throws SAXException                 sax parser exception
	 */
	public Document makeDocument(@NonNull final URL url, @Nullable final EntityResolver resolver) throws ParserConfigurationException, SAXException, IOException
	{
		@NonNull final ParseErrorLogger handler = new ParseErrorLogger();
		try
		{
			final DocumentBuilder builder = makeDocumentBuilder();
			builder.setErrorHandler(handler);
			if (resolver != null)
			{
				builder.setEntityResolver(resolver);
			}
			return builder.parse(url.openStream());
		}
		finally
		{
			handler.terminate();
		}
	}

	/**
	 * Make document
	 *
	 * @param filePath in data file path
	 * @param resolver entity resolver
	 * @return DOM document
	 * @throws ParserConfigurationException parser configuration exception
	 * @throws IOException                  io exception
	 * @throws SAXException                 sax parser exception
	 */
	public Document makeDocument(@NonNull final String filePath, @Nullable final EntityResolver resolver) throws ParserConfigurationException, SAXException, IOException
	{
		@NonNull final ParseErrorLogger handler = new ParseErrorLogger();
		try (InputStream is = Files.newInputStream(Paths.get(filePath)))
		{
			final DocumentBuilder builder = makeDocumentBuilder();
			builder.setErrorHandler(handler);
			if (resolver != null)
			{
				builder.setEntityResolver(resolver);
			}
			return builder.parse(is);
		}
		finally
		{
			handler.terminate();
		}
	}

	/**
	 * Document
	 *
	 * @param url      in data url
	 * @param xslt     xslt url
	 * @param resolver entity resolver, null if none
	 * @return DOM document
	 */
	@Nullable
	public Document makeDocument(@NonNull final URL url, @NonNull final URL xslt, @Nullable final EntityResolver resolver)
	{
		try
		{
			// xsl
			@NonNull final Source xslSource = new StreamSource(xslt.openStream());

			// in
			Source source;
			if (resolver == null)
			{
				source = new StreamSource(url.openStream());
			}
			else
			{
				final XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
				reader.setEntityResolver(resolver);
				source = new SAXSource(reader, new InputSource(url.openStream()));
			}

			// out
			@NonNull final DOMResult result = new DOMResult();

			// transform
			final TransformerFactory factory = TransformerFactory.newInstance();
			final Transformer transformer = factory.newTransformer(xslSource);
			transformer.setParameter("http://xml.org/sax/features/validation", false);
			transformer.transform(source, result);

			return (Document) result.getNode();
		}
		catch (@NonNull final Exception e)
		{
			System.err.println("Dom parser: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Make DOM document from its Url
	 *
	 * @param filePath document filePath
	 * @return DOM document
	 */
	@Nullable
	@SuppressWarnings("WeakerAccess")
	private static Document makeDocument(@NonNull String filePath) throws ParserConfigurationException, IOException, SAXException
	{
		return new Parser().makeDocument(filePath, (publicId, systemId) -> {
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

	/**
	 * Make model
	 *
	 * @param filePath command-line arguments
	 * @return model
	 * @throws ParserConfigurationException parser configuration exception
	 * @throws SAXException                 sax exception
	 * @throws IOException                  io exception
	 */
	public static Model makeModel(@NonNull final String filePath) throws ParserConfigurationException, SAXException, IOException
	{
		@Nullable final Document document = makeDocument(filePath);
		if (document == null)
		{
			return null;
		}
		return new BaseDocumentAdapter().makeModel(document);
	}

	/**
	 * Main
	 *
	 * @param args command-line arguments
	 * @throws ParserConfigurationException parser configuration exception
	 * @throws SAXException                 sax exception
	 * @throws IOException                  io exception
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		@Nullable Model model = makeModel(args[0]);
		System.out.println(ModelDump.toString(model));
	}
}
