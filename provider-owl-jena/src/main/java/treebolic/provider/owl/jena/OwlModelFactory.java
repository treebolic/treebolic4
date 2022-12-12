/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.provider.owl.jena;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
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

	static private final String LANG = "";

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
	 * LoadBalancer (instances and properties) : image
	 */
	@Nullable
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
	 * LoadBalancer (instances and properties) : image
	 */
	@Nullable
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
	private OntModel model;

	/**
	 * Engine
	 */
	@Nullable
	private QueryEngine engine;

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

		url = null;
		model = null;
		engine = null;

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

	// P A R S E

	/**
	 * Make model
	 *
	 * @param ontologyUrl ontology URL string
	 * @return model if successful
	 */
	@Nullable
	public Model makeModel(final String ontologyUrl)
	{
		@Nullable final Tree tree = makeTree(ontologyUrl);
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
		if (!argUrl.equals(url) || model == null || engine == null)
		{
			try (InputStream is = new URL(argUrl).openStream())
			{
				model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
				model.read(is, argUrl);
				url = argUrl;
				engine = new QueryEngine(model);
			}
			catch (final Exception e)
			{
				System.err.println("OWL:" + e);
				return null;
			}
		}

		// root
		OntClass clazz;
		if (argClassIri != null || argClassName != null)
		{
			// this is a mount: there were parameters
			if (argClassIri != null)
			{
				// use class iri
				clazz = model.getOntClass(argClassIri);
			}
			else
			{
				// use short form
				clazz = model.getOntClass(url + "#" + argClassName);
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
					classNode.setLabel(clazz.getLocalName() + "\nroot");
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
						final ExtendedIterator<OntResource> instances = engine.getInstances(clazz);
						visitInstances(instancesNode, instances);
					}
					// relation
					if (isRelation)
					{
						// relation
						OntProperty relation = engine.getRelation(clazz);
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
						@NonNull final Stream<OntProperty> properties = engine.getProperties(clazz);
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
			// from top
			Set<OntClass> tops = engine.getTopClasses(model).toSet();
			if (tops.size() == 1)
			{
				// walk classes
				final OntClass rootClass = tops.iterator().next();
				@NonNull final MutableNode rootNode = visitClassAndSubclasses(null, rootClass, argUrl);
				rootNode.setLabel(rootNode.getLabel() + "\nroot");
				return new Tree(decorateRoot(rootNode), null);
			}
			else
			{
				final OntClass rootClass = model.createClass("#Thing");
				@NonNull MutableNode rootNode = visitClass(null, rootClass, argUrl);
				rootNode.setLabel(rootNode.getLabel() + "\nroot");
				for (@NonNull OntClass top : tops)
				{
					visitClassAndSubclasses(rootNode, top, argUrl);
				}
				return new Tree(decorateRoot(rootNode), null);
			}
		}
	}

	/**
	 * Make tree
	 *
	 * @param url0 url string
	 * @return tree if successful
	 */
	@NonNull
	public Map<String, String> parseUrl(final String url0)
	{
		@NonNull final Map<String, String> map = new HashMap<>();

		String url = url0;

		// parameter and url
		@Nullable String parameters = null;
		final int index = url.indexOf('?');
		if (index != -1)
		{
			parameters = url.substring(index + 1);
			url = url.substring(0, index);
		}
		map.put("url", url);

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
	 * @param parentClassNode treebolic parent node to attach to
	 * @param classes         class stream
	 * @param ontologyUrl     URL string
	 */
	public void visitClasses(@NonNull final TreeMutableNode parentClassNode, final ExtendedIterator<OntClass> classes, final String ontologyUrl)
	{
		@NonNull final List<INode> childNodes = StreamUtils.toStream(classes) //

				.sorted(Comparator.comparing(OntClass::getLocalName)) //
				.map(clazz -> {

					// node
					@NonNull final TreeMutableNode classNode = visitClass(null, clazz, ontologyUrl);

					// recurse
					assert engine != null;
					final ExtendedIterator<OntClass> subClasses = engine.getSubClasses(clazz);
					visitClasses(classNode, subClasses, ontologyUrl);

					return (INode) classNode;
				}) //
				.collect(toList());

		// balance load and attach to parent
		@Nullable final List<INode> balancedNodes = loadBalancer.buildHierarchy(childNodes, 0);
		parentClassNode.addChildren(balancedNodes);
	}

	/**
	 * Visit class
	 *
	 * @param parentOwlClassNode treebolic parent node to attach to
	 * @param clazz              class
	 * @param ontologyUrl        ontology URL string
	 * @return treebolic node
	 */
	@NonNull
	public TreeMutableNode visitClass(final INode parentOwlClassNode, @NonNull final OntClass clazz, final String ontologyUrl)
	{
		final String className = clazz.getLocalName();
		final String classId = clazz.getLocalName();
		assert engine != null;
		final boolean isRelation = engine.isRelation(clazz);

		// comment
		@NonNull String comment = clazz.getLocalName() + "<br>" + clazz.getComment(LANG) + "<br>" + clazz.getNameSpace(); //annotationsToString(annotations);

		// node
		@NonNull final TreeMutableNode classNode = new TreeMutableNode(parentOwlClassNode, classId);
		classNode.setLabel(isRelation ? className + "\nRelation" : className);
		classNode.setTarget(classId);
		classNode.setContent(comment);
		decorateClass(classNode);

		// mounts
		// if (!class.equals(engine.getTopClass(model)))
		{
			// get instances or properties
			final ExtendedIterator<OntResource> instances = engine.getInstances(clazz);
			final boolean hasInstances = instances.hasNext();
			@NonNull final Stream<OntProperty> properties = engine.getProperties(clazz);
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
				mountingPoint.url = ontologyUrl + "?iri=" + clazz + "&target=" + String.join("+", targets);
				classNode.setMountPoint(mountingPoint);
			}
		}
		return classNode;
	}

	/**
	 * Visit classes and subclasses
	 *
	 * @param parentOwlClassNode treebolic parent node to attach to
	 * @param clazz              class
	 * @param ontologyUrl        ontology URL string
	 * @return treebolic node
	 */
	@NonNull
	public MutableNode visitClassAndSubclasses(final INode parentOwlClassNode, @NonNull final OntClass clazz, final String ontologyUrl)
	{
		@NonNull final TreeMutableNode classNode = visitClass(parentOwlClassNode, clazz, ontologyUrl);

		// recurse
		assert engine != null;
		final ExtendedIterator<OntClass> subClasses = engine.getSubClasses(clazz);
		visitClasses(classNode, subClasses, ontologyUrl);

		return classNode;
	}

	/**
	 * Walk instances in stream
	 *
	 * @param parentNode treebolic parent node to attach to
	 * @param instances  individual stream
	 */
	public void visitInstances(@NonNull final TreeMutableNode parentNode, final ExtendedIterator<OntResource> instances)
	{
		@NonNull final List<INode> childNodes = StreamUtils.toStream(instances) //
				.sorted(Comparator.comparing(OntResource::getLocalName)) //
				.map(instance -> {

					final String name = instance.getLocalName();
					final String id = instance.getLocalName();
					assert engine != null;
					final ExtendedIterator<String> types = engine.getTypes(instance).mapWith(OntClass::getLocalName);
					final ExtendedIterator<String> annotations = engine.getAnnotations(instance, LANG).mapWith(RDFNode::toString);

					@NonNull final MutableNode instanceNode = new MutableNode(null, id);
					instanceNode.setLabel(name);
					instanceNode.setTarget(id);
					instanceNode.setContent(typesToString(types) + "<br>" + annotationsToString(annotations) + "<br>");
					decorateInstance(instanceNode);
					return (INode) instanceNode;
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
	public void visitRelation(final MutableNode parentNode, @NonNull final OntProperty property)
	{
		final String name = property.getLocalName();
		final String id = property.getLocalName();
		@NonNull final MutableNode relationNode = new MutableNode(parentNode, id);
		relationNode.setLabel(name);
		relationNode.setTarget(id);
		relationNode.setEdgeLabel("is relation");
		decorateRelation(relationNode);

		assert engine != null;
		final List<OntClass> domains = engine.getDomains(property).toList();
		if (!domains.isEmpty())
		{
			@NonNull final MutableNode domainsNode = new MutableNode(relationNode, name + "-domains");
			domainsNode.setLabel("domains");
			decorateRelation(domainsNode);
			domains.forEach(domainClass -> {

				String domainName = domainClass.getLocalName();
				String domainId = domainClass.getLocalName();
				@NonNull final MutableNode domainNode = new MutableNode(domainsNode, domainId);
				domainNode.setLabel(domainName);
				domainNode.setTarget(domainId);
				decorateRelation(domainNode);
			});
		}

		final List<OntClass> ranges = engine.getRanges(property).toList();
		if (!ranges.isEmpty())
		{
			@NonNull final MutableNode rangesNode = new MutableNode(relationNode, name + "-ranges");
			rangesNode.setLabel("ranges");
			decorateRelation(rangesNode);
			ranges.forEach(rangeClass -> {

				String rangeName = rangeClass.getLocalName();
				String rangeId = rangeClass.getLocalName();
				@NonNull final MutableNode rangeNode = new MutableNode(rangesNode, rangeId);
				rangeNode.setLabel(rangeName);
				rangeNode.setTarget(rangeId);
				decorateRelation(rangeNode);
			});
		}

		final List<? extends OntProperty> subproperties = engine.getSubproperties(property).toList();
		if (!subproperties.isEmpty())
		{
			@NonNull final MutableNode subPropertiesNode = new MutableNode(relationNode, name + "-subproperties");
			subPropertiesNode.setLabel("subproperties");
			decorateRelation(subPropertiesNode);
			subproperties.forEach(subProperty -> {

				String subpropertyName = subProperty.getLocalName();
				String subpropertyId = subProperty.getLocalName();
				@NonNull final MutableNode subPropertyNode = new MutableNode(subPropertiesNode, subpropertyId);
				subPropertyNode.setLabel(subpropertyName);
				subPropertyNode.setTarget(subpropertyId);
				decorateRelation(subPropertyNode);
			});
		}

		final List<? extends OntProperty> inverses = engine.getInverseProperties(property).toList();
		if (!inverses.isEmpty())
		{
			@NonNull final MutableNode inversesNode = new MutableNode(relationNode, name + "-inverses");
			inversesNode.setLabel("inverses");
			decorateRelation(inversesNode);
			inverses.forEach(inverseProperty -> {

				String inverseName = inverseProperty.getLocalName();
				String inverseId = inverseProperty.getLocalName();
				@NonNull final MutableNode inverseNode = new MutableNode(inversesNode, inverseId);
				inverseNode.setLabel(inverseName);
				inverseNode.setTarget(inverseId);
				decorateRelation(inverseNode);
			});
		}
	}

	/**
	 * Walk properties in iterator
	 *
	 * @param parentNode treebolic parent node to attach to
	 * @param properties property stream
	 */
	public void visitProperties(@NonNull final TreeMutableNode parentNode, @NonNull final Stream<OntProperty> properties)
	{
		@NonNull final List<INode> childNodes = properties //

				.sorted(Comparator.comparing(OntProperty::getLocalName)) //
				.map(property -> {
					final String name = property.getLocalName();
					final String id = property.getLocalName();

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

	protected void setNodeImage(@NonNull final MutableNode node, @Nullable final String imageFile, @Nullable final ImageIndex index)
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

	protected void setNodeEdgeImage(@NonNull final MutableNode node, @Nullable final String edgeImageFile, @SuppressWarnings("SameParameterValue") @Nullable final ImageIndex index)
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
		setNodeEdgeImage(node, instanceEdgeImageFile, null);
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
		setNodeEdgeImage(node, relationEdgeImageFile, null);
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
		setNodeEdgeImage(node, propertyEdgeImageFile, null);
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
	private static String annotationsToString(@NonNull final ExtendedIterator<String> annotations)
	{
		List<String> annotationElements = annotations //
				.mapWith(Object::toString) //
				.toList();
		return String.join("<br>", annotationElements);
	}

	/**
	 * Types to string
	 *
	 * @param types stream of types
	 * @return string
	 */
	@NonNull
	private static String typesToString(@NonNull final ExtendedIterator<String> types)
	{
		List<String> typeElements = types //
				.mapWith(Object::toString) //
				.toList();
		return String.join("<br>", typeElements);
	}
}
