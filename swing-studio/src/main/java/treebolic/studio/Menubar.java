/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.studio;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Menu bar
 *
 * @author Bernard Bou
 */
public class Menubar extends JMenuBar
{
	// D A T A

	/**
	 * Controller (command sink)
	 */
	private Controller controller;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public Menubar()
	{
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		@NonNull final JMenuItem openMenuItem = makeItem(Messages.getString("Menubar.open"), "open.png", Controller.Code.OPEN, KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
		@NonNull final JMenuItem openUrlMenuItem = makeItem(Messages.getString("Menubar.open_url"), "openurl.png", Controller.Code.OPENURL, null);
		@NonNull final JMenuItem importXslMenuItem = makeItem(Messages.getString("Menubar.open_xsl"), "import.png", Controller.Code.IMPORTXSL, null);
		@NonNull final JMenuItem importProviderMenuItem = makeItem(Messages.getString("Menubar.open_provider"), "importprovider.png", Controller.Code.IMPORTPROVIDER, null);
		@NonNull final JMenuItem newMenuItem = makeItem(Messages.getString("Menubar.new"), "newdoc.png", Controller.Code.NEW, KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
		@NonNull final JMenuItem saveMenuItem = makeItem(Messages.getString("Menubar.save"), "save.png", Controller.Code.SAVE, KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
		@NonNull final JMenuItem saveAsMenuItem = makeItem(Messages.getString("Menubar.saveas"), "saveas.png", Controller.Code.SAVEAS, null);
		@NonNull final JMenuItem exportMenuItem = makeItem(Messages.getString("Menubar.export"), "export.png", Controller.Code.EXPORT, null);
		@NonNull final JMenuItem exportSettingsMenuItem = makeItem(Messages.getString("Menubar.export_rendering"), null, Controller.Code.EXPORTSETTINGS, null);
		@NonNull final JMenuItem zipMenuItem = makeItem(Messages.getString("Menubar.zip"), "zip.png", Controller.Code.ZIP, null);
		@NonNull final JMenuItem unZipMenuItem = makeItem(Messages.getString("Menubar.unzip"), "unzip.png", Controller.Code.UNZIP, null);
		@NonNull final JMenuItem serializeMenuItem = makeItem(Messages.getString("Menubar.serialize"), "serialize.png", Controller.Code.SERIALIZE, null);
		@NonNull final JMenuItem deserializeMenuItem = makeItem(Messages.getString("Menubar.deserialize"), "deserialize.png", Controller.Code.DESERIALIZE, null);

		@NonNull final JMenuItem newElementMenuItem = makeItem(Messages.getString("Menubar.new_item"), "new.png", Controller.Code.NEWELEMENT, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
		@NonNull final JMenuItem removeElementMenuItem = makeItem(Messages.getString("Menubar.remove_item"), "delete.png", Controller.Code.REMOVEELEMENT, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		@NonNull final JMenuItem normalizeIdsMenuItem = makeItem(Messages.getString("Menubar.normalize_ids"), null, Controller.Code.NORMALIZEIDS, null);
		@NonNull final JMenuItem listImagesMenuItem = makeItem(Messages.getString("Menubar.list_images"), null, Controller.Code.LISTIMAGES, null);
		@NonNull final JMenuItem listLinksMenuItem = makeItem(Messages.getString("Menubar.list_links"), null, Controller.Code.LISTLINKS, null);
		@NonNull final JMenuItem listMountsMenuItem = makeItem(Messages.getString("Menubar.list_mounts"), null, Controller.Code.LISTMOUNTS, null);
		@NonNull final JMenuItem listIdsMenuItem = makeItem(Messages.getString("Menubar.list_ids"), null, Controller.Code.LISTIDS, null);

		@NonNull final JMenuItem settingsMenuItem = makeItem(Messages.getString("Menubar.settings"), "settings.png", Controller.Code.SETTINGS, null);
		// final JMenuItem baseMenuItem = makeItem(Messages.getString("Menubar.site"), "site.png", Controller.Code.SETTINGSBASE, null);
		// final JMenuItem urlMenuItem = makeItem(Messages.getString("Menubar.url"), "url.png", Controller.Code.SETTINGSURL, null);
		@NonNull final JCheckBoxMenuItem treebolicRendererMenuItem = makeCheckboxItem(Messages.getString("Menubar.renderer"), null, Controller.Code.OPTIONTREEBOLICRENDERER, null, MainFrame.hasTreebolicRendering);
		@NonNull final JCheckBoxMenuItem validateMenuItem = makeCheckboxItem(Messages.getString("Menubar.validate"), null, Controller.Code.OPTIONVALIDATEXML, null, Controller.validate);
		@NonNull final JCheckBoxMenuItem focusParentMenuItem = makeCheckboxItem(Messages.getString("Menubar.focus"), null, Controller.Code.OPTIONFOCUSPARENT, null, treebolic.studio.tree.Tree.focusParent);

		@NonNull final JMenuItem dtdMenuItem = makeItem(Messages.getString("Menubar.dtd"), "dtd.png", Controller.Code.DTD, null);

		@NonNull final JMenuItem aboutMenuItem = makeItem(Messages.getString("Menubar.about"), "about.png", Controller.Code.ABOUT, null);
		@NonNull final JMenuItem helpMenuItem = makeItem(Messages.getString("Menubar.help"), "help.png", Controller.Code.HELP, null);

		@NonNull final JMenu filesMenu = new JMenu();
		filesMenu.setText(Messages.getString("Menubar.menu_files"));
		filesMenu.add(newMenuItem);
		filesMenu.add(openMenuItem);
		filesMenu.add(openUrlMenuItem);
		filesMenu.add(importXslMenuItem);
		filesMenu.add(importProviderMenuItem);
		filesMenu.addSeparator();
		filesMenu.add(saveMenuItem);
		filesMenu.add(saveAsMenuItem);
		filesMenu.add(exportMenuItem);
		filesMenu.add(exportSettingsMenuItem);
		filesMenu.addSeparator();
		filesMenu.add(zipMenuItem);
		filesMenu.add(unZipMenuItem);
		filesMenu.addSeparator();
		filesMenu.add(serializeMenuItem);
		filesMenu.add(deserializeMenuItem);

		@NonNull final JMenu elementsMenu = new JMenu();
		elementsMenu.setText(Messages.getString("Menubar.menu_items"));
		elementsMenu.add(newElementMenuItem);
		elementsMenu.add(removeElementMenuItem);
		elementsMenu.addSeparator();
		elementsMenu.add(normalizeIdsMenuItem);
		elementsMenu.addSeparator();
		elementsMenu.add(listImagesMenuItem);
		elementsMenu.add(listLinksMenuItem);
		elementsMenu.add(listMountsMenuItem);
		elementsMenu.add(listIdsMenuItem);

		@NonNull final JMenu optionsMenu = new JMenu();
		optionsMenu.setText(Messages.getString("Menubar.menu_options"));
		optionsMenu.add(settingsMenuItem);
		// optionsMenu.add(baseMenuItem);
		// optionsMenu.add(urlMenuItem);
		optionsMenu.addSeparator();
		optionsMenu.add(treebolicRendererMenuItem);
		optionsMenu.add(validateMenuItem);
		optionsMenu.add(focusParentMenuItem);

		@NonNull final JMenu helpMenu = new JMenu();
		helpMenu.setText(Messages.getString("Menubar.menu_help"));
		helpMenu.add(aboutMenuItem);
		helpMenu.add(helpMenuItem);

		@NonNull final JMenu toolsMenu = new JMenu();
		toolsMenu.setText(Messages.getString("Menubar.menu_tools"));
		toolsMenu.add(dtdMenuItem);

		this.add(filesMenu);
		this.add(elementsMenu);
		this.add(optionsMenu);
		this.add(toolsMenu);
		this.add(helpMenu);
	}

	/**
	 * Set controller
	 *
	 * @param controller controller
	 */
	public void setController(final Controller controller)
	{
		this.controller = controller;
	}

	/**
	 * Make menu item
	 *
	 * @param text           text
	 * @param image          image
	 * @param command        command code
	 * @param acceleratorKey accelerator key
	 * @return menu item
	 */
	@NonNull
	private JMenuItem makeItem(final String text, @Nullable final String image, @NonNull final Controller.Code command, @Nullable final KeyStroke acceleratorKey)
	{
		@NonNull final JMenuItem item = new JMenuItem();
		item.setText(text);
		if (image != null)
		{
			@Nullable final URL imageUrl = Menubar.class.getResource("images/" + image);
			assert imageUrl != null;
			item.setIcon(new ImageIcon(imageUrl));
		}
		if (acceleratorKey != null)
		{
			item.setAccelerator(acceleratorKey);
		}
		item.addActionListener(e -> Menubar.this.controller.execute(command, 0));
		return item;
	}

	/**
	 * Make menu item
	 *
	 * @param text           text
	 * @param image          image
	 * @param command        command code
	 * @param acceleratorKey accelerator key
	 * @return menu item
	 */
	@NonNull
	private JCheckBoxMenuItem makeCheckboxItem(final String text, @Nullable @SuppressWarnings("SameParameterValue") final String image, @NonNull final Controller.Code command, @Nullable @SuppressWarnings("SameParameterValue") final KeyStroke acceleratorKey, final boolean state)
	{
		@NonNull final JCheckBoxMenuItem item = new JCheckBoxMenuItem();
		item.setText(text);
		item.setSelected(state);
		if (image != null)
		{
			@Nullable final URL imageUrl = Menubar.class.getResource("images/" + image);
			assert imageUrl != null;
			item.setIcon(new ImageIcon(imageUrl));
		}
		if (acceleratorKey != null)
		{
			item.setAccelerator(acceleratorKey);
		}
		item.addActionListener(e -> Menubar.this.controller.execute(command, 0));
		return item;
	}
}