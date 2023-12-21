/*
 * Copyright (c) 2023. Bernard Bou
 */

package treebolic.provider.owl.sax;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Ontology
 */
public class Ontology
{
    private static final Consumer<Ontology.Resource> warnIfNull = r -> {
        if (r == null)
        {
            System.err.println("Not resolved");
        }
    };

    /**
     * Constructor
     *
     * @param classes    classes
     * @param things     things
     * @param properties properties
     */
    public Ontology(@NonNull final Map<String, Class> classes, final Map<String, Thing> things, @NonNull final Map<String, Property> properties)
    {
        this.classes = classes;
        this.things = things;
        this.properties = properties;

        // resolve
        this.classes.values().forEach(c -> {
            assert c._superclasses != null;
            c.superclasses = c._superclasses.stream().map(classes::get).peek(warnIfNull).filter(Objects::nonNull).collect(Collectors.toSet());
        });
        this.things.values().forEach(c -> {
            assert c._types != null;
            c.types = c._types.stream().map(classes::get).peek(warnIfNull).filter(Objects::nonNull).collect(Collectors.toSet());
        });
        this.properties.values().forEach(p -> {
            assert p._domains != null;
            p.domains = p._domains.stream().map(classes::get).peek(warnIfNull).filter(Objects::nonNull).collect(Collectors.toSet());
        });
        this.properties.values().forEach(p -> {
            assert p._ranges != null;
            p.ranges = p._ranges.stream().map(classes::get).peek(warnIfNull).filter(Objects::nonNull).collect(Collectors.toSet());
        });
        this.properties.values().forEach(p -> {
            assert p._inverses != null;
            p.inverses = p._inverses.stream().map(properties::get).peek(warnIfNull).filter(Objects::nonNull).collect(Collectors.toSet());
        });
        this.properties.values().forEach(p -> {
            assert p._subproperties != null;
            p.subproperties = p._subproperties.stream().map(properties::get).peek(warnIfNull).filter(Objects::nonNull).collect(Collectors.toSet());
        });

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
        this.classes.values().forEach(c -> c._superclasses = null);
        this.things.values().forEach(c -> c._types = null);
        this.properties.values().forEach(c -> {
            c._domains = null;
            c._ranges = null;
            c._inverses = null;
            c._subproperties = null;
        });

        // check
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

    @NonNull
    final Map<String, Class> classes;

    final Map<String, Thing> things;

    @NonNull
    final Map<String, Property> properties;

    /**
     * Create class
     *
     * @param iri iri
     * @return class
     */
    @NonNull
    public Class createClass(@NonNull final String iri)
    {
        @NonNull Class c = new Class(iri);
        classes.put(iri, c);
        return c;
    }

    /**
     * Get top classes
     *
     * @return stream of top classes
     */
    @NonNull
    public Stream<Class> getTopClasses()
    {
        return classes.values().stream().filter(clazz -> clazz.superclasses.isEmpty());
    }

    /**
     * Get relation
     *
     * @param owlClass class
     * @return property
     */
    public Property getRelation(@NonNull final Class owlClass)
    {
        return properties.get(owlClass.iri);
    }

    /**
     * Is relation
     *
     * @param owlClass class
     * @return true if this class is a relation
     */
    public boolean isRelation(@NonNull final Class owlClass)
    {
        return properties.containsKey(owlClass.iri);
    }

    static class Resource
    {
        static final Comparator<Resource> COMPARATOR = Comparator.comparing(Resource::getIri);

        @NonNull
        final String iri;

        public String comment;

        public String label;

        public final Set<String> annotations = new HashSet<>();

        public Resource(@NonNull final String iri)
        {
            this.iri = iri;
        }

        @NonNull
        public String getIri()
        {
            return iri;
        }

        @NonNull
        public String getLocalName()
        {
            int begin = iri.indexOf('#');
            if (begin != -1)
            {
                return iri.substring(begin + 1);
            }
            return iri;
        }

        @NonNull
        public String getNameSpace()
        {
            int begin = iri.indexOf('#');
            if (begin != -1)
            {
                return iri.substring(0, begin);
            }
            return "";
        }

        @NonNull
        @Override
        public String toString()
        {
            return iri;
        }
    }

    /**
     * Class
     */
    static public class Class extends Resource implements Comparable<Class>
    {
        /**
         * Sub classes
         */
        public Set<Ontology.Class> subclasses;

        /**
         * Super classes
         */
        public Set<Ontology.Class> superclasses;

        /**
         * Instances
         */
        public Set<Ontology.Thing> instances;

        /**
         * Properties
         */
        public Set<Ontology.Property> properties;

        @Nullable
        Set<String> _superclasses = new HashSet<>();

        /**
         * Constructor
         *
         * @param iri iri
         */
        public Class(@NonNull final String iri)
        {
            super(iri);
        }

        @Override
        public int compareTo(@NonNull final Class that)
        {
            return COMPARATOR.compare(this, that);
        }
    }

    /**
     * Thing
     */
    static public class Thing extends Resource
    {
        /**
         * Constructor
         *
         * @param iri iri
         */
        public Thing(@NonNull final String iri)
        {
            super(iri);
        }

        /**
         * Types
         */
        public Set<Ontology.Class> types;

        @Nullable
        Set<String> _types = new HashSet<>();
    }

    /**
     * Property
     */
    static public class Property extends Resource
    {
        final String subtype;

        /**
         * Constructor
         *
         * @param iri iri
         */
        public Property(@NonNull final String iri)
        {
            this(iri, null);
        }

        /**
         * Constructor
         *
         * @param iri     iri
         * @param subtype subtype
         */
        public Property(@NonNull final String iri, final String subtype)
        {
            super(iri);
            this.subtype = subtype;
        }

        /**
         * Domains
         */
        public Set<Ontology.Class> domains;

        /**
         * Ranges
         */
        public Set<Ontology.Class> ranges;

        /**
         * Sub properties
         */
        public Set<Ontology.Property> subproperties;

        /**
         * Inverses
         */
        public Set<Ontology.Property> inverses;

        @Nullable
        Set<String> _domains = new HashSet<>();

        @Nullable
        Set<String> _ranges = new HashSet<>();

        @Nullable
        Set<String> _subproperties = new HashSet<>();

        @Nullable
        Set<String> _inverses = new HashSet<>();
    }
}
