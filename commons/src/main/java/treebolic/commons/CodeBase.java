/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.commons;

/**
 * DetermineCode base
 *
 * @author Bernard Bou
 */
public class CodeBase
{
	/**
	 * Get code base
	 *
	 * @return code base
	 */
	static public String getJarLocation()
	{
		String urlString = Persist.class.getResource("CodeBase.class").toString(); //$NON-NLS-1$
		if (urlString != null)
		{
			final int index = urlString.lastIndexOf("/treebolic/commons/CodeBase.class"); //$NON-NLS-1$
			if (index != -1)
			{
				urlString = urlString.substring(0, index);
				if (urlString.startsWith("jar:")) //$NON-NLS-1$
				{
					final int index2 = urlString.lastIndexOf('/');
					urlString = urlString.substring(4, index2);
					return urlString;
				}
			}
		}
		return null;
	}
}
