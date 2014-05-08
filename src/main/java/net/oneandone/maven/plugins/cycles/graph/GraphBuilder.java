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

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import net.oneandone.maven.plugins.cycles.classes.ClassDependencies;
import net.oneandone.maven.plugins.cycles.classes.ClassDependency;
import net.oneandone.maven.plugins.cycles.classes.PackageDependencies;
import net.oneandone.maven.plugins.cycles.classes.PackageDependency;

import com.google.common.base.Predicate;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * Builds a package dependency graph using the Classycle analyzers.
 * 
 * @author chschmitz
 */
public final class GraphBuilder {
    /**
     * @param nameFilter a name filter on package names
     * @param classDirs class directories or Jar files
     * @return a directed graph of package dependencies
     * @throws IOException if I/O fails
     */
    public static DirectedGraph<String, WeightedEdge> buildPackageGraph(Predicate<String> nameFilter, 
            File classDirs) throws IOException {
        return buildPackageGraph(nameFilter, Integer.MAX_VALUE, classDirs);
    }

    /**
     * @param nameFilter a name filter on package names
     * @param packageDepth depth to which package names are aggregated (1 = "com", 2 = "com.unitedinternet", etc.)
     * @param classDirs a class directory or Jar file
     * @return a directed graph of package dependencies
     * @throws IOException if I/O fails
     */
    public static DirectedGraph<String, WeightedEdge> buildPackageGraph(Predicate<String> nameFilter, int packageDepth, 
            File... classDirs) throws IOException {
        
        ClassDependencies classDependencies = new ClassDependencies(nameFilter, classDirs);
        PackageDependencies packageDependencies = new PackageDependencies(classDependencies, packageDepth);
        return buildGraph(packageDependencies);
    }

    private static DirectedGraph<String, WeightedEdge> buildGraph(PackageDependencies packageDependencies) {
        DirectedGraph<String, WeightedEdge> graph = new DirectedSparseGraph<String, WeightedEdge>();
      
        int id = 0;
        for (Entry<String, Set<PackageDependency>> dependency 
                : packageDependencies.getPackageDependencies().entrySet()) {
            String fromPkg = dependency.getKey();
            for (PackageDependency pkgDep : dependency.getValue()) {
                String toPkg = pkgDep.getTo();
                Set<ClassDependency> classDependencies = pkgDep.getClassDependencies();
                graph.addEdge(new WeightedEdge(id++, classDependencies), fromPkg, toPkg);
            }
        }
        return graph;
    }

    private GraphBuilder() {
        // Don't instantiate
    }
}
