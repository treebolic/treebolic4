/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.commons;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Where (base location)
 */
public class Where
{
	/**
	 * Make base location
	 *
	 * @return base location
	 */
	@Nullable
	@SuppressWarnings("UnusedReturnValue")
	static public String makeBaseLocation()
	{
		// base=parent(classes)/database
		final URL uRL = Where.class.getProtectionDomain().getCodeSource().getLocation();
		System.out.println("url " + uRL);
		final String location0 = Where.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		System.out.println("path " + location0);
		try
		{
			final String location = URLDecoder.decode(location0, "UTF-8");
			System.out.println("decoded " + location);
			final String parent = new File(location).getParent();
			System.out.println("parent " + parent);
			@NonNull final File dir = new File(parent, "database/");
			System.out.println("database " + dir);
			return dir.getAbsolutePath();
		}
		catch (UnsupportedEncodingException exception)
		{
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * Main
	 *
	 * @param args args
	 */
	public static void main(String[] args)
	{
		makeBaseLocation();
	}
}
