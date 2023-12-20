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
 * Mount list dialog
 *
 * @author Bernard Bou
 */
public class MountListDialog extends ReferenceListDialog
{
	/**
	 * Constructor
	 *
	 * @param controller controller
	 */
	public MountListDialog(final Controller controller)
	{
		super(controller);
		setTitle(Messages.getString("MountListDialog.title"));
		this.label.setText(Messages.getString("MountListDialog.label"));
		this.scrollPane.setPreferredSize(new Dimension(300, 120));
	}

	@Override
	protected void update()
	{
		assert this.controller.getModel() != null;
		@NonNull final Map<String, SortedSet<String>> mountToLocationMap = ModelUtils.getMountMap(this.controller.getModel());
		setModel(mountToLocationMap);
		super.update();
	}
}
