/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;
import java.util.Properties;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Site dialog
 *
 * @author Bernard Bou
 */
public class SiteDialog extends JDialog
{
	/**
	 * Mode types
	 */
	private enum Mode
	{
		FILE, NET
	}

	// V A L U E S

	/**
	 * Properties (input/output)
	 */
	private final Properties properties;

	/**
	 * Result
	 */
	public boolean ok;

	// C O M P O N E N T S

	/**
	 * Combo box
	 */
	private JComboBox<Object> comboBox;

	/**
	 * Server field
	 */
	private JTextField serverTextField;

	/**
	 * Directory field
	 */
	private JTextField directoryTextField;

	/**
	 * Login field
	 */
	private JTextField loginTextField;

	/**
	 * Password field
	 */
	private JPasswordField passwordTextField;

	/**
	 * Path field
	 */
	private JTextField pathTextField;

	// C O N S T R U C T OR

	/**
	 * Constructor
	 *
	 * @param properties properties
	 */
	public SiteDialog(final Properties properties)
	{
		this.properties = properties;
		initialize();
	}

	/**
	 * Initialize dialog
	 */
	private void initialize()
	{
		setTitle(Messages.getString("SiteDialog.title"));
		setResizable(true);

		// images
		@NonNull @SuppressWarnings("ConstantConditions") final Icon icon = new ImageIcon(SiteDialog.class.getResource("images/sitemake.png"));
		@NonNull final JLabel headerLabel = new JLabel();
		headerLabel.setIcon(icon);
		headerLabel.setVerticalTextPosition(SwingConstants.TOP);
		headerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		headerLabel.setText(Messages.getString("SiteDialog.header"));
		headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		@NonNull @SuppressWarnings("ConstantConditions") final Icon fileIcon = new ImageIcon(SiteDialog.class.getResource("images/local.png"));
		@NonNull @SuppressWarnings("ConstantConditions") final Icon httpIcon = new ImageIcon(SiteDialog.class.getResource("images/net.png"));

		// mode combo
		@NonNull final Object[] options = {Mode.FILE, Mode.NET};
		this.comboBox = new JComboBox<>(options);
		this.comboBox.setRenderer(new ListCellRenderer<Object>()
		{
			private final JLabel label = new JLabel();

			@NonNull
			@Override
			public Component getListCellRendererComponent(final JList<?> list, @NonNull final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
			{
				this.label.setText(value.toString());
				this.label.setIcon(value.equals(Mode.FILE) ? fileIcon : httpIcon);
				return this.label;
			}
		});

		// text fields
		this.serverTextField = new JTextField(16);
		this.directoryTextField = new JTextField(16);
		this.loginTextField = new JTextField(16);
		this.passwordTextField = new JPasswordField(16);
		this.pathTextField = new JTextField(16);

		// buttons
		@NonNull final JButton oKButton = new JButton(Messages.getString("SiteDialog.ok"));
		@NonNull final JButton cancelButton = new JButton(Messages.getString("SiteDialog.cancel"));
		@NonNull final JButton browsePathButton = new JButton(Messages.getString("SiteDialog.browse"));

		// panels
		@NonNull final JPanel selectionPanel = new JPanel();
		selectionPanel.add(new JLabel(Messages.getString("SiteDialog.mode")));
		selectionPanel.setLayout(new FlowLayout());
		selectionPanel.add(this.comboBox);

		@NonNull final JPanel pathPanel = new JPanel();
		pathPanel.setLayout(new FlowLayout());
		pathPanel.add(new JLabel(Messages.getString("SiteDialog.localdir")));
		pathPanel.add(this.pathTextField);

		@NonNull final JPanel filePanel = new JPanel();
		filePanel.setLayout(new FlowLayout());
		filePanel.add(pathPanel);
		filePanel.add(browsePathButton);

		@NonNull final JPanel fTPPanel = new JPanel();
		fTPPanel.setLayout(new GridBagLayout());
		fTPPanel.add(new JLabel(Messages.getString("SiteDialog.server")), new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
		fTPPanel.add(new JLabel(Messages.getString("SiteDialog.serverdir")), new GridBagConstraints(0, 1, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
		fTPPanel.add(new JLabel(Messages.getString("SiteDialog.login")), new GridBagConstraints(0, 2, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
		fTPPanel.add(new JLabel(Messages.getString("SiteDialog.password")), new GridBagConstraints(0, 3, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 10), 0, 0));
		fTPPanel.add(this.serverTextField, new GridBagConstraints(1, 0, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
		fTPPanel.add(this.directoryTextField, new GridBagConstraints(1, 1, 1, 1, 1., 01., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
		fTPPanel.add(this.loginTextField, new GridBagConstraints(1, 2, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));
		fTPPanel.add(this.passwordTextField, new GridBagConstraints(1, 3, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 20), 0, 0));

		@NonNull final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(cancelButton);
		buttonPanel.add(oKButton);

		// tab init
		@NonNull final JTabbedPane tabbedPanel = new JTabbedPane();
		tabbedPanel.addTab(Mode.FILE.toString(), fileIcon, filePanel, Messages.getString("SiteDialog.local"));
		tabbedPanel.addTab(Mode.NET.toString(), httpIcon, fTPPanel, Messages.getString("SiteDialog.net"));

		// connect selections
		tabbedPanel.addChangeListener(e -> {
			final int index = tabbedPanel.getSelectedIndex();
			SiteDialog.this.comboBox.setSelectedIndex(index);
		});
		this.comboBox.addActionListener(e -> {
			final int index = SiteDialog.this.comboBox.getSelectedIndex();
			tabbedPanel.setSelectedIndex(index);
		});

		// assemble tabs
		@NonNull final JPanel settingsPanel = new JPanel();
		settingsPanel.add(tabbedPanel);

		// events
		browsePathButton.addActionListener(e -> {
			@Nullable final String path = FileDialogs.getFolder(SiteDialog.this.properties.getProperty("base", "."));
			if (path != null)
			{
				SiteDialog.this.pathTextField.setText(path);
			}
		});
		oKButton.addActionListener(e -> {
			SiteDialog.this.ok = true;
			setVisible(false);
		});
		cancelButton.addActionListener(e -> setVisible(false));

		// assemble
		@NonNull final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(headerLabel);
		panel.add(selectionPanel);
		panel.add(settingsPanel);
		panel.add(buttonPanel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		setContentPane(panel);
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
			this.serverTextField.setText(this.properties.getProperty("server"));
			this.directoryTextField.setText(this.properties.getProperty("directory"));
			this.loginTextField.setText(this.properties.getProperty("login"));
			this.passwordTextField.setText(this.properties.getProperty("password"));
			this.pathTextField.setText(this.properties.getProperty("path"));

			@NonNull final Mode mode = this.properties.getProperty("mode") != null ? Mode.valueOf(this.properties.getProperty("mode")) : Mode.FILE;
			this.comboBox.setSelectedItem(mode);

			pack();
			Utils.center(this);
		}
		else
		{
			if (this.ok)
			{
				// update properties from components
				@Nullable final Mode mode = (Mode) this.comboBox.getSelectedItem();
				assert mode != null;
				this.properties.setProperty("mode", mode.toString());
				switch (mode)
				{
					case NET:
						this.properties.setProperty("server", this.serverTextField.getText());
						this.properties.setProperty("directory", this.directoryTextField.getText());
						this.properties.setProperty("login", this.loginTextField.getText());
						this.properties.setProperty("password", new String(this.passwordTextField.getPassword()));
						break;

					case FILE:
						this.properties.setProperty("path", this.pathTextField.getText());
						break;
				}
			}
		}
		super.setVisible(flag);
	}
}
