/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.propertyview;

import java.util.EventListener;

/**
 * Select listener interface
 *
 * @author Bernard Bou
 */
public interface SelectListener extends EventListener
{
	/**
	 * Select event listener
	 *
	 * @param object
	 *        parameter
	 */
	void onSelected(Object object);
}
