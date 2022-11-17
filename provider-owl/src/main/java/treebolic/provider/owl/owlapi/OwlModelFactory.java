/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.provider.owl.owlapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import treebolic.glue.Color;
import treebolic.glue.Image;
import treebolic.model.IEdge;
import treebolic.model.INode;
import treebolic.model.Model;
import treebolic.model.MountPoint;
import treebolic.model.MutableNode;
import treebolic.model.Settings;
import treebolic.model.Tree;
import treebolic.model.TreeMutableNode;
import treebolic.model.Utils;
import treebolic.provider.LoadBalancer;

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
	static boolean asTree = false;

	// S T A T I C . D A T A

	public enum ImageIndices
	{
		ROOT, CLASS, CLASSWITHPROPERTIES, PROPERTIES, PROPERTY, CLASSWITHINSTANCES, INSTANCES, INSTANCE, BRANCH, BRANCH2
	}

	static Image[] images;

	/**
	 * Default class color
	 */
	static Color defaultClassBackColor = new Color(0xffffC0);

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

	// M E M B E RS

	/**
	 * Properties
	 */
	private final Properties properties;

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
	static private Color propertyEdgeColor;

	/**
	 * Property edge style
	 */
	static private Integer propertyEdgeStyle;

	/**
	 * Property edge image file
	 */
	static private String propertyEdgeImageFile;

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
	static private Color instanceEdgeColor;

	/**
	 * Instance edge style
	 */
	static private Integer instanceEdgeStyle;

	/**
	 * Instance edge image file
	 */
	static private String instanceEdgeImageFile;

	/**
	 * Entities are named using IRIs. These are usually too long for use in user interfaces. To solve this problem, and so a query can be written using short
	 * class, property, individual names we use a short form provider. In this case, we'll just use a simple short form provider that generates short forms from
	 * IRI fragments.
	 */
	final private ShortFormProvider shortFormProvider;

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
	protected LoadBalancer loadBalancer;

	/**
	 * Load balancer (properties and instances)
	 */
	protected LoadBalancer subLoadBalancer;

	// C O N S T R U C T O R

	public OwlModelFactory(final Properties properties)
	{
		this.properties = properties;
		this.shortFormProvider = new SimpleShortFormProvider();
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

	// P A R S E

	/**
	 * Make model
	 *
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
		OwlModelFactory.propertyEdgeStyle = settings.edgeStyle;
		OwlModelFactory.propertyEdgeColor = settings.edgeColor;
		OwlModelFactory.propertyEdgeImageFile = settings.defaultEdgeImage;

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
				this.parser = new QueryParser(this.ontology, this.shortFormProvider);
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
				// instances
				if ("instances".equals(target))
				{
					// root
					final MutableNode owlClassNode = new MutableNode(null, "root");
					owlClassNode.setLabel(OwlModelFactory.getName(classIri));
					decorateClassWithInstances(owlClassNode);

					// instances root
					final TreeMutableNode instancesNode = new TreeMutableNode(owlClassNode, classIri + "-instances");
					decorateInstances(instancesNode);

					// instances
					final Set<OWLNamedIndividual> instances = this.engine.getInstances(owlClass, true);
					visitInstances(instancesNode, instances);

					return new Tree(owlClassNode, null);
				}
				// properties
				else if ("properties".equals(target))
				{
					// root
					final MutableNode owlClassNode = new MutableNode(null, "root");
					owlClassNode.setLabel(OwlModelFactory.getName(classIri));
					decorateClassWithProperties(owlClassNode);

					// properties root
					final TreeMutableNode propertiesNode = new TreeMutableNode(owlClassNode, classIri + "-properties");
					decorateProperties(propertiesNode);

					// properties
					final Set<OWLObjectProperty> properties = this.engine.getProperties(owlClass, true);
					visitProperties(propertiesNode, properties);

					return new Tree(owlClassNode, null);
				}
				// instances + properties
				else if ("instances_properties".equals(target))
				{
					// root
					final MutableNode owlClassNode = new MutableNode(null, "root");
					owlClassNode.setLabel(OwlModelFactory.getName(classIri));
					decorateClassWithInstancesAndProperties(owlClassNode);

					// instances root
					final TreeMutableNode instancesNode = new TreeMutableNode(owlClassNode, classIri + "-instances");
					decorateInstances(instancesNode);

					// properties root
					final TreeMutableNode propertiesNode = new TreeMutableNode(owlClassNode, classIri + "-properties");
					decorateProperties(propertiesNode);

					// instances
					final Set<OWLNamedIndividual> instances = this.engine.getInstances(owlClass, true);
					visitInstances(instancesNode, instances);

					// properties
					final Set<OWLObjectProperty> properties = this.engine.getProperties(owlClass, true);
					visitProperties(propertiesNode, properties);

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

	private MutableNode decorateClassWithInstancesAndProperties(final MutableNode node)
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

	/**
	 * Walk classes in iterator
	 *
	 * @param parentClassNode   treebolic parent node to attach to
	 * @param owlClasses        class iterator
	 * @param ontologyUrlString URL string
	 */
	public void visitClasses(final TreeMutableNode parentClassNode, final Iterator<OWLClass> owlClasses, final String ontologyUrlString)
	{
		final List<INode> childNodes = new ArrayList<>();
		while (owlClasses.hasNext())
		{
			final OWLClass owlClass = owlClasses.next();
			if (owlClass.isOWLNothing())
			{
				continue;
			}
			// System.out.println(this.shortFormProvider.getShortForm(owlClass) + " class=" + " " + owlClass);

			// node
			final TreeMutableNode owlClassNode = visitClass(null, owlClass, ontologyUrlString);
			childNodes.add(owlClassNode);

			// recurse
			final Set<OWLClass> owlSubClasses = this.engine.getSubClasses(owlClass, true);
			visitClasses(owlClassNode, owlSubClasses.iterator(), ontologyUrlString);
		}

		// balance load
		final List<INode> balancedNodes = this.loadBalancer.buildHierarchy(childNodes, 0);
		parentClassNode.addChildren(balancedNodes);
	}

	public TreeMutableNode visitClass(final INode parentOwlClassNode, final OWLClass owlClass, final String ontologyUrlString)
	{
		final String ownClassShortForm = this.shortFormProvider.getShortForm(owlClass);
		final String owlClassId = OwlModelFactory.getName(ownClassShortForm);
		final Set<OWLAnnotation> annotations = null; // owlClass.getAnnotations(this.ontology)
		// comment
		String comment = owlClass.getIRI().toString() + "<br>" + annotationsToString(annotations);

		// node
		final TreeMutableNode owlClassNode = new TreeMutableNode(parentOwlClassNode, owlClassId);
		owlClassNode.setLabel(owlClassId);
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
			final Set<OWLNamedIndividual> instances = this.engine.getInstances(owlClass, true);
			final Set<OWLObjectProperty> properties = this.engine.getProperties(owlClass, true);

			// instances+properties mountpoint
			if (instances != null && !instances.isEmpty() && properties != null && !properties.isEmpty())
			{
				final MountPoint.Mounting mountingPoint = new MountPoint.Mounting();
				mountingPoint.url = ontologyUrlString + "?iri=" + owlClass.getIRI().toString() + "&target=instances_properties";
				owlClassNode.setMountPoint(mountingPoint);
			}

			// instances mountpoint
			else if (instances != null && !instances.isEmpty())
			{
				final MountPoint.Mounting mountingPoint = new MountPoint.Mounting();
				mountingPoint.url = ontologyUrlString + "?iri=" + owlClass.getIRI().toString() + "&target=instances";
				owlClassNode.setMountPoint(mountingPoint);
			}

			// properties mountpoint
			else if (properties != null && !properties.isEmpty())
			{
				final MountPoint.Mounting mountingPoint = new MountPoint.Mounting();
				mountingPoint.url = ontologyUrlString + "?iri=" + owlClass.getIRI().toString() + "&target=properties";
				owlClassNode.setMountPoint(mountingPoint);
			}
		}

		// if (owlClass.listDeclaredProperties(true).hasNext())
		return owlClassNode;
	}

	public MutableNode visitClassAndSubclasses(final INode parentOwlClassNode, final OWLClass owlClass, final String ontologyUrlString)
	{
		final TreeMutableNode owlClassNode = visitClass(parentOwlClassNode, owlClass, ontologyUrlString);

		// recurse
		final Set<OWLClass> owlSubClasses = this.engine.getSubClasses(owlClass, true);
		visitClasses(owlClassNode, owlSubClasses.iterator(), ontologyUrlString);

		return owlClassNode;
	}

	/**
	 * Walk properties in iterator
	 *
	 * @param parentNode    treebolic parent node to attach to
	 * @param owlProperties property iterator
	 */
	public void visitProperties(final TreeMutableNode parentNode, final Set<OWLObjectProperty> owlProperties)
	{
		final List<INode> childNodes = new ArrayList<>();
		for (final OWLObjectProperty owlProperty : owlProperties)
		{
			final String owlPropertyShortForm = this.shortFormProvider.getShortForm(owlProperty);
			final String owlPropertyId = OwlModelFactory.getName(owlPropertyShortForm);

			final MutableNode owlPropertyNode = new MutableNode(null, owlPropertyId);
			owlPropertyNode.setLabel(owlPropertyId);
			// owlPropertyNode.setContent(owlProperty.getComment("en"));
			owlPropertyNode.setBackColor(this.propertyBackColor);
			owlPropertyNode.setForeColor(this.propertyForeColor);
			if (this.propertyImageFile != null)
			{
				owlPropertyNode.setImageFile(this.propertyImageFile);
			}
			else
			{
				owlPropertyNode.setImageIndex(ImageIndices.PROPERTY.ordinal());
			}

			// owlPropertyNode.setContent(owlProperty.getComment("en"));
			owlPropertyNode.setEdgeStyle(OwlModelFactory.propertyEdgeStyle);
			owlPropertyNode.setEdgeColor(OwlModelFactory.propertyEdgeColor);
			if (OwlModelFactory.propertyEdgeImageFile != null)
			{
				owlPropertyNode.setEdgeImageFile(OwlModelFactory.propertyEdgeImageFile);
			}

			childNodes.add(owlPropertyNode);

			// // recurse
			// final ExtendedIterator owlSubProperties = owlProperty.listSubProperties(true);
			// walkProperties(owlPropertyNode, owlSubProperties);
		}

		// balance load
		final List<INode> balancedNodes = this.subLoadBalancer.buildHierarchy(childNodes, 0);
		parentNode.addChildren(balancedNodes);
	}

	/**
	 * Walk instances in iterator
	 *
	 * @param parentNode     treebolic parent node to attach to
	 * @param owlIndividuals individual iterator
	 */
	public void visitInstances(final TreeMutableNode parentNode, final Set<OWLNamedIndividual> owlIndividuals)
	{
		final List<INode> childNodes = new ArrayList<>();
		for (final OWLNamedIndividual owlIndividual : owlIndividuals)
		{
			final String owlIndividualPropertyShortForm = this.shortFormProvider.getShortForm(owlIndividual);
			final String owlIndividualId = OwlModelFactory.getName(owlIndividualPropertyShortForm);
			final Set<OWLClassExpression> types = owlIndividual.getNestedClassExpressions(); // owlIndividual.getTypes(this.ontology).toString()
			final Set<OWLAnnotation> annotations = null; // owlIndividual.getAnnotations(this.ontology))

			final MutableNode owlInstanceNode = new MutableNode(null, owlIndividualId);
			owlInstanceNode.setLabel(owlIndividualId);
			owlInstanceNode.setContent(types + "<br>" + annotationsToString(annotations));
			owlInstanceNode.setBackColor(this.instanceBackColor);
			owlInstanceNode.setForeColor(this.instanceForeColor);
			if (this.instanceImageFile != null)
			{
				owlInstanceNode.setImageFile(this.instanceImageFile);
			}
			else
			{
				owlInstanceNode.setImageIndex(ImageIndices.INSTANCE.ordinal());
			}

			// owlPropertyNode.setContent(owlProperty.getComment("en"));
			owlInstanceNode.setEdgeStyle(OwlModelFactory.instanceEdgeStyle);
			owlInstanceNode.setEdgeColor(OwlModelFactory.instanceEdgeColor);
			if (OwlModelFactory.instanceEdgeImageFile != null)
			{
				owlInstanceNode.setEdgeImageFile(OwlModelFactory.instanceEdgeImageFile);
			}

			childNodes.add(owlInstanceNode);
		}

		// balance load
		final List<INode> balancedNodes = this.subLoadBalancer.buildHierarchy(childNodes, 0);
		parentNode.addChildren(balancedNodes);
	}

	/**
	 * Annotations to string
	 *
	 * @param annotations set of annotations
	 * @return string
	 */
	private String annotationsToString(final Set<OWLAnnotation> annotations)
	{
		if (annotations == null)
		{
			return "";
		}

		final StringBuilder sb = new StringBuilder();
		for (final OWLAnnotation annotation : annotations)
		{
			if (annotation.getValue() instanceof OWLLiteral)
			{
				final OWLLiteral val = (OWLLiteral) annotation.getValue();
				String str = val.getLiteral();
				str = str.replaceAll("\n", "");
				sb.append(str);
				sb.append("<br>");
			}
		}
		return sb.toString();
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
		try(InputStream is = new URL(ontologyDocumentUrl).openStream())
		{
			return this.manager.loadOntologyFromOntologyDocument(new StreamDocumentSource(is));
		}
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

		this.propertiesLabel = getLabel("properties.label", "properties");
		this.propertiesBackColor = getColor("properties.backcolor", Color.WHITE);
		this.propertiesForeColor = getColor("properties.forecolor", Color.MAGENTA);
		this.propertiesImageFile = getImageFile("properties.image");

		this.propertyBackColor = getColor("property.backcolor", Color.WHITE);
		this.propertyForeColor = getColor("property.forecolor", Color.MAGENTA);
		this.propertyImageFile = getImageFile("property.image");

		this.instancesLabel = getLabel("instances.label", "instances");
		this.instancesBackColor = getColor("instances.backcolor", Color.WHITE);
		this.instancesForeColor = getColor("instances.forecolor", Color.BLUE);
		this.instancesImageFile = getImageFile("instances.image");

		this.instanceBackColor = getColor("instance.backcolor", Color.WHITE);
		this.instanceForeColor = getColor("instance.forecolor", Color.BLUE);
		this.instanceImageFile = getImageFile("instance.image");

		OwlModelFactory.images = new Image[]{ //
				Image.make(Provider.class.getResource("images/root.png")), // ROOT
				Image.make(Provider.class.getResource("images/class.png")), // CLASS
				Image.make(Provider.class.getResource("images/classwithproperties.png")), // CLASSWITHPROPERTIES
				Image.make(Provider.class.getResource("images/properties.png")), // PROPERTIES
				Image.make(Provider.class.getResource("images/property.png")), // PROPERTY
				Image.make(Provider.class.getResource("images/classwithinstances.png")), // CLASSWITHINSTANCES
				Image.make(Provider.class.getResource("images/instances.png")), // INSTANCES
				Image.make(Provider.class.getResource("images/instance.png")), // INSTANCE
				Image.make(Provider.class.getResource("images/branch.png")), // BRANCH
				Image.make(Provider.class.getResource("images/branch2.png")), // BRANCH2
		};
	}

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

	// N A M E

	/**
	 * Get name
	 *
	 * @param uriString uri string
	 * @return name
	 */
	static public String getName(final String uriString)
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
}
