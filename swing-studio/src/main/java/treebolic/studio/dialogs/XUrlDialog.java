/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.dialogs;

import java.util.Properties;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.commons.Persist;
import treebolic.commons.UrlDialog;

/**
 * URL dialog
 *
 * @author Bernard Bou
 */
public class XUrlDialog extends UrlDialog
{
	/**
	 * Constructor
	 *
	 * @param properties properties
	 */
	public XUrlDialog(final Properties properties)
	{
		super(properties);
	}

	/**
	 * Main
	 *
	 * @param args arguments
	 */
	static public void main(final String[] args)
	{
		UIManager.put("swing.boldMetal", false);
		@NonNull final Properties settings = Persist.getSettings("treebolic-studio");
		@NonNull final XUrlDialog dialog = new XUrlDialog(settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			System.out.println(settings.getProperty("openurl"));
			Persist.saveSettings("treebolic-studio", settings);
		}
		System.exit(0);
	}
}
