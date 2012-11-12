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
package net.oneandone.maven.plugins.cycles.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import net.oneandone.maven.plugins.cycles.classes.ClassDependency;
import net.oneandone.maven.plugins.cycles.graph.FeedbackArcSet;
import net.oneandone.maven.plugins.cycles.graph.GraphDotUtils;
import net.oneandone.maven.plugins.cycles.graph.GraphStringUtils;
import net.oneandone.maven.plugins.cycles.graph.InstabilityVertexEvaluator;
import net.oneandone.maven.plugins.cycles.graph.WeightedEdge;
import net.oneandone.maven.plugins.cycles.graph.WeightedEdgeComparator;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Outputs the results of a {@link ComponentAnalysis} to a string.
 * 
 * @author chschmitz
 */
public final class ComponentAnalyzer {
    private File classDir;
    private String filterPrefix;
    private boolean shorten;
    private boolean writeDotFiles;
    private int cycleCount;
    private int packageDepth;
    private boolean showClassDeps;

    /**
     * @param classDir source of our class files
     * @param filterPrefix class name filter prefix
     * @param shorten whether to shorten the class names
     * @param writeDotFiles iff true, .dot files will be written
     * @param packageDepth prefix depth to which packages are aggregated 
     * @param showClassDeps iff true, class-level dependencies are shown
     */
    public ComponentAnalyzer(File classDir, String filterPrefix, boolean shorten, boolean writeDotFiles, 
            int packageDepth, boolean showClassDeps) {
        this.classDir = classDir;
        this.filterPrefix = filterPrefix;
        this.shorten = shorten;
        this.writeDotFiles = writeDotFiles;
        this.packageDepth = packageDepth;
        this.showClassDeps = showClassDeps;
    }

    /**
     * Perform the actual analysis.
     * 
     * @return human-readable output of the analysis results
     * @throws IOException if parsing the classes fails
     */
    public String analyze() throws IOException {
        ComponentAnalysis analysis = new ComponentAnalysis(classDir, filterPrefix, packageDepth);

        if (!analysis.hasNonTrivialComponents()) {
            return ("No package cycles.");
        }

        StringBuilder builder = new StringBuilder();
        printCycles(analysis.getStrongComponents(), builder);
        return builder.toString();
    }

    private  void printCycles(Collection<DirectedGraph<String, WeightedEdge>> components, StringBuilder builder)
            throws IOException {
        cycleCount = 0;
        for (DirectedGraph<String, WeightedEdge> component : components) {
            if (component.getVertices().size() > 1) {
                printCycle(component, builder);
                writeCycleGraph(component);
                cycleCount++;
            }
        }
    }

    private  void printCycle(DirectedGraph<String, WeightedEdge> component, StringBuilder builder) {
        builder.append(String.format("=== Cycle / strongly connected component (%d packages, %d dependencies)",
                component.getVertexCount(), component.getEdgeCount()));
        builder.append("\n");
        printPackages(component, builder);
        printDependencies(component, builder);
        printFeedbackArcs(component, builder);
    }

    private void writeCycleGraph(DirectedGraph<String, WeightedEdge> component) throws IOException {
        if (!writeDotFiles) {
            return;
        }
        File dotFile = new File(classDir.getParent(), "graph-" + cycleCount + ".dot");
        String dotString = GraphDotUtils.toDot(component, shorten);
        Files.write(dotString, dotFile, Charsets.UTF_8);
    }

    private  void printFeedbackArcs(DirectedGraph<String, WeightedEdge> component, StringBuilder builder) {
        builder.append("\n= Cycle-breaking dependencies\n");
        for (WeightedEdge dependency : FeedbackArcSet.feedbackArcs(component, 
                new InstabilityVertexEvaluator<String>())) {
            builder.append(GraphStringUtils.edgeToString(dependency, component, shorten));
            builder.append("\n");
        }
    }

    private  void printDependencies(DirectedGraph<String, WeightedEdge> component, StringBuilder builder) {
        builder.append("\n= Dependencies\n");
        TreeSet<WeightedEdge> sortedEdges = Sets.newTreeSet(new WeightedEdgeComparator(component));
        sortedEdges.addAll(component.getEdges());
        for (WeightedEdge dependency : sortedEdges) {
            builder.append(GraphStringUtils.edgeToString(dependency, component, shorten));
            builder.append("\n");
            if (showClassDeps) {
                printClassDependencies(builder, dependency);
            }
        }
    }

    private void printClassDependencies(StringBuilder builder, WeightedEdge dependency) {
        SortedSet<String> depsStrs = Sets.newTreeSet();
        for (ClassDependency classDependency : dependency.getDependencies()) {
            depsStrs.add(GraphStringUtils.dependencyToString(classDependency, shorten));
        }
        for (String depsStr : depsStrs) {
            builder.append("\t[");
            builder.append(depsStr);
            builder.append("]\n");
        }
    }

    private  void printPackages(DirectedGraph<String, WeightedEdge> component, StringBuilder builder) {
        builder.append("\n= Packages\n");
        for (String pkg : Sets.newTreeSet(component.getVertices())) {
            builder.append(GraphStringUtils.vertexToString(pkg, shorten));
            builder.append("\n");
        }
    }
}
