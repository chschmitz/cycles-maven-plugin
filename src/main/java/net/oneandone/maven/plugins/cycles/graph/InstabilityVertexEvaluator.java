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

import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Favours breaking edges from stable to unstable packages, i.e., from those
 * with a high indegree to those with a high outdegree. 
 * 
 * @author chschmitz
 *
 * @param <V> vertex type
 */
public final class InstabilityVertexEvaluator<V> implements 
    VertexEvaluator<V, DirectedGraph<V, WeightedEdge>> {

    @Override
    public double weight(V vertex, DirectedGraph<V, WeightedEdge> graph) {
       return outWeight(vertex, graph) - inWeight(vertex, graph);
    }

    private double inWeight(V vertex, DirectedGraph<V, WeightedEdge> graph) {
        int degree = 0;
        for (WeightedEdge e : graph.getInEdges(vertex)) {
            degree += e.getWeight();
        }
        return (int) degree;
    }

    private double outWeight(V vertex, DirectedGraph<V, WeightedEdge> graph) {
        int degree = 0;
        for (WeightedEdge e : graph.getOutEdges(vertex)) {
            degree += e.getWeight();
        }
        return degree;
    }
}
