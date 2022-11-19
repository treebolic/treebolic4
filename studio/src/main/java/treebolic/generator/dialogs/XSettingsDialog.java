/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.dialogs;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import treebolic.commons.FileDialogs;
import treebolic.commons.Persist;
import treebolic.commons.SettingsDialog;
import treebolic.generator.Messages;

/**
 * Settings dialog
 *
 * @author Bernard Bou
 */
public class XSettingsDialog extends SettingsDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Index and mask
	 */
	static private final int REPOIDX = 4;

	static public final int REPO = 1 << XSettingsDialog.REPOIDX;

	/**
	 * Repository text
	 */
	private JTextField repositoryTextField;

	/**
	 * Constructor
	 *
	 * @param properties properties
	 */
	public XSettingsDialog(final Properties properties)
	{
		super(properties, SettingsDialog.BASE | SettingsDialog.IMAGEBASE | SettingsDialog.HELP | SettingsDialog.BROWSER);
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.commons.SettingsDialog#initialize()
	 */
	@Override
	protected void initialize()
	{
		super.initialize();
		this.repositoryTextField = new JTextField(32);
		this.repositoryTextField.setToolTipText(Messages.getString("XSettingsDialog.tooltip_repo")); //$NON-NLS-1$
		final JLabel repositoryFolderLabel = new JLabel(Messages.getString("XSettingsDialog.label_repo")); //$NON-NLS-1$
		final JButton repositoryBrowseButton = new JButton(Messages.getString("XSettingsDialog.browse")); //$NON-NLS-1$
		repositoryBrowseButton.addActionListener(event -> {
			final String folder = FileDialogs.getFolder(XSettingsDialog.this.settings.getProperty("base", ".")); //$NON-NLS-1$ //$NON-NLS-2$
			if (folder != null && !folder.isEmpty())
			{
				XSettingsDialog.this.repositoryTextField.setText(folder);
			}
			else
			{
				XSettingsDialog.this.repositoryTextField.setText(Messages.getString("XSettingsDialog.internal")); //$NON-NLS-1$
			}
		});

		this.dataPanel.add(repositoryFolderLabel, new GridBagConstraints(0, XSettingsDialog.REPOIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
		this.dataPanel.add(this.repositoryTextField, new GridBagConstraints(1, XSettingsDialog.REPOIDX, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
		this.dataPanel.add(repositoryBrowseButton, new GridBagConstraints(2, XSettingsDialog.REPOIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			// read properties into components
			this.repositoryTextField.setText(this.settings.getProperty("repository", Messages.getString("XSettingsDialog.internal"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			if (this.ok)
			{
				// update properties from components
				final String repository = this.repositoryTextField.getText();
				if (repository == null || repository.isEmpty() || repository.equals(Messages.getString("XSettingsDialog.internal"))) //$NON-NLS-1$
				{
					this.settings.remove("repository"); //$NON-NLS-1$
				}
				else
				{
					this.settings.setProperty("repository", repository); //$NON-NLS-1$
				}
			}
		}
		super.setVisible(flag);
	}

	static public void main(final String[] args)
	{
		UIManager.put("swing.boldMetal", false); //$NON-NLS-1$
		final Properties settings = Persist.getSettings("treebolic-generator"); //$NON-NLS-1$
		final XSettingsDialog dialog = new XSettingsDialog(settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			Persist.saveSettings("treebolic-generator", settings); //$NON-NLS-1$
		}
		System.exit(0);
	}
}
