/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.commons;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * @author bbou
 *
 */
public class Where
{
	static public String makeBaseLocation()
	{
		// base=parent(classes)/database
		final URL uRL = Where.class.getProtectionDomain().getCodeSource().getLocation();
		System.out.println("url " + uRL); //$NON-NLS-1$
		final String location0 = Where.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		System.out.println("path " + location0); //$NON-NLS-1$
		try
		{
			final String location = URLDecoder.decode(location0, "UTF-8"); //$NON-NLS-1$
			System.out.println("decoded " + location); //$NON-NLS-1$
			final String parent = new File(location).getParent();
			System.out.println("parent " + parent); //$NON-NLS-1$
			final File dir = new File(parent, "database/"); //$NON-NLS-1$
			System.out.println("database " + dir); //$NON-NLS-1$
			return dir.getAbsolutePath();
		}
		catch (UnsupportedEncodingException exception)
		{
			System.err.println(exception);
		}
		return null;
	}

	/**
	 * Main
	 * 
	 * @param args
	 *        args
	 */
	public static void main(String[] args)
	{
		makeBaseLocation();
	}
}
