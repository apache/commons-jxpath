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
 * Implementation of the DynamicPropertyHandler interface that provides
 * access to attributes of a HttpSession.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class HttpSessionHandler extends ServletContextHandler {

    /**
     * {@inheritDoc}
     */
    protected void collectPropertyNames(HashSet set, Object bean) {
        HttpSessionAndServletContext handle =
            (HttpSessionAndServletContext) bean;
        super.collectPropertyNames(set, handle.getServletContext());
        HttpSession session = handle.getSession();
        if (session != null) {
            Enumeration e = session.getAttributeNames();
            while (e.hasMoreElements()) {
                set.add(e.nextElement());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getProperty(Object bean, String property) {
        HttpSessionAndServletContext handle =
            (HttpSessionAndServletContext) bean;
        HttpSession session = handle.getSession();
        if (session != null) {
            Object object = session.getAttribute(property);
            if (object != null) {
                return object;
            }
        }
        return super.getProperty(handle.getServletContext(), property);
    }

    /**
     * {@inheritDoc}
     */
    public void setProperty(Object bean, String property, Object value) {
        HttpSessionAndServletContext handle =
            (HttpSessionAndServletContext) bean;
        HttpSession session = handle.getSession();
        if (session != null) {
            session.setAttribute(property, value);
        }
        else {
            throw new JXPathException("Cannot set session attribute: "
                    + "there is no session");
        }
    }
}
