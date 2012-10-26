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
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Greedily computes an approximation of a "feedback arc set" (FAS), i.e., a
 * minimal set of edges that breaks a strongly connected component if removed.
 * 
 * Algorithm taken from: Peter Eades, Xuemin Lin, and W. F. Smyth.
 * "A Fast and Effective Heuristic for the Feedback Arc Set Problem.". Inf.
 * Process. Lett., Vol. 47, Nr. 6 (1993), p. 319-323.
 * 
 * @author chschmitz
 */
public final class FeedbackArcSet {

    /**
     * @param graph a directed graph
     * @param <V> vertex type
     * @param <E> edge type
     * @return approximation of a feedback arc set, i.e., a set of edges that, if removed,
     * breaks a strong component 
     */
    public static <V, E> Collection<E> feedbackArcs(DirectedGraph<V, E> graph) {
        return feedbackArcs(graph, new DefaultVertexEvaluator<V, DirectedGraph<V, E>>());
    }
    
    /**
     * @param graph a directed graph
     * @param evaluator a vertex evaluator
     * @param <V> vertex type
     * @param <E> edge type
     * @return approximation of a feedback arc set, i.e., a set of edges that, if removed,
     * breaks a strong component; the algorithm will try to pick minimum weight edges 
     */
    public static <V, E> Collection<E> feedbackArcs(DirectedGraph<V, E> graph, 
            VertexEvaluator<V, DirectedGraph<V, E>> evaluator) {
        return ImmutableList.copyOf(Collections2.filter(graph.getEdges(), 
                isBackwardEdge(vertexOrdering(graph, evaluator), graph)));
    }

    private static <V, E> Predicate<E> isBackwardEdge(final Ordering<V> ordering, 
            final DirectedGraph<V, E> graph) {
        return new Predicate<E>() {
            public boolean apply(E edge) {
                V from = graph.getSource(edge);
                V to = graph.getDest(edge);
                return ordering.compare(from, to) > 0;
            }
        };
    }

    private static <V, E> Ordering<V> vertexOrdering(DirectedGraph<V, E> graph,
            VertexEvaluator<V, DirectedGraph<V, E>> evaluator) {
        List<V> front = Lists.newLinkedList();
        List<V> back = Lists.newLinkedList();

        // FIXME: there must be an easier way to clone a graph
        DirectedGraph<V, E> g = FilterUtils.createInducedSubgraph(graph.getVertices(), graph);

        while (!g.getVertices().isEmpty()) {
            collectSinks(g, back);
            collectSources(g, front);
            collectMaxDelta(g, front, evaluator);
        }

        return Ordering.explicit(Lists.newArrayList(Iterables.concat(front, back)));
    }

    private static <V, E> void collectMaxDelta(DirectedGraph<V, E> g, List<V> front,
            VertexEvaluator<V, DirectedGraph<V, E>> evaluator) {
        V maxVertex = findMaxWeightVertex(g, evaluator);
        if (maxVertex != null) {
            front.add(maxVertex);
            g.removeVertex(maxVertex);
        }
    }

    private static <V, E> V findMaxWeightVertex(DirectedGraph<V, E> g, 
            VertexEvaluator<V, DirectedGraph<V, E>> evaluator) {
        V maxWeightVertex = null;
        double maxWeight = 0;
        for (V v : g.getVertices()) {
            double degreeDelta = evaluator.weight(v, g);
            if (maxWeightVertex == null || degreeDelta > maxWeight) {
                maxWeightVertex = v;
                maxWeight = degreeDelta;
            }
        }
        return maxWeightVertex;
    }

    private static <V, E> void collectSources(DirectedGraph<V, E> g, List<V> front) {
        V source;
        while ((source = findSource(g)) != null) {
            g.removeVertex(source);
            front.add(source);
        }
    }

    private static <V, E> void collectSinks(DirectedGraph<V, E> g, List<V> back) {
        V sink;
        while ((sink = findSink(g)) != null) {
            g.removeVertex(sink);
            back.add(0, sink);
        }
    }

    private static <V, E> V findSink(DirectedGraph<V, E> g) {
        for (V v : g.getVertices()) {
            if (g.outDegree(v) == 0) {
                return v;
            }
        }
        return null;
    }

    private static <V, E> V findSource(DirectedGraph<V, E> g) {
        for (V v : g.getVertices()) {
            if (g.inDegree(v) == 0) {
                return v;
            }
        }
        return null;
    }

    private FeedbackArcSet() {
    }
}
