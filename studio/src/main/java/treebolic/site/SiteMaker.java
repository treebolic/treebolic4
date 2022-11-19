/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.site;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Observable;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import treebolic.dtd.Dtd;
import treebolic.generator.DocumentSearch;
import treebolic.generator.Pair;
import treebolic.xml.transformer.DomTransformer;

/**
 * Make site
 *
 * @author Bernard Bou
 */
public abstract class SiteMaker extends Observable
{
	// reference

	/**
	 * Jar file to transfer
	 */
	static private final String jarFile = "treebolic-applet-dom.jar"; //$NON-NLS-1$

	/**
	 * Dtd file to transfer
	 */
	static private final String dTDFile = "Treebolic.dtd"; //$NON-NLS-1$

	/**
	 * Applet class
	 */
	static private final String appclass = "treebolic.applet.Treebolic.class"; //$NON-NLS-1$

	/**
	 * HTML template file
	 */
	static public String templateFile = "template.html"; //$NON-NLS-1$

	// progress

	/**
	 * Start
	 */
	static final int PROGRESSZERO = 0;

	/**
	 * Connect start
	 */
	static final int PROGRESSCONNECT = 0;

	/**
	 * Connect completed
	 */
	static final int PROGRESSCONNECTCOMPLETE = 10;

	/**
	 * Transfer XML
	 */
	static final int PROGRESSXFERXML = 10;

	/**
	 * Transfer HTML
	 */
	static final int PROGRESSXFERHTML = 40;

	/**
	 * Transfer applet Jar
	 */
	static final int PROGRESSXFERJAR = 50;

	/**
	 * Transfer DTD
	 */
	static final int PROGRESSXFERDTD = 75;

	/**
	 * Transfer images
	 */
	static final int PROGRESSXFERIMAGES = 80;

	/**
	 * Disconnect start
	 */
	static final int PROGRESSDISCONNECT = 95;

	/**
	 * Disconnect complete
	 */
	static final int PROGRESSCOMPLETE = 100;

	// D A T A

	/**
	 * Document (source)
	 */
	private final Document document;

	/**
	 * Repository (source) for templetes
	 */
	private final String repository;

	/**
	 * Image repository (source)
	 */
	private final String imageRepository;

	/**
	 * Title for HTML title (target)
	 */
	private final String title;

	/**
	 * HTML file (target)
	 */
	protected String htmlFile;

	/**
	 * XML file (target)
	 */
	protected String xmlFile;

	/**
	 * Operation string
	 */
	protected String operation;

	// C O N S T R U C T I O N

	/**
	 * Constructor
	 *
	 * @param document        document (source)
	 * @param repository      repository (source)
	 * @param imageRepository image repository (source)
	 * @param htmlFile        HTML file (target)
	 * @param xmlFile         XML file (target)
	 * @param title           HTML file title (target)
	 */
	public SiteMaker(final Document document, final String repository, final String imageRepository, final String htmlFile, final String xmlFile, final String title)
	{
		this.document = document;
		this.title = title;
		this.xmlFile = xmlFile;
		this.htmlFile = htmlFile;
		this.repository = repository;
		this.imageRepository = imageRepository;
	}

	// I N T E R F A C E

	/**
	 * Connect to destination
	 *
	 * @return true if successful
	 */
	abstract boolean connect();

	/**
	 * Disconnect from destination
	 *
	 * @return true if successful
	 */
	abstract boolean disconnect();

	/**
	 * Change folder
	 *
	 * @param relativeFolder relative folder
	 * @return true if successful
	 * @throws MalformedURLException malformed URL exception
	 * @throws URISyntaxException    URI syntax exception
	 */
	abstract boolean changeFolder(String relativeFolder) throws MalformedURLException, URISyntaxException;

	/**
	 * Make output stream
	 *
	 * @param filename filename
	 * @return output stream
	 * @throws MalformedURLException malformed URL exception
	 * @throws IOException           io exception
	 * @throws URISyntaxException    URI syntax exception
	 */
	abstract OutputStream makeOutStream(String filename) throws MalformedURLException, IOException, URISyntaxException;

	// O B S E R V A B L E

	/**
	 * Notify operation progress
	 *
	 * @param operation     pending operation string
	 * @param progressValue progress value (max 100)
	 */
	void notifyOperation(final String operation, final int progressValue)
	{
		setChanged();
		this.operation = operation;
		notifyObservers(new Pair<>(operation, progressValue));
	}

	// O P E R A T I O N

	/**
	 * Make site operation
	 *
	 * @return true if successful
	 */
	public boolean make()
	{
		// connect
		notifyOperation(Messages.getString("SiteMaker.connecting"), SiteMaker.PROGRESSCONNECT); //$NON-NLS-1$
		if (!connect())
		{
			notifyOperation(Messages.getString("SiteMaker.connect_fail"), SiteMaker.PROGRESSCOMPLETE); //$NON-NLS-1$
			return false;
		}

		boolean success = false;
		try
		{
			// urls
			final URL repositoryUrl = new URL(this.repository);
			final URL imageRepositoryUrl = new URL(this.imageRepository);

			// xml file
			notifyOperation("STOR " + this.xmlFile, SiteMaker.PROGRESSXFERXML); //$NON-NLS-1$
			OutputStream outputStream = makeOutStream(this.xmlFile);
			new DomTransformer().documentToStream(this.document, outputStream);
			outputStream.close();

			// html file
			notifyOperation("STOR " + this.htmlFile, SiteMaker.PROGRESSXFERHTML); //$NON-NLS-1$
			final String[] macros = {"%XMLFILE%", "%TITLE%", "%CLASS%", "%JAR%", "%WIDTH%", "%HEIGHT%", "%DATE%"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			final String[] values = {this.xmlFile, this.title, SiteMaker.appclass, SiteMaker.jarFile, "100%", "100%", new Date().toString()}; //$NON-NLS-1$ //$NON-NLS-2$
			URL url = new URL(repositoryUrl, SiteMaker.templateFile);
			outputStream = makeOutStream(this.htmlFile);
			copyStreamsReplace(url.openStream(), outputStream, macros, values);
			outputStream.close();

			// copy code jar
			notifyOperation("STOR " + SiteMaker.jarFile, SiteMaker.PROGRESSXFERJAR); //$NON-NLS-1$
			url = new URL(repositoryUrl, SiteMaker.jarFile);
			outputStream = makeOutStream(SiteMaker.jarFile);
			SiteMaker.copyStreams(url.openStream(), outputStream);
			outputStream.close();

			// copy dtd
			notifyOperation("STOR " + SiteMaker.dTDFile, SiteMaker.PROGRESSXFERDTD); //$NON-NLS-1$
			outputStream = makeOutStream(SiteMaker.dTDFile);
			Dtd.copyToUTF8Stream(outputStream);
			outputStream.close();

			// copy images
			notifyOperation("CD " + "images", SiteMaker.PROGRESSXFERIMAGES); //$NON-NLS-1$ //$NON-NLS-2$
			if (changeFolder("images")) //$NON-NLS-1$
			{
				for (final String imageFileName : DocumentSearch.makeImageList(this.document))
				{
					notifyOperation("STOR " + "images" + "/" + imageFileName, SiteMaker.PROGRESSXFERIMAGES); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					url = new URL(imageRepositoryUrl, imageFileName);
					outputStream = makeOutStream(imageFileName);
					SiteMaker.copyStreams(url.openStream(), outputStream);
					outputStream.close();
				}
			}

			// end
			notifyOperation(Messages.getString("SiteMaker.disconnecting"), SiteMaker.PROGRESSDISCONNECT); //$NON-NLS-1$
			success = true;
		}
		catch (final MalformedURLException exception)
		{
			notifyOperation(Messages.getString("SiteMaker.except_malformed_url") + this.operation + " [" + exception + "]", SiteMaker.PROGRESSCOMPLETE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		catch (final IOException exception)
		{
			notifyOperation(Messages.getString("SiteMaker.except_io") + this.operation + " [" + exception + "]", SiteMaker.PROGRESSCOMPLETE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		catch (final TransformerConfigurationException exception)
		{
			notifyOperation(Messages.getString("SiteMaker.except_transf") + this.operation + " [" + exception + "]", SiteMaker.PROGRESSCOMPLETE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		catch (final TransformerException exception)
		{
			notifyOperation(Messages.getString("SiteMaker.except_conf") + this.operation + " [" + exception + "]", SiteMaker.PROGRESSCOMPLETE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		catch (final URISyntaxException exception)
		{
			notifyOperation(Messages.getString("SiteMaker.except_uri_syntax") + this.operation + " [" + exception + "]", SiteMaker.PROGRESSCOMPLETE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		finally
		{
			disconnect();
		}
		if (success)
		{
			notifyOperation(Messages.getString("SiteMaker.transfer_end"), SiteMaker.PROGRESSCOMPLETE); //$NON-NLS-1$
		}
		return success;
	}

	/**
	 * Copy text file to output stream while replacing strings with values
	 *
	 * @param instream     input stream
	 * @param outstream    output strean
	 * @param sources      what to replace
	 * @param destinations what to replace it with
	 * @return true if successful
	 */
	private boolean copyStreamsReplace(final InputStream instream, final OutputStream outstream, final String[] sources, final String[] destinations)
	{
		try
		{
			final int buffercount = 512;
			final BufferedReader reader = new BufferedReader(new InputStreamReader(instream), buffercount);
			final PrintWriter writer = new PrintWriter(new BufferedOutputStream(outstream));
			String line;
			while ((line = reader.readLine()) != null)
			{
				for (int i = 0; i < sources.length; i++)
				{
					final int pos = line.indexOf(sources[i]);
					if (pos != -1)
					{
						line = line.substring(0, pos) + destinations[i] + line.substring(pos + sources[i].length());
					}
				}
				writer.println(line);
			}
			reader.close();
			writer.close();
		}
		catch (final Exception unused)
		{
			return false;
		}
		return true;
	}

	/**
	 * Copy streams
	 *
	 * @param instream  input stream
	 * @param outstream output stream
	 * @return true if successful
	 */
	static private boolean copyStreams(final InputStream instream, final OutputStream outstream)
	{
		try
		{
			final int bufferCount = 512;
			final BufferedInputStream reader = new BufferedInputStream(instream, bufferCount);
			final BufferedOutputStream writer = new BufferedOutputStream(outstream);
			final byte[] buffer = new byte[bufferCount];
			int count;
			while ((count = reader.read(buffer, 0, bufferCount)) != -1)
			{
				writer.write(buffer, 0, count);
			}
			reader.close();
			writer.close();
		}
		catch (final Exception unused)
		{
			return false;
		}
		return true;
	}
}
