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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nullable;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class FeedbackArcSetTest {

    @Test
    public void testFeedbackArcs() {
        DirectedGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();

        graph.addEdge(12, 1, 2);
        graph.addEdge(13, 1, 3);
        graph.addEdge(24, 2, 4);
        graph.addEdge(34, 3, 4);
        graph.addEdge(45, 4, 5);
        graph.addEdge(51, 5, 1);
        graph.addEdge(53, 5, 3);

        assertThat(StronglyConnectedComponents.strongComponentsAsSets(graph).size(), is(1));

        Collection<Integer> feedbackArcs = FeedbackArcSet.feedbackArcs(graph);
        for (Integer edge : feedbackArcs) {
            graph.removeEdge(edge);
        }

        assertThat(StronglyConnectedComponents.strongComponentsAsSets(graph).size(), greaterThan(1));
    }

    @Test
    public void testFeedbackArcsInstabilityLarge() {
        DirectedGraph<Integer, WeightedEdge> graph = new DirectedSparseGraph<Integer, WeightedEdge>();

        for (int i = 0; i < 100; i++) {
            graph.addEdge(weightedEdge(i, i == 47 ? 1 : 2), i, (i + 1) % 100);
        }

        assertThat(StronglyConnectedComponents.strongComponentsAsSets(graph).size(), is(1));

        Collection<WeightedEdge> feedbackArcs = FeedbackArcSet.feedbackArcs(graph,
                new InstabilityVertexEvaluator<Integer>());
        assertThat(feedbackArcs.size(), is(1));
        WeightedEdge feedbackArc = feedbackArcs.iterator().next();
        assertThat(graph.getSource(feedbackArc), is(47));
        assertThat(graph.getDest(feedbackArc), is(48));
    }

    @Test
    public void testFeedbackArcsInstabilitySmall() {
        DirectedGraph<String, WeightedEdge> graph = new DirectedSparseGraph<String, WeightedEdge>();

        graph.addEdge(weightedEdge(1, 1), "stable", "instable");
        graph.addEdge(weightedEdge(2, 10), "instable", "stable");
        graph.addEdge(weightedEdge(3, 1), "instable", "package1");
        graph.addEdge(weightedEdge(4, 1), "instable", "package2");
        graph.addEdge(weightedEdge(5, 1), "package3", "stable");
        graph.addEdge(weightedEdge(6, 1), "package4", "stable");

        Collection<Set<String>> largeComponents = Collections2.filter(
                StronglyConnectedComponents.strongComponentsAsSets(graph), new Predicate<Set<String>>() {
                    @Override
                    public boolean apply(@Nullable Set<String> input) {
                        return input != null && input.size() > 1;
                    }
                });
        assertThat(largeComponents.size(), is(1));
        DirectedGraph<String, WeightedEdge> largeComponent = FilterUtils.createInducedSubgraph(largeComponents
                .iterator().next(), graph);
        assertThat(largeComponent.getVertices().size(), is(2));

        Collection<WeightedEdge> feedbackArcs = FeedbackArcSet.feedbackArcs(largeComponent,
                new InstabilityVertexEvaluator<String>());
        assertThat(feedbackArcs.size(), is(1));
        WeightedEdge feedbackArc = feedbackArcs.iterator().next();
        assertThat(graph.getSource(feedbackArc), is("stable"));
        assertThat(graph.getDest(feedbackArc), is("instable"));
    }

}
