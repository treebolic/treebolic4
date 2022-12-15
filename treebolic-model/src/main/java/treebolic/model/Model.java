/*
 * Copyright (c) 2019-2022. Bernard Bou
 */

package treebolic.model;

import java.io.Serializable;
import java.util.List;

import treebolic.annotations.Nullable;
import treebolic.glue.iface.Image;

/**
 * Data model
 *
 * @author Bernard Bou
 */
public class Model implements Serializable
{
	private static final long serialVersionUID = 4L;

	/**
	 * Tree
	 */
	@SuppressWarnings("InstanceVariableOfConcreteClass")
	public final Tree tree;

	/**
	 * Settings
	 */
	@SuppressWarnings("InstanceVariableOfConcreteClass")
	public final Settings settings;

	/**
	 * Images
	 */
	@Nullable
	public final Image[] images;

	/**
	 * Immediate mount tasks
	 */
	@Nullable
	public final List<MountTask> mountTasks;

	/**
	 * Constructor
	 *
	 * @param tree     tree
	 * @param settings settings
	 */
	public Model(final Tree tree, final Settings settings)
	{
		this(tree, settings, null, null);
	}

	/**
	 * Constructor
	 *
	 * @param tree     tree
	 * @param settings settings
	 * @param images   images
	 */
	public Model(final Tree tree, final Settings settings, @Nullable final Image[] images)
	{
		this(tree, settings, images, null);
	}

	/**
	 * Constructor
	 *
	 * @param tree       tree
	 * @param settings   settings
	 * @param images     images
	 * @param mountTasks mount tasks
	 */
	public Model(final Tree tree, final Settings settings, @Nullable final Image[] images, @Nullable final List<MountTask> mountTasks)
	{
		this.tree = tree;
		this.settings = settings;
		this.images = images;
		this.mountTasks = mountTasks;
	}
}
