/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.propertyview;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

/**
 * File editor
 *
 * @author Bernard Bou
 */
class LongTextEditor extends AbstractCellEditor implements TableCellEditor
{
	private static final long serialVersionUID = 1L;

	/**
	 * Text
	 */
	private String text;

	/**
	 * Dialog
	 */
	private final TextDialog textDialog;

	/**
	 * Button component
	 */
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

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column)
	{
		this.text = (String) value;
		this.button.setText(this.text);
		return this.button;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue()
	{
		final String text = this.textDialog.getText();
		return text == null || text.isEmpty() ? null : text;
	}
}
