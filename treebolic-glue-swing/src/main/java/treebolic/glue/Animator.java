/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue;

import java.awt.event.ActionEvent;

/**
 * Animator implements animation, derived from Timer
 *
 * @author Bernard Bou
 */
public class Animator extends javax.swing.Timer implements java.awt.event.ActionListener, treebolic.glue.iface.Animator<ActionListener>
{
	private static final long serialVersionUID = -6793510103759256318L;

	/**
	 * Timer time slice
	 */
	static private final int ANIMATIONTIMESLICE = 100;

	/**
	 * Start delay
	 */
	private ActionListener animation;

	/**
	 * Constructor
	 */
	public Animator()
	{
		super(Animator.ANIMATIONTIMESLICE, null);
		setRepeats(true);
		setCoalesce(true);
	}

	/**
	 * Run animation
	 *
	 * @param animation  animation callback
	 * @param steps      number of steps
	 * @param startDelay start delay
	 * @return true if successful
	 */
	@Override
	public boolean run(final ActionListener animation, final int steps, final int startDelay)
	{
		this.animation = animation;
		super.addActionListener(this);

		super.setInitialDelay(startDelay);
		super.start();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e)
	{
		if (!this.animation.onAction())
		{
			stop();
			super.removeActionListener(this);
		}
	}
}
