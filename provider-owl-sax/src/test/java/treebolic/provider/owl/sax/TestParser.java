/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.sax;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

public class TestParser
{
	@Test
	public void testModel() throws IOException, ParserConfigurationException, SAXException
	{
		final String source = System.getProperty("SOURCE");
		final String base = System.getProperty("BASE");
		final URI uri = new File(base, source).toURI();
		Ontology ontology = Parser.make(uri.toString());
		tree(ontology);

		// System.out.println("classes:" + ontology.classes.size());
		// System.out.println("things: " + ontology.things.size());
		// System.out.println("properties: " + ontology.properties.size());
		// System.out.println("classes: " + ontology.classes.keySet());
		// System.out.println("things: " + ontology.things.keySet());
		// System.out.println("properties: " + ontology.properties.keySet());
	}

	void tree(Ontology ontology)
	{
		ontology.getTopClasses().sorted().forEach(c -> {
			visitClass(c, 0);
		});
	}

	void visitClass(Ontology.Class c, int level)
	{
		System.out.println(repeat('\t', level) + c);

		if (level <3 && c.subclasses != null)
		{
			c.subclasses.forEach(sc -> visitClass(sc, level + 1));
		}
	}

	String repeat(char c, int n)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < n; i++)
		{
			sb.append(c);
		}
		return sb.toString();
	}
}
