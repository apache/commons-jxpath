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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A filter to be used by JXPath based on the system property jxpath.class.allow.
 * This property is a comma separated list of patterns defining which classes
 * are allowed to be loaded by JXPath.
 */
public class SystemPropertyJXPathFilter extends AbstractJXPathFilter {

    public SystemPropertyJXPathFilter() {
        super(loadAllowedClassesFromSystemProperty());
    }

    private static Set<String> loadAllowedClassesFromSystemProperty() {
        final String allowedClasses = System.getProperty("jxpath.class.allow");
        List<String> allowedClassesList =
                allowedClasses != null && !allowedClasses.isEmpty()
                        ? Arrays.asList(allowedClasses.split(","))
                        : Collections.emptyList();
        return Collections.unmodifiableSet(new HashSet<>(allowedClassesList));
    }
}
