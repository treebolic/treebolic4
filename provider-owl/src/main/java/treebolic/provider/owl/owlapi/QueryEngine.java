/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.owlapi;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.*;
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

		// We need to create an instance of OWLReasoner. An OWLReasoner provides/ the basic query functionality that we need do our query answering
		// for example the ability obtain the subclasses of a class etc.
		this.reasoner = reasonerFactory.createReasoner(this.ontology);

		// Object properties
		this.owlProperties = this.ontology.getObjectPropertiesInSignature();
	}

	// E N T I T I E S

	public Stream<OWLAnnotation> getAnnotations(final OWLEntity entity)
	{
		return EntitySearcher.getAnnotations(entity, this.ontology);
	}

	// C L A S S E S

	/**
	 * Gets the superclasses of a class expression
	 *
	 * @param classExpression The class expression.
	 * @param direct          Specifies whether direct superclasses should be returned or not.
	 * @return The superclasses of the specified class expression If there was a problem parsing the class expression.
	 */
	public Set<OWLClass> getSuperClasses(final OWLClassExpression classExpression, final boolean direct)
	{
		final NodeSet<OWLClass> superClasses = this.reasoner.getSuperClasses(classExpression, direct);
		return superClasses.getFlattened();
	}

	/**
	 * Gets the equivalent classes of a class expression
	 *
	 * @param classExpression The class expression.
	 * @return The equivalent classes of the specified class expression If there was a problem parsing the class expression.
	 */
	public Set<OWLClass> getEquivalentClasses(final OWLClassExpression classExpression)
	{
		final Node<OWLClass> equivalentClasses = this.reasoner.getEquivalentClasses(classExpression);
		Set<OWLClass> result;
		if (classExpression.isAnonymous())
		{
			result = equivalentClasses.getEntities();
		}
		else
		{
			result = equivalentClasses.getEntitiesMinus(classExpression.asOWLClass());
		}
		return result;
	}

	/**
	 * Gets the subclasses of a class expression
	 *
	 * @param classExpression The class expression.
	 * @param direct          Specifies whether direct subclasses should be returned or not.
	 * @return The subclasses of the specified class expression If there was a problem parsing the class expression.
	 */
	public Set<OWLClass> getSubClasses(final OWLClassExpression classExpression, final boolean direct)
	{
		final NodeSet<OWLClass> subClasses = this.reasoner.getSubClasses(classExpression, direct);
		return new TreeSet<>(subClasses.getFlattened());
	}

	/**
	 * Gets the instances of a class expression
	 *
	 * @param classExpression The class expression.
	 * @param direct          Specifies whether direct instances should be returned or not.
	 * @return The instances of the specified class expression If there was a problem parsing the class expression.
	 */
	public Set<OWLNamedIndividual> getInstances(final OWLClassExpression classExpression, final boolean direct)
	{
		final NodeSet<OWLNamedIndividual> individuals = this.reasoner.getInstances(classExpression, direct);
		return new TreeSet<>(individuals.getFlattened());
	}

	public Stream<OWLIndividual> getInstances(final OWLClassExpression classExpression)
	{
		return EntitySearcher.getInstances(classExpression, this.ontology);
	}

	/**
	 * Gets the properties of a class expression
	 *
	 * @param owlClass The class
	 * @param direct   Specifies whether direct instances should be returned or not.
	 * @return The instances of the specified class expression If there was a problem parsing the class expression.
	 */
	public Set<OWLObjectProperty> getProperties(final OWLClass owlClass, final boolean direct)
	{
		if (!this.ontology.containsClassInSignature(owlClass.getIRI()))
		{
			// throw new RuntimeException("Class not in signature of the ontology");
			return null;
		}

		// System.out.println("Properties of " + owlClass);
		final Set<OWLObjectProperty> owlClassProperties = new HashSet<>();
		for (final OWLObjectProperty owlProperty : this.owlProperties)
		{
			final boolean test = hasProperty(owlClass, owlProperty);
			if (test)
			{
				// System.out.println(" property " + owlProperty);
				owlClassProperties.add(owlProperty);
			}
		}
		return owlClassProperties;
	}

	private boolean hasProperty(final OWLClass owlClass, final OWLObjectPropertyExpression prop)
	{
		final NodeSet<OWLClass> owlClasses = this.reasoner.getObjectPropertyDomains(prop, true);
		return owlClasses.containsEntity(owlClass);
	}

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
}
