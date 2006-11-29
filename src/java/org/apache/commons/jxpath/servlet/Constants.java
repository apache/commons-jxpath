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

/**
 * String constants for this package.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public final class Constants {

    /**
     * Variable name for ServletContext.
     */
    public static final String APPLICATION_SCOPE = "application";

    /**
     * Variable name for HttpSession.
     */
    public static final String SESSION_SCOPE = "session";

    /**
     * Variable name for ServletRequest.
     */
    public static final String REQUEST_SCOPE = "request";

    /**
     * Variable name for PageContext.
     */
    public static final String PAGE_SCOPE = "page";

    /**
     * Attribute  name used in page context, requst, session, and servlet
     * context to store the corresponding JXPathContext.
     */
    public static final String JXPATH_CONTEXT =
        "org.apache.commons.jxpath.JXPATH_CONTEXT";

}
