/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.propertyview;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import treebolic.glue.Color;

/**
 * Color editor
 *
 * @author Bernard Bou
 */
class ColorEditor extends AbstractCellEditor implements TableCellEditor
{
	private static final long serialVersionUID = 1L;

	/**
	 * Color being edited
	 */
	private Color currentColor;

	/**
	 * Button (editor component)
	 */
	private final JButton button;

	/**
	 * Color chooser
	 */
	private final JColorChooser colorChooser;

	/**
	 * Wrapping color dialog
	 */
	private final JDialog colorDialog;

	/**
	 * Constructor
	 */
	public ColorEditor()
	{
		this.button = new JButton();
		this.button.setBorderPainted(false);
		this.button.addActionListener(new ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				ColorEditor.this.colorChooser.setColor(ColorEditor.this.currentColor == null || ColorEditor.this.currentColor.isNull() ? null : ColorEditor.this.currentColor.color);
				ColorEditor.this.colorDialog.setVisible(true);

				// dialog returns control
				fireEditingStopped();
			}
		});

		// set up the dialog that the button brings up.
		this.colorChooser = new JColorChooser();
		this.colorDialog = JColorChooser.createDialog(this.button, Messages.getString("ColorEditor.prompt_color"), true, // modal //$NON-NLS-1$
				this.colorChooser,
				// ok button handler
				e -> ColorEditor.this.currentColor = new Color(ColorEditor.this.colorChooser.getColor()),
		// cancel button handler
				e -> ColorEditor.this.currentColor = null);
	}

	// implement the one method defined by TableCellEditor.
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column)
	{
		this.currentColor = (Color) value;
		this.button.setBackground(this.currentColor == null || this.currentColor.isNull() ? null : this.currentColor.color);
		return this.button;
	}

	// implement the one CellEditor method that AbstractCellEditor doesn't.
	/*
	 * (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue()
	{
		return this.currentColor;
	}
}
