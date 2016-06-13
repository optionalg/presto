/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.tests.utils;

import com.facebook.presto.jdbc.PrestoConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcDriverUtils
{
    public static void setSessionProperty(Connection connection, String key, String value) throws SQLException
    {
        if (usingFacebookJdbcDriver(connection)) {
            PrestoConnection prestoConnection = connection.unwrap(PrestoConnection.class);
            prestoConnection.setSessionProperty(key, value);
        }
        else if (usingSimbaJdbcDriver(connection)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(String.format("set session %s=%s", key, value));
            }
        }
        else {
            throw new IllegalStateException();
        }
    }

    public static boolean usingFacebookJdbcDriver(Connection connection)
    {
        return getClassNameForJdbcDriver(connection).equals("com.facebook.presto.jdbc.PrestoConnection");
    }

    public static boolean usingSimbaJdbcDriver(Connection connection)
    {
        String className = getClassNameForJdbcDriver(connection);
        return  className.equals("com.teradata.jdbc.jdbc4.S4Connection") ||
                className.equals("com.teradata.jdbc.jdbc41.S41Connection") ||
                className.equals("com.teradata.jdbc.jdbc42.S42Connection");
    }

    public static boolean usingSimbaJdbc4Driver(Connection connection)
    {
        return getClassNameForJdbcDriver(connection).contains("jdbc4.");
    }

    private static String getClassNameForJdbcDriver(Connection connection)
    {
        return connection.getClass().getCanonicalName();
    }

    private JdbcDriverUtils() {}
}
