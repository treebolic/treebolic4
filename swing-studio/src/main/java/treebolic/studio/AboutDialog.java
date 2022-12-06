/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio;

import javax.swing.*;

import treebolic.Studio;
import treebolic.annotations.NonNull;

/**
 * About dialog
 *
 * @author Bernard Bou
 */
public class AboutDialog extends treebolic.commons.AboutDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public AboutDialog()
	{
		super(Messages.getString("AboutDialog.title"), Studio.getVersion());
	}

	/**
	 * Standalone entry point
	 *
	 * @param args program arguments
	 */
	static public void main(final String[] args)
	{
		@NonNull final AboutDialog dialog = new AboutDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		System.exit(0);
	}
}
