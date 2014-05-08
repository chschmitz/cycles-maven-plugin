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
import java.util.TreeSet;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Bits and pieces for Graphviz output.
 *
 * @author jgrote
 */
public final class GraphDotUtils {
    private static final double FONT_SIZE = 20.0;
    private static final double STANDARD_FONTSIZE = 14.0;

    /**
     * @param component a graph
     * @param shorten whether to shorten the package names
     * @return a dot string
     */
    public static String toDot(DirectedGraph<String, WeightedEdge> component, boolean shorten) {
        StringBuilder builder = new StringBuilder();
        Collection<WeightedEdge> feedbackArcs = FeedbackArcSet.feedbackArcs(component,
                new InstabilityVertexEvaluator<String>());

        TreeSet<WeightedEdge> sortedEdges = Sets.newTreeSet(new WeightedEdgeComparator(component));
        sortedEdges.addAll(component.getEdges());

        builder.append("digraph mygraph {\n");
        double maxEdgeWeight = getMaxEdgeWeight(component);
        for (WeightedEdge edge : sortedEdges) {
            builder.append(formatEdge(component, shorten, feedbackArcs, edge, maxEdgeWeight));
        }
        builder.append("}\n");
        return builder.toString();
    }

    private static String formatEdge(DirectedGraph<String, WeightedEdge> component, boolean shorten,
            Collection<WeightedEdge> feedbackArcs, WeightedEdge edge, double maxEdgeWeight) {
        double relativeImportance = edge.getWeight() / maxEdgeWeight;
        return String.format("    %s [label=\"%d\", fontsize=%f%s];\n",
            GraphDotUtils.edgeToDot(edge, component, shorten),
            (int) edge.getWeight(),
            (STANDARD_FONTSIZE + (FONT_SIZE * relativeImportance)),
            feedbackArcs.contains(edge) ? ",color=red,fontcolor=red,penwidth=3" : ""
        );
    }

    private static double getMaxEdgeWeight(DirectedGraph<String, WeightedEdge> component) {
        return Ordering.from(new WeightedEdgeComparator(component)).min(component.getEdges()).getWeight();
    }

    /**
     * @param edge an edge
     * @param g a graph
     * @param shorten whether to shorten the class names
     * @return the dot representation (shortened)
     */
    public static String edgeToDot(WeightedEdge edge, DirectedGraph<String, WeightedEdge> g, boolean shorten) {
        return "\"" + GraphStringUtils.shorten(g.getSource(edge), shorten) + "\" -> \"" 
                + GraphStringUtils.shorten(g.getDest(edge), shorten) + "\"";
    }
    
    private GraphDotUtils() {
        // Don't instantiate
    }
}
