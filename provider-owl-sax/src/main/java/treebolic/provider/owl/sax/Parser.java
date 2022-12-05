/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.*;

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

		private static final String ID = "rdf:ID";
		private static final String ABOUT = "rdf:about";
		private static final String RESOURCE = "rdf:resource";

		private Map<String, Ontology.Class> classes = new HashMap<>();

		private Map<String, Ontology.Thing> things = new HashMap<>();

		private Map<String, Ontology.Property> properties = new HashMap<>();

		private Stack<Ontology.Class> classStack = new Stack<>();

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

		private String getIri(String uri, String localName, String qName, Attributes attributes)
		{
			if (uri != null && !uri.isEmpty())
			{
				System.err.println(uri);
			}

			String id = attributes.getValue(ID);
			if (id != null)
			{
				return '#' + id;
			}
			String about = attributes.getValue(ABOUT);
			if (about != null)
			{
				return about;
			}
			return null;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			switch (qName)
			{
				case CLASS:
				{
					Ontology.Class clazz = null;
					String iri = getIri(uri, localName, qName, attributes);
					if (iri != null)
					{
						clazz = new Ontology.Class(iri);
						classes.put(iri, clazz);
					}
					classStack.push(clazz);
					break;
				}

				case THING:
				{
					String iri = attributes.getValue(ABOUT);
					if (iri != null)
					{
						thing = new Ontology.Thing(iri);
						things.put(iri, thing);
					}
					break;
				}

				case PROPERTY:
				{
					String iri = attributes.getValue(ABOUT);
					if (iri != null)
					{
						property = new Ontology.Property(iri);
						properties.put(iri, property);
					}
					break;
				}

				case SUBCLASSOF:
				{
					Ontology.Class clazz = classStack.peek();
					if (clazz != null)
					{
						String iri = attributes.getValue(RESOURCE);
						if (iri != null)
						{
							clazz._superclasses.add(iri);
						}
					}
					break;
				}

				case DOMAIN:
				{
					if (property != null)
					{
						String iri = attributes.getValue(RESOURCE);
						if (iri != null)
						{
							property._domains.add(iri);
						}
					}
					break;
				}

				case RANGE:
				{
					if (property != null)
					{
						String iri = attributes.getValue(RESOURCE);
						if (iri != null)
						{
							property._ranges.add(iri);
						}
					}
					break;
				}

				case TYPE:
				{
					if (thing != null)
					{
						String iri = attributes.getValue(RESOURCE);
						if (iri != null)
						{
							thing._types.add(iri);
						}
					}
					break;
				}

				case SUBPROPERTYOF:
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
					classStack.pop();
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
