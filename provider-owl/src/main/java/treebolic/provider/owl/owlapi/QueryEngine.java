/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.owlapi;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * OWL query engine
 */
public class QueryEngine
{
	private final OWLOntology ontology;

	private final OWLReasoner reasoner;

	private final Map<String, OWLObjectProperty> owlProperties;

	/**
	 * Constructs a QueryEngine. This will answer queries using the specified reasoner.
	 *
	 * @param ontology The ontology
	 */
	public QueryEngine(final OWLOntology ontology)
	{
		this.ontology = ontology;

		// Create a reasoner factory.
		final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();

		// We need to create an instance of OWLReasoner.
		// An OWLReasoner provides/ the basic query functionality that we need to
		// do our query answering for example the ability obtain the subclasses of a class etc.
		this.reasoner = reasonerFactory.createReasoner(this.ontology);

		// Object properties
		this.owlProperties = this.ontology.getObjectPropertiesInSignature().stream() //
				.collect(toMap(p -> p.getIRI().getShortForm(), Function.identity()));
	}

	// C L A S S E S

	/**
	 * Gets the equivalent classes of a class expression
	 *
	 * @param classExpression The class expression.
	 * @return The equivalent classes of the specified class expression If there was a problem parsing the class expression.
	 */
	public Stream<OWLClass> getEquivalentClasses(final OWLClassExpression classExpression)
	{
		return this.reasoner.equivalentClasses(classExpression);
	}

	/**
	 * Gets the superclasses of a class expression
	 *
	 * @param classExpression The class expression.
	 * @return The superclasses of the specified class expression If there was a problem parsing the class expression.
	 */
	public Stream<OWLClass> getSuperClasses(final OWLClassExpression classExpression)
	{
		return this.reasoner.superClasses(classExpression, true);
	}

	// subclasses

	/**
	 * Gets the subclasses of a class expression
	 *
	 * @param owlClass The class expression.
	 * @return The subclasses of the specified class expression If there was a problem parsing the class expression.
	 */
	public Stream<OWLClass> getSubClasses(final OWLClass owlClass)
	{
		return this.reasoner.subClasses(owlClass, true);
	}

	// instances
	static final boolean DIRECT_INSTANCES = false;

	/**
	 * Gets the instances of a class expression
	 *
	 * @param owlClass The class expression.
	 * @return The instances of the specified class expression.
	 */
	public Stream<OWLNamedIndividual> getInstances(final OWLClass owlClass)
	{
		return this.reasoner.instances(owlClass, DIRECT_INSTANCES);
	}

	// relations

	/**
	 * Gets the properties of a class expression
	 *
	 * @param owlEntity OWL entity
	 * @return The objectproperty matching the specified entity (by name).
	 */
	public OWLObjectProperty getRelation(final OWLEntity owlEntity)
	{
		String key = owlEntity.getIRI().getShortForm();
		return owlProperties.get(key);
	}

	/**
	 * Is a relation
	 *
	 * @param owlEntity OWL entity
	 * @return true is this entity is a relation
	 */
	public boolean isRelation(final OWLEntity owlEntity)
	{
		return getRelation(owlEntity) != null;
	}

	/**
	 * Get domains of object property
	 *
	 * @param owlObjectProperty property
	 * @return stream of domain classes
	 */
	public Stream<OWLClass> getDomains(final OWLObjectProperty owlObjectProperty)
	{
		return this.reasoner.objectPropertyDomains(owlObjectProperty, true);
	}

	/**
	 * Get ranges of object property
	 *
	 * @param owlObjectProperty property
	 * @return stream of domain classes
	 */
	public Stream<OWLClass> getRanges(final OWLObjectProperty owlObjectProperty)
	{
		return this.reasoner.objectPropertyRanges(owlObjectProperty, true);
	}

	/**
	 * Get subclasses of object property
	 *
	 * @param owlObjectProperty property
	 * @return stream of sub classes
	 */
	public Stream<OWLClass> getSubclasses(final OWLObjectProperty owlObjectProperty)
	{
		return this.reasoner.subClasses(owlObjectProperty.asOWLClass(), true);
	}

	/**
	 * Get superclasses of object property
	 *
	 * @param owlObjectProperty property
	 * @return stream of super classes
	 */
	public Stream<OWLClass> getSuperclasses(final OWLObjectProperty owlObjectProperty)
	{
		return this.reasoner.superClasses(owlObjectProperty.asOWLClass(), true);
	}

	/**
	 * Get sub properties of object property
	 *
	 * @param owlObjectProperty property
	 * @return stream of sub properties
	 */
	public Stream<OWLObjectProperty> getSubproperties(final OWLObjectProperty owlObjectProperty)
	{
		return this.reasoner.subObjectProperties(owlObjectProperty, true) //
				.filter(OWLObjectPropertyExpression::isNamed) //
				.map(OWLObjectPropertyExpression::getNamedProperty);
	}

	/**
	 * Get inverse properties of object property
	 *
	 * @param owlObjectProperty property
	 * @return stream of inverse properties
	 */
	public Stream<OWLObjectProperty> getInverseProperties(final OWLObjectProperty owlObjectProperty)
	{
		return this.reasoner.inverseObjectProperties(owlObjectProperty) //
				.filter(OWLObjectPropertyExpression::isNamed) //
				.map(OWLObjectPropertyExpression::getNamedProperty);
	}

	/**
	 * Gets the properties of a class
	 *
	 * @param owlClass OWL class
	 * @return stream of properties
	 */
	public Stream<OWLObjectProperty> getProperties(final OWLClass owlClass)
	{
		return this.owlProperties.values().stream() //
				.filter(p -> getDomains(p) //
						.anyMatch(c -> c.equals(owlClass)));
	}

	// top classes

	/**
	 * Top classes
	 *
	 * @return top classes
	 */
	public Iterator<OWLClass> getTopClasses()
	{
		return this.reasoner.getTopClassNode().iterator();
	}

	/**
	 * Top class
	 *
	 * @return top class
	 */
	public OWLClass getTopClass()
	{
		final Iterator<OWLClass> it = getTopClasses();
		if (!it.hasNext())
		{
			return null;
		}
		return it.next();
	}

	// I N D I V I D U A L S

	/**
	 * Get class of an individual
	 *
	 * @param owlIndividual individual
	 * @return stream of class expressions
	 */
	public Stream<OWLClassExpression> getTypes(final OWLIndividual owlIndividual)
	{
		return EntitySearcher.getTypes(owlIndividual, this.ontology);
	}

	// E N T I T I E S

	/**
	 * Get entity's annotations
	 *
	 * @param entity entity
	 * @return stream of annotations
	 */
	public Stream<OWLAnnotation> getAnnotations(final OWLEntity entity)
	{
		return EntitySearcher.getAnnotations(entity, this.ontology);
	}
}
