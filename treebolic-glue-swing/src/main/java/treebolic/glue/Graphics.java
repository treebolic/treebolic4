/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.glue.iface.Image;

/**
 * Graphics, embeds awt's Graphics
 *
 * @author Bernard Bou
 */
public class Graphics implements treebolic.glue.iface.Graphics
{
	/**
	 * Plain style
	 */
	static public final int PLAIN = 0;

	/**
	 * Bold style
	 */
	static public final int BOLD = 1;

	static private final float SWING_FONT_FACTOR = 1.5F;

	static private final float[] DASHPATTERN = {10, 4};

	static private final float[] DOTPATTERN = {1, 3};

	static private final Stroke solidStroke = new BasicStroke(1.0F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0F);

	static private final Stroke dotStroke = new BasicStroke(1.0F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0F, Graphics.DOTPATTERN, 0);

	static private final Stroke dashStroke = new BasicStroke(1.0F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0F, Graphics.DASHPATTERN, 0);

	static private final Map<Float, Font> fontCache = new HashMap<>();

	/**
	 * Graphics2D context
	 */
	public final Graphics2D g;

	/**
	 * Baseline font
	 */
	@Nullable
	public Font font;

	/**
	 * Where stroke is pushed
	 */
	private Stroke stroke;

	/**
	 * Transform stack
	 */
	@NonNull
	private final Stack<AffineTransform> transformStack;

	// C O N S T R U C T

	/**
	 * Constructor
	 *
	 * @param graphics awt graphics2D
	 */
	public Graphics(final Graphics2D graphics)
	{
		this.g = graphics;
		this.g.setPaintMode();
		this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.font = null;
		this.transformStack = new Stack<>();
	}

	/**
	 * Constructor
	 *
	 * @param graphics graphics context
	 */
	public Graphics(final java.awt.Graphics graphics)
	{
		this((Graphics2D) graphics);
	}

	// D R A W

	/*
	 * (non-Javadoc)
	 * @see treebolic.glue.iface.Graphics#drawBackgroundColor(java.lang.Object, int, int, int, int)
	 */
	@Override
	public void drawBackgroundColor(@Nullable final Integer color, final int left, final int top, final int width, final int height)
	{
		setColor(color);
		fillRectangle(left, top, width, height);
	}

	@Override
	public void drawLine(final int x, final int y, final int x2, final int y2)
	{
		this.g.drawLine(x, y, x2, y2);
	}

	@Override
	public void drawArc(final float x, final float y, final float w, final float h, final float startAngle, final float extentAngle)
	{
		@NonNull final java.awt.geom.Arc2D a = new java.awt.geom.Arc2D.Float(x, y, w, h, startAngle, extentAngle, java.awt.geom.Arc2D.OPEN);
		this.g.draw(a);
		// DO NOT USE: rounding error on angles (a fraction of a degree may matter)
		// this.g.drawArc(x, y, w, h, (int) startAngle, (int) extentAngle);
	}

	@Override
	public void drawPolyline(final int[] x, final int[] y, final int length)
	{
		this.g.drawPolyline(x, y, length);
	}

	@Override
	public void fillRectangle(final int left, final int top, final int width, final int height)
	{
		this.g.fillRect(left, top, width, height);
	}

	@Override
	public void fillRoundRectangle(final int x, final int y, final int w, final int h, final int rx, final int ry)
	{
		this.g.fillRoundRect(x, y, w, h, rx, ry);
	}

	@Override
	public void drawRoundRectangle(final int x, final int y, final int w, final int h, final int rx, final int ry)
	{
		this.g.drawRoundRect(x, y, w, h, rx, ry);
	}

	@Override
	public void fillPolygon(final int[] x, final int[] y, final int length)
	{
		this.g.fillPolygon(x, y, length);
	}

	@Override
	public void drawPolygon(final int[] x, final int[] y, final int length)
	{
		this.g.drawPolygon(x, y, length);
	}

	@Override
	public void fillOval(final float x, final float y, final float xradius, final float yradius)
	{
		this.g.fillOval((int) x, (int) y, (int) xradius, (int) yradius);
	}

	@Override
	public void drawOval(final float x, final float y, final float xradius, final float yradius)
	{
		this.g.drawOval((int) x, (int) y, (int) xradius, (int) yradius);
	}

	@Override
	public void drawString(final String str, final int x, final int y)
	{
		this.g.drawString(str, x, y);
	}

	@Override
	public void drawImage(@NonNull final Image image0, final int x, final int y)
	{
		assert image0 instanceof treebolic.glue.Image;
		final treebolic.glue.Image image = (treebolic.glue.Image) image0;
		if (image.image == null)
		{
			return;
		}
		this.g.drawImage(image.image, x, y, null);
	}

	@Override
	public void drawImage(@NonNull final Image image0, final int x, final int y, final int w, final int h)
	{
		assert image0 instanceof treebolic.glue.Image;
		final treebolic.glue.Image image = (treebolic.glue.Image) image0;
		if (image.image == null)
		{
			return;
		}
		final java.awt.Image awtImage = image.image.getScaledInstance(w, h, java.awt.Image.SCALE_FAST);
		this.g.drawImage(awtImage, x, y, null);
	}

	// S E T T I N G S

	@Override
	public void setColor(@Nullable final Integer color)
	{
		this.g.setColor(ColorKit.toAWT(color));
	}

	@Nullable
	@Override
	public Integer getColor()
	{
		java.awt.Color color = this.g.getColor();
		return ColorKit.fromAWT(color);
	}

	@Override
	public void setFont(final String fontface, final int style)
	{
		this.font = new Font(fontface, style, 20); // arbitrary size
		Graphics.fontCache.clear();
	}

	@Override
	public void setTextSize(final float size)
	{
		Font font = Graphics.fontCache.get(size);
		if (font == null)
		{
			// cache miss
			if (this.font == null)
			{
				return;
			}
			font = this.font.deriveFont(size * SWING_FONT_FACTOR);
			Graphics.fontCache.put(size, font);
		}
		this.g.setFont(font);
	}

	@Override
	public int getDescent()
	{
		final FontMetrics metrics = this.g.getFontMetrics();
		return metrics.getDescent();
	}

	@Override
	public int getAscent()
	{
		final FontMetrics metrics = this.g.getFontMetrics();
		return metrics.getAscent();
	}

	// @formatter:off
	/*
	public int getLeading()
	{
		final FontMetrics metrics = this.g.getFontMetrics();
		return metrics.getLeading();
	}

	public int getHeight()
	{
		final FontMetrics metrics = this.g.getFontMetrics();
		return metrics.getHeight();
	}

	public int[] getMetrics()
	{
		final FontMetrics metrics = this.g.getFontMetrics();
		int[] result = new int[4];
		result[0] = metrics.getLeading();
		result[1] = metrics.getAscent();
		result[2] = metrics.getDescent();
		result[3] = metrics.getHeight();
		return result;
	}
	*/
	// @formatter:on

	@Override
	public int stringWidth(@NonNull final String string)
	{
		final FontMetrics metrics = this.g.getFontMetrics();
		return metrics.stringWidth(string);
	}

	@Override
	public void setStroke(final int stroke0, final int width)
	{
		@Nullable Stroke stroke = null;
		switch (stroke0)
		{
			case treebolic.glue.iface.Graphics.SOLID:
				stroke = width <= 1 ? Graphics.solidStroke : new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0F);
				break;
			case treebolic.glue.iface.Graphics.DOT:
				stroke = width <= 1 ? Graphics.dotStroke : new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0F, Graphics.DOTPATTERN, 0);
				break;
			case treebolic.glue.iface.Graphics.DASH:
				stroke = width <= 1 ? Graphics.dashStroke : new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10.0F, Graphics.DASHPATTERN, 0);
				break;
		}
		this.g.setStroke(stroke);
	}

	@Override
	public void pushStroke()
	{
		this.stroke = this.g.getStroke();
	}

	@Override
	public void popStroke()
	{
		this.g.setStroke(this.stroke);
	}

	// T R A N S F O R M

	@Override
	public void pushMatrix()
	{
		this.transformStack.push(this.g.getTransform());
	}

	@Override
	public void popMatrix()
	{
		this.g.setTransform(this.transformStack.pop());
	}

	@Override
	public void translate(final float x, final float y)
	{
		this.g.translate(x, y);
	}

	@Override
	public void rotate(final float theta, final float x, final float y)
	{
		// final AffineTransform transform = new AffineTransform(this.g.getTransform());
		// transform.translate(x, y);
		// transform.rotate(theta);
		// this.g.setTransform(transform);

		this.g.translate(x, y);
		this.g.rotate(theta);
	}

	@Override
	public void scale(final float factor, final float x, final float y)
	{
		this.g.translate(x, y);
		this.g.scale(factor, factor);
	}

	// H E L P E R

	// --Commented out by Inspection START (11/16/22, 2:29 PM):
	//	static public int convertStyleToAwt(final int style)
	//	{
	//		switch (style)
	//		{
	//		case Graphics.PLAIN:
	//			return Font.PLAIN;
	//		case Graphics.BOLD:
	//			return Font.BOLD;
	//		default:
	//			return -1;
	//		}
	//	}
	// --Commented out by Inspection STOP (11/16/22, 2:29 PM)

	// --Commented out by Inspection START (11/16/22, 2:28 PM):
	//	static public int convertStyleFromAwt(final int style)
	//	{
	//		switch (style)
	//		{
	//		case Font.PLAIN:
	//			return Graphics.PLAIN;
	//		case Font.BOLD:
	//			return Graphics.BOLD;
	//		default:
	//			return -1;
	//		}
	//	}
	// --Commented out by Inspection STOP (11/16/22, 2:28 PM)
}
