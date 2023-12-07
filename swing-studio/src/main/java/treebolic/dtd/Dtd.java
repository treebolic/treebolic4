/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.dtd;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

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
    @Nullable
    static public String getString()
    {
        try (@NonNull ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); @NonNull BufferedOutputStream outputStream = new BufferedOutputStream(byteArrayOutputStream))
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
    static public boolean copyToStream(@NonNull final OutputStream outputStream)
    {
        @Nullable final URL uRL = Dtd.class.getResource(Dtd.DTD_FILE);
        if (uRL == null)
        {
            return false;
        }
        try (@NonNull InputStream is = uRL.openStream())
        {
            return Dtd.copyStreams(is, outputStream);
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
    static public boolean copyToUTF8Stream(@NonNull final OutputStream outstream)
    {
        @Nullable final String str = Dtd.getString();
        if (str == null)
        {
            return false;
        }
        try (@NonNull PrintWriter writer = new PrintWriter(new OutputStreamWriter(outstream, StandardCharsets.UTF_8), false))
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
    static private boolean copyStreams(@NonNull final InputStream instream, @NonNull final OutputStream outstream)
    {
        final int bufferSize = 512;
        try (@NonNull BufferedInputStream reader = new BufferedInputStream(instream, bufferSize); @NonNull BufferedOutputStream writer = new BufferedOutputStream(outstream))
        {
            @NonNull final byte[] buffer = new byte[bufferSize];
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
