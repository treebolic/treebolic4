package treebolic.wordnet.browser;

import java.awt.*;
import java.io.*;
import java.util.Properties;

import javax.swing.*;

import treebolic.annotations.NonNull;

/**
 * Settings dialog
 *
 * @author Bernard Bou
 */
public class RelationFilterSettingsDialog extends SettingsDialog
{
	/**
	 * Constructor
	 *
	 * @param settings settings
	 * @param owner    frame owner
	 */
	public RelationFilterSettingsDialog(final Properties settings, final Frame owner)
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
		@NonNull final String path = System.getProperty("user.home") + File.separatorChar + ".treebolic-wordnet-browser";
		@NonNull final Properties settings = new Properties();
		try
		{
			settings.load(new FileReader(path));
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}

		@NonNull final RelationFilterSettingsDialog dialog = new RelationFilterSettingsDialog(settings, null);
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
