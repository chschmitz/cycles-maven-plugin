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
package net.oneandone.maven.plugins.cycles.classes;

import java.util.Set;

/**
 * Represents a single package dependency.
 * 
 * @author chschmitz
 */
public final class PackageDependency {
    private String from;
    private String to;
    private Set<ClassDependency> classDependencies;
    
    /**
     * @param from the depending package
     * @param to the package that <code>from</code> depends on
     * @param classDependencies the underlying class dependencies
     */
    public PackageDependency(String from, String to, Set<ClassDependency> classDependencies) {
        this.from = from;
        this.to = to;
        this.classDependencies = classDependencies;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Set<ClassDependency> getClassDependencies() {
        return classDependencies;
    }

    @Override
    public String toString() {
        return from + "->" + to;
    }
}
