/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.commons;

import java.io.File;
import java.io.IOException;

import treebolic.annotations.NonNull;

/**
 * Import dialog
 *
 * @author Bernard Bou
 */
public class ZipEntryDialog extends ChoiceDialog
{
	/**
	 * Constructor
	 *
	 * @param archive zip archive
	 * @throws IOException io exception
	 */
	public ZipEntryDialog(@NonNull final File archive) throws IOException
	{
		super(null, Searcher.findZipEntries(archive, null, ".*\\.(png|jpg|gif|MF)|META-INF/"), Messages.getString("ZipEntryDialog.zip"), Messages.getString("ZipEntryDialog.entry"), false);
	}
}
