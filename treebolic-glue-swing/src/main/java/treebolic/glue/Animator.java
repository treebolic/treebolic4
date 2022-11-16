/**
 * Title : Treebolic
 * Description : Treebolic
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 *
 * Update : Mon Mar 10 00:00:00 CEST 2008
 */
package treebolic.glue;

import java.awt.event.ActionEvent;

/**
 * Animator implements animation
 *
 * @author Bernard Bou
 */
public class Animator extends javax.swing.Timer implements java.awt.event.ActionListener, treebolic.glue.iface.Animator<ActionListener>
{
	private static final long serialVersionUID = -6793510103759256318L;

	/**
	 * Timee time slice
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
	 * @param animation
	 *        animation callback
	 * @param steps
	 *        number of steps
	 * @param startDelay
	 *        start delay
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
