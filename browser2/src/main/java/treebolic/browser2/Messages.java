/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.browser2;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Language dependency
 * 
 * @author Bernard Bou
 */
public class Messages
{
	private static final String BUNDLE_NAME = "treebolic.browser2.messages"; 

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages()
	{
		// do nothing
	}

	/**
	 * Get localized message
	 *
	 * @param key message key
	 * @return localized message
	 */
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
