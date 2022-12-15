/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.model;

/**
 * Mount task (protracted)
 *
 * @author Bernard Bou
 */
public class MountTask
{
	/**
	 * Mount point
	 */
	public final MountPoint.Mounting mountPoint;

	/**
	 * Node to mount graph at
	 */
	public final INode mountingNode;

	/**
	 * Constructor
	 *
	 * @param mountPoint   mount point
	 * @param mountingNode Node to mount graph at
	 */
	public MountTask(final MountPoint.Mounting mountPoint, final INode mountingNode)
	{
		this.mountPoint = mountPoint;
		this.mountingNode = mountingNode;
	}
}
