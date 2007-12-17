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

import java.util.Enumeration;
import java.util.HashSet;

import javax.servlet.ServletRequest;

/**
 * Implementation of the DynamicPropertyHandler interface that provides
 * access to attributes and parameters of a ServletRequest.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class ServletRequestHandler extends HttpSessionHandler {

    protected void collectPropertyNames(HashSet set, Object bean) {
        super.collectPropertyNames(set, bean);
        ServletRequestAndContext handle = (ServletRequestAndContext) bean;
        ServletRequest servletRequest = handle.getServletRequest();
        Enumeration e = servletRequest.getAttributeNames();
        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }
        e = servletRequest.getParameterNames();
        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }
    }

    public Object getProperty(Object bean, String property) {
        ServletRequestAndContext handle = (ServletRequestAndContext) bean;
        ServletRequest servletRequest = handle.getServletRequest();
        String[] strings = servletRequest.getParameterValues(property);
        if (strings != null) {
            if (strings.length == 0) {
                return null;
            }
            if (strings.length == 1) {
                return strings[0];
            }
            return strings;
        }

        Object object = servletRequest.getAttribute(property);
        if (object != null) {
            return object;
        }

        return super.getProperty(bean, property);
    }

    public void setProperty(Object request, String property, Object value) {
        ((ServletRequest) request).setAttribute(property, value);
    }
}
