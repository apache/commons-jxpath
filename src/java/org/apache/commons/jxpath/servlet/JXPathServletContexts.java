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
package org.apache.commons.jxpath.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathContextFactory;
import org.apache.commons.jxpath.JXPathIntrospector;

/**
 * Static methods that allocate and cache JXPathContexts bound to PageContext,
 * ServletRequest, HttpSession and ServletContext.
 * <p>
 * The JXPathContext returned by {@link #getPageContext getPageContext()}
 * provides access to all scopes via the PageContext.findAttribute()
 * method.  Thus, an expression like "foo" will first look for the attribute
 * named "foo" in the "page" context, then the "request" context, then
 * the "session" one and finally in the "application" context.
 * <p>
 * If you need to limit the attibute lookup to just one scope, you can use the
 * pre-definded variables "page", "request", "session" and "application".
 * For example, the expression "$session/foo" extracts the value of the
 * session attribute named "foo".
 * <p>
 * Following are some implementation details. There is a separate JXPathContext
 * for each of the four scopes. These contexts are chained according to the
 * nesting of the scopes.  So, the parent of the "page" JXPathContext is a
 * "request" JXPathContext, whose parent is a "session" JXPathContext (that is
 * if there is a session), whose parent is an "application" context.
 * <p>
 * The  XPath context node for each context is the corresponding object:
 * PageContext, ServletRequest, HttpSession or ServletContext.  This feature can
 * be used by servlets.  A servlet can use one of the methods declared by this
 * class and work with a specific JXPathContext for any scope.
 * <p>
 * Since JXPath chains lookups for variables and extension functions, variables
 * and extension function declared in the outer scopes are also available in
 * the inner scopes.
 * <p>
 * Each  of the four context declares exactly one variable, the value of which
 * is the corresponding object: PageContext, etc.
 * <p>
 * The  "session" variable will be undefined if there is no session for this
 * servlet. JXPath does not automatically create sessions.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2004/02/29 14:17:40 $
 */
public final class JXPathServletContexts {

    private static JXPathContextFactory factory;

    static {
        JXPathIntrospector.registerDynamicClass(
            PageScopeContext.class,
            PageScopeContextHandler.class);
        factory = JXPathContextFactory.newInstance();
    }

    /**
     * Returns a JXPathContext bound to the "page" scope. Caches that context
     * within the PageContext itself.
     */
    public static JXPathContext getPageContext(PageContext pageContext) {
        JXPathContext context =
            (JXPathContext) pageContext.getAttribute(Constants.JXPATH_CONTEXT);
        if (context == null) {
            JXPathIntrospector.registerDynamicClass(
                pageContext.getClass(),
                PageContextHandler.class);
            JXPathContext parentContext =
                getRequestContext(
                    pageContext.getRequest(),
                    pageContext.getServletContext());
            context = factory.newContext(parentContext, pageContext);
            context.setVariables(
                new KeywordVariables(
                    Constants.PAGE_SCOPE,
                    new PageScopeContext(pageContext)));
            pageContext.setAttribute(Constants.JXPATH_CONTEXT, context);
        }
        return context;
    }

    /**
     * Returns a JXPathContext bound to the "request" scope. Caches that context
     * within the request itself.
     */
    public static JXPathContext getRequestContext(
        ServletRequest request,
        ServletContext servletContext) 
    {
        JXPathContext context =
            (JXPathContext) request.getAttribute(Constants.JXPATH_CONTEXT);
        if (context == null) {
            JXPathContext parentContext = null;
            if (request instanceof HttpServletRequest) {
                HttpSession session =
                    ((HttpServletRequest) request).getSession(false);
                if (session != null) {
                    parentContext = getSessionContext(session, servletContext);
                }
                else {
                    parentContext = getApplicationContext(servletContext);
                }
            }
            JXPathIntrospector.registerDynamicClass(
                request.getClass(),
                ServletRequestHandler.class);
            context = factory.newContext(parentContext, request);
            context.setVariables(
                new KeywordVariables(Constants.REQUEST_SCOPE, request));
            request.setAttribute(Constants.JXPATH_CONTEXT, context);
        }
        return context;
    }

    /**
     * Returns a JXPathContext bound to the "session" scope. Caches that context
     * within the session itself.
     */
    public static JXPathContext getSessionContext(
        HttpSession session,
        ServletContext servletContext) 
    {
        JXPathContext context =
            (JXPathContext) session.getAttribute(Constants.JXPATH_CONTEXT);
        if (context == null) {
            JXPathIntrospector.registerDynamicClass(
                session.getClass(),
                HttpSessionHandler.class);
            JXPathContext parentContext = getApplicationContext(servletContext);
            context = factory.newContext(parentContext, session);
            context.setVariables(
                new KeywordVariables(Constants.SESSION_SCOPE, session));
            session.setAttribute(Constants.JXPATH_CONTEXT, context);
        }
        return context;
    }

    /**
     * Returns  a JXPathContext bound to the "application" scope. Caches that
     * context within the servlet context itself.
     */
    public static JXPathContext getApplicationContext(
            ServletContext servletContext) 
    {
        JXPathContext context =
            (JXPathContext) servletContext.getAttribute(
                Constants.JXPATH_CONTEXT);
        if (context == null) {
            JXPathIntrospector.registerDynamicClass(
                servletContext.getClass(),
                ServletContextHandler.class);
            context = factory.newContext(null, servletContext);
            context.setVariables(
                new KeywordVariables(
                    Constants.APPLICATION_SCOPE,
                    servletContext));
            servletContext.setAttribute(Constants.JXPATH_CONTEXT, context);
        }
        return context;
    }
}
