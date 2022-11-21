/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.glue.component;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Language dependency, bundle reader
 *
 * @author Bernard Bou
 */
class Messages
{
	private static final String BUNDLE_NAME = "treebolic.glue.component.messages";

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

	public static String[] getStrings(final String... keys)
	{
		String[] strings = new String[keys.length];
		int i = 0;
		for (String key : keys)
		{
			try
			{
				strings[i] = Messages.RESOURCE_BUNDLE.getString(key);
			}
			catch (final MissingResourceException e)
			{
				strings[i] = '!' + key + '!';
			}
			i++;
		}
		return strings;
	}
}
