/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.dialogs;

import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.WindowConstants;

import treebolic.commons.Persist;
import treebolic.commons.UrlDialog;

/**
 * URL dialog
 *
 * @author Bernard Bou
 */
public class XUrlDialog extends UrlDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 *
	 * @param properties
	 *        properties
	 */
	public XUrlDialog(final Properties properties)
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
		UIManager.put("swing.boldMetal", false); 
		final Properties settings = Persist.getSettings("treebolic-studio");
		final XUrlDialog dialog = new XUrlDialog(settings);
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
