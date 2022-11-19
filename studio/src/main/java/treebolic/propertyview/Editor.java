/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.propertyview;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.URISyntaxException;
import java.util.EventObject;
import java.util.Set;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

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
	private Attribute attribute;

	/**
	 * Color editor
	 */
	private final ColorEditor colorEditor;

	/**
	 * File editor
	 */
	private final FileEditor fileEditor;

	/**
	 * List editor
	 */
	private final ListEditor listEditor;

	/**
	 * Long text editor
	 */
	private final LongTextEditor longTextEditor;

	/**
	 * Default editor
	 */
	private final DefaultCellEditor defaultEditor;

	/**
	 * The delegate subeditor
	 */
	private TableCellEditor subEditor;

	/**
	 * The subeditors
	 */
	private final TableCellEditor[] subEditors;

	static class TextEditor extends DefaultCellEditor
	{
		private static final long serialVersionUID = 1042643320776522695L;

		public TextEditor()
	    {
	    	super(new JTextField());

	    	final JTextField textField = (JTextField) this.editorComponent;

	    	// undo super constructor
	    	textField.removeActionListener(this.delegate);

	    	// new delegate
	    	this.delegate = new EditorDelegate()
	    	{
				private static final long serialVersionUID = 8570059590275656633L;

				@Override
				public void setValue(Object value)
	            {
	                textField.setText((value != null) ? value.toString().replaceAll("\n", "\\\\n") : "");
	            }

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
	 * @param propertyView
	 *        view
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
		this.subEditors = new TableCellEditor[] { this.colorEditor, this.fileEditor, this.listEditor, this.longTextEditor, this.defaultEditor };

		// edited attribute
		this.attribute = null;
	}

	/**
	 * Set get/set handlers
	 *
	 * @param handler
	 *        handler
	 */
	public void setHandler(final Handler handler)
	{
		this.handler = handler;
	}

	// I N T E R F A C E

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue()
	{
		Object value = null;
		switch (this.attribute.descriptor.type)
		{
		// conversion
		case BOOLEAN:
		{
			final String editedValue = (String) this.subEditor.getCellEditorValue();
			if (editedValue != null && !PropertyView.defaultString.equals(editedValue) && !editedValue.isEmpty())
				value = Boolean.valueOf(editedValue);
			break;
		}

		case INTEGER:
		case FONTSIZE:
		{
			final String editedValue = (String) this.subEditor.getCellEditorValue();
			if (editedValue != null && !PropertyView.defaultString.equals(editedValue) && !editedValue.isEmpty())
				try
				{
					value = Integer.valueOf(editedValue);
				}
				catch (final NumberFormatException e)
				{
					// do nothing
				}
			break;
		}

		case FLOAT:
		{
			final String editedValue = (String) this.subEditor.getCellEditorValue();
			if (editedValue != null && !PropertyView.defaultString.equals(editedValue) && !editedValue.isEmpty())
				try
				{
					value = Float.valueOf(editedValue);
				}
				catch (final NumberFormatException e)
				{
					// do nothing
				}
			break;
		}

		case FLOATS:
		{
			final String editedValue = (String) this.subEditor.getCellEditorValue();
			if (editedValue != null && !PropertyView.defaultString.equals(editedValue) && !editedValue.isEmpty())
			{
				final float[] floats = Utils.stringToFloats(editedValue);
				if (floats != null)
					value = new Floats(floats);
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
				value = editedValue;
			break;
		}
		}
		this.attribute.value = value;
		return this.attribute;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
	 */
	@Override
	public boolean shouldSelectCell(final EventObject event)
	{
		return this.subEditor.shouldSelectCell(event);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	@Override
	public boolean stopCellEditing()
	{
		return this.subEditor.stopCellEditing();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#cancelCellEditing()
	 */
	@Override
	public void cancelCellEditing()
	{
		this.subEditor.cancelCellEditing();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
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
	private File getImageRepository()
	{
		try
		{
			if (this.propertyView.imageRepository != null)
				return new File(this.propertyView.imageRepository.toURI());
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
	 * @param combo
	 *        combobox
	 */
	private void populateWithIds(final JComboBox<String> combo)
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
	 * @param combo
	 *        combobox
	 */
	private void populateWithStrokes(final JComboBox<String> combo)
	{
		populateWithStrings(combo, PropertyView.strokeStrings);
	}

	/**
	 * Populate combobox with true/false
	 *
	 * @param combo
	 *        combobox
	 */
	private void populateWithTrueFalse(final JComboBox<String> combo)
	{
		populateWithStrings(combo, new String[] { "false", "true" }); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Populate combobox with urls
	 *
	 * @param combo
	 *        combobox
	 */
	private void populateWithUrls(final JComboBox<String> combo)
	{
		populateWithStrings(combo, new String[] { "http://", "ftp://" }); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Populate combobox with ids referencing nodes
	 *
	 * @param combo
	 *        combobox
	 */
	private void addPopulateWithSharpIds(final JComboBox<String> combo)
	{
		final Set<String> ids = this.handler.idGetter.ids();
		for (final String string : ids)
		{
			combo.addItem("#" + string); //$NON-NLS-1$
		}
	}

	/**
	 * Populate combobox with fonts
	 *
	 * @param combo
	 *        combobox
	 */
	private void populateWithFonts(final JComboBox<String> combo)
	{
		populateWithStrings(combo, new String[] { "SansSerif", "Serif", "MonoSpaced", "Dialog", "DialogInput" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		final Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for (final Font font : fonts)
		{
			combo.addItem(font.getFontName());
		}
	}

	/**
	 * Populate combobox with terminators
	 *
	 * @param combo
	 *        combobox
	 */
	private void populateWithTerminators(final JComboBox<String> combo)
	{
		populateWithStrings(combo, PropertyView.terminatorStrings);
	}

	/**
	 * Populate combobox with strings list
	 *
	 * @param combo
	 *        combobox
	 * @param strings
	 *        list of strings
	 */
	private void populateWithStrings(final JComboBox<String> combo, final String[] strings)
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

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.CellEditorListener#editingCanceled(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void editingCanceled(final ChangeEvent e)
	{
		// System.out.println("Editor notification: Canceled"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.CellEditorListener#editingStopped(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void editingStopped(final ChangeEvent e)
	{
		// System.out.println("Editor notification: Stopped"); //$NON-NLS-1$
	}

	// L I S T E N E R S

	/*
	 * (non-Javadoc)
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	@Override
	public void addCellEditorListener(final CellEditorListener listener)
	{
		for (final CellEditor subEditor : this.subEditors)
		{
			subEditor.addCellEditorListener(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	@Override
	public void removeCellEditorListener(final CellEditorListener listener)
	{
		for (final CellEditor subEditor : this.subEditors)
		{
			subEditor.removeCellEditorListener(listener);
		}
	}
}