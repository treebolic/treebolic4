/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;
import java.util.Collection;
import java.util.Set;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Import dialog
 *
 * @author Bernard Bou
 */
public class OpenDialog extends JDialog
{
	// V A L U E S

	/**
	 * Provider
	 */
	public String provider;

	/**
	 * Source
	 */
	public String source;

	/**
	 * Base
	 */
	public final String base;

	/**
	 * Ok result
	 */
	public boolean ok;

	// C O M P O N E N T S

	/**
	 * Source text
	 */
	private JTextField sourceTextField;

	/**
	 * Provider text
	 */
	private JComboBox<String> providerComboBox;

	/**
	 * Data panel
	 */
	protected JPanel dataPanel;

	/**
	 * Constructor
	 *
	 * @param provider provider
	 * @param source   source
	 * @param base     base
	 */
	public OpenDialog(final String provider, final String source, final String base)
	{
		super();
		this.provider = provider;
		this.source = source;
		this.base = base;

		@NonNull final Set<String> presetProviders = Searcher.findClasses(".*\\.Provider");
		initialize(presetProviders);
	}

	/**
	 * Initialize
	 *
	 * @param providers providers
	 */
	protected void initialize(@Nullable final Collection<String> providers)
	{
		setTitle(Messages.getString("OpenDialog.title"));
		setResizable(true);

		// images
		@NonNull @SuppressWarnings("ConstantConditions") final Icon icon = new ImageIcon(OpenDialog.class.getResource("images/open.png"));
		@NonNull final JLabel headerLabel = new JLabel();
		headerLabel.setIcon(icon);
		headerLabel.setVerticalTextPosition(SwingConstants.TOP);
		headerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		headerLabel.setText(Messages.getString("OpenDialog.header"));
		headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// labels
		@NonNull final JLabel sourceLabel = new JLabel(Messages.getString("OpenDialog.source"));
		@NonNull final JLabel providerLabel = new JLabel(Messages.getString("OpenDialog.provider"));

		// text
		@NonNull final ListCellRenderer<Object> renderer = new DefaultListCellRenderer()
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
		};
		this.sourceTextField = new JTextField(32);
		this.providerComboBox = new JComboBox<>();
		this.providerComboBox.setEditable(true);
		this.providerComboBox.setRenderer(renderer);
		this.providerComboBox.setPreferredSize(new Dimension(300, 24));
		if (providers != null)
		{
			for (final String item : providers)
			{
				this.providerComboBox.addItem(item);
			}
		}

		// tooltips
		this.sourceTextField.setToolTipText(Messages.getString("OpenDialog.tooltip_source"));
		this.providerComboBox.setToolTipText(Messages.getString("OpenDialog.tooltip_provider"));

		// buttons
		@NonNull final JButton sourceBrowseButton = new JButton(Messages.getString("OpenDialog.browse"));
		@NonNull final JButton providerAddButton = new JButton(Messages.getString("OpenDialog.add"));
		@NonNull final JButton oKButton = new JButton(Messages.getString("OpenDialog.ok"));
		@NonNull final JButton cancelButton = new JButton(Messages.getString("OpenDialog.cancel"));

		// panels
		this.dataPanel = new JPanel();
		this.dataPanel.setLayout(new GridBagLayout());
		this.dataPanel.add(sourceLabel, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		this.dataPanel.add(providerLabel, new GridBagConstraints(0, 2, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
		this.dataPanel.add(this.sourceTextField, new GridBagConstraints(0, 1, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 10), 0, 0));
		this.dataPanel.add(this.providerComboBox, new GridBagConstraints(0, 3, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 10), 0, 0));
		this.dataPanel.add(sourceBrowseButton, new GridBagConstraints(1, 1, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));
		this.dataPanel.add(providerAddButton, new GridBagConstraints(1, 3, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));

		@NonNull final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(cancelButton);
		buttonPanel.add(oKButton);

		// action
		sourceBrowseButton.addActionListener(event -> {
			@Nullable final String url = FileDialogs.getAnyUrl(OpenDialog.this.base != null ? OpenDialog.this.base : ".");
			if (url != null && !url.isEmpty())
			{
				OpenDialog.this.sourceTextField.setText(url);
			}
		});
		providerAddButton.addActionListener(event -> {
			final String provider = ask(Messages.getString("OpenDialog.prompt_provider"));
			if (provider != null && !provider.isEmpty())
			{
				OpenDialog.this.providerComboBox.addItem(provider);
				OpenDialog.this.providerComboBox.getEditor().setItem(provider);
			}
		});
		oKButton.addActionListener(event -> {
			OpenDialog.this.ok = true;
			setVisible(false);
		});
		cancelButton.addActionListener(event -> setVisible(false));

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

	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			this.ok = false;

			// set components to values
			this.sourceTextField.setText(this.source);
			this.providerComboBox.addItem(this.provider);
			this.providerComboBox.getEditor().setItem(this.provider);

			pack();
			Utils.center(this);
		}
		else
		{
			if (this.ok)
			{
				// update values from components
				this.source = this.sourceTextField.getText();
				this.provider = (String) this.providerComboBox.getEditor().getItem();
			}
		}
		super.setVisible(flag);
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

	/**
	 * Inform dialog
	 *
	 * @param message message
	 */
	protected void inform(@NonNull final String message)
	{
		@NonNull final String[] lines = message.split("\n");
		JOptionPane.showMessageDialog(null, lines, Messages.getString("OpenDialog.app"), JOptionPane.WARNING_MESSAGE);
	}
}
