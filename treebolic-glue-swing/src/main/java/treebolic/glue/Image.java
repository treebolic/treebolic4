/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.glue;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

import javax.imageio.ImageIO;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Image, embeds awt's Image
 *
 * @author Bernard Bou
 */
public class Image implements treebolic.glue.iface.Image, Serializable
{
	private static final long serialVersionUID = 4L;

	/**
	 * AWT image
	 */
	@Nullable
	public java.awt.Image image;

	/**
	 * Constructor from AWT image
	 *
	 * @param image AWT image or null if it could not be loaded
	 */
	public Image(@Nullable final java.awt.Image image)
	{
		this.image = image;
	}

	/**
	 * Constructor from URL
	 *
	 * @param resource resource url
	 */
	public Image(@NonNull final URL resource)
	{
		this(makeOptional(resource));
	}

	/**
	 * Make image
	 *
	 * @param resource resource url
	 * @return awt Image or null if it fails
	 */
	@Nullable
	static public java.awt.Image makeOptional(@NonNull final URL resource)
	{
		try
		{
			return ImageIO.read(resource);
		}
		catch (IOException|IllegalArgumentException ignored)
		{
		}
		return null;
	}

	@Override
	public int getWidth()
	{
		if (this.image == null)
		{
			return 0;
		}
		return this.image.getWidth(null);
	}

	@Override
	public int getHeight()
	{
		if (this.image == null)
		{
			return 0;
		}
		return this.image.getHeight(null);
	}

	/**
	 * Get graphics context
	 *
	 * @return graphics context
	 */
	@Nullable
	public Graphics getGraphics()
	{
		if (this.image == null)
		{
			return null;
		}
		return new Graphics(this.image.getGraphics());
	}

	// O V E R R I D E S E R I A L I Z A T I O N

	private void writeObject(@NonNull final ObjectOutputStream out) throws IOException
	{
		@Nullable byte[] imageBytes = null;
		if (this.image != null)
		{
			@NonNull final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write((BufferedImage) this.image, "png", baos);
			imageBytes = baos.toByteArray();
		}
		out.writeObject(imageBytes);
	}

	private void readObject(@NonNull final ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		this.image = null;
		final byte[] imageBytes = (byte[]) in.readObject();
		if (imageBytes != null)
		{
			this.image = ImageIO.read(new ByteArrayInputStream(imageBytes));
		}
	}
}
