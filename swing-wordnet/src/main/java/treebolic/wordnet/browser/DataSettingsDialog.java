/**
 * Title : Treebolic
 * Description : Treebolic
 * Version : 3.x
 * Copyright :	(c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.wordnet.browser;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.*;

import treebolic.commons.Persist;
import treebolic.commons.Utils;

/**
 * Settings dialog
 *
 * @author Bernard Bou
 */
public class DataSettingsDialog extends JDialog
{
	// V A L U E S

	/**
	 * Properties (input/output)
	 */
	protected final Properties settings;

	/**
	 * Ok result
	 */
	public boolean ok;

	/**
	 * Changed value
	 */
	public boolean changed;

	// C O M P O N E N T S

	/**
	 * Provider input
	 */
	private JComboBox<String> dataComboBox;

	/**
	 * Data panel
	 */
	protected JPanel dataPanel;

	/**
	 * Constructor
	 *
	 * @param settings settings
	 */
	public DataSettingsDialog(final Properties settings)
	{
		super();
		this.settings = settings;
		initialize();
	}

	/**
	 * Initialize
	 */
	protected void initialize()
	{
		setTitle(Messages.getString("DataSettingsDialog.title"));
		setResizable(true);

		// images
		final URL dataSettingsIconUrl = DataSettingsDialog.class.getResource("images/datasettings.png");
		assert dataSettingsIconUrl != null;
		final Icon icon = new ImageIcon(dataSettingsIconUrl);
		final JLabel headerLabel = new JLabel();
		headerLabel.setIcon(icon);
		headerLabel.setVerticalTextPosition(SwingConstants.TOP);
		headerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		headerLabel.setText(Messages.getString("DataSettingsDialog.header"));
		headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// buttons
		final JButton oKButton = new JButton(Messages.getString("DataSettingsDialog.ok"));
		oKButton.addActionListener(event -> {
			DataSettingsDialog.this.ok = true;
			setVisible(false);
		});
		final JButton cancelButton = new JButton(Messages.getString("DataSettingsDialog.cancel"));
		cancelButton.addActionListener(event -> setVisible(false));

		// panels
		this.dataPanel = new JPanel();
		this.dataPanel.setLayout(new GridBagLayout());

		// buttons
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(cancelButton);
		buttonPanel.add(oKButton);

		// P R O V I D E R
		// input
		this.dataComboBox = new JComboBox<>();
		this.dataComboBox.setEditable(false);
		this.dataComboBox.setPreferredSize(new Dimension(280, 24));
		this.dataComboBox.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
			{
				String str = (String) value;

				if (str != null)
				{
					try
					{
						URL url = new URL(str);
						String urlFile = url.getFile();
						Path path = Paths.get(urlFile);
						str = path.getFileName() + " (" + url.getHost() + ')';
					}
					catch (MalformedURLException e)
					{
						//
					}
				}
				return super.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
			}
		});

		// providers
		final String[] providers = new String[]{"WN31", "OEWN"};
		for (final String item : providers)
		{
			this.dataComboBox.addItem(item);
		}

		// tooltips
		this.dataComboBox.setToolTipText(Messages.getString("DataSettingsDialog.tooltip_data"));

		// label
		final JLabel providerLabel = new JLabel(Messages.getString("DataSettingsDialog.data"));

		// button
		final JButton providerAddButton = new JButton(Messages.getString("DataSettingsDialog.other"));
		providerAddButton.addActionListener(event -> {
			final String other = ask(Messages.getString("DataSettingsDialog.prompt_other"), "https://x-englishwordnet.github.io/wndb/oewn_2022.zip");
			if (other != null && !other.isEmpty())
			{
				if (!isInModel(other, DataSettingsDialog.this.dataComboBox))
				{
					DataSettingsDialog.this.dataComboBox.addItem(other);
				}
				DataSettingsDialog.this.dataComboBox.setSelectedItem(other);
			}
		});

		// assemble
		this.dataPanel.add(providerLabel, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
		this.dataPanel.add(this.dataComboBox, new GridBagConstraints(1, 0, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
		this.dataPanel.add(providerAddButton, new GridBagConstraints(2, 0, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));

		// assemble
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(headerLabel);
		panel.add(this.dataPanel);
		panel.add(buttonPanel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		setContentPane(panel);
	}

	/**
	 * Ask
	 *
	 * @param message      message
	 * @param initialValue initial value
	 * @return input
	 */
	protected String ask(final String message, @SuppressWarnings("SameParameterValue") final String initialValue)
	{
		final String[] lines = message.split("\n");
		return JOptionPane.showInputDialog(null, lines, initialValue);
	}

	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			this.ok = false;
			this.changed = false;

			// read properties into components
			final String provider = this.settings.getProperty("data");
			if (provider != null && !provider.isEmpty())
			{
				if (!isInModel(provider, DataSettingsDialog.this.dataComboBox))
				{
					this.dataComboBox.addItem(provider);
				}
				this.dataComboBox.setSelectedItem(provider);
			}

			pack();
			Utils.center(this);
		}
		else
		{
			if (this.ok)
			{
				// update properties from components
				String newValue = (String) this.dataComboBox.getSelectedItem();
				Object previousValue = this.settings.setProperty("data", newValue);
				this.changed = newValue == null || !newValue.equals(previousValue);
			}
		}
		super.setVisible(flag);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean isInModel(final String value, final JComboBox<String> comboBox)
	{
		final ComboBoxModel<String> model = comboBox.getModel();
		int size = model.getSize();
		for (int i = 0; i < size; i++)
		{
			final String item = model.getElementAt(i);
			if (item.equals(value))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Main entry point
	 *
	 * @param args arguments
	 */
	static public void main(final String[] args)
	{
		UIManager.put("swing.boldMetal", false);
		final Properties settings = Persist.getSettings(MainFrame.getStaticPersistName());
		final DataSettingsDialog dialog = new DataSettingsDialog(settings);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			Persist.saveSettings(MainFrame.getStaticPersistName(), settings);
		}
		System.exit(0);
	}
}
