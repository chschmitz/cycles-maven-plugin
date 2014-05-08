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
package net.oneandone.maven.plugins.cycles.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import net.oneandone.maven.plugins.cycles.graph.GraphBuilder;
import net.oneandone.maven.plugins.cycles.graph.NameFilter;
import net.oneandone.maven.plugins.cycles.graph.StronglyConnectedComponents;
import net.oneandone.maven.plugins.cycles.graph.SubgraphUtils;
import net.oneandone.maven.plugins.cycles.graph.WeightedEdge;

import com.google.common.base.Predicate;

import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Analyses a class directory or jar file and presents the results as a collection
 * of strongly connected components (vulgo: dependency cycles).  
 * 
 * @author chschmitz
 */
public final class ComponentAnalysis {
    private Predicate<String> nameFilter;
    private Collection<DirectedGraph<String, WeightedEdge>> strongComponents;
    private int packageDepth;
    private File[] classDirs;
    
    /**
     * @param filterPrefix a filter prefix on full class names
     * @param packageDepth depth to which package prefixes are aggregated
     * @param classDirs class directories or jar files
     * @throws IOException if parsing the class files fails
     */
    public ComponentAnalysis(String filterPrefix, int packageDepth, File... classDirs) throws IOException {
        this(NameFilter.nameFilter(filterPrefix), packageDepth, classDirs);
    }
    
    /**
     * @param nameFilter a filter on class names
     * @param packageDepth depth to which package prefixes are aggregated
     * @param classDirs class directories or jar files
     * @throws IOException if parsing the class files fails
     */
    public ComponentAnalysis(Predicate<String> nameFilter, int packageDepth, File... classDirs) throws IOException {
        this.nameFilter = nameFilter;
        this.classDirs = Arrays.copyOf(classDirs, classDirs.length);
        this.packageDepth = packageDepth;
        
        analyze();
    }

    private void analyze() throws IOException {
         DirectedGraph<String, WeightedEdge> packageGraph = 
                 GraphBuilder.buildPackageGraph(nameFilter, packageDepth, classDirs);
         strongComponents = SubgraphUtils.asSubgraphs(
                 StronglyConnectedComponents.strongComponentsAsSets(packageGraph), packageGraph);
    }

    public Collection<DirectedGraph<String, WeightedEdge>> getStrongComponents() {
        return strongComponents;
    }

    /**
     * @return true iff there is a strong component larger than 1.
     */
    public boolean hasNonTrivialComponents() {
        for (DirectedGraph<String, WeightedEdge> compo : strongComponents) {
            if (compo.getVertices().size() > 1) {
                return true;
            }
        }
        return false;
    }
}
