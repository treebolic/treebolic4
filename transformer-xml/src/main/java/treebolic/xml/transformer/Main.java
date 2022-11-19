/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.xml.transformer;

/**
 * @author Bernard Bou
 */
public class Main
{
	/**
	 * Main entry point for transformer
	 *
	 * @param args arguments
	 */
	public static void main(final String[] args)
	{
		try
		{
			final String sourceFilePath = args[0];
			final String resultFilePath = args[1];
			final String xsltFilePath = args[2];
			boolean outputHtml = false;
			if (args.length > 3)
			{
				final String flag = args[3];
				outputHtml = flag.equalsIgnoreCase("html");
			}
			String dtd = null;
			if (args.length > 4)
			{
				dtd = args[4];
			}

			new DomTransformer(outputHtml, dtd).fileToFile(sourceFilePath, resultFilePath, xsltFilePath);
		}
		catch (final Throwable e)
		{
			System.err.println("Usage: <source file><result file><xslt file><html|xml|text><dtd>");
			e.printStackTrace();
		}
	}
}
