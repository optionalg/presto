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
package com.facebook.presto.kafka;

import com.facebook.presto.kafka.util.EmbeddedKafka;
import com.facebook.presto.tests.AbstractTestDistributedQueries;
import io.airlift.tpch.TpchTable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.facebook.presto.kafka.KafkaQueryRunner.createKafkaQueryRunner;
import static com.facebook.presto.kafka.util.EmbeddedKafka.createEmbeddedKafka;
import static io.airlift.testing.Closeables.closeAllRuntimeException;

@Test
public class TestKafkaDistributed
        extends AbstractTestDistributedQueries
{
    private final EmbeddedKafka embeddedKafka;

    public TestKafkaDistributed()
            throws IOException
    {
        this(createEmbeddedKafka());
    }

    public TestKafkaDistributed(EmbeddedKafka embeddedKafka)
    {
        super(createKafkaQueryRunner(embeddedKafka, TpchTable.getTables()));
        this.embeddedKafka = embeddedKafka;
    }

    @AfterClass(alwaysRun = true)
    public void destroy()
    {
        closeAllRuntimeException(queryRunner, embeddedKafka);
    }

    //
    // Kafka connector does not support table creation.
    //

    @Override
    public void testCreateTable()
    {
    }

    @Override
    public void testCreateTableAsSelect()
    {
    }

    @Override
    public void testSymbolAliasing()
    {
    }

    //
    // Kafka connector does not support views.
    //

    @Override
    public void testView()
    {
    }

    @Override
    public void testCompatibleTypeChangeForView()
    {
        // Kafka connector currently does not support views
    }

    @Override
    public void testCompatibleTypeChangeForView2()
    {
        // Kafka connector currently does not support views
    }

    @Override
    public void testViewMetadata()
    {
    }

    @Test
    public void testViewCaseSensitivity()
    {
        // Kafka connector currently does not support views
    }

    //
    // Kafka connector does not insert.
    //

    @Override
    public void testInsert()
    {
    }

    //
    // Kafka connector does not delete.
    //

    @Override
    public void testDelete()
    {
    }

    //
    // Kafka connector does not table rename.
    //

    @Override
    public void testRenameTable()
    {
    }

    //
    // Kafka connector does not rename column.
    //

    @Override
    public void testRenameColumn()
    {
    }

    //
    // Kafka connector does not add column.
    //

    @Override
    public void testAddColumn()
    {
    }
}
