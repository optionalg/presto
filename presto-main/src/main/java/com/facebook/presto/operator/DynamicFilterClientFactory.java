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
package com.facebook.presto.operator;

import com.facebook.presto.execution.TaskId;
import io.airlift.http.client.HttpClient;
import io.airlift.json.JsonCodec;

import javax.inject.Provider;

import java.net.URI;

public class DynamicFilterClientFactory
        implements DynamicFilterClientSupplier
{
    private final Provider<URI> coordinatorURIProvider;
    private final HttpClient httpClient;
    private final JsonCodec<DynamicFilterSummary> summaryJsonCodec;

    public DynamicFilterClientFactory(
            Provider<URI> coordinatorURIProvider,
            HttpClient httpClient,
            JsonCodec<DynamicFilterSummary> summaryJsonCodec)
    {
        this.coordinatorURIProvider = coordinatorURIProvider;
        this.httpClient = httpClient;
        this.summaryJsonCodec = summaryJsonCodec;
    }

    @Override
    public DynamicFilterClient get(TaskId taskId, String source, int expectedDriversCount)
    {
        return new DynamicFilterClient(summaryJsonCodec, coordinatorURIProvider.get(), httpClient, taskId, source, expectedDriversCount);
    }
}