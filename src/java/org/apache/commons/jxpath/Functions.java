/*
 * Copyright 1999-2004 The Apache Software Foundation
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
package org.apache.commons.jxpath;

import java.util.Set;

/**
 * A group of Function objects sharing a common namespace or a set of
 * common namespaces. Use the classes
 * {@link ClassFunctions ClassFunctions} and 
 * {@link PackageFunctions PackageFunctions}
 * to register extension functions implemented as Java methods.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.7 $ $Date: 2004/02/29 14:17:42 $
 */
public interface Functions {

    /**
     * Returns all namespaces in which this function collection defines
     * functions.
     */
    Set getUsedNamespaces();

    /**
     * Returns a Function, if any, for the specified namespace,
     * name and parameter types.
     */
    Function getFunction(String namespace, String name, Object[] parameters);
}