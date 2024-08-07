/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.xml.transformer;

import org.w3c.dom.Document;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;

/**
 * Transform (document to XML text)
 *
 * @author Bernard Bou
 */
public class DomTransformer
{
    /**
     * Output as html
     */
    private final boolean outputHtml;

    /**
     * Dtd string
     */
    private final String dtd;

    // C O N S T R U C T O R

    /**
     * Constructor
     */
    public DomTransformer()
    {
        this(false, null);
    }

    /**
     * Constructor
     *
     * @param outputHtmlFlag output as html
     * @param dtd            dtd id
     */
    public DomTransformer(final boolean outputHtmlFlag, final String dtd)
    {
        this.outputHtml = outputHtmlFlag;
        this.dtd = dtd;
    }

    // T O . F I L E

    /**
     * Transform XML file to XML file using XSLT file
     *
     * @param inFilePath   in file
     * @param outFilePath  out file
     * @param xsltFilePath xslt file
     * @throws TransformerException transformer exception
     * @throws IOException          io exception
     */
    public void fileToFile(@NonNull final String inFilePath, @NonNull final String outFilePath, @NonNull final String xsltFilePath) throws TransformerException, IOException
    {
        // xsl
        @NonNull final Source xslSource = new StreamSource(new File(xsltFilePath));

        // in
        @NonNull final Source source = inFilePath.equals("-") ? new StreamSource(System.in) : new StreamSource(new File(inFilePath));

        // out
        @NonNull final StreamResult result = outFilePath.equals("-") ? new StreamResult(System.out) : new StreamResult(new FileWriter(outFilePath));

        // transform
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer(xslSource);
        transformer.transform(source, result);
    }

    /**
     * Transform DOM document to XML file
     *
     * @param document   is the DOM Document to be output as XML
     * @param outputFile is the output file
     * @throws TransformerConfigurationException transformer configuration exception
     * @throws TransformerException              transformer exception
     */
    public void documentToFile(final Document document, @NonNull final File outputFile) throws TransformerConfigurationException, TransformerException
    {
        toFile(document, null, outputFile);
    }

    /**
     * Transform DOM document to XML file after applying XSL transform
     *
     * @param document   is the DOM Document to be output as XML
     * @param xsltUrl    is the XSLT source file
     * @param outputFile is the output file
     * @throws TransformerConfigurationException transformer configuration exception
     * @throws TransformerException              transformer exception
     * @throws IOException                       io exception
     */
    public void documentToFile(final Document document, @NonNull final URL xsltUrl, @NonNull final File outputFile) throws TransformerConfigurationException, TransformerException, IOException
    {
        try (@NonNull InputStream xsltIs = xsltUrl.openStream())
        {
            @NonNull final StreamSource xslt = new StreamSource(xsltIs);
            toFile(document, xslt, outputFile);
        }
    }

    // T O . S T R I N G

    /**
     * Transform DOM document to XML string
     *
     * @param document is the DOM Document to be output as XML
     * @return XML String that represents DOM document
     * @throws TransformerConfigurationException transformer configuration exception
     * @throws TransformerException              transformer exception
     */
    public String documentToString(final Document document) throws TransformerConfigurationException, TransformerException
    {
        return toString(document, null);
    }

    /**
     * Transform DOM document to XML string after applying XSL transformation
     *
     * @param document is the DOM Document to be output as XML
     * @param xsltFile is the XSLT source file
     * @return XML String that represents DOM document
     * @throws TransformerConfigurationException transformer configuration exception
     * @throws TransformerException              transformer exception
     */
    public String documentToString(final Document document, @NonNull final File xsltFile) throws TransformerConfigurationException, TransformerException
    {
        @NonNull final StreamSource xslt = new StreamSource(xsltFile);
        return toString(document, xslt);
    }

    /**
     * Transform DOM document to XML string after applying XSL transformation
     *
     * @param document is the DOM Document to be output as XML
     * @param xsltUrl  is the XSLT source url
     * @return XML String that represents DOM document
     * @throws TransformerConfigurationException transformer configuration exception
     * @throws TransformerException              transformer exception
     * @throws IOException                       io exception
     */
    public String documentToString(final Document document, @NonNull final URL xsltUrl) throws TransformerConfigurationException, TransformerException, IOException
    {
        try (@NonNull InputStream xsltIs = xsltUrl.openStream())
        {
            @NonNull final StreamSource xslt = new StreamSource(xsltIs);
            return toString(document, xslt);
        }
    }

    // T O . S T R E A M

    /**
     * Transform DOM document to XML stream
     *
     * @param document     is the DOM Document to be output as XML
     * @param outputStream is the output stream
     * @throws TransformerConfigurationException transformer configuration exception
     * @throws TransformerException              transformer exception
     */
    public void documentToStream(final Document document, final OutputStream outputStream) throws TransformerConfigurationException, TransformerException
    {
        @NonNull final StreamResult result = new StreamResult(outputStream);
        toStream(document, null, result);
    }

    // T O . D O C U M E N T

    /**
     * File to document
     *
     * @param xmlUrl  XML document url
     * @param xsltUrl XSL url
     * @return document
     * @throws TransformerConfigurationException transformer configuration exception
     * @throws TransformerException              transformer exception
     * @throws IOException                       io exception
     */
    public Document fileToDocument(@NonNull final URL xmlUrl, @NonNull final URL xsltUrl) throws TransformerConfigurationException, TransformerException, IOException
    {
        try (@NonNull InputStream xmlIs = xmlUrl.openStream(); @NonNull InputStream xsltIs = xsltUrl.openStream())
        {
            @NonNull final StreamSource xml = new StreamSource(xmlIs);
            @NonNull final StreamSource xslt = new StreamSource(xsltIs);
            return toDocument(xml, xslt);
        }
    }

    /**
     * Transform DOM document to DOM document
     *
     * @param document DOM document
     * @param xsltUrl  XSLT url
     * @return document
     * @throws IOException                       io exception
     * @throws TransformerException              transformer exception
     * @throws TransformerConfigurationException transformer configuration exception
     */
    public Document documentToDocument(final Document document, @NonNull final URL xsltUrl) throws TransformerConfigurationException, TransformerException, IOException
    {
        @NonNull final DOMSource source = new DOMSource(document);
        try (@NonNull InputStream xsltIs = xsltUrl.openStream())
        {
            @NonNull final StreamSource styleSource = new StreamSource(xsltIs);
            return toDocument(source, styleSource);
        }
    }

    // H E L P E R S

    /**
     * Transform DOM document to XML string
     *
     * @param document is the DOM Document to be output as XML
     * @param xslt     is the XSLT source
     * @return XML String that represents DOM document
     * @throws TransformerConfigurationException transformer configuration exception
     * @throws TransformerException              transformer exception
     */
    protected String toString(final Document document, final Source xslt) throws TransformerConfigurationException, TransformerException
    {
        @NonNull final StringWriter writer = new StringWriter();
        @NonNull final StreamResult resultStream = new StreamResult(writer);
        toStream(document, xslt, resultStream);
        return writer.toString();
    }

    /**
     * Transform DOM document to XML file
     *
     * @param document is the DOM Document to be output as XML
     * @param xslt     is the XSLT source
     * @param file     output file
     * @throws TransformerConfigurationException transformer configuration exception
     * @throws TransformerException              transformer exception
     */
    protected void toFile(final Document document, final Source xslt, @NonNull final File file) throws TransformerConfigurationException, TransformerException
    {
        @NonNull final StreamResult resultStream = new StreamResult(file);
        toStream(document, xslt, resultStream);
    }

    /**
     * Transform DOM document to stream result
     *
     * @param document is the DOM Document to be output as XML
     * @param xslt     is the XSLT source
     * @param result   is the stream result
     * @throws TransformerConfigurationException transformer configuration exception
     * @throws TransformerException              transformer exception
     */
    protected void toStream(final Document document, @Nullable final Source xslt, final StreamResult result) throws TransformerConfigurationException, TransformerException
    {
        @NonNull final DOMSource source = new DOMSource(document);

        // transform
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = xslt == null ? transformerFactory.newTransformer() : transformerFactory.newTransformer(xslt);
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.METHOD, this.outputHtml ? "html" : "xml");
        if (this.dtd != null)
        {
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.DOCTYPE_SYSTEM, this.dtd);
        }
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(source, result);
    }

    /**
     * Transform source to document
     *
     * @param source the source
     * @param xslt   the XSLT source
     * @return document
     * @throws TransformerConfigurationException transformer configuration exception
     * @throws TransformerException              transformer exception
     */
    protected Document toDocument(final Source source, @Nullable final Source xslt) throws TransformerConfigurationException, TransformerException
    {
        @NonNull final DOMResult result = new DOMResult();

        // transform
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = xslt == null ? transformerFactory.newTransformer() : transformerFactory.newTransformer(xslt);
        transformer.transform(source, result);
        return (Document) result.getNode();
    }
}