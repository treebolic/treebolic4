/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Set;
import java.util.TreeSet;

import treebolic.annotations.NonNull;

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
	 * @param document document
	 * @return image list
	 */
	@NonNull
	static public Set<String> makeImageList(@NonNull final Document document)
	{
		@NonNull final Set<String> set = new TreeSet<>();

		// <img>
		final NodeList nodes = document.getElementsByTagName("img");
		for (int i = 0; i < nodes.getLength(); i++)
		{
			final Element imageElement = (Element) nodes.item(i);
			@NonNull final String imageFile = imageElement.getAttribute("src");
			if (!imageFile.isEmpty())
			{
				set.add(imageFile);
			}
		}
		return set;
	}
}
