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
package com.facebook.presto.execution;

import com.facebook.presto.Session;
import com.facebook.presto.metadata.MetadataManager;
import com.facebook.presto.security.AllowAllAccessControl;
import com.facebook.presto.spi.PrestoException;
import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.tree.AllColumns;
import com.facebook.presto.sql.tree.Prepare;
import com.facebook.presto.sql.tree.QualifiedName;
import com.facebook.presto.sql.tree.Query;
import com.facebook.presto.transaction.TransactionManager;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import static com.facebook.presto.SessionTestUtils.TEST_SESSION;
import static com.facebook.presto.metadata.MetadataManager.createTestMetadataManager;
import static com.facebook.presto.sql.QueryUtil.selectList;
import static com.facebook.presto.sql.QueryUtil.simpleQuery;
import static com.facebook.presto.sql.QueryUtil.table;
import static com.facebook.presto.transaction.TransactionManager.createTestTransactionManager;
import static io.airlift.concurrent.Threads.daemonThreadsNamed;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.testng.Assert.assertEquals;

public class TestPrepareTask
{
    private final MetadataManager metadata = createTestMetadataManager();
    private final ExecutorService executor = newCachedThreadPool(daemonThreadsNamed("stage-executor-%s"));

    @AfterClass(alwaysRun = true)
    public void tearDown()
            throws Exception
    {
        executor.shutdownNow();
    }

    @Test
    public void testPrepare()
    {
        String statementName = "my_query";
        Query query = simpleQuery(selectList(new AllColumns()), table(QualifiedName.of("foo")));
        String sqlString = "PREPARE my_query FROM SELECT * FROM foo";
        QueryStateMachine stateMachine = executePrepare(statementName, query, sqlString, TEST_SESSION);
        assertEquals(stateMachine.getAddedPreparedStatements(), ImmutableMap.of(statementName, "SELECT *\nFROM\n  foo\n"));
    }

    @Test(expectedExceptions = PrestoException.class)
    public void testPrepareNameExists()
    {
        String statementName = "existing_query";
        String query1 = "SELECT bar, baz from foo";
        Session session = TEST_SESSION.withPreparedStatement(statementName, query1);

        Query query2 = simpleQuery(selectList(new AllColumns()), table(QualifiedName.of("foo")));
        String sqlString = "PREPARE existing_query FROM SELECT * FROM foo";

        executePrepare(statementName, query2, sqlString, session);
    }

    private QueryStateMachine executePrepare(String statementName, Query query, String sqlString, Session session)
    {
        TransactionManager transactionManager = createTestTransactionManager();
        QueryStateMachine stateMachine = QueryStateMachine.begin(new QueryId("query"), sqlString, session, URI.create("fake://uri"), false, transactionManager, metadata, executor);
        Prepare prepare = new Prepare(statementName, query);
        new PrepareTask(new SqlParser()).execute(prepare, transactionManager, metadata, new AllowAllAccessControl(), stateMachine);
        return stateMachine;
    }
}
