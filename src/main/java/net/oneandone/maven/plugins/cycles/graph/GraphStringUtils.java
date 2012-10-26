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

import net.oneandone.maven.plugins.cycles.classes.ClassDependency;
import edu.uci.ics.jung.graph.DirectedGraph;


/**
 * Helper methods to convert vertices and edges to printable form.
 * 
 * @author chschmitz
 */
public final class GraphStringUtils {
    /**
     * @param pkg a package name
     * @param shorten whether to shorten it or not
     * @return the shortened name, in the form a.b.c.d.package
     */
    public static String shorten(String pkg, boolean shorten) {
        if (!shorten) {
            return pkg;
        }
        
        String[] parts = pkg.split("\\.");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            result.append(parts[i].charAt(0));
            result.append(".");
        }
        result.append(parts[parts.length - 1]);
        return result.toString();
    }

    /**
     * @param vertex a vertex
     * @param shorten 
     * @return the string representation (shortened)
     */
    public static String vertexToString(String vertex, boolean shorten) {
        return shorten(vertex, shorten);
    }

    /**
     * @param edge an edge
     * @param g a graph
     * @param shorten whether to shorten the class names
     * @return the string representation (shortened)
     */
    public static String edgeToString(WeightedEdge edge, DirectedGraph<String, WeightedEdge> g, boolean shorten) {
        return shorten(g.getSource(edge), shorten) + " -> " + shorten(g.getDest(edge), shorten) 
                + " [" + (int) edge.getWeight() + "]";
    }

    private GraphStringUtils() {
    }

    public static String dependencyToString(ClassDependency classDependency, boolean shorten) {
        return shorten(classDependency.getFrom(), shorten) + " -> " + shorten(classDependency.getTo(), shorten);
    }
}
