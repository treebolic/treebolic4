/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.provider.owl.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * SAX parser
 */
public class Parser
{
    /**
     * SAX handler
     */
    public static class SaxHandler extends DefaultHandler
    {
        private static final String CLASS = "owl:Class";
        private static final String THING = "owl:Thing";
        private static final String PROPERTY = "owl:ObjectProperty";

        private static final String SYMMETRICPROPERTY = "owl:SymmetricProperty";
        private static final String TRANSITIVEPROPERTY = "owl:TransitiveProperty";
        private static final String FUNCTIONALPROPERTY = "owl:FunctionalProperty";

        private static final String INVERSE = "owl:inverseOf";
        private static final String EQUIVALENT = "owl:equivalentClass";

        private static final String DESCRIPTION = "rdf:Description";
        private static final String SUBCLASSOF = "rdfs:subClassOf";
        private static final String SUBPROPERTYOF = "rdfs:subPropertyOf";
        private static final String DOMAIN = "rdfs:domain";
        private static final String RANGE = "rdfs:range";
        private static final String COMMENT = "rdfs:comment";
        private static final String LABEL = "rdfs:label";

        private static final String TYPE = "rdf:type";
        private static final String ID = "rdf:ID";
        private static final String ABOUT = "rdf:about";
        private static final String RESOURCE = "rdf:resource";

        @NonNull
        private final Map<String, Ontology.Class> classes = new HashMap<>();

        @NonNull
        private final Map<String, Ontology.Thing> things = new HashMap<>();

        @NonNull
        private final Map<String, Ontology.Property> properties = new HashMap<>();

        @NonNull
        private final Stack<Ontology.Class> classStack = new Stack<>();

        @NonNull
        private final Stack<String> scopeStack = new Stack<>();

        @Nullable
        private Ontology.Thing thing = null;

        @Nullable
        private Ontology.Property property = null;

        @Nullable
        private StringBuilder textSb = null;

        @Override
        public void characters(@NonNull char[] ch, int start, int length)
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

        private String getIri(String ignoredUri, String ignoredLocalName, String ignoredQName, @NonNull Attributes attributes)
        {
            // if (uri != null && !uri.isEmpty())
            // {
            // 	System.err.println(uri);
            // }

            String id = attributes.getValue(ID);
            if (id != null)
            {
                return '#' + id;
            }
            return attributes.getValue(ABOUT);
        }

        @Override
        public void startElement(String uri, String localName, @NonNull String qName, @NonNull Attributes attributes)
        {
            switch (qName)
            {
                case CLASS:
                {
                    @Nullable Ontology.Class clazz = null;
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
                case SYMMETRICPROPERTY:
                case TRANSITIVEPROPERTY:
                case FUNCTIONALPROPERTY:
                {
                    String iri = attributes.getValue(ABOUT);
                    if (iri != null)
                    {
                        property = PROPERTY.equals(qName) ? new Ontology.Property(iri) : new Ontology.Property(iri, qName.substring(4, qName.lastIndexOf("Property")));
                        properties.put(iri, property);
                    }
                    break;
                }

                case SUBCLASSOF:
                {
                    Ontology.Class clazz = classStack.isEmpty() ? null : classStack.peek();
                    if (clazz != null)
                    {
                        String iri = attributes.getValue(RESOURCE);
                        if (iri != null)
                        {
                            assert clazz._superclasses != null;
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
                            assert property._domains != null;
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
                            assert property._ranges != null;
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
                            assert thing._types != null;
                            thing._types.add(iri);
                        }
                    }
                    break;
                }

                case SUBPROPERTYOF:
                case INVERSE:
                case EQUIVALENT:
                case COMMENT:
                case DESCRIPTION:
                case LABEL:

                case "owl:DatatypeProperty":
                case "owl:AnnotationProperty":
                case "owl:disjointObjectProperties":
                case "owl:unionOf":
                case "owl:intersectionOf":
                case "owl:disjointWith":
                case "owl:Restriction":
                case "owl:onProperty":
                case "owl:distinctMembers":
                case "owl:someValuesFrom":
                case "owl:allValuesFrom":
                case "rdfs:seeAlso":
                case "rdfs:isDefinedBy":
                {
                    break;
                }

                default:
                    if (qName.startsWith("rdf:") || qName.startsWith("rdfs:") || qName.startsWith("owl:"))
                    {
                        System.err.printf(">%s q=%s u=%s%n", localName, qName, uri);
                    }
            }
            scopeStack.push(qName);
        }

        @Override
        public void endElement(String uri, String localName, @NonNull String qName)
        {
            // System.err.printf("<%s q=%s u=%s%n", localName, qName, uri);

            assert textSb != null;
            @NonNull String text = textSb.toString();
            if (!text.isEmpty())
            {
                text = text.replace('\n', ' ');
                text = text.trim();
                // if (!text.isEmpty())
                // {
                // 	//
                // }
            }
            textSb.setLength(0);

            switch (qName)
            {
                case CLASS:
                    classStack.pop();
                    break;
                case THING:
                    thing = null;
                    break;

                case PROPERTY:
                case SYMMETRICPROPERTY:
                case TRANSITIVEPROPERTY:
                case FUNCTIONALPROPERTY:
                    property = null;
                    break;

                case COMMENT:
                {
                    String scope = penultimate(scopeStack);
                    if (THING.equals(scope) && thing != null)
                    {
                        thing.comment = text;
                    }
                    else if (PROPERTY.equals(scope) && property != null)
                    {
                        property.comment = text;
                    }
                    else if (CLASS.equals(scope) && !classStack.empty() && classStack.peek() != null)
                    {
                        classStack.peek().comment = text;
                    }
                    break;
                }

                case LABEL:
                {
                    String scope = penultimate(scopeStack);
                    if (THING.equals(scope) && thing != null)
                    {
                        thing.label = text;
                    }
                    else if (PROPERTY.equals(scope) && property != null)
                    {
                        property.label = text;
                    }
                    else if (CLASS.equals(scope) && !classStack.empty() && classStack.peek() != null)
                    {
                        classStack.peek().label = text;
                    }
                    break;
                }
            }
            scopeStack.pop();
        }

        /**
         * Retrieve result
         *
         * @return ontology
         */
        @NonNull
        public Ontology getResult()
        {
            return new Ontology(classes, things, properties);
        }
    }

    static <T> @Nullable T penultimate(@NonNull final Stack<T> stack)
    {
        int len = stack.size();
        if (len <= 1)
            return null;
        return stack.elementAt(len - 2);
    }

    /**
     * Make ontology
     *
     * @param uri uri
     * @return ontology
     * @throws ParserConfigurationException parser configuration exception
     * @throws SAXException                 sax exception
     * @throws IOException                  io exception
     */
    @NonNull
    public static Ontology make(String uri) throws ParserConfigurationException, SAXException, IOException
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        // @formatter:off
		try	{ factory.setFeature(XMLConstants.ACCESS_EXTERNAL_DTD, false);} catch(@NonNull final Exception ignored){}
		try	{ factory.setFeature(XMLConstants.ACCESS_EXTERNAL_SCHEMA, false);} catch(@NonNull final Exception ignored){}
		try	{ factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); } catch(@NonNull final Exception ignored){}
		// @formatter:on
        SAXParser saxParser = factory.newSAXParser();

        @NonNull SaxHandler handler = new SaxHandler();
        saxParser.parse(uri, handler);
        return handler.getResult();
    }

    /**
     * Main
     *
     * @param args command-line arguments
     * @throws ParserConfigurationException parser configuration exception
     * @throws SAXException                 sax exception
     * @throws IOException                  io exception
     */
    public static void main(@NonNull String[] args) throws ParserConfigurationException, SAXException, IOException
    {
        @NonNull Ontology ontology = make(args[0]);

        System.out.println("classes:" + ontology.classes.size());
        System.out.println("things: " + ontology.things.size());
        System.out.println("properties: " + ontology.properties.size());

        System.out.println("classes: " + ontology.classes.keySet());
        System.out.println("things: " + ontology.things.keySet());
        System.out.println("properties: " + ontology.properties.keySet());
    }
}
