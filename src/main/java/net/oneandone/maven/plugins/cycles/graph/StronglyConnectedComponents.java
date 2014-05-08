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
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Computes strongly connected components (SCC) using the Tarjan algorithm
 * (cf. http://algowiki.net/wiki/index.php?title=Tarjan%27s_algorithm).
 * 
 * @author chschmitz
 */
public final class StronglyConnectedComponents {
    
    /**
     * Computes strongly connected components.
     * 
     * @param g a graph
     * @param <V> vertex type
     * @param <E> edge type
     * @return a collection of strongly connected components
     */
    public static <V, E> Collection<Set<V>> strongComponentsAsSets(DirectedGraph<V, E> g) {
        AtomicInteger index = new AtomicInteger(0);
        Stack<V> vertexStack = new Stack<V>();
        Map<V, Integer> vIndex = Maps.newHashMap();
        Map<V, Integer> vLowlink = Maps.newHashMap();
        List<Set<V>> componentCollector = Lists.newArrayList();
        for (V vertex : g.getVertices()) {
            if (!vIndex.containsKey(vertex)) {
                tarjan(vertex, g, index, vertexStack, vIndex, vLowlink, componentCollector);
            }
        }
        return componentCollector;
    }
    
    private static <V, E> void tarjan(V currentVertex, DirectedGraph<V, E> g, AtomicInteger index, Stack<V> vertexStack,
            Map<V, Integer> vIndex, Map<V, Integer> vLowlink, List<Set<V>> componentCollector) {
        vIndex.put(currentVertex, index.get());
        vLowlink.put(currentVertex, index.get());
        index.incrementAndGet();
        vertexStack.push(currentVertex);
        for (V successor : g.getSuccessors(currentVertex)) {
            processSuccessor(currentVertex, g, index, vertexStack, vIndex, vLowlink, componentCollector, successor);
        }
        if (vLowlink.get(currentVertex).equals(vIndex.get(currentVertex))) {
            componentCollector.add(extractNewComponent(currentVertex, vertexStack));
        }
    }

    private static <V, E> void processSuccessor(V currentVertex, DirectedGraph<V, E> g, AtomicInteger index,
            Stack<V> vertexStack, Map<V, Integer> vIndex, Map<V, Integer> vLowlink, List<Set<V>> componentCollector,
            V successor) {
        if (!vIndex.containsKey(successor)) {
            tarjan(successor, g, index, vertexStack, vIndex, vLowlink, componentCollector);
            vLowlink.put(currentVertex, Math.min(vLowlink.get(currentVertex), vLowlink.get(successor)));
        } else if (vertexStack.contains(successor)) {
            vLowlink.put(currentVertex, Math.min(vLowlink.get(currentVertex), vIndex.get(successor)));
        }
    }

    private static <V> Set<V> extractNewComponent(V currentVertex, Stack<V> vertexStack) {
        Set<V> component = Sets.newHashSet();
        V vertex;
        do {
            vertex = vertexStack.pop();
            component.add(vertex);
        } while (currentVertex != vertex);
        return component;
    }
    
    private StronglyConnectedComponents() {
        // Don't instantiate
    }
}
