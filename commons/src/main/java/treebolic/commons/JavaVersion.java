/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.commons;

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
	static private String[] names = new String[] { "java.version", "java.vendor", "java.vendor.url", "java.specification.name", "java.specification.version", "java.specification.vendor", "java.vm.name", "java.vm.version", "java.vm.vendor", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
			"java.vm.specification.name", "java.vm.specification.version", "java.vm.specification.vendor" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * Get property values
	 *
	 * @return string array of name=value pairs
	 */
	static public String[] getJavaProps()
	{
		final String[] result = new String[JavaVersion.names.length];

		String prop;
		for (int i = 0; i < JavaVersion.names.length; i++)
		{
			prop = JavaVersion.names[i] + " : "; //$NON-NLS-1$
			try
			{
				prop += System.getProperty(JavaVersion.names[i]);
			}
			catch (final SecurityException e)
			{
				prop += "<protected>"; //$NON-NLS-1$
			}
			catch (final Exception e)
			{
				prop += "<>"; //$NON-NLS-1$
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
	static public String getJavaPropsString()
	{
		final String[] strings = JavaVersion.getJavaProps();
		final StringBuffer buffer = new StringBuffer();
		for (final String string : strings)
		{
			buffer.append(string);
			buffer.append("\n"); //$NON-NLS-1$
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
		final String version = System.getProperty("java.version"); //$NON-NLS-1$
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
	 * @param args
	 *        arguments
	 */
	static public void main(final String[] args)
	{
		final String[] props = JavaVersion.getJavaProps();
		for (final String prop : props)
		{
			System.out.println(prop);
		}
	}
}
