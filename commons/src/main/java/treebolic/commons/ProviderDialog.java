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
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 *
	 * @param provider
	 *        provider
	 */
	public ProviderDialog(final String provider)
	{
		super(provider, Searcher.findClasses(".*\\.Provider"), Messages.getString("ProviderDialog.provider"), Messages.getString("ProviderDialog.class"), true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
