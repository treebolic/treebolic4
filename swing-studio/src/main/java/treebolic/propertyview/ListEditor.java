/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.propertyview;

import java.awt.*;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * List editor
 *
 * @author Bernard Bou
 */
class ListEditor extends DefaultCellEditor
{
	/**
	 * String to image map
	 */
	@Nullable
	private Map<String, ImageIcon> imageMap = null;

	/**
	 * Constructor
	 */
	public ListEditor()
	{
		super(new JComboBox<String>());

		// renderer
		@NonNull final DefaultListCellRenderer renderer = new DefaultListCellRenderer()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public Component getListCellRendererComponent(@NonNull final JList<?> list, @Nullable final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
			{
				if (ListEditor.this.imageMap == null)
				{
					return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				}

				final ImageIcon icon = ListEditor.this.imageMap.get((String) value);
				setText(value == null ? PropertyView.defaultString : (String) value);
				setIcon(icon);
				setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
				setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
				return this;
			}
		};
		renderer.setHorizontalAlignment(SwingConstants.LEFT);
		renderer.setVerticalAlignment(SwingConstants.CENTER);
		renderer.setOpaque(true);
		getComboBox().setRenderer(renderer);

		// if popup closes fire event (standard code depends on if value has changed)
		getComboBox().addPopupMenuListener(new PopupMenuListener()
		{
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
			{
				//
			}

			@SuppressWarnings("synthetic-access")
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
			{
				ListEditor.this.fireEditingStopped();
			}

			@SuppressWarnings("synthetic-access")
			@Override
			public void popupMenuCanceled(PopupMenuEvent e)
			{
				ListEditor.this.fireEditingCanceled();
			}
		});
	}

	/**
	 * Get access to combobox component
	 *
	 * @return combo box component
	 */
	@SuppressWarnings("unchecked")
	public JComboBox<String> getComboBox()
	{
		return (JComboBox<String>) this.editorComponent;
	}

	/**
	 * Allow for edit line
	 *
	 * @param flag true/false
	 */
	public void setEditable(final boolean flag)
	{
		getComboBox().setEditable(flag);
	}

	/**
	 * Render strings as images (as per map)
	 *
	 * @param imageMap string to image map
	 */
	public void setImageMap(@Nullable final Map<String, ImageIcon> imageMap)
	{
		this.imageMap = imageMap;
	}

	// I N T E R F A C E

	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#getCellEditorValue()
	 */
	@Nullable
	@Override
	public Object getCellEditorValue()
	{
		@Nullable String value = (String) super.getCellEditorValue();
		if (PropertyView.defaultString.equals(value))
		{
			value = null;
		}
		// System.out.println("List getCellEditorValue " + value);
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value0, boolean isSelected, int row, int column)
	{
		Object value = value0;
		if (value == null)
		{
			value = PropertyView.defaultString;
		}
		// System.out.println("List getTableCellEditorComponent " + value);
		return super.getTableCellEditorComponent(table, value.toString(), isSelected, row, column);
	}
}
