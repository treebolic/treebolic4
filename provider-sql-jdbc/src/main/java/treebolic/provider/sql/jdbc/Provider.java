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
import treebolic.annotations.Nullable;
import treebolic.provider.sql.AbstractProvider;

/**
 * Provider for SQL, JDBC implementation
 *
 * @author Bernard Bou
 */
public class Provider extends AbstractProvider<Provider.JdbcDatabase, Provider.JdbcCursor, SQLException>
{
    /**
     * JDBC cursor
     */
    static public class JdbcCursor implements AbstractProvider.Cursor<SQLException>
    {
        @NonNull
        private final Statement statement;

        @NonNull
        private final ResultSet resultSet;

        /**
         * Constructor
         *
         * @param statement statement
         * @param resultSet resultset
         */
        public JdbcCursor(@NonNull final Statement statement, @NonNull final ResultSet resultSet)
        {
            this.statement = statement;
            this.resultSet = resultSet;
        }

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

        @Override
        public boolean moveToNext() throws SQLException
        {
            return this.resultSet.next();
        }

        @Override
        public int getPosition() throws SQLException
        {
            return this.resultSet.getRow();
        }

        @Override
        public int getColumnIndex(@NonNull final String columnName) throws SQLException
        {
            return this.resultSet.findColumn(columnName);
        }

        @Override
        public boolean isNull(int columnIndex) throws SQLException
        {
            this.resultSet.getObject(columnIndex);
            return this.resultSet.wasNull();
        }

        @Override
        public String getString(int columnIndex) throws SQLException
        {
            return this.resultSet.getString(columnIndex);
        }

        @NonNull
        @Override
        public Integer getInt(int columnIndex) throws SQLException
        {
            return this.resultSet.getInt(columnIndex);
        }

        @NonNull
        @Override
        public Float getFloat(int columnIndex) throws SQLException
        {
            return this.resultSet.getFloat(columnIndex);
        }

        @NonNull
        @Override
        public Double getDouble(int columnIndex) throws SQLException
        {
            return this.resultSet.getDouble(columnIndex);
        }
    }

    /**
     * JDBC
     */
    static public class JdbcDatabase implements AbstractProvider.Database<JdbcCursor, SQLException>
    {
        @Nullable
        private Connection connection;

        /**
         * Constructor
         *
         * @param properties properties
         */
        public JdbcDatabase(@NonNull Properties properties)
        {
            // url/user/password
            @NonNull final String url = makeUrl(properties);
            System.out.println("Sql provider URL: " + url);
            final String user = properties.getProperty("user");
            final String passwd = properties.getProperty("passwd");

            try
            {
                // connect
                this.connection = DriverManager.getConnection(url, user, passwd);

            }
            catch (final SQLException exception)
            {
                this.connection = null;
                System.err.println("Sql exception : " + exception.getMessage());
            }
        }

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

        @NonNull
        @Override
        public JdbcCursor query(@NonNull final String nodesSql) throws SQLException
        {
            assert this.connection != null;
            try
            {
                final Statement statement = this.connection.createStatement();
                final ResultSet resultSet = statement.executeQuery(nodesSql);
                return new JdbcCursor(statement, resultSet);
            }
            catch (SQLException se)
            {
                System.err.println(se);
                throw se;
            }
        }

        /**
         * @return the connection
         */
        @Nullable
        public Connection getConnection()
        {
            return this.connection;
        }

        // H E L P E R

        /**
         * Make URL
         *
         * @param properties properties
         * @return url string
         */
        @NonNull
        private String makeUrl(@NonNull final Properties properties)
        {
            String uRL = properties.getProperty("url");
            if (uRL == null || uRL.isEmpty())
            {
                final String protocol = properties.getProperty("protocol");
                String server = properties.getProperty("server");
                if (server == null || server.isEmpty())
                {
                    server = "localhost";
                }
                final String database = properties.getProperty("database");
                uRL = protocol + "://" + server + "/" + database;
            }
            final String database = properties.getProperty("database");
            uRL = uRL.replaceAll("%database%", Matcher.quoteReplacement(database));
            return uRL;
        }
    }

    @NonNull
    @Override
    protected JdbcDatabase openDatabase(@NonNull final Properties properties)
    {
        return new JdbcDatabase(properties);
    }
}
