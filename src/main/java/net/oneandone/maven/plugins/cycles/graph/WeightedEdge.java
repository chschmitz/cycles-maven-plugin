/**
 * Copyright 1&1 Internet AG, https://github.com/1and1/
 *
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
package net.oneandone.maven.plugins.cycles.graph;

import java.util.Set;

import net.oneandone.maven.plugins.cycles.classes.ClassDependency;

import com.google.common.base.Objects;

/**
 * An edge type with an integer identity and a real-valued weight.
 * 
 * @author chschmitz
 */
public final class WeightedEdge {
    private int id;
    private Set<ClassDependency> dependencies;

    /**
     * @param id the id
     * @param dependencies the class dependencies causing this edge
     */
    public WeightedEdge(int id, Set<ClassDependency> dependencies) {
        super();
        this.id = id;
        this.dependencies = dependencies;
    }

    public int getId() {
        return id;
    }

    public double getWeight() {
        return dependencies.size();
    }

    public Set<ClassDependency> getDependencies() {
        return dependencies;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WeightedEdge other = (WeightedEdge) obj;
        return Objects.equal(id, other.id);
    }
}
