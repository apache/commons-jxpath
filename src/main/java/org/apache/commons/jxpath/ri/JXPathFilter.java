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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A filter to be used by JXPath, to evaluate the xpath string values to impose any restrictions.
 * This class implements specific filter interfaces, and implements methods in those. 
 * For instance, it JXPathClassFilter interface, which is used to check if any restricted java classes are passed via xpath
 * JXPath uses this filter instance when an extension function instance is created.
 *
 * @author bhmohanr-techie
 * @version $Revision$ $Date$
 */
public class JXPathFilter implements JXPathClassFilter {
    ArrayList<String> allowedClassesList = null;

    public JXPathFilter() {
        init();
    }

    public void init() {
        String restrictedClasses = System.getProperty("jxpath.class.allow");
        allowedClassesList = null;
        if ((restrictedClasses != null) && (restrictedClasses.trim().length() > 0)) {
            allowedClassesList = new ArrayList<>();
            allowedClassesList.addAll(Arrays.asList(restrictedClasses.split(",")));
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
    public boolean exposeToXPath(String className) {
        if ((allowedClassesList == null) || (allowedClassesList.size() < 1)) {
            return false;
        }

        if (allowedClassesList.contains(className) ||  allowedClassesList.contains("*")) {
            return true;
        }

        return false;
    }
}

