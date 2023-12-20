/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.studio;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Properties;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;

import treebolic.IContext;
import treebolic.IWidget;
import treebolic.Widget;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.studio.domtree.DomTreeView;
import treebolic.studio.tree.TreeView;

/**
 * Main frame
 *
 * @author Bernard Bou
 */
public class MainFrame extends JFrame
{
	/**
	 * Tree icon
	 */
	@SuppressWarnings("DataFlowIssue")
	static final Icon treeIcon = new ImageIcon(MainFrame.class.getResource("images/treetab.png"));

	/**
	 * Tree icon
	 */
	@SuppressWarnings("DataFlowIssue")
	static final Icon treebolicIcon = new ImageIcon(MainFrame.class.getResource("images/treebolictab.png"));

	/**
	 * Rendering mode
	 */
	static public final boolean hasTreebolicRendering = true;

	/**
	 * Property view
	 */
	private PropertyView propertyView;

	/**
	 * Treebolic widget
	 */
	private IWidget widget;

	/**
	 * Controller
	 */
	@NonNull
	private final Controller controller;

	/**
	 * Tree view
	 */
	private TreeView treeView;

	/**
	 * Dom tree view
	 */
	private DomTreeView domTreeView;

	/**
	 * Text view
	 */
	private TextView textView;

	/**
	 * Tabbed pane
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Toolbar panel
	 */
	private JPanel toolbarPanel;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param args arguments
	 */
	public MainFrame(final String[] args)
	{
		super();
		this.controller = new Controller(args);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		initialize();

		// pack frame with preferred size data
		pack();

		// show frame
		setVisible(true);

		// open
		@Nullable Properties parameters = this.controller.getParameters();
		final String documentPath = parameters == null ? null : parameters.getProperty("doc");
		if (documentPath != null && !documentPath.isEmpty())
		{
			this.controller.open(documentPath);
		}
	}

	/**
	 * Make mainframe
	 */
	private void initialize()
	{
		this.widget = makeWidget(this.controller);
		this.treeView = makeTreeView(this.controller);
		this.propertyView = makePropertyView(this.controller);
		this.textView = makeTextView();
		this.domTreeView = makeDomTreeView();
		this.toolbarPanel = makeToolbarPanel(this.controller);

		this.treeView.setPreferredSize(new Dimension(400, 0));

		@NonNull final JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(makeTabbedPane(this.controller), BorderLayout.CENTER);
		contentPane.add(this.toolbarPanel, BorderLayout.EAST);

		setJMenuBar(makeMenubar(this.controller));
		setContentPane(contentPane);
		setTitle(Messages.getString("MainFrame.title"));

		// connect controller
		this.controller.connect(this.widget);
		this.controller.connect(this.propertyView);
		this.controller.connect(this.treeView);
		this.controller.connect(this.textView);
		this.controller.connect(this.domTreeView);
		this.controller.connect(this.tabbedPane);
		this.controller.connect(this);
	}

	@Override
	protected void processWindowEvent(@NonNull final WindowEvent event)
	{
		if (event.getID() == WindowEvent.WINDOW_CLOSING)
		{
			this.controller.exit();
		}
		super.processWindowEvent(event);
	}

	// C O M P O N E N T S

	/**
	 * Make the menu bar
	 *
	 * @return javax.swing.JMenuBar
	 */
	@NonNull
	private Menubar makeMenubar(final Controller controller)
	{
		@NonNull final Menubar menubar = new Menubar();
		menubar.setController(controller);
		return menubar;
	}

	/**
	 * Make the toolbar space
	 *
	 * @return javax.swing.JToolBar
	 */
	@NonNull
	private JPanel makeToolbarPanel(final Controller controller)
	{
		@NonNull final JPanel toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.Y_AXIS));
		toolbarPanel.add(makeToolbar(controller));
		toolbarPanel.add(makeTreeToolbar(controller));
		return toolbarPanel;
	}

	/**
	 * Make the toolbar
	 *
	 * @return javax.swing.JToolBar
	 */
	@NonNull
	private Toolbar makeToolbar(final Controller controller)
	{
		@NonNull final Toolbar toolBar = new Toolbar();
		toolBar.setController(controller);
		return toolBar;
	}

	/**
	 * Make the toolbar
	 *
	 * @return javax.swing.JToolBar
	 */
	@NonNull
	private TreeToolbar makeTreeToolbar(final Controller controller)
	{
		@NonNull final TreeToolbar toolBar = new TreeToolbar();
		toolBar.setController(controller);
		return toolBar;
	}

	/**
	 * Make the tabbed pane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane makeTabbedPane(@NonNull final Controller controller)
	{
		this.tabbedPane = new JTabbedPane();

		makeTab(this.tabbedPane, Messages.getString("MainFrame.tab_tree"), MainFrame.treeIcon, makeEditor(controller), MainFrame.this.controller::updateTree);

		makeTab(this.tabbedPane, Messages.getString("MainFrame.tab_view"), MainFrame.treebolicIcon, makeView(controller), MainFrame.this.controller::updateWidget);

		makeTab(this.tabbedPane, Messages.getString("MainFrame.tab_xmltree"), null, new JScrollPane(this.domTreeView), MainFrame.this.controller::updateDom);

		makeTab(this.tabbedPane, Messages.getString("MainFrame.tab_xml"), null, new JScrollPane(this.textView), MainFrame.this.controller::updateText);
		this.tabbedPane.addChangeListener(controller);
		this.tabbedPane.addChangeListener(event -> {
			final int selectedTabIndex = MainFrame.this.tabbedPane.getSelectedIndex();
			MainFrame.this.toolbarPanel.getComponent(1).setVisible(selectedTabIndex == 0);
		});
		return this.tabbedPane;
	}

	/**
	 * Make tab
	 *
	 * @param tabbedPane tabbed pane
	 * @param title      title
	 * @param icon       tab icon
	 * @param component  component
	 * @param action     component update runnable
	 */
	private void makeTab(@NonNull final JTabbedPane tabbedPane, final String title, final Icon icon, final Component component, final Runnable action)
	{
		tabbedPane.addTab(title, icon, component, null);
		this.controller.updateMap.put(component, action);
	}

	/**
	 * Make the html view
	 *
	 * @return javax.swing.JPanel
	 */
	@NonNull
	private TreeView makeTreeView(final Controller controller)
	{
		@NonNull final TreeView treeView = new TreeView();
		treeView.connect(controller);
		treeView.setPreferredSize(new Dimension(300, 500));
		return treeView;
	}

	/**
	 * Make the treebolic view
	 *
	 * @return treebolic view
	 */
	@NonNull
	@SuppressWarnings("RedundantSuppression")
	private JPanel makeView(final Controller ignoredController)
	{
		@NonNull final JPanel view = new JPanel();
		view.setLayout(new BorderLayout());
		//noinspection ConstantConditions
		assert widget instanceof Component;
		view.add((Component) this.widget, BorderLayout.CENTER);
		return view;
	}

	/**
	 * Make the widget
	 *
	 * @return widget
	 */
	@NonNull
	private Widget makeWidget(final IContext context)
	{
		return new Widget(context, null);
	}

	/**
	 * Make the text view
	 *
	 * @return text view
	 */
	@NonNull
	private TextView makeTextView()
	{
		return new TextView();
	}

	/**
	 * Make the property view
	 *
	 * @return property view
	 */
	@NonNull
	private PropertyView makePropertyView(@NonNull final Controller controller)
	{
		@NonNull final PropertyView propertyView = new PropertyView();
		propertyView.setImageRepository(controller.makeImageRepositoryURL());
		propertyView.setCellEditorListener(controller);
		return propertyView;
	}

	/**
	 * Make the Dom tree view
	 *
	 * @return Dom tree view
	 */
	@NonNull
	private DomTreeView makeDomTreeView()
	{
		@NonNull final TreeCellRenderer renderer = MainFrame.hasTreebolicRendering ? new treebolic.studio.domtree.treebolic.Renderer() : new treebolic.studio.domtree.Renderer();
		return new DomTreeView(renderer);
	}

	/**
	 * Make the editor
	 *
	 * @return javax.swing.JComponent
	 */
	@NonNull
	private JComponent makeEditor(@NonNull final Controller controller)
	{
		@NonNull final JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pane.setDividerSize(8);
		pane.setOneTouchExpandable(true);
		pane.add(this.treeView, JSplitPane.LEFT);
		pane.add(makeSubEditor(controller), JSplitPane.RIGHT);
		return pane;
	}

	/**
	 * Make the subeditor
	 *
	 * @return javax.swing.JComponent
	 */
	@NonNull
	private JComponent makeSubEditor(@NonNull final Controller controller)
	{
		@NonNull final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(this.propertyView, BorderLayout.CENTER);
		panel.add(makeDefaultsPanel(controller), BorderLayout.SOUTH);
		return panel;
	}

	/**
	 * Make the defaults panel
	 *
	 * @return javax.swing.JPanel
	 */
	@NonNull
	private JPanel makeDefaultsPanel(@NonNull final Controller controller)
	{
		@NonNull final JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(makeGlobalsButton(controller, Messages.getString("MainFrame.globals"), "top.png", Controller.Code.SELECTTOP));
		panel.add(makeGlobalsButton(controller, Messages.getString("MainFrame.tree"), "tree.png", Controller.Code.SELECTTREE));
		panel.add(makeGlobalsButton(controller, Messages.getString("MainFrame.nodes"), "nodes.png", Controller.Code.SELECTNODES));
		panel.add(makeGlobalsButton(controller, Messages.getString("MainFrame.edges"), "edges.png", Controller.Code.SELECTEDGES));
		return panel;
	}

	/**
	 * Make the globals button
	 *
	 * @return javax.swing.JButton
	 */
	@NonNull
	private JButton makeGlobalsButton(@NonNull final Controller controller, final String label, final String image, @NonNull final Controller.Code code)
	{
		@NonNull final JButton button = new JButton();
		@Nullable final URL imageUrl = getClass().getResource("images/" + image);
		assert imageUrl != null;
		button.setIcon(new ImageIcon(imageUrl));
		button.setToolTipText(label);
		button.setBorder(null);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.addActionListener(e -> controller.execute(code, 0));
		return button;
	}
}
