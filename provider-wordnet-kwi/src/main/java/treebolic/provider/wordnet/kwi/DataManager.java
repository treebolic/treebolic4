/*
 * Copyright (c) 2019-2025. Bernard Bou
 */

package treebolic.provider.wordnet.kwi;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Data manager
 *
 * @author Bernard Bou
 */
public class DataManager extends BaseDataManager
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

	static private final String OEWN_ARCHIVE = "/oewn2024.zip";

	static private final String CACHESUBDIR = "wordnet";

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
	 * @param sourceData source data url
	 * @param cacheHome  cache home directory (parent to cache)
	 * @return cached dictionary data
	 * @throws IOException io exception
	 */
	@NonNull
	@Override
	public File getDataDir(@NonNull final URL sourceData, @NonNull final File cacheHome) throws IOException
	{
		return setup(sourceData, cacheHome, false);
	}

	/**
	 * Deploy zip to cache
	 *
	 * @param sourceData source data url
	 * @param cacheHome  cache home directory (parent to cache)
	 * @return cached dictionary data
	 * @throws IOException io exception
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	@Override
	public File deploy(@NonNull final URL sourceData, @NonNull final File cacheHome) throws IOException
	{
		return setup(sourceData, cacheHome, true);
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
		@NonNull final File cache = new File(cacheHome, DataManager.CACHESUBDIR);

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
			expand(zipUrl, null, cache);
		}

		// return cache
		return cache;
	}

	/**
	 * Make source zip url
	 *
	 * @param data0 data
	 * @return source zip url
	 * @throws MalformedURLException malformed url exception
	 */
	@Nullable
	public static URL getSourceZipURL(@Nullable String data0) throws MalformedURLException
	{
		@Nullable String data = data0;
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
