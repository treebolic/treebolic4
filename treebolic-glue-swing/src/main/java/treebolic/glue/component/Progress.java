/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue.component;

import java.awt.*;

import javax.swing.*;

import treebolic.annotations.NonNull;

/**
 * Progress panel, derived from JPanel
 *
 * @author Bernard Bou
 */
public class Progress extends JPanel implements Component, treebolic.glue.iface.component.Progress
{
	/**
	 * Stop icon
	 */
	@SuppressWarnings("ConstantConditions")
	static private final Icon stopIcon = new ImageIcon(Progress.class.getResource("images/progress_stop.png"));

	/**
	 * Working icon
	 */
	@SuppressWarnings("ConstantConditions")
	static private final Icon workingIcon = new ImageIcon(Progress.class.getResource("images/progress_wait.gif"));

	/**
	 * Progress bar
	 */
	@NonNull
	private final JProgressBar progress;

	/**
	 * Label
	 */
	@NonNull
	private final JLabel label;

	/**
	 * Label
	 */
	@NonNull
	private final JLabel text;

	/**
	 * Constructor
	 *
	 * @param ignoredHandle Opaque handle required for component creation
	 */
	public Progress(final Object ignoredHandle)
	{
		super();

		setBackground(Color.WHITE);

		this.progress = new JProgressBar(0, 100);
		this.progress.setMaximumSize(Constants.DIM_PROGRESS_PROGRESS);
		this.progress.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		this.progress.setStringPainted(true);
		this.progress.setVisible(false);

		this.label = new JLabel();
		this.label.setText("Treebolic");
		this.label.setFont(Constants.FONT_PROGRESS_LABEL);
		this.label.setPreferredSize(Constants.DIM_PROGRESS);
		this.label.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		this.label.setHorizontalAlignment(SwingConstants.CENTER);
		this.label.setVerticalTextPosition(SwingConstants.TOP);
		this.label.setHorizontalTextPosition(SwingConstants.CENTER);

		this.text = new JLabel();
		this.text.setText(Messages.getString("Progress.starting"));
		this.text.setFont(Constants.FONT_PROGRESS_TEXT);
		this.text.setPreferredSize(Constants.DIM_PROGRESS_TEXT);
		this.text.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		this.text.setBackground(Color.BLUE);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalGlue());
		add(this.label);
		add(this.text);
		add(this.progress);
		add(Box.createVerticalGlue());
	}

	/**
	 * Put message
	 *
	 * @param message message
	 */
	@Override
	public void put(final String message, final boolean fail)
	{
		@NonNull final Runnable routine = () -> {
			Progress.this.text.setText(message);
			Progress.this.label.setIcon(fail ? Progress.stopIcon : Progress.workingIcon);
			Progress.this.progress.setValue(fail ? 0 : Progress.this.progress.getValue() + 10);
			Progress.this.progress.setVisible(Progress.this.progress.getValue() > 0);
		};
		SwingUtilities.invokeLater(routine);
	}
}
