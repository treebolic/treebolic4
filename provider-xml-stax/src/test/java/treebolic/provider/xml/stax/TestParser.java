/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.xml.stax;

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
		Model model1 = treebolic.provider.xml.dom.Parser.makeModel(TEST_FILE);
		String dump1 = ModelDump.toString(model1);
		Model model2 = treebolic.provider.xml.stax.Parser.makeModel(TEST_FILE);
		String dump2 = ModelDump.toString(model2);
		assertEquals(dump1, dump2);
		System.out.println("DOM");
		System.out.println(dump1);
		System.out.println("STAX");
		System.out.println(dump2);
	}
}
