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
package org.apache.commons.jxpath.ri.model.beans;

import java.util.Locale;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Implements NodePointerFactory for stand-alone collections.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class CollectionPointerFactory implements NodePointerFactory {

    public static final int COLLECTION_POINTER_FACTORY_ORDER = 10;

    public int getOrder() {
        return COLLECTION_POINTER_FACTORY_ORDER;
    }

    public NodePointer createNodePointer(
        QName name,
        Object bean,
        Locale locale)
    {
        return ValueUtils.isCollection(bean) ? new CollectionPointer(bean, locale) : null;
    }

    public NodePointer createNodePointer(
        NodePointer parent,
        QName name,
        Object bean)
    {
        return ValueUtils.isCollection(bean) ? new CollectionPointer(parent, bean) : null;
    }
}