/**
 * Title : Treebolic application
 * Description : Treebolic application
 * Version : 3.x
 * Copyright :	(c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 *
 */
package treebolic.application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * About dialog
 *
 * @author Bernard Bou
 */
public class AboutDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

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
		final JLabel title = new JLabel();
		title.setText(Messages.getString("AboutDialog.title")); //$NON-NLS-1$
		title.setFont(new Font(Font.DIALOG, 1, 18));
		title.setOpaque(true);
		title.setBackground(Color.RED);
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);

		final JLabel description = new JLabel();
		description.setText(Messages.getString("AboutDialog.app")); //$NON-NLS-1$
		description.setFont(new Font(Font.DIALOG, 0, 10)); 
		description.setHorizontalAlignment(SwingConstants.CENTER);
		description.setAlignmentX(Component.CENTER_ALIGNMENT);

		final JLabel version = new JLabel();
		version.setText("version 3.9.0"); //$NON-NLS-1$
		version.setFont(new Font(Font.DIALOG, 0, 10)); 
		version.setHorizontalAlignment(SwingConstants.CENTER);
		version.setAlignmentX(Component.CENTER_ALIGNMENT);

		final JLabel author = new JLabel();
		author.setText("Bernard Bou <1313ou@gmail.com>"); //$NON-NLS-1$
		author.setFont(new Font(Font.DIALOG, 0, 10)); 
		author.setHorizontalAlignment(SwingConstants.CENTER);
		author.setAlignmentX(Component.CENTER_ALIGNMENT);

		final JPanel panel = new JPanel();
		panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		final BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
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
	 * @param component
	 *        component to center
	 */
	static public void center(final Component component)
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
	 * @param args
	 *        arguments
	 */
	static public void main(final String[] args)
	{
		final AboutDialog dialog = new AboutDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		System.exit(0);
	}
}
