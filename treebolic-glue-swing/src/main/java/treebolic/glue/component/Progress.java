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
package treebolic.glue.component;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Progress panel
 *
 * @author Bernard Bou
 */
public class Progress extends JPanel implements Component, treebolic.glue.iface.component.Progress
{
	private static final long serialVersionUID = 1L;

	/**
	 * Stop icon
	 */
	static private Icon stopIcon = new ImageIcon(Progress.class.getResource("images/progress_stop.png")); //$NON-NLS-1$

	/**
	 * Working icon
	 */
	static private Icon workingIcon = new ImageIcon(Progress.class.getResource("images/progress_wait.gif")); //$NON-NLS-1$

	/**
	 * Progress bar
	 */
	private final JProgressBar progress;

	/**
	 * Label
	 */
	private final JLabel label;

	/**
	 * Label
	 */
	private final JLabel text;

	/**
	 * Constructor
	 *
	 * @param handle
	 *        Handle required for component creation (unused)
	 */
	public Progress(final Object handle)
	{
		super();

		setBackground(Color.WHITE);

		this.progress = new JProgressBar(0, 100);
		this.progress.setMaximumSize(Constants.DIM_PROGRESS_PROGRESS);
		this.progress.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		this.progress.setStringPainted(true);
		this.progress.setVisible(false);

		this.label = new JLabel();
		this.label.setText("Treebolic"); //$NON-NLS-1$
		this.label.setFont(Constants.FONT_PROGRESS_LABEL); 
		this.label.setPreferredSize(Constants.DIM_PROGRESS);
		this.label.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		this.label.setHorizontalAlignment(SwingConstants.CENTER);
		this.label.setVerticalTextPosition(SwingConstants.TOP);
		this.label.setHorizontalTextPosition(SwingConstants.CENTER);

		this.text = new JLabel();
		this.text.setText(Messages.getString("Progress.starting")); //$NON-NLS-1$
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
	 * @param message
	 *        message
	 */
	@Override
	public void put(final String message, final boolean fail)
	{
		final Runnable routine = new Runnable()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void run()
			{
				Progress.this.text.setText(message);
				Progress.this.label.setIcon(fail ? Progress.stopIcon : Progress.workingIcon);
				Progress.this.progress.setValue(fail ? 0 : Progress.this.progress.getValue() + 10);
				Progress.this.progress.setVisible(Progress.this.progress.getValue() > 0);
			}
		};
		SwingUtilities.invokeLater(routine);
	}
}
