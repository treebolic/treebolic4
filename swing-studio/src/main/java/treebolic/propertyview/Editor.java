/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.propertyview;

import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.EventObject;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.model.Utils;
import treebolic.propertyview.PropertyView.Attribute;
import treebolic.propertyview.PropertyView.Handler;

/**
 * Versatile super editor
 *
 * @author Bernard Bou
 */
public class Editor implements TableCellEditor, CellEditorListener
{
	/**
	 * View
	 */
	private final PropertyView propertyView;

	/**
	 * Handler used to retrieve id map
	 */
	private Handler handler;

	/**
	 * Attribute being edited
	 */
	@Nullable
	private Attribute attribute;

	/**
	 * Color editor
	 */
	@NonNull
	private final ColorEditor colorEditor;

	/**
	 * File editor
	 */
	@NonNull
	private final FileEditor fileEditor;

	/**
	 * List editor
	 */
	@NonNull
	private final ListEditor listEditor;

	/**
	 * Long text editor
	 */
	@NonNull
	private final LongTextEditor longTextEditor;

	/**
	 * Default editor
	 */
	@NonNull
	private final DefaultCellEditor defaultEditor;

	/**
	 * The delegate subeditor
	 */
	private TableCellEditor subEditor;

	/**
	 * The subeditors
	 */
	@NonNull
	private final TableCellEditor[] subEditors;

	static class TextEditor extends DefaultCellEditor
	{
		public TextEditor()
		{
			super(new JTextField());

			final JTextField textField = (JTextField) this.editorComponent;

			// undo super constructor
			textField.removeActionListener(this.delegate);

			// new delegate
			this.delegate = new EditorDelegate()
			{
				@Override
				public void setValue(@Nullable Object value)
				{
					textField.setText((value != null) ? value.toString().replaceAll("\n", "\\\\n") : "");
				}

				@NonNull
				@Override
				public Object getCellEditorValue()
				{
					return textField.getText().replaceAll("\\\\n", "\n");
				}
			};
			textField.addActionListener(this.delegate);
		}
	}

	/**
	 * Constructor
	 *
	 * @param propertyView view
	 */
	public Editor(final PropertyView propertyView)
	{
		this.propertyView = propertyView;

		// subeditors
		this.colorEditor = new ColorEditor();
		this.fileEditor = new FileEditor();
		this.listEditor = new ListEditor();
		this.longTextEditor = new LongTextEditor();
		this.defaultEditor = new TextEditor();
		this.subEditors = new TableCellEditor[]{this.colorEditor, this.fileEditor, this.listEditor, this.longTextEditor, this.defaultEditor};

		// edited attribute
		this.attribute = null;
	}

	/**
	 * Set get/set handlers
	 *
	 * @param handler handler
	 */
	public void setHandler(final Handler handler)
	{
		this.handler = handler;
	}

	// I N T E R F A C E

	@Nullable
	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column)
	{
		if (column == PropertyView.VALUE)
		{
			this.attribute = (Attribute) value;

			// subeditor
			switch (this.attribute.descriptor.type)
			{
				// color
				case COLOR:
					this.subEditor = this.colorEditor;
					break;

				// lists
				case STROKE:
					this.listEditor.setEditable(false);
					this.listEditor.setImageMap(PropertyView.strokeIcons);
					populateWithStrokes(this.listEditor.getComboBox());
					this.subEditor = this.listEditor;
					break;

				case TERMINATOR:
					this.listEditor.setImageMap(PropertyView.terminatorIcons);
					this.listEditor.setEditable(false);
					populateWithTerminators(this.listEditor.getComboBox());
					this.subEditor = this.listEditor;
					break;

				case FONTFACE:
					this.listEditor.setImageMap(null);
					this.listEditor.setEditable(false);
					populateWithFonts(this.listEditor.getComboBox());
					this.subEditor = this.listEditor;
					break;

				case BOOLEAN:
					this.listEditor.setImageMap(null);
					this.listEditor.setEditable(false);
					populateWithTrueFalse(this.listEditor.getComboBox());
					this.subEditor = this.listEditor;
					break;

				case REFID:
					this.listEditor.setImageMap(null);
					this.listEditor.setEditable(false);
					populateWithIds(this.listEditor.getComboBox());
					this.subEditor = this.listEditor;
					break;

				case LINK:
					this.listEditor.setImageMap(null);
					this.listEditor.setEditable(true);
					populateWithUrls(this.listEditor.getComboBox());
					addPopulateWithSharpIds(this.listEditor.getComboBox());
					this.subEditor = this.listEditor;
					break;

				// files
				case IMAGE:
					this.fileEditor.setDirectory(getImageRepository());
					this.subEditor = this.fileEditor;
					break;

				// text
				case LONGTEXT:
					this.subEditor = this.longTextEditor;
					break;

				case TEXT:
					if (this.attribute.descriptor.possibleValues != null)
					{
						this.listEditor.setImageMap(null);
						this.listEditor.setEditable(false);
						populateWithStrings(this.listEditor.getComboBox(), this.attribute.descriptor.possibleValues);
						this.subEditor = this.listEditor;
						break;
					}
					// fall through

					// text
				case INTEGER:
				case FLOAT:
				case FLOATS:
				case ID:
				case FONTSIZE:
				default:
					this.subEditor = this.defaultEditor;
					break;
			}

			// invoke subeditor
			return this.subEditor.getTableCellEditorComponent(table, this.attribute.value, isSelected, row, column);
		}
		return null;
	}

	@Nullable
	@Override
	public Object getCellEditorValue()
	{
		if (this.attribute == null)
		{
			return null;
		}
		@Nullable Object value = null;
		switch (this.attribute.descriptor.type)
		{
			// conversion
			case BOOLEAN:
			{
				final String editedValue = (String) this.subEditor.getCellEditorValue();
				if (editedValue != null && !PropertyView.defaultString.equals(editedValue) && !editedValue.isEmpty())
				{
					value = Boolean.valueOf(editedValue);
				}
				break;
			}

			case INTEGER:
			case FONTSIZE:
			{
				final String editedValue = (String) this.subEditor.getCellEditorValue();
				if (editedValue != null && !PropertyView.defaultString.equals(editedValue) && !editedValue.isEmpty())
				{
					try
					{
						value = Integer.valueOf(editedValue);
					}
					catch (final NumberFormatException e)
					{
						// do nothing
					}
				}
				break;
			}

			case FLOAT:
			{
				final String editedValue = (String) this.subEditor.getCellEditorValue();
				if (editedValue != null && !PropertyView.defaultString.equals(editedValue) && !editedValue.isEmpty())
				{
					try
					{
						value = Float.valueOf(editedValue);
					}
					catch (final NumberFormatException e)
					{
						// do nothing
					}
				}
				break;
			}

			case FLOATS:
			{
				final String editedValue = (String) this.subEditor.getCellEditorValue();
				if (editedValue != null && !PropertyView.defaultString.equals(editedValue) && !editedValue.isEmpty())
				{
					@Nullable final float[] floats = Utils.stringToFloats(editedValue);
					if (floats != null)
					{
						value = new Floats(floats);
					}
				}
				break;
			}

			// raw value
			case COLOR:
			{
				value = this.subEditor.getCellEditorValue();
				break;
			}

			// string value
			default:
			{
				final String editedValue = (String) this.subEditor.getCellEditorValue();
				if (editedValue != null && !PropertyView.defaultString.equals(editedValue) && !editedValue.isEmpty())
				{
					value = editedValue;
				}
				break;
			}
		}
		this.attribute.value = value;
		return this.attribute;
	}

	@Override
	public boolean shouldSelectCell(final EventObject event)
	{
		return this.subEditor.shouldSelectCell(event);
	}

	@Override
	public boolean stopCellEditing()
	{
		return this.subEditor.stopCellEditing();
	}

	@Override
	public void cancelCellEditing()
	{
		this.subEditor.cancelCellEditing();
	}

	@Override
	public boolean isCellEditable(final EventObject anEvent)
	{
		return true;
	}

	// H E L P E R S

	/**
	 * Get image repository
	 *
	 * @return image repository as file
	 */
	@Nullable
	private File getImageRepository()
	{
		try
		{
			if (this.propertyView.imageRepository != null)
			{
				return new File(this.propertyView.imageRepository.toURI());
			}
		}
		catch (final URISyntaxException e)
		{
			// do nothing
		}
		return null;
	}

	// P O P U L A T E . C O M B O B O X

	/**
	 * Populate combobox with ids
	 *
	 * @param combo combobox
	 */
	private void populateWithIds(@NonNull final JComboBox<String> combo)
	{
		final Set<String> ids = this.handler.idGetter.ids();
		combo.removeAllItems();
		combo.addItem(PropertyView.defaultString);
		for (final String string : ids)
		{
			combo.addItem(string);
		}
	}

	/**
	 * Populate combobox with stroke types
	 *
	 * @param combo combobox
	 */
	private void populateWithStrokes(@NonNull final JComboBox<String> combo)
	{
		populateWithStrings(combo, PropertyView.strokeStrings);
	}

	/**
	 * Populate combobox with true/false
	 *
	 * @param combo combobox
	 */
	private void populateWithTrueFalse(@NonNull final JComboBox<String> combo)
	{
		populateWithStrings(combo, new String[]{"false", "true"});
	}

	/**
	 * Populate combobox with urls
	 *
	 * @param combo combobox
	 */
	private void populateWithUrls(@NonNull final JComboBox<String> combo)
	{
		populateWithStrings(combo, new String[]{"http://", "ftp://"});
	}

	/**
	 * Populate combobox with ids referencing nodes
	 *
	 * @param combo combobox
	 */
	private void addPopulateWithSharpIds(@NonNull final JComboBox<String> combo)
	{
		final Set<String> ids = this.handler.idGetter.ids();
		for (final String string : ids)
		{
			combo.addItem("#" + string);
		}
	}

	/**
	 * Populate combobox with fonts
	 *
	 * @param combo combobox
	 */
	private void populateWithFonts(@NonNull final JComboBox<String> combo)
	{
		populateWithStrings(combo, new String[]{"SansSerif", "Serif", "MonoSpaced", "Dialog", "DialogInput"});
		final Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for (@NonNull final Font font : fonts)
		{
			combo.addItem(font.getFontName());
		}
	}

	/**
	 * Populate combobox with terminators
	 *
	 * @param combo combobox
	 */
	private void populateWithTerminators(@NonNull final JComboBox<String> combo)
	{
		populateWithStrings(combo, PropertyView.terminatorStrings);
	}

	/**
	 * Populate combobox with strings list
	 *
	 * @param combo   combobox
	 * @param strings list of strings
	 */
	private void populateWithStrings(@NonNull final JComboBox<String> combo, @NonNull final String[] strings)
	{
		combo.removeAllItems();
		combo.setMaximumRowCount(strings.length + 1);
		combo.removeAllItems();
		combo.addItem(PropertyView.defaultString);
		for (final String string : strings)
		{
			combo.addItem(string);
		}
	}

	// N O T I F I C A T I O N

	@Override
	public void editingCanceled(final ChangeEvent e)
	{
		// System.out.println("Editor notification: Canceled"); 
	}

	@Override
	public void editingStopped(final ChangeEvent e)
	{
		// System.out.println("Editor notification: Stopped"); 
	}

	// L I S T E N E R S

	@Override
	public void addCellEditorListener(final CellEditorListener listener)
	{
		for (@NonNull final CellEditor subEditor : this.subEditors)
		{
			subEditor.addCellEditorListener(listener);
		}
	}

	@Override
	public void removeCellEditorListener(final CellEditorListener listener)
	{
		for (@NonNull final CellEditor subEditor : this.subEditors)
		{
			subEditor.removeCellEditorListener(listener);
		}
	}
}