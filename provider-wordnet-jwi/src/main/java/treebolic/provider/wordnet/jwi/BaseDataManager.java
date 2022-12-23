/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import treebolic.annotations.NonNull;

/**
 * Data manager
 *
 * @author Bernard Bou
 */
public abstract class BaseDataManager
{
	static private final String[] WORDNET_FILES = { //
			"data.noun", "data.verb", "data.adj", "data.adv", //
			"index.noun", "index.verb", "index.adj", "index.adv", "index.sense", //
			"noun.exc", "verb.exc", "adj.exc", "adv.exc", // "cousin.exc", //
			"sentidx.vrb", "sents.vrb", "verb.Framestext", //
			"cntlist", "cntlist.rev",};

	static private final Set<String> WORDNET_FILESET = new HashSet<>(Arrays.asList(WORDNET_FILES));

	/**
	 * Get data dir
	 *
	 * @param data      data (tag or url)
	 * @param cacheHome cache home directory (parent to cache)
	 * @return cached dictionary data
	 * @throws IOException io exception
	 */
	abstract public File getDataDir(final String data, final File cacheHome) throws IOException;

	/**
	 * Deploy zip to cache
	 *
	 * @param data      data (tag or url)
	 * @param cacheHome cache home directory (parent to cache)
	 * @return cached dictionary data
	 * @throws IOException io exception
	 */
	@SuppressWarnings("UnusedReturnValue")
	abstract public File deploy(final String data, final File cacheHome) throws IOException;

	/**
	 * Empty dir
	 *
	 * @param dir dir
	 */
	protected static void cleanup(final File dir)
	{
		System.out.println("Clean up " + dir);
		// clean up
		String[] entries = dir.list();
		if (entries != null)
		{
			for (String entry : entries)
			{
				final File file = new File(dir.getPath(), entry);
				if (WORDNET_FILESET.contains(file.getName()))
				{
					//noinspection ResultOfMethodCallIgnored
					file.delete();
				}
			}
		}
		final File file = new File(dir.getPath(), "build");
		if (file.exists())
		{
			//noinspection ResultOfMethodCallIgnored
			file.delete();
		}
	}

	/**
	 * Check for existence of files
	 *
	 * @param dir dir
	 * @return true if cache is valid
	 */
	protected static boolean check(final File dir)
	{
		// check if each file exists
		for (final String entry : BaseDataManager.WORDNET_FILES)
		{
			final File file = new File(dir, entry);
			if (!file.exists())
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Expand zip to dir
	 *
	 * @param zipUrl           zip file url
	 * @param pathPrefixFilter path prefix filter on entries
	 * @param destDir          destination dir
	 * @return dest dir
	 * @throws IOException io exception
	 */
	@NonNull
	@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
	protected static File expand(@NonNull final URL zipUrl, @SuppressWarnings("SameParameterValue") final String pathPrefixFilter, @NonNull final File destDir) throws IOException
	{
		System.out.println("Expand " + zipUrl);
		return BaseDataManager.expand(zipUrl.openStream(), pathPrefixFilter, destDir);
	}

	/**
	 * Expand zip stream to dir
	 *
	 * @param inputStream       zip file input stream
	 * @param pathPrefixFilter0 path prefix filter on entries
	 * @param destDir           destination dir
	 * @return dest dir
	 * @throws IOException io exception
	 */
	@NonNull
	protected static  File expand(@NonNull final InputStream inputStream, final String pathPrefixFilter0, @NonNull final File destDir) throws IOException
	{
		// prefix
		String pathPrefixFilter = pathPrefixFilter0;
		if (pathPrefixFilter != null && !pathPrefixFilter.isEmpty() && pathPrefixFilter.charAt(0) == File.separatorChar)
		{
			pathPrefixFilter = pathPrefixFilter.substring(1);
		}

		// create output directory is not exists
		// noinspection ResultOfMethodCallIgnored
		destDir.mkdir();

		// read and expand entries
		try (ZipInputStream zipInputStream = new ZipInputStream(inputStream))
		{
			// get the zipped file list entry
			final byte[] buffer = new byte[1024];
			ZipEntry entry = zipInputStream.getNextEntry();
			while (entry != null)
			{
				if (!entry.isDirectory())
				{
					final String entryName = entry.getName();
					if (pathPrefixFilter == null || pathPrefixFilter.isEmpty() || entryName.startsWith(pathPrefixFilter))
					{
						// flatten zip hierarchy
						final File file = new File(destDir + File.separator + new File(entryName).getName());

						// create all non exists folders else you will hit FileNotFoundException for compressed folder
						// noinspection ResultOfMethodCallIgnored
						new File(file.getParent()).mkdirs();

						// output

						// copy
						try (FileOutputStream outputStream = new FileOutputStream(file))
						{
							int len;
							while ((len = zipInputStream.read(buffer)) > 0)
							{
								outputStream.write(buffer, 0, len);
							}
						}
					}
				}
				zipInputStream.closeEntry();
				entry = zipInputStream.getNextEntry();
			}
		}
		return destDir;
	}
}
