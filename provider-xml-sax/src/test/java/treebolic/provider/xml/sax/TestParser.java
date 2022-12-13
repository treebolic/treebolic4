/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.xml.sax;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import treebolic.model.Model;
import treebolic.model.ModelDump;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestParser
{
	private static String TEST_FILE = System.getProperty("SOURCE");

	@Test
	void testParsers() throws ParserConfigurationException, IOException, SAXException, XMLStreamException
	{
		long start1 = System.currentTimeMillis();
		Model model1 = treebolic.provider.xml.dom.Parser.makeModel(TEST_FILE);
		long end1 = System.currentTimeMillis();
		String dump1 = ModelDump.toString(model1);
		long start2 = System.currentTimeMillis();
		Model model2 = treebolic.provider.xml.sax.Parser.makeModel(TEST_FILE);
		long end2 = System.currentTimeMillis();
		String dump2 = ModelDump.toString(model2);
		System.out.println("DOM " + (end1 - start1) + " ms");
		// System.out.println(dump1);
		System.out.println("SAX " + (end2 - start2) + " ms");
		// System.out.println(dump2);
		assertEquals(dump1, dump2);
	}
}
