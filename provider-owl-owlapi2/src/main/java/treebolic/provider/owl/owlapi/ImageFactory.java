/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.provider.owl.owlapi;

import treebolic.annotations.NonNull;
import treebolic.glue.iface.Image;

/**
 * Image factory for OWL
 *
 * @author Bernard Bou
 */
public class ImageFactory
{
	/**
	 * ImageFactory
	 *
	 * @param imageUrls image urls
	 * @return images
	 */
	@NonNull
	public static Image[] makeImages(@NonNull final String[] imageUrls)
	{
		@NonNull Image[] images = new Image[imageUrls.length];
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
	static final Image[] images = makeImages(OwlModelFactory.images);
}
