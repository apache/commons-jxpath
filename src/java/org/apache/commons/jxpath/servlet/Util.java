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

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Turns an Enumeration of Strings into an array of Strings.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.4 $ $Date: 2004/02/29 14:17:40 $
 */
public final class Util {

    private static final String[] STRING_ARRAY = new String[0];

    public static String[] toStrings(Enumeration e) {
        ArrayList list = new ArrayList(16);
        while (e.hasMoreElements()) {
            list.add(e.nextElement());
        }
        return (String[]) list.toArray(STRING_ARRAY);
    }
}
