/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * URL dialog
 *
 * @author Bernard Bou
 */
public class UrlDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	// V A L U E S

	/**
	 * Properties (input/output)
	 */
	private final Properties properties;

	/**
	 * Ok flag
	 */
	public boolean ok;

	// C O M P O N E N T S

	/**
	 * URL
	 */
	private JTextField urlTextField;

	/**
	 * Parameter table
	 */
	private JTable queryTable;

	/**
	 * Constructor
	 *
	 * @param properties properties
	 */
	public UrlDialog(final Properties properties)
	{
		super();
		this.properties = properties;
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		setTitle(Messages.getString("UrlDialog.title"));
		setResizable(true);

		// label
		@NonNull final JLabel urlLabel = new JLabel(Messages.getString("UrlDialog.url"));
		@NonNull final JLabel queryLabel = new JLabel(Messages.getString("UrlDialog.query"));
		urlLabel.setToolTipText(Messages.getString("UrlDialog.tooltip_url"));
		queryLabel.setToolTipText(Messages.getString("UrlDialog.tooltip_query"));

		// url
		this.urlTextField = new JTextField();
		this.urlTextField.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		this.urlTextField.setToolTipText(Messages.getString("UrlDialog.tooltip_result"));

		// parameter table
		this.queryTable = new JTable();
		this.queryTable.setModel(new ParameterModel(null));
		this.queryTable.getColumnModel().getColumn(0).setMaxWidth(90);
		this.queryTable.setToolTipText(Messages.getString("UrlDialog.tooltip_parameters"));
		final JScrollPane scrollPane = new JScrollPane(this.queryTable);
		scrollPane.setPreferredSize(new Dimension(0, 60));

		// buttons
		@NonNull final JButton okButton = new JButton(Messages.getString("UrlDialog.ok"));
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		okButton.addActionListener(e -> {
			UrlDialog.this.ok = true;
			setVisible(false);
		});
		@NonNull final JButton cancelButton = new JButton(Messages.getString("UrlDialog.cancel"));
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		cancelButton.addActionListener(e -> setVisible(false));
		@NonNull final JButton addParameter = new JButton(Messages.getString("UrlDialog.add"));
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		addParameter.addActionListener(e -> {
			final ParameterModel model = (ParameterModel) UrlDialog.this.queryTable.getModel();
			if (model != null)
			{
				model.newRow(null);
			}
		});
		@NonNull final JButton removeParameter = new JButton(Messages.getString("UrlDialog.remove"));
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		removeParameter.addActionListener(e -> {
			final int rowIdx = UrlDialog.this.queryTable.getSelectedRow();
			final ParameterModel model = (ParameterModel) UrlDialog.this.queryTable.getModel();
			if (rowIdx != -1 && model != null)
			{
				model.deleteRow(rowIdx);
			}
		});

		// button panel
		@NonNull final JPanel buttonPanel = new JPanel();
		buttonPanel.add(addParameter, null);
		buttonPanel.add(removeParameter, null);
		buttonPanel.add(cancelButton, null);
		buttonPanel.add(okButton, null);

		// assemble
		@NonNull final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.add(urlLabel, new GridBagConstraints(0, 1, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
		panel.add(this.urlTextField, new GridBagConstraints(0, 2, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));
		panel.add(queryLabel, new GridBagConstraints(0, 3, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
		panel.add(scrollPane, new GridBagConstraints(0, 4, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
		panel.add(buttonPanel, new GridBagConstraints(0, 5, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		setContentPane(panel);
	}

	// A C C E S S

	/**
	 * Make url end point
	 *
	 * @return url end point
	 */
	private String getURL(final String url, final String query)
	{
		String result = url;
		if (query != null)
		{
			result += "?";
			result += query;
		}
		return result;
	}

	/**
	 * Make query
	 *
	 * @return stringified query
	 */
	private String getQuery(final ParameterModel model)
	{
		if (model != null)
		{
			final Properties parameters = model.getProperties();
			if (parameters != null && parameters.size() != 0)
			{
				return UrlDialog.properties2Query(parameters);
			}
		}
		return null;
	}

	// P A R A M S . H E L P E R S

	/**
	 * Convert query to properties
	 *
	 * @param query queries
	 * @return property set
	 */
	@NonNull
	static private Properties query2Properties(final String query)
	{
		final Properties properties = new Properties();
		@NonNull final StringTokenizer stringTokenizer = new StringTokenizer(query, "&");
		while (stringTokenizer.hasMoreTokens())
		{
			final StringTokenizer stringTokenizer2 = new StringTokenizer(stringTokenizer.nextToken(), "=");
			if (stringTokenizer2.countTokens() != 2)
			{
				continue;
			}

			final String name = stringTokenizer2.nextToken();
			final String value = UrlDialog.decode(stringTokenizer2.nextToken());
			properties.put(name, value);
		}
		return properties;
	}

	/**
	 * Convert properties to query
	 *
	 * @param params properties
	 * @return query string
	 */
	static private String properties2Query(@Nullable final Properties params)
	{
		if (params == null)
		{
			return null;
		}

		@NonNull StringBuilder query = new StringBuilder();
		int i = 0;
		for (final String key : params.stringPropertyNames())
		{
			final String value = params.getProperty(key);
			if (i++ != 0)
			{
				query.append("&");
			}
			query.append(key).append("=").append(UrlDialog.encode(value));
		}
		return query.toString();
	}

	// D E C O D E / E N C O D E

	/**
	 * Decode encoded URL (for display)
	 *
	 * @param string encode URL string
	 * @return decoded URL string
	 */
	static private String decode(final String string)
	{
		try
		{
			return URLDecoder.decode(string, "UTF8");
		}
		catch (final UnsupportedEncodingException e)
		{
			System.err.println(Messages.getString("UrlDialog.err_decode") + string + " - " + e);
		}
		return string;
	}

	/**
	 * Encode encoded URL
	 *
	 * @param string encode URL string
	 * @return decoded URL string
	 */
	static private String encode(final String string)
	{
		try
		{
			return URLEncoder.encode(string, "UTF8");
		}
		catch (final UnsupportedEncodingException e)
		{
			System.err.println(Messages.getString("UrlDialog.err_decode") + string + " - " + e);
		}
		return string;
	}

	// M O D E L

	/**
	 * Parameter model
	 *
	 * @author Bernard Bou
	 */
	private static class ParameterModel extends AbstractTableModel
	{
		/**
		 * Serial version uid
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Properties
		 */
		private final Vector<Entry> entries = new Vector<>();

		private static class Entry
		{
			/**
			 * Key
			 */
			public String key;

			/**
			 * Value
			 */
			public String value;

			/**
			 * Constructor
			 */
			public Entry()
			{
				this.key = null;
				this.value = null;
			}

			/**
			 * Constructor
			 *
			 * @param key   key
			 * @param value value
			 */
			public Entry(final String key, final String value)
			{
				this.key = key;
				this.value = value;
			}
		}

		/**
		 * Parameter model
		 *
		 * @param params parameters
		 */
		public ParameterModel(@Nullable final Properties params)
		{
			if (params == null)
			{
				newRow(null);
			}
			else
			{
				for (final Object key : params.keySet())
				{
					final Object value = params.get(key);
					newRow(new Entry((String) key, (String) value));
				}
			}
		}

		/**
		 * New row
		 *
		 * @param entry entry
		 * @return row index
		 */
		@SuppressWarnings("UnusedReturnValue")
		public int newRow(@Nullable final Entry entry)
		{
			this.entries.add(entry != null ? entry : new Entry());
			final int rowIdx = this.entries.size() - 1;
			fireTableRowsInserted(rowIdx, rowIdx);
			return rowIdx;
		}

		/**
		 * Delete row
		 *
		 * @param rowIdx row index
		 */
		public void deleteRow(final int rowIdx)
		{
			if (rowIdx >= this.entries.size())
			{
				return;
			}
			this.entries.remove(rowIdx);
			fireTableRowsDeleted(rowIdx, rowIdx);
		}

		/**
		 * Get properties
		 *
		 * @return properties
		 */
		@Nullable
		public Properties getProperties()
		{
			@Nullable Properties properties = null;
			for (@NonNull final Entry entry : this.entries)
			{
				if (entry.key != null && !entry.key.isEmpty() && entry.value != null && !entry.value.isEmpty())
				{
					if (properties == null)
					{
						properties = new Properties();
					}
					properties.put(entry.key, entry.value);
				}
			}
			return properties;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount()
		{
			return this.entries.size();
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount()
		{
			return 2;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(final int y, final int x)
		{
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
		 */
		@Override
		public void setValueAt(final Object value, final int y, final int x)
		{
			final Entry entry = this.entries.get(y);
			switch (x)
			{
				case 0:
					entry.key = (String) value;
					break;
				case 1:
					entry.value = (String) value;
					break;
				default:
					break;
			}
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Nullable
		@Override
		public Object getValueAt(final int y, final int x)
		{
			final Entry entry = this.entries.get(y);
			switch (x)
			{
				case 0:
					return entry.key;
				case 1:
					return entry.value;
				default:
					return null;
			}
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		@NonNull
		@Override
		public String getColumnName(final int x)
		{
			switch (x)
			{
				case 0:
					return Messages.getString("UrlDialog.name");
				case 1:
					return Messages.getString("UrlDialog.value");
				default:
					return "";
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			this.ok = false;

			// read properties into components
			final String property = this.properties.getProperty("openurl");
			if (property != null)
			{
				final String[] fields = property.split("\\?");
				final String url = fields.length > 0 ? fields[0] : null;
				final String query = fields.length > 1 ? fields[1] : null;
				if (url != null)
				{
					this.urlTextField.setText(url);
					this.urlTextField.selectAll();
				}
				if (query != null)
				{
					@NonNull final Properties parameters = UrlDialog.query2Properties(query);
					final ParameterModel model = new ParameterModel(parameters);
					this.queryTable.setModel(model);
					this.queryTable.getColumnModel().getColumn(0).setMaxWidth(90);
				}
			}

			pack();
			Utils.center(this);
		}
		else
		{
			if (this.ok)
			{
				// update properties from components
				final String url = this.urlTextField.getText();
				final String query = getQuery((ParameterModel) this.queryTable.getModel());
				this.properties.setProperty("openurl", getURL(url, query));
			}
			else
			{
				this.properties.remove("openurl");
			}
		}
		super.setVisible(flag);
	}
}
