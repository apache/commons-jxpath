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

import java.util.Iterator;

/**
 * Represents a compiled XPath. The interpretation of compiled XPaths may be faster, because it bypasses the compilation step. The reference implementation of
 * {@link JXPathContext} also globally caches some of the results of compilation, so the direct use of JXPathContext is not always less efficient than the use
 * of CompiledExpression.
 * <p>
 * Use CompiledExpression only when there is a need to evaluate the same expression multiple times and the CompiledExpression can be conveniently cached.
 * <p>
 * To acquire a CompiledExpression, call {@link JXPathContext#compile JXPathContext.compile}
 */
public interface CompiledExpression {

    /**
     * Creates intermediate elements of the path by invoking an {@link AbstractFactory}, which should first be installed on the context by calling
     * {@link JXPathContext#setFactory}.
     *
     * @param context base
     * @return Pointer created
     */
    Pointer createPath(JXPathContext context);

    /**
     * The same as setValue, except it creates intermediate elements of the path by invoking an {@link AbstractFactory}, which should first be installed on the
     * context by calling {@link JXPathContext#setFactory}.
     * <p>
     * Will throw an exception if one of the following conditions occurs:
     * <ul>
     * <li>Elements of the XPath aleady exist, by the path does not in fact describe an existing property</li>
     * <li>The AbstractFactory fails to create an instance for an intermediate element.</li>
     * <li>The property is not writable (no public, non-static set method)</li>
     * </ul>
     *
     * @param context base
     * @param value   to set
     * @return Pointer created
     */
    Pointer createPathAndSetValue(JXPathContext context, Object value);

    /**
     * Traverses the XPath and returns a Pointer. A Pointer provides easy access to a property. If the XPath matches no properties in the graph, the pointer
     * will be null.
     *
     * @param context base
     * @param xpath   string
     * @return Pointer found
     */
    Pointer getPointer(JXPathContext context, String xpath);

    /**
     * Evaluates the XPath and returns the resulting object. Primitive types are wrapped into objects.
     *
     * @param context to evaluate
     * @return Object
     */
    Object getValue(JXPathContext context);

    /**
     * Evaluates the xpath, converts the result to the specified class and returns the resulting object.
     *
     * @param context      to evaluate
     * @param requiredType return type
     * @return Object
     */
    Object getValue(JXPathContext context, Class requiredType);

    /**
     * Traverses the XPath and returns a Iterator of all results found for the path. If the XPath matches no properties in the graph, the Iterator will not be
     * null.
     *
     * @param context base
     * @return Iterator
     */
    Iterator iterate(JXPathContext context);

    /**
     * Traverses the XPath and returns an Iterator of Pointers. A Pointer provides easy access to a property. If the XPath matches no properties in the graph,
     * the Iterator be empty, but not null.
     *
     * @param context to iterate
     * @return Iterator
     */
    Iterator<Pointer> iteratePointers(JXPathContext context);

    /**
     * Remove all graph elements described by this expression.
     *
     * @param context base
     */
    void removeAll(JXPathContext context);

    /**
     * Remove the graph element described by this expression.
     *
     * @param context base
     */
    void removePath(JXPathContext context);

    /**
     * Modifies the value of the property described by the supplied xpath. Will throw an exception if one of the following conditions occurs:
     * <ul>
     * <li>The XPath does not in fact describe an existing property</li>
     * <li>The property is not writable (no public, non-static set method)</li>
     * </ul>
     *
     * @param context base
     * @param value   to set
     */
    void setValue(JXPathContext context, Object value);
}
