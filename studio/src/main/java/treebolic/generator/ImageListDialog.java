/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import treebolic.generator.model.ModelUtils;

/**
 * Image list dialog
 *
 * @author Bernard Bou
 */
public class ImageListDialog extends ReferenceListDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Image repository
	 */
	private URL imageRepository;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public ImageListDialog(final Controller controller)
	{
		super(controller);
		setTitle(Messages.getString("ImageListDialog.title")); //$NON-NLS-1$
		this.label.setText(Messages.getString("ImageListDialog.label")); //$NON-NLS-1$
		this.referenceTable.setRowHeight(32);
		this.scrollPane.setPreferredSize(new Dimension(300, 320));

		final JButton checkMissingButton = new JButton(Messages.getString("ImageListDialog.missing")); //$NON-NLS-1$
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		checkMissingButton.addActionListener(e -> checkMissing());
		final JButton checkUnusedButton = new JButton(Messages.getString("ImageListDialog.unused")); //$NON-NLS-1$
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		checkUnusedButton.addActionListener(e -> checkUnused());
		this.buttonPanel.add(checkMissingButton, null);
		this.buttonPanel.add(checkUnusedButton, null);
	}

	// O V E R R I D E S

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.ReferenceListDialog#update()
	 */
	@Override
	protected void update()
	{
		this.label.setText(Messages.getString("ImageListDialog.label")); //$NON-NLS-1$
		this.imageRepository = this.controller.makeImageRepositoryURL();
		final Map<String, SortedSet<String>> imageToLocationMap = ModelUtils.getImageMap(this.controller.getModel());
		setModel(imageToLocationMap);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.ReferenceListDialog#setModel(java.util.Map)
	 */
	@Override
	protected void setModel(final Map<String, SortedSet<String>> targetToLocationMap)
	{
		super.setModel(targetToLocationMap);

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
			@SuppressWarnings("synthetic-access")
			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
			{
				final ParameterModel.Entry entry = (ParameterModel.Entry) value;
				final String imageFile = entry.key;
				setIcon(makeIcon(imageFile));
				setText(imageFile);
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
				if (location.startsWith("default") || location.equals("background")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					setBackground(Color.LIGHT_GRAY);
					setForeground(Color.WHITE);
				}
				else if (location.startsWith("node")) //$NON-NLS-1$
				{
					setBackground(Color.WHITE);
					setForeground(Color.BLUE);
				}
				else if (location.startsWith("treeedge") || location.startsWith("edge")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					setBackground(Color.WHITE);
					setForeground(Color.BLACK);
				}
				else if (location.startsWith("unused")) //$NON-NLS-1$
				{
					setBackground(Color.WHITE);
					setForeground(Color.BLACK);
				}
				else
				{
					setBackground(Color.RED);
					setForeground(Color.WHITE);
				}

				setText(location);
				return this;
			}
		});
	}

	// H E L P E R S

	/**
	 * Check missing files
	 */
	private void checkMissing()
	{
		this.imageRepository = this.controller.makeImageRepositoryURL();
		final Map<String, SortedSet<String>> imageToLocationMap = ModelUtils.getImageMap(this.controller.getModel());
		final Set<String> images = imageToLocationMap.keySet();

		if (this.imageRepository.getProtocol().equals("file")) //$NON-NLS-1$
		{
			try
			{
				final URI uri = this.imageRepository.toURI();
				final File file = new File(uri);
				if (file.isDirectory())
				{
					for (final File directoryEntry : file.listFiles())
					{
						final String name = directoryEntry.getName();
						if (images.contains(name))
						{
							imageToLocationMap.remove(name);
						}
					}
					setModel(imageToLocationMap);
					this.label.setText(Messages.getString("ImageListDialog.label_missing") + uri); //$NON-NLS-1$
				}
			}
			catch (final URISyntaxException exception)
			{
				// do nothing
			}
		}
	}

	/**
	 * Check unused files
	 */
	private void checkUnused()
	{
		this.imageRepository = this.controller.makeImageRepositoryURL();
		final Map<String, SortedSet<String>> unusedToLocationMap = new TreeMap<>();
		final Map<String, SortedSet<String>> imageToLocationMap = ModelUtils.getImageMap(this.controller.getModel());
		final Set<String> images = imageToLocationMap.keySet();

		if (this.imageRepository.getProtocol().equals("file")) //$NON-NLS-1$
		{
			try
			{
				final URI uri = this.imageRepository.toURI();
				final File file = new File(uri);
				if (file.isDirectory())
				{
					for (final File directoryEntry : file.listFiles())
					{
						if (directoryEntry.isDirectory())
						{
							continue;
						}
						final String name = directoryEntry.getName();
						if (!images.contains(name))
						{
							final SortedSet<String> value = new TreeSet<>();
							value.add(Messages.getString("ImageListDialog.is_unused")); //$NON-NLS-1$
							unusedToLocationMap.put(name, value);
						}
					}
					setModel(unusedToLocationMap);
					this.label.setText(Messages.getString("ImageListDialog.label_unused") + uri); //$NON-NLS-1$
				}
			}
			catch (final URISyntaxException exception)
			{
				// do nothing
			}
		}
	}

	/**
	 * Make icon
	 *
	 * @param imageFile
	 *        image file
	 * @return icon
	 */
	private Icon makeIcon(final String imageFile)
	{
		try
		{
			final URL url = new URL(this.imageRepository, imageFile);
			return new ImageIcon(url);
		}
		catch (final MalformedURLException e)
		{
			return null;
		}
	}
}
