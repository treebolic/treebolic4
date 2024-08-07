/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.studio.model.ModelUtils;

/**
 * Image list dialog
 *
 * @author Bernard Bou
 */
public class ImageListDialog extends ReferenceListDialog
{
	/**
	 * Image repository
	 */
	@Nullable
	private URL imageRepository;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param controller controller
	 */
	public ImageListDialog(final Controller controller)
	{
		super(controller);
		setTitle(Messages.getString("ImageListDialog.title"));
		this.label.setText(Messages.getString("ImageListDialog.label"));
		this.referenceTable.setRowHeight(32);
		this.scrollPane.setPreferredSize(new Dimension(300, 320));

		@NonNull final JButton checkMissingButton = new JButton(Messages.getString("ImageListDialog.missing"));
		checkMissingButton.addActionListener(e -> checkMissing());
		@NonNull final JButton checkUnusedButton = new JButton(Messages.getString("ImageListDialog.unused"));
		checkUnusedButton.addActionListener(e -> checkUnused());
		this.buttonPanel.add(checkMissingButton, null);
		this.buttonPanel.add(checkUnusedButton, null);
	}

	// O V E R R I D E S

	@Override
	protected void update()
	{
		assert this.controller.getModel() != null;
		this.label.setText(Messages.getString("ImageListDialog.label"));
		this.imageRepository = this.controller.makeImageRepositoryURL();
		@NonNull final Map<String, SortedSet<String>> imageToLocationMap = ModelUtils.getImageMap(this.controller.getModel());
		setModel(imageToLocationMap);
	}

	@Override
	protected void setModel(final Map<String, SortedSet<String>> targetToLocationMap)
	{
		super.setModel(targetToLocationMap);

		// renderer
		final TableColumn imageColumn = this.referenceTable.getColumnModel().getColumn(0);
		imageColumn.setWidth(150);
		imageColumn.setCellRenderer(new DefaultTableCellRenderer()
		{
			@NonNull
			@SuppressWarnings("synthetic-access")
			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
			{
				final ParameterModel.Entry entry = (ParameterModel.Entry) value;
				@NonNull final String imageFile = entry.key;
				setIcon(makeIcon(imageFile));
				setText(imageFile);
				return this;
			}
		});

		final TableColumn locationColumn = this.referenceTable.getColumnModel().getColumn(1);
		locationColumn.setCellRenderer(new DefaultTableCellRenderer()
		{
			@NonNull
			@Override
			public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
			{
				final ParameterModel.Entry entry = (ParameterModel.Entry) value;
				@NonNull final String location = entry.value;
				if (location.startsWith("default") || location.equals("background"))
				{
					setBackground(Color.LIGHT_GRAY);
					setForeground(Color.WHITE);
				}
				else if (location.startsWith("node"))
				{
					setBackground(Color.WHITE);
					setForeground(Color.BLUE);
				}
				else if (location.startsWith("treeedge") || location.startsWith("edge"))
				{
					setBackground(Color.WHITE);
					setForeground(Color.BLACK);
				}
				else if (location.startsWith("unused"))
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
		assert this.controller.getModel() != null;
		this.imageRepository = this.controller.makeImageRepositoryURL();
		if (this.imageRepository != null)
			return;

		@NonNull final Map<String, SortedSet<String>> imageToLocationMap = ModelUtils.getImageMap(this.controller.getModel());
		@NonNull final Set<String> images = imageToLocationMap.keySet();

		if (this.imageRepository.getProtocol().equals("file"))
		{
			try
			{
				@NonNull final URI uri = this.imageRepository.toURI();
				@NonNull final File file = new File(uri);
				if (file.isDirectory())
				{
					@Nullable final File[] files = file.listFiles();
					if (files != null)
					{
						for (@NonNull final File directoryEntry : files)
						{
							@NonNull final String name = directoryEntry.getName();
							if (images.contains(name))
							{
								imageToLocationMap.remove(name);
							}
						}
					}
					setModel(imageToLocationMap);
					this.label.setText(Messages.getString("ImageListDialog.label_missing") + uri);
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
		assert this.controller.getModel() != null;
		this.imageRepository = this.controller.makeImageRepositoryURL();
		if (this.imageRepository != null)
			return;

		@NonNull final Map<String, SortedSet<String>> unusedToLocationMap = new TreeMap<>();
		@NonNull final Map<String, SortedSet<String>> imageToLocationMap = ModelUtils.getImageMap(this.controller.getModel());
		@NonNull final Set<String> images = imageToLocationMap.keySet();

		if (this.imageRepository.getProtocol().equals("file"))
		{
			try
			{
				@NonNull final URI uri = this.imageRepository.toURI();
				@NonNull final File file = new File(uri);
				if (file.isDirectory())
				{
					@Nullable final File[] files = file.listFiles();
					if (files != null)
					{
						for (@NonNull final File directoryEntry : files)
						{
							if (directoryEntry.isDirectory())
							{
								continue;
							}
							@NonNull final String name = directoryEntry.getName();
							if (!images.contains(name))
							{
								@NonNull final SortedSet<String> value = new TreeSet<>();
								value.add(Messages.getString("ImageListDialog.is_unused"));
								unusedToLocationMap.put(name, value);
							}
						}
					}
					setModel(unusedToLocationMap);
					this.label.setText(Messages.getString("ImageListDialog.label_unused") + uri);
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
	 * @param imageFile image file
	 * @return icon
	 */
	@Nullable
	private Icon makeIcon(@NonNull final String imageFile)
	{
		try
		{
			@NonNull final URL url = new URL(this.imageRepository, imageFile);
			return new ImageIcon(url);
		}
		catch (final MalformedURLException e)
		{
			return null;
		}
	}
}
