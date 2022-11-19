/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.dialogs;

import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.WindowConstants;

import treebolic.commons.Persist;
import treebolic.commons.SiteDialog;

/**
 * Site dialog
 *
 * @author Bernard Bou
 */
public class XSiteDialog extends SiteDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 *
	 * @param properties
	 *        properties
	 */
	public XSiteDialog(final Properties properties)
	{
		super(properties);
	}

	/**
	 * Main
	 *
	 * @param args
	 *        arguments
	 */
	static public void main(final String[] args)
	{
		UIManager.put("swing.boldMetal", false); //$NON-NLS-1$
		final Properties settings = Persist.getSettings("treebolic-generator"); //$NON-NLS-1$
		final XSiteDialog dialog = new XSiteDialog(settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			Persist.saveSettings("treebolic-generator", settings); //$NON-NLS-1$
		}
		System.exit(0);
	}
}
