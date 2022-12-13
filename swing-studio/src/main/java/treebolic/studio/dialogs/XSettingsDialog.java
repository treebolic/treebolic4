/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio.dialogs;

import java.util.Properties;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.commons.Persist;
import treebolic.commons.SettingsDialog;

/**
 * Settings dialog
 *
 * @author Bernard Bou
 */
public class XSettingsDialog extends SettingsDialog
{
	private static final long serialVersionUID = 1L;

	/*
	 * Index and mask to insert in Settings dialog
	 */
	// static private final int SLOTIDX = 4;

	/**
	 * Constructor
	 *
	 * @param properties properties
	 */
	public XSettingsDialog(final Properties properties)
	{
		super(properties, SettingsDialog.BASE | SettingsDialog.IMAGEBASE | SettingsDialog.HELP | SettingsDialog.BROWSER);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.commons.SettingsDialog#initialize()
	 */
	@Override
	protected void initialize()
	{
		super.initialize();
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean flag)
	{
		super.setVisible(flag);
	}

	/**
	 * Main entry point
	 *
	 * @param args arguments
	 */
	static public void main(final String[] args)
	{
		UIManager.put("swing.boldMetal", false);
		@NonNull final Properties settings = Persist.getSettings("treebolic-studio");
		@NonNull final XSettingsDialog dialog = new XSettingsDialog(settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			Persist.saveSettings("treebolic-studio", settings);
		}
		System.exit(0);
	}
}
