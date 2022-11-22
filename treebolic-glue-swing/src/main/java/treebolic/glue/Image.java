/*
 * Copyright (c) 2022. Bernard Bou
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
	private static final long serialVersionUID = -6088374866767037559L;

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
	 * @throws IOException io exception
	 */
	public Image(@NonNull final URL resource)
	{
		this(make(resource));
	}

	/**
	 * Make image
	 *
	 * @param resource resource url
	 * @return Image
	 */
	@Nullable
	static public java.awt.Image make(@NonNull final URL resource)
	{
		try
		{
			return ImageIO.read(resource);
		}
		catch (IOException ignored)
		{
		}
		return null;
	}

	/**
	 * Make image (caught exception returns null)
	 *
	 * @param resource resource url
	 * @return Image or null if it fails
	 */
	@Nullable
	static public Image makeOptional(@NonNull final URL resource)
	{
		try
		{
			return new Image(ImageIO.read(resource));
		}
		catch (final IOException exception)
		{
			return null;
		}
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
