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

package com.facebook.presto.plugin.nullconnector;

import com.facebook.presto.spi.type.TypeManager;
import com.google.inject.Binder;
import com.google.inject.Module;

import static com.google.inject.Scopes.SINGLETON;

public class NullModule
        implements Module
{
    private final TypeManager typeManager;

    public NullModule(TypeManager typeManager)
    {
        this.typeManager = typeManager;
    }

    @Override
    public void configure(Binder binder)
    {
        binder.bind(NullConnector.class).in(SINGLETON);
        binder.bind(NullMetadata.class).in(SINGLETON);
        binder.bind(NullSplitManager.class).in(SINGLETON);
        binder.bind(NullHandleResolver.class).in(SINGLETON);
        binder.bind(NullPageSinkProvider.class).in(SINGLETON);
        binder.bind(NullPageSourceProvider.class).in(SINGLETON);
        binder.bind(TypeManager.class).toInstance(typeManager);
    }
}
