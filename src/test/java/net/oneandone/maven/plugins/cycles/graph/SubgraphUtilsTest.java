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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class SubgraphUtilsTest {

    @Test
    public void testSubgraphs() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();

        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);

        Collection<? extends Collection<Integer>> vertexSets = ImmutableSet.<Collection<Integer>>of(
                ImmutableSet.of(1, 2),
                ImmutableSet.of(3));
        Collection<DirectedGraph<Integer, Integer>> subgraphs = SubgraphUtils.asSubgraphs(vertexSets, graph);
        
        assertThat(subgraphs.size(), is(2));
    }
}
