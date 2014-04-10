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

import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Represents package-level dependencies.
 * 
 * @author chschmitz
 */
public final class PackageDependencies {
    private Map<String, Set<PackageDependency>> packageDependencies;

    /**
     * @param classDependencies the class-level dependencies
     * @param depth the aggregation depth (i.e., the length of the FQCN prefixes to which the package
     * dependencies are aggregated; i.e. "2" means <code>net.oneandone</code>, "3" means 
     * <code>net.oneandone.maven</code> etc.)
     */
    public PackageDependencies(ClassDependencies classDependencies, int depth) {
        Map<String, Map<String, Set<ClassDependency>>> packageDependenciesRaw = Maps.newHashMap();
        
        for (String source : classDependencies.getClassDependencies().keySet()) {
            String sourcePkg = packagePrefix(getPackageNameOfClass(source), depth);
            for (ClassDependency classDep : classDependencies.getClassDependencies().get(source)) {
                String destPkg = packagePrefix(getPackageNameOfClass(classDep.getTo()), depth);
                if (!sourcePkg.equals(destPkg)) {
                    addDependency(packageDependenciesRaw, sourcePkg, destPkg, classDep);
                }
            }
        }
        
        packageDependencies = convertToPackageDependencies(packageDependenciesRaw);
    }
    
    public Map<String, Set<PackageDependency>> getPackageDependencies() {
        return packageDependencies;
    }

    /**
     * @param className a class name
     * @return the package name of that class
     */
    public static String getPackageNameOfClass(String className) {
        int index = className.lastIndexOf('.');
        return index < 0 ? "(default package)" : className.substring(0, index);
    }

    private Map<String, Set<PackageDependency>> convertToPackageDependencies(
            Map<String, Map<String, Set<ClassDependency>>> packageDependenciesRaw) {
        Map<String, Set<PackageDependency>> packageDependencies = Maps.newHashMap();
        for (String srcPkg : packageDependenciesRaw.keySet()) {
            Set<PackageDependency> pkgDeps = Sets.newHashSet();
            for (String destPkg : packageDependenciesRaw.get(srcPkg).keySet()) {
                pkgDeps.add(new PackageDependency(srcPkg, destPkg, 
                        packageDependenciesRaw.get(srcPkg).get(destPkg)));
            }
            packageDependencies.put(srcPkg, pkgDeps);
        }
        return packageDependencies;
    }

    private void addDependency(Map<String, Map<String, Set<ClassDependency>>> packageDependenciesRaw, String sourcePkg,
            String destPkg, ClassDependency classDep) {
        if (!packageDependenciesRaw.containsKey(sourcePkg)) {
            packageDependenciesRaw.put(sourcePkg, Maps.<String, Set<ClassDependency>>newHashMap());
        }
        if (!packageDependenciesRaw.get(sourcePkg).containsKey(destPkg)) {
            packageDependenciesRaw.get(sourcePkg).put(destPkg, Sets.<ClassDependency>newHashSet());
        }
        packageDependenciesRaw.get(sourcePkg).get(destPkg).add(classDep);
    }

    private static String packagePrefix(String pkg, int depth) {
        return Joiner.on('.').join(Lists.partition(ImmutableList.copyOf(Splitter.on('.').split(pkg)), depth).get(0));
    }
}
