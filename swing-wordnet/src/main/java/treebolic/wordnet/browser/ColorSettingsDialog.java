package treebolic.wordnet.browser;

import java.awt.*;
import java.util.Properties;

import javax.swing.*;

import treebolic.annotations.NonNull;

/**
 * Settings dialog
 *
 * @author Bernard Bou
 */
public class ColorSettingsDialog extends SettingsDialog
{
	/**
	 * Constructor
	 *
	 * @param settings settings
	 * @param owner    frame owner
	 */
	public ColorSettingsDialog(final Properties settings, final Frame owner)
	{
		super(settings, owner, false, true);
	}

	/**
	 * Main
	 *
	 * @param args command line arguments
	 */
	static public void main(final String[] args)
	{
		@NonNull final Properties settings = new Properties();

		@NonNull final ColorSettingsDialog dialog = new ColorSettingsDialog(settings, null);
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);

		System.exit(0);
	}
}
