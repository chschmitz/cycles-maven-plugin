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
import java.util.Collection;

import net.oneandone.maven.plugins.cycles.graph.GraphBuilder;
import net.oneandone.maven.plugins.cycles.graph.StronglyConnectedComponents;
import net.oneandone.maven.plugins.cycles.graph.SubgraphUtils;
import net.oneandone.maven.plugins.cycles.graph.WeightedEdge;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Analyses a class directory or jar file and presents the results as a collection
 * of strongly connected components (vulgo: dependency cycles).  
 * 
 * @author chschmitz
 */
public final class ComponentAnalysis {
    private File classDir;
    private String filterPrefix;
    private Collection<DirectedGraph<String, WeightedEdge>> strongComponents;
    private int packageDepth;
    
    /**
     * @param classDir a class directory or jar file
     * @param filterPrefix a filter prefix on full class names
     * @param packageDepth depth to which package prefixes are aggregated
     * @throws IOException if parsing the class files fails
     */
    public ComponentAnalysis(File classDir, String filterPrefix, int packageDepth) throws IOException {
        super();
        this.classDir = classDir;
        this.filterPrefix = filterPrefix;
        this.packageDepth = packageDepth;
        
        analyze();
    }

    private void analyze() throws IOException {
         DirectedGraph<String, WeightedEdge> packageGraph = 
                 GraphBuilder.buildPackageGraph(classDir, NameFilter.nameFilter(filterPrefix), packageDepth);
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
