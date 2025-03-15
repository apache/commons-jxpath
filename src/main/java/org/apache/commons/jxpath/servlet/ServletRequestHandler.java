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
 * Implementation of the {@link org.apache.commons.jxpath.DynamicPropertyHandler}
 * interface that provides access to attributes and parameters
 * of a {@link ServletRequest}.
 */
public class ServletRequestHandler extends HttpSessionHandler {

    @Override
    protected void collectPropertyNames(final HashSet set, final Object bean) {
        super.collectPropertyNames(set, bean);
        final ServletRequestAndContext handle = (ServletRequestAndContext) bean;
        final ServletRequest servletRequest = handle.getServletRequest();
        Enumeration e = servletRequest.getAttributeNames();
        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }
        e = servletRequest.getParameterNames();
        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }
    }

    @Override
    public Object getProperty(final Object bean, final String property) {
        final ServletRequestAndContext handle = (ServletRequestAndContext) bean;
        final ServletRequest servletRequest = handle.getServletRequest();
        final String[] strings = servletRequest.getParameterValues(property);

        if (strings != null) {
            if (strings.length == 0) {
                return null;
            }
            if (strings.length == 1) {
                return strings[0];
            }
            return strings;
        }

        final Object object = servletRequest.getAttribute(property);
        if (object != null) {
            return object;
        }

        return super.getProperty(bean, property);
    }

    @Override
    public void setProperty(final Object request, final String property, final Object value) {
        ((ServletRequestAndContext) request).getServletRequest().setAttribute(property, value);
    }
}
