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
package org.apache.commons.jxpath.ri.model.dynamic;

import java.util.Locale;

import org.apache.commons.jxpath.DynamicPropertyHandler;
import org.apache.commons.jxpath.JXPathBeanInfo;
import org.apache.commons.jxpath.JXPathIntrospector;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.apache.commons.jxpath.ri.model.beans.NullPointer;
import org.apache.commons.jxpath.util.ValueUtils;

/**
 * Implements NodePointerFactory for Dynamic classes like Map.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class DynamicPointerFactory implements NodePointerFactory {

    /**
     * Factory order constant.
     */
    public static final int DYNAMIC_POINTER_FACTORY_ORDER = 800;

    public int getOrder() {
        return DYNAMIC_POINTER_FACTORY_ORDER;
    }

    public NodePointer createNodePointer(
        QName name,
        Object bean,
        Locale locale) {
        JXPathBeanInfo bi = JXPathIntrospector.getBeanInfo(bean.getClass());
        if (bi.isDynamic()) {
            DynamicPropertyHandler handler =
                ValueUtils.getDynamicPropertyHandler(
                    bi.getDynamicPropertyHandlerClass());
            return new DynamicPointer(name, bean, handler, locale);
        }
        return null;
    }

    public NodePointer createNodePointer(
        NodePointer parent,
        QName name,
        Object bean) {
        if (bean == null) {
            return new NullPointer(parent, name);
        }

        JXPathBeanInfo bi = JXPathIntrospector.getBeanInfo(bean.getClass());
        if (bi.isDynamic()) {
            DynamicPropertyHandler handler =
                ValueUtils.getDynamicPropertyHandler(
                    bi.getDynamicPropertyHandlerClass());
            return new DynamicPointer(parent, name, bean, handler);
        }
        return null;
    }
}
