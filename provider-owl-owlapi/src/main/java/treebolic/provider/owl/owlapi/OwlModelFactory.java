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
import treebolic.annotations.Nullable;
import treebolic.glue.iface.Colors;
import treebolic.glue.iface.Image;
import treebolic.model.*;
import treebolic.provider.LoadBalancer;

import static java.util.stream.Collectors.toList;

/**
 * OWL model factory
 *
 * @author Bernard Bou
 */
public class OwlModelFactory implements ImageDecorator
{
	static private final Integer backgroundColor = 0xffffe0;

	/**
	 * As tree
	 */
	static final boolean asTree = false;

	// S T A T I C . D A T A

	/**
	 * Image indices
	 */
	public enum ImageIndex
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
		 * Class with relation attached
		 */
		CLASSWITHRELATION,
		/**
		 * Relation
		 */
		RELATION,
		/**
		 * Load balancing branch
		 */
		BRANCH,
		/**
		 * Load balancing instances branch
		 */
		BRANCH_INSTANCES,
		/**
		 * Load balancing properties branch
		 */
		BRANCH_PROPERTIES,
		/**
		 * Yields count of enumeration
		 */
		COUNT
	}

	protected static String[] images;

	/**
	 * Default class background color
	 */
	static final Integer defaultClassBackColor = 0xffffC0;

	/**
	 * Default class background color
	 */
	static final Integer defaultClassForeColor = Colors.DARK_GRAY;

	/**
	 * Default instance background color
	 */
	static final Integer defaultInstanceBackColor = Colors.WHITE;

	/**
	 * Default instance foreground color
	 */
	static final Integer defaultInstanceForeColor = Colors.BLUE;

	/**
	 * Default relation background color
	 */
	static final Integer defaultRelationBackColor = Colors.WHITE;

	/**
	 * Default relation foreground color
	 */
	static final Integer defaultRelationForeColor = Colors.RED;

	/**
	 * Default property background color
	 */
	static final Integer defaultPropertyBackColor = Colors.WHITE;

	/**
	 * Default property foreground color
	 */
	static final Integer defaultPropertyForeColor = Colors.MAGENTA;

	// L O A D B A L A N C I N G

	// class nodes

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
	protected static final Integer LOADBALANCING_BACKCOLOR = null;

	/**
	 * LoadBalancer (classes) : fore color
	 */
	protected static final Integer LOADBALANCING_FORECOLOR = null;

	/**
	 * LoadBalancer (classes) : edge color
	 */
	protected static final Integer LOADBALANCING_EDGECOLOR = Colors.DARK_GRAY;

	/**
	 * LoadBalancer (classes) : image index
	 */
	protected static final int LOADBALANCING_IMAGEINDEX = ImageIndex.BRANCH.ordinal();

	/**
	 * LoadBalancer (classes) : image
	 */
	protected static final Image LOADBALANCING_IMAGE = null;

	/**
	 * LoadBalancer (classes) : image file
	 */
	protected static final String LOADBALANCING_IMAGEFILE = "branch.png";

	/**
	 * LoadBalancer (classes) : Edge style
	 */
	protected static final int LOADBALANCING_EDGE_STYLE = IEdge.SOLID | /* IEdge.FROMDEF | IEdge.FROMCIRCLE | */IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.STROKEDEF | IEdge.TODEF;

	// instances

	/**
	 * LoadBalancer (instances and properties) : back color
	 */
	protected static final Integer LOADBALANCING_INSTANCES_BACKCOLOR = defaultInstanceBackColor;

	/**
	 * LoadBalancer (instances and properties) : fore color
	 */
	protected static final Integer LOADBALANCING_INSTANCES_FORECOLOR = defaultInstanceForeColor;

	/**
	 * LoadBalancer (instances and properties) : edge color
	 */
	protected static final Integer LOADBALANCING_INSTANCES_EDGECOLOR = defaultInstanceForeColor;

	/**
	 * LoadBalancer (instances and properties) : image index
	 */
	protected static final int LOADBALANCING_INSTANCES_IMAGEINDEX = ImageIndex.BRANCH_INSTANCES.ordinal();

	/**
	 * LoadBalancer (instances and properties) : image
	 */
	protected static final Image LOADBALANCING_INSTANCES_IMAGE = null;

	/**
	 * LoadBalancer (instances and properties) : image file
	 */
	protected static final String LOADBALANCING_INSTANCES_IMAGEFILE = "branch_instances.png";

	/**
	 * LoadBalancer (instances and properties) : Edge style
	 */
	protected static final int LOADBALANCING_INSTANCES_EDGE_STYLE = IEdge.DASH | /* IEdge.FROMDEF | IEdge.FROMCIRCLE | */IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.STROKEDEF | IEdge.TODEF;

	// properties

	/**
	 * LoadBalancer (instances and properties) : back color
	 */
	protected static final Integer LOADBALANCING_PROPERTIES_BACKCOLOR = defaultPropertyBackColor;

	/**
	 * LoadBalancer (instances and properties) : fore color
	 */
	protected static final Integer LOADBALANCING_PROPERTIES_FORECOLOR = defaultPropertyForeColor;

	/**
	 * LoadBalancer (instances and properties) : edge color
	 */
	protected static final Integer LOADBALANCING_PROPERTIES_EDGECOLOR = defaultPropertyForeColor;

	/**
	 * LoadBalancer (instances and properties) : image index
	 */
	protected static final int LOADBALANCING_PROPERTIES_IMAGEINDEX = ImageIndex.BRANCH_PROPERTIES.ordinal(); // -1;

	/**
	 * LoadBalancer (instances and properties) : image
	 */
	protected static final Image LOADBALANCING_PROPERTIES_IMAGE = null;

	/**
	 * LoadBalancer (instances and properties) : image file
	 */
	protected static final String LOADBALANCING_PROPERTIES_IMAGEFILE = "branch_properties.png";

	/**
	 * LoadBalancer (instances and properties) : Edge style
	 */
	protected static final int LOADBALANCING_PROPERTIES_EDGE_STYLE = IEdge.DASH | /* IEdge.FROMDEF | IEdge.FROMCIRCLE | */IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.STROKEDEF | IEdge.TODEF;

	// D E C O R A T I O N   M E M B E R S

	/**
	 * Properties
	 */
	private final Properties properties;

	// class

	/**
	 * Class back color
	 */
	private Integer classBackColor;

	/**
	 * Class fore color
	 */
	private Integer classForeColor;

	/**
	 * Class image
	 */
	private String classImageFile;

	/**
	 * Class with relation fore color
	 */
	private Integer classWithRelationBackColor;

	/**
	 * Class with relation fore color
	 */
	private Integer classWithRelationForeColor;

	/**
	 * Class with relation image
	 */
	private String classWithRelationImageFile;

	/**
	 * Class with instances fore color
	 */
	private Integer classWithInstancesBackColor;

	/**
	 * Class with instances fore color
	 */
	private Integer classWithInstancesForeColor;

	/**
	 * Class with instances image
	 */
	private String classWithInstancesImageFile;

	/**
	 * Class with properties fore color
	 */
	private Integer classWithPropertiesBackColor;

	/**
	 * Class with properties fore color
	 */
	private Integer classWithPropertiesForeColor;

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
	private Integer rootForeColor;

	/**
	 * Root fore color
	 */
	private Integer rootBackColor;

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
	private Integer instancesForeColor;

	/**
	 * Instances fore color
	 */
	private Integer instancesBackColor;

	/**
	 * 6 Instances image
	 */
	private String instancesImageFile;

	/**
	 * Instance fore color
	 */
	private Integer instanceForeColor;

	/**
	 * Instance fore color
	 */
	private Integer instanceBackColor;

	/**
	 * Instance image
	 */
	private String instanceImageFile;

	/**
	 * Instance edge color
	 */
	private Integer instanceEdgeColor;

	/**
	 * Instance edge style
	 */
	private Integer instanceEdgeStyle;

	/**
	 * Instance edge image file
	 */
	private String instanceEdgeImageFile;

	/**
	 * Instance fore color
	 */
	private Integer relationForeColor;

	/**
	 * Relation fore color
	 */
	private Integer relationBackColor;

	/**
	 * Relation image
	 */
	private String relationImageFile;

	/**
	 * Relation edge color
	 */
	private Integer relationEdgeColor;

	/**
	 * Relation edge style
	 */
	private Integer relationEdgeStyle;

	/**
	 * Relation edge image file
	 */
	private String relationEdgeImageFile;

	// properties

	/**
	 * Properties label
	 */
	private String propertiesLabel;

	/**
	 * Properties fore color
	 */
	private Integer propertiesForeColor;

	/**
	 * Properties fore color
	 */
	private Integer propertiesBackColor;

	/**
	 * Properties image
	 */
	private String propertiesImageFile;

	/**
	 * Property fore color
	 */
	private Integer propertyForeColor;

	/**
	 * Property fore color
	 */
	private Integer propertyBackColor;

	/**
	 * Property image
	 */
	private String propertyImageFile;

	/**
	 * Property edge color
	 */
	private Integer propertyEdgeColor;

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
	 * Load balancer (instances)
	 */
	protected final LoadBalancer instancesLoadBalancer;

	/**
	 * Load balancer (properties)
	 */
	protected final LoadBalancer propertiesLoadBalancer;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param properties properties
	 */
	public OwlModelFactory(final Properties properties)
	{
		this.properties = properties;
		manager = OWLManager.createOWLOntologyManager();
		dataFactory = manager.getOWLDataFactory();

		url = null;
		ontology = null;
		engine = null;
		parser = null;

		loadBalancer = new LoadBalancer(MAX_AT_LEVEL, LABEL_TRUNCATE_AT);
		loadBalancer.setGroupNode(null, LOADBALANCING_BACKCOLOR, LOADBALANCING_FORECOLOR, LOADBALANCING_EDGECOLOR, LOADBALANCING_EDGE_STYLE, -1, LOADBALANCING_IMAGE, LOADBALANCING_IMAGEFILE);
		instancesLoadBalancer = new LoadBalancer(MAX_AT_LEVEL, LABEL_TRUNCATE_AT);
		instancesLoadBalancer.setGroupNode(null, LOADBALANCING_INSTANCES_BACKCOLOR, LOADBALANCING_INSTANCES_FORECOLOR, LOADBALANCING_INSTANCES_EDGECOLOR, LOADBALANCING_INSTANCES_EDGE_STYLE, -1, LOADBALANCING_INSTANCES_IMAGE, LOADBALANCING_INSTANCES_IMAGEFILE);
		propertiesLoadBalancer = new LoadBalancer(MAX_AT_LEVEL, LABEL_TRUNCATE_AT);
		propertiesLoadBalancer.setGroupNode(null, LOADBALANCING_PROPERTIES_BACKCOLOR, LOADBALANCING_PROPERTIES_FORECOLOR, LOADBALANCING_PROPERTIES_EDGECOLOR, LOADBALANCING_PROPERTIES_EDGE_STYLE, -1, LOADBALANCING_PROPERTIES_IMAGE, LOADBALANCING_PROPERTIES_IMAGEFILE);

		initialize();
	}

	/**
	 * Initialize from properties
	 */
	void initialize()
	{
		rootBackColor = getColor("root.backcolor", Colors.ORANGE);
		rootForeColor = getColor("root.forecolor", Colors.BLACK);
		rootImageFile = getImageFile("root.image");
		rootLabel = getLabel("root.label", "Thing");

		classBackColor = getColor("class.backcolor", OwlModelFactory.defaultClassBackColor);
		classForeColor = getColor("class.forecolor", OwlModelFactory.defaultClassForeColor);
		classImageFile = getImageFile("class.image");

		classWithPropertiesBackColor = getColor("class.withprops.backcolor", OwlModelFactory.defaultPropertyBackColor);
		classWithPropertiesForeColor = getColor("class.withprops.forecolor", OwlModelFactory.defaultPropertyForeColor);
		classWithPropertiesImageFile = getImageFile("class.withprops.image");

		classWithInstancesBackColor = getColor("class.withinstances.backcolor", OwlModelFactory.defaultInstanceBackColor);
		classWithInstancesForeColor = getColor("class.withinstances.forecolor", OwlModelFactory.defaultInstanceForeColor);
		classWithInstancesImageFile = getImageFile("class.withinstances.image");

		classWithRelationBackColor = getColor("class.withrelation.backcolor", OwlModelFactory.defaultRelationBackColor);
		classWithRelationForeColor = getColor("class.withrelation.forecolor", OwlModelFactory.defaultRelationForeColor);
		classWithRelationImageFile = getImageFile("class.withrelation.image");

		instancesLabel = getLabel("instances.label", "instances");
		instancesBackColor = getColor("instances.backcolor", OwlModelFactory.defaultInstanceBackColor);
		instancesForeColor = getColor("instances.forecolor", OwlModelFactory.defaultInstanceForeColor);
		instancesImageFile = getImageFile("instances.image");

		instanceBackColor = getColor("instance.backcolor", OwlModelFactory.defaultInstanceBackColor);
		instanceForeColor = getColor("instance.forecolor", OwlModelFactory.defaultInstanceForeColor);
		instanceEdgeColor = getColor("instance.edgecolor", OwlModelFactory.defaultInstanceForeColor);
		instanceImageFile = getImageFile("instance.image");
		instanceEdgeImageFile = getImageFile("instance.edge.image");
		instanceEdgeStyle = IEdge.DOT | IEdge.TOTRIANGLE | IEdge.TOFILL;

		relationBackColor = getColor("relation.backcolor", OwlModelFactory.defaultRelationBackColor);
		relationForeColor = getColor("relation.forecolor", OwlModelFactory.defaultRelationForeColor);
		relationEdgeColor = getColor("relation.edgecolor", OwlModelFactory.defaultRelationForeColor);
		relationImageFile = getImageFile("relation.image");
		relationEdgeImageFile = getImageFile("relation.edge.image");
		relationEdgeStyle = IEdge.SOLID | IEdge.TOTRIANGLE | IEdge.TOFILL;

		propertiesLabel = getLabel("properties.label", "properties");
		propertiesBackColor = getColor("properties.backcolor", defaultPropertyBackColor);
		propertiesForeColor = getColor("properties.forecolor", defaultPropertyForeColor);
		propertiesImageFile = getImageFile("properties.image");

		propertyBackColor = getColor("property.backcolor", defaultPropertyBackColor);
		propertyForeColor = getColor("property.forecolor", defaultPropertyForeColor);
		propertyEdgeColor = getColor("property.edge.color", defaultPropertyForeColor);
		propertyImageFile = getImageFile("property.image");
		propertyEdgeImageFile = getImageFile("property.edge.image");
		propertyEdgeStyle = IEdge.DOT | IEdge.TOTRIANGLE | IEdge.TOFILL;

		initializeImages();
	}

	/**
	 * Initialize from properties
	 */
	protected void initializeImages()
	{
		OwlModelFactory.images = new String[ImageIndex.COUNT.ordinal()];
		images[ImageIndex.ROOT.ordinal()] = "root.png";
		images[ImageIndex.CLASS.ordinal()] = "class.png";
		images[ImageIndex.CLASSWITHINSTANCES.ordinal()] = "classwithinstances.png";
		images[ImageIndex.INSTANCES.ordinal()] = "instances.png";
		images[ImageIndex.INSTANCE.ordinal()] = "instance.png";
		images[ImageIndex.CLASSWITHPROPERTIES.ordinal()] = "classwithproperties.png";
		images[ImageIndex.PROPERTIES.ordinal()] = "properties.png";
		images[ImageIndex.PROPERTY.ordinal()] = "property.png";
		images[ImageIndex.CLASSWITHRELATION.ordinal()] = "classwithrelation.png";
		images[ImageIndex.RELATION.ordinal()] = "relation.png";
		images[ImageIndex.BRANCH.ordinal()] = "branch.png";
		images[ImageIndex.BRANCH_INSTANCES.ordinal()] = "branch_instances.png";
		images[ImageIndex.BRANCH_PROPERTIES.ordinal()] = "branch_properties.png";
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
			return manager.loadOntologyFromOntologyDocument(new StreamDocumentSource(is));
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
		settings.backColor = OwlModelFactory.backgroundColor;
		settings.treeEdgeColor = Colors.BLACK;
		settings.treeEdgeStyle = IEdge.SOLID | IEdge.TOTRIANGLE | IEdge.TOFILL;
		settings.orientation = OwlModelFactory.asTree ? "south" : "radial";

		settings.fontFace = "SansSerif";
		settings.fontSize = 15;
		settings.expansion = .9F;
		settings.sweep = 1.2F;

		settings.hasToolbarFlag = true;
		settings.hasStatusbarFlag = true;
		settings.focusOnHoverFlag = false;

		if (OwlModelFactory.asTree)
		{
			settings.yMoveTo = -0.3F;
		}

		// override
		if (properties != null)
		{
			try
			{
				settings.load(properties);
			}
			catch (final Exception e)
			{
				System.err.println("SETTINGS: " + e);
			}
		}

		return new Model(tree, settings);
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
		if (!ontologyUrlString.equals(url) || ontology == null || engine == null || parser == null)
		{
			try
			{
				ontology = getOntology(ontologyUrlString);
				url = ontologyUrlString;
				// System.out.println("Loaded ontology: " + ontology.getOntologyID());

				engine = new QueryEngine(ontology);
				parser = new QueryParser(ontology, new SimpleShortFormProvider());
			}
			catch (final Exception e)
			{
				System.err.println("OWL:" + e);
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
				owlClass = dataFactory.getOWLClass(IRI.create(classIri));
			}
			else
			{
				owlClass = parser.parseClassExpression(classShortForm).asOWLClass();
			}

			// class
			if (owlClass != null)
			{
				if (target != null)
				{
					boolean hasInstances = target.contains("instances");
					boolean hasProperties = target.contains("properties");
					boolean isRelation = target.contains("relation");

					// root
					final MutableNode owlClassNode = new MutableNode(null, "root");
					owlClassNode.setLabel(owlClass.getIRI().getShortForm());
					decorateClassWith(owlClassNode, hasInstances, hasProperties, isRelation);
					owlClassNode.setEdgeColor(defaultClassForeColor);

					// instances
					if (hasInstances)
					{
						// instances root
						final TreeMutableNode instancesNode = new TreeMutableNode(owlClassNode, classIri + "-instances");
						decorateInstances(instancesNode);
						instancesNode.setEdgeColor(defaultInstanceForeColor);

						// instances
						final Stream<OWLNamedIndividual> instances = engine.getInstances(owlClass);
						visitInstances(instancesNode, instances.sorted());
					}
					// relation
					if (isRelation)
					{
						// relation
						OWLObjectProperty relation = engine.getRelation(owlClass);
						visitRelation(owlClassNode, relation);
					}
					// properties
					if (hasProperties)
					{
						// properties root
						final TreeMutableNode propertiesNode = new TreeMutableNode(owlClassNode, classIri + "-properties");
						decorateProperties(propertiesNode);
						propertiesNode.setEdgeColor(defaultPropertyForeColor);

						// properties
						final Stream<OWLObjectProperty> properties = engine.getProperties(owlClass);
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
			final OWLClass rootClass = engine.getTopClass();
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
					final Stream<OWLClass> owlSubClasses = engine.getSubClasses(owlClass);
					visitClasses(owlClassNode, owlSubClasses, ontologyUrlString);

					return owlClassNode;
				}) //
				.collect(toList());

		// balance load
		final List<INode> balancedNodes = loadBalancer.buildHierarchy(childNodes, 0);
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
		final String owlClassId = owlClass.getIRI().getShortForm();
		final Stream<OWLAnnotation> annotations = engine.getAnnotations(owlClass);

		// comment
		String comment = owlClass.getIRI() + "<br>" + annotationsToString(annotations);

		// node
		final TreeMutableNode owlClassNode = new TreeMutableNode(parentOwlClassNode, owlClassId);
		owlClassNode.setLabel(ownClassShortForm);
		owlClassNode.setTarget(owlClassId);
		owlClassNode.setContent(comment);
		decorateClass(owlClassNode);

		// mounts
		if (!owlClass.isOWLThing())
		{
			// get instances or properties
			final Stream<OWLNamedIndividual> instances = engine.getInstances(owlClass);
			final Stream<OWLObjectProperty> properties = engine.getProperties(owlClass);
			final boolean hasInstances = instances.findAny().isPresent();
			final boolean hasProperties = properties.findAny().isPresent();
			final boolean isRelation = engine.isRelation(owlClass.asOWLClass());

			// mountpoint
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
		final Stream<OWLClass> owlSubClasses = engine.getSubClasses(owlClass);
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

					final String owlIndividualShortForm = owlNamedIndividual.getIRI().getShortForm();
					final String owlIndividualId = owlNamedIndividual.getIRI().getShortForm();
					final Stream<OWLClassExpression> types = engine.getTypes(owlNamedIndividual);
					final Stream<OWLAnnotation> annotations = engine.getAnnotations(owlNamedIndividual);

					final MutableNode instanceNode = new MutableNode(null, owlIndividualId);
					instanceNode.setLabel(owlIndividualShortForm);
					instanceNode.setTarget(owlIndividualId);
					instanceNode.setContent(typesToString(types) + "<br>" + annotationsToString(annotations) + "<br>");
					decorateInstance(instanceNode);
					return instanceNode;
				}) //
				.collect(toList());

		// balance load
		final List<INode> balancedNodes = instancesLoadBalancer.buildHierarchy(childNodes, 0);
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
		relationNode.setEdgeLabel("is relation");
		decorateRelation(relationNode);

		final List<OWLClass> domains = engine.getDomains(owlProperty).collect(toList());
		if (!domains.isEmpty())
		{
			final MutableNode domainsNode = new MutableNode(relationNode, owlPropertyShortForm + "-domains");
			domainsNode.setLabel("domains");
			decorateRelation(domainsNode);
			domains.forEach(owlDomainClass -> {

				String owlClassId = owlDomainClass.asOWLClass().getIRI().getShortForm();
				final MutableNode domainNode = new MutableNode(domainsNode, owlClassId);
				domainNode.setLabel(owlClassId);
				domainNode.setTarget(owlClassId);
				decorateRelation(domainNode);
			});
		}

		final List<OWLClass> ranges = engine.getRanges(owlProperty).collect(toList());
		if (!ranges.isEmpty())
		{
			final MutableNode rangesNode = new MutableNode(relationNode, owlPropertyShortForm + "-ranges");
			rangesNode.setLabel("ranges");
			decorateRelation(rangesNode);
			ranges.forEach(owlRangeClass -> {

				String owlClassId = owlRangeClass.asOWLClass().getIRI().getShortForm();
				final MutableNode rangeNode = new MutableNode(rangesNode, owlClassId);
				rangeNode.setLabel(owlClassId);
				rangeNode.setTarget(owlClassId);
				decorateRelation(rangeNode);
			});
		}

		final List<OWLObjectProperty> subproperties = engine.getSubproperties(owlProperty).collect(toList());
		if (!subproperties.isEmpty())
		{
			final MutableNode subPropertiesNode = new MutableNode(relationNode, owlPropertyShortForm + "-subproperties");
			subPropertiesNode.setLabel("subproperties");
			decorateRelation(subPropertiesNode);
			subproperties.forEach(owlSubProperty -> {

				String owlSubpropertyId = owlSubProperty.getIRI().getShortForm();
				final MutableNode subPropertyNode = new MutableNode(subPropertiesNode, owlSubpropertyId);
				subPropertyNode.setLabel(owlSubpropertyId);
				subPropertyNode.setTarget(owlSubpropertyId);
				decorateRelation(subPropertyNode);
			});
		}

		final List<OWLObjectProperty> inverses = engine.getInverseProperties(owlProperty).collect(toList());
		if (!inverses.isEmpty())
		{
			final MutableNode inversesNode = new MutableNode(relationNode, owlPropertyShortForm + "-inverses");
			inversesNode.setLabel("inverses");
			decorateRelation(inversesNode);
			inverses.forEach(owlInverseProperty -> {

				String owlInverseId = owlInverseProperty.getIRI().getShortForm();
				final MutableNode inverseNode = new MutableNode(inversesNode, owlInverseId);
				inverseNode.setLabel(owlInverseId);
				inverseNode.setTarget(owlInverseId);
				decorateRelation(inverseNode);
			});
		}

		if (owlProperty.isOWLClass())
		{
			final List<OWLClass> subclasses = engine.getSubclasses(owlProperty).collect(toList());
			if (!subclasses.isEmpty())
			{
				final MutableNode subclassesNode = new MutableNode(relationNode, owlPropertyShortForm + "-subclasses");
				subclassesNode.setLabel("subclasses");
				decorateRelation(subclassesNode);
				subclasses.forEach(owlSubclass -> {

					String owlSubclassId = owlSubclass.getIRI().getShortForm();
					final MutableNode subclassNode = new MutableNode(subclassesNode, owlSubclassId);
					subclassNode.setLabel(owlSubclassId);
					subclassNode.setTarget(owlSubclassId);
					decorateRelation(subclassNode);
				});
			}

			final List<OWLClass> superclasses = engine.getSuperclasses(owlProperty).collect(toList());
			if (!superclasses.isEmpty())
			{
				final MutableNode superclassesNode = new MutableNode(relationNode, owlPropertyShortForm + "-superclasses");
				superclassesNode.setLabel("superclasses");
				decorateRelation(superclassesNode);
				superclasses.forEach(owlSuperclass -> {

					String owlSuperclassId = owlSuperclass.getIRI().getShortForm();
					final MutableNode superclassNode = new MutableNode(superclassesNode, owlSuperclassId);
					superclassNode.setLabel(owlSuperclassId);
					superclassNode.setTarget(owlSuperclassId);
					decorateRelation(superclassNode);
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
					final String owlPropertyId = owlProperty.getIRI().getShortForm();

					final MutableNode propertyNode = new MutableNode(null, owlPropertyId);
					propertyNode.setLabel(owlPropertyShortForm);
					propertyNode.setTarget(owlPropertyId);
					decorateProperty(propertyNode);
					return propertyNode;
				}) //
				.collect(toList());

		// balance load
		final List<INode> balancedNodes = propertiesLoadBalancer.buildHierarchy(childNodes, 0);
		parentNode.addChildren(balancedNodes);
	}

	// D E C O R A T E

	private void setNodeImage(final MutableNode node, @Nullable final String imageFile, @Nullable final ImageIndex index)
	{
		if (imageFile != null)
		{
			node.setImageFile(imageFile);
		}
		else if (index != null)
		{
			setNodeImage(node, index.ordinal());
		}
	}

	public void setNodeImage(final MutableNode node, final int index)
	{
		if (index != -1)
		{
			node.setImageFile(images[index]);
		}
	}

	private void setTreeEdgeImage(final MutableNode node, @Nullable final String edgeImageFile, @Nullable final ImageIndex index)
	{
		if (edgeImageFile != null)
		{
			node.setEdgeImageFile(edgeImageFile);
		}
		else if (index != null)
		{
			setTreeEdgeImage(node, index.ordinal());
		}
	}

	public void setTreeEdgeImage(final MutableNode node, final int index)
	{
		if (index != -1)
		{
			node.setEdgeImageFile(images[index]);
		}
	}

	@Override
	public void setEdgeImage(final MutableEdge edge, final int index)
	{
		if (index != -1)
		{
			edge.setImageFile(images[index]);
		}
	}

	private MutableNode decorateRoot(final MutableNode node)
	{
		if (node.getLabel() == null)
		{
			node.setLabel(rootLabel);
		}
		node.setBackColor(rootBackColor);
		node.setForeColor(rootForeColor);
		setNodeImage(node, rootImageFile, ImageIndex.ROOT);
		return node;
	}

	private MutableNode decorateClass(final MutableNode node)
	{
		node.setBackColor(classBackColor);
		node.setForeColor(classForeColor);
		setNodeImage(node, classImageFile, ImageIndex.CLASS);
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateClassWith(final MutableNode node, boolean hasInstances, boolean hasProperties, boolean isRelation)
	{
		if (isRelation)
		{
			return decorateClassWithRelation(node);
		}
		else if (hasInstances)
		{
			return decorateClassWithInstances(node);
		}
		else if (hasProperties)
		{
			return decorateClassWithProperties(node);
		}
		return decorateClass(node);
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateClassWithRelation(final MutableNode node)
	{
		node.setBackColor(classWithRelationBackColor);
		node.setForeColor(classWithRelationForeColor);
		setNodeImage(node, classWithRelationImageFile, ImageIndex.CLASSWITHRELATION);
		return node;
	}


	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateClassWithInstances(final MutableNode node)
	{
		node.setBackColor(classWithInstancesBackColor);
		node.setForeColor(classWithInstancesForeColor);
		setNodeImage(node, classWithInstancesImageFile, ImageIndex.CLASSWITHINSTANCES);
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateClassWithProperties(final MutableNode node)
	{
		node.setBackColor(classWithPropertiesBackColor);
		node.setForeColor(classWithPropertiesForeColor);
		setNodeImage(node, classWithPropertiesImageFile, ImageIndex.CLASSWITHPROPERTIES);
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateProperties(final MutableNode node)
	{
		node.setLabel(propertiesLabel);
		node.setBackColor(propertiesBackColor);
		node.setForeColor(propertiesForeColor);
		node.setEdgeColor(propertyEdgeColor);
		setNodeImage(node, propertiesImageFile, ImageIndex.PROPERTIES);
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateInstances(final MutableNode node)
	{
		node.setLabel(instancesLabel);
		node.setBackColor(instancesBackColor);
		node.setForeColor(instancesForeColor);
		node.setEdgeColor(instanceEdgeColor);
		setNodeImage(node, instancesImageFile, ImageIndex.INSTANCES);
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateInstance(final MutableNode node)
	{
		node.setBackColor(instanceBackColor);
		node.setForeColor(instanceForeColor);
		node.setEdgeStyle(instanceEdgeStyle);
		node.setEdgeColor(instanceEdgeColor);
		setNodeImage(node, instanceImageFile, ImageIndex.INSTANCE);
		setTreeEdgeImage(node, instanceEdgeImageFile, null);
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateRelation(final MutableNode node)
	{
		node.setBackColor(relationBackColor);
		node.setForeColor(relationForeColor);
		node.setEdgeStyle(relationEdgeStyle);
		node.setEdgeColor(relationEdgeColor);
		setNodeImage(node, relationImageFile, ImageIndex.RELATION);
		setTreeEdgeImage(node, relationEdgeImageFile, null);
		return node;
	}

	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateProperty(final MutableNode node)
	{
		node.setBackColor(propertyBackColor);
		node.setForeColor(propertyForeColor);
		node.setEdgeStyle(propertyEdgeStyle);
		node.setEdgeColor(propertyEdgeColor);
		setNodeImage(node, propertyImageFile, ImageIndex.PROPERTY);
		setTreeEdgeImage(node, propertyEdgeImageFile, null);
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
		return properties == null ? labelValue : properties.getProperty(labelKey, labelValue);
	}

	/**
	 * Get node color
	 *
	 * @param colorKey   forecolor key
	 * @param colorValue forecolor value
	 * @return color
	 */
	private Integer getColor(final String colorKey, final Integer colorValue)
	{
		final String colorString = properties == null ? null : properties.getProperty(colorKey);
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
		return properties == null ? null : properties.getProperty(imageKey);
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
