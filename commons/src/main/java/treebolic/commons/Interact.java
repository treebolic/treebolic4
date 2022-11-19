/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.commons;

import javax.swing.JOptionPane;

/**
 * Interact with user
 * 
 * @author Bernard Bou
 */
public class Interact
{
	/**
	 * Warn
	 *
	 * @param message
	 *        message
	 */
	static public void warn(final String message)
	{
		final String[] lines = message.split("\n"); //$NON-NLS-1$
		JOptionPane.showMessageDialog(null, lines, Messages.getString("Interact.title"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
	}

	/**
	 * Confirm
	 *
	 * @param message
	 *        message
	 * @return true if confirmed
	 */
	static public boolean confirm(final String[] message)
	{
		final int value = JOptionPane.showConfirmDialog(null, message, Messages.getString("Interact.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
		return value == JOptionPane.YES_OPTION;
	}

	/**
	 * Ask
	 *
	 * @param message
	 *        message
	 * @return input
	 */
	static public String ask2(final String message, final String initial)
	{
		final String[] lines = message.split("\n"); //$NON-NLS-1$
		return JOptionPane.showInputDialog(null, lines, initial);
	}
}
