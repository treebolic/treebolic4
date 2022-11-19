/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.io.File;
import java.io.IOException;

/**
 * Import dialog
 *
 * @author Bernard Bou
 */
public class ZipEntryDialog extends ChoiceDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 *
	 * @param archive
	 *        zip archive
	 * @throws IOException
	 */
	public ZipEntryDialog(final File archive) throws IOException
	{
		super(null, Searcher.findZipEntries(archive, null, ".*\\.(png|jpg|gif|MF)|META-INF/"), Messages.getString("ZipEntryDialog.zip"), Messages.getString("ZipEntryDialog.entry"), false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
