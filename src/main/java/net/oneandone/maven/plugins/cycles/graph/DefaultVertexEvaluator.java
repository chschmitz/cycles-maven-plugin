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
 * {@link VertexEvaluator} that uses out degrees; the weight of a vertex is how many more
 * outbound edges there are than inbound edges.
 * 
 * @author chschmitz
 *
 * @param <V> vertex type
 * @param <G> graph type
 */
public final class DefaultVertexEvaluator<V, G extends DirectedGraph<V, ?>> 
    implements VertexEvaluator<V, G> {
    
    @Override
    public double weight(V vertex, G graph) {
        return (double) graph.outDegree(vertex) - (double) graph.inDegree(vertex);
    }
}
