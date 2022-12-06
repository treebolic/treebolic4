/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Class searcher
 *
 * @author Walter Angerer 'greenhorn'
 * @author Bernard Bou
 */
public class Searcher
{
	/**
	 * Find classes matching pattern in classpath
	 *
	 * @param pattern0 pattern
	 * @return result set
	 */
	@NonNull
	static public Set<String> findClasses(final String pattern0)
	{
		String pattern = pattern0;
		final Set<String> list = new TreeSet<>();
		pattern = pattern.replaceAll("\\\\.", "/");
		for (final String fileName : Searcher.findFiles(pattern + "\\.class$").keySet())
		{
			String className = fileName.replace('/', '.');
			if (File.separatorChar == '\\')
			{
				className = className.replace('\\', '.');
			}
			list.add(className.substring(0, fileName.length() - 6));
		}
		return list;
	}

	/**
	 * Find files matching pattern in classpath
	 *
	 * @param pattern pattern
	 * @return result set
	 */
	@NonNull
	static public Map<String, String> findFiles(final String pattern)
	{
		final String pattern2 = File.separatorChar == '/' ? pattern : pattern.replaceAll("/", "\\\\\\\\");
		@NonNull final Map<String, String> list = new TreeMap<>();
		final String classPath = System.getProperty("java.class.path");
		final String[] pathElements = classPath.split(File.pathSeparator);
		for (@NonNull final String pathElement : pathElements)
		{
			try
			{
				@NonNull final File file = new File(pathElement);
				if (file.isDirectory())
				{
					list.putAll(Searcher.findInDirectory(file, pattern2, pathElement));
				}
				else
				{
					list.putAll(Searcher.findInFile(file, pattern));
				}
			}
			catch (final IOException e)
			{
				System.err.println("Find files: " + e);
			}
		}
		return list;
	}

	/**
	 * Find in JAR file
	 *
	 * @param file    JAR file
	 * @param pattern pattern
	 * @return file with matching file path
	 * @throws IOException io exception
	 */
	@NonNull
	static private Map<String, String> findInFile(@NonNull final File file, final String pattern) throws IOException
	{
		final Map<String, String> map = new TreeMap<>();
		if (file.canRead() && file.getAbsolutePath().endsWith(".jar"))
		{
			@SuppressWarnings("resource") final JarFile jar = new JarFile(file);
			for (@NonNull final Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); )
			{
				final JarEntry entry = entries.nextElement();
				if (entry.getName().matches(pattern))
				{
					map.put(entry.getName(), jar.getName());
				}
			}
		}
		return map;
	}

	/**
	 * Find zip entries
	 *
	 * @param file            JAR file
	 * @param positivePattern pattern
	 * @param negativePattern pattern
	 * @return matching entry
	 * @throws IOException io exception
	 */
	@NonNull
	static public Collection<String> findZipEntries(@NonNull final File file, final String positivePattern, final String negativePattern) throws IOException
	{
		final Collection<String> set = new TreeSet<>();
		if (file.canRead())
		{
			@NonNull @SuppressWarnings("resource") final ZipFile zip = new ZipFile(file);
			for (final Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); )
			{
				final ZipEntry entry = entries.nextElement();
				@NonNull final String name = entry.getName();
				if (positivePattern != null && !name.matches(positivePattern))
				{
					continue;
				}
				if (negativePattern != null && name.matches(negativePattern))
				{
					continue;
				}
				set.add(entry.getName());
			}
		}
		return set;
	}

	/**
	 * Find in directory
	 *
	 * @param directory directory
	 * @param pattern   pattern
	 * @return file with matching file path
	 * @throws IOException io exception
	 */
	@NonNull
	static private Map<String, String> findInDirectory(@NonNull final File directory, @NonNull final String pattern, final String pathElement) throws IOException
	{
		@NonNull final Map<String, String> map = new TreeMap<>();
		@Nullable File[] files = directory.listFiles();
		if (files != null)
		{
			for (@NonNull final File directoryEntry : files)
			{
				if (directoryEntry.getAbsolutePath().matches(pattern))
				{
					final String fileName = directoryEntry.getAbsolutePath().substring(pathElement.length() + 1);
					map.put(fileName, pathElement);
				}
				else if (directoryEntry.isDirectory())
				{
					map.putAll(Searcher.findInDirectory(directoryEntry, pattern, pathElement));
				}
				else
				{
					map.putAll(Searcher.findInFile(directoryEntry, pattern));
				}
			}
		}
		return map;
	}

	/**
	 * Find files matching pattern in classpath
	 *
	 * @param pattern pattern
	 * @return list of urls
	 */
	@NonNull
	static public List<String> findFileUrls(@NonNull final String pattern)
	{
		return Searcher.toUrls(Searcher.findFiles(pattern));
	}

	/**
	 * Convert file map to list of urls
	 *
	 * @param map path to container map
	 * @return list of urls
	 */
	@NonNull
	static public List<String> toUrls(@NonNull final Map<String, String> map)
	{
		@NonNull final List<String> urls = new ArrayList<>();
		for (@NonNull final Map.Entry<String, String> entry : map.entrySet())
		{
			final String container = entry.getValue();
			final String path = entry.getKey();
			String urlString;
			if (container.endsWith(".jar"))
			{
				urlString = "jar:file:" + container + "!/" + path;
			}
			else
			{
				urlString = "file:" + container + '/' + path;
			}
			urls.add(urlString);
		}
		return urls;
	}

	/**
	 * Main
	 *
	 * @param args args
	 */
	public static void main(final String[] args)
	{
		for (final String className : Searcher.findClasses(".*\\.Provider"))
		{
			System.out.println("PROVIDER CLASS: " + className);
		}

		for (@NonNull final Map.Entry<String, String> entry : Searcher.findFiles(".*xsl$").entrySet())
		{
			System.out.println("XSL FILE: " + entry.getKey() + " @ " + entry.getValue());
		}
	}
}
