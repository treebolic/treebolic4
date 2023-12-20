/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.application;

import java.awt.*;

import javax.swing.*;

import treebolic.annotations.NonNull;

/**
 * About dialog
 *
 * @author Bernard Bou
 */
public class AboutDialog extends JDialog
{
	/**
	 * Constructor
	 */
	public AboutDialog()
	{
		super();
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		@NonNull final JLabel title = new JLabel();
		title.setText(Messages.getString("AboutDialog.title"));
		title.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
		title.setOpaque(true);
		title.setBackground(Color.RED);
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);

		@NonNull final JLabel description = new JLabel();
		description.setText(Messages.getString("AboutDialog.app"));
		description.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
		description.setHorizontalAlignment(SwingConstants.CENTER);
		description.setAlignmentX(Component.CENTER_ALIGNMENT);

		@NonNull final JLabel version = new JLabel();
		version.setText("version 4.0.0");
		version.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
		version.setHorizontalAlignment(SwingConstants.CENTER);
		version.setAlignmentX(Component.CENTER_ALIGNMENT);

		@NonNull final JLabel author = new JLabel();
		author.setText("Bernard Bou <1313ou@gmail.com>");
		author.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
		author.setHorizontalAlignment(SwingConstants.CENTER);
		author.setAlignmentX(Component.CENTER_ALIGNMENT);

		@NonNull final JPanel panel = new JPanel();
		panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		@NonNull final BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);
		panel.add(Box.createVerticalGlue());
		panel.add(title);
		panel.add(Box.createVerticalGlue());
		panel.add(description);
		panel.add(version);
		panel.add(author);

		setContentPane(panel);
		center(this);
		pack();
	}

	/**
	 * Center on screen
	 *
	 * @param component component to center
	 */
	static public void center(@NonNull final Component component)
	{
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension componentSize = component.getSize();
		if (componentSize.height > screenSize.height)
		{
			componentSize.height = screenSize.height;
		}
		if (componentSize.width > screenSize.width)
		{
			componentSize.width = screenSize.width;
		}
		component.setLocation((screenSize.width - componentSize.width) / 2, (screenSize.height - componentSize.height) / 2);
	}

	/**
	 * Main
	 *
	 * @param args arguments
	 */
	static public void main(final String[] args)
	{
		@NonNull final AboutDialog dialog = new AboutDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		System.exit(0);
	}
}
