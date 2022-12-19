package treebolic.wordnet.browser;

import java.awt.*;
import java.io.*;
import java.util.Properties;

import javax.swing.*;

/**
 * Settings dialog
 *
 * @author Bernard Bou
 */
public class LinkFilterSettingsDialog extends SettingsDialog
{
	/**
	 * Constructor
	 *
	 * @param settings settings
	 * @param owner    frame owner
	 */
	public LinkFilterSettingsDialog(final Properties settings, final Frame owner)
	{
		super(settings, owner, true, false);
	}

	/**
	 * Main
	 *
	 * @param args command line arguments
	 */
	static public void main(final String[] args)
	{
		final String path = System.getProperty("user.home") + File.separatorChar + ".treebolic-wordnet-browser";
		final Properties settings = new Properties();
		try
		{
			settings.load(new FileReader(path));
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}

		final LinkFilterSettingsDialog dialog = new LinkFilterSettingsDialog(settings, null);
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		if (dialog.ok)
		{
			try
			{
				settings.store(new FileWriter(path), "");
			}
			catch (IOException exception)
			{
				exception.printStackTrace();
			}
		}
		System.exit(0);
	}
}
