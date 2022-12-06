/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio;

import java.awt.*;

import javax.swing.*;

import treebolic.annotations.NonNull;

/**
 * Toolbar
 *
 * @author Bernard Bou
 */
public class Toolbar extends JToolBar
{
	private static final long serialVersionUID = 1L;

	// D A T A

	/**
	 * Controller (command sink)
	 */
	private Controller controller;

	/**
	 * Vertical layout
	 */
	static public final boolean isVertical = true;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public Toolbar()
	{
		super(Toolbar.isVertical ? SwingConstants.VERTICAL : SwingConstants.HORIZONTAL);
		setFloatable(true);
		setPreferredSize(new Dimension(36, 20 + 8 * 36));
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		@NonNull final JButton openButton = makeButton(Messages.getString("Toolbar.open"), Messages.getString("Toolbar.tooltip_open"), "open.png");
		openButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.OPEN, 0));

		@NonNull final JButton openHttpButton = makeButton(Messages.getString("Toolbar.open_url"), Messages.getString("Toolbar.tooltip_open_url"), "openurl.png");
		openHttpButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.OPENURL, 0));

		@NonNull final JButton newButton = makeButton(Messages.getString("Toolbar.new"), Messages.getString("Toolbar.tooltip_new"), "newdoc.png");
		newButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.NEW, 0));

		@NonNull final JButton saveButton = makeButton(Messages.getString("Toolbar.save"), Messages.getString("Toolbar.tooltip_save"), "save.png");
		saveButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.SAVE, 0));

		@NonNull final JButton helpButton = makeButton(Messages.getString("Toolbar.help"), Messages.getString("Toolbar.tooltip_help"), "help.png");
		helpButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.HELP, 0));

		@NonNull final JButton updateNodeButton = makeButton(Messages.getString("Toolbar.update"), Messages.getString("Toolbar.tooltip_update"), "refresh.png");
		updateNodeButton.addActionListener(e -> Toolbar.this.controller.execute(Controller.Code.UPDATE, 0));

		this.add(openButton);
		this.add(openHttpButton);
		this.add(newButton);
		this.add(saveButton);
		this.add(updateNodeButton);
		this.add(helpButton);
	}

	/**
	 * Make button
	 *
	 * @param ignoredText text
	 * @param tooltip     tooltip
	 * @param image       image
	 * @return button
	 */
	@NonNull
	private JButton makeButton(final String ignoredText, final String tooltip, final String image)
	{
		@NonNull final JButton button = new JButton();
		button.setToolTipText(tooltip);
		//noinspection ConstantConditions
		button.setIcon(new ImageIcon(Toolbar.class.getResource("images/" + image)));
		return button;
	}

	/**
	 * Set controller
	 *
	 * @param controller controller
	 */
	public void setController(final Controller controller)
	{
		this.controller = controller;
	}
}
