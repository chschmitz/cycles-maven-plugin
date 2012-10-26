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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import net.oneandone.maven.plugins.cycles.analyzer.NameFilter;

import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedGraph;

public class GraphBuilderTest {
    private File baseDir;

    @Before
    public void setUp() {
        baseDir = new File(GraphBuilderTest.class.getResource(".").getFile());
    }
    
    @Test
    public void testBuildGraph() throws IOException {
        DirectedGraph<String, WeightedEdge> graph = GraphBuilder.buildPackageGraph(baseDir, 
                NameFilter.nameFilter("net.oneandone.maven.plugins.cycles.graph"));
        assertThat(graph.getVertexCount(), is(3));
        assertThat(graph.getEdgeCount(), is(3));
    }

    @Test
    public void testBuildGraphCollapse() throws IOException {
        DirectedGraph<String, WeightedEdge> graph = GraphBuilder.buildPackageGraph(baseDir, 
                NameFilter.nameFilter("net.oneandone.maven.plugins.cycles.graph"), 7);
        assertThat(graph.getVertexCount(), is(2));
        assertThat(graph.getEdgeCount(), is(2));
        assertThat(graph.getEdges().iterator().next().getWeight(), is(2d));
    }
}
