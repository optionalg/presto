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
package com.facebook.presto.tests;

import com.facebook.presto.Session;
import com.facebook.presto.connector.ConnectorId;
import com.facebook.presto.metadata.SessionPropertyManager;
import com.facebook.presto.testing.LocalQueryRunner;
import com.facebook.presto.testing.MaterializedResult;
import com.facebook.presto.tpch.TpchConnectorFactory;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.facebook.presto.SystemSessionProperties.REORDER_JOINS;
import static com.facebook.presto.spi.type.DoubleType.DOUBLE;
import static com.facebook.presto.spi.type.VarcharType.VARCHAR;
import static com.facebook.presto.testing.MaterializedResult.resultBuilder;
import static com.facebook.presto.testing.TestingSession.TESTING_CATALOG;
import static com.facebook.presto.testing.TestingSession.testSessionBuilder;
import static com.facebook.presto.tpch.TpchMetadata.TINY_SCHEMA_NAME;
import static org.testng.Assert.assertEquals;

public class TestLocalQueries
        extends AbstractTestQueries
{
    public TestLocalQueries()
    {
        super(createLocalQueryRunner());
    }

    public static LocalQueryRunner createLocalQueryRunner()
    {
        Session defaultSession = testSessionBuilder()
                .setCatalog("local")
                .setSchema(TINY_SCHEMA_NAME)
                .setSystemProperty(REORDER_JOINS, "true")
                .build();

        LocalQueryRunner localQueryRunner = new LocalQueryRunner(defaultSession);

        // add the tpch catalog
        // local queries run directly against the generator
        localQueryRunner.createCatalog(
                defaultSession.getCatalog().get(),
                new TpchConnectorFactory(1),
                ImmutableMap.of());

        localQueryRunner.getMetadata().addFunctions(CUSTOM_FUNCTIONS);

        SessionPropertyManager sessionPropertyManager = localQueryRunner.getMetadata().getSessionPropertyManager();
        sessionPropertyManager.addSystemSessionProperties(TEST_SYSTEM_PROPERTIES);
        sessionPropertyManager.addConnectorSessionProperties(new ConnectorId(TESTING_CATALOG), TEST_CATALOG_PROPERTIES);

        return localQueryRunner;
    }

    @Test
    public void testShowColumnStats()
            throws Exception
    {
        // FIXME Add tests for more complex scenario with more stats
        MaterializedResult result = computeActual("SHOW STATS nation");

        MaterializedResult expectedStatistics = resultBuilder(getSession(), VARCHAR, DOUBLE)
                .row(null, 25.0)
                .build();

        assertEquals(result, expectedStatistics);
    }

    @Test
    public void testInequalityJoinWithDate()
            throws Exception
    {
        assertQuery(
                "select o.orderkey, o.orderdate, l.shipdate from orders o, lineitem l where l.orderkey=o.orderkey and l.shipdate < o.orderdate + INTERVAL '10' DAY",
                "select o.orderkey, o.orderdate, l.shipdate from orders o, lineitem l where l.orderkey=o.orderkey and l.shipdate < DATEADD('DAY', 10, o.orderdate)");
    }

    @Test
    public void testInequalityJoin()
            throws Exception
    {
        assertQuery("select l.suppkey, n.nationkey, l.partkey, n.regionkey from nation n, lineitem l where l.suppkey=n.nationkey and l.partkey < n.regionkey");
    }

    @Test
    public void testReversedInequalityJoin()
            throws Exception
    {
        assertQuery("select l.suppkey, n.nationkey, l.partkey, n.regionkey from nation n, lineitem l where l.suppkey=n.nationkey and l.partkey > n.regionkey");
    }
}
