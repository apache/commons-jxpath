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
package org.apache.commons.jxpath.ri.model.beans;

import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Combines child node iterators of all elements of a collection into one
 * aggregate child node iterator.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.3 $ $Date: 2004/02/29 14:17:41 $
 */
public class CollectionChildNodeIterator extends CollectionNodeIterator {

    private NodeTest test;

    public CollectionChildNodeIterator(
        CollectionPointer pointer,
        NodeTest test,
        boolean reverse,
        NodePointer startWith) 
    {
        super(pointer, reverse, startWith);
        this.test = test;
    }

    protected NodeIterator getElementNodeIterator(NodePointer elementPointer) {
        return elementPointer.childIterator(test, false, null);
    }
}
