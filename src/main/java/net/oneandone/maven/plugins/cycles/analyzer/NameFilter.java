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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;

/**
 * A predicate that filters on package name prefixes.
 * 
 * @author chschmitz
 */
@VisibleForTesting
public final class NameFilter implements Predicate<String> {
    private final String filterPrefix;
    
    /**
     * @param filterPrefix the filter prefix
     * @return a predicate that returns true iff a string starts with that prefix
     */
    public static NameFilter nameFilter(String filterPrefix) {
        return new NameFilter(filterPrefix);
    }

    private NameFilter(String filterPrefix) {
        this.filterPrefix = filterPrefix;
    }

    @Override
    public boolean apply(String name) {
        return name.startsWith(filterPrefix);
    }
}
