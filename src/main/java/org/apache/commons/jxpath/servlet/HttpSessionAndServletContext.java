/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jxpath.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * Just a structure to hold a ServletRequest and ServletContext together.
 */
public class HttpSessionAndServletContext {

    private final HttpSession session;
    private final ServletContext context;

    /**
     * Constructs a new HttpSessionAndServletContext.
     *
     * @param session HttpSession
     * @param context ServletContext
     */
    public HttpSessionAndServletContext(final HttpSession session, final ServletContext context) {
        this.session = session;
        this.context = context;
    }

    /**
     * Gets the ServletContext.
     *
     * @return ServletContext
     */
    public ServletContext getServletContext() {
        return context;
    }

    /**
     * Gets the session.
     *
     * @return HttpSession
     */
    public HttpSession getSession() {
        return session;
    }
}
