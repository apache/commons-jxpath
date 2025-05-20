/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jxpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An object that aggregates {@link Functions} objects into a group Functions object. Since {@link JXPathContext} can only register a single Functions object,
 * FunctionLibrary should always be used to group all Functions objects that need to be registered.
 */
public class FunctionLibrary implements Functions {

    private final List<Functions> allFunctions = new ArrayList<>();
    private Map<String, Object> byNamespace;

    /**
     * Constructs a new instance.
     */
    public FunctionLibrary() {
        // empty
    }

    /**
     * Add functions to the library
     *
     * @param functions to add
     */
    public void addFunctions(final Functions functions) {
        allFunctions.add(functions);
        synchronized (this) {
            byNamespace = null;
        }
    }

    /**
     * Prepare the cache.
     *
     * @return cache map keyed by namespace
     */
    private synchronized Map<String, Object> functionCache() {
        if (byNamespace == null) {
            byNamespace = new HashMap<>();
            final int count = allFunctions.size();
            for (int i = 0; i < count; i++) {
                final Functions funcs = allFunctions.get(i);
                final Set<String> namespaces = funcs.getUsedNamespaces();
                for (final String ns : namespaces) {
                    final Object candidates = byNamespace.get(ns);
                    if (candidates == null) {
                        byNamespace.put(ns, funcs);
                    } else if (candidates instanceof Functions) {
                        final List<Object> lst = new ArrayList<>();
                        lst.add(candidates);
                        lst.add(funcs);
                        byNamespace.put(ns, lst);
                    } else {
                        ((List) candidates).add(funcs);
                    }
                }
            }
        }
        return byNamespace;
    }

    /**
     * Gets a Function, if any, for the specified namespace, name and parameter types.
     *
     * @param namespace  function namespace
     * @param name       function name
     * @param parameters parameters
     * @return Function found
     */
    @Override
    public Function getFunction(final String namespace, final String name, final Object[] parameters) {
        final Object candidates = functionCache().get(namespace);
        if (candidates instanceof Functions) {
            return ((Functions) candidates).getFunction(namespace, name, parameters);
        }
        if (candidates instanceof List) {
            final List<Functions> list = (List<Functions>) candidates;
            final int count = list.size();
            for (int i = 0; i < count; i++) {
                final Function function = list.get(i).getFunction(namespace, name, parameters);
                if (function != null) {
                    return function;
                }
            }
        }
        return null;
    }

    /**
     * Gets a set containing all namespaces used by the aggregated Functions.
     *
     * @return a set containing all namespaces used by the aggregated Functions.
     */
    @Override
    public Set<String> getUsedNamespaces() {
        return functionCache().keySet();
    }

    /**
     * Removes functions from the library.
     *
     * @param functions to remove.
     */
    public void removeFunctions(final Functions functions) {
        allFunctions.remove(functions);
        synchronized (this) {
            byNamespace = null;
        }
    }
}
