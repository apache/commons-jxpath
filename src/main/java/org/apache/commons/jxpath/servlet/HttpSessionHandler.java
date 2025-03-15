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

import javax.servlet.http.HttpSession;

import org.apache.commons.jxpath.JXPathException;

/**
 * Implementation of the {@link org.apache.commons.jxpath.DynamicPropertyHandler}
 * interface that provides access to attributes of a @{link HttpSession}.
 */
public class HttpSessionHandler extends ServletContextHandler {

    @Override
    protected void collectPropertyNames(final HashSet set, final Object bean) {
        final HttpSessionAndServletContext handle =
            (HttpSessionAndServletContext) bean;
        super.collectPropertyNames(set, handle.getServletContext());
        final HttpSession session = handle.getSession();
        if (session != null) {
            final Enumeration e = session.getAttributeNames();
            while (e.hasMoreElements()) {
                set.add(e.nextElement());
            }
        }
    }

    @Override
    public Object getProperty(final Object bean, final String property) {
        final HttpSessionAndServletContext handle =
            (HttpSessionAndServletContext) bean;
        final HttpSession session = handle.getSession();
        if (session != null) {
            final Object object = session.getAttribute(property);
            if (object != null) {
                return object;
            }
        }
        return super.getProperty(handle.getServletContext(), property);
    }

    @Override
    public void setProperty(final Object bean, final String property, final Object value) {
        final HttpSessionAndServletContext handle =
            (HttpSessionAndServletContext) bean;
        final HttpSession session = handle.getSession();
        if (session != null) {
            session.setAttribute(property, value);
        }
        else {
            throw new JXPathException("Cannot set session attribute: "
                    + "there is no session");
        }
    }
}
