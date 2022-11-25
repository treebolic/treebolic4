/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator;

import java.awt.*;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.*;

import treebolic.commons.Utils;
import treebolic.generator.ReferenceListDialog.ParameterModel.Entry;

/**
 * Reference list dialog
 *
 * @author Bernard Bou
 */
public class ReferenceListDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Controller
	 */
	protected final Controller controller;

	// C O M P O N E N T S

	/**
	 * Label
	 */
	protected JLabel label;

	/**
	 * Reference table
	 */
	protected JTable referenceTable;

	/**
	 * Reference table scrollpane
	 */
	protected JScrollPane scrollPane;

	/**
	 * Button panel
	 */
	protected JPanel buttonPanel;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param controller controller
	 */
	public ReferenceListDialog(final Controller controller)
	{
		super();
		this.controller = controller;
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		setResizable(true);

		// label
		this.label = new JLabel();

		// reference table
		this.referenceTable = new JTable();
		this.referenceTable.setToolTipText(Messages.getString("ReferenceListDialog.tooltip"));

		// update
		update();

		// scroll pane
		this.scrollPane = new JScrollPane(this.referenceTable);

		// buttons
		final JButton updateButton = new JButton(Messages.getString("ReferenceListDialog.update"));
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		updateButton.addActionListener(e -> update());
		final JButton cancelButton = new JButton(Messages.getString("ReferenceListDialog.cancel"));
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		cancelButton.addActionListener(e -> setVisible(false));

		// button panel
		this.buttonPanel = new JPanel();
		this.buttonPanel.add(cancelButton, null);
		this.buttonPanel.add(updateButton, null);

		// assemble
		final JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.add(this.label, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		panel.add(this.scrollPane, new GridBagConstraints(0, 1, 1, 1, 1., 1., GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 5, 10), 0, 0));
		panel.add(this.buttonPanel, new GridBagConstraints(0, 2, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 10, 10), 0, 0));

		setContentPane(panel);
	}

	/**
	 * Update
	 */
	protected void update()
	{
		// renderer
		final TableColumn imageColumn = this.referenceTable.getColumnModel().getColumn(0);
		imageColumn.setWidth(150);
		imageColumn.setCellRenderer(new DefaultTableCellRenderer()
		{
			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
			 */
			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
			{
				final ParameterModel.Entry entry = (ParameterModel.Entry) value;
				setText(entry.key);
				setBackground(entry.valueCount > 1 ? Color.PINK : Color.WHITE);
				return this;
			}
		});

		final TableColumn locationColumn = this.referenceTable.getColumnModel().getColumn(1);
		locationColumn.setCellRenderer(new DefaultTableCellRenderer()
		{
			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
			 */
			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
			{
				final ParameterModel.Entry entry = (ParameterModel.Entry) value;
				final String location = entry.value;
				setText(location);
				return this;
			}
		});
	}

	/**
	 * Set model
	 *
	 * @param targetToLocationMap target to location map
	 */
	protected void setModel(final Map<String, SortedSet<String>> targetToLocationMap)
	{
		final TableModel model = new ParameterModel(targetToLocationMap);
		this.referenceTable.setModel(model);

		// sort
		final TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
		this.referenceTable.setRowSorter(sorter);
		sorter.setComparator(0, Comparator.comparing((Entry entry) -> entry.key));
		sorter.setComparator(1, Comparator.comparing((Entry entry) -> entry.value));

		// init sort
		sorter.toggleSortOrder(0);

		// renderer
		final TableColumn targetColumn = this.referenceTable.getColumnModel().getColumn(0);
		targetColumn.setWidth(150);
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
			pack();
			Utils.center(this);
		}
		super.setVisible(flag);
	}

	// M O D E L

	/**
	 * Parameter model
	 *
	 * @author Bernard Bou
	 */
	protected static class ParameterModel extends AbstractTableModel
	{
		/**
		 * Serial version uid
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Properties
		 */
		private final Vector<Entry> entries = new Vector<>();

		static class Entry
		{
			/**
			 * Key
			 */
			public final String key;

			/**
			 * Value
			 */
			public final String value;

			/**
			 * Number of values for key
			 */
			public final int valueCount;

			/**
			 * Constructor
			 *
			 * @param key   key
			 * @param value value
			 */
			public Entry(final String key, final String value, final int valueCount)
			{
				this.key = key.trim();
				this.value = value.trim();
				this.valueCount = valueCount;
			}
		}

		/**
		 * Parameter model
		 *
		 * @param targetToLocationMap target location map
		 */
		public ParameterModel(final Map<String, SortedSet<String>> targetToLocationMap)
		{
			if (targetToLocationMap == null)
			{
				return;
			}

			for (final String target : targetToLocationMap.keySet())
			{
				final SortedSet<String> locations = targetToLocationMap.get(target);
				final int count = locations.size();
				for (final String location : locations)
				{
					newRow(new Entry(target, location, count));
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
		public int newRow(final Entry entry)
		{
			this.entries.add(entry);
			final int rowIdx = this.entries.size() - 1;
			fireTableRowsInserted(rowIdx, rowIdx);
			return rowIdx;
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
			return false;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(final int y, final int x)
		{
			return this.entries.get(y);
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(final int x)
		{
			switch (x)
			{
				case 0:
					return Messages.getString("ReferenceListDialog.name");
				case 1:
					return Messages.getString("ReferenceListDialog.value");
				default:
					return null;
			}
		}
	}
}