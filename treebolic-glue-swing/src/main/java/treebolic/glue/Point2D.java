package treebolic.glue;

public class Point2D extends java.awt.geom.Point2D.Double implements treebolic.glue.iface.Point2D
{
	private static final long serialVersionUID = 2080063772680665125L;

	public Point2D(final double x, final double y)
	{
		super(x, y);
	}

	/**
	 * Constructor
	 *
	 * @param p
	 *        awt.geom.Point2D
	 */
	public Point2D(final java.awt.geom.Point2D p)
	{
		super(p.getX(), p.getY());
	}

	// public double getX();

	// public double getY();

	@Override
	public String toString()
	{
		return String.format("%.1f, %.1f", this.x, this.y); //$NON-NLS-1$
	}
}
