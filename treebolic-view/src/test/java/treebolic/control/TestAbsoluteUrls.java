/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.control;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test relative to absolute replacement
 */
public class TestAbsoluteUrls
{
	/**
	 * Test if replacement words with single quotes
	 *
	 * @throws MalformedURLException malformed URL exception
	 */
	@Test
	public void testReplaceQuote1() throws MalformedURLException
	{
		URL base = new URL("file:/somewhere/");
		String input = //
				"context" + //
						"<img src='image1'>" + //
						"<img src='image2'>" + //
						"<img src='image3'>" + //
						"<img src='http://someserver/image3'>" + //
						"<img src='image1'>" + //
						"<img src='image2'>" + //
						"<img src='image3'>" + //
						"context";
		String expected = //
				"context" +//
						"<img src='file:/somewhere/image1'>" + //
						"<img src='file:/somewhere/image2'>" + //
						"<img src='file:/somewhere/image3'>" + //
						"<img src='http://someserver/image3'>" + //
						"<img src='file:/somewhere/image1'>" + //
						"<img src='file:/somewhere/image2'>" + //
						"<img src='file:/somewhere/image3'>" + //
						"context";

		String actual = Controller.tweakImageSrcs(input, base);
		System.out.println(input);
		System.out.println(actual);
		assertEquals(expected, actual);
	}

	/**
	 * Test if replacement words with double quotes
	 *
	 * @throws MalformedURLException malformed URL exception
	 */
	@Test
	public void testReplaceQuote2() throws MalformedURLException
	{
		URL base = new URL("file:/somewhere/");
		String input = //
				"context" + //
						"<img src=\"image1\">" + //
						"<img src=\"image2\">" + //
						"<img src=\"image3\">" + //
						"<img src=\"http://someserver/image3\">" + //
						"<img src=\"image1\">" + //
						"<img src=\"image2\">" + //
						"<img src=\"image3\">" + //
						"context";
		String expected = //
				"context" +//
						"<img src=\"file:/somewhere/image1\">" + //
						"<img src=\"file:/somewhere/image2\">" + //
						"<img src=\"file:/somewhere/image3\">" + //
						"<img src=\"http://someserver/image3\">" + //
						"<img src=\"file:/somewhere/image1\">" + //
						"<img src=\"file:/somewhere/image2\">" + //
						"<img src=\"file:/somewhere/image3\">" + //
						"context";

		String actual = Controller.tweakImageSrcs(input, base);
		System.out.println(input);
		System.out.println(actual);
		assertEquals(expected, actual);
	}
}
