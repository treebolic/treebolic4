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
public class IdListDialog extends ReferenceListDialog
{
	/**
	 * Constructor
	 *
	 * @param controller controller
	 */
	public IdListDialog(final Controller controller)
	{
		super(controller);
		setTitle(Messages.getString("IdListDialog.title"));
		this.label.setText(Messages.getString("IdListDialog.label"));
		this.scrollPane.setPreferredSize(new Dimension(300, 120));
	}

	@Override
	protected void update()
	{
		assert this.controller.getModel() != null;
		@NonNull final Map<String, SortedSet<String>> idToLocationMap = ModelUtils.getIdMap(this.controller.getModel());
		setModel(idToLocationMap);
		super.update();
	}
}
