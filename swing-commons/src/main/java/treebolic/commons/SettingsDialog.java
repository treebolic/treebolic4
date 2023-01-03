/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;
import java.util.Properties;
import java.util.Set;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Settings dialog
 *
 * @author Bernard Bou
 */
public class SettingsDialog extends JDialog
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
	 * Flags
	 */
	private final long flags;

	/**
	 * Flags and indices
	 */
	static private final int PROVIDERIDX = 0;

	/**
	 * Provider bit
	 */
	static public final int PROVIDER = 1 << SettingsDialog.PROVIDERIDX;

	static private final int BASEIDX = 1;

	/**
	 * Document base
	 */
	static public final int BASE = 1 << SettingsDialog.BASEIDX;

	static private final int IMAGEBASEIDX = 2;

	/**
	 * Image base bit
	 */
	static public final int IMAGEBASE = 1 << SettingsDialog.IMAGEBASEIDX;

	static private final int HELPIDX = 8;

	/**
	 * Help bit
	 */
	static public final int HELP = 1 << SettingsDialog.HELPIDX;

	static private final int BROWSERIDX = 9;

	/**
	 * Browser bit
	 */
	static public final int BROWSER = 1 << SettingsDialog.BROWSERIDX;

	// C O M P O N E N T S

	/**
	 * Base input
	 */
	private JTextField baseTextField;

	/**
	 * Image base input
	 */
	private JTextField imageBaseTextField;

	/**
	 * Help input
	 */
	private JTextField helpTextField;

	/**
	 * Browser input
	 */
	private JTextField browserTextField;

	/**
	 * Provider input
	 */
	private JComboBox<String> providerComboBox;

	/**
	 * Data panel
	 */
	protected JPanel dataPanel;

	/**
	 * Constructor
	 *
	 * @param settings settings
	 */
	public SettingsDialog(final Properties settings)
	{
		this(settings, SettingsDialog.BASE | SettingsDialog.IMAGEBASE | SettingsDialog.HELP | SettingsDialog.BROWSER);
	}

	/**
	 * Constructor
	 *
	 * @param settings settings
	 * @param flags    extension flags
	 */
	public SettingsDialog(final Properties settings, final long flags)
	{
		super();
		this.flags = flags;
		this.settings = settings;
		initialize();
	}

	/**
	 * Initialize
	 */
	protected void initialize()
	{
		setTitle(Messages.getString("SettingsDialog.title"));
		setResizable(true);

		// images
		@SuppressWarnings("DataFlowIssue") @NonNull final Icon icon = new ImageIcon(SettingsDialog.class.getResource("images/settings.png"));
		@NonNull final JLabel headerLabel = new JLabel();
		headerLabel.setIcon(icon);
		headerLabel.setVerticalTextPosition(SwingConstants.TOP);
		headerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		headerLabel.setText(Messages.getString("SettingsDialog.header"));
		headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// buttons
		@NonNull final JButton oKButton = new JButton(Messages.getString("SettingsDialog.ok"));
		oKButton.addActionListener(event -> {
			SettingsDialog.this.ok = true;
			setVisible(false);
		});
		@NonNull final JButton cancelButton = new JButton(Messages.getString("SettingsDialog.cancel"));
		cancelButton.addActionListener(event -> setVisible(false));

		// panels
		this.dataPanel = new JPanel();
		this.dataPanel.setLayout(new GridBagLayout());

		// buttons
		@NonNull final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(cancelButton);
		buttonPanel.add(oKButton);

		// B A S E
		if ((this.flags & 1 << SettingsDialog.BASEIDX) != 0)
		{
			// label
			@NonNull final JLabel workFolderLabel = new JLabel(Messages.getString("SettingsDialog.base"));

			// input
			this.baseTextField = new JTextField(32);

			// tooltip
			this.baseTextField.setToolTipText(Messages.getString("SettingsDialog.tooltip_base"));

			// button
			@NonNull final JButton baseBrowseButton = new JButton(Messages.getString("SettingsDialog.browse"));

			// action
			baseBrowseButton.addActionListener(event -> {
				@Nullable final String folder = FileDialogs.getFolder(SettingsDialog.this.settings.getProperty("base", "."));
				if (folder != null && !folder.isEmpty())
				{
					SettingsDialog.this.baseTextField.setText(folder);
				}
			});

			// assemble
			this.dataPanel.add(workFolderLabel, new GridBagConstraints(0, SettingsDialog.BASEIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
			this.dataPanel.add(this.baseTextField, new GridBagConstraints(1, SettingsDialog.BASEIDX, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
			this.dataPanel.add(baseBrowseButton, new GridBagConstraints(2, SettingsDialog.BASEIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
		}

		// I M A G E B A S E
		if ((this.flags & 1 << SettingsDialog.IMAGEBASEIDX) != 0)
		{
			// label
			@NonNull final JLabel imageBaseFolderLabel = new JLabel(Messages.getString("SettingsDialog.images"));

			// input
			this.imageBaseTextField = new JTextField(32);

			// tooltip
			this.imageBaseTextField.setToolTipText(Messages.getString("SettingsDialog.tooltip_images"));

			// button
			@NonNull final JButton imageBaseBrowseButton = new JButton(Messages.getString("SettingsDialog.browse"));

			// action
			imageBaseBrowseButton.addActionListener(event -> {
				@Nullable final String folder = FileDialogs.getFolder(SettingsDialog.this.settings.getProperty("base", "."));
				if (folder != null && !folder.isEmpty())
				{
					SettingsDialog.this.imageBaseTextField.setText(folder);
				}
			});

			// assemble
			this.dataPanel.add(imageBaseFolderLabel, new GridBagConstraints(0, SettingsDialog.IMAGEBASEIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
			this.dataPanel.add(this.imageBaseTextField, new GridBagConstraints(1, SettingsDialog.IMAGEBASEIDX, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
			this.dataPanel.add(imageBaseBrowseButton, new GridBagConstraints(2, SettingsDialog.IMAGEBASEIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
		}

		// H E L P
		if ((this.flags & 1 << SettingsDialog.HELPIDX) != 0)
		{
			// label
			@NonNull final JLabel helpFolderLabel = new JLabel(Messages.getString("SettingsDialog.help"));

			// input
			this.helpTextField = new JTextField(32);

			// tooltip
			this.helpTextField.setToolTipText(Messages.getString("SettingsDialog.tooltip_help"));

			// button
			@NonNull final JButton helpBrowseButton = new JButton(Messages.getString("SettingsDialog.browse"));

			// action
			helpBrowseButton.addActionListener(event -> {
				@Nullable final String folder = FileDialogs.getFolder(SettingsDialog.this.settings.getProperty("base", "."));
				if (folder != null && !folder.isEmpty())
				{
					SettingsDialog.this.helpTextField.setText(folder);
				}
			});

			// assemble
			this.dataPanel.add(helpFolderLabel, new GridBagConstraints(0, SettingsDialog.HELPIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
			this.dataPanel.add(this.helpTextField, new GridBagConstraints(1, SettingsDialog.HELPIDX, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
			this.dataPanel.add(helpBrowseButton, new GridBagConstraints(2, SettingsDialog.HELPIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
		}

		// B R O W S E R
		if ((this.flags & 1 << SettingsDialog.BROWSERIDX) != 0)
		{
			// label
			@NonNull final JLabel browserLabel = new JLabel(Messages.getString("SettingsDialog.browser"));

			// input
			this.browserTextField = new JTextField(32);

			// tooltip
			this.browserTextField.setToolTipText(Messages.getString("SettingsDialog.tooltip_browser"));

			// button
			@NonNull final JButton browserBrowseButton = new JButton(Messages.getString("SettingsDialog.browse"));

			// action
			browserBrowseButton.addActionListener(event -> {
				@Nullable final String folder = FileDialogs.getExec(SettingsDialog.this.settings.getProperty("base", "."));
				if (folder != null && !folder.isEmpty())
				{
					SettingsDialog.this.browserTextField.setText(folder);
				}
			});

			// assemble
			this.dataPanel.add(browserLabel, new GridBagConstraints(0, SettingsDialog.BROWSERIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
			this.dataPanel.add(this.browserTextField, new GridBagConstraints(1, SettingsDialog.BROWSERIDX, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
			this.dataPanel.add(browserBrowseButton, new GridBagConstraints(2, SettingsDialog.BROWSERIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
		}

		// P R O V I D E R
		if ((this.flags & 1 << SettingsDialog.PROVIDERIDX) != 0)
		{
			// input
			this.providerComboBox = new JComboBox<>();
			this.providerComboBox.setEditable(true);
			this.providerComboBox.setPreferredSize(new Dimension(300, 24));
			this.providerComboBox.setRenderer(new DefaultListCellRenderer()
			{
				@Override
				public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
				{
					String string = (String) value;
					if (string != null)
					{
						final int position = string.lastIndexOf('/');
						if (position != -1)
						{
							string = string.substring(position + 1);
						}
					}
					return super.getListCellRendererComponent(list, string, index, isSelected, cellHasFocus);
				}
			});

			// providers
			@NonNull final Set<String> providers = Searcher.findClasses(".*\\.Provider");
			for (final String item : providers)
			{
				this.providerComboBox.addItem(item);
			}

			// tooltips
			this.providerComboBox.setToolTipText(Messages.getString("SettingsDialog.tooltip_provider"));

			// label
			@NonNull final JLabel providerLabel = new JLabel(Messages.getString("SettingsDialog.provider"));

			// button
			@NonNull final JButton providerAddButton = new JButton(Messages.getString("SettingsDialog.add"));
			providerAddButton.addActionListener(event -> {
				final String provider = ask(Messages.getString("SettingsDialog.prompt_provider"));
				if (provider != null && !provider.isEmpty())
				{
					SettingsDialog.this.providerComboBox.addItem(provider);
					SettingsDialog.this.providerComboBox.getEditor().setItem(provider);
				}
			});

			// assemble
			this.dataPanel.add(providerLabel, new GridBagConstraints(0, SettingsDialog.PROVIDERIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
			this.dataPanel.add(this.providerComboBox, new GridBagConstraints(1, SettingsDialog.PROVIDERIDX, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
			this.dataPanel.add(providerAddButton, new GridBagConstraints(2, SettingsDialog.PROVIDERIDX, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
		}

		// assemble
		@NonNull final JPanel panel = new JPanel();
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
	 * @param message message
	 * @return input
	 */
	protected String ask(@NonNull final String message)
	{
		@NonNull final String[] lines = message.split("\n");
		return JOptionPane.showInputDialog(null, lines);
	}

	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			this.ok = false;

			// read properties into components
			if ((this.flags & 1 << SettingsDialog.BASEIDX) != 0)
			{
				this.baseTextField.setText(this.settings.getProperty("base"));
			}
			if ((this.flags & 1 << SettingsDialog.IMAGEBASEIDX) != 0)
			{
				this.imageBaseTextField.setText(this.settings.getProperty("images"));
			}
			if ((this.flags & 1 << SettingsDialog.HELPIDX) != 0)
			{
				this.helpTextField.setText(this.settings.getProperty("help"));
			}
			if ((this.flags & 1 << SettingsDialog.BROWSERIDX) != 0)
			{
				this.browserTextField.setText(this.settings.getProperty("browser"));
			}
			if ((this.flags & 1 << SettingsDialog.PROVIDERIDX) != 0)
			{
				final String provider = this.settings.getProperty("provider");
				this.providerComboBox.getEditor().setItem(provider);
			}
			pack();
			Utils.center(this);
		}
		else
		{
			if (this.ok)
			{
				// update properties from components
				if ((this.flags & 1 << SettingsDialog.BASEIDX) != 0)
				{
					this.settings.setProperty("base", this.baseTextField.getText());
				}
				if ((this.flags & 1 << SettingsDialog.IMAGEBASEIDX) != 0)
				{
					this.settings.setProperty("images", this.imageBaseTextField.getText());
				}
				if ((this.flags & 1 << SettingsDialog.HELPIDX) != 0)
				{
					this.settings.setProperty("help", this.helpTextField.getText());
				}
				if ((this.flags & 1 << SettingsDialog.BROWSERIDX) != 0)
				{
					this.settings.setProperty("browser", this.browserTextField.getText());
				}
				if ((this.flags & 1 << SettingsDialog.PROVIDERIDX) != 0)
				{
					this.settings.setProperty("provider", (String) this.providerComboBox.getEditor().getItem());
				}
			}
		}
		super.setVisible(flag);
	}

	/**
	 * Main entry point
	 *
	 * @param args arguments
	 */
	static public void main(final String[] args)
	{
		UIManager.put("swing.boldMetal", false);
		@NonNull final Properties settings = Persist.getSettings("treebolic-browser");
		@NonNull final SettingsDialog dialog = new SettingsDialog(settings, SettingsDialog.PROVIDER | SettingsDialog.BASE);
		dialog.setModal(true);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (dialog.ok)
		{
			Persist.saveSettings("treebolic-browser", settings);
		}
		System.exit(0);
	}
}
