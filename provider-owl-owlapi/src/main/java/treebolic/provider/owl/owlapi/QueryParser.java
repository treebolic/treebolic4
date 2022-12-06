/*
 * Copyright (c) 2022. Bernard Bou
 */

package treebolic.provider.owl.owlapi;

import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import java.util.Set;

import treebolic.annotations.NonNull;

class QueryParser
{
	private final OWLOntology rootOntology;

	@NonNull
	private final BidirectionalShortFormProvider bidiShortFormProvider;

	/**
	 * Constructs a DLQueryParser using the specified ontology and short form provider to map entity IRIs to short names.
	 *
	 * @param rootOntology      The root ontology. This essentially provides the domain vocabulary for the query.
	 * @param shortFormProvider A short form provider to be used for mapping back and forth between entities and their short names (renderings).
	 */
	public QueryParser(@NonNull final OWLOntology rootOntology, @NonNull final ShortFormProvider shortFormProvider)
	{
		this.rootOntology = rootOntology;
		final OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
		final Set<OWLOntology> importsClosure = rootOntology.getImportsClosure();
		// Create a bidirectional short form provider to do the actual mapping.
		// It will generate names using the input short form provider.
		this.bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager, importsClosure, shortFormProvider);
	}

	/**
	 * Parses a class expression string to obtain a class expression.
	 *
	 * @param classExpressionString The class expression string
	 * @return The corresponding class expression if the class expression string is malformed or contains unknown entity names.
	 */
	public OWLClassExpression parseClassExpression(@NonNull final String classExpressionString)
	{
		// Set up the parser
		final OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
		final OWLDataFactory dataFactory = manager.getOWLDataFactory();
		final OntologyConfigurator configurator = manager.getOntologyConfigurator();
		@NonNull final ManchesterOWLSyntaxParser parser = new ManchesterOWLSyntaxParserImpl(configurator, dataFactory);
		parser.setStringToParse(classExpressionString);
		parser.setDefaultOntology(rootOntology);

		// Specify an entity checker that wil be used to check a class expression contains the correct names.
		@NonNull final OWLEntityChecker entityChecker = new ShortFormEntityChecker(this.bidiShortFormProvider);
		parser.setOWLEntityChecker(entityChecker);

		// Do the actual parsing
		return parser.parseClassExpression();
	}
}