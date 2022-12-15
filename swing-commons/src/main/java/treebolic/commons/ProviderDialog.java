/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

/**
 * Import dialog
 *
 * @author Bernard Bou
 */
public class ProviderDialog extends ChoiceDialog
{
	/**
	 * Constructor
	 *
	 * @param provider provider
	 */
	public ProviderDialog(final String provider)
	{
		super(provider, Searcher.findClasses(".*\\.Provider"), Messages.getString("ProviderDialog.provider"), Messages.getString("ProviderDialog.class"), true);
	}
}
