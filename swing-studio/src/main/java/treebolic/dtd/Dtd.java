/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.dtd;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * DTD
 *
 * @author Bernard Bou
 */
public class Dtd
{
	/**
	 * DTD file name
	 */
	static final String DTD_FILE = "Treebolic.dtd";

	/**
	 * Get DTD as string
	 *
	 * @return DTD as string
	 */
	static public String getString()
	{
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); BufferedOutputStream outputStream = new BufferedOutputStream(byteArrayOutputStream))
		{
			if (Dtd.copyToStream(outputStream))
			{
				return byteArrayOutputStream.toString();
			}
		}
		catch (IOException ioe)
		{
			//
		}
		return null;
	}

	/**
	 * Copy DTD to output stream
	 *
	 * @param outputStream output stream
	 * @return true if successful
	 */
	static public boolean copyToStream(final OutputStream outputStream)
	{
		final URL uRL = Dtd.class.getResource(Dtd.DTD_FILE);
		if (uRL == null)
		{
			return false;
		}
		try
		{
			final InputStream inputStream = uRL.openStream();
			return Dtd.copyStreams(inputStream, outputStream);
		}
		catch (final IOException ioe)
		{
			System.err.println("Can't find " + uRL + " " + ioe);
			return false;
		}
	}

	/**
	 * Copy DTD to UTF8 output stream
	 *
	 * @param outstream output stream
	 * @return true if successful
	 */
	@SuppressWarnings("UnusedReturnValue")
	static public boolean copyToUTF8Stream(final OutputStream outstream)
	{
		final String str = Dtd.getString();
		if (str == null)
		{
			return false;
		}
		try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outstream, StandardCharsets.UTF_8), false))
		{
			writer.print(str);
			return true;
		}
		catch (final Exception e)
		{
			// do nothing
		}
		return false;
	}

	/**
	 * Copy input stream to output stream
	 *
	 * @param instream  input stream
	 * @param outstream output stream
	 * @return true if successful
	 */
	static private boolean copyStreams(final InputStream instream, final OutputStream outstream)
	{
		final int bufferSize = 512;
		try (BufferedInputStream reader = new BufferedInputStream(instream, bufferSize); BufferedOutputStream writer = new BufferedOutputStream(outstream))
		{
			final byte[] buffer = new byte[bufferSize];
			int count;
			while ((count = reader.read(buffer, 0, bufferSize)) != -1)
			{
				writer.write(buffer, 0, count);
			}
			reader.close();
			writer.close();
			return true;
		}
		catch (final IOException unused)
		{
			// do nothing
		}
		return false;
	}
}
