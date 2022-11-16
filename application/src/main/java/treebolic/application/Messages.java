/**
 * Title : Treebolic
 * Description : Treebolic
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 *
 * Update : 21 juin 08
 */

package treebolic.application;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Language dependency, bundle reader
 * 
 * @author Bernard Bou
 */
public class Messages
{
	private static final String BUNDLE_NAME = "treebolic.application.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages()
	{
		// do nothing
	}

	public static String getString(final String key)
	{
		try
		{
			return Messages.RESOURCE_BUNDLE.getString(key);
		}
		catch (final MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
}
