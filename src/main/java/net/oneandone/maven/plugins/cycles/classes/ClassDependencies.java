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
package net.oneandone.maven.plugins.cycles.classes;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import classycle.Analyser;
import classycle.ClassAttributes;
import classycle.graph.AtomicVertex;
import classycle.graph.Vertex;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Represents dependencies on a per-class level.
 * 
 * @author chschmitz
 */
public final class ClassDependencies {
    private Map<String, Collection<ClassDependency>> classDependencies;
    
    /**
     * @param nameFilter the name filter (on FQCNs)
     * @param classDirsOrJars the class directories or jar files
     * @throws IOException iff parsing the class files fails
     */
    public ClassDependencies(Predicate<String> nameFilter, File... classDirsOrJars) throws IOException {
        AtomicVertex[] classGraph = getClassGraph(classDirsOrJars);
        classDependencies = Maps.newHashMap();
        for (AtomicVertex clazz : classGraph) {
            if (nameFilter.apply(getClassName(clazz))) {
                classDependencies.put(getClassName(clazz), Sets.<ClassDependency>newHashSet());
                collectDependencies(clazz, nameFilter);
            }
        }
    }

    private void collectDependencies(AtomicVertex clazz, Predicate<String> nameFilter) {
        String srcName = getClassName(clazz);
        for (int i = 0; i < clazz.getNumberOfOutgoingArcs(); i++) {
            Vertex target = clazz.getHeadVertex(i);
            String destName = getClassName(target);
            if (nameFilter.apply(destName) && !srcName.equals(destName)) {
                    classDependencies.get(srcName).add(new ClassDependency(srcName, destName));
            }
        }
    }

    public Map<String, Collection<ClassDependency>> getClassDependencies() {
        return classDependencies;
    }
    
    private static AtomicVertex[] getClassGraph(File... classDirs) throws IOException {
        String[] classDirNames = new String[classDirs.length];
        int i = 0;
        for (File classDir : classDirs) {
            Preconditions.checkArgument(classDir.exists(),
                    "Class directory %s does not exist, please run 'mvn compile'.",
                    classDir.getAbsolutePath());
            classDirNames[i++] = classDir.getAbsolutePath();
        }
        Analyser analyser = new Analyser(classDirNames);
        analyser.createClassGraph();
        return analyser.getClassGraph();
    }

    private static String getClassName(Vertex classVertex) {
        ClassAttributes classAttributes = (ClassAttributes) classVertex.getAttributes();
        return (classAttributes).getName();
    }
}
