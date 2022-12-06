/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

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
	private static final long serialVersionUID = 1L;

	/**
	 * Author
	 */
	static final String author = "Bernard Bou";

	/**
	 * Email
	 */
	static final String email = "mailto:1313ou@gmail.com";

	/**
	 * Copyright notice
	 */
	static final String copyright = "Copyright © 2001-2017";

	/**
	 * Constructor
	 *
	 * @param product product
	 * @param version string
	 */
	public AboutDialog(final String product, final String version)
	{
		this(product, version, true);
	}

	/**
	 * Constructor
	 *
	 * @param product product
	 * @param version string
	 * @param sysInfo whether to add sysinfo
	 */
	public AboutDialog(final String product, final String version, final boolean sysInfo)
	{
		@NonNull final JPanel panel = initialize(product, version, sysInfo);
		setContentPane(panel);
	}

	/**
	 * Initialize component
	 *
	 * @param product product
	 * @param version string
	 * @param sysInfo whether to add sysinfo
	 * @return panel
	 */
	@NonNull
	protected JPanel initialize(final String product, final String version, final boolean sysInfo)
	{
		setTitle(Messages.getString("AboutDialog.title"));

		@NonNull final JLabel titleLabel = new JLabel(product);
		titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));

		@NonNull final HyperlinkButton authorLabel = new HyperlinkButton(HyperlinkButton.makeURILabel(AboutDialog.author), AboutDialog.email);

		@NonNull final JLabel copyrightLabel = new JLabel(AboutDialog.copyright);
		@NonNull final JLabel versionLabel = new JLabel(version);

		@NonNull final JLabel image = new JLabel();
		//noinspection ConstantConditions
		image.setIcon(new ImageIcon(AboutDialog.class.getResource("images/logo.png")));

		@NonNull final JButton oKButton = new JButton(Messages.getString("AboutDialog.ok"));
		oKButton.addActionListener(e -> setVisible(false));

		@NonNull final JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout());
		commandPanel.add(oKButton);

		@NonNull final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 10, 0, 10), 0, 0));
		panel.add(versionLabel, new GridBagConstraints(0, 10, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
		panel.add(image, new GridBagConstraints(0, 11, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
		panel.add(authorLabel, new GridBagConstraints(0, 21, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
		panel.add(copyrightLabel, new GridBagConstraints(0, 23, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
		panel.add(commandPanel, new GridBagConstraints(0, 100, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));

		if (sysInfo)
		{
			@NonNull final String props = JavaVersion.getJavaPropsString();
			@NonNull final JTextArea javaInfo = new JTextArea();
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
