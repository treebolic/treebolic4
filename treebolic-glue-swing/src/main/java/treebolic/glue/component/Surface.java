/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.glue.component;

import java.awt.*;

import javax.swing.*;

import treebolic.annotations.Nullable;
import treebolic.glue.EventListener;
import treebolic.glue.Graphics;


/**
 * Surface, derived from JPanel
 *
 * @author Bernard Bou
 */
public abstract class Surface extends javax.swing.JPanel implements Component, treebolic.glue.iface.component.Surface<Graphics, EventListener>
{
	/**
	 * Error margin when finding node
	 */
	static public final float FIND_DISTANCE_EPSILON_FACTOR = 1.5F;

	/**
	 * Hover linger detector
	 */
	static class GestureDetector
	{
		/**
		 * Hot node time slice
		 */
		static private final int HOTNODETIMESLICE = 1000;

		/**
		 * Timer to detect hover linger events
		 */
		static private Timer timer;

		/**
		 * Event listener
		 */
		private final EventListener listener;

		/**
		 * Constructor
		 */
		public GestureDetector(final EventListener listener)
		{
			this.listener = listener;
		}

		/**
		 * Start detector
		 */
		public void start()
		{
			if (GestureDetector.timer == null)
			{
				GestureDetector.timer = new Timer(GestureDetector.HOTNODETIMESLICE, e -> GestureDetector.this.listener.onLongHover());
			}
			GestureDetector.timer.setRepeats(true);

			// start
			GestureDetector.timer.start();
		}

		/**
		 * Stop detector
		 */
		public void stop()
		{
			if (GestureDetector.timer != null)
			{
				GestureDetector.timer.stop();
			}
		}
	}

	/**
	 * Event listener
	 */
	private EventListener eventListener;

	/**
	 * Gesture detector
	 */
	private GestureDetector gestureDetector;

	/**
	 * Constructor
	 *
	 * @param ignoredHandle Handle required for component creation (unused)
	 */
	public Surface(final Object ignoredHandle)
	{
		super();
		setLayout(null);
	}

	@Override
	public void paint(final java.awt.Graphics g)
	{
		paint(new Graphics(g));
	}

	@Override
	abstract public void paint(final Graphics g);

	// public void repaint();

	@Override
	public int getWidth()
	{
		return super.getSize().width;
	}

	@Override
	public int getHeight()
	{
		return super.getSize().height;
	}

	@Override
	public void setFireHover(final boolean flag)
	{
		if (this.eventListener == null)
		{
			return;
		}
		if (flag)
		{
			// timer creation
			if (this.gestureDetector == null)
			{
				this.gestureDetector = new GestureDetector(this.eventListener);
			}

			// start
			this.gestureDetector.start();
		}
		else
		{
			if (this.gestureDetector != null)
			{
				this.gestureDetector.stop();
			}
		}
	}

	@Override
	public void setCursor(final int cursor)
	{
		@Nullable Cursor awtCursor = null;
		switch (cursor)
		{
			case DEFAULTCURSOR:
				awtCursor = Cursor.getDefaultCursor();
				break;
			case HOTCURSOR:
				awtCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				break;
		}
		super.setCursor(awtCursor);
	}

	//	public void setToolTipText(final String string);

	@Override
	public void addEventListener(final EventListener listener)
	{
		this.eventListener = listener;
		super.addMouseListener(listener);
		super.addMouseMotionListener(listener);
	}

	@Override
	public float getFinderDistanceEpsilonFactor()
	{
		return FIND_DISTANCE_EPSILON_FACTOR;
	}
}
