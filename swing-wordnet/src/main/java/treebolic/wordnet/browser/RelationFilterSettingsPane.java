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
public class RelationFilterSettingsPane extends JPanel
{
	private static final int TOP0 = 20;

	private static final int TOP = 0;

	private static final int BOTTOM = 20;

	private static final int LEFT = 20;

	private static final int RIGHT = 10;

	private static final Insets IL_1 = new Insets(RelationFilterSettingsPane.TOP0, RelationFilterSettingsPane.LEFT, 0, RelationFilterSettingsPane.RIGHT);

	private static final Insets IL = new Insets(RelationFilterSettingsPane.TOP, RelationFilterSettingsPane.LEFT, 0, RelationFilterSettingsPane.RIGHT);

	private static final Insets IL_n = new Insets(RelationFilterSettingsPane.TOP, RelationFilterSettingsPane.LEFT, RelationFilterSettingsPane.BOTTOM, RelationFilterSettingsPane.RIGHT);

	private static final Insets IT_1 = new Insets(RelationFilterSettingsPane.TOP0, RelationFilterSettingsPane.LEFT, 0, RelationFilterSettingsPane.RIGHT);

	private static final Insets IT = new Insets(RelationFilterSettingsPane.TOP, RelationFilterSettingsPane.LEFT, 0, RelationFilterSettingsPane.RIGHT);

	private static final Insets IT_n = new Insets(RelationFilterSettingsPane.TOP, RelationFilterSettingsPane.LEFT, RelationFilterSettingsPane.BOTTOM, RelationFilterSettingsPane.RIGHT);

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
	public RelationFilterSettingsPane(final Properties settings)
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

		@NonNull final JPanel relationPanel = new JPanel();
		relationPanel.setLayout(new GridBagLayout());

		final int n = RelationReference.values().length;
		this.checkBoxes = new JCheckBox[n];

		int i = 0;
		for (@NonNull final RelationReference relation : RelationReference.values())
		{
			@NonNull final JLabel label = new JLabel(relation.getLabel());
			this.checkBoxes[i] = RelationFilterSettingsPane.makeCheckBox();
			relationPanel.add(label, new GridBagConstraints(0, i, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, i == 0 ? RelationFilterSettingsPane.IL_1 : i == n - 1 ? RelationFilterSettingsPane.IL_n : RelationFilterSettingsPane.IL, 0, 0));
			relationPanel.add(this.checkBoxes[i], new GridBagConstraints(1, i, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, i == 0 ? RelationFilterSettingsPane.IT_1 : i == n - 1 ? RelationFilterSettingsPane.IT_n : RelationFilterSettingsPane.IT, 0, 0));
			i++;
		}
		@NonNull final JButton setButton = new JButton(Messages.getString("RelationFilterSettingsPane.set"));
		setButton.addActionListener(e -> {
			for (int i12 = 0; i12 < RelationReference.values().length; i12++)
			{
				RelationFilterSettingsPane.this.checkBoxes[i12].setSelected(true);
			}
		});
		@NonNull final JButton resetButton = new JButton(Messages.getString("RelationFilterSettingsPane.reset"));
		resetButton.addActionListener(e -> {
			for (int i1 = 0; i1 < RelationReference.values().length; i1++)
			{
				RelationFilterSettingsPane.this.checkBoxes[i1].setSelected(false);
			}
		});

		@NonNull final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.add(setButton, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		buttonPanel.add(resetButton, new GridBagConstraints(1, 0, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.setLayout(new BorderLayout());
		this.add(relationPanel, BorderLayout.CENTER);
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
		final String value = this.settings.getProperty(RelationReference.KEYRELATIONFILTER, null);
		long l = RelationReference.baseline();
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
		this.settings.setProperty(RelationReference.KEYRELATIONFILTER, value);
	}

	private void toCheckBoxes()
	{
		int i = 0;
		for (@NonNull final RelationReference relation : RelationReference.values())
		{
			this.checkBoxes[i].setSelected(relation.test(this.filter));
			i++;
		}
	}

	private void fromCheckBoxes()
	{
		int i = 0;
		for (@NonNull final RelationReference relation : RelationReference.values())
		{
			if (this.checkBoxes[i].isSelected())
			{
				this.filter |= relation.mask();
			}
			else
			{
				this.filter &= ~relation.mask();
			}

			i++;
		}
	}
}
