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
import treebolic.glue.ColorKit;

/**
 * Color editor
 *
 * @author Bernard Bou
 */
class ColorEditor extends AbstractCellEditor implements TableCellEditor
{
	/**
	 * Color being edited
	 */
	@Nullable
	private Integer currentColor;

	/**
	 * Button (editor component)
	 */
	@NonNull
	private final JButton button;

	/**
	 * Color chooser
	 */
	@NonNull
	private final JColorChooser colorChooser;

	/**
	 * Wrapping color dialog
	 */
	@NonNull
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
				ColorEditor.this.colorChooser.setColor(ColorKit.toAWT(ColorEditor.this.currentColor));
				ColorEditor.this.colorDialog.setVisible(true);

				// dialog returns control
				fireEditingStopped();
			}
		});

		// set up the dialog that the button brings up.
		this.colorChooser = new JColorChooser();
		this.colorDialog = JColorChooser.createDialog(this.button, Messages.getString("ColorEditor.prompt_color"), true, // modal 
				this.colorChooser,
				// ok button handler
				e -> ColorEditor.this.currentColor = ColorEditor.this.colorChooser.getColor().getRGB(),
				// cancel button handler
				e -> ColorEditor.this.currentColor = null);
	}

	// implement the one method defined by TableCellEditor.
	@NonNull
	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column)
	{
		this.currentColor = (Integer) value;
		this.button.setBackground(ColorKit.toAWT(this.currentColor));
		return this.button;
	}

	// implement the one CellEditor method that AbstractCellEditor doesn't.
	@Nullable
	@Override
	public Object getCellEditorValue()
	{
		return this.currentColor;
	}
}
