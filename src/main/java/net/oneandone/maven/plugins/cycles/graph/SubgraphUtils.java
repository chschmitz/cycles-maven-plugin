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

import java.util.Collection;

import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * Different ways of dealing with subgraphs.
 * 
 * @author chschmitz
 */
public final class SubgraphUtils {
    /**
     * Present the vertex sets as proper subgraphs.
     *  
     * @param vertexSets sets of vertices
     * @param graph a graph
     * @param <V> vertex type
     * @param <E> edge type
     * @param <G> graph type
     * @return a collection of subgraphs
     */
    public static <V, E, G extends Hypergraph<V, E>> Collection<G> asSubgraphs(
            Collection<? extends Collection<V>> vertexSets, G graph) {
        return FilterUtils.createAllInducedSubgraphs(vertexSets, graph);
    }

    private SubgraphUtils() {
    }
}
