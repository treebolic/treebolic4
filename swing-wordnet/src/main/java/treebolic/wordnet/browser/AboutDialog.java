/**
 * Title : Treebolic browser
 * Description : Treebolic browser
 * Version : 3.x
 * Copyright :	(c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.wordnet.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.wordnet.browser;

import java.awt.*;
import java.util.Properties;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.commons.HyperlinkButton;
import treebolic.commons.Laf;
import treebolic.commons.Persist;
import treebolic.wordnet.Browser;

/**
 * About dialog
 *
 * @author Bernard Bou
 */
public class AboutDialog extends treebolic.commons.AboutDialog
{
	private final String dataVersion;

	/**
	 * Constructor
	 */
	public AboutDialog(final String dataVersion)
	{
		super(Messages.getString("AboutDialog.app"), Browser.getVersion(), false);
		this.dataVersion = dataVersion;
	}

	@NonNull
	@Override
	protected JPanel initialize(final String product, final String version, final boolean sysInfo)
	{
		final JPanel panel = super.initialize(product, version, sysInfo);

		// WordNet
		final JLabel image = new JLabel(new ImageIcon(AboutDialog.class.getResource("images/wordnet.png")));
		final HyperlinkButton wordNetButton = "ewn".equals(this.dataVersion) ?
				new HyperlinkButton(HyperlinkButton.makeURILabel("WordNet® 3.1"), "http://wordnet.princeton.edu") :
				new HyperlinkButton(HyperlinkButton.makeURILabel("English WordNet®"), "https://github.com/globalwordnet/english-wordnet");
		final JLabel wordNetLabel = new JLabel("WordNet lexical database for English");

		// JWI
		final HyperlinkButton jWIButton = new HyperlinkButton(HyperlinkButton.makeURILabel("JWI"), "http://projects.csail.mit.edu/jwi");//$NON-NLS-2$
		final JLabel jWILabel = new JLabel("Java WordNet Interface");

		// TWN
		final HyperlinkButton tWButton = new HyperlinkButton(HyperlinkButton.makeURILabel("Treebolic WordNet"), "http://treebolicwordnet.sourceforge.net");//$NON-NLS-2$

		// Android
		final JLabel androidImage = new JLabel("<HTML><FONT color=\"#808080\">" + Messages.getString("AboutDialog.also") + "</FONT><B>Android</B></FONT></HTML>", new ImageIcon(AboutDialog.class.getResource("images/android.png")), SwingConstants.CENTER);
		androidImage.setHorizontalTextPosition(SwingConstants.CENTER);
		androidImage.setVerticalTextPosition(SwingConstants.BOTTOM);

		// Google Play
		final String googlePlayUri = "https://play.google.com/store/apps/details?id=org.treebolic.wordnet.browser";
		final Icon googlePlayIcon = new ImageIcon(AboutDialog.class.getResource("images/google-play.png"));
		final HyperlinkButton googlePlayButton = new HyperlinkButton(googlePlayIcon, googlePlayUri);

		panel.add(image, new GridBagConstraints(0, 30, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		panel.add(wordNetButton, new GridBagConstraints(0, 31, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
		panel.add(wordNetLabel, new GridBagConstraints(0, 32, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));

		panel.add(jWIButton, new GridBagConstraints(0, 40, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
		panel.add(jWILabel, new GridBagConstraints(0, 41, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));

		panel.add(tWButton, new GridBagConstraints(0, 50, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 10, 10, 10), 0, 0));
		panel.add(androidImage, new GridBagConstraints(0, 60, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
		panel.add(googlePlayButton, new GridBagConstraints(0, 61, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
		return panel;
	}

	/**
	 * Standalone entry point
	 *
	 * @param args
	 *        program arguments
	 */
	static public void main(final String[] args)
	{
		Laf.lookAndFeel(args);
		final Properties settings = treebolic.browser2.MainFrame.makeSettings(Persist.loadSettings(MainFrame.getStaticPersistName()), args);
		final AboutDialog dialog = new AboutDialog(settings.getProperty("data", null));
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		System.exit(0);
	}
}
