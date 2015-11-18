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
package org.apache.commons.jxpath;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implements the DynamicPropertyHandler interface for {@link java.util.Map}.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class MapDynamicPropertyHandler implements DynamicPropertyHandler {

    public String[] getPropertyNames(Object object) {
        Map map = (Map) object;
        Set set = map.keySet();
        String[] names = new String[set.size()];
        Iterator it = set.iterator();
        for (int i = 0; i < names.length; i++) {
            names[i] = String.valueOf(it.next());
        }
        return names;
    }

    public Object getProperty(Object object, String propertyName) {
        return ((Map) object).get(propertyName);
    }

    public void setProperty(Object object, String propertyName, Object value) {
        ((Map) object).put(propertyName, value);
    }
}
