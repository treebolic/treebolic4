/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator;

import java.awt.Dimension;
import java.util.Map;
import java.util.SortedSet;

import treebolic.generator.model.ModelUtils;

/**
 * Mount list dialog
 *
 * @author Bernard Bou
 */
public class MountListDialog extends ReferenceListDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public MountListDialog(final Controller controller)
	{
		super(controller);
		setTitle(Messages.getString("MountListDialog.title")); 
		this.label.setText(Messages.getString("MountListDialog.label")); 
		this.scrollPane.setPreferredSize(new Dimension(300, 120));
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.ReferenceListDialog#update()
	 */
	@Override
	protected void update()
	{
		final Map<String, SortedSet<String>> mountToLocationMap = ModelUtils.getMountMap(this.controller.getModel());
		setModel(mountToLocationMap);
		super.update();
	}
}
