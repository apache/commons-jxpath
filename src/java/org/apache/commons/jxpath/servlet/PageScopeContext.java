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

import java.util.Enumeration;

import javax.servlet.jsp.PageContext;

/**
 * A lightweight wrapper for PageContext that restricts access
 * to attributes of the "page" scope.  This object is needed so that
 * XPath "foo" would lookup the attribute "foo" in all scopes, while
 * "$page/foo" would only look in the "page" scope.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2004/02/29 14:17:40 $
 */
public class PageScopeContext {
    private PageContext pageContext;

    public PageScopeContext(PageContext pageContext) {
        this.pageContext = pageContext;
    }

    /**
     * Returns attributes of the pageContext declared in the "page" scope.
     */
    public Enumeration getAttributeNames() {
        return pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE);
    }

    public Object getAttribute(String attribute) {
        return pageContext.getAttribute(attribute, PageContext.PAGE_SCOPE);
    }

    public void setAttribute(String attribute, Object value) {
        pageContext.setAttribute(attribute, value, PageContext.PAGE_SCOPE);
    }
}
