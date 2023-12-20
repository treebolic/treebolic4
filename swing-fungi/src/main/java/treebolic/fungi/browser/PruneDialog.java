/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.fungi.browser;

import treebolic.commons.RadioChoiceDialog;

/**
 * Import dialog
 *
 * @author Bernard Bou
 */
public class PruneDialog extends RadioChoiceDialog
{
	private static final String[] labels = new String[]{ //
			Messages.getString("PruneDialog.cofgs"), Messages.getString("PruneDialog.ofgs"), Messages.getString("PruneDialog.fgs"), Messages.getString("PruneDialog.gs"), Messages.getString("PruneDialog.s"), Messages.getString("PruneDialog.none"),};

	private static final String[] values = new String[]{ //
			"substr(id,1,1) NOT IN ('c','o','f','g','e')", "substr(id,1,1) NOT IN ('o','f','g','e')", "substr(id,1,1) NOT IN ('f','g','e')", "substr(id,1,1) NOT IN ('g','e')", MainFrame.DEFAULTPRUNE, //
			"",};

	/**
	 * Constructor
	 *
	 * @param value
	 *        value
	 */
	public PruneDialog(final String value)
	{
		super(value, values, labels, Messages.getString("PruneDialog.title"), Messages.getString("PruneDialog.prompt"));
	}
}
