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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple implementation of {@link NodeSet} that behaves as a collection of pointers.
 */
public class BasicNodeSet implements NodeSet {

    private final List<Pointer> pointers = new ArrayList<>();
    private List<Pointer> readOnlyPointers;
    private List nodes;
    private List values;

    /**
     * Constructs a new instance.
     */
    public BasicNodeSet() {
        // empty
    }

    /**
     * Add the specified NodeSet to this NodeSet.
     *
     * @param nodeSet to add
     */
    public void add(final NodeSet nodeSet) {
        if (pointers.addAll(nodeSet.getPointers())) {
            clear();
        }
    }

    /**
     * Add a pointer to this NodeSet.
     *
     * @param pointer to add
     */
    public void add(final Pointer pointer) {
        if (pointers.add(pointer)) {
            clear();
        }
    }

    /**
     * Clear cache list members.
     */
    private synchronized void clear() {
        readOnlyPointers = null;
        nodes = null;
        values = null;
    }

    @Override
    public synchronized List getNodes() {
        if (nodes == null) {
            nodes = Collections.unmodifiableList(pointers.stream().map(Pointer::getNode).collect(Collectors.toList()));
        }
        return nodes;
    }

    @Override
    public synchronized List<Pointer> getPointers() {
        if (readOnlyPointers == null) {
            readOnlyPointers = Collections.unmodifiableList(pointers);
        }
        return readOnlyPointers;
    }

    @Override
    public synchronized List getValues() {
        if (values == null) {
            values = Collections.unmodifiableList(pointers.stream().map(Pointer::getValue).collect(Collectors.toList()));
        }
        return values;
    }

    /**
     * Remove a pointer from this NodeSet.
     *
     * @param pointer to remove
     */
    public void remove(final Pointer pointer) {
        if (pointers.remove(pointer)) {
            clear();
        }
    }

    @Override
    public String toString() {
        return pointers.toString();
    }
}
