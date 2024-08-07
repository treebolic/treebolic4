/*
 * Copyright (c) 2019-2023. Bernard Bou
 */

package treebolic.glue.iface;

import treebolic.annotations.NonNull;

/**
 * Glue interface for GraphicsCache
 *
 * @param <G> platform graphics context
 * @author Bernard Bou
 */
public interface GraphicsCache<G>
{
	/**
	 * Obtain cache graphics context
	 *
	 * @return cache graphics context
	 */
	@NonNull
	G getGraphics();

	/**
	 * Put cache to graphics context
	 *
	 * @param g graphics context
	 */
	void put(G g);
}
