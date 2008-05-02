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
package org.apache.commons.jxpath.servlet;

import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.jxpath.DynamicPropertyHandler;

/**
 * Implementation of the {@link DynamicPropertyHandler} interface that provides
 * access to attributes of a {@link PageScopeContext}.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class PageScopeContextHandler implements DynamicPropertyHandler {

    private static final int DEFAULT_LIST_SIZE = 16;

    public String[] getPropertyNames(Object pageScope) {
        Enumeration e = ((PageScopeContext) pageScope).getAttributeNames();
        ArrayList list = new ArrayList(DEFAULT_LIST_SIZE);
        while (e.hasMoreElements()) {
            list.add(e.nextElement());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public Object getProperty(Object pageScope, String property) {
        return ((PageScopeContext) pageScope).getAttribute(property);
    }

    public void setProperty(Object pageScope, String property, Object value) {
        ((PageScopeContext) pageScope).setAttribute(property, value);
    }
}
