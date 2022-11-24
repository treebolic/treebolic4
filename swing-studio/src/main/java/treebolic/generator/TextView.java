/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator;

import java.awt.*;

import javax.swing.*;

/**
 * Text view
 *
 * @author Bernard Bou
 */
public class TextView extends JTextArea
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public TextView()
	{
		setText(Messages.getString("TextView.init"));
		setEditable(false);
		setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		setLineWrap(false);
		setMargin(new Insets(10, 20, 10, 10));
	}

	/**
	 * Update text view with text
	 *
	 * @param text text
	 */
	public void update(final String text)
	{
		setText(text);
		setCaretPosition(0);
	}
}