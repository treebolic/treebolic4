/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;
import java.util.Collection;

import javax.swing.*;

/**
 * Import dialog
 *
 * @author Bernard Bou
 */
public class ChoiceDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	// V A L U E S

	/**
	 * Value
	 */
	public String value;

	/**
	 * Ok result
	 */
	public boolean ok;

	// C O M P O N E N T S

	/**
	 * Combo
	 */
	protected final JComboBox<String> comboBox;

	/**
	 * Data panel
	 */
	protected final JPanel dataPanel;

	/**
	 * Constructor
	 *
	 * @param value           initial value
	 * @param values          range of values
	 * @param title           title
	 * @param headerLabelText label
	 * @param canAdd          if value can be added
	 */
	public ChoiceDialog(final String value, final Collection<String> values, final String title, final String headerLabelText, final boolean canAdd)
	{
		super();
		this.value = value;

		setTitle(title);
		setResizable(true);

		// images
		@SuppressWarnings("ConstantConditions") final Icon icon = new ImageIcon(ChoiceDialog.class.getResource("images/open.png"));
		final JLabel headerLabel = new JLabel();
		headerLabel.setIcon(icon);
		headerLabel.setVerticalTextPosition(SwingConstants.TOP);
		headerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		headerLabel.setText(headerLabelText);
		headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// text
		final ListCellRenderer<Object> renderer = new DefaultListCellRenderer()
		{
			private static final long serialVersionUID = -2940683342675209960L;

			/*
			 * (non-Javadoc)
			 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
			 */
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
		this.comboBox = new JComboBox<>();
		this.comboBox.setEditable(true);
		this.comboBox.setRenderer(renderer);
		this.comboBox.setPreferredSize(new Dimension(300, 24));
		if (values != null)
		{
			for (final String item : values)
			{
				this.comboBox.addItem(item);
			}
		}

		// buttons
		final JButton oKButton = new JButton(Messages.getString("ChoiceDialog.ok"));
		final JButton cancelButton = new JButton(Messages.getString("ChoiceDialog.cancel"));

		// panels
		this.dataPanel = new JPanel();
		this.dataPanel.setLayout(new GridBagLayout());
		this.dataPanel.add(this.comboBox, new GridBagConstraints(0, 0, 1, 1, 1., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 10), 0, 0));

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(cancelButton);
		buttonPanel.add(oKButton);

		oKButton.addActionListener(event -> {
			ChoiceDialog.this.ok = true;
			setVisible(false);
		});
		cancelButton.addActionListener(event -> setVisible(false));

		if (canAdd)
		{
			final JButton addButton = new JButton(Messages.getString("ChoiceDialog.add"));

			// action
			addButton.addActionListener(event -> {
				final String value1 = ask(Messages.getString("ChoiceDialog.addprompt"));
				if (value1 != null && !value1.isEmpty())
				{
					ChoiceDialog.this.comboBox.addItem(value1);
					ChoiceDialog.this.comboBox.getEditor().setItem(value1);
				}
			});
			this.dataPanel.add(addButton, new GridBagConstraints(1, 3, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 20), 0, 0));
		}

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

	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			this.ok = false;

			// read properties into components
			if (this.value != null && this.value.isEmpty())
			{
				this.comboBox.addItem(this.value);
				this.comboBox.getEditor().setItem(this.value);
			}

			pack();
			Utils.center(this);
		}
		else
		{
			if (this.ok)
			{
				// update properties from components
				this.value = (String) this.comboBox.getEditor().getItem();
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
	protected String ask(final String message)
	{
		final String[] lines = message.split("\n");
		return JOptionPane.showInputDialog(null, lines);
	}
}
