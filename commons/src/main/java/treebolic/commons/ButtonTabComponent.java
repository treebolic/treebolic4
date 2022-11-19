/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ButtonTabComponent extends JPanel
{
	/**
	 *
	 */
	private static final long serialVersionUID = 6199221351815232505L;

	/**
	 * Default icon
	 */
	@SuppressWarnings("ConstantConditions")
	static private final Icon defaultIcon = new ImageIcon(ButtonTabComponent.class.getResource("images/close_default.png"));

	/**
	 * Rollover icon
	 */
	@SuppressWarnings("ConstantConditions")
	static private final Icon rolloverIcon = new ImageIcon(ButtonTabComponent.class.getResource("images/close_rollover.png"));

	/**
	 * Pressed icon
	 */
	@SuppressWarnings("ConstantConditions")
	static private final Icon pressedIcon = new ImageIcon(ButtonTabComponent.class.getResource("images/close_pressed.png"));

	/**
	 * Reference to tabbed pane
	 */
	private final JTabbedPane tabbedPane;

	/**
	 * Constructor
	 *
	 * @param pane
	 *        tabbed pane
	 */
	public ButtonTabComponent(final JTabbedPane pane)
	{
		// unset default FlowLayout' gaps
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));

		if (pane == null)
			throw new NullPointerException("TabbedPane is null"); 
		this.tabbedPane = pane;

		// L A B E L

		// make label read titles from JTabbedPane
		final JLabel label = new JLabel()
		{
			/**
			 *
			 */
			private static final long serialVersionUID = -393007019999118052L;

			@Override
			public String getText()
			{
				final int index = pane.indexOfTabComponent(ButtonTabComponent.this);
				if (index != -1)
					return pane.getTitleAt(index);
				return null;
			}
		};

		// add more space between the label and the button
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

		// B U T T O N

		final JButton button = new JButton();

		// tooltip
		button.setToolTipText(Messages.getString("ButtonTabComponent.close")); 

		// transparent
		button.setContentAreaFilled(false);

		// no need to be focusable
		button.setFocusable(false);

		// border
		button.setBorder(BorderFactory.createEtchedBorder());
		button.setBorderPainted(false);

		// icon
		button.setRolloverEnabled(true);
		button.setIcon(ButtonTabComponent.defaultIcon);
		button.setRolloverIcon(ButtonTabComponent.rolloverIcon);
		button.setPressedIcon(ButtonTabComponent.pressedIcon);

		// close the proper tab by clicking the button
		button.addActionListener(event -> {
			final int index = ButtonTabComponent.this.tabbedPane.indexOfTabComponent(ButtonTabComponent.this);
			if (index != -1)
			{
				ButtonTabComponent.this.tabbedPane.remove(index);
			}
		});

		// C O M P O N E N T

		// add more space to the top of the component
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		// opaque
		setOpaque(false);

		// assemble
		add(label);
		add(button);
	}
}
