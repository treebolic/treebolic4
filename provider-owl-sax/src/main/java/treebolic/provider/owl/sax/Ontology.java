/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.sax;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import treebolic.annotations.NonNull;

class Ontology
{
	public Ontology(final Map<String, Class> classes, final Map<String, Thing> things, final Map<String, Property> properties)
	{
		this.classes = classes;
		this.things = things;
		this.properties = properties;

		// resolve
		this.classes.values().forEach(c -> c.superclasses = c._superclasses.stream().map(classes::get).peek(c2 -> {
			if (c2 == null)
			{
				System.err.println("NR " + c);
			}
		}).filter(Objects::nonNull).collect(Collectors.toSet()));
		this.things.values().forEach(c -> c.types = c._types.stream().map(classes::get).collect(Collectors.toSet()));
		this.properties.values().forEach(p -> p.domains = p._domains.stream().map(classes::get).collect(Collectors.toSet()));
		this.properties.values().forEach(p -> p.ranges = p._ranges.stream().map(classes::get).collect(Collectors.toSet()));
		this.properties.values().forEach(p -> p.inverses = p._inverses.stream().map(properties::get).collect(Collectors.toSet()));
		this.properties.values().forEach(p -> p.subproperties = p._subproperties.stream().map(properties::get).collect(Collectors.toSet()));

		// reverse tree link (class-to-subclasses) from class-to-superclass
		this.classes.values().forEach(c -> c.superclasses.forEach(sc -> {

			if (sc.subclasses == null)
			{
				sc.subclasses = new HashSet<>();
			}
			sc.subclasses.add(c);
		}));

		// attach instances to class
		this.things.values().stream().filter(i -> i.types != null).forEach(i -> i.types.stream().filter(Objects::nonNull).forEach(c -> {

			if (c.instances == null)
			{
				c.instances = new HashSet<>();
			}
			c.instances.add(i);
		}));

		// attach properties to class
		this.properties.values().stream().filter(p -> p.domains != null).forEach(p -> p.domains.forEach(c -> {

			if (c.properties == null)
			{
				c.properties = new HashSet<>();
			}
			c.properties.add(p);
		}));

		// clean up
		this.classes.values().forEach(c -> {
			c._superclasses = null;
		});
		this.things.values().forEach(c -> {
			c._types = null;
		});
		this.properties.values().forEach(c -> {
			c._domains = null;
			c._ranges = null;
			c._inverses = null;
			c._subproperties = null;
		});

		// check
		this.classes.values().forEach(c -> {
			assert c.iri != null : c;
		});
		this.classes.values().stream().filter(c -> c.superclasses != null).map(c1 -> c1.superclasses).forEach(c2 -> c2.forEach(c3 -> {
			assert c3 != null;
		}));
		this.classes.values().stream().filter(c -> c.subclasses != null).map(c1 -> c1.subclasses).forEach(c2 -> c2.forEach(c3 -> {
			assert c3 != null;
		}));

		// this.classes.values().stream().filter(c -> c.superclasses != null).flatMap(c -> c.superclasses.stream()).forEach(c -> {
		// 	assert c != null;
		// });
		// this.classes.values().stream().filter(c -> c.subclasses != null).flatMap(c -> c.subclasses.stream()).forEach(c -> {
		// 	assert c != null;
		// });
	}

	final Map<String, Class> classes;

	final Map<String, Thing> things;

	final Map<String, Property> properties;

	public Class createClass(final String iri)
	{
		Class c = new Class(iri);
		classes.put(iri, c);
		return c;
	}

	public Stream<Class> getTopClasses()
	{
		return classes.values().stream().filter(clazz -> clazz.superclasses.isEmpty());
	}

	public Property getRelation(final Class owlClass)
	{
		return properties.get(owlClass.iri);
	}

	public boolean isRelation(final Class owlClass)
	{
		return properties.containsKey(owlClass.iri);
	}

	static class Resource
	{
		static Comparator<Resource> COMPARATOR = Comparator.comparing(Resource::getIri);

		final String iri;

		public String comment;

		Set<String> annotations = new HashSet<>();

		public Resource(final String iri)
		{
			assert iri != null;
			this.iri = iri;
		}

		String getLocalName()
		{
			int begin = iri.indexOf('#');
			if (begin != -1)
			{
				return iri.substring(begin + 1);
			}
			return iri;
		}

		public String getIri()
		{
			return iri;
		}

		public String getNameSpace()
		{
			return "";
		}

		@Override
		public String toString()
		{
			return iri;
		}
	}

	static class Class extends Resource implements Comparable<Class>
	{
		public Set<Ontology.Class> subclasses;

		public Set<Ontology.Class> superclasses;

		public Set<Ontology.Thing> instances;

		public Set<Ontology.Property> properties;

		Set<String> _superclasses = new HashSet<>();

		public Class(final String iri)
		{
			super(iri);
		}

		@Override
		public int compareTo(@NonNull final Class that)
		{
			return COMPARATOR.compare(this, that);
		}
	}

	static class Thing extends Resource
	{
		public Thing(final String iri)
		{
			super(iri);
		}

		public Set<Ontology.Class> types;

		Set<String> _types = new HashSet<>();
	}

	static class Property extends Resource
	{
		String subtype;

		public Property(final String iri)
		{
			this(iri, null);
		}

		public Property(final String iri, final String subtype)
		{
			super(iri);
			this.subtype = subtype;
		}

		public Set<Ontology.Class> domains;

		public Set<Ontology.Class> ranges;

		public Set<Ontology.Property> subproperties;

		public Set<Ontology.Property> inverses;

		Set<String> _domains = new HashSet<>();

		Set<String> _ranges = new HashSet<>();

		Set<String> _subproperties = new HashSet<>();

		Set<String> _inverses = new HashSet<>();
	}
}
