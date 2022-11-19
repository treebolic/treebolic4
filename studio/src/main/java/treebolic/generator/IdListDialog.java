/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator;

import java.awt.Dimension;
import java.util.Map;
import java.util.SortedSet;

import treebolic.generator.model.ModelUtils;

/**
 * Link list dialog
 *
 * @author Bernard Bou
 */
public class IdListDialog extends ReferenceListDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public IdListDialog(final Controller controller)
	{
		super(controller);
		setTitle(Messages.getString("IdListDialog.title")); 
		this.label.setText(Messages.getString("IdListDialog.label")); 
		this.scrollPane.setPreferredSize(new Dimension(300, 120));
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.generator.ReferenceListDialog#update()
	 */
	@Override
	protected void update()
	{
		final Map<String, SortedSet<String>> idToLocationMap = ModelUtils.getIdMap(this.controller.getModel());
		setModel(idToLocationMap);
		super.update();
	}
}
