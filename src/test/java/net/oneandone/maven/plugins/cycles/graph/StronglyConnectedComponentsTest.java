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

import static net.oneandone.maven.plugins.cycles.graph.TestUtil.weightedEdge;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class StronglyConnectedComponentsTest {

    @Test
    public void testEmpty() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();

        Collection<Set<Integer>> components = StronglyConnectedComponents.strongComponentsAsSets(graph);
        assertThat(components.size(), is(0));
    }

    @Test
    public void testOneVertex() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
        graph.addVertex(0);

        Collection<Set<Integer>> components = StronglyConnectedComponents.strongComponentsAsSets(graph);
        assertThat(components.size(), is(1));
    }

    @Test
    public void testTwoVerticesTrivialComponents() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
        graph.addEdge(0, 0, 1);

        Collection<Set<Integer>> components = StronglyConnectedComponents.strongComponentsAsSets(graph);
        assertThat(components.size(), is(2));

    }

    @Test
    public void testTwoVerticesOneComponent() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
        graph.addEdge(0, 0, 1);
        graph.addEdge(1, 1, 0);

        Collection<Set<Integer>> components = StronglyConnectedComponents.strongComponentsAsSets(graph);
        assertThat(components.size(), is(1));
        assertThat(components.iterator().next().size(), is(2));
    }

    @Test
    public void testTwoComponents() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
        graph.addEdge(0, 0, 1);
        graph.addEdge(1, 1, 0);
        graph.addEdge(2, 1, 2);
        graph.addEdge(3, 2, 3);
        graph.addEdge(4, 3, 4);
        graph.addEdge(5, 4, 2);

        Collection<Set<Integer>> components = StronglyConnectedComponents.strongComponentsAsSets(graph);
        assertThat(components.size(), is(2));
        for (Set<Integer> comp : components) {
            switch (comp.size()) {
            case 2:
                assertThat(comp, hasItems(0, 1));
                break;
            case 3:
                assertThat(comp, hasItems(2, 3, 4));
                break;
            default:
                fail("Unexpected component size.");
            }
        }
    }

    @Test
    public void testTwoComponentsSubgraphs() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
        graph.addEdge(0, 0, 1);
        graph.addEdge(1, 1, 0);
        graph.addEdge(2, 1, 2);
        graph.addEdge(3, 2, 3);
        graph.addEdge(4, 3, 4);
        graph.addEdge(5, 4, 2);

        Collection<DirectedGraph<Integer, Integer>> components = SubgraphUtils.asSubgraphs(
                StronglyConnectedComponents.strongComponentsAsSets(graph), graph);
        assertThat(components.size(), is(2));
        for (DirectedGraph<Integer, Integer> comp : components) {
            switch (comp.getVertexCount()) {
            case 2:
                assertThat(comp.getVertices(), hasItems(0, 1));
                break;
            case 3:
                assertThat(comp.getVertices(), hasItems(2, 3, 4));
                break;
            default:
                fail("Unexpected component size.");
            }
        }
    }

    @Test
    public void testLargeLoop() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
        for (int i = 0; i < 100; i++) {
            graph.addEdge(i, i, (i + 1) % 100);
        }
        Collection<Set<Integer>> components = StronglyConnectedComponents.strongComponentsAsSets(graph);
        assertThat(components.size(), is(1));
        Set<Integer> firstComponent = components.iterator().next();
        assertThat(firstComponent.size(), is(100));
        for (int i = 0; i < 100; i++) {
            assertThat(firstComponent, hasItem(i));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTwoLargeLoops() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
        for (int i = 0; i < 100; i++) {
            graph.addEdge(i, i, (i + 1) % 100);
        }
        for (int i = 0; i < 100; i++) {
            graph.addEdge(100 + i, 100 + i, 100 + (i + 1) % 100);
        }
        graph.addEdge(200, 0, 200);
        graph.addEdge(201, 200, 100);

        Collection<Set<Integer>> components = StronglyConnectedComponents.strongComponentsAsSets(graph);
        assertThat(components.size(), is(3));
        for (Set<Integer> component : components) {
            assertThat(component.size(), anyOf(is(100), is(1)));
        }
    }

    @Test
    public void testManySmallLoops() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
        for (int i = 0; i < 10000; i++) {
            graph.addEdge(2 * i, i, i + 10000);
            graph.addEdge(2 * i + 1, i + 10000, i);
        }
        Collection<Set<Integer>> components = StronglyConnectedComponents.strongComponentsAsSets(graph);
        assertThat(components.size(), is(10000));
        for (Set<Integer> component : components) {
            assertThat(component.size(), is(2));
        }
    }

    @Test
    public void testManySmallLoopsWeightedEdge() {
        DirectedGraph<Integer, WeightedEdge> graph = new DirectedSparseGraph<Integer, WeightedEdge>();
        for (int i = 0; i < 10000; i++) {
            graph.addEdge(weightedEdge(2 * i, 100), i, i + 10000);
            graph.addEdge(weightedEdge(2 * i + 1, 100), i + 10000, i);
        }
        Collection<Set<Integer>> components = StronglyConnectedComponents.strongComponentsAsSets(graph);
        assertThat(components.size(), is(10000));
        for (Set<Integer> component : components) {
            assertThat(component.size(), is(2));
        }
    }

    @Test
    public void testLargeTree() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
        for (int i = 0; i < 1000; i++) {
            graph.addEdge(2 * i, i, 2 * i + 1);
            graph.addEdge(2 * i + 1, i, 2 * i + 2);
        }
        Collection<Set<Integer>> components = StronglyConnectedComponents.strongComponentsAsSets(graph);
        assertThat(components.size(), is(2001));
        for (Set<Integer> component : components) {
            assertThat(component.size(), is(1));
        }
    }

    @Test
    public void testTwoEdgesThreeComponents() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();
        graph.addEdge(0, 0, 1);
        graph.addEdge(1, 2, 1);

        Collection<Set<Integer>> components = StronglyConnectedComponents.strongComponentsAsSets(graph);
        assertThat(components.size(), is(3));
    }
}
