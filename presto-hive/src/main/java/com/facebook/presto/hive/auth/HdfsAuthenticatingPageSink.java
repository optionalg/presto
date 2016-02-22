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

package com.facebook.presto.hive.auth;

import com.facebook.presto.spi.ConnectorPageSink;
import com.facebook.presto.spi.ConnectorSession;
import com.facebook.presto.spi.Page;
import com.facebook.presto.spi.block.Block;
import io.airlift.slice.Slice;

import java.util.Collection;

public class HdfsAuthenticatingPageSink
        implements ConnectorPageSink
{
    private final HadoopAuthentication authentication;
    private final ConnectorSession connectorSession;
    private final ConnectorPageSink targetPageSink;

    public HdfsAuthenticatingPageSink(ConnectorSession connectorSession, HadoopAuthentication authentication, ConnectorPageSink targetPageSink)
    {
        this.connectorSession = connectorSession;
        this.authentication = authentication;
        this.targetPageSink = targetPageSink;
    }

    @Override
    public void appendPage(Page page, Block sampleWeightBlock)
    {
        authentication.doAs(connectorSession.getUser(), () -> targetPageSink.appendPage(page, sampleWeightBlock));
    }

    @Override
    public Collection<Slice> finish()
    {
        return authentication.doAs(connectorSession.getUser(), targetPageSink::finish);
    }

    @Override
    public void abort()
    {
        authentication.doAs(connectorSession.getUser(), targetPageSink::abort);
    }
}
