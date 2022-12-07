/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.propertyview;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Property view
 *
 * @author Bernard Bou
 */
public class PropertyView extends JPanel implements SelectListener
{
	private static final long serialVersionUID = 1L;

	/**
	 * String for default value
	 */
	static final String defaultString = Messages.getString("PropertyView.default");

	// I N T E R F A C E S

	/**
	 * Attribute type
	 */
	public enum AttributeType
	{
		// @formatter:off
		/** None */ NONE, /** Boolean */ BOOLEAN, /** Integer */ INTEGER, /** Float */ FLOAT, /** Floats */ FLOATS,
		/** Label */ LABEL, /** Text */ TEXT, /** Long text */ LONGTEXT,
		/** Color */ COLOR,
		/** Id */ ID, /** Id reference */ REFID,
		/** Image */ IMAGE, /** Link */ LINK,
		/** Font face */ FONTFACE, /** Font size */ FONTSIZE,
		/** Stroke */ STROKE, /** Terminator */ TERMINATOR
		// @formatter:on
	}

	/**
	 * Set attribute value interface
	 */
	public interface Setter
	{
		/**
		 * Set object attribute
		 *
		 * @param object         object
		 * @param attributeName  attribute name
		 * @param attributeValue attribute value
		 */
		void set(Object object, String attributeName, Object attributeValue);
	}

	/**
	 * Get attribute value interface
	 */
	public interface Getter
	{
		/**
		 * Get object attribute's value
		 *
		 * @param object        object
		 * @param attributeName attribute name
		 * @return attribute value
		 */
		@Nullable
		Object get(Object object, String attributeName);
	}

	/**
	 * Get object referenced by id interface
	 */
	public interface IdGetter
	{
		/**
		 * Get object referenced by id interface
		 *
		 * @param id id
		 * @return object referenced by id
		 */
		Object get(String id);

		/**
		 * Get set of ids
		 *
		 * @return set of ids
		 */
		Set<String> ids();
	}

	/**
	 * Attribute descriptor
	 *
	 * @author Bernard Bou
	 */
	public static class AttributeDescriptor
	{
		/**
		 * Name
		 */
		public String name;

		/**
		 * Type
		 */
		public AttributeType type;

		/**
		 * Whether attribute value can be changed
		 */
		public boolean isReadOnly;

		/**
		 * Whether the attribute is mandatory in object description
		 */
		public boolean isMandatory;

		/**
		 * Rank value used in sorting
		 */
		public int rank;

		/**
		 * Possible values
		 */
		public String[] possibleValues;


		@Override
		public String toString()
		{
			return String.format("%s (%s)", this.name, this.type);
		}
	}

	/**
	 * Attribute (descriptor and value)
	 *
	 * @author Bernard Bou
	 */
	public static class Attribute
	{
		/**
		 * Descriptor
		 */
		public final AttributeDescriptor descriptor;

		/**
		 * Value
		 */
		@Nullable
		public Object value;

		/**
		 * Constructor
		 *
		 * @param descriptor descriptor
		 */
		public Attribute(final AttributeDescriptor descriptor)
		{
			this.descriptor = descriptor;
		}

		@Override
		public String toString()
		{
			return String.format("%s=%s", this.descriptor, this.value);
		}
	}

	/**
	 * Interface giving access to an object's attributes
	 *
	 * @author Bernard Bou
	 */
	public static class Handler
	{
		/**
		 * Set of attributes descriptors
		 */
		public Set<AttributeDescriptor> attributeDescriptors;

		/**
		 * Set routines
		 */
		public Setter setter;

		/**
		 * Get routines
		 */
		public Getter getter;

		/**
		 * Access to id map
		 */
		public IdGetter idGetter;
	}

	/**
	 * Get handler for this object
	 *
	 * @author Bernard Bou
	 */
	public interface HandlerFactory
	{
		/**
		 * Get handler for this object
		 *
		 * @param object object
		 * @return handler for this object
		 */
		Handler create(Object object);
	}

	// D A T A

	/**
	 * Change notifier
	 */
	public final ChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * Handler factory
	 */
	@Nullable
	protected HandlerFactory handlerFactory;

	/**
	 * Image repository URL
	 */
	protected URL imageRepository;

	// components

	/**
	 * Table
	 */
	protected final JTable attributeTable = new JTable();

	// renderer and editor

	/**
	 * Table renderer
	 */
	private final Renderer attributeCellRenderer = new Renderer(this);

	/**
	 * Table editor
	 */
	private final Editor attributeCellEditor = new Editor(this);

	// table columns

	static final int STATUS = 0;

	static final int TYPE = 1;

	static final int NAME = 2;

	static final int VALUE = 3;

	private static final int COLUMNNUMBER = PropertyView.VALUE + 1;

	// strings and icons

	/**
	 * Stoke strings
	 */
	static public final String[] strokeStrings = {"solid", "dash", "dot"};

	/**
	 * String to stroke icons
	 */
	static public final Map<String, ImageIcon> strokeIcons = new HashMap<>();

	/**
	 * Terminator strings
	 */
	static public final String[] terminatorStrings = {"z", "a", "t", "h", "tf", "c", "cf", "d", "df"};

	/**
	 * String to terminator icon map
	 */
	static public final Map<String, ImageIcon> terminatorIcons = new HashMap<>();

	/*
	 * Initialize maps
	 */
	static
	{
		//noinspection ConstantConditions
		PropertyView.terminatorIcons.put("z", new ImageIcon(PropertyView.class.getResource("images/terminatorz.png")));
		//noinspection ConstantConditions
		PropertyView.terminatorIcons.put("a", new ImageIcon(PropertyView.class.getResource("images/terminatora.png")));
		//noinspection ConstantConditions
		PropertyView.terminatorIcons.put("h", new ImageIcon(PropertyView.class.getResource("images/terminatorh.png")));
		//noinspection ConstantConditions
		PropertyView.terminatorIcons.put("t", new ImageIcon(PropertyView.class.getResource("images/terminatort.png")));
		//noinspection ConstantConditions
		PropertyView.terminatorIcons.put("tf", new ImageIcon(PropertyView.class.getResource("images/terminatortf.png")));
		//noinspection ConstantConditions
		PropertyView.terminatorIcons.put("c", new ImageIcon(PropertyView.class.getResource("images/terminatorc.png")));
		//noinspection ConstantConditions
		PropertyView.terminatorIcons.put("cf", new ImageIcon(PropertyView.class.getResource("images/terminatorcf.png")));
		//noinspection ConstantConditions
		PropertyView.terminatorIcons.put("d", new ImageIcon(PropertyView.class.getResource("images/terminatord.png")));
		//noinspection ConstantConditions
		PropertyView.terminatorIcons.put("df", new ImageIcon(PropertyView.class.getResource("images/terminatordf.png")));

		//noinspection ConstantConditions
		PropertyView.strokeIcons.put("solid", new ImageIcon(PropertyView.class.getResource("images/solid.png")));
		//noinspection ConstantConditions
		PropertyView.strokeIcons.put("dash", new ImageIcon(PropertyView.class.getResource("images/dash.png")));
		//noinspection ConstantConditions
		PropertyView.strokeIcons.put("dot", new ImageIcon(PropertyView.class.getResource("images/dot.png")));
	}

	// C O N S T R U C T

	/**
	 * Constructor
	 */
	public PropertyView()
	{
		this.handlerFactory = null;

		initialize();

		// attributes
		this.attributeTable.setRowHeight(24);

		// model
		this.attributeTable.setModel(new TableModel());

		// renderer and editor
		setupColumns();
		setRenderer();
		setEditor(null);
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		this.attributeTable.setToolTipText(null);
		@NonNull final JLabel attributesLabel = new JLabel(Messages.getString("PropertyView.attributes"));
		setLayout(new BorderLayout());
		this.add(attributesLabel, BorderLayout.NORTH);
		this.add(new JScrollPane(this.attributeTable), BorderLayout.CENTER);
	}

	/**
	 * Set up columns
	 */
	private void setupColumns()
	{
		final TableColumn statusColumn = this.attributeTable.getColumnModel().getColumn(PropertyView.STATUS);
		statusColumn.setMaxWidth(18);

		final TableColumn typeColumn = this.attributeTable.getColumnModel().getColumn(PropertyView.TYPE);
		typeColumn.setMaxWidth(18);

		final TableColumn nameColumn = this.attributeTable.getColumnModel().getColumn(PropertyView.NAME);
		nameColumn.setMaxWidth(250);
		nameColumn.setMinWidth(150);
	}

	/**
	 * Set handler factory
	 *
	 * @param handlerFactory handler factory
	 */
	public void setHandlerFactory(@Nullable final HandlerFactory handlerFactory)
	{
		this.handlerFactory = handlerFactory;
	}

	/**
	 * Set renderer
	 */
	private void setRenderer()
	{
		final TableColumn statusColumn = this.attributeTable.getColumnModel().getColumn(PropertyView.STATUS);
		statusColumn.setCellRenderer(this.attributeCellRenderer);

		final TableColumn typeColumn = this.attributeTable.getColumnModel().getColumn(PropertyView.TYPE);
		typeColumn.setCellRenderer(this.attributeCellRenderer);

		final TableColumn nameColumn = this.attributeTable.getColumnModel().getColumn(PropertyView.NAME);
		nameColumn.setCellRenderer(this.attributeCellRenderer);

		final TableColumn valueColumn = this.attributeTable.getColumnModel().getColumn(PropertyView.VALUE);
		valueColumn.setCellRenderer(this.attributeCellRenderer);
	}

	/**
	 * Set editor
	 *
	 * @param handler handler
	 */
	private void setEditor(final Handler handler)
	{
		this.attributeCellEditor.setHandler(handler);

		final TableColumn valueColumn = this.attributeTable.getColumnModel().getColumn(PropertyView.VALUE);
		valueColumn.setCellEditor(this.attributeCellEditor);
	}

	/**
	 * Set editor listener
	 *
	 * @param listener listener
	 */
	public void setCellEditorListener(final CellEditorListener listener)
	{
		this.attributeCellEditor.addCellEditorListener(listener);
	}

	/**
	 * Set image repository url
	 *
	 * @param imageRepository image repository url
	 */
	public void setImageRepository(final URL imageRepository)
	{
		this.imageRepository = imageRepository;
	}

	/**
	 * Get image repository
	 *
	 * @return image repository url
	 */
	public URL getImageRepository()
	{
		return this.imageRepository;
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.propertyview.SelectListener#onSelected(java.lang.Object)
	 */
	@Override
	public void onSelected(final Object object)
	{
		// System.err.println("PROPERTYVIEW: selected " + object);

		// model
		assert this.handlerFactory != null;
		final Handler handler = this.handlerFactory.create(object);
		@NonNull final javax.swing.table.TableModel model = new TableModel(object, handler);
		this.attributeTable.setModel(model);

		// renderer and editor
		setupColumns();
		setRenderer();
		setEditor(handler);
	}

	// H E L P E R S

	/**
	 * Decode encoded URL (for display)
	 *
	 * @param string encode URL string
	 * @return decoded URL string
	 */
	static String decode(@NonNull final String string)
	{
		try
		{
			return URLDecoder.decode(string, "UTF8");
		}
		catch (final UnsupportedEncodingException e)
		{
			System.err.println("Can't decode " + string + " - " + e);
		}
		return string;
	}

	/**
	 * Encode encoded URL
	 *
	 * @param string encode URL string
	 * @return decoded URL string
	 */
	static String encode(@NonNull final String string)
	{
		try
		{
			return URLEncoder.encode(string, "UTF8");
		}
		catch (final UnsupportedEncodingException e)
		{
			System.err.println("Cant decode " + string + " - " + e);
		}
		return string;
	}

	// A T T R I B U T E M O D E L

	/**
	 * Table model
	 *
	 * @author Bernard Bou
	 */
	private class TableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 1L;

		/**
		 * The selected object
		 */
		@Nullable
		private final Object selectedObject;

		/**
		 * Its handler
		 */
		@Nullable
		public final Handler handler;

		/**
		 * Its attributes
		 */
		@Nullable
		private Vector<Attribute> attributes;

		/**
		 * Constructor
		 */
		public TableModel()
		{
			this.selectedObject = null;
			this.handler = null;
		}

		/**
		 * Constructor
		 *
		 * @param object  selected object
		 * @param handler its handler
		 */
		public TableModel(@Nullable final Object object, @Nullable final Handler handler)
		{
			this.selectedObject = object;
			this.handler = handler;
			get();
		}

		/**
		 * Get all attribute values
		 */
		public void get()
		{
			if (this.selectedObject == null || this.handler == null)
			{
				this.attributes = null;
			}
			else
			{
				this.attributes = new Vector<>();
				for (final AttributeDescriptor attributeDescriptor : this.handler.attributeDescriptors)
				{
					@NonNull final Attribute attribute = new Attribute(attributeDescriptor);
					attribute.value = this.handler.getter.get(this.selectedObject, attribute.descriptor.name);
					this.attributes.add(attribute);
				}
			}
		}

		/**
		 * Set all attribute values
		 */
		public void set()
		{
			if (this.selectedObject != null)
			{
				assert this.attributes != null;
				for (@NonNull final Attribute attribute : this.attributes)
				{
					assert this.handler != null;
					this.handler.setter.set(this.selectedObject, attribute.descriptor.name, attribute.value);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount()
		{
			if (this.attributes == null)
			{
				return 0;
			}
			return this.attributes.size();
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount()
		{
			return PropertyView.COLUMNNUMBER;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(final int y, final int x)
		{
			if (x == PropertyView.VALUE)
			{
				assert this.attributes != null;
				final Attribute row = this.attributes.elementAt(y);
				return !row.descriptor.isReadOnly;
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Nullable
		@Override
		public Object getValueAt(final int y, final int x)
		{
			assert this.attributes != null;
			final Attribute attribute = this.attributes.elementAt(y);
			switch (x)
			{
				case STATUS:
					return attribute.descriptor.isMandatory;
				case TYPE:
					return attribute.descriptor.type;
				case NAME:
					return attribute.descriptor.name;
				case VALUE:
					return attribute;
				default:
					return null;
			}
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		@Override
		public void setValueAt(final Object value, final int y, final int x)
		{
			// System.out.println(value);
			if (x == PropertyView.VALUE)
			{
				final Attribute attribute0 = (Attribute) value;
				assert this.attributes != null;
				final Attribute attribute = this.attributes.elementAt(y);
				attribute.value = attribute0.value;
				set();
				PropertyView.this.changeNotifier.fireStateChanged(new ChangeEvent(attribute));
			}
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@NonNull
		@Override
		public String getColumnName(final int x)
		{
			switch (x)
			{
				case STATUS:
					return "x";
				case TYPE:
					return "t";
				case NAME:
					return Messages.getString("PropertyView.name");
				case VALUE:
					return Messages.getString("PropertyView.value");
				default:
					return "";
			}
		}
	}
}
