/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.provider.owl.owlapi;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import treebolic.annotations.NonNull;
import treebolic.glue.Color;
import treebolic.glue.Image;
import treebolic.model.*;
import treebolic.provider.LoadBalancer;

import static java.util.stream.Collectors.toList;

/**
 * OWL model factory
 *
 * @author Bernard Bou
 */
public class OwlModelFactory
{
	/**
	 * As tree
	 */
	static final boolean asTree = false;

	// S T A T I C . D A T A

	/**
	 * Image indices
	 */
	public enum ImageIndices
	{
		/**
		 * Root
		 */
		ROOT,
		/**
		 * Class
		 */
		CLASS,
		/**
		 * Class with instances attached
		 */
		CLASSWITHINSTANCES,
		/**
		 * Instances
		 */
		INSTANCES,
		/**
		 * Instance
		 */
		INSTANCE,
		/**
		 * Class with properties attached
		 */
		CLASSWITHPROPERTIES,
		/**
		 * Properties
		 */
		PROPERTIES,
		/**
		 * Property
		 */
		PROPERTY,
		/**
		 * Load balancing branch
		 */
		BRANCH,
		/**
		 * Load balancing 2 branch
		 */
		BRANCH2
	}

	static Image[] images;

	/**
	 * Default class color
	 */
	static final Color defaultClassBackColor = new Color(0xffffC0);

	// L O A D B A L A N C I N G

	/**
	 * LoadBalancer : Max children nodes at level 0, 1 ... n. Level 0 is just above leaves. Level > 0 is upward from leaves. Last value i holds for level i to n.
	 */
	static private final int[] MAX_AT_LEVEL = {8, 3};

	/**
	 * LoadBalancer : Truncation threshold
	 */
	static private final int LABEL_TRUNCATE_AT = 3;

	/**
	 * LoadBalancer (classes) : back color
	 */
	static private final Color LOADBALANCING_BACKCOLOR = null;

	/**
	 * LoadBalancer (classes) : fore color
	 */
	static private final Color LOADBALANCING_FORECOLOR = null;

	/**
	 * LoadBalancer (classes) : edge color
	 */
	static private final Color LOADBALANCING_EDGECOLOR = Color.DARK_GRAY;

	/**
	 * LoadBalancer (classes) : image index
	 */
	static private final int LOADBALANCING_IMAGEINDEX = ImageIndices.BRANCH.ordinal(); // -1;

	/**
	 * LoadBalancer (classes) : image
	 */
	static private final Image LOADBALANCING_IMAGE = null;

	/**
	 * LoadBalancer (classes) : Edge style
	 */
	static private final int LOADBALANCING_EDGE_STYLE = IEdge.SOLID | /* IEdge.FROMDEF | IEdge.FROMCIRCLE | */IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.STROKEDEF | IEdge.TODEF;

	/**
	 * LoadBalancer (instances and properties) : back color
	 */
	static private final Color LOADBALANCING2_BACKCOLOR = null;

	/**
	 * LoadBalancer (instances and properties) : fore color
	 */
	static private final Color LOADBALANCING2_FORECOLOR = null;

	/**
	 * LoadBalancer (instances and properties) : edge color
	 */
	static private final Color LOADBALANCING2_EDGECOLOR = Color.GRAY; // Color.DARK_GRAY;

	/**
	 * LoadBalancer (instances and properties) : image index
	 */
	static private final int LOADBALANCING2_IMAGEINDEX = ImageIndices.BRANCH2.ordinal(); // -1;

	/**
	 * LoadBalancer (instances and properties) : image
	 */
	static private final Image LOADBALANCING2_IMAGE = null;

	/**
	 * LoadBalancer (instances and properties) : Edge style
	 */
	static private final int LOADBALANCING2_EDGE_STYLE = IEdge.DASH | /* IEdge.FROMDEF | IEdge.FROMCIRCLE | */IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.STROKEDEF | IEdge.TODEF;

	// D E C O R A T I O N   M E M B E R S

	/**
	 * Properties
	 */
	private final Properties properties;

	// class

	/**
	 * Class back color
	 */
	private Color classBackColor;

	/**
	 * Class fore color
	 */
	private Color classForeColor;

	/**
	 * Class image
	 */
	private String classImageFile;

	/**
	 * Class with instances fore color
	 */
	private Color classWithInstancesBackColor;

	/**
	 * Class with instances fore color
	 */
	private Color classWithInstancesForeColor;

	/**
	 * Class with properties image
	 */
	private String classWithInstancesImageFile;

	/**
	 * Class with properties fore color
	 */
	private Color classWithPropertiesBackColor;

	/**
	 * Class with properties fore color
	 */
	private Color classWithPropertiesForeColor;

	/**
	 * Class with properties image
	 */
	private String classWithPropertiesImageFile;

	// root

	/**
	 * Root label
	 */
	private String rootLabel;

	/**
	 * Root fore color
	 */
	private Color rootForeColor;

	/**
	 * Root fore color
	 */
	private Color rootBackColor;

	/**
	 * Root image
	 */
	private String rootImageFile;

	// instances

	/**
	 * Instances label
	 */
	private String instancesLabel;

	/**
	 * Instances fore color
	 */
	private Color instancesForeColor;

	/**
	 * Instances fore color
	 */
	private Color instancesBackColor;

	/**
	 * 6 Instances image
	 */
	private String instancesImageFile;

	/**
	 * Instance fore color
	 */
	private Color instanceForeColor;

	/**
	 * Instance fore color
	 */
	private Color instanceBackColor;

	/**
	 * Instance image
	 */
	private String instanceImageFile;

	/**
	 * Instance edge color
	 */
	private Color instanceEdgeColor;

	/**
	 * Instance edge style
	 */
	private Integer instanceEdgeStyle;

	/**
	 * Instance edge image file
	 */
	private String instanceEdgeImageFile;

	// properties

	/**
	 * Properties label
	 */
	private String propertiesLabel;

	/**
	 * Properties fore color
	 */
	private Color propertiesForeColor;

	/**
	 * Properties fore color
	 */
	private Color propertiesBackColor;

	/**
	 * Properties image
	 */
	private String propertiesImageFile;

	/**
	 * Property fore color
	 */
	private Color propertyForeColor;

	/**
	 * Property fore color
	 */
	private Color propertyBackColor;

	/**
	 * Property image
	 */
	private String propertyImageFile;

	/**
	 * Property edge color
	 */
	private Color propertyEdgeColor;

	/**
	 * Property edge style
	 */
	private Integer propertyEdgeStyle;

	/**
	 * Property edge image file
	 */
	private String propertyEdgeImageFile;

	// M E M B E R S

	/**
	 * Ontology
	 */
	private OWLOntology ontology;

	/**
	 * Engine
	 */
	private QueryParser parser;

	/**
	 * Engine
	 */
	private QueryEngine engine;

	/**
	 * Manager
	 */
	final OWLOntologyManager manager;

	/**
	 * Data factory
	 */
	private final OWLDataFactory dataFactory;

	/**
	 * Url engine was build from
	 */
	private String url;

	/**
	 * Load balancer (classes)
	 */
	protected final LoadBalancer loadBalancer;

	/**
	 * Load balancer (properties and instances)
	 */
	protected final LoadBalancer subLoadBalancer;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param properties properties
	 */
	public OwlModelFactory(final Properties properties)
	{
		this.properties = properties;
		this.manager = OWLManager.createOWLOntologyManager();
		this.dataFactory = this.manager.getOWLDataFactory();

		this.url = null;
		this.ontology = null;
		this.engine = null;
		this.parser = null;

		this.loadBalancer = new LoadBalancer(MAX_AT_LEVEL, LABEL_TRUNCATE_AT);
		this.loadBalancer.setGroupNode(null, LOADBALANCING_BACKCOLOR, LOADBALANCING_FORECOLOR, LOADBALANCING_EDGECOLOR, LOADBALANCING_EDGE_STYLE, LOADBALANCING_IMAGEINDEX, LOADBALANCING_IMAGE);
		this.subLoadBalancer = new LoadBalancer(MAX_AT_LEVEL, LABEL_TRUNCATE_AT);
		this.subLoadBalancer.setGroupNode(null, LOADBALANCING2_BACKCOLOR, LOADBALANCING2_FORECOLOR, LOADBALANCING2_EDGECOLOR, LOADBALANCING2_EDGE_STYLE, LOADBALANCING2_IMAGEINDEX, LOADBALANCING2_IMAGE);

		initialize();
	}

	/**
	 * Initialize from properties
	 */
	void initialize()
	{
		this.rootBackColor = getColor("root.backcolor", Color.ORANGE);
		this.rootForeColor = getColor("root.forecolor", Color.BLACK);
		this.rootImageFile = getImageFile("root.image");
		this.rootLabel = getLabel("root.label", "Thing");

		this.classBackColor = getColor("class.backcolor", OwlModelFactory.defaultClassBackColor);
		this.classForeColor = getColor("class.forecolor", Color.BLACK);
		this.classImageFile = getImageFile("class.image");

		this.classWithPropertiesBackColor = getColor("class.withprops.backcolor", OwlModelFactory.defaultClassBackColor);
		this.classWithPropertiesForeColor = getColor("class.withprops.forecolor", Color.MAGENTA);
		this.classWithPropertiesImageFile = getImageFile("class.withprops.image");

		this.classWithInstancesBackColor = getColor("class.withinstances.backcolor", OwlModelFactory.defaultClassBackColor);
		this.classWithInstancesForeColor = getColor("class.withinstances.forecolor", Color.BLUE);
		this.classWithInstancesImageFile = getImageFile("class.withinstances.image");

		this.instancesLabel = getLabel("instances.label", "instances");
		this.instancesBackColor = getColor("instances.backcolor", Color.WHITE);
		this.instancesForeColor = getColor("instances.forecolor", Color.BLUE);
		this.instancesImageFile = getImageFile("instances.image");

		this.instanceBackColor = getColor("instance.backcolor", Color.WHITE);
		this.instanceForeColor = getColor("instance.forecolor", Color.BLUE);
		this.instanceEdgeColor = getColor("instance.edgecolor", Color.BLUE);
		this.instanceImageFile = getImageFile("instance.image");
		this.instanceEdgeImageFile = getImageFile("instance.edge.image");

		this.propertiesLabel = getLabel("properties.label", "properties");
		this.propertiesBackColor = getColor("properties.backcolor", Color.WHITE);
		this.propertiesForeColor = getColor("properties.forecolor", Color.MAGENTA);
		this.propertiesImageFile = getImageFile("properties.image");

		this.propertyBackColor = getColor("property.backcolor", Color.WHITE);
		this.propertyForeColor = getColor("property.forecolor", Color.MAGENTA);
		this.propertyEdgeColor = getColor("property.edge.color", Color.MAGENTA);
		this.propertyImageFile = getImageFile("property.image");
		this.propertyEdgeImageFile = getImageFile("property.edge.image");

		OwlModelFactory.images = new Image[]{ //
				Image.make(Provider.class.getResource("images/root.png")), // ROOT
				Image.make(Provider.class.getResource("images/class.png")), // CLASS
				Image.make(Provider.class.getResource("images/classwithinstances.png")), // CLASSWITHINSTANCES
				Image.make(Provider.class.getResource("images/instances.png")), // INSTANCES
				Image.make(Provider.class.getResource("images/instance.png")), // INSTANCE
				Image.make(Provider.class.getResource("images/classwithproperties.png")), // CLASSWITHPROPERTIES
				Image.make(Provider.class.getResource("images/properties.png")), // PROPERTIES
				Image.make(Provider.class.getResource("images/property.png")), // PROPERTY
				Image.make(Provider.class.getResource("images/branch.png")), // BRANCH
				Image.make(Provider.class.getResource("images/branch2.png")), // BRANCH2
		};
	}

	/**
	 * Get ontology
	 *
	 * @param ontologyDocumentUrl document url
	 * @return ontology
	 * @throws MalformedURLException        malformed url exception
	 * @throws IOException                  io exception
	 * @throws OWLOntologyCreationException owl ontology creation exception
	 */
	private OWLOntology getOntology(final String ontologyDocumentUrl) throws MalformedURLException, IOException, OWLOntologyCreationException
	{
		try (InputStream is = new URL(ontologyDocumentUrl).openStream())
		{
			return this.manager.loadOntologyFromOntologyDocument(new StreamDocumentSource(is));
		}
	}

	// P A R S E

	/**
	 * Make model
	 *
	 * @param ontologyUrlString ontology URL string
	 * @return model if successful
	 */
	public Model makeModel(final String ontologyUrlString)
	{
		final Tree tree = makeTree(ontologyUrlString);
		if (tree == null)
		{
			return null;
		}

		final Settings settings = new Settings();
		settings.hasToolbarFlag = true;
		settings.hasStatusbarFlag = true;
		settings.orientation = OwlModelFactory.asTree ? "south" : "radial";
		settings.hasToolbarFlag = true;
		settings.backColor = new Color(0xffffe0);
		settings.fontFace = "SansSerif";
		settings.fontSize = 15;
		settings.expansion = .9F;
		settings.sweep = 1.2F;
		settings.hasStatusbarFlag = true;
		settings.focusOnHoverFlag = false;
		settings.treeEdgeColor = Color.BLACK;
		settings.treeEdgeStyle = IEdge.SOLID | IEdge.TOTRIANGLE | IEdge.TOFILL;
		if (OwlModelFactory.asTree)
		{
			settings.yMoveTo = -0.3F;
		}

		// override
		if (this.properties != null)
		{
			try
			{
				settings.load(this.properties);
			}
			catch (final Exception e)
			{
				System.err.println("SETTING " + e.toString());
			}
		}

		// cache property features from settings
		this.instanceEdgeStyle = settings.edgeStyle;
		this.instanceEdgeColor = settings.edgeColor;
		this.instanceEdgeImageFile = settings.defaultEdgeImage;
		this.propertyEdgeStyle = settings.edgeStyle;
		this.propertyEdgeColor = settings.edgeColor;
		this.propertyEdgeImageFile = settings.defaultEdgeImage;

		return new Model(tree, settings, OwlModelFactory.images);
	}

	/**
	 * Make tree
	 *
	 * @param urlString url string
	 * @return tree if successful
	 */
	public Tree makeTree(final String urlString)
	{
		// parameter and url
		final Map<String, String> parse = parseUrl(urlString);
		final String ontologyUrlString = parse.get("url");
		final String classIri = parse.get("iri");
		final String classShortForm = parse.get("class");
		final String target = parse.get("target");

		if (ontologyUrlString == null)
		{
			return null;
		}

		// load document
		if (!ontologyUrlString.equals(this.url) || this.ontology == null || this.engine == null || this.parser == null)
		{
			try
			{
				this.ontology = getOntology(ontologyUrlString);
				this.url = ontologyUrlString;
				// System.out.println("Loaded ontology: " + this.ontology.getOntologyID());

				this.engine = new QueryEngine(this.ontology);
				this.parser = new QueryParser(this.ontology, new SimpleShortFormProvider());
			}
			catch (final Exception e)
			{
				System.err.println("Owl:" + e);
				return null;
			}
		}

		// root
		OWLClass owlClass;
		if (classIri != null || classShortForm != null)
		{
			// parse
			if (classIri != null)
			{
				owlClass = this.dataFactory.getOWLClass(IRI.create(classIri));
			}
			else
			{
				owlClass = this.parser.parseClassExpression(classShortForm).asOWLClass();
			}

			// class
			if (owlClass != null)
			{
				if (target != null)
				{
					if ("AsymmetricRelation".equals(owlClass.getIRI().getShortForm()))
					{
						System.out.println();
					}

					boolean hasInstances = target.contains("instances");
					boolean hasProperties = target.contains("properties");
					boolean isRelation = target.contains("relation");

					// root
					final MutableNode owlClassNode = new MutableNode(null, "root");
					owlClassNode.setLabel(owlClass.getIRI().getShortForm());
					decorateClassWith(owlClassNode, hasInstances, hasProperties, isRelation);

					// instances
					if (hasInstances)
					{
						// instances root
						final TreeMutableNode instancesNode = new TreeMutableNode(owlClassNode, classIri + "-instances");
						decorateInstances(instancesNode);

						// instances
						final Stream<OWLNamedIndividual> instances = this.engine.getInstances(owlClass);
						visitInstances(instancesNode, instances.sorted());
					}
					// relation
					if (isRelation)
					{
						// relation
						OWLObjectProperty relation = this.engine.getRelation(owlClass);
						visitRelation(owlClassNode, relation);
					}
					// properties
					if (hasProperties)
					{
						// properties root
						final TreeMutableNode propertiesNode = new TreeMutableNode(owlClassNode, classIri + "-properties");
						decorateProperties(propertiesNode);

						// properties
						final Stream<OWLObjectProperty> properties = this.engine.getProperties(owlClass);
						visitProperties(propertiesNode, properties);
					}
					return new Tree(owlClassNode, null);
				}
				// class
				final MutableNode owlClassNode = visitClassAndSubclasses(null, owlClass, ontologyUrlString);
				return new Tree(decorateRoot(owlClassNode), null);
			}
			return null;
		}
		else
		{
			// walk classes
			final OWLClass rootClass = this.engine.getTopClass();
			final MutableNode owlClassNode = visitClassAndSubclasses(null, rootClass, ontologyUrlString);
			return new Tree(decorateRoot(owlClassNode), null);
		}
	}

	/**
	 * Make tree
	 *
	 * @param ontologyUrlString0 url string
	 * @return tree if successful
	 */
	public Map<String, String> parseUrl(final String ontologyUrlString0)
	{
		final Map<String, String> map = new HashMap<>();

		String ontologyUrlString = ontologyUrlString0;

		// parameter and url
		String parameters = null;
		final int index = ontologyUrlString.indexOf('?');
		if (index != -1)
		{
			parameters = ontologyUrlString.substring(index + 1);
			ontologyUrlString = ontologyUrlString.substring(0, index);
		}
		map.put("url", ontologyUrlString);

		// parameters
		if (parameters != null)
		{
			final String[] fields = parameters.split("&");
			for (final String field : fields)
			{
				final String[] nameValue = field.split("=");
				if (nameValue.length > 1)
				{
					map.put(nameValue[0], nameValue[1]);
				}
			}
		}
		return map;
	}

	// V I S I T   N O D E S

	/**
	 * Walk classes in stream
	 *
	 * @param parentClassNode   treebolic parent node to attach to
	 * @param owlClasses        class stream
	 * @param ontologyUrlString URL string
	 */
	public void visitClasses(final TreeMutableNode parentClassNode, final Stream<OWLClass> owlClasses, final String ontologyUrlString)
	{
		final List<INode> childNodes = owlClasses //

				.filter(owlClass -> !owlClass.isOWLNothing()) //
				.sorted() //
				.map(owlClass -> {

					// node
					final TreeMutableNode owlClassNode = visitClass(null, owlClass.asOWLClass(), ontologyUrlString);

					// recurse
					final Stream<OWLClass> owlSubClasses = this.engine.getSubClasses(owlClass);
					visitClasses(owlClassNode, owlSubClasses, ontologyUrlString);

					return owlClassNode;
				}) //
				.collect(toList());

		// balance load
		final List<INode> balancedNodes = this.loadBalancer.buildHierarchy(childNodes, 0);
		parentClassNode.addChildren(balancedNodes);
	}

	/**
	 * Visit class
	 *
	 * @param parentOwlClassNode treebolic parent node to attach to
	 * @param owlClass           class
	 * @param ontologyUrlString  ontology URL string
	 * @return treebolic node
	 */
	public TreeMutableNode visitClass(final INode parentOwlClassNode, final OWLClass owlClass, final String ontologyUrlString)
	{
		final String ownClassShortForm = owlClass.getIRI().getShortForm();
		final String owlClassId = getName(ownClassShortForm);
		final Stream<OWLAnnotation> annotations = this.engine.getAnnotations(owlClass);

		// comment
		String comment = owlClass.getIRI().toString() + "<br>" + annotationsToString(annotations);

		// node
		final TreeMutableNode owlClassNode = new TreeMutableNode(parentOwlClassNode, owlClassId);
		owlClassNode.setLabel(owlClassId);
		owlClassNode.setTarget(owlClassId);
		owlClassNode.setContent(comment);
		owlClassNode.setBackColor(this.classBackColor);
		owlClassNode.setForeColor(this.classForeColor);
		if (this.classImageFile != null)
		{
			owlClassNode.setImageFile(this.classImageFile);
		}
		else
		{
			owlClassNode.setImageIndex(ImageIndices.CLASS.ordinal());
		}

		// mounts
		if (!owlClass.isOWLThing())
		{
			// get instances or properties
			final Stream<OWLNamedIndividual> instances = this.engine.getInstances(owlClass);
			final Stream<OWLObjectProperty> properties = this.engine.getProperties(owlClass);
			final boolean hasInstances = instances.findAny().isPresent();
			final boolean hasProperties = properties.findAny().isPresent();
			final boolean isRelation = this.engine.isRelation(owlClass.asOWLClass());

			// mountpoint
			final String owlClassShortForm = owlClass.getIRI().getShortForm();
			if ("AsymmetricRelation".equals(owlClassShortForm))
			{
				System.out.println();
			}
			if (hasInstances || hasProperties || isRelation)
			{
				List<String> targets = new ArrayList<>();
				if (hasInstances)
				{
					targets.add("instances");
				}
				if (isRelation)
				{
					targets.add("relation");
				}
				if (hasProperties)
				{
					targets.add("properties");
				}

				final MountPoint.Mounting mountingPoint = new MountPoint.Mounting();
				mountingPoint.url = ontologyUrlString + "?iri=" + owlClass.getIRI().toString() + "&target=" + String.join("+", targets);
				owlClassNode.setMountPoint(mountingPoint);
			}
		}

		// if (owlClass.listDeclaredProperties(true).hasNext())
		return owlClassNode;
	}

	/**
	 * Visit classes and subclasses
	 *
	 * @param parentOwlClassNode treebolic parent node to attach to
	 * @param owlClass           class
	 * @param ontologyUrlString  ontology URL string
	 * @return treebolic node
	 */
	public MutableNode visitClassAndSubclasses(final INode parentOwlClassNode, final OWLClass owlClass, final String ontologyUrlString)
	{
		final TreeMutableNode owlClassNode = visitClass(parentOwlClassNode, owlClass, ontologyUrlString);

		// recurse
		final Stream<OWLClass> owlSubClasses = this.engine.getSubClasses(owlClass);
		visitClasses(owlClassNode, owlSubClasses.sorted(), ontologyUrlString);

		return owlClassNode;
	}

	/**
	 * Walk instances in stream
	 *
	 * @param parentNode     treebolic parent node to attach to
	 * @param owlIndividuals individual stream
	 */
	public void visitInstances(final TreeMutableNode parentNode, final Stream<OWLNamedIndividual> owlIndividuals)
	{
		final List<INode> childNodes = owlIndividuals //
				.map(owlNamedIndividual -> {

					final String owlIndividualPropertyShortForm = owlNamedIndividual.getIRI().getShortForm();
					final String owlIndividualId = getName(owlIndividualPropertyShortForm);
					final Stream<OWLClassExpression> types = this.engine.getTypes(owlNamedIndividual);
					final Stream<OWLAnnotation> annotations = this.engine.getAnnotations(owlNamedIndividual);

					final MutableNode instanceNode = new MutableNode(null, owlIndividualId);
					instanceNode.setLabel(owlIndividualId);
					instanceNode.setTarget(owlIndividualId);
					instanceNode.setContent(typesToString(types) + "<br>" + annotationsToString(annotations) + "<br>");
					decorateInstance(instanceNode);
					return instanceNode;
				}) //
				.collect(toList());

		// balance load
		final List<INode> balancedNodes = this.subLoadBalancer.buildHierarchy(childNodes, 0);
		parentNode.addChildren(balancedNodes);
	}

	/**
	 * Walk relation properties
	 *
	 * @param parentNode  treebolic parent node to attach to
	 * @param owlProperty property
	 */
	public void visitRelation(final MutableNode parentNode, final OWLObjectProperty owlProperty)
	{
		final String owlPropertyShortForm = owlProperty.getIRI().getShortForm();
		final MutableNode relationNode = new MutableNode(parentNode, owlPropertyShortForm);
		relationNode.setLabel(owlPropertyShortForm);
		decorateProperty(relationNode);

		final List<OWLClass> domains = this.engine.getDomains(owlProperty).collect(toList());
		if (!domains.isEmpty())
		{
			final MutableNode domainsNode = new MutableNode(relationNode, owlPropertyShortForm + "-domains");
			domainsNode.setLabel("domains");
			decorateProperty(domainsNode);
			domains.forEach(owlDomainClass -> {

				String owlClassId = owlDomainClass.asOWLClass().getIRI().getShortForm();
				final MutableNode domainNode = new MutableNode(domainsNode, owlClassId);
				domainNode.setLabel(owlClassId);
				domainNode.setTarget(owlClassId);
				decorateProperty(domainNode);
			});
		}

		final List<OWLClass> ranges = this.engine.getRanges(owlProperty).collect(toList());
		if (!ranges.isEmpty())
		{
			final MutableNode rangesNode = new MutableNode(relationNode, owlPropertyShortForm + "-ranges");
			rangesNode.setLabel("ranges");
			decorateProperty(rangesNode);
			ranges.forEach(owlRangeClass -> {

				String owlClassId = owlRangeClass.asOWLClass().getIRI().getShortForm();
				final MutableNode rangeNode = new MutableNode(rangesNode, owlClassId);
				rangeNode.setLabel(owlClassId);
				rangeNode.setTarget(owlClassId);
				decorateProperty(rangeNode);
			});
		}

		final List<OWLObjectProperty> subproperties = this.engine.getSubproperties(owlProperty).collect(toList());
		if (!subproperties.isEmpty())
		{
			final MutableNode subPropertiesNode = new MutableNode(relationNode, owlPropertyShortForm + "-subproperties");
			subPropertiesNode.setLabel("subproperties");
			decorateProperty(subPropertiesNode);
			subproperties.forEach(owlSubProperty -> {

				String owlSubpropertyId = owlSubProperty.getIRI().getShortForm();
				final MutableNode subPropertyNode = new MutableNode(subPropertiesNode, owlSubpropertyId);
				subPropertyNode.setLabel(owlSubpropertyId);
				subPropertyNode.setTarget(owlSubpropertyId);
				decorateProperty(subPropertyNode);
			});
		}

		final List<OWLObjectProperty> inverses = this.engine.getInverseProperties(owlProperty).collect(toList());
		if (!inverses.isEmpty())
		{
			final MutableNode inversesNode = new MutableNode(relationNode, owlPropertyShortForm + "-inverses");
			inversesNode.setLabel("inverses");
			decorateProperty(inversesNode);
			inverses.forEach(owlInverseProperty -> {

				String owlInverseId = owlInverseProperty.getIRI().getShortForm();
				final MutableNode inverseNode = new MutableNode(inversesNode, owlInverseId);
				inverseNode.setLabel(owlInverseId);
				inverseNode.setTarget(owlInverseId);
				decorateProperty(inverseNode);
			});
		}

		if (owlProperty.isOWLClass())
		{
			final List<OWLClass> subclasses = this.engine.getSubclasses(owlProperty).collect(toList());
			if (!subclasses.isEmpty())
			{
				final MutableNode subclassesNode = new MutableNode(relationNode, owlPropertyShortForm + "-subclasses");
				subclassesNode.setLabel("subclasses");
				decorateProperty(subclassesNode);
				subclasses.forEach(owlSubclass -> {

					String owlSubclassId = owlSubclass.getIRI().getShortForm();
					final MutableNode subclassNode = new MutableNode(subclassesNode, owlSubclassId);
					subclassNode.setLabel(owlSubclassId);
					subclassNode.setTarget(owlSubclassId);
					decorateProperty(subclassNode);
				});
			}

			final List<OWLClass> superclasses = this.engine.getSuperclasses(owlProperty).collect(toList());
			if (!superclasses.isEmpty())
			{
				final MutableNode superclassesNode = new MutableNode(relationNode, owlPropertyShortForm + "-superclasses");
				superclassesNode.setLabel("superclasses");
				decorateProperty(superclassesNode);
				superclasses.forEach(owlSuperclass -> {

					String owlSuperclassId = owlSuperclass.getIRI().getShortForm();
					final MutableNode superclassNode = new MutableNode(superclassesNode, owlSuperclassId);
					superclassNode.setLabel(owlSuperclassId);
					superclassNode.setTarget(owlSuperclassId);
					decorateProperty(superclassNode);
				});
			}
		}
	}

	/**
	 * Walk properties in iterator
	 *
	 * @param parentNode    treebolic parent node to attach to
	 * @param owlProperties property stream
	 */
	public void visitProperties(final TreeMutableNode parentNode, final Stream<OWLObjectProperty> owlProperties)
	{
		final List<INode> childNodes = owlProperties //

				.map(owlProperty -> {
					final String owlPropertyShortForm = owlProperty.getIRI().getShortForm();
					final String owlPropertyId = getName(owlPropertyShortForm);

					final MutableNode propertyNode = new MutableNode(null, owlPropertyId);
					propertyNode.setLabel(owlPropertyId);
					propertyNode.setTarget(owlPropertyId);
					propertyNode.setBackColor(this.propertyBackColor);
					propertyNode.setForeColor(this.propertyForeColor);
					propertyNode.setEdgeStyle(this.propertyEdgeStyle);
					propertyNode.setEdgeColor(this.propertyEdgeColor);
					if (this.propertyImageFile != null)
					{
						propertyNode.setImageFile(this.propertyImageFile);
					}
					else
					{
						propertyNode.setImageIndex(ImageIndices.PROPERTY.ordinal());
					}
					if (this.propertyEdgeImageFile != null)
					{
						propertyNode.setEdgeImageFile(this.propertyEdgeImageFile);
					}

					return propertyNode;
				}) //
				.collect(toList());

		// balance load
		final List<INode> balancedNodes = this.subLoadBalancer.buildHierarchy(childNodes, 0);
		parentNode.addChildren(balancedNodes);
	}

	// D E C O R A T E

	private MutableNode decorateRoot(final MutableNode node)
	{
		if (node.getLabel() == null)
		{
			node.setLabel(this.rootLabel);
		}
		node.setBackColor(this.rootBackColor);
		node.setForeColor(this.rootForeColor);
		if (this.rootImageFile != null)
		{
			node.setImageFile(this.rootImageFile);
		}
		else
		{
			node.setImageIndex(ImageIndices.ROOT.ordinal());
		}
		return node;
	}

	private MutableNode decorateClassWith(final MutableNode node, boolean hasInstances, boolean hasProperties, boolean isRelation)
	{
		if (hasInstances)
		{
			return decorateClassWithInstances(node);
		}
		else if (hasProperties)
		{
			return decorateClassWithProperties(node);
		}
		else if (isRelation)
		{
			return node;
		}
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateClassWithProperties(final MutableNode node)
	{
		node.setBackColor(this.classWithPropertiesBackColor);
		node.setForeColor(this.classWithPropertiesForeColor);
		if (this.classWithPropertiesImageFile != null)
		{
			node.setImageFile(this.classWithPropertiesImageFile);
		}
		else
		{
			node.setImageIndex(ImageIndices.CLASSWITHPROPERTIES.ordinal());
		}
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateClassWithInstances(final MutableNode node)
	{
		node.setBackColor(this.classWithInstancesBackColor);
		node.setForeColor(this.classWithInstancesForeColor);
		if (this.classWithInstancesImageFile != null)
		{
			node.setImageFile(this.classWithInstancesImageFile);
		}
		else
		{
			node.setImageIndex(ImageIndices.CLASSWITHINSTANCES.ordinal());
		}
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateProperties(final MutableNode node)
	{
		node.setLabel(this.propertiesLabel);
		node.setBackColor(this.propertiesBackColor);
		node.setForeColor(this.propertiesForeColor);
		if (this.propertiesImageFile != null)
		{
			node.setImageFile(this.propertiesImageFile);
		}
		else
		{
			node.setImageIndex(ImageIndices.PROPERTIES.ordinal());
		}
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateInstances(final MutableNode node)
	{
		node.setLabel(this.instancesLabel);
		node.setBackColor(this.instancesBackColor);
		node.setForeColor(this.instancesForeColor);
		if (this.instancesImageFile != null)
		{
			node.setImageFile(this.instancesImageFile);
		}
		else
		{
			node.setImageIndex(ImageIndices.INSTANCES.ordinal());
		}
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateInstance(final MutableNode node)
	{
		node.setBackColor(this.instanceBackColor);
		node.setForeColor(this.instanceForeColor);
		node.setEdgeStyle(this.instanceEdgeStyle);
		node.setEdgeColor(this.instanceEdgeColor);
		if (this.instanceImageFile != null)
		{
			node.setImageFile(this.instanceImageFile);
		}
		else
		{
			node.setImageIndex(ImageIndices.INSTANCE.ordinal());
		}
		if (this.instanceEdgeImageFile != null)
		{
			node.setEdgeImageFile(this.instanceEdgeImageFile);
		}
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateProperty(final MutableNode node)
	{
		node.setBackColor(this.propertyBackColor);
		node.setForeColor(this.propertyForeColor);
		node.setEdgeStyle(this.propertyEdgeStyle);
		node.setEdgeColor(this.propertyEdgeColor);
		if (this.propertyImageFile != null)
		{
			node.setImageFile(this.propertyImageFile);
		}
		else
		{
			node.setImageIndex(ImageIndices.PROPERTY.ordinal());
		}
		if (this.propertyEdgeImageFile != null)
		{
			node.setEdgeImageFile(this.propertyEdgeImageFile);
		}
		return node;
	}

	// H E L P E R S

	/**
	 * Get node label
	 *
	 * @param labelKey   label key
	 * @param labelValue label value
	 */
	private String getLabel(final String labelKey, final String labelValue)
	{
		return this.properties == null ? labelValue : this.properties.getProperty(labelKey, labelValue);
	}

	/**
	 * Get node color
	 *
	 * @param colorKey   forecolor key
	 * @param colorValue forecolor value
	 * @return color
	 */
	private Color getColor(final String colorKey, final Color colorValue)
	{
		final String colorString = this.properties == null ? null : this.properties.getProperty(colorKey);
		return colorString == null ? colorValue : Utils.stringToColor(colorString);
	}

	/**
	 * Set node image file
	 *
	 * @param imageKey image key
	 * @return image file or null
	 */
	private String getImageFile(final String imageKey)
	{
		return this.properties == null ? null : this.properties.getProperty(imageKey);
	}

	/**
	 * Get name
	 *
	 * @param uriString uri string
	 * @return name
	 */
	static String getName(final String uriString)
	{
		if (uriString != null)
		{
			final int index = uriString.lastIndexOf('#');
			if (index != -1)
			{
				return uriString.substring(index + 1);
			}
		}
		return uriString;
	}

	/**
	 * Annotations to string
	 *
	 * @param annotations stream of annotations
	 * @return string
	 */
	private static String annotationsToString(@NonNull final Stream<OWLAnnotation> annotations)
	{
		return annotations //
				.filter(a -> a.getValue() instanceof OWLLiteral) //
				.map(a -> (OWLLiteral) a.getValue()) //
				.map(OWLLiteral::getLiteral) //
				.map(s -> s.replaceAll("\n", "")) //
				.collect(Collectors.joining("<br>"));
	}

	/**
	 * Types to string
	 *
	 * @param types stream of types
	 * @return string
	 */
	private static String typesToString(@NonNull final Stream<OWLClassExpression> types)
	{
		return types //
				.map(Object::toString) //
				.collect(Collectors.joining("<br>"));
	}
}
