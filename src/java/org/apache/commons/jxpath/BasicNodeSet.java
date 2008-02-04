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
package org.apache.commons.jxpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple implementation of NodeSet that behaves as a collection of pointers.
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class BasicNodeSet implements NodeSet {
    private List pointers = new ArrayList();
    private List readOnlyPointers;
    private List nodes;
    private List values;

    /**
     * Add a pointer to this NodeSet.
     * @param pointer to add
     */
    public void add(Pointer pointer) {
        if (pointers.add(pointer)) {
            clearCacheLists();
        }
    }

    /**
     * Add the specified NodeSet to this NodeSet.
     * @param nodeSet to add
     */
    public void add(NodeSet nodeSet) {
        if (pointers.addAll(nodeSet.getPointers())) {
            clearCacheLists();
        }
    }

    /**
     * Remove a pointer from this NodeSet.
     * @param pointer to remove
     */
    public void remove(Pointer pointer) {
        if (pointers.remove(pointer)) {
            clearCacheLists();
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized List getPointers() {
        if (readOnlyPointers == null) {
            readOnlyPointers = Collections.unmodifiableList(pointers);
        }
        return readOnlyPointers;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized List getNodes() {
        if (nodes == null) {
            nodes = new ArrayList();
            for (int i = 0; i < pointers.size(); i++) {
                Pointer pointer = (Pointer) pointers.get(i);
                nodes.add(pointer.getNode());
            }
            nodes = Collections.unmodifiableList(nodes);
        }
        return nodes;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized List getValues() {
        if (values == null) {
            values = new ArrayList();
            for (int i = 0; i < pointers.size(); i++) {
                Pointer pointer = (Pointer) pointers.get(i);
                values.add(pointer.getValue());
            }
            values = Collections.unmodifiableList(values);
        }
        return values;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return pointers.toString();
    }

    /**
     * Clear cache list members.
     */
    private synchronized void clearCacheLists() {
        readOnlyPointers = null;
        nodes = null;
        values = null;
    }

}
