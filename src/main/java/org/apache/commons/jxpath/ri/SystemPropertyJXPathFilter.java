/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.ri;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A filter to be used by JXPath, to evaluate the xpath string values to impose any restrictions.
 * This class implements specific filter interfaces, and implements methods in those.
 * For instance, it JXPathClassFilter interface, which is used to check if any restricted java classes are passed via xpath
 * JXPath uses this filter instance when an extension function instance is created.
 */
public class SystemPropertyJXPathFilter implements JXPathFilter {
    private final Set<String> allowedClasses;

    public SystemPropertyJXPathFilter() {
        this.allowedClasses = new HashSet<>();
        final String allowedClasses = System.getProperty("jxpath.class.allow");
        if (allowedClasses != null && !allowedClasses.isEmpty()) {
            this.allowedClasses.addAll(Arrays.asList(allowedClasses.split(",")));
        }
    }

    /**
     * Specifies whether the Java class of the specified name be exposed via xpath
     *
     * @param className is the fully qualified name of the java class being checked.
     *                  This will not be null. Only non-array class names will be passed.
     * @return true if the java class can be exposed via xpath, false otherwise
     */
    @Override
    public boolean isClassNameExposed(String className) {
        if (allowedClasses.isEmpty()) {
            return false;
        }

        if (allowedClasses.contains("*")) {
            return true;
        }

        return allowedClasses.stream().anyMatch(pattern -> {
            if (Objects.equals(className, pattern)) {
                return true;
            }
            else if (pattern.endsWith("*")) {
                return className.startsWith(pattern.substring(0, pattern.length() - 1));
            }
            return false;
        });
    }
}
