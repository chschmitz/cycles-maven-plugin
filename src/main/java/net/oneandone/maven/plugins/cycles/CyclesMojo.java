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
package net.oneandone.maven.plugins.cycles;

import java.io.File;
import java.io.IOException;

import net.oneandone.maven.plugins.cycles.analyzer.ComponentAnalyzer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;


/**
 * Talk about dependency cycles.
 * 
 * @author chschmitz
 * 
 * @goal cycles
 * @requiresProject true
 */
public class CyclesMojo extends AbstractMojo {
    /**
     * @parameter expression="${project.build.outputDirectory}" 
     */
    private File classDir;

    /**
     * @parameter expression="${nameprefix}"
     */
    private String namePrefix;

    /**
     * @parameter expression="${shorten}" default-value="true"
     */
    private boolean shorten;
    
    /**
     * @parameter expression="${classDeps}" default-value="false"
     */
    private boolean showClassDeps;

    /**
     * @parameter expression="${writeDotFiles}" default-value="true"
     */
    private boolean writeDotFiles;

    /**
     * Default is Integer.MAX_VALUE (== infinity for practical purposes).
     * 
     * @parameter expression="${packageDepth}" default-value="2147483647"
     */
    private int packageDepth;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        ComponentAnalyzer componentAnalyzer = new ComponentAnalyzer(classDir, 
                Strings.nullToEmpty(namePrefix), shorten, writeDotFiles, packageDepth, showClassDeps);

        try {
            getLog().info(componentAnalyzer.analyze());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    @VisibleForTesting
    void setClassDir(File classDir) {
        this.classDir = classDir;
    }

    @VisibleForTesting
    void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @VisibleForTesting
    void setShorten(boolean shorten) {
        this.shorten = shorten;
    }

    @VisibleForTesting
    void setWriteDotFiles(boolean writeDotFiles) {
        this.writeDotFiles = writeDotFiles;
    }

    @VisibleForTesting
    void setPackageDepth(int packageDepth) {
        this.packageDepth = packageDepth;
    }
}
