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

import javax.servlet.http.HttpSession;
import org.apache.commons.jxpath.DynamicPropertyHandler;

/**
 * Implementation of the DynamicPropertyHandler interface that provides
 * access to attributes of a HttpSession.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2004/02/29 14:17:40 $
 */
public class HttpSessionHandler implements DynamicPropertyHandler {

    public String[] getPropertyNames(Object session) {
        Enumeration e = ((HttpSession) session).getAttributeNames();
        return Util.toStrings(e);
    }

    public Object getProperty(Object session, String property) {
        return ((HttpSession) session).getAttribute(property);
    }

    public void setProperty(Object session, String property, Object value) {
        ((HttpSession) session).setAttribute(property, value);
    }
}
