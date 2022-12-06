/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.propertyview;

import treebolic.annotations.NonNull;
import treebolic.model.Utils;

/**
 * Wrapper around arry of floats
 */
public class Floats
{
	/**
	 * Float array
	 */
	public final float[] floats;

	/**
	 * Constructor
	 *
	 * @param floats0 array of floats
	 */
	public Floats(final float[] floats0)
	{
		super();
		this.floats = floats0;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@NonNull
	@Override
	public String toString()
	{
		return Utils.floatsToString(this.floats);
	}
}
