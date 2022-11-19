/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.generator.tree;

import java.util.List;

import treebolic.generator.Messages;
import treebolic.model.IEdge;
import treebolic.model.Model;
import treebolic.model.Settings;

/**
 * Edges wrapper
 *
 * @author Bernard Bou
 */
public class EdgesWrapper extends SettingsWrapper
{
	/**
	 * Edge list
	 */
	public List<IEdge> edgeList;

	/**
	 * Model (as container of edges list)
	 */
	public Model model;

	/**
	 * Constructor
	 *
	 * @param edgeList
	 *        edge list
	 * @param model
	 *        model
	 * @param settings
	 *        settings
	 */
	public EdgesWrapper(final List<IEdge> edgeList, final Model model, final Settings settings)
	{
		super(settings);
		this.edgeList = edgeList;
		this.model = model;
	}
	
	@Override
	public String toString()
	{
		return Messages.getString("EdgesWrapper.label"); 
	}
}
