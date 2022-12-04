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

	public Class createClass(final String id)
	{
		return new Class(id);
	}

	public Stream<Class> getTopClasses()
	{
		return classes.values().stream().filter(clazz -> clazz.superclasses.isEmpty());
	}

	public Stream<Thing> getInstances(final Class owlClass)
	{
		return things.values().stream().filter(thing -> thing.types.contains(owlClass.id));
	}

	public boolean hasInstances(final Class owlClass)
	{
		return things.values().stream().anyMatch(thing -> thing.types.contains(owlClass.id));
	}

	public Property getRelation(final Class owlClass)
	{
		return properties.get(owlClass.id);
	}

	public boolean isRelation(final Class owlClass)
	{
		return properties.containsKey(owlClass.id);
	}

	public boolean hasProperties(final Class owlClass)
	{
		return properties.values().stream().anyMatch(p -> p.domains.contains(owlClass.id));
	}

	public Stream<Property> getProperties(final Class owlClass)
	{
		return properties.values().stream().filter(p -> p.domains.contains(owlClass.id));
	}

	static class Resource
	{
		final String id;

		public String comment;

		Set<String> annotations = new HashSet<>();

		public Resource(final String id)
		{
			this.id = id;
		}

		String getLocalName()
		{
			int begin = id.indexOf('#');
			if (begin != -1)
			{
				return id.substring(begin + 1);
			}
			return id;
		}

		public String getNameSpace()
		{
			return "";
		}
	}

	static class Class extends Resource
	{
		Set<String> subclasses = new HashSet<>();

		Set<String> superclasses = new HashSet<>();

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
