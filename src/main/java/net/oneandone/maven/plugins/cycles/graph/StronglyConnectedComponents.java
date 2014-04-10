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
     * @return a collection of components
     */
    public static <V, E> Collection<Set<V>> strongComponentsAsSets(DirectedGraph<V, E> g) {
        AtomicInteger index = new AtomicInteger(0);
        Stack<V> s = new Stack<V>();
        Map<V, Integer> vindex = Maps.newHashMap();
        Map<V, Integer> vlowlink = Maps.newHashMap();
        List<Set<V>> acc = Lists.newArrayList();
        for (V v : g.getVertices()) {
            if (!vindex.containsKey(v)) {
                tarjan(v, g, index, s, vindex, vlowlink, acc);
            }
        }
        return acc;
    }
    
    private static <V, E> void tarjan(V v, DirectedGraph<V, E> g, AtomicInteger index, Stack<V> s,
            Map<V, Integer> vindex, Map<V, Integer> vlowlink, List<Set<V>> acc) {
        vindex.put(v, index.get());
        vlowlink.put(v, index.get());
        index.incrementAndGet();
        s.push(v);
        for (V w : g.getSuccessors(v)) {
            if (!vindex.containsKey(w)) {
                tarjan(w, g, index, s, vindex, vlowlink, acc);
                vlowlink.put(v, Math.min(vlowlink.get(v), vlowlink.get(w)));
            } else if (s.contains(w)) {
                vlowlink.put(v, Math.min(vlowlink.get(v), vindex.get(w)));
            }
        }
        if (vlowlink.get(v).equals(vindex.get(v))) {
            Set<V> component = Sets.newHashSet();
            V w;
            do {
                w = s.pop();
                component.add(w);
            } while (v != w);
            acc.add(component);
        }
    }
    
    private StronglyConnectedComponents() {
        // Don't instantiate
    }
}