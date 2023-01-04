package treebolic.wordnet.browser;

import java.awt.*;
import java.util.Properties;

import javax.swing.*;

import treebolic.annotations.NonNull;
import treebolic.annotations.Nullable;

/**
 * Settings dialog
 *
 * @author Bernard Bou
 */
public class SettingsDialog extends JDialog
{
	/**
	 * Link panel
	 */
	@Nullable
	private final LinkFilterSettingsPane linkFilterSettingsPane;

	/**
	 * Color settings pane
	 */
	@Nullable
	private final ColorSettingsPane colorsSettingsPane;

	/**
	 * Result
	 */
	public boolean ok;

	/**
	 * Constructor
	 *
	 * @param settings settings
	 * @param owner    frame owner
	 */
	public SettingsDialog(final Properties settings, final Frame owner)
	{
		this(settings, owner, true, true);
	}

	/**
	 * Constructor
	 *
	 * @param settings      settings
	 * @param owner         frame owner
	 * @param hasLinkFilter whether to put link filter settings panel
	 * @param hasColors     whether dialog has color settings panel
	 */
	public SettingsDialog(final Properties settings, final Frame owner, final boolean hasLinkFilter, final boolean hasColors)
	{
		super(owner);
		this.linkFilterSettingsPane = hasLinkFilter ? new LinkFilterSettingsPane(settings) : null;
		this.colorsSettingsPane = hasColors ? new ColorSettingsPane(settings) : null;

		setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
		setTitle(Messages.getString("SettingsDialog.title"));

		@NonNull final JTabbedPane tabbedPane = new JTabbedPane();
		if (hasLinkFilter)
		{
			tabbedPane.add(Messages.getString("SettingsDialog.links"), this.linkFilterSettingsPane);
		}
		if (hasColors)
		{
			tabbedPane.add(Messages.getString("SettingsDialog.colors"), this.colorsSettingsPane);
		}

		final Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(tabbedPane, BorderLayout.CENTER);
		container.add(makeCommandPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Make command panel
	 *
	 * @return command panel
	 */
	@NonNull
	private JPanel makeCommandPanel()
	{
		@NonNull final JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout());
		commandPanel.add(makeOkButton(), null);
		commandPanel.add(makeCancelButton(), null);
		commandPanel.add(makeApplyButton(), null);
		return commandPanel;
	}

	/**
	 * Make ok button
	 *
	 * @return ok button
	 */
	@NonNull
	private JButton makeOkButton()
	{
		@NonNull final JButton okButton = new JButton();
		okButton.setText(Messages.getString("SettingsDialog.ok"));
		okButton.addActionListener(e -> {
			SettingsDialog.this.ok = true;
			setVisible(false);
		});
		return okButton;
	}

	/**
	 * Make cancel button
	 *
	 * @return cancel button
	 */
	@NonNull
	private JButton makeCancelButton()
	{
		@NonNull final JButton cancelButton = new JButton();
		cancelButton.setText(Messages.getString("SettingsDialog.cancel"));
		cancelButton.addActionListener(e -> setVisible(false));
		return cancelButton;
	}

	/**
	 * Make apply button
	 *
	 * @return apply button
	 */
	@NonNull
	private JButton makeApplyButton()
	{
		@NonNull final JButton applyButton = new JButton();
		applyButton.setText(Messages.getString("SettingsDialog.save"));
		applyButton.addActionListener(e -> save());
		return applyButton;
	}

	private void save()
	{
		if (SettingsDialog.this.linkFilterSettingsPane != null)
		{
			SettingsDialog.this.linkFilterSettingsPane.get();
		}
		if (SettingsDialog.this.colorsSettingsPane != null)
		{
			SettingsDialog.this.colorsSettingsPane.get();
		}
	}

	@Override
	public void setVisible(final boolean show)
	{
		if (show)
		{
			if (this.linkFilterSettingsPane != null)
			{
				this.linkFilterSettingsPane.set();
			}
			if (this.colorsSettingsPane != null)
			{
				this.colorsSettingsPane.set();
			}
			pack();
			center();
		}
		else
		{
			if (this.ok)
			{
				save();
			}
		}

		super.setVisible(show);
	}

	private void center()
	{
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension componentSize = getSize();
		if (componentSize.height > screenSize.height)
		{
			componentSize.height = screenSize.height;
		}
		if (componentSize.width > screenSize.width)
		{
			componentSize.width = screenSize.width;
		}
		setLocation((screenSize.width - componentSize.width) / 2, (screenSize.height - componentSize.height) / 2);
	}

	/**
	 * Main
	 *
	 * @param args command line arguments
	 */
	static public void main(final String[] args)
	{
		@NonNull final Properties settings = new Properties();

		@NonNull final SettingsDialog dialog = new SettingsDialog(settings, null);
		dialog.setModal(true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		System.out.println("FILTER: " + settings.getProperty(LinkReference.KEYRELATIONFILTER));

		System.exit(0);
	}
}
