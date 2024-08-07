/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.zip;

import org.w3c.dom.Document;
import treebolic.annotations.NonNull;
import treebolic.studio.DocumentSearch;
import treebolic.xml.transformer.DomTransformer;

import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        try (@NonNull ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(this.archive, true)))
        {
            // XML
            @NonNull final ZipEntry ze = new ZipEntry(this.entry);
            zos.putNextEntry(ze);
            new DomTransformer(false, "Treebolic.dtd").documentToStream(this.document, zos);
            zos.closeEntry();

            // images
            for (@NonNull final String imageFileName : DocumentSearch.makeImageList(this.document))
            {
                @NonNull final File imageFile = new File(imageFileName);
                @NonNull final String name = imageFile.getName();

                @NonNull final ZipEntry zei = new ZipEntry(name);

                @NonNull final URL url = this.imageBase == null ? new URL(imageFileName) : new URL(this.imageBase, imageFileName);

                // open image stream
                try (@NonNull final InputStream is = new BufferedInputStream(url.openStream()))
                {
                    // copy image stream into entry
                    zos.putNextEntry(zei);
                    ZipMaker.copyStreams(is, zos);
                    is.close();
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

    static private void copyStreams(@NonNull final InputStream instream, @NonNull final OutputStream outstream) throws IOException
    {
        @NonNull final byte[] buffer = new byte[1024];
        int length;
        while ((length = instream.read(buffer)) > 0)
        {
            outstream.write(buffer, 0, length);
        }
    }
}
