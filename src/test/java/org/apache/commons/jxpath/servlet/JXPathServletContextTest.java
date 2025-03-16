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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import javax.servlet.ServletContext;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.junit.jupiter.api.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockPageContext;
import com.mockrunner.mock.web.MockServletConfig;
import com.mockrunner.mock.web.MockServletContext;

/**
 */
public class JXPathServletContextTest {

    private void checkPointerIterator(final JXPathContext context) {
        final Iterator<Pointer> it = context.iteratePointers("/*");
        assertTrue(it.hasNext(), "Empty context");
        while (it.hasNext()) {
            final Pointer pointer = it.next();
            assertNotNull(pointer, "null pointer");
            assertNotNull(pointer.asPath(), "null path");
        }
    }

    private ServletContext getServletContext() {
        final MockServletContext context = new MockServletContext();
        context.setAttribute("app", "OK");
        return context;
    }

    @Test
    public void testPageContext() {
        final MockServletContext servletContext = new MockServletContext();
        servletContext.setAttribute("app", "app");
        final MockServletConfig servletConfig = new MockServletConfig();
        servletConfig.setServletContext(servletContext);
        final MockHttpSession session = new MockHttpSession();
        session.setupServletContext(servletContext);
        session.setAttribute("session", "session");
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("request", "request");
        request.setSession(session);
        final MockPageContext pageContext = new MockPageContext();
        pageContext.setServletConfig(servletConfig);
        pageContext.setServletRequest(request);
        pageContext.setAttribute("page", "page");
        assertSame(session, request.getSession(), "Request session");
        final JXPathContext context = JXPathServletContexts.getPageContext(pageContext);
        context.setLenient(true);
        checkPointerIterator(context);
        assertEquals("page", context.getValue("page"), "Page Scope");
        assertEquals("request", context.getValue("request"), "Request Scope");
        assertEquals("session", context.getValue("session"), "Session Scope");
        assertEquals("app", context.getValue("app"), "Application Scope");
        assertEquals("page", context.getValue("$page/page"), "Explicit Page Scope");
        assertEquals("request", context.getValue("$request/request"), "Explicit Request Scope");
        assertEquals("session", context.getValue("$session/session"), "Explicit Session Scope");
        assertEquals("app", context.getValue("$application/app"), "Explicit Application Scope");
        // iterate through the elements of page context only (two elements expected, 'page' and the context)
        final Iterator<Pointer> it = context.iteratePointers("$page/*");
        assertTrue(it.hasNext(), "element not found");
        it.next();
        it.next();
        assertFalse(it.hasNext(), "too many elements");
        // test setting a value in the context
        context.setValue("/foo1", "bar1");
        assertEquals("bar1", context.getValue("/foo1"), "Context property");
        context.setValue("$page/foo2", "bar2");
        assertEquals("bar2", context.getValue("$page/foo2"), "Context property");
    }

    @Test
    public void testServletContext() {
        final ServletContext context = getServletContext();
        final JXPathContext appContext = JXPathServletContexts.getApplicationContext(context);
        assertSame(appContext, JXPathServletContexts.getApplicationContext(context), "Cached context not property returned");
        assertEquals("OK", appContext.getValue("app"), "Application Context");
        checkPointerIterator(appContext);
        // test setting a value in the context
        appContext.setValue("/foo", "bar");
        assertEquals("bar", appContext.getValue("/foo"), "Context property");
        // test the variables
        final Variables variables = appContext.getVariables();
        assertNotNull(variables.getVariable("application"), "$application variable");
        assertNull(variables.getVariable("$foo"), "$foo variable");
    }

    @Test
    public void testServletRequest() {
        final ServletContext context = getServletContext();
        final MockHttpSession session = new MockHttpSession();
        session.setupServletContext(context);
        session.setUpIsNew(true);
        final Integer count = Integer.valueOf(10);
        session.setAttribute("count", count);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        request.setAttribute("attr", "OK");
        request.setupAddParameter("parm", "OK");
        request.setupAddParameter("multiparam", new String[] { "value1", "value2" });
        request.setupAddParameter("emptyparam", new String[0]);
        assertSame(session, request.getSession(), "Request session");
        final JXPathContext reqContext = JXPathServletContexts.getRequestContext(request, context);
        assertSame(reqContext, JXPathServletContexts.getRequestContext(request, context), "Cached context not property returned");
        final JXPathContext sessionContext = JXPathServletContexts.getSessionContext(session, context);
        assertSame(sessionContext, JXPathServletContexts.getSessionContext(session, context), "Cached context not property returned");
        assertEquals("OK", reqContext.getValue("attr"), "Request Context Attribute");
        assertEquals("OK", reqContext.getValue("parm"), "Request Context Parameter");
        assertTrue(reqContext.getValue("multiparam").getClass().isArray(), "Request Context Parameter (Array)");
        assertNull(reqContext.getValue("emptyparam"), "Request Context Parameter (Empty)");
        assertEquals(count, sessionContext.getValue("count"), "Session Context Parameter");
        assertEquals("OK", reqContext.getValue("app"), "Application Context via Request Context");
        assertEquals(count, reqContext.getValue("count"), "Session Context via Request Context");
        assertEquals("OK", sessionContext.getValue("app"), "Application Context via Session Context");
        checkPointerIterator(reqContext);
        checkPointerIterator(sessionContext);
        // test setting a value in the context
        reqContext.setValue("/foo1", "bar1");
        assertEquals("bar1", reqContext.getValue("/foo1"), "Context property");
        sessionContext.setValue("/foo2", "bar2");
        assertEquals("bar2", sessionContext.getValue("/foo2"), "Context property");
    }

    @Test
    public void testServletRequestWithoutSession() {
        final ServletContext context = getServletContext();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final JXPathContext reqContext = JXPathServletContexts.getRequestContext(request, context);
        assertEquals("OK", reqContext.getValue("app"), "Application Context via Request Context");
    }
}
