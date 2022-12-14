package treebolic.wordnet.browser;

import java.awt.*;
import java.util.Properties;

import javax.swing.*;

import treebolic.annotations.NonNull;

/**
 * Settings dialog
 *
 * @author Bernard Bou
 */
public class LinkFilterSettingsPane extends JPanel
{
	private static final int TOP0 = 20;

	private static final int TOP = 0;

	private static final int BOTTOM = 20;

	private static final int LEFT = 20;

	private static final int RIGHT = 10;

	private static final Insets IL_1 = new Insets(LinkFilterSettingsPane.TOP0, LinkFilterSettingsPane.LEFT, 0, LinkFilterSettingsPane.RIGHT);

	private static final Insets IL = new Insets(LinkFilterSettingsPane.TOP, LinkFilterSettingsPane.LEFT, 0, LinkFilterSettingsPane.RIGHT);

	private static final Insets IL_n = new Insets(LinkFilterSettingsPane.TOP, LinkFilterSettingsPane.LEFT, LinkFilterSettingsPane.BOTTOM, LinkFilterSettingsPane.RIGHT);

	private static final Insets IT_1 = new Insets(LinkFilterSettingsPane.TOP0, LinkFilterSettingsPane.LEFT, 0, LinkFilterSettingsPane.RIGHT);

	private static final Insets IT = new Insets(LinkFilterSettingsPane.TOP, LinkFilterSettingsPane.LEFT, 0, LinkFilterSettingsPane.RIGHT);

	private static final Insets IT_n = new Insets(LinkFilterSettingsPane.TOP, LinkFilterSettingsPane.LEFT, LinkFilterSettingsPane.BOTTOM, LinkFilterSettingsPane.RIGHT);

	/**
	 * Checkboxes
	 */
	private JCheckBox[] checkBoxes;

	/**
	 * Settings
	 */
	private final Properties settings;

	/**
	 * Filter
	 */
	private long filter;

	/**
	 * Constructor
	 *
	 * @param settings settings
	 */
	public LinkFilterSettingsPane(final Properties settings)
	{
		super();

		this.settings = settings;
		this.filter = read();
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		setFont(new Font(Font.DIALOG, Font.PLAIN, 10));

		@NonNull final JPanel linkPanel = new JPanel();
		linkPanel.setLayout(new GridBagLayout());

		final int n = LinkReference.values().length;
		this.checkBoxes = new JCheckBox[n];

		int i = 0;
		for (@NonNull final LinkReference link : LinkReference.values())
		{
			@NonNull final JLabel label = new JLabel(link.getLabel());
			this.checkBoxes[i] = LinkFilterSettingsPane.makeCheckBox();
			linkPanel.add(label, new GridBagConstraints(0, i, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, i == 0 ? LinkFilterSettingsPane.IL_1 : i == n - 1 ? LinkFilterSettingsPane.IL_n : LinkFilterSettingsPane.IL, 0, 0));
			linkPanel.add(this.checkBoxes[i], new GridBagConstraints(1, i, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, i == 0 ? LinkFilterSettingsPane.IT_1 : i == n - 1 ? LinkFilterSettingsPane.IT_n : LinkFilterSettingsPane.IT, 0, 0));
			i++;
		}
		@NonNull final JButton setButton = new JButton(Messages.getString("LinkFilterSettingsPane.set"));
		setButton.addActionListener(e -> {
			for (int i12 = 0; i12 < LinkReference.values().length; i12++)
			{
				LinkFilterSettingsPane.this.checkBoxes[i12].setSelected(true);
			}
		});
		@NonNull final JButton resetButton = new JButton(Messages.getString("LinkFilterSettingsPane.reset"));
		resetButton.addActionListener(e -> {
			for (int i1 = 0; i1 < LinkReference.values().length; i1++)
			{
				LinkFilterSettingsPane.this.checkBoxes[i1].setSelected(false);
			}
		});

		@NonNull final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.add(setButton, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		buttonPanel.add(resetButton, new GridBagConstraints(1, 0, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.setLayout(new BorderLayout());
		this.add(linkPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Make filter checkbox
	 *
	 * @return checkbox
	 */
	@NonNull
	private static JCheckBox makeCheckBox()
	{
		return new JCheckBox();
	}

	/**
	 * Read filter
	 *
	 * @return filter
	 */
	private long read()
	{
		final String value = this.settings.getProperty(LinkReference.KEYRELATIONFILTER, null);
		long l = LinkReference.baseline();
		if (value != null)
		{
			if (value.startsWith("0x"))
			{
				try
				{
					l = Long.valueOf(value.substring(2), 16);
				}
				catch (final NumberFormatException e)
				{
					//
				}
			}
			else
			{
				try
				{
					l = Long.parseLong(value);
				}
				catch (final NumberFormatException e2)
				{
					//
				}
			}

		}
		return l;
	}

	/**
	 * Set filter values to pane
	 */
	public void set()
	{
		toCheckBoxes();
	}

	/**
	 * Get filter values from pane
	 */
	public void get()
	{
		fromCheckBoxes();
		@NonNull final String value = "0x" + Long.toHexString(this.filter);
		this.settings.setProperty(LinkReference.KEYRELATIONFILTER, value);
	}

	private void toCheckBoxes()
	{
		int i = 0;
		for (@NonNull final LinkReference link : LinkReference.values())
		{
			this.checkBoxes[i].setSelected(link.test(this.filter));
			i++;
		}
	}

	private void fromCheckBoxes()
	{
		int i = 0;
		for (@NonNull final LinkReference link : LinkReference.values())
		{
			if (this.checkBoxes[i].isSelected())
			{
				this.filter |= link.mask();
			}
			else
			{
				this.filter &= ~link.mask();
			}

			i++;
		}
	}
}
