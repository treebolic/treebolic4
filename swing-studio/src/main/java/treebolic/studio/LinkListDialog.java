/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio;

import java.awt.*;
import java.util.Map;
import java.util.SortedSet;

import treebolic.annotations.NonNull;
import treebolic.studio.model.ModelUtils;

/**
 * Link list dialog
 *
 * @author Bernard Bou
 */
public class LinkListDialog extends ReferenceListDialog
{
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

	@Override
	protected void update()
	{
		assert this.controller.getModel() != null;
		@NonNull final Map<String, SortedSet<String>> linkToLocationMap = ModelUtils.getLinkMap(this.controller.getModel());
		setModel(linkToLocationMap);
		super.update();
	}
}
