/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator;

import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Document search
 *
 * @author Bernard Bou
 */
public class DocumentSearch
{
	/**
	 * Make image list
	 *
	 * @param document
	 *        document
	 * @return image list
	 */
	static public Set<String> makeImageList(final Document document)
	{
		final Set<String> set = new TreeSet<>();

		// <img>
		final NodeList nodes = document.getElementsByTagName("img"); 
		for (int i = 0; i < nodes.getLength(); i++)
		{
			final Element imageElement = (Element) nodes.item(i);
			final String imageFile = imageElement.getAttribute("src"); 
			if (!imageFile.isEmpty())
			{
				set.add(imageFile);
			}
		}
		return set;
	}
}
