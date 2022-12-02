/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.jena;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.UniqueFilter;
import org.apache.jena.vocabulary.RDF;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import treebolic.annotations.NonNull;

/**
 * OWL query engine
 */
public class QueryEngine
{
	private final Map<String, OntProperty> owlProperties = new HashMap<>();

	/**
	 * Constructs a QueryEngine. This will answer queries using the specified reasoner.
	 *
	 * @param model The ontology
	 */
	public QueryEngine(final OntModel model)
	{
		// Object properties
		model.listObjectProperties().forEach(p -> owlProperties.put(p.getLocalName(), p));
	}

	// C L A S S E S

	/**
	 * Gets the equivalent classes of a class expression
	 *
	 * @param clazz The class expression.
	 * @return The equivalent classes of the specified class expression If there was a problem parsing the class expression.
	 */
	public ExtendedIterator<OntClass> getEquivalentClasses(final OntClass clazz)
	{
		return clazz.listEquivalentClasses();
	}

	/**
	 * Gets the superclasses of a class expression
	 *
	 * @param clazz The class expression.
	 * @return The superclasses of the specified class expression If there was a problem parsing the class expression.
	 */
	public ExtendedIterator<OntClass> getSuperClasses(final OntClass clazz)
	{
		return clazz.listSuperClasses(true);
	}

	// subclasses

	/**
	 * Gets the subclasses of a class expression
	 *
	 * @param owlClass The class expression.
	 * @return The subclasses of the specified class expression If there was a problem parsing the class expression.
	 */
	public ExtendedIterator<OntClass> getSubClasses(final OntClass owlClass)
	{
		return owlClass.listSubClasses(true);
	}

	// instances
	static final boolean DIRECT_INSTANCES = false;

	/**
	 * Gets the instances of a class expression
	 *
	 * @param clazz The class expression.
	 * @return The instances of the specified class expression.
	 */
	public ExtendedIterator<OntResource> getInstances(final OntClass clazz)
	{
		Model model = clazz.getModel();
		return model.listStatements(null, RDF.type, clazz) //
				.mapWith(Statement::getSubject) //
				.mapWith(r -> r.as(OntResource.class)) //
				.filterKeep(o -> o.hasRDFType(clazz, true)) //
				.filterKeep(new UniqueFilter<>()) //
				;
	}

	// relations

	/**
	 * Gets the properties of a class expression
	 *
	 * @param entity OWL entity
	 * @return The objectproperty matching the specified entity (by name).
	 */
	public OntProperty getRelation(final OntResource entity)
	{
		String key = entity.getLocalName();
		return owlProperties.get(key);
	}

	/**
	 * Is a relation
	 *
	 * @param entity OWL entity
	 * @return true is this entity is a relation
	 */
	public boolean isRelation(final OntResource entity)
	{
		return getRelation(entity) != null;
	}

	/**
	 * Get domains of object property
	 *
	 * @param property property
	 * @return stream of domain classes
	 */
	public ExtendedIterator<OntClass> getDomains(final OntProperty property)
	{
		return property.asObjectProperty().listDomain().mapWith(OntResource::asClass);
	}

	/**
	 * Get ranges of object property
	 *
	 * @param property property
	 * @return stream of domain classes
	 */
	public ExtendedIterator<OntClass> getRanges(final OntProperty property)
	{
		return property.asObjectProperty().listRange().mapWith(OntResource::asClass);
	}

	/**
	 * Get sub properties of object property
	 *
	 * @param property property
	 * @return stream of sub properties
	 */
	public ExtendedIterator<? extends OntProperty> getSubproperties(final OntProperty property)
	{
		return property.asObjectProperty().listSubProperties();
	}

	/**
	 * Get inverse properties of object property
	 *
	 * @param property property
	 * @return stream of inverse properties
	 */
	public ExtendedIterator<? extends OntProperty> getInverseProperties(final OntProperty property)
	{
		return property.asObjectProperty().listInverse();
	}

	/**
	 * Gets the properties of a class
	 *
	 * @param owlClass OWL class
	 * @return stream of properties
	 */
	public Stream<OntProperty> getProperties(final OntClass owlClass)
	{
		return this.owlProperties.values().stream() //
				.filter(p -> getDomains(p).toList().stream().anyMatch(c -> c.equals(owlClass)));
	}

	// top classes

	/**
	 * Top classes
	 *
	 * @param model model
	 * @return top classes
	 */
	public ExtendedIterator<OntClass> getTopClasses(final OntModel model)
	{
		return model.listHierarchyRootClasses().filterDrop(c -> c.getLocalName() == null);
	}

	// E N T I T I E S

	/**
	 * Get entity's annotations
	 *
	 * @param entity entity
	 * @param lang   language
	 * @return stream of annotations
	 */
	public ExtendedIterator<RDFNode> getAnnotations(final OntResource entity, @NonNull final String lang)
	{
		return entity.listComments(lang);
	}

	public Stream<OntClass> getTypes(final OntResource entity)
	{
		return Stream.empty();
	}
}
