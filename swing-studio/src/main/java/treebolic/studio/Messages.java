/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import treebolic.annotations.NonNull;

/**
 * Language dependency
 *
 * @author Bernard Bou
 */
public class Messages
{
	private static final String BUNDLE_NAME = "treebolic.studio.messages";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * Get localized message
	 *
	 * @param key message key
	 * @return localized message
	 */
	public static String getString(@NonNull final String key)
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
