/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.fungi.browser;

import treebolic.annotations.NonNull;
import treebolic.commons.RadioChoiceDialog;

/**
 * Import dialog
 *
 * @author Bernard Bou
 */
public class SourceDialog extends RadioChoiceDialog
{
	/**
	 * Constructor
	 *
	 * @param value  value
	 * @param values values
	 * @param labels labels
	 */
	public SourceDialog(@NonNull final String value, @NonNull final String[] values, @NonNull final String[] labels)
	{
		super(value, values, labels, Messages.getString("SourceDialog.title"), Messages.getString("SourceDialog.prompt"));
	}
}
