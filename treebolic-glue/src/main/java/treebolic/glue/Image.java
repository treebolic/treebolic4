package treebolic.glue;

import java.net.URL;

/**
 * Image
 *
 * @author Bernard Bou
 */
public class Image implements treebolic.glue.iface.Image
{
	/**
	 * Make image
	 *
	 * @param resource
	 *        resource url
	 * @return Image
	 */
	static public Image make(final URL resource)
	{
		throw new NotImplementedException();
	}

	@Override
	public int getWidth()
	{
		throw new NotImplementedException();
	}

	@Override
	public int getHeight()
	{
		throw new NotImplementedException();
	}
}
