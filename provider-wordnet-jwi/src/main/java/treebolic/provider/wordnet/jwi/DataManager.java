/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
public class DataManager
{
	/**
	 * WordNet 3.1
	 */
	static public final String WN31_TAG = "WN31";

	/**
	 * Open English WordNet
	 */
	static public final String OEWN_TAG = "OEWN";

	static private final String WN31_ARCHIVE = "/wordnet31.zip";

	static private final String OEWN_ARCHIVE = "/oewn2022.zip";

	static private final String CACHESUBDIR = "wordnet";

	static private final String[] WORDNET_FILES = { //
			"data.noun", "data.verb", "data.adj", "data.adv", //
			"index.noun", "index.verb", "index.adj", "index.adv", "index.sense", //
			"noun.exc", "verb.exc", "adj.exc", "adv.exc", "cousin.exc", //
			"sentidx.vrb", "sents.vrb", "verb.Framestext", //
			"cntlist", "cntlist.rev",};

	static private final Set<String> WORDNET_FILESET = new HashSet<>(Arrays.asList(WORDNET_FILES));

	static private final DataManager INSTANCE = new DataManager();

	/**
	 * Get instance of singleton data manager
	 *
	 * @return instance of singleton data manager
	 */
	static public DataManager getInstance()
	{
		return INSTANCE;
	}

	/**
	 * Constructor
	 */
	private DataManager()
	{
	}

	/**
	 * Get data dir
	 *
	 * @param data      data (tag or url)
	 * @param cacheHome cache home directory (parent to cache)
	 * @return cached dictionary data
	 * @throws IOException io exception
	 */
	public File getDataDir(final String data, final File cacheHome) throws IOException
	{
		URL zipUrl = getSourceZipURL(data);
		return setup(zipUrl, cacheHome, false);
	}

	/**
	 * Deploy zip to cache
	 *
	 * @param data      data (tag or url)
	 * @param cacheHome cache home directory (parent to cache)
	 * @return cached dictionary data
	 * @throws IOException io exception
	 */
	@SuppressWarnings("UnusedReturnValue")
	public File deploy(final String data, final File cacheHome) throws IOException
	{
		URL zipUrl = getSourceZipURL(data);
		return setup(zipUrl, cacheHome, true);
	}

	/**
	 * Setup
	 *
	 * @param zipUrl    source zip url
	 * @param cacheHome cache home directory (parent to cache)
	 * @param doCleanup whether to clean up
	 * @return cached dictionary data
	 */
	@NonNull
	private File setup(@NonNull final URL zipUrl, @NonNull final File cacheHome, final boolean doCleanup) throws IOException
	{
		final File cache = new File(cacheHome, DataManager.CACHESUBDIR);

		// directory
		boolean doUnzip = true;
		if (cache.exists())
		{
			if (doCleanup)
			// force clean up, doUnzip remains true
			{
				cleanup(cache);
			}
			else
			// doUnzip may toggle if check says so
			{
				doUnzip = !DataManager.check(cache);
			}
		}
		else
		{
			// create output directory is not exists, doUnzip remains true
			// noinspection ResultOfMethodCallIgnored
			cache.mkdir();
		}

		// unzip
		if (doUnzip)
		{
			DataManager.expand(zipUrl, null, cache);
		}

		// return cache
		return cache;
	}

	/**
	 * Empty dir
	 *
	 * @param dir dir
	 */
	static private void cleanup(final File dir)
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
	static private boolean check(final File dir)
	{
		// check if each file exists
		for (final String entry : DataManager.WORDNET_FILES)
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
	 */
	@NonNull
	@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
	static private File expand(@NonNull final URL zipUrl, @SuppressWarnings("SameParameterValue") final String pathPrefixFilter, @NonNull final File destDir) throws IOException
	{
		System.out.println("Expand " + zipUrl);
		return DataManager.expand(zipUrl.openStream(), pathPrefixFilter, destDir);
	}

	/**
	 * Expand zip stream to dir
	 *
	 * @param inputStream       zip file input stream
	 * @param pathPrefixFilter0 path prefix filter on entries
	 * @param destDir           destination dir
	 * @return dest dir
	 */
	@NonNull
	static private File expand(@NonNull final InputStream inputStream, final String pathPrefixFilter0, @NonNull final File destDir) throws IOException
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

	/**
	 * Make source zip url
	 *
	 * @param data0 data
	 * @return source zip url
	 * @throws MalformedURLException malformed url exception
	 */
	private URL getSourceZipURL(String data0) throws MalformedURLException
	{
		String data = data0;
		if (data == null)
		{
			data = WN31_TAG;
		}

		switch (data)
		{
			case WN31_TAG:
				// source archive in class path
				return DataManager.class.getResource(WN31_ARCHIVE);
			case OEWN_TAG:
				// source archive in class path
				return DataManager.class.getResource(OEWN_ARCHIVE);
			default:
				// source archive at URL
				System.out.println(data);
				return new URL(data);
		}
	}
}
