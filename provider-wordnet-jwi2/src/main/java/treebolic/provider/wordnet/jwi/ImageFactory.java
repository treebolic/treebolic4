/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi;

import treebolic.annotations.NonNull;

/**
 * Image factory for WordNet
 *
 * @author Bernard Bou
 */
@SuppressWarnings({"SameParameterValue"})
public abstract class ImageFactory
{
	/**
	 * ImageFactory
	 *
	 * @param imageUrls image urls
	 * @return images
	 */
	@NonNull
	public static treebolic.glue.iface.Image[] makeImages(@NonNull final String[] imageUrls)
	{
		@NonNull treebolic.glue.iface.Image[] images = new treebolic.glue.iface.Image[imageUrls.length];
		for (int i = 0; i < imageUrls.length; i++)
		{
			images[i] = new treebolic.glue.Image(ImageFactory.class.getResource("images/" + imageUrls[i]));
		}
		return images;
	}

	// D E C O R A T I O N   M E M B E R S

	/**
	 * Images
	 */
	public static final treebolic.glue.iface.Image[] images = makeImages(BaseProvider.images);
}
