/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio.dialogs;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.commons.OpenDialog;

/**
 * Open dialog
 *
 * @author Bernard Bou
 */
public class XOpenDialog extends OpenDialog
{
	/**
	 * Constructor
	 *
	 * @param provider provider
	 * @param source   source
	 * @param base     base
	 */
	public XOpenDialog(final String provider, final String source, final String base)
	{
		super(provider, source, base);
	}

	/**
	 * Main
	 *
	 * @param args arguments
	 */
	static public void main(final String[] args)
	{
		UIManager.put("swing.boldMetal", false);
		@NonNull final XOpenDialog dialog = new XOpenDialog(null, null, null);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			System.out.println("provider=" + dialog.provider);
			System.out.println("source=" + dialog.source);
		}
		System.exit(0);
	}
}
