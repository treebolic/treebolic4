/*
 * Copyright (c) 2022. Bernard Bou
 */
package treebolic.propertyview;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import treebolic.propertyview.PropertyView.Attribute;
import treebolic.propertyview.PropertyView.AttributeType;

class Renderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;

	/**
	 * View
	 */
	private final PropertyView propertyView;

	/**
	 * Color panel
	 */
	private final JPanel colorPanel;

	/**
	 * Alternate row column
	 */
	private final Color altRowColor = new Color(0xf0f0f0);

	/**
	 * The bold font
	 */
	private final Font boldFont = new Font(Font.DIALOG, Font.BOLD, 12);

	/**
	 * The italic font
	 */
	private final Font italicFont = new Font(Font.DIALOG, Font.ITALIC, 12);

	// icons

	/**
	 * Mandatory attribute icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon mandatoryIcon = new ImageIcon(Renderer.class.getResource("images/mandatory.png"));

	/**
	 * Optional attribute icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon optionalIcon = new ImageIcon(Renderer.class.getResource("images/optional.png"));

	/**
	 * Attribute icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon attributeIcon = new ImageIcon(Renderer.class.getResource("images/attribute.png"));

	/**
	 * Id icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon idIcon = new ImageIcon(Renderer.class.getResource("images/id.png"));

	/**
	 * Id reference icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon refIdcon = new ImageIcon(Renderer.class.getResource("images/node.png"));

	/**
	 * Text icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon textIcon = new ImageIcon(Renderer.class.getResource("images/text.png"));

	/**
	 * Long text icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon longTextIcon = new ImageIcon(Renderer.class.getResource("images/longtext.png"));

	/**
	 * Image icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon imageIcon = new ImageIcon(Renderer.class.getResource("images/image.png"));

	/**
	 * Link icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon linkIcon = new ImageIcon(Renderer.class.getResource("images/link.png"));

	/**
	 * Color icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon colorIcon = new ImageIcon(Renderer.class.getResource("images/color.png"));

	/**
	 * Stroke icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon strokeIcon = new ImageIcon(Renderer.class.getResource("images/stroke.png"));

	/**
	 * Terminator icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon terminatorIcon = new ImageIcon(Renderer.class.getResource("images/terminator.png"));

	/**
	 * Font icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon fontIcon = new ImageIcon(Renderer.class.getResource("images/font.png"));

	/**
	 * Font size icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon fontSizeIcon = new ImageIcon(Renderer.class.getResource("images/fontsize.png"));

	/**
	 * Boolean icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon booleanIcon = new ImageIcon(Renderer.class.getResource("images/boolean.png"));

	/**
	 * Floats icon
	 */
	@SuppressWarnings("ConstantConditions")
	private final ImageIcon floatsIcon = new ImageIcon(Renderer.class.getResource("images/floats.png"));

	/**
	 * Constructor
	 *
	 * @param propertyView property view
	 */
	public Renderer(final PropertyView propertyView)
	{
		this.propertyView = propertyView;
		this.colorPanel = new JPanel();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column)
	{
		setIcon(null);
		setFont(null);
		setForeground(Color.BLACK);
		setBackground(row % 2 == 0 ? Color.WHITE : this.altRowColor);

		switch (column)
		{
			case PropertyView.VALUE:
			{
				final Attribute attribute = (Attribute) value;
				final Object attributeValue = attribute == null ? null : attribute.value;
				if (attributeValue == null)
				{
					setText(PropertyView.defaultString);
					return this;
				}

				final AttributeType type = attribute.descriptor.type;
				switch (type)
				{
					case COLOR:
						this.colorPanel.setBackground(((Color) attributeValue));
						return this.colorPanel;

					case LABEL:
						setText(attributeValue.toString().replaceAll("\n", "\\\\n"));
						setFont(this.boldFont);
						assert Color.RED != null;
						setForeground(Color.RED.darker());
						return this;

					case TEXT:
					case LONGTEXT:
						setText(attributeValue.toString().replaceAll("\n", "\\\\n"));
						return this;

					case BOOLEAN:
					case FLOAT:
					case INTEGER:
						setText(attributeValue.toString());
						return this;

					case FLOATS:
						setIcon(this.floatsIcon);
						setText(attributeValue.toString());
						return this;

					case ID:
						setText((String) attributeValue);
						return this;

					case REFID:
						setText((String) attributeValue);
						setIcon(this.refIdcon);
						return this;

					case LINK:
						setText(PropertyView.decode((String) attributeValue));
						setForeground(Color.BLUE);
						setFont(this.italicFont);
						setIcon(this.linkIcon);
						return this;

					case FONTFACE:
						setText((String) attributeValue);
						setFont(new Font((String) attributeValue, Font.PLAIN, 18));
						return this;

					case IMAGE:
						final String imageFile = (String) attributeValue;
						setIcon(makeIcon(imageFile));
						setText(imageFile);
						return this;

					case FONTSIZE:
						setIcon(this.fontSizeIcon);
						setText((String) attributeValue);
						return this;

					case STROKE:
						setIcon(PropertyView.strokeIcons.get((String) attributeValue));
						setText((String) attributeValue);
						setForeground(Color.BLUE);
						return this;

					case TERMINATOR:
						setIcon(PropertyView.terminatorIcons.get((String) attributeValue));
						setText((String) attributeValue);
						setForeground(Color.BLUE);
						return this;

					default:
						System.err.println("Renderer doesn't handle this class: " + attribute.descriptor.name + " type:" + attribute.descriptor.type);
						return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				}
			}

			case PropertyView.STATUS:
			{
				final Boolean booleanValue = (Boolean) value;
				setIcon(booleanValue ? this.mandatoryIcon : this.optionalIcon);
				setText(null);
				setHorizontalAlignment(SwingConstants.CENTER);
				return this;
			}

			case PropertyView.TYPE:
			{
				final AttributeType propertyTypeValue = (AttributeType) value;
				switch (propertyTypeValue)
				{
					case TEXT:
						setIcon(this.textIcon);
						break;

					case LONGTEXT:
						setIcon(this.longTextIcon);
						break;

					case IMAGE:
						setIcon(this.imageIcon);
						break;

					case LINK:
						setIcon(this.linkIcon);
						break;

					case COLOR:
						setIcon(this.colorIcon);
						break;

					case BOOLEAN:
						setIcon(this.booleanIcon);
						break;

					case FLOATS:
						setIcon(this.floatsIcon);
						break;

					case ID:
						setIcon(this.idIcon);
						break;

					case REFID:
						setIcon(this.refIdcon);
						break;

					case STROKE:
						setIcon(this.strokeIcon);
						break;

					case TERMINATOR:
						setIcon(this.terminatorIcon);
						break;

					case FONTFACE:
						setIcon(this.fontIcon);
						break;

					case FONTSIZE:
						setIcon(this.fontSizeIcon);
						break;

					default:
						setIcon(this.attributeIcon);
						break;
				}
				setText(null);
				setHorizontalAlignment(SwingConstants.CENTER);
				return this;
			}

			case PropertyView.NAME:
			{
				final String name = (String) value;
				setText(name);
				return this;
			}
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

	/**
	 * Make icon
	 *
	 * @param imageFile image file
	 * @return icon
	 */
	private Icon makeIcon(final String imageFile)
	{
		try
		{
			final URL url = new URL(this.propertyView.getImageRepository(), imageFile);
			return new ImageIcon(url);
		}
		catch (final MalformedURLException e)
		{
			return null;
		}
	}
}