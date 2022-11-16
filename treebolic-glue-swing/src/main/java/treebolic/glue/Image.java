/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Image, embeds awt's Image
 *
 * @author Bernard Bou
 */
public class Image implements treebolic.glue.iface.Image, Serializable
{
	private static final long serialVersionUID = -6088374866767037559L;

	public java.awt.Image image;

	/**
	 * Constructor from AWT image
	 *
	 * @param image AWT image
	 */
	public Image(final java.awt.Image image)
	{
		this.image = image;
	}

	/**
	 * Make image
	 *
	 * @param resource resource url
	 * @return Image
	 * @throws IOException io exception
	 */
	static public Image make(final URL resource) throws IOException
	{
		return new Image(ImageIO.read(resource));
	}

	/**
	 * Make image (caught exeption returns null)
	 *
	 * @param resource resource url
	 * @return Image
	 */
	static public Image try_make(final URL resource)
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

	public Graphics getGraphics()
	{
		return new Graphics(this.image.getGraphics());
	}

	// O V E R R I D E S E R I A L I Z A T I O N

	private void writeObject(final ObjectOutputStream out) throws IOException
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write((BufferedImage) this.image, "png", baos);
		final byte[] imageBytes = baos.toByteArray();
		out.writeObject(imageBytes);
	}

	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		final byte[] imageBytes = (byte[]) in.readObject();
		this.image = ImageIO.read(new ByteArrayInputStream(imageBytes));
	}
}
