/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.owlapi;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public class QueryEngine
{
	private final OWLOntology ontology;

	private final OWLReasoner reasoner;

	private final Set<OWLObjectProperty> owlProperties;

	/**
	 * Constructs a DLQueryEngine. This will answer "DL queries" using the specified reasoner.
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
		this.owlProperties = this.ontology.getObjectPropertiesInSignature();
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

	/**
	 * Gets the instances of a class expression
	 *
	 * @param owlClass The class expression.
	 * @return The instances of the specified class expression.
	 */
	public Stream<OWLNamedIndividual> getInstances(final OWLClass owlClass)
	{
		return this.reasoner.instances(owlClass, true);
	}

	// properties

	/**
	 * Gets the properties of a class expression
	 *
	 * @param owlClass The class
	 * @return The instances of the specified class expression. Null if there was a problem parsing the class expression.
	 */
	public Stream<OWLObjectProperty> getProperties(final OWLClass owlClass)
	{
		return this.owlProperties.stream().filter(p -> hasProperty(owlClass, p));
	}

	private boolean hasProperty(final OWLClass owlClass, final OWLObjectPropertyExpression prop)
	{
		final NodeSet<OWLClass> owlClasses = this.reasoner.getObjectPropertyDomains(prop, true);
		return owlClasses.containsEntity(owlClass);
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

	public Stream<OWLClassExpression> getTypes(final OWLIndividual owlIndividual)
	{
		return EntitySearcher.getTypes(owlIndividual, this.ontology);
	}

	// E N T I T I E S

	public Stream<OWLAnnotation> getAnnotations(final OWLEntity entity)
	{
		return EntitySearcher.getAnnotations(entity, this.ontology);
	}
}
