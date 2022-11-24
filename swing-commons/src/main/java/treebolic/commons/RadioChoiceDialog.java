/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Import dialog
 *
 * @author Bernard Bou
 */
public class RadioChoiceDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;

	// V A L U E S

	/**
	 * Value
	 */
	public String value;

	// C O M P O N E N T S

	/**
	 * Buttons
	 */
	protected final JRadioButton[] buttons;

	/**
	 * Data panel
	 */
	protected final JPanel dataPanel;

	/**
	 * Constructor
	 *
	 * @param value0 initial value
	 * @param values range of values
	 * @param labels labels
	 * @param title  title
	 * @param label  label
	 */
	public RadioChoiceDialog(final String value0, final String[] values, final String[] labels, final String title, final String label)
	{
		super();
		this.value = value0;

		setTitle(title);
		setResizable(true);

		// label
		final JLabel headerLabel = new JLabel();
		headerLabel.setText(label);

		// value buttons
		final ButtonGroup group = new ButtonGroup();
		this.buttons = new JRadioButton[values.length];
		for (int i = 0; i < values.length; i++)
		{
			this.buttons[i] = new JRadioButton(labels[i]);
			this.buttons[i].setActionCommand(values[i]);
			this.buttons[i].addActionListener(this);
			this.buttons[i].setSelected(values[i].equals(value0));
			group.add(this.buttons[i]);
		}

		// buttons
		final JButton oKButton = new JButton(Messages.getString("RadioChoiceDialog.ok"));
		final JButton cancelButton = new JButton(Messages.getString("RadioChoiceDialog.cancel"));

		// buttons panel
		this.dataPanel = new JPanel();
		this.dataPanel.setLayout(new GridBagLayout());
		this.dataPanel.add(headerLabel, new GridBagConstraints(0, 1, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 0, 20), 0, 20));
		for (int i = 0; i < values.length; i++)
		{
			this.dataPanel.add(this.buttons[i], new GridBagConstraints(0, 2 + i, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));
		}

		// command panel
		final JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout());
		commandPanel.add(cancelButton);
		commandPanel.add(oKButton);

		oKButton.addActionListener(event -> setVisible(false));
		cancelButton.addActionListener(event -> setVisible(false));

		// assemble
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(this.dataPanel);
		panel.add(commandPanel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		setContentPane(panel);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.value = e.getActionCommand();
		// System.err.println(this.value);
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

			pack();
			Utils.center(this);
		}
		super.setVisible(flag);
	}
}
