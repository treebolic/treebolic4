/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * About dialog
 *
 * @author Bernard Bou
 */
public class AboutDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Author
	 */
	static final String author = "Bernard Bou"; //$NON-NLS-1$

	/**
	 * Email
	 */
	static final String email = "mailto:1313ou@gmail.com"; //$NON-NLS-1$

	/**
	 * Copyright notice
	 */
	static final String copyright = "Copyright © 2001-2017"; //$NON-NLS-1$

	/**
	 * Constructor
	 *
	 * @param product
	 *        product
	 * @param version
	 *        string
	 */
	public AboutDialog(final String product, final String version)
	{
		this(product, version, true);
	}

	/**
	 * Constructor
	 *
	 * @param product
	 *        product
	 * @param version
	 *        string
	 * @param sysInfo
	 *        whether to add sysinfo
	 */
	public AboutDialog(final String product, final String version, final boolean sysInfo)
	{
		final JPanel panel = initialize(product, version, sysInfo);
		setContentPane(panel);
	}

	/**
	 * Initialize component
	 *
	 * @param product
	 *        product
	 * @param version
	 *        string
	 * @param sysInfo
	 *        whether to add sysinfo
	 */
	protected JPanel initialize(final String product, final String version, final boolean sysInfo)
	{
		setTitle(Messages.getString("AboutDialog.title")); //$NON-NLS-1$

		final JLabel titleLabel = new JLabel(product);
		titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));

		final HyperlinkButton authorLabel = new HyperlinkButton(HyperlinkButton.makeURILabel(AboutDialog.author), AboutDialog.email);

		final JLabel copyrightLabel = new JLabel(AboutDialog.copyright);
		final JLabel versionLabel = new JLabel(version);

		final JLabel image = new JLabel();
		image.setIcon(new ImageIcon(AboutDialog.class.getResource("images/logo.png"))); //$NON-NLS-1$

		final JButton oKButton = new JButton(Messages.getString("AboutDialog.ok")); //$NON-NLS-1$
		oKButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				setVisible(false);
			}
		});

		final JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout());
		commandPanel.add(oKButton);

		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 10, 0, 10), 0, 0));
		panel.add(versionLabel, new GridBagConstraints(0, 10, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
		panel.add(image, new GridBagConstraints(0, 11, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
		panel.add(authorLabel, new GridBagConstraints(0, 21, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
		panel.add(copyrightLabel, new GridBagConstraints(0, 23, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
		panel.add(commandPanel, new GridBagConstraints(0, 100, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));

		if (sysInfo)
		{
			final String props = JavaVersion.getJavaPropsString();
			final JTextArea javaInfo = new JTextArea();
			javaInfo.setEditable(false);
			javaInfo.setText(props);
			javaInfo.setCaretPosition(0);
			javaInfo.setLineWrap(false);
			panel.add(new JScrollPane(javaInfo), new GridBagConstraints(0, 30, 1, 1, 1., 1., GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
		}
		return panel;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			pack();
			Utils.center(this);
		}
		super.setVisible(flag);
	}
}
