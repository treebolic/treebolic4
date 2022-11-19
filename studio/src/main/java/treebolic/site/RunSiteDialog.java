/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.site;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import treebolic.commons.Persist;
import treebolic.commons.Utils;
import treebolic.generator.dialogs.XSiteDialog;

/**
 * Run site dialog
 *
 * @author Bernard Bou
 */
public class RunSiteDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	// C O M P O N E N T S

	/**
	 * Home input
	 */
	private JTextField homeTextField;

	/**
	 * Command information
	 */
	private JLabel commandLabel;

	/**
	 * Properties (input/output)
	 */
	private final Properties properties;

	// C O N S T R U C T OR

	/**
	 * Constructor
	 *
	 * @param properties
	 *        settings
	 */
	public RunSiteDialog(final Properties properties)
	{
		super();
		this.properties = properties;
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		setTitle(Messages.getString("RunSiteDialog.title")); 
		setResizable(true);

		// text fields
		this.homeTextField = new JTextField(16);

		// images
		final Icon icon = new ImageIcon(MakeSiteDialog.class.getResource("images/siterun.png")); 
		final JLabel imageLabel = new JLabel();
		imageLabel.setIcon(icon);
		imageLabel.setVerticalTextPosition(SwingConstants.TOP);
		imageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		imageLabel.setText(Messages.getString("RunSiteDialog.header")); 
		imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// labels
		final JLabel htmlFileLabel = new JLabel(Messages.getString("RunSiteDialog.file")); 

		this.commandLabel = new JLabel();
		this.commandLabel.setToolTipText(Messages.getString("RunSiteDialog.tooltip_command")); 
		this.commandLabel.setEnabled(false);
		this.commandLabel.setFont(new Font(Font.DIALOG, Font.ITALIC, 10));

		// buttons
		final JButton runButton = new JButton(Messages.getString("RunSiteDialog.run")); 
		final JButton cancelButton = new JButton(Messages.getString("RunSiteDialog.cancel")); 
		final JButton browserButton = new JButton(Messages.getString("RunSiteDialog.browser")); 
		final JButton modeButton = new JButton(Messages.getString("RunSiteDialog.mode")); 

		// panels
		final JPanel dataPanel = new JPanel();
		dataPanel.setLayout(new FlowLayout());
		dataPanel.add(htmlFileLabel);
		dataPanel.add(this.homeTextField);

		final JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout());
		commandPanel.add(this.commandLabel);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(browserButton);
		buttonPanel.add(modeButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(runButton);

		// event
		runButton.addActionListener(e -> {
			run();
			setVisible(false);
		});

		cancelButton.addActionListener(e -> setVisible(false));

		browserButton.addActionListener(e -> {
			final String message = Messages.getString("RunSiteDialog.prompt_exe") + '\n'; 
			final String[] lines = message.split("\n"); 
			final String browser = JOptionPane.showInputDialog(null, lines);
			if (browser != null && !browser.isEmpty())
			{
				RunSiteDialog.this.properties.setProperty("browser", browser); 
				RunSiteDialog.this.commandLabel.setText(makeCommand());
			}
		});

		modeButton.addActionListener(e -> {
			final XSiteDialog dialog = new XSiteDialog(RunSiteDialog.this.properties);
			dialog.setModal(true);
			dialog.setVisible(true);
			if (dialog.ok)
			{
				RunSiteDialog.this.commandLabel.setText(makeCommand());
			}
		});

		// change handler
		this.homeTextField.getDocument().addDocumentListener(new DocumentListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void removeUpdate(final DocumentEvent e)
			{
				RunSiteDialog.this.commandLabel.setText(makeCommand());
			}

			@SuppressWarnings("synthetic-access")
			@Override
			public void insertUpdate(final DocumentEvent e)
			{
				RunSiteDialog.this.commandLabel.setText(makeCommand());
			}

			@Override
			public void changedUpdate(final DocumentEvent e)
			{
				// do nothing
			}
		});

		// assemble
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		panel.add(imageLabel);
		panel.add(dataPanel);
		panel.add(commandPanel);
		panel.add(buttonPanel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		setContentPane(panel);
	}

	// C O M M A N D . H A N D L I N G

	/**
	 * Make command
	 *
	 * @return command
	 */
	private String makeCommand()
	{
		String command;
		command = this.properties.getProperty("browser", "");  
		if (command.isEmpty())
			return null;
		command += " "; 

		final String mode = this.properties.getProperty("mode"); 
		if ("NET".equals(mode)) 
		{
			final String server = this.properties.getProperty("server", "");  
			final String directory = this.properties.getProperty("directory", "");  
			command += "http://" + server + "/";  
			if (!directory.isEmpty())
			{
				command += directory;
				command += "/"; 
			}
			command += this.homeTextField.getText();
		}
		else
		{
			String path = this.properties.getProperty("path", "");  
			if (!path.endsWith(File.separator))
			{
				path += File.separator;
			}
			path += this.homeTextField.getText();
			final File folder = new File(path);
			try
			{
				path = folder.toURI().toURL().toString();
			}
			catch (final MalformedURLException exception)
			{
				// do nothing
			}
			command += path;
		}
		return command;
	}

	/**
	 * Run command
	 */
	private void run()
	{
		final String command = makeCommand();
		if (command != null && !command.isEmpty())
		{
			try
			{
				Runtime.getRuntime().exec(command);
			}
			catch (final Exception e)
			{
				System.err.println("Cannot run " + command); 
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean flag)
	{
		if (flag)
		{
			// read properties into components
			this.homeTextField.setText(this.properties.getProperty("page")); 
			this.commandLabel.setText(makeCommand());

			pack();
			Utils.center(this);
		}
		else
		{
			// update properties from components
			this.properties.setProperty("page", this.homeTextField.getText()); 
		}

		super.setVisible(flag);
	}

	/**
	 * Main
	 *
	 * @param args
	 *        arguments
	 */
	static public void main(final String[] args)
	{
		UIManager.put("swing.boldMetal", false); 
		final Properties settings = Persist.getSettings("treebolic-generator"); 
		final RunSiteDialog dialog = new RunSiteDialog(settings);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setVisible(true);
		Persist.saveSettings("treebolic-generator", settings); 
		System.exit(0);
	}
}
