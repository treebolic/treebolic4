/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.zip;

import org.w3c.dom.Document;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import treebolic.studio.DocumentSearch;
import treebolic.xml.transformer.DomTransformer;

/**
 * Utility class to pack all in a zip file
 *
 * @author Bernard Bou
 */
public class ZipMaker
{
	private final Document document;

	private final URL imageBase;

	private final File archive;

	private final String entry;

	/**
	 * Constructor
	 *
	 * @param document  document
	 * @param imageBase image base
	 * @param archive   archive
	 * @param entry     entry
	 */
	public ZipMaker(final Document document, final URL imageBase, final File archive, final String entry)
	{
		this.document = document;
		this.imageBase = imageBase;
		this.archive = archive;
		this.entry = entry;
	}

	/**
	 * Make
	 */
	public void make()
	{
		if (this.document == null)
		{
			return;
		}
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(this.archive, true)))
		{
			// XML
			final ZipEntry ze = new ZipEntry(this.entry);
			zos.putNextEntry(ze);
			new DomTransformer(false, "Treebolic.dtd").documentToStream(this.document, zos);
			zos.closeEntry();

			// images
			for (final String imageFileName : DocumentSearch.makeImageList(this.document))
			{
				final File imageFile = new File(imageFileName);
				final String name = imageFile.getName();

				final ZipEntry zei = new ZipEntry(name);

				final URL url = this.imageBase == null ? new URL(imageFileName) : new URL(this.imageBase, imageFileName);
				try
				{
					// open image stream
					final InputStream inputStream = new BufferedInputStream(url.openStream());

					// coppy image stream into entry
					zos.putNextEntry(zei);
					ZipMaker.copyStreams(inputStream, zos);
					inputStream.close();
					zos.flush();
					zos.closeEntry();
				}
				catch (Exception e)
				{
					System.err.println("Zip can't include " + imageFileName);
				}
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();//
		}
		//
	}

	static private void copyStreams(final InputStream instream, final OutputStream outstream) throws IOException
	{
		final byte[] buffer = new byte[1024];
		int length;
		while ((length = instream.read(buffer)) > 0)
		{
			outstream.write(buffer, 0, length);
		}
	}
}
