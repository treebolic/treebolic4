/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.commons;

import javax.swing.*;

import treebolic.annotations.NonNull;

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
     * @param message message
     */
    static public void warn(@NonNull final String message)
    {
        SwingUtilities.invokeLater(() -> {
            @NonNull final String[] lines = message.split("\n");
            JOptionPane.showMessageDialog(null, lines, Messages.getString("Interact.title"), JOptionPane.WARNING_MESSAGE);
        });
    }

    /**
     * Confirm
     *
     * @param message message
     * @return true if confirmed
     */
    static public boolean confirm(final String[] message)
    {
        final int value = JOptionPane.showConfirmDialog(null, message, Messages.getString("Interact.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return value == JOptionPane.YES_OPTION;
    }

    /**
     * Ask
     *
     * @param message message
     * @param initial initial value
     * @return input
     */
    static public String ask2(@NonNull final String message, final String initial)
    {
        @NonNull final String[] lines = message.split("\n");
        return JOptionPane.showInputDialog(null, lines, initial);
    }
}
