/*
 * Copyright (c) 2023. Bernard Bou
 */
package treebolic.fungi.browser;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import javax.swing.*;

import treebolic.IWidget;
import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;
import treebolic.commons.Persist;
import treebolic.control.Commander;
import treebolic.fungi.browser.Context.SourceListener;
import treebolic.glue.component.SearchTool;
import treebolic.glue.component.Statusbar;
import treebolic.provider.sql.SqlProperties;

/**
 * Browser main frame
 *
 * @author Bernard Bou
 */
public class MainFrame extends treebolic.application.MainFrame implements SourceListener
{
    // D E F A U L T S

    /**
     * Default source
     */
    public static final String DEFAULTSOURCE = "query-en-wikipedia.properties";

    /**
     * Default French source
     */
    public static final String DEFAULTFRSOURCE = "query-fr-mycodb.properties";

    /**
     * Default pruning
     */
    public static final String DEFAULTPRUNE = "substr(id,1,1) NOT IN ('e')";

    // S O U R C E S

    @Nullable
    private static String[] labels = null;

    @Nullable
    private static String[] values = null;

    // M E M B E R S

    /**
     * Command-line arguments
     */
    private final Properties args;

    /**
     * Current source
     */
    @Nullable
    private String source;

    /**
     * Persist properties
     */
    private Properties persistProperties;

    // actions

    /**
     * Restart action
     */
    protected AbstractAction restartAction;

    /**
     * Set source action
     */
    protected AbstractAction setSourceAction;

    /**
     * Source action
     */
    protected AbstractAction sourceAction;

    /**
     * Set truncate action
     */
    protected AbstractAction setTruncateAction;

    /**
     * Truncate action
     */
    protected AbstractAction truncateAction;

    /**
     * Reset truncate action
     */
    protected AbstractAction resetTruncateAction;

    /**
     * Set prune action
     */
    protected AbstractAction setPruneAction;

    /**
     * Set prune action
     */
    protected AbstractAction pruneAction;

    /**
     * Colors action
     */
    protected AbstractAction colorsAction;

    /**
     * Font size action
     */
    protected AbstractAction fontSizeAction;

    /**
     * About action
     */
    protected AbstractAction aboutAction;

    /**
     * Search tool
     */
    private SearchTool searchTool;

    // C O N S T R U C T O R

    /**
     * Constructor
     *
     * @param args command-line arguments
     */
    public MainFrame(final String[] args)
    {
        this(processArgs(args));
    }

    /**
     * Constructor
     *
     * @param args command-line arguments
     */
    public MainFrame(Properties args)
    {
        super(args);

        this.source = null;
        this.args = completeArgs(args);

        // global settings
        Commander.TOOLTIP_HTML = true; // depends on provider
        Commander.TOOLTIP_LINESPAN = 25;

        setTitle(Messages.getString("MainFrame.title"));
    }

    // A C C E S S
    @Override
    protected String getProvider()
    {
        return "treebolic.provider.sql.jdbc.Provider";
    }

    @Override
    protected String getSource()
    {
        if (this.source == null)
        {
            this.source = getPersistSource();
        }
        return this.source;
    }

    /**
     * Get persisted source
     *
     * @return persisted source
     */
    protected String getPersistSource()
    {
        Properties persistParameters = getPersistParameters();
        String defaultSource = getDefaultSource();
        if (persistParameters != null)
            return persistParameters.getProperty(SqlProperties.SOURCE, defaultSource);
        return defaultSource;
    }

    /**
     * Get default source
     *
     * @return default source
     */
    @NonNull
    protected String getDefaultSource()
    {
        if ("fr".equals(Locale.getDefault().getLanguage()))
        {
            return MainFrame.DEFAULTFRSOURCE;
        }
        return MainFrame.DEFAULTSOURCE;
    }

    /**
     * Get persist parameters
     *
     * @return persisted parameters
     */
    private Properties getPersistParameters()
    {
        return this.persistProperties;
    }

    @NonNull
    @SuppressWarnings("SameReturnValue")
    protected String getPersistName()
    {
        return "treebolic-fungi-browser";
    }

    // S O U R C E L I S T E N E R

    @Override
    public void onUpdate(String source)
    {
        System.out.println(Messages.getString("MainFrame.updatesource") + source);
        this.source = source;
    }

    // F A C T O R I E S

    @NonNull
    @Override
    protected Context makeContext(final String source, @Nullable final String base, final String imageBase)
    {
        @Nullable File dir = base == null ? null : new File(base);
        if (dir == null || !dir.exists())
        {
            dir = Context.makeDataLocation();
        }

        @NonNull final Deployer deployer = new Deployer(dir);
        if (!deployer.check())
        {
            deployer.expand();
        }

        values = deployer.getQueryFiles();
        labels = deployer.getQueryDescriptions(values);
        //		for (String queryDescription : labels)
        //		{
        //			System.out.println("QUERY " + queryDescription);
        //		}

        return new Context(this, source, dir.getAbsolutePath(), imageBase, this);
    }

    private Properties completeArgs(Properties properties)
    {
        // values set, diverging from provider
        properties.put(SqlProperties.PRUNE_NODES, MainFrame.DEFAULTPRUNE);
        properties.put(SqlProperties.PRUNE_TREEEDGES, MainFrame.DEFAULTPRUNE);

        // persist add/override
        this.persistProperties = Persist.loadSettings(getPersistName());
        properties.putAll(this.persistProperties);

        return properties;
    }

    /**
     * Make menu bar
     *
     * @return toolbar
     */
    @Override
    protected JToolBar makeToolBar()
    {
        makeActions();

        @NonNull final JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(true);
        toolbar.setPreferredSize(Constants.DIM_TOOLBAR);
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
        toolbar.add(Box.createHorizontalStrut(16));

        // set truncate
        @NonNull final JButton restartButton = makeButton(Messages.getString("MainFrame.update"), Messages.getString("MainFrame.update"), "images/update.png", this.restartAction);
        toolbar.add(restartButton);

        // set truncate
        @NonNull final JButton setTruncateButton = makeButton(Messages.getString("MainFrame.settruncate"), Messages.getString("MainFrame.settruncate"), "images/truncate.png", this.setTruncateAction);
        toolbar.add(setTruncateButton);

        // reset truncate
        @NonNull final JButton resetTruncateButton = makeButton(Messages.getString("MainFrame.resettruncate"), Messages.getString("MainFrame.resettruncate"), "images/tree.png", this.resetTruncateAction);
        toolbar.add(resetTruncateButton);

        // set prune
        @NonNull final JButton pruneButton = makeButton(Messages.getString("MainFrame.setprune"), Messages.getString("MainFrame.setprune"), "images/prune.png", this.setPruneAction);
        toolbar.add(pruneButton);

        // search tool
        Statusbar.hasSearch = false;
        this.searchTool = new SearchTool();
        this.searchTool.addListener(new treebolic.glue.ActionListener()
        {
            @SuppressWarnings("synthetic-access")
            @Override
            public boolean onAction(Object... params)
            {
                // for (Object param : params) { System.out.print(param + " "); } System.out.println();
                String command = (String) params[0];
                if (IWidget.SEARCH.equals(command))
                {
                    widget.search(command, (String) params[1], (String) params[2], (String) params[3]);
                }
                else
                {
                    widget.search(command);
                }
                return true;
            }
        });
        toolbar.add(this.searchTool);

        // menu
        @NonNull final JButton menuButton = new JButton();
        menuButton.setToolTipText(Messages.getString("MainFrame.menutooltip"));
        @Nullable final URL menuIconUrl = MainFrame.class.getResource("images/menu.png");
        assert menuIconUrl != null;
        menuButton.setIcon(new ImageIcon(menuIconUrl));
        menuButton.setComponentPopupMenu(makePopupMenu());
        menuButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(@NonNull final MouseEvent e)
            {
                @NonNull final JPopupMenu popup = makePopupMenu();
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(menuButton);
        toolbar.validate();
        return toolbar;
    }

    /**
     * Make actions
     */
    private void makeActions()
    {
        // restart
        if (this.restartAction == null)
        {
            this.restartAction = makeAction("restart", KeyStroke.getKeyStroke('R', InputEvent.ALT_DOWN_MASK), e -> restart());
        }

        // set source
        if (this.setSourceAction == null)
        {
            this.setSourceAction = makeAction("setsource", KeyStroke.getKeyStroke('S', InputEvent.ALT_DOWN_MASK), e -> setSource());
        }

        // set source
        if (this.sourceAction == null)
        {
            this.sourceAction = makeAction("source", null, e -> source());
        }

        // set truncate
        if (this.setTruncateAction == null)
        {
            this.setTruncateAction = makeAction("settruncate", KeyStroke.getKeyStroke('T', InputEvent.ALT_DOWN_MASK), e -> setTruncate());
        }

        // set truncate
        if (this.truncateAction == null)
        {
            this.truncateAction = makeAction("truncate", null, e -> truncate());
        }

        // reset truncate
        if (this.resetTruncateAction == null)
        {
            this.resetTruncateAction = makeAction("resettruncate", KeyStroke.getKeyStroke('Z', InputEvent.ALT_DOWN_MASK), e -> resetTruncate());
        }

        // set prune
        if (this.setPruneAction == null)
        {
            this.setPruneAction = makeAction("setprune", KeyStroke.getKeyStroke('P', InputEvent.ALT_DOWN_MASK), e -> setPrune());
        }

        // set prune
        if (this.pruneAction == null)
        {
            this.pruneAction = makeAction("prune", null, e -> prune());
        }

        // settings
        if (this.colorsAction == null)
        {
            this.colorsAction = makeAction("colors", KeyStroke.getKeyStroke('C', InputEvent.ALT_DOWN_MASK), e -> colors());
        }

        // font
        if (this.fontSizeAction == null)
        {
            this.fontSizeAction = makeAction("colors", KeyStroke.getKeyStroke('F', InputEvent.ALT_DOWN_MASK), e -> fontsize());
        }

        // about
        if (this.aboutAction == null)
        {
            this.aboutAction = makeAction("about", null, e -> about());
        }
    }

    /**
     * Make action
     *
     * @param tag       text
     * @param keyStroke tooltip
     * @param listener  action listener
     * @return button
     */
    @NonNull
    private AbstractAction makeAction(final String tag, final KeyStroke keyStroke, @NonNull final ActionListener listener)
    {
        @NonNull final AbstractAction action = new AbstractAction(tag)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                listener.actionPerformed(e);
            }
        };
        // configure the action with the accelerator
        action.putValue(Action.ACCELERATOR_KEY, keyStroke);
        return action;
    }

    /**
     * Make button
     *
     * @param text    text
     * @param tooltip tooltip
     * @param image   image
     * @param action  action
     * @return button
     */
    @NonNull
    private JButton makeButton(final String text, final String tooltip, @NonNull final String image, @NonNull final AbstractAction action)
    {
        @NonNull final JButton button = new JButton(action); // new JButton(text);
        button.setText(text);
        button.setToolTipText(tooltip);
        @Nullable URL iconUrl = MainFrame.class.getResource(image);
        assert iconUrl != null;
        button.setIcon(new ImageIcon(iconUrl));

        // manually register the accelerator in the button's component input map
        final Object key = action.getValue(Action.NAME);
        button.getActionMap().put(key, action);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), key);

        return button;
    }

    /**
     * Make popup menu
     *
     * @return popup
     */
    @NonNull
    protected JPopupMenu makePopupMenu()
    {
        makeActions();

        // menu
        @NonNull final JPopupMenu menu = new JPopupMenu();
        menu.setToolTipText(Messages.getString("MainFrame.menutooltip"));

        // restart
        @NonNull final JMenuItem updateMenuItem = new JMenuItem(this.restartAction);
        updateMenuItem.setText(Messages.getString("MainFrame.update"));
        @Nullable final URL updateIcon = treebolic.fungi.browser.MainFrame.class.getResource("images/update.png");
        assert updateIcon != null;
        updateMenuItem.setIcon(new ImageIcon(updateIcon));
        menu.add(updateMenuItem);

        // source
        @NonNull final JMenuItem setSourceMenuItem = new JMenuItem(this.setSourceAction);
        setSourceMenuItem.setText(Messages.getString("MainFrame.setsource"));
        @Nullable final URL setSourceIconUrl = treebolic.fungi.browser.MainFrame.class.getResource("images/settings.png");
        assert setSourceIconUrl != null;
        setSourceMenuItem.setIcon(new ImageIcon(setSourceIconUrl));
        menu.add(setSourceMenuItem);

        @NonNull final JMenuItem sourceMenuItem = new JMenuItem(this.sourceAction);
        sourceMenuItem.setText(Messages.getString("MainFrame.source"));
        menu.add(sourceMenuItem);

        // truncate
        @NonNull final JMenuItem setTruncateMenuItem = new JMenuItem(this.setTruncateAction);
        setTruncateMenuItem.setText(Messages.getString("MainFrame.settruncate"));
        @Nullable final URL setTruncateIconUrl = treebolic.fungi.browser.MainFrame.class.getResource("images/truncate.png");
        assert setTruncateIconUrl != null;
        setTruncateMenuItem.setIcon(new ImageIcon(setTruncateIconUrl));
        menu.add(setTruncateMenuItem);

        @NonNull final JMenuItem resetTruncateMenuItem = new JMenuItem(this.resetTruncateAction);
        resetTruncateMenuItem.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.ALT_DOWN_MASK));
        resetTruncateMenuItem.setText(Messages.getString("MainFrame.resettruncate"));
        @Nullable final URL resetTruncateIconUrl = treebolic.fungi.browser.MainFrame.class.getResource("images/tree.png");
        assert resetTruncateIconUrl != null;
        resetTruncateMenuItem.setIcon(new ImageIcon(resetTruncateIconUrl));
        menu.add(resetTruncateMenuItem);

        @NonNull final JMenuItem truncateMenuItem = new JMenuItem(this.truncateAction);
        truncateMenuItem.setText(Messages.getString("MainFrame.truncate"));
        menu.add(truncateMenuItem);

        // prune
        @NonNull final JMenuItem setPruneMenuItem = new JMenuItem(this.setPruneAction);
        setPruneMenuItem.setText(Messages.getString("MainFrame.setprune"));
        setPruneMenuItem.setAccelerator(KeyStroke.getKeyStroke('P', InputEvent.ALT_DOWN_MASK));
        @Nullable final URL pruneIconUrl = treebolic.fungi.browser.MainFrame.class.getResource("images/prune.png");
        assert pruneIconUrl != null;
        setPruneMenuItem.setIcon(new ImageIcon(pruneIconUrl));
        menu.add(setPruneMenuItem);

        @NonNull final JMenuItem pruneMenuItem = new JMenuItem(this.pruneAction);
        pruneMenuItem.setText(Messages.getString("MainFrame.prune"));
        menu.add(pruneMenuItem);

        // settings
        @NonNull final JMenuItem colorsMenuItem = new JMenuItem();
        colorsMenuItem.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.ALT_DOWN_MASK));
        colorsMenuItem.setText(Messages.getString("MainFrame.colors"));
        @Nullable final URL colorsIconUrl = treebolic.fungi.browser.MainFrame.class.getResource("images/palette.png");
        assert colorsIconUrl != null;
        colorsMenuItem.setIcon(new ImageIcon(colorsIconUrl));
        colorsMenuItem.addActionListener(e -> colors());
        menu.add(colorsMenuItem);

        // font
        @NonNull final JMenuItem fontSizeItem = new JMenuItem();
        fontSizeItem.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.ALT_DOWN_MASK));
        fontSizeItem.setText(Messages.getString("MainFrame.fontsize"));
        @Nullable final URL fontSizeIconUrl = treebolic.fungi.browser.MainFrame.class.getResource("images/fontsize.png");
        assert fontSizeIconUrl != null;
        fontSizeItem.setIcon(new ImageIcon(fontSizeIconUrl));
        fontSizeItem.addActionListener(e -> fontsize());
        menu.add(fontSizeItem);

        // about
        @NonNull final JMenuItem aboutItem = new JMenuItem();
        aboutItem.setText(Messages.getString("MainFrame.about"));
        @Nullable final URL aboutIconUrl = treebolic.fungi.browser.MainFrame.class.getResource("images/about.png");
        assert aboutIconUrl != null;
        aboutItem.setIcon(new ImageIcon(aboutIconUrl));
        aboutItem.addActionListener(e -> about());
        menu.add(aboutItem);

        return menu;
    }

    // H A N D L E R S

    /**
     * Restart
     */
    protected void restart()
    {
        // reset search
        this.searchTool.resetSearch();

        // reload params
        this.source = null;
        this.parameters = this.args;

        // remove
        //noinspection DataFlowIssue
        getContentPane().remove((Component) this.widget);

        // widget
        this.widget = makeWidget();

        // assemble
        //noinspection DataFlowIssue
        getContentPane().add((Component) this.widget);
    }

    /**
     * Set source
     */
    protected void setSource()
    {
        @Nullable final String value = getSource();
        assert value != null;
        assert values != null;
        assert labels != null;
        @NonNull final SourceDialog dialog = new SourceDialog(value, values, labels);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        if (dialog.ok)
        {
            System.err.println(dialog.value);
            getPersistParameters().setProperty(SqlProperties.SOURCE, dialog.value);
            Persist.saveSettings(getPersistName(), getPersistParameters());
        }
    }

    /**
     * Choose source
     */
    protected void source()
    {
        final String source = ask(Messages.getString("MainFrame.source"), getSource());
        if (source != null)
        {
            getPersistParameters().setProperty("source", source);
            Persist.saveSettings(getPersistName(), getPersistParameters());
        }
    }

    /**
     * Get prune statement
     *
     * @return prune statement
     */
    private String getPrune()
    {
        return getPersistParameters().getProperty(SqlProperties.PRUNE_NODES, MainFrame.DEFAULTPRUNE);
    }

    /**
     * Prune select
     */
    protected void prune()
    {
        final String prune = ask(Messages.getString("MainFrame.prune"), getPrune());
        if (prune != null)
        {
            getPersistParameters().setProperty(SqlProperties.PRUNE_NODES, prune);
            getPersistParameters().setProperty(SqlProperties.PRUNE_TREEEDGES, prune);
            Persist.saveSettings(getPersistName(), getPersistParameters());
        }
    }

    /**
     * Truncate
     */
    protected void truncate()
    {
        final String truncate = ask(Messages.getString("MainFrame.truncate"), getPersistParameters().getProperty(SqlProperties.TRUNCATE_NODES));
        if (truncate != null)
        {
            getPersistParameters().setProperty(SqlProperties.TRUNCATE_NODES, truncate);
            getPersistParameters().setProperty(SqlProperties.TRUNCATE_TREEEDGES, truncate);
            Persist.saveSettings(getPersistName(), getPersistParameters());
        }
    }

    /**
     * Set truncate
     */
    protected void setTruncate()
    {
        if (this.source != null)
        {
            @NonNull final String[] fields = this.source.split(",");
            if (fields.length > 1)
            {
                if (fields[1].startsWith("where:"))
                {
                    @NonNull final String whereClause = fields[1].substring(6);
                    getPersistParameters().setProperty(SqlProperties.TRUNCATE_NODES, whereClause);
                    getPersistParameters().setProperty(SqlProperties.TRUNCATE_TREEEDGES, whereClause);
                    Persist.saveSettings(getPersistName(), getPersistParameters());
                }
            }
        }
    }

    /**
     * Reset truncate
     */
    protected void resetTruncate()
    {
        this.source = null;
        getPersistParameters().remove(SqlProperties.TRUNCATE_NODES);
        getPersistParameters().remove(SqlProperties.TRUNCATE_TREEEDGES);
        Persist.saveSettings(getPersistName(), getPersistParameters());
    }

    /**
     * Set prune
     */
    protected void setPrune()
    {
        final String value = getPrune();
        @NonNull final PruneDialog dialog = new PruneDialog(value);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        if (dialog.ok)
        {
            // System.err.println(dialog.value);
            getPersistParameters().setProperty(SqlProperties.PRUNE_NODES, dialog.value);
            getPersistParameters().setProperty(SqlProperties.PRUNE_TREEEDGES, dialog.value);
            Persist.saveSettings(getPersistName(), getPersistParameters());
        }
    }

    /**
     * Font size
     */
    protected void fontsize()
    {
        @Nullable final Integer fontSize = askRange(Messages.getString("MainFrame.fontsizeprompt"), getPersistParameters().getProperty("fontsize"), 12, 64);
        if (fontSize != null)
        {
            getPersistParameters().setProperty("fontsize", fontSize.toString());
            Persist.saveSettings(getPersistName(), getPersistParameters());
        }
    }

    /**
     * Colors
     */
    protected void colors()
    {
        @NonNull final ColorSettingsDialog dialog = new ColorSettingsDialog(getPersistParameters(), this);
        dialog.setModal(true);
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        if (dialog.ok)
        {
            Persist.saveSettings(getPersistName(), getPersistParameters());
        }
    }

    /**
     * About
     */
    protected void about()
    {
        @NonNull final JDialog dialog = new AboutDialog();
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    // H E L P E R S

    /**
     * Ask
     *
     * @param message message
     * @param value   initial value
     * @return input
     */
    protected String ask(@NonNull final String message, final String value)
    {
        @NonNull final String[] lines = message.split("\n");
        return JOptionPane.showInputDialog(null, lines, value);
    }

    /**
     * Ask int value in range
     *
     * @param message0 message
     * @param value    initial value
     * @param min      min
     * @param max      max
     * @return value
     */
    @Nullable
    protected Integer askRange(@NonNull final String message0, final String value, @SuppressWarnings("SameParameterValue") final int min, @SuppressWarnings("SameParameterValue") final int max)
    {
        final String message = String.format(message0, min, max);
        @NonNull final String[] lines = message.split("\n");
        final String string = JOptionPane.showInputDialog(null, lines, value);
        try
        {
            final int newValue = Integer.parseInt(string);
            return newValue >= min && newValue <= max ? newValue : null;
        }
        catch (final NumberFormatException e2)
        {
            //
        }
        return null;
    }
}
