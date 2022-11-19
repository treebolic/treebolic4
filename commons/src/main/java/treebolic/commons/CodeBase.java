/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.commons;

import java.net.URL;

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
		URL url = Persist.class.getResource("CodeBase.class"); 
		if (url != null)
		{
			String urlString = url.toString();
			final int index = urlString.lastIndexOf("/treebolic/commons/CodeBase.class");
			if (index != -1)
			{
				urlString = urlString.substring(0, index);
				if (urlString.startsWith("jar:")) 
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
