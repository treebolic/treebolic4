/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.fungi.browser;

import java.awt.*;
import java.net.URL;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.commons.HyperlinkButton;
import treebolic.commons.Laf;
import treebolic.fungi.Browser;

/**
 * About dialog
 *
 * @author Bernard Bou
 */
public class AboutDialog extends treebolic.commons.AboutDialog
{
	/**
	 * Constructor
	 */
	public AboutDialog()
	{
		super(Messages.getString("AboutDialog.app"), Browser.getVersion(), false);
	}

	@NonNull
	@Override
	protected JPanel initialize(final String product, final String version, final boolean sysInfo)
	{
		@NonNull final JPanel panel = super.initialize(product, version, sysInfo);

		// Fungi
		@Nullable final URL iconUrl1 = AboutDialog.class.getResource("images/fungi.png");
		assert iconUrl1 != null;
		@NonNull final JLabel image = new JLabel(new ImageIcon(iconUrl1));
		@NonNull final HyperlinkButton dBButton = new HyperlinkButton(HyperlinkButton.makeURILabel("MycoDB®"), "http://mycodb.fr");
		@NonNull final JLabel dBLabel = new JLabel("MycoDB");

		// Project
		@NonNull final HyperlinkButton tWButton = new HyperlinkButton(HyperlinkButton.makeURILabel(Messages.getString("AboutDialog.app")), "http://treebolicfungi.sourceforge.net");

		// Android
		@Nullable final URL iconUrl2 = AboutDialog.class.getResource("images/android.png");
		assert iconUrl2 != null;
		@NonNull final JLabel androidImage = new JLabel("<HTML><FONT color=\"#808080\">" + Messages.getString("AboutDialog.also") + ' ' + "</FONT><B>Android</B></FONT></HTML>", new ImageIcon(iconUrl2), SwingConstants.CENTER);
		androidImage.setHorizontalTextPosition(SwingConstants.CENTER);
		androidImage.setVerticalTextPosition(SwingConstants.BOTTOM);

		// Google Play
		@NonNull final String googlePlayUri = "https://play.google.com/store/apps/details?id=org.treebolic.fungi.browser";
		@Nullable final URL googlePlayIconURl = AboutDialog.class.getResource("images/google-play.png");
		assert googlePlayIconURl != null;
		@NonNull final Icon googlePlayIcon = new ImageIcon(googlePlayIconURl);
		@NonNull final HyperlinkButton googlePlayButton = new HyperlinkButton(googlePlayIcon, googlePlayUri);

		panel.add(image, new GridBagConstraints(0, 30, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		panel.add(dBButton, new GridBagConstraints(0, 31, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
		panel.add(dBLabel, new GridBagConstraints(0, 32, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));

		panel.add(tWButton, new GridBagConstraints(0, 50, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 10, 10, 10), 0, 0));
		panel.add(androidImage, new GridBagConstraints(0, 60, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
		panel.add(googlePlayButton, new GridBagConstraints(0, 61, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
		return panel;
	}

	/**
	 * Standalone entry point
	 *
	 * @param args program arguments
	 */
	static public void main(@NonNull final String[] args)
	{
		Laf.lookAndFeel(args);
		@NonNull final AboutDialog dialog = new AboutDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		System.exit(0);
	}
}
