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
package org.apache.commons.jxpath.ri.model.jdom;

import java.util.Locale;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Implements NodePointerFactory for DOM elements.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class JDOMPointerFactory implements NodePointerFactory {

    /** factory order constant */
    public static final int JDOM_POINTER_FACTORY_ORDER = 110;

    public int getOrder() {
        return JDOM_POINTER_FACTORY_ORDER;
    }

    public NodePointer createNodePointer(
            QName name, Object bean, Locale locale) {
        if (bean instanceof Document) {
            return new JDOMNodePointer(bean, locale);
        }
        if (bean instanceof Element) {
            return new JDOMNodePointer(bean, locale);
        }
        return null;
    }

    public NodePointer createNodePointer(
            NodePointer parent, QName name, Object bean) {
        if (bean instanceof Document) {
            return new JDOMNodePointer(parent, bean);
        }
        if (bean instanceof Element) {
            return new JDOMNodePointer(parent, bean);
        }
        return null;
    }
}
