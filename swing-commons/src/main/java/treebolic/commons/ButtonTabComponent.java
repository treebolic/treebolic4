/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.commons;

import java.awt.*;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Button tab component
 */
public class ButtonTabComponent extends JPanel
{
	/**
	 * Default icon
	 */
	@SuppressWarnings("DataFlowIssue")
	static private final Icon defaultIcon = new ImageIcon(ButtonTabComponent.class.getResource("images/close_default.png"));

	/**
	 * Rollover icon
	 */
	@SuppressWarnings("DataFlowIssue")
	static private final Icon rolloverIcon = new ImageIcon(ButtonTabComponent.class.getResource("images/close_rollover.png"));

	/**
	 * Pressed icon
	 */
	@SuppressWarnings("DataFlowIssue")
	static private final Icon pressedIcon = new ImageIcon(ButtonTabComponent.class.getResource("images/close_pressed.png"));

	/**
	 * Reference to tabbed pane
	 */
	@NonNull
	private final JTabbedPane tabbedPane;

	/**
	 * Constructor
	 *
	 * @param pane tabbed pane
	 */
	public ButtonTabComponent(@Nullable final JTabbedPane pane)
	{
		// unset default FlowLayout' gaps
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));

		if (pane == null)
		{
			throw new NullPointerException("TabbedPane is null");
		}
		this.tabbedPane = pane;

		// L A B E L

		// make label read titles from JTabbedPane
		@NonNull final JLabel label = new JLabel()
		{
			@Nullable
			@Override
			public String getText()
			{
				final int index = pane.indexOfTabComponent(ButtonTabComponent.this);
				if (index != -1)
				{
					return pane.getTitleAt(index);
				}
				return null;
			}
		};

		// add more space between the label and the button
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

		// B U T T O N

		@NonNull final JButton button = new JButton();

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
