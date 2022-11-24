/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator;

import java.awt.*;
import java.util.Map;
import java.util.SortedSet;

import treebolic.generator.model.ModelUtils;

/**
 * Link list dialog
 *
 * @author Bernard Bou
 */
public class LinkListDialog extends ReferenceListDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 *
	 * @param controller controller
	 */
	public LinkListDialog(final Controller controller)
	{
		super(controller);
		setTitle(Messages.getString("LinkListDialog.title"));
		this.label.setText(Messages.getString("LinkListDialog.label"));
		this.scrollPane.setPreferredSize(new Dimension(300, 120));
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.ReferenceListDialog#update()
	 */
	@Override
	protected void update()
	{
		final Map<String, SortedSet<String>> linkToLocationMap = ModelUtils.getLinkMap(this.controller.getModel());
		setModel(linkToLocationMap);
		super.update();
	}
}
