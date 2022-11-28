/**
 * Title : Treebolic SQL provider
 * Description : Treebolic SQL provider
 * Version : 3.x
 * Copyright : (c) 2001-2014
 * Terms of use : see license agreement at http://treebolic.sourceforge.net/en/license.htm
 * Author : Bernard Bou
 */
package treebolic.provider.sql.jdbc;

import java.sql.*;
import java.util.Properties;
import java.util.regex.Matcher;

import treebolic.annotations.NonNull;
import treebolic.provider.sql.AbstractProvider;

/**
 * Provider for SQL, JDBC implementation
 *
 * @author Bernard Bou
 */
public class Provider extends AbstractProvider<Provider.JdbcDatabase, Provider.JdbcCursor, SQLException>
{
	static class JdbcCursor implements AbstractProvider.Cursor<SQLException>
	{
		@NonNull private final Statement statement;

		@NonNull private final ResultSet resultSet;

		/**
		 * Constructor
		 * @param statement statement
		 * @param resultSet resultset
		 */
		public JdbcCursor(@NonNull final Statement statement, @NonNull final ResultSet resultSet)
		{
			this.statement = statement;
			this.resultSet = resultSet;
		}

		/*
		 * (non-Javadoc)
		 * @see treebolic.provider.sqlx.AbstractProvider.Cursor#close()
		 */
		@Override
		public void close()
		{
			try
			{
				this.resultSet.close();
			}
			catch (SQLException exception)
			{
				//
				exception.printStackTrace();
			}
			try
			{
				this.statement.close();
			}
			catch (SQLException exception)
			{
				//
				exception.printStackTrace();
			}
		}

		/*
		 * (non-Javadoc)
		 * @see treebolic.provider.sqlx.AbstractProvider.Cursor#moveToNext()
		 */
		@Override
		public boolean moveToNext() throws SQLException
		{
			return this.resultSet.next();
		}

		/*
		 * (non-Javadoc)
		 * @see treebolic.provider.sqlx.AbstractProvider.Cursor#getPosition()
		 */
		@Override
		public int getPosition() throws SQLException
		{
			return this.resultSet.getRow();
		}

		/*
		 * (non-Javadoc)
		 * @see treebolic.provider.sqlx.AbstractProvider.Cursor#getColumnIndex(java.lang.String)
		 */
		@Override
		public int getColumnIndex(@NonNull final String columnName) throws SQLException
		{
			if (this.resultSet.isAfterLast())
			{
				return -1;
			}
			return this.resultSet.findColumn(columnName);
		}

		/*
		 * (non-Javadoc)
		 * @see treebolic.provider.sqlx.AbstractProvider.Cursor#isNull(int)
		 */
		@Override
		public boolean isNull(int columnIndex) throws SQLException
		{
			this.resultSet.getObject(columnIndex);
			return this.resultSet.wasNull();
		}

		/*
		 * (non-Javadoc)
		 * @see treebolic.provider.sqlx.AbstractProvider.Cursor#getString(int)
		 */
		@Override
		public String getString(int columnIndex) throws SQLException
		{
			return this.resultSet.getString(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see treebolic.provider.sqlx.AbstractProvider.Cursor#getInt(int)
		 */
		@Override
		public Integer getInt(int columnIndex) throws SQLException
		{
			return this.resultSet.getInt(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see treebolic.provider.sqlx.AbstractProvider.Cursor#getFloat(int)
		 */
		@Override
		public Float getFloat(int columnIndex) throws SQLException
		{
			return this.resultSet.getFloat(columnIndex);
		}

		/*
		 * (non-Javadoc)
		 * @see treebolic.provider.sqlx.AbstractProvider.Cursor#getDouble(int)
		 */
		@Override
		public Double getDouble(int columnIndex) throws SQLException
		{
			return this.resultSet.getDouble(columnIndex);
		}
	}

	static class JdbcDatabase implements AbstractProvider.Database<JdbcCursor, SQLException>
	{
		private Connection connection;

		public JdbcDatabase(Properties properties)
		{
			// url/user/password
			final String url = makeUrl(properties);
			System.out.println("Sqlx provider URL: " + url); //$NON-NLS-1$
			final String user = properties.getProperty("user"); //$NON-NLS-1$
			final String passwd = properties.getProperty("passwd"); //$NON-NLS-1$

			try
			{
				// connect
				this.connection = DriverManager.getConnection(url, user, passwd);

			}
			catch (final SQLException exception)
			{
				this.connection = null;
				System.err.println("Sqlx exception : " + exception.getMessage()); //$NON-NLS-1$
			}
		}

		/*
		 * (non-Javadoc)
		 * @see treebolic.provider.sqlx.AbstractProvider.Database#close()
		 */
		@Override
		public void close()
		{
			if (this.connection != null)
			{
				try
				{
					this.connection.close();
				}
				catch (SQLException exception)
				{
					exception.printStackTrace();
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * @see treebolic.provider.sqlx.AbstractProvider.Database#query(java.lang.String)
		 */
		@Override
		public JdbcCursor query(@NonNull final String nodesSql) throws SQLException
		{
			final Statement statement = this.connection.createStatement();
			final ResultSet resultSet = statement.executeQuery(nodesSql);
			return new JdbcCursor(statement, resultSet);
		}

		/**
		 * @return the connection
		 */
		public Connection getConnection()
		{
			return this.connection;
		}

		// H E L P E R

		/**
		 * Make URL
		 *
		 * @param properties
		 *        properties
		 * @return url string
		 */
		private String makeUrl(final Properties properties)
		{
			String uRL = properties.getProperty("url"); //$NON-NLS-1$
			if (uRL == null || uRL.isEmpty())
			{
				final String protocol = properties.getProperty("protocol"); //$NON-NLS-1$
				String server = properties.getProperty("server"); //$NON-NLS-1$
				if (server == null || server.isEmpty())
				{
					server = "localhost"; //$NON-NLS-1$
				}
				final String database = properties.getProperty("database"); //$NON-NLS-1$
				uRL = protocol + "://" + server + "/" + database; //$NON-NLS-1$ //$NON-NLS-2$
			}
			final String database = properties.getProperty("database"); //$NON-NLS-1$
			uRL = uRL.replaceAll("%database%", Matcher.quoteReplacement(database)); //$NON-NLS-1$
			return uRL;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see treebolic.provider.sqlx.AbstractProvider#openDatabase(java.util.Properties)
	 */
	@Override
	protected JdbcDatabase openDatabase(Properties properties)
	{
		return new JdbcDatabase(properties);
	}
}
