package treebolic.wordnet.browser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.*;

/**
 * Settings dialog
 *
 * @author Bernard Bou
 */
public class ColorSettingsPane extends JPanel
{
	private static final int TOP0 = 20;

	private static final int TOP = 5;

	private static final int BOTTOM = 20;

	private static final int LEFT = 20;

	private static final int RIGHT = 10;

	private static final Insets IL_1 = new Insets(ColorSettingsPane.TOP0, ColorSettingsPane.LEFT, 0, ColorSettingsPane.RIGHT);

	private static final Insets IL = new Insets(ColorSettingsPane.TOP, ColorSettingsPane.LEFT, 0, ColorSettingsPane.RIGHT);

	private static final Insets IL_n = new Insets(ColorSettingsPane.TOP, ColorSettingsPane.LEFT, ColorSettingsPane.BOTTOM, ColorSettingsPane.RIGHT);

	private static final Insets IT_1 = new Insets(ColorSettingsPane.TOP0, ColorSettingsPane.LEFT, 0, ColorSettingsPane.RIGHT);

	private static final Insets IT = new Insets(ColorSettingsPane.TOP, ColorSettingsPane.LEFT, 0, ColorSettingsPane.RIGHT);

	private static final Insets IT_n = new Insets(ColorSettingsPane.TOP, ColorSettingsPane.LEFT, ColorSettingsPane.BOTTOM, ColorSettingsPane.RIGHT);

	/**
	 * ColorPad
	 */
	private ColorPad[] colorPads;

	/**
	 * Settings
	 */
	private final Properties settings;

	/**
	 * Constructor
	 *
	 * @param settings settings
	 */
	public ColorSettingsPane(final Properties settings)
	{
		super();

		this.settings = settings;
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		setFont(new Font(Font.DIALOG, Font.PLAIN, 10));

		final JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new GridBagLayout());

		final int n = ColorReference.values().length;
		this.colorPads = new ColorPad[n];

		int i = 0;
		for (final ColorReference color : ColorReference.values())
		{
			final JLabel label = new JLabel(color.getLabel());
			this.colorPads[i] = ColorSettingsPane.makeColorPad();
			colorPanel.add(label, new GridBagConstraints(0, i, 1, 1, 0., 0., GridBagConstraints.EAST, GridBagConstraints.NONE, i == 0 ? ColorSettingsPane.IL_1 : i == n - 1 ? ColorSettingsPane.IL_n : ColorSettingsPane.IL, 0, 0));
			colorPanel.add(this.colorPads[i], new GridBagConstraints(1, i, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, i == 0 ? ColorSettingsPane.IT_1 : i == n - 1 ? ColorSettingsPane.IT_n : ColorSettingsPane.IT, 0, 0));
			i++;
		}

		final JButton clearButton = new JButton(Messages.getString("ColorSettingsPane.clear"));
		clearButton.addActionListener(new ActionListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (int i = 0; i < ColorReference.values().length; i++)
				{
					ColorSettingsPane.this.colorPads[i].setBackground(null);
					ColorSettingsPane.this.colorPads[i].repaint();
				}
			}
		});

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.add(clearButton, new GridBagConstraints(0, 0, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.setLayout(new BorderLayout());
		this.add(colorPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Make color pad
	 *
	 * @return color pad
	 */
	private static ColorPad makeColorPad()
	{
		final ColorPad colorPad = new ColorPad();
		colorPad.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent event)
			{
				final Color color = JColorChooser.showDialog(colorPad, Messages.getString("ColorSettingsPane.title"), colorPad.getBackground());
				if (color != null)
				{
					colorPad.setBackground(color);
				}
				else
				{
					colorPad.setBackground(null);
				}
				colorPad.repaint();
			}
		});
		return colorPad;
	}

	private void settingsToColorPad(final ColorPad pad, final String key)
	{
		final String value = this.settings.getProperty(key, null);
		if (value != null)
		{
			Color color;
			try
			{
				color = Color.decode("0x" + value);
				pad.setBackground(color);
			}
			catch (final NumberFormatException e)
			{
				try
				{
					color = Color.decode(value);
				}
				catch (final NumberFormatException e2)
				{
					return;
				}
			}
			pad.setBackground(color);
		}
	}

	private void colorPadToSettings(final ColorPad pad, final String key)
	{
		final Color color = pad.getBackground();
		if (color == null)
		{
			this.settings.remove(key);
			return;
		}
		this.settings.setProperty(key, Integer.toHexString(color.getRGB()).substring(2));
	}

	public void set()
	{
		int i = 0;
		for (final ColorReference color : ColorReference.values())
		{
			settingsToColorPad(this.colorPads[i], color.key);
			i++;
		}
	}

	public void get()
	{
		int i = 0;
		for (final ColorReference color : ColorReference.values())
		{
			colorPadToSettings(this.colorPads[i], color.key);
			i++;
		}
	}
}
