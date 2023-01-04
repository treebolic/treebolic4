/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.fungi.browser;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Source data deployer
 *
 * @author Bernard Bou
 */
public class Deployer
{
	static private final String[] DATA_FILES = {"fungi.db", //
	};

	/**
	 * Dir to write data to
	 */
	private final File dir;

	/**
	 * Constructor
	 *
	 * @param dir0 parent dir to write data to
	 */
	public Deployer(final File dir0)
	{
		this.dir = dir0;
	}

	/**
	 * Data status
	 *
	 * @return data status
	 */
	public boolean check()
	{
		return check(this.dir);
	}

	/**
	 * Clean up data
	 */
	public void cleanup()
	{
		@Nullable File[] files = this.dir.listFiles();
		if (files != null)
		{
			for (@NonNull final File file : files)
			{
				//noinspection ResultOfMethodCallIgnored
				file.delete();
			}
		}
	}

	/**
	 * Check for existence of files
	 *
	 * @param dir dir
	 * @return true if cache is valid
	 */
	static public boolean check(final File dir)
	{
		// check if each file exists
		for (@NonNull final String entry : DATA_FILES)
		{
			@NonNull final File file = new File(dir, entry);
			if (!file.exists())
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Process internal data
	 *
	 * @return true if successful
	 */
	@SuppressWarnings("UnusedReturnValue")
	public boolean expand()
	{
		try
		{
			@Nullable final URL zipUrl = Deployer.class.getResource("/data.zip");
			assert zipUrl != null;
			expand(zipUrl, null, this.dir);
			return true;
		}
		catch (final Exception exception)
		{
			//
		}
		return false;
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
	@SuppressWarnings("UnusedReturnValue")
	static public File expand(@NonNull final URL zipUrl, final String pathPrefixFilter, @NonNull final File destDir) throws IOException
	{
		return expand(zipUrl.openStream(), pathPrefixFilter, destDir);
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
	static public File expand(@NonNull final InputStream inputStream, final String pathPrefixFilter0, @NonNull final File destDir) throws IOException
	{
		// prefix
		String pathPrefixFilter = pathPrefixFilter0;
		if (pathPrefixFilter != null && !pathPrefixFilter.isEmpty() && pathPrefixFilter.charAt(0) == File.separatorChar)
		{
			pathPrefixFilter = pathPrefixFilter.substring(1);
		}

		// create output directory is not exists
		//noinspection ResultOfMethodCallIgnored
		destDir.mkdir();

		// read and expand entries
		try (@NonNull ZipInputStream zipInputStream = new ZipInputStream(inputStream))
		{
			// get the zipped file list entry
			@NonNull final byte[] buffer = new byte[1024];
			@Nullable ZipEntry entry = zipInputStream.getNextEntry();
			while (entry != null)
			{
				if (!entry.isDirectory())
				{
					@NonNull final String entryName = entry.getName();
					if (pathPrefixFilter == null || entryName.startsWith(pathPrefixFilter))
					{
						// flatten zip hierarchy
						@NonNull final File file = new File(destDir + File.separator + new File(entryName).getName());

						// create all non exists folders else you will hit FileNotFoundException for compressed folder
						//noinspection ResultOfMethodCallIgnored
						new File(file.getParent()).mkdirs();

						// output

						// copy
						try (@NonNull FileOutputStream outputStream = new FileOutputStream(file))
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
	 * Get query files
	 *
	 * @return query files
	 */
	@NonNull
	public String[] getQueryFiles()
	{
		@NonNull final List<String> result = new ArrayList<>();
		@Nullable File[] files = this.dir.listFiles();
		if (files != null)
		{
			for (@NonNull final File file : files)
			{
				@NonNull final String name = file.getName();
				if (name.matches("query.*.properties"))
				{
					result.add(name);
				}
			}
		}
		return result.toArray(new String[0]);
	}

	/**
	 * Get query descriptions
	 *
	 * @param queryFiles query files
	 * @return descriptions
	 */
	@NonNull
	public String[] getQueryDescriptions(@NonNull final String[] queryFiles)
	{
		@NonNull final String[] descriptions = new String[queryFiles.length];
		for (int i = 0; i < queryFiles.length; i++)
		{
			@NonNull File file = new File(this.dir, queryFiles[i]);
			try (@NonNull FileInputStream inputStream = new FileInputStream(file); @NonNull BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
			{
				// Read first line
				@NonNull String line = reader.readLine().trim();
				descriptions[i] = line.startsWith("#") ? line.substring(1).trim() : "";
				if (descriptions[i].isEmpty())
				{
					descriptions[i] = file.getName();
				}
			}
			catch (Exception e)
			{
				System.err.println("Error: " + e.getMessage());
			}
			// Close the reader
			//
			// Close the input stream
			//
		}
		return descriptions;
	}

}
