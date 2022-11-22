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

// --Commented out by Inspection START (11/21/22, 6:34 PM):
//	/**
//	 * Repository
//	 */
//	static public final int REPO = 1 << XSettingsDialog.REPOIDX;
// --Commented out by Inspection STOP (11/21/22, 6:34 PM)

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
		this.repositoryTextField.setToolTipText(Messages.getString("XSettingsDialog.tooltip_repo"));
		final JLabel repositoryFolderLabel = new JLabel(Messages.getString("XSettingsDialog.label_repo"));
		final JButton repositoryBrowseButton = new JButton(Messages.getString("XSettingsDialog.browse"));
		repositoryBrowseButton.addActionListener(event -> {
			final String folder = FileDialogs.getFolder(XSettingsDialog.this.settings.getProperty("base", "."));
			if (folder != null && !folder.isEmpty())
			{
				XSettingsDialog.this.repositoryTextField.setText(folder);
			}
			else
			{
				XSettingsDialog.this.repositoryTextField.setText(Messages.getString("XSettingsDialog.internal"));
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
			this.repositoryTextField.setText(this.settings.getProperty("repository", Messages.getString("XSettingsDialog.internal")));
		}
		else
		{
			if (this.ok)
			{
				// update properties from components
				final String repository = this.repositoryTextField.getText();
				if (repository == null || repository.isEmpty() || repository.equals(Messages.getString("XSettingsDialog.internal")))
				{
					this.settings.remove("repository");
				}
				else
				{
					this.settings.setProperty("repository", repository);
				}
			}
		}
		super.setVisible(flag);
	}

	/**
	 * Main entry point
	 *
	 * @param args argmuments
	 */
	static public void main(final String[] args)
	{
		UIManager.put("swing.boldMetal", false);
		final Properties settings = Persist.getSettings("treebolic-studio");
		final XSettingsDialog dialog = new XSettingsDialog(settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			Persist.saveSettings("treebolic-studio", settings);
		}
		System.exit(0);
	}
}
