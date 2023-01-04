/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio;

import java.awt.*;
import java.net.URL;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Toolbar for tree view
 *
 * @author Bernard Bou
 */
public class TreeToolbar extends JToolBar
{
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
	public TreeToolbar()
	{
		super(TreeToolbar.isVertical ? SwingConstants.VERTICAL : SwingConstants.HORIZONTAL);
		setFloatable(true);
		setPreferredSize(new Dimension(36, 20 + 4 * 36));
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		@NonNull final JButton newElementButton = makeButton(Messages.getString("TreeToolbar.new"), Messages.getString("TreeToolbar.tooltip_new"), "new.png");
		newElementButton.addActionListener(e -> TreeToolbar.this.controller.execute(Controller.Code.NEWELEMENT, 0));

		@NonNull final JButton deleteElementButton = makeButton(Messages.getString("TreeToolbar.remove"), Messages.getString("TreeToolbar.tooltip_remove"), "delete.png");
		deleteElementButton.addActionListener(e -> TreeToolbar.this.controller.execute(Controller.Code.REMOVEELEMENT, 0));
		this.add(newElementButton);
		this.add(deleteElementButton);

		@NonNull final JButton expandButton = makeButton(Messages.getString("TreeToolbar.expand"), Messages.getString("TreeToolbar.tooltip_expand"), "expand.png");
		expandButton.addActionListener(e -> TreeToolbar.this.controller.execute(Controller.Code.EXPANDTREE, 0));
		@NonNull final JButton collapseButton = makeButton(Messages.getString("TreeToolbar.collapse"), Messages.getString("TreeToolbar.tooltip_collapse"), "collapse.png");
		collapseButton.addActionListener(e -> TreeToolbar.this.controller.execute(Controller.Code.COLLAPSETREE, 0));
		this.add(newElementButton);
		this.add(deleteElementButton);
		this.add(expandButton);
		this.add(collapseButton);

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
		@Nullable final URL imageUrl = TreeToolbar.class.getResource("images/" + image);
		assert imageUrl != null;
		button.setIcon(new ImageIcon(imageUrl));
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
