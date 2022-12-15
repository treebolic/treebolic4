/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.propertyview;

import java.awt.*;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellEditor;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * File editor
 *
 * @author Bernard Bou
 */
class FileEditor extends AbstractCellEditor implements TableCellEditor
{
	/**
	 * File chooser
	 */
	@NonNull
	private final JFileChooser fileChooser;

	/**
	 * Button component
	 */
	@NonNull
	private final JButton button;

	/**
	 * Current directory
	 */
	private File currentDirectory;

	/**
	 * Constructor
	 */
	public FileEditor()
	{
		this.button = new JButton();
		this.button.setBorderPainted(false);
		this.button.setHorizontalAlignment(SwingConstants.LEFT);
		this.button.addActionListener(e -> {
			FileEditor.this.fileChooser.setCurrentDirectory(FileEditor.this.currentDirectory);
			FileEditor.this.fileChooser.showOpenDialog(null);

			// dialog returns control
			fireEditingStopped();
		});

		// file chooser
		@NonNull final JButton defaultFileButton = new JButton(Messages.getString("FileEditor.default"));
		defaultFileButton.addActionListener(e -> {
			FileEditor.this.fileChooser.setSelectedFile(null);
			Container container = FileEditor.this.fileChooser.getParent();
			while (container.getParent() != null)
			{
				container = container.getParent();
			}
			container.setVisible(false);
		});
		this.fileChooser = new JFileChooser();
		this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.fileChooser.setFileFilter(new FileFilter()
		{
			@Override
			public boolean accept(@NonNull final File file)
			{
				return file.getName().toLowerCase().endsWith(".gif") || file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png") || file.isDirectory();
			}

			@NonNull
			@Override
			public String getDescription()
			{
				return Messages.getString("FileEditor.image");
			}
		});
		this.fileChooser.addActionListener(e -> {
			if (e.getActionCommand().equalsIgnoreCase(JFileChooser.APPROVE_SELECTION))
			{
				fireEditingStopped();
			}
			else
			{
				fireEditingCanceled();
			}
		});
		this.fileChooser.setAccessory(defaultFileButton);
	}

	/**
	 * Set current directory
	 *
	 * @param currentDirectory current directory
	 */
	public void setDirectory(final File currentDirectory)
	{
		this.currentDirectory = currentDirectory;
	}

	// I N T E R F A C E

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@NonNull
	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column)
	{
		this.button.setText((String) value);
		return this.button;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Nullable
	@Override
	public Object getCellEditorValue()
	{
		final File selection = this.fileChooser.getSelectedFile();
		return selection == null ? null : selection.getName();
	}
}
