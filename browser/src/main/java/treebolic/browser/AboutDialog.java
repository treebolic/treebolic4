/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.browser;

import javax.swing.WindowConstants;

import treebolic.Browser;

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
		super(Messages.getString("AboutDialog.app"), Browser.getVersion()); 
	}

	/**
	 * Standalone entry point
	 *
	 * @param args
	 *        program arguments
	 */
	static public void main(final String[] args)
	{
		final AboutDialog dialog = new AboutDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		System.exit(0);
	}
}
