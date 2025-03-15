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

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Just a structure to hold a {@link ServletRequest} and {@link ServletContext}
 * together.
 */
public class ServletRequestAndContext extends HttpSessionAndServletContext {
    private final ServletRequest request;

    /**
     * Create a new ServletRequestAndContext.
     *
     * @param request ServletRequest
     * @param context ServletContext
     */
    public ServletRequestAndContext(final ServletRequest request,
            final ServletContext context) {
        super(null, context);
        this.request = request;
    }

    @Override
    public HttpSession getSession() {
        return request instanceof HttpServletRequest
                ? ((HttpServletRequest) request).getSession(false) : null;
    }

    /**
     * Gets the request.
     *
     * @return ServletRequest
     */
    public ServletRequest getServletRequest() {
        return request;
    }
}
