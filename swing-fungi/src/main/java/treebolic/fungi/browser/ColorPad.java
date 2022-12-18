/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.fungi.browser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.*;

/**
 * Color pad
 *
 * @author Bernard Bou
 */
public class ColorPad extends JComponent
{
	/**
	 * Background color
	 */
	private Color color;

	/**
	 * Action listeners
	 */
	private final Vector<ActionListener> listeners;

	/**
	 * Constructor
	 */
	public ColorPad()
	{
		super();
		setPreferredSize(Constants.DIM_COLORPAD);
		this.listeners = new Vector<>();
		addMouseListener(new MouseAdapter()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void mousePressed(final MouseEvent e)
			{
				for (final ActionListener listener : ColorPad.this.listeners)
				{
					listener.actionPerformed(new ActionEvent(ColorPad.this, 0, "pressed"));
				}
			}
		});
	}

	@Override
	public void setBackground(final Color color)
	{
		this.color = color;
	}

	@Override
	public Color getBackground()
	{
		return this.color;
	}

	@Override
	public void paintComponent(final Graphics g)
	{
		final Dimension d = this.getSize();
		if (this.color != null)
		{
			g.setColor(this.color);
			g.fill3DRect(0, 0, d.width, d.height, true);
		}
		else
		{
			g.drawLine(0, 0, d.width, d.height);
			g.drawLine(0, /* 0 + */ d.height, d.width, 0);
			g.draw3DRect(0, 0, d.width, d.height, true);
		}
	}

	/**
	 * Add action listener
	 *
	 * @param listener action listener
	 */
	public void addActionListener(final ActionListener listener)
	{
		this.listeners.add(listener);
	}
}
