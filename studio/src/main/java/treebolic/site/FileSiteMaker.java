/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.site;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.w3c.dom.Document;

public class FileSiteMaker extends SiteMaker
{
	/**
	 * Path url
	 */
	private final String pathUrl;

	/**
	 * Extra path
	 */
	private String xtraPath = ""; 

	/**
	 * Constructor
	 *
	 * @param document         document
	 * @param repositoryFolder repository folder (source)
	 * @param imageFolder      image folder (source)
	 * @param path             path (source)
	 * @param htmlFile         HTML file (target)
	 * @param xmlFile          XML file (target)
	 * @param title            HTML file title (target)
	 * @throws IOException        io exception
	 */
	public FileSiteMaker(final Document document, final String repositoryFolder, final String imageFolder, final String path, final String htmlFile, final String xmlFile, final String title) throws IOException
	{
		super(document, repositoryFolder, imageFolder, htmlFile, xmlFile, title);

		final File folder = new File(path);
		if (!folder.exists() || !folder.isDirectory())
		{
			throw new FileNotFoundException(path + " not found"); 
		}

		this.pathUrl = folder.toURI().toURL().toString();
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.site.SiteMaker#connect()
	 */
	@Override
	public boolean connect()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.site.SiteMaker#disconnect()
	 */
	@Override
	public boolean disconnect()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.site.SiteMaker#makeOutStream(java.lang.String)
	 */
	@Override
	public OutputStream makeOutStream(final String filename) throws IOException, URISyntaxException
	{
		final URL pathUrl = new URL(this.pathUrl);
		final URL url = new URL(pathUrl, this.xtraPath + filename);
		final File file = new File(url.toURI());
		return new FileOutputStream(file);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.site.SiteMaker#changeFolder(java.lang.String)
	 */
	@Override
	public boolean changeFolder(final String relativeFolder) throws MalformedURLException, URISyntaxException
	{
		if (relativeFolder == null || relativeFolder.isEmpty())
		{
			return true;
		}
		final URL pathUrl = new URL(this.pathUrl);
		final URL url = new URL(pathUrl, relativeFolder);
		final File folder = new File(url.toURI());
		if (!folder.exists())
		{
			if (!folder.mkdirs())
			{
				return false;
			}
		}
		// update extra path
		this.xtraPath = relativeFolder;
		if (!"".equals(this.xtraPath) && !this.xtraPath.endsWith("/"))  
		{
			this.xtraPath += "/"; 
		}
		return true;
	}
}
