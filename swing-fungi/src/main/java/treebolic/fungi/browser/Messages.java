/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.fungi.browser;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Language dependency
 *
 * @author Bernard Bou
 */
public class Messages
{
	private static final String BUNDLE_NAME = "treebolic.fungi.browser.messages";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages()
	{
		// do nothing
	}

	/**
	 * Get message value from key
	 *
	 * @param key key
	 * @return message
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