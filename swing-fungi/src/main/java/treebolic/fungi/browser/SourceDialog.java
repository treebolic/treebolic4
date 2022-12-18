/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.fungi.browser;

import treebolic.commons.RadioChoiceDialog;

/**
 * Import dialog
 *
 * @author Bernard Bou
 */
public class SourceDialog extends RadioChoiceDialog
{
	static String[] labels = null;

	static String[] values = null;

	/**
	 * Constructor
	 *
	 * @param value value
	 */
	public SourceDialog(final String value)
	{
		super(value, values, labels, Messages.getString("SourceDialog.title"), Messages.getString("SourceDialog.prompt"));
	}
}
