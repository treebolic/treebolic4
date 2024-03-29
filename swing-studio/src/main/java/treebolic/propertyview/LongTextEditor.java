/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.propertyview;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.TableCellEditor;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * File editor
 *
 * @author Bernard Bou
 */
class LongTextEditor extends AbstractCellEditor implements TableCellEditor
{
	/**
	 * Text
	 */
	private String text;

	/**
	 * Dialog
	 */
	@NonNull
	private final TextDialog textDialog;

	/**
	 * Button component
	 */
	@NonNull
	private final JButton button;

	/**
	 * Constructor
	 */
	public LongTextEditor()
	{
		this.button = new JButton();
		this.button.setBorderPainted(false);
		this.button.setHorizontalAlignment(SwingConstants.LEFT);
		this.button.addActionListener(new ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				LongTextEditor.this.textDialog.setText(LongTextEditor.this.text);
				LongTextEditor.this.textDialog.setModal(true);
				LongTextEditor.this.textDialog.setVisible(true);

				// dialog returns control
				fireEditingStopped();
			}
		});

		// text
		this.textDialog = new TextDialog( //

				// ok listener
				e -> fireEditingStopped(),

				// cancel
				e -> fireEditingCanceled());
	}

	// I N T E R F A C E

	@NonNull
	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column)
	{
		this.text = (String) value;
		this.button.setText(this.text);
		return this.button;
	}

	@Nullable
	@Override
	public Object getCellEditorValue()
	{
		@Nullable final String text = this.textDialog.getText();
		return text == null || text.isEmpty() ? null : text;
	}
}
