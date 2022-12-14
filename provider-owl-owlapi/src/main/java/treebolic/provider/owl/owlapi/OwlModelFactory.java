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
	 * LoadBalancer : Max children nodes at level 0, 1 ... n. Level 0 is just above leaves. Level > 0 is upward from leaves. Last value 'i' holds for level 'i' to 'n'.
	 */
	static private final int[] MAX_AT_LEVEL = {8, 3};

	/**
	 * LoadBalancer : Truncation threshold
	 */
	static private final int LABEL_TRUNCATE_AT = 3;

	/**
	 * LoadBalancer (classes) : back color
	 */
	@Nullable
	protected static final Integer LOADBALANCING_BACKCOLOR = null;

	/**
	 * LoadBalancer (classes) : fore color
	 */
	@Nullable
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
	@Nullable
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
	 * LoadBalancer (instances) : back color
	 */
	protected static final Integer LOADBALANCING_INSTANCES_BACKCOLOR = defaultInstanceBackColor;

	/**
	 * LoadBalancer (instances) : fore color
	 */
	protected static final Integer LOADBALANCING_INSTANCES_FORECOLOR = defaultInstanceForeColor;

	/**
	 * LoadBalancer (instances) : edge color
	 */
	protected static final Integer LOADBALANCING_INSTANCES_EDGECOLOR = defaultInstanceForeColor;

	/**
	 * LoadBalancer (instances) : image index
	 */
	protected static final int LOADBALANCING_INSTANCES_IMAGEINDEX = ImageIndex.BRANCH_INSTANCES.ordinal();

	/**
	 * LoadBalancer (instances) : image
	 */
	@Nullable
	protected static final Image LOADBALANCING_INSTANCES_IMAGE = null;

	/**
	 * LoadBalancer (instances) : image file
	 */
	protected static final String LOADBALANCING_INSTANCES_IMAGEFILE = "branch_instances.png";

	/**
	 * LoadBalancer (instances) : Edge style
	 */
	protected static final int LOADBALANCING_INSTANCES_EDGE_STYLE = IEdge.DASH | /* IEdge.FROMDEF | IEdge.FROMCIRCLE | */IEdge.TOTRIANGLE | IEdge.TOFILL | IEdge.STROKEDEF | IEdge.TODEF;

	// properties

	/**
	 * LoadBalancer (properties) : back color
	 */
	protected static final Integer LOADBALANCING_PROPERTIES_BACKCOLOR = defaultPropertyBackColor;

	/**
	 * LoadBalancer (properties) : fore color
	 */
	protected static final Integer LOADBALANCING_PROPERTIES_FORECOLOR = defaultPropertyForeColor;

	/**
	 * LoadBalancer (properties) : edge color
	 */
	protected static final Integer LOADBALANCING_PROPERTIES_EDGECOLOR = defaultPropertyForeColor;

	/**
	 * LoadBalancer (properties) : image index
	 */
	protected static final int LOADBALANCING_PROPERTIES_IMAGEINDEX = ImageIndex.BRANCH_PROPERTIES.ordinal(); // -1;

	/**
	 * LoadBalancer (properties) : image
	 */
	@Nullable
	protected static final Image LOADBALANCING_PROPERTIES_IMAGE = null;

	/**
	 * LoadBalancer (properties) : image file
	 */
	protected static final String LOADBALANCING_PROPERTIES_IMAGEFILE = "branch_properties.png";

	/**
	 * LoadBalancer (properties) : Edge style
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
	@Nullable
	private Integer classBackColor;

	/**
	 * Class fore color
	 */
	@Nullable
	private Integer classForeColor;

	/**
	 * Class image
	 */
	@Nullable
	private String classImageFile;

	/**
	 * Class with relation fore color
	 */
	@Nullable
	private Integer classWithRelationBackColor;

	/**
	 * Class with relation fore color
	 */
	@Nullable
	private Integer classWithRelationForeColor;

	/**
	 * Class with relation image
	 */
	@Nullable
	private String classWithRelationImageFile;

	/**
	 * Class with instances fore color
	 */
	@Nullable
	private Integer classWithInstancesBackColor;

	/**
	 * Class with instances fore color
	 */
	@Nullable
	private Integer classWithInstancesForeColor;

	/**
	 * Class with instances image
	 */
	@Nullable
	private String classWithInstancesImageFile;

	/**
	 * Class with properties fore color
	 */
	@Nullable
	private Integer classWithPropertiesBackColor;

	/**
	 * Class with properties fore color
	 */
	@Nullable
	private Integer classWithPropertiesForeColor;

	/**
	 * Class with properties image
	 */
	@Nullable
	private String classWithPropertiesImageFile;

	// root

	/**
	 * Root label
	 */
	private String rootLabel;

	/**
	 * Root fore color
	 */
	@Nullable
	private Integer rootForeColor;

	/**
	 * Root fore color
	 */
	@Nullable
	private Integer rootBackColor;

	/**
	 * Root image
	 */
	@Nullable
	private String rootImageFile;

	// instances

	/**
	 * Instances label
	 */
	private String instancesLabel;

	/**
	 * Instances fore color
	 */
	@Nullable
	private Integer instancesForeColor;

	/**
	 * Instances fore color
	 */
	@Nullable
	private Integer instancesBackColor;

	/**
	 * 6 Instances image
	 */
	@Nullable
	private String instancesImageFile;

	/**
	 * Instance fore color
	 */
	@Nullable
	private Integer instanceForeColor;

	/**
	 * Instance fore color
	 */
	@Nullable
	private Integer instanceBackColor;

	/**
	 * Instance image
	 */
	@Nullable
	private String instanceImageFile;

	/**
	 * Instance edge color
	 */
	@Nullable
	private Integer instanceEdgeColor;

	/**
	 * Instance edge style
	 */
	private Integer instanceEdgeStyle;

	/**
	 * Instance edge image file
	 */
	@Nullable
	private String instanceEdgeImageFile;

	/**
	 * Instance fore color
	 */
	@Nullable
	private Integer relationForeColor;

	/**
	 * Relation fore color
	 */
	@Nullable
	private Integer relationBackColor;

	/**
	 * Relation image
	 */
	@Nullable
	private String relationImageFile;

	/**
	 * Relation edge color
	 */
	@Nullable
	private Integer relationEdgeColor;

	/**
	 * Relation edge style
	 */
	private Integer relationEdgeStyle;

	/**
	 * Relation edge image file
	 */
	@Nullable
	private String relationEdgeImageFile;

	// properties

	/**
	 * Properties label
	 */
	private String propertiesLabel;

	/**
	 * Properties fore color
	 */
	@Nullable
	private Integer propertiesForeColor;

	/**
	 * Properties fore color
	 */
	@Nullable
	private Integer propertiesBackColor;

	/**
	 * Properties image
	 */
	@Nullable
	private String propertiesImageFile;

	/**
	 * Property fore color
	 */
	@Nullable
	private Integer propertyForeColor;

	/**
	 * Property fore color
	 */
	@Nullable
	private Integer propertyBackColor;

	/**
	 * Property image
	 */
	@Nullable
	private String propertyImageFile;

	/**
	 * Property edge color
	 */
	@Nullable
	private Integer propertyEdgeColor;

	/**
	 * Property edge style
	 */
	private Integer propertyEdgeStyle;

	/**
	 * Property edge image file
	 */
	@Nullable
	private String propertyEdgeImageFile;

	// M E M B E R S

	/**
	 * Ontology
	 */
	@Nullable
	private OWLOntology ontology;

	/**
	 * Engine
	 */
	@Nullable
	private QueryParser parser;

	/**
	 * Engine
	 */
	@Nullable
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
	@Nullable
	private String url;

	/**
	 * Load balancer (classes)
	 */
	@NonNull
	protected final LoadBalancer loadBalancer;

	/**
	 * Load balancer (instances)
	 */
	@NonNull
	protected final LoadBalancer instancesLoadBalancer;

	/**
	 * Load balancer (properties)
	 */
	@NonNull
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
	private OWLOntology getOntology(@NonNull final String ontologyDocumentUrl) throws MalformedURLException, IOException, OWLOntologyCreationException
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
	@Nullable
	public Model makeModel(final String ontologyUrlString)
	{
		@Nullable final Tree tree = makeTree(ontologyUrlString);
		if (tree == null)
		{
			return null;
		}

		@NonNull final Settings settings = new Settings();
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
	@Nullable
	public Tree makeTree(final String urlString)
	{
		// parameter and url
		@NonNull final Map<String, String> parse = parseUrl(urlString);
		final String argUrl = parse.get("url");
		final String argClassIri = parse.get("iri");
		final String argClassName = parse.get("class");
		final String target = parse.get("target");

		if (argUrl == null)
		{
			return null;
		}

		// load document
		if (!argUrl.equals(url) || ontology == null || engine == null || parser == null)
		{
			try
			{
				ontology = getOntology(argUrl);
				if (ontology == null)
				{
					throw new RuntimeException("Null ontology");
				}
				url = argUrl;
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
		OWLClass clazz;
		if (argClassIri != null || argClassName != null)
		{
			// parse
			if (argClassIri != null)
			{
				clazz = dataFactory.getOWLClass(IRI.create(argClassIri));
			}
			else
			{
				clazz = parser.parseClassExpression(argClassName).asOWLClass();
			}

			// class
			if (clazz != null)
			{
				if (target != null)
				{
					boolean hasInstances = target.contains("instances");
					boolean hasProperties = target.contains("properties");
					boolean isRelation = target.contains("relation");

					// root
					@NonNull final MutableNode classNode = new MutableNode(null, "root");
					classNode.setLabel(clazz.getIRI().getShortForm() + "\nroot");
					decorateClassWith(classNode, hasInstances, hasProperties, isRelation);
					classNode.setEdgeColor(defaultClassForeColor);

					// instances
					if (hasInstances)
					{
						// instances root
						@NonNull final TreeMutableNode instancesNode = new TreeMutableNode(classNode, argClassIri + "-instances");
						decorateInstances(instancesNode);
						instancesNode.setEdgeColor(defaultInstanceForeColor);

						// instances
						final Stream<OWLNamedIndividual> instances = engine.getInstances(clazz);
						visitInstances(instancesNode, instances.sorted());
					}
					// relation
					if (isRelation)
					{
						// relation
						OWLObjectProperty relation = engine.getRelation(clazz);
						visitRelation(classNode, relation);
					}
					// properties
					if (hasProperties)
					{
						// properties root
						@NonNull final TreeMutableNode propertiesNode = new TreeMutableNode(classNode, argClassIri + "-properties");
						decorateProperties(propertiesNode);
						propertiesNode.setEdgeColor(defaultPropertyForeColor);

						// properties
						@NonNull final Stream<OWLObjectProperty> properties = engine.getProperties(clazz);
						visitProperties(propertiesNode, properties);
					}
					return new Tree(classNode, null);
				}
				// class
				@NonNull final MutableNode classNode = visitClassAndSubclasses(null, clazz, argUrl);
				return new Tree(decorateRoot(classNode), null);
			}
			return null;
		}
		else
		{
			// walk classes
			@Nullable final OWLClass rootClass = engine.getTopClass();
			if (rootClass == null)
			{
				return null;
			}
			@NonNull final MutableNode rootNode = visitClassAndSubclasses(null, rootClass, argUrl);
			rootNode.setLabel(rootNode.getLabel() + "\nroot");
			return new Tree(decorateRoot(rootNode), null);
		}
	}

	/**
	 * Make tree
	 *
	 * @param ontologyUrl0 url string
	 * @return tree if successful
	 */
	@NonNull
	public Map<String, String> parseUrl(final String ontologyUrl0)
	{
		@NonNull final Map<String, String> map = new HashMap<>();

		String ontologyUrl = ontologyUrl0;

		// parameter and url
		@Nullable String parameters = null;
		final int index = ontologyUrl.indexOf('?');
		if (index != -1)
		{
			parameters = ontologyUrl.substring(index + 1);
			ontologyUrl = ontologyUrl.substring(0, index);
		}
		map.put("url", ontologyUrl);

		// parameters
		if (parameters != null)
		{
			@NonNull final String[] fields = parameters.split("&");
			for (@NonNull final String field : fields)
			{
				@NonNull final String[] nameValue = field.split("=");
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
	 * @param classes           class stream
	 * @param ontologyUrlString URL string
	 */
	public void visitClasses(@NonNull final TreeMutableNode parentClassNode, @NonNull final Stream<OWLClass> classes, final String ontologyUrlString)
	{
		@NonNull final List<INode> childNodes = classes //

				.filter(clazz -> !clazz.isOWLNothing()) //
				.sorted() //
				.map(clazz -> {

					// node
					@NonNull final TreeMutableNode classNode = visitClass(null, clazz.asOWLClass(), ontologyUrlString);

					// recurse
					assert engine != null;
					final Stream<OWLClass> subClasses = engine.getSubClasses(clazz);
					visitClasses(classNode, subClasses, ontologyUrlString);

					return classNode;
				}) //
				.collect(toList());

		// balance load
		@Nullable final List<INode> balancedNodes = loadBalancer.buildHierarchy(childNodes, 0);
		parentClassNode.addChildren(balancedNodes);
	}

	/**
	 * Visit class
	 *
	 * @param parentOwlClassNode treebolic parent node to attach to
	 * @param clazz              class
	 * @param ontologyUrlString  ontology URL string
	 * @return treebolic node
	 */
	@NonNull
	public TreeMutableNode visitClass(final INode parentOwlClassNode, @NonNull final OWLClass clazz, final String ontologyUrlString)
	{
		final String name = clazz.getIRI().getShortForm();
		final String id = clazz.getIRI().getShortForm();
		assert engine != null;
		final boolean isRelation = engine.isRelation(clazz.asOWLClass());
		final Stream<OWLAnnotation> annotations = engine.getAnnotations(clazz);

		// comment
		@NonNull String comment = clazz.getIRI() + "<br>" + annotationsToString(annotations);

		// node
		@NonNull final TreeMutableNode classNode = new TreeMutableNode(parentOwlClassNode, id);
		classNode.setLabel(isRelation ? name + "\nRelation" : name);
		classNode.setTarget(id);
		classNode.setContent(comment);
		decorateClass(classNode);

		// mounts
		if (!clazz.isOWLThing())
		{
			// get instances or properties
			final Stream<OWLNamedIndividual> instances = engine.getInstances(clazz);
			@NonNull final Stream<OWLObjectProperty> properties = engine.getProperties(clazz);
			final boolean hasInstances = instances.findAny().isPresent();
			final boolean hasProperties = properties.findAny().isPresent();

			// mountpoint
			if (hasInstances || hasProperties || isRelation)
			{
				@NonNull List<String> targets = new ArrayList<>();
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

				@NonNull final MountPoint.Mounting mountingPoint = new MountPoint.Mounting();
				mountingPoint.url = ontologyUrlString + "?iri=" + clazz.getIRI().toString() + "&target=" + String.join("+", targets);
				classNode.setMountPoint(mountingPoint);
			}
		}
		return classNode;
	}

	/**
	 * Visit classes and subclasses
	 *
	 * @param parentClassNode   treebolic parent node to attach to
	 * @param clazz             class
	 * @param ontologyUrlString ontology URL string
	 * @return treebolic node
	 */
	@NonNull
	public MutableNode visitClassAndSubclasses(final INode parentClassNode, @NonNull final OWLClass clazz, final String ontologyUrlString)
	{
		@NonNull final TreeMutableNode classNode = visitClass(parentClassNode, clazz, ontologyUrlString);

		// recurse
		assert engine != null;
		final Stream<OWLClass> subClasses = engine.getSubClasses(clazz);
		visitClasses(classNode, subClasses.sorted(), ontologyUrlString);

		return classNode;
	}

	/**
	 * Walk instances in stream
	 *
	 * @param parentNode treebolic parent node to attach to
	 * @param instances  individual stream
	 */
	public void visitInstances(@NonNull final TreeMutableNode parentNode, @NonNull final Stream<OWLNamedIndividual> instances)
	{
		@NonNull final List<INode> childNodes = instances //
				.map(instance -> {

					final String name = instance.getIRI().getShortForm();
					final String id = instance.getIRI().getShortForm();
					assert engine != null;
					final Stream<OWLClassExpression> types = engine.getTypes(instance);
					final Stream<OWLAnnotation> annotations = engine.getAnnotations(instance);

					@NonNull final MutableNode instanceNode = new MutableNode(null, id);
					instanceNode.setLabel(name);
					instanceNode.setTarget(id);
					instanceNode.setContent(typesToString(types) + "<br>" + annotationsToString(annotations) + "<br>");
					decorateInstance(instanceNode);
					return instanceNode;
				}) //
				.collect(toList());

		// balance load
		@Nullable final List<INode> balancedNodes = instancesLoadBalancer.buildHierarchy(childNodes, 0);
		parentNode.addChildren(balancedNodes);
	}

	/**
	 * Walk relation properties
	 *
	 * @param parentNode treebolic parent node to attach to
	 * @param property   property
	 */
	public void visitRelation(final MutableNode parentNode, @NonNull final OWLObjectProperty property)
	{
		final String name = property.getIRI().getShortForm();
		final String id = property.getIRI().getShortForm();
		@NonNull final MutableNode relationNode = new MutableNode(parentNode, name);
		relationNode.setLabel(name);
		relationNode.setTarget(id);
		relationNode.setEdgeLabel("is relation");
		decorateRelation(relationNode);

		assert engine != null;
		@NonNull final List<OWLClass> domains = engine.getDomains(property).collect(toList());
		if (!domains.isEmpty())
		{
			@NonNull final MutableNode domainsNode = new MutableNode(relationNode, name + "-domains");
			domainsNode.setLabel("domains");
			decorateRelation(domainsNode);
			domains.forEach(domainClass -> {

				String name2 = domainClass.asOWLClass().getIRI().getShortForm();
				String id2 = domainClass.asOWLClass().getIRI().getShortForm();
				@NonNull final MutableNode domainNode = new MutableNode(domainsNode, id2);
				domainNode.setLabel(name2);
				domainNode.setTarget(id2);
				decorateRelation(domainNode);
			});
		}

		@NonNull final List<OWLClass> ranges = engine.getRanges(property).collect(toList());
		if (!ranges.isEmpty())
		{
			@NonNull final MutableNode rangesNode = new MutableNode(relationNode, name + "-ranges");
			rangesNode.setLabel("ranges");
			decorateRelation(rangesNode);
			ranges.forEach(rangeClass -> {

				String name2 = rangeClass.asOWLClass().getIRI().getShortForm();
				String id2 = rangeClass.asOWLClass().getIRI().getShortForm();
				@NonNull final MutableNode rangeNode = new MutableNode(rangesNode, id2);
				rangeNode.setLabel(name2);
				rangeNode.setTarget(id2);
				decorateRelation(rangeNode);
			});
		}

		@NonNull final List<OWLObjectProperty> subproperties = engine.getSubproperties(property).collect(toList());
		if (!subproperties.isEmpty())
		{
			@NonNull final MutableNode subPropertiesNode = new MutableNode(relationNode, name + "-subproperties");
			subPropertiesNode.setLabel("subproperties");
			decorateRelation(subPropertiesNode);
			subproperties.forEach(subProperty -> {

				String name2 = subProperty.getIRI().getShortForm();
				String id2 = subProperty.getIRI().getShortForm();
				@NonNull final MutableNode subPropertyNode = new MutableNode(subPropertiesNode, id2);
				subPropertyNode.setLabel(name2);
				subPropertyNode.setTarget(id2);
				decorateRelation(subPropertyNode);
			});
		}

		@NonNull final List<OWLObjectProperty> inverses = engine.getInverseProperties(property).collect(toList());
		if (!inverses.isEmpty())
		{
			@NonNull final MutableNode inversesNode = new MutableNode(relationNode, name + "-inverses");
			inversesNode.setLabel("inverses");
			decorateRelation(inversesNode);
			inverses.forEach(inverseProperty -> {

				String name2 = inverseProperty.getIRI().getShortForm();
				String id2 = inverseProperty.getIRI().getShortForm();
				@NonNull final MutableNode inverseNode = new MutableNode(inversesNode, id2);
				inverseNode.setLabel(name2);
				inverseNode.setTarget(id2);
				decorateRelation(inverseNode);
			});
		}

		if (property.isOWLClass())
		{
			@NonNull final List<OWLClass> subclasses = engine.getSubclasses(property).collect(toList());
			if (!subclasses.isEmpty())
			{
				@NonNull final MutableNode subclassesNode = new MutableNode(relationNode, name + "-subclasses");
				subclassesNode.setLabel("subclasses");
				decorateRelation(subclassesNode);
				subclasses.forEach(subclass -> {

					String name2 = subclass.getIRI().getShortForm();
					String id2 = subclass.getIRI().getShortForm();
					@NonNull final MutableNode subclassNode = new MutableNode(subclassesNode, id2);
					subclassNode.setLabel(name2);
					subclassNode.setTarget(id2);
					decorateRelation(subclassNode);
				});
			}

			@NonNull final List<OWLClass> superclasses = engine.getSuperclasses(property).collect(toList());
			if (!superclasses.isEmpty())
			{
				@NonNull final MutableNode superclassesNode = new MutableNode(relationNode, name + "-superclasses");
				superclassesNode.setLabel("superclasses");
				decorateRelation(superclassesNode);
				superclasses.forEach(superclass -> {

					String name2 = superclass.getIRI().getShortForm();
					String id2 = superclass.getIRI().getShortForm();
					@NonNull final MutableNode superclassNode = new MutableNode(superclassesNode, id2);
					superclassNode.setLabel(name2);
					superclassNode.setTarget(id2);
					decorateRelation(superclassNode);
				});
			}
		}
	}

	/**
	 * Walk properties in iterator
	 *
	 * @param parentNode treebolic parent node to attach to
	 * @param properties property stream
	 */
	public void visitProperties(@NonNull final TreeMutableNode parentNode, @NonNull final Stream<OWLObjectProperty> properties)
	{
		@NonNull final List<INode> childNodes = properties //

				.map(property -> {
					final String name = property.getIRI().getShortForm();
					final String id = property.getIRI().getShortForm();

					@NonNull final MutableNode propertyNode = new MutableNode(null, id);
					propertyNode.setLabel(name);
					propertyNode.setTarget(id);
					decorateProperty(propertyNode);
					return propertyNode;
				}) //
				.collect(toList());

		// balance load
		@Nullable final List<INode> balancedNodes = propertiesLoadBalancer.buildHierarchy(childNodes, 0);
		parentNode.addChildren(balancedNodes);
	}

	// D E C O R A T E

	private void setNodeImage(@NonNull final MutableNode node, @Nullable final String imageFile, @Nullable final ImageIndex index)
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

	private void setTreeEdgeImage(@NonNull final MutableNode node, @Nullable final String edgeImageFile, @SuppressWarnings("SameParameterValue") @Nullable final ImageIndex index)
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

	@Override
	public void setNodeImage(@NonNull final MutableNode node, final int index)
	{
		if (index != -1)
		{
			node.setImageFile(images[index]);
		}
	}

	@Override
	public void setTreeEdgeImage(@NonNull final MutableNode node, final int index)
	{
		if (index != -1)
		{
			node.setEdgeImageFile(images[index]);
		}
	}

	@Override
	public void setEdgeImage(@NonNull final MutableEdge edge, final int index)
	{
		if (index != -1)
		{
			edge.setImageFile(images[index]);
		}
	}

	@NonNull
	private MutableNode decorateRoot(@NonNull final MutableNode node)
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

	@NonNull
	private MutableNode decorateClass(@NonNull final MutableNode node)
	{
		node.setBackColor(classBackColor);
		node.setForeColor(classForeColor);
		setNodeImage(node, classImageFile, ImageIndex.CLASS);
		return node;
	}

	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateClassWith(@NonNull final MutableNode node, boolean hasInstances, boolean hasProperties, boolean isRelation)
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

	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateClassWithRelation(@NonNull final MutableNode node)
	{
		node.setBackColor(classWithRelationBackColor);
		node.setForeColor(classWithRelationForeColor);
		setNodeImage(node, classWithRelationImageFile, ImageIndex.CLASSWITHRELATION);
		return node;
	}


	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateClassWithInstances(@NonNull final MutableNode node)
	{
		node.setBackColor(classWithInstancesBackColor);
		node.setForeColor(classWithInstancesForeColor);
		setNodeImage(node, classWithInstancesImageFile, ImageIndex.CLASSWITHINSTANCES);
		return node;
	}

	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateClassWithProperties(@NonNull final MutableNode node)
	{
		node.setBackColor(classWithPropertiesBackColor);
		node.setForeColor(classWithPropertiesForeColor);
		setNodeImage(node, classWithPropertiesImageFile, ImageIndex.CLASSWITHPROPERTIES);
		return node;
	}

	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateProperties(@NonNull final MutableNode node)
	{
		node.setLabel(propertiesLabel);
		node.setBackColor(propertiesBackColor);
		node.setForeColor(propertiesForeColor);
		node.setEdgeColor(propertyEdgeColor);
		setNodeImage(node, propertiesImageFile, ImageIndex.PROPERTIES);
		return node;
	}

	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateInstances(@NonNull final MutableNode node)
	{
		node.setLabel(instancesLabel);
		node.setBackColor(instancesBackColor);
		node.setForeColor(instancesForeColor);
		node.setEdgeColor(instanceEdgeColor);
		setNodeImage(node, instancesImageFile, ImageIndex.INSTANCES);
		return node;
	}

	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateInstance(@NonNull final MutableNode node)
	{
		node.setBackColor(instanceBackColor);
		node.setForeColor(instanceForeColor);
		node.setEdgeStyle(instanceEdgeStyle);
		node.setEdgeColor(instanceEdgeColor);
		setNodeImage(node, instanceImageFile, ImageIndex.INSTANCE);
		setTreeEdgeImage(node, instanceEdgeImageFile, null);
		return node;
	}

	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateRelation(@NonNull final MutableNode node)
	{
		node.setBackColor(relationBackColor);
		node.setForeColor(relationForeColor);
		node.setEdgeStyle(relationEdgeStyle);
		node.setEdgeColor(relationEdgeColor);
		setNodeImage(node, relationImageFile, ImageIndex.RELATION);
		setTreeEdgeImage(node, relationEdgeImageFile, null);
		return node;
	}

	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	private MutableNode decorateProperty(@NonNull final MutableNode node)
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
	@Nullable
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
	@Nullable
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
	@NonNull
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
	@NonNull
	private static String typesToString(@NonNull final Stream<OWLClassExpression> types)
	{
		return types //
				.map(Object::toString) //
				.collect(Collectors.joining("<br>"));
	}
}
