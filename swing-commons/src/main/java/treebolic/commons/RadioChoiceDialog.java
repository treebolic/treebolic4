/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import treebolic.annotations.NonNull;

/**
 * Import dialog
 *
 * @author Bernard Bou
 */
public class RadioChoiceDialog extends JDialog implements ActionListener
{
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
	 * Buttons
	 */
	@NonNull
	protected final JRadioButton[] buttons;

	/**
	 * Data panel
	 */
	@NonNull
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
	public RadioChoiceDialog(final String value0, @NonNull final String[] values, final String[] labels, final String title, final String label)
	{
		super();
		this.value = value0;

		setTitle(title);
		setResizable(true);

		// label
		@NonNull final JLabel headerLabel = new JLabel();
		headerLabel.setText(label);

		// value buttons
		@NonNull final ButtonGroup group = new ButtonGroup();
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
		@NonNull final JButton oKButton = new JButton(Messages.getString("RadioChoiceDialog.ok"));
		@NonNull final JButton cancelButton = new JButton(Messages.getString("RadioChoiceDialog.cancel"));

		// buttons panel
		this.dataPanel = new JPanel();
		this.dataPanel.setLayout(new GridBagLayout());
		this.dataPanel.add(headerLabel, new GridBagConstraints(0, 1, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 0, 20), 0, 20));
		for (int i = 0; i < values.length; i++)
		{
			this.dataPanel.add(this.buttons[i], new GridBagConstraints(0, 2 + i, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 20, 0, 20), 0, 0));
		}

		// command panel
		@NonNull final JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout());
		commandPanel.add(cancelButton);
		commandPanel.add(oKButton);

		oKButton.addActionListener(event -> {ok =true; setVisible(false);});
		cancelButton.addActionListener(event -> setVisible(false));

		// assemble
		@NonNull final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(this.dataPanel);
		panel.add(commandPanel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		setContentPane(panel);
	}

	@Override
	public void actionPerformed(@NonNull ActionEvent e)
	{
		this.value = e.getActionCommand();
	}

	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			this.ok = false;

			pack();
			Utils.center(this);
		}
		super.setVisible(flag);
	}
}
