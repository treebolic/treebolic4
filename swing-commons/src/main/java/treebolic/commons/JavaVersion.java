/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

import treebolic.annotations.NonNull;

/**
 * Java version
 *
 * @author Bernard Bou
 */
public class JavaVersion
{
	/**
	 * The names of properties
	 */
	static private final String[] names = new String[]{"java.version", "java.vendor", "java.vendor.url", "java.specification.name", "java.specification.version", "java.specification.vendor", "java.vm.name", "java.vm.version", "java.vm.vendor", "java.vm.specification.name", "java.vm.specification.version", "java.vm.specification.vendor"};

	/**
	 * Get property values
	 *
	 * @return string array of name=value pairs
	 */
	@NonNull
	static public String[] getJavaProps()
	{
		@NonNull final String[] result = new String[JavaVersion.names.length];

		String prop;
		for (int i = 0; i < JavaVersion.names.length; i++)
		{
			prop = JavaVersion.names[i] + " : ";
			try
			{
				prop += System.getProperty(JavaVersion.names[i]);
			}
			catch (final SecurityException e)
			{
				prop += "<protected>";
			}
			catch (final Exception e)
			{
				prop += "<>";
			}
			result[i] = prop;
		}
		return result;
	}

	/**
	 * Get property values as string
	 *
	 * @return s string
	 */
	@NonNull
	static public String getJavaPropsString()
	{
		@NonNull final String[] strings = JavaVersion.getJavaProps();
		final StringBuilder buffer = new StringBuilder();
		for (final String string : strings)
		{
			buffer.append(string);
			buffer.append("\n");
		}
		return buffer.toString();
	}

	/**
	 * Java version
	 *
	 * @return float for java version
	 */
	public static float getJavaVersion()
	{
		// String version = Runtime.class.getPackage().getImplementationVersion();
		final String version = System.getProperty("java.version");
		int pos = 0, count = 0;
		for (; pos < version.length() && count < 2; pos++)
		{
			if (version.charAt(pos) == '.')
			{
				count++;
			}
		}
		--pos;
		try
		{
			return Float.parseFloat(version.substring(0, pos));
		}
		catch (final NumberFormatException e)
		{
			//
		}
		return -1;
	}

	/**
	 * Main
	 *
	 * @param args arguments
	 */
	static public void main(final String[] args)
	{
		@NonNull final String[] props = JavaVersion.getJavaProps();
		for (final String prop : props)
		{
			System.out.println(prop);
		}
	}
}
