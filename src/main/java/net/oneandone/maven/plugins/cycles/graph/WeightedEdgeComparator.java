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

import java.io.Serializable;
import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * CAUTION: This is only for display purposes. It is not compatible with 
 * {@link WeightedEdge#hashCode()} and{@link WeightedEdge#equals()}.
 *  
 * @author chschmitz
 */
public final class WeightedEdgeComparator implements Comparator<WeightedEdge>, Serializable {
    private static final long serialVersionUID = 1L;
    private DirectedGraph<String, WeightedEdge> graph;

    /**
     * @param graph the graph from which the edges are taken; needed to obtain the vertices
     */
    public WeightedEdgeComparator(DirectedGraph<String, WeightedEdge> graph) {
        this.graph = graph;
    }

    @Override
    public int compare(WeightedEdge o1, WeightedEdge o2) {
        return ComparisonChain
                .start()
                .compare(o2.getWeight(), o1.getWeight())
                .compare(graph.getSource(o1), graph.getSource(o2))
                .compare(graph.getDest(o1), graph.getDest(o2))
                .compare(o1.hashCode(), o2.hashCode())
                .result();
    }
}
