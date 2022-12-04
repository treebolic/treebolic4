/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Parser
{
	public static class SaxHandler extends DefaultHandler
	{

		private static final String CLASS = "owl:Class";
		private static final String THING = "owl:Thing";
		private static final String PROPERTY = "owl:ObjectProperty";

		private static final String COMMENT = "rdfs:comment";

		private static final String DESCRIPTION = "rdf:Description";

		private static final String SUBCLASSOF = "rdfs:subClassOf";

		private static final String SUBPROPERTYOF = "rdfs:subPropertyOf";

		private static final String DOMAIN = "rdfs:domain";

		private static final String RANGE = "rdfs:range";

		private static final String INVERSE = "owl:inverseOf";

		private static final String EQUIVALENT = "owl:equivalentClass";

		private static final String TYPE = "rdf:type";

		private static final String ABOUT = "rdf:about";

		private static final String RESOURCE = "rdf:resource";

		private Map<String, Ontology.Class> classes = new HashMap<>();

		private Map<String, Ontology.Thing> things = new HashMap<>();

		private Map<String, Ontology.Property> properties = new HashMap<>();

		private Ontology.Class clazz = null;

		private Ontology.Thing thing = null;

		private Ontology.Property property = null;

		private StringBuilder textSb = null;

		@Override
		public void characters(char[] ch, int start, int length)
		{
			if (textSb == null)
			{
				textSb = new StringBuilder();
			}
			else
			{
				textSb.append(ch, start, length);
			}
		}

		@Override
		public void startDocument()
		{
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			switch (qName)
			{
				case CLASS:
				{
					String id = attributes.getValue(ABOUT);
					clazz = new Ontology.Class(id);
					classes.put(id, clazz);
					break;
				}

				case THING:
				{
					String id = attributes.getValue(ABOUT);
					thing = new Ontology.Thing(id);
					things.put(id, thing);
					break;
				}

				case PROPERTY:
				{
					String id = attributes.getValue(ABOUT);
					property = new Ontology.Property(id);
					properties.put(id, property);
					break;
				}

				case SUBCLASSOF:
				{
					if (clazz != null)
					{
						String id = attributes.getValue(RESOURCE);
						clazz.superclasses.add(id);
					}
					break;
				}

				case SUBPROPERTYOF:
				case DOMAIN:
				case RANGE:
				case INVERSE:
				case EQUIVALENT:
				{
					break;
				}

				case "owl:DatatypeProperty":
				case "owl:AnnotationProperty":
				case "owl:disjointObjectProperties":
				case "rdfs:label":
				case "rdfs:seeAlso":
				case "rdfs:isDefinedBy":
				case "owl:unionOf":
				case "owl:intersectionOf":
				case "owl:disjointWith":
				case "owl:Restriction":
				case "owl:onProperty":
				case "owl:distinctMembers":
				case "owl:someValuesFrom":
				case "owl:allValuesFrom":
				{
					break;
				}

				case COMMENT:
				{
					break;
				}

				case DESCRIPTION:
				{
					break;
				}

				case TYPE:
				{
					break;
				}

				default:
					if (qName.startsWith("rdf:") || qName.startsWith("rdfs:") || qName.startsWith("owl:"))
					{
						System.err.printf(">%s q=%s u=%s%n", localName, qName, uri);
					}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
		{
			// System.err.printf("<%s q=%s u=%s%n", localName, qName, uri);

			String text = textSb.toString();
			if (!text.isEmpty())
			{
				text = text.replace('\n', ' ');
				text = text.trim();
				if (!text.isEmpty())
				{
					//
				}
			}
			textSb.setLength(0);

			switch (qName)
			{
				case CLASS:
					clazz = null;
					break;

				case PROPERTY:
					property = null;
					break;

				case THING:
					thing = null;
					break;
			}
		}

		public Ontology getResult()
		{
			classes.values().forEach(c -> c.superclasses.forEach(sc -> classes.get(sc).subclasses.add(c.id)));
			return new Ontology(classes, things, properties);
		}
	}

	public static Ontology make(String uri) throws ParserConfigurationException, SAXException, IOException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		SAXParser saxParser = factory.newSAXParser();

		SaxHandler handler = new SaxHandler();
		saxParser.parse(uri, handler);
		return handler.getResult();
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		Ontology ontology = make(args[0]);

		System.out.println("classes:" + ontology.classes.size());
		System.out.println("things: " + ontology.things.size());
		System.out.println("properties: " + ontology.properties.size());

		System.out.println("classes: " + ontology.classes.keySet());
		System.out.println("things: " + ontology.things.keySet());
		System.out.println("properties: " + ontology.properties.keySet());
	}
}
