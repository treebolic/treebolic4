/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>
 */

package treebolic.provider.wordnet.jwi;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import treebolic.annotations.NonNull;

/**
 * Gloss
 *
 * @author Bernard Bou
 */
public final class Gloss
{
	/**
	 * Fields (field[0] is definition, field[>0] samples)
	 */
	@NonNull
	private final String[] gloss;

	/**
	 * Constructor
	 *
	 * @param gloss WordNet gloss
	 */
	public Gloss(@NonNull final String gloss)
	{
		this.gloss = split(gloss.trim());
		for (int i = 1; i < this.gloss.length; i++)
		{
			this.gloss[i] = this.gloss[i].trim();
		}
	}

	@NonNull
	@Override
	public String toString()
	{
		@NonNull final StringBuilder sb = new StringBuilder();
		sb.append(getDefinition());
		sb.append(" {");
		for (int i = 1; i < this.gloss.length; i++)
		{
			if (i != 1)
			{
				sb.append(";");
			}
			sb.append(this.gloss[i]);
		}
		sb.append('}');
		return sb.toString();
	}

	//noinspection
	private static final String REGEX = "\\\"[^\\\"]*\\\"";

	private static final Pattern pattern = Pattern.compile(REGEX);

	/**
	 * Split gloss into definition + sample*
	 *
	 * @param gloss gloss
	 * @return fields (field[0] is definition, field[>0] samples)
	 */
	@NonNull
	static private String[] split(@NonNull final String gloss)
	{
		int quoteCount = 0;
		for (int p = 0; (p = gloss.indexOf('"', p + 1)) != -1; )
		{
			quoteCount++;
		}
		if (quoteCount % 2 != 0)
		{
			System.err.println("UNEVEN QUOTES IN " + gloss);
		}

		@NonNull final Matcher matcher = pattern.matcher(gloss); // get a matcher object
		int count = 0;
		int split = -1;
		while (matcher.find())
		{
			if (count == 0)
			{
				split = matcher.start();
			}
			count++;
		}

		@NonNull final String[] result = new String[count + 1];

		// [0] definition
		@NonNull String definition = split == -1 ? gloss : gloss.substring(0, split);
		definition = definition.replaceFirst("[;\\s]*$", "");
		result[0] = definition;

		// [1-n] samples
		matcher.reset();
		for (count = 1; matcher.find(); count++)
		{
			String sample = matcher.group();
			if (sample.startsWith("\"") && sample.endsWith("\""))
			{
				sample = sample.substring(1, sample.length() - 1);
			}
			result[count] = sample;
		}
		return result;
	}

	/**
	 * Get definition
	 *
	 * @return definition
	 */
	public String getDefinition()
	{
		return this.gloss[0];
	}

	/**
	 * Get samples
	 *
	 * @return samples
	 */
	@NonNull
	public String[] getSamples()
	{
		return Arrays.copyOfRange(this.gloss, 1, this.gloss.length);
	}
}
