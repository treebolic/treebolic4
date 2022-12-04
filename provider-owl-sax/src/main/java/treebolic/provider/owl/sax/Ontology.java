/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.sax;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

class Ontology
{
	public Ontology(final Map<String, Class> classes, final Map<String, Thing> things, final Map<String, Property> properties)
	{
		this.classes = classes;
		this.things = things;
		this.properties = properties;
	}

	final Map<String, Class> classes;

	final Map<String, Thing> things;

	final Map<String, Property> properties;

	public Stream<Property> getProperties(final Class owlClass)
	{
		return null;
	}

	public Property getRelation(final Class owlClass)
	{
		return null;
	}

	public Class createClass(final String s)
	{
		return null;
	}

	public Stream<Class> getTopClasses()
	{
		return null;
	}

	public Stream<Thing> getInstances(final Class owlClass)
	{
		return null;
	}

	public boolean isRelation(final Class owlClass)
	{
		return false;
	}

	public boolean hasProperties(final Class owlClass)
	{
		return false;
	}

	public boolean hasInstances(final Class owlClass)
	{
		return false;
	}

	static class Resource
	{
		final String id;

		public String comment;

		public Resource(final String id)
		{
			this.id = id;
		}

		String getLocalName()
		{
			int begin = id.indexOf('#');
			if (begin != -1)
			{
				return id.substring(begin);
			}
			return id;
		}

		Set<String> annotations = new HashSet<>();

		public String getNameSpace()
		{
			return "";
		}
	}

	static class Class extends Resource
	{
		Set<String> subclasses = new HashSet<>();

		public Class(final String id)
		{
			super(id);
		}
	}

	static class Thing extends Resource
	{
		public Thing(final String id)
		{
			super(id);
		}

		Set<String> types = new HashSet<>();
	}

	static class Property extends Resource
	{
		public Property(final String id)
		{
			super(id);
		}

		Set<String> domains = new HashSet<>();

		Set<String> ranges = new HashSet<>();

		Set<String> subproperties = new HashSet<>();

		Set<String> inverses = new HashSet<>();
	}
}
