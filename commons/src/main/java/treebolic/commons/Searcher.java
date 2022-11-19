/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
	 * @param pattern pattern
	 * @return result set
	 */
	static public Set<String> findClasses(final String pattern0)
	{
		String pattern = pattern0;
		final Set<String> list = new TreeSet<String>();
		pattern = pattern.replaceAll("\\\\.", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		for (final String fileName : Searcher.findFiles(pattern + "\\.class$").keySet()) //$NON-NLS-1$
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
	static public Map<String, String> findFiles(final String pattern)
	{
		final String pattern2 = File.separatorChar == '/' ? pattern : pattern.replaceAll("/", "\\\\\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
		final Map<String, String> list = new TreeMap<String, String>();
		final String classPath = System.getProperty("java.class.path"); //$NON-NLS-1$
		final String[] pathElements = classPath.split(File.pathSeparator);
		for (final String pathElement : pathElements)
		{
			try
			{
				final File file = new File(pathElement);
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
				System.err.println("Find files: " + e.toString()); //$NON-NLS-1$
			}
		}
		return list;
	}

	/**
	 * Find in JAR file
	 *
	 * @param file JAR file
	 * @param pattern pattern
	 * @return file with matching file path
	 * @throws IOException
	 */
	static private Map<String, String> findInFile(final File file, final String pattern) throws IOException
	{
		final Map<String, String> map = new TreeMap<String, String>();
		if (file.canRead() && file.getAbsolutePath().endsWith(".jar")) //$NON-NLS-1$
		{
			@SuppressWarnings("resource")
			final JarFile jar = new JarFile(file);
			for (final Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();)
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
	 * @param file JAR file
	 * @param positivePattern pattern
	 * @param positivePattern pattern
	 * @return matching entry
	 * @throws IOException
	 */
	static public Collection<String> findZipEntries(final File file, final String positivePattern, final String negativePattern) throws IOException
	{
		final Collection<String> set = new TreeSet<String>();
		if (file.canRead())
		{
			@SuppressWarnings("resource")
			final ZipFile zip = new ZipFile(file);
			for (final Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();)
			{
				final ZipEntry entry = entries.nextElement();
				final String name = entry.getName();
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
	 * @param pattern pattern
	 * @return file with matching file path
	 * @throws IOException
	 */
	static private Map<String, String> findInDirectory(final File directory, final String pattern, final String pathElement) throws IOException
	{
		final Map<String, String> map = new TreeMap<String, String>();
		for (final File directoryEntry : directory.listFiles())
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
		return map;
	}

	/**
	 * Find files matching pattern in classpath
	 *
	 * @param pattern pattern
	 * @return list of urls
	 */
	static public List<String> findFileUrls(final String pattern)
	{
		return Searcher.toUrls(Searcher.findFiles(pattern));
	}

	/**
	 * Convert file map to list of urls
	 *
	 * @param map path to container map
	 * @return list of urls
	 */
	static public List<String> toUrls(final Map<String, String> map)
	{
		final List<String> urls = new ArrayList<String>();
		for (final Map.Entry<String, String> entry : map.entrySet())
		{
			final String container = entry.getValue();
			final String path = entry.getKey();
			String urlString = null;
			if (container.endsWith(".jar")) //$NON-NLS-1$
			{
				urlString = "jar:file:" + container + "!/" + path; //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				urlString = "file:" + container + '/' + path; //$NON-NLS-1$
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
		for (final String className : Searcher.findClasses(".*\\.Provider")) //$NON-NLS-1$
		{
			System.out.println("PROVIDER CLASS: " + className); //$NON-NLS-1$
		}

		for (final Map.Entry<String, String> entry : Searcher.findFiles(".*xsl$").entrySet()) //$NON-NLS-1$
		{
			System.out.println("XSL FILE: " + entry.getKey() + " @ " + entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
