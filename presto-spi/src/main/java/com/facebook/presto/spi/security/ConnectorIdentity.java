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
package com.facebook.presto.spi.security;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static java.util.Objects.requireNonNull;

public class ConnectorIdentity
{
    private final String user;
    private final Optional<Principal> principal;
    private final Optional<SelectedRole> role;

    public ConnectorIdentity(String user, Optional<Principal> principal, Optional<SelectedRole> role)
    {
        this.user = requireNonNull(user, "user is null");
        this.principal = requireNonNull(principal, "principal is null");
        this.role = requireNonNull(role, "role is null");
    }

    public String getUser()
    {
        return user;
    }

    public Optional<Principal> getPrincipal()
    {
        return principal;
    }

    public Optional<SelectedRole> getRole()
    {
        return role;
    }

    public Identity toIdentity(String catalogName)
    {
        return new Identity(user, principal, role.isPresent() ? singletonMap(catalogName, role.get()) : emptyMap());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConnectorIdentity that = (ConnectorIdentity) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(principal, that.principal) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(user, principal, role);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("ConnectorIdentity{");
        sb.append("user='").append(user).append('\'');
        principal.ifPresent(principal -> sb.append(", principal=").append(principal));
        role.ifPresent(role -> sb.append(", role=").append(role));
        sb.append('}');
        return sb.toString();
    }
}
