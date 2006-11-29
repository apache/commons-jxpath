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
package org.apache.commons.jxpath.ri.model.dynamic;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.NestedTestBean;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.TestBean;

/**
 * Test AbstractFactory.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class TestDynamicPropertyFactory extends AbstractFactory {

    /**
     * Create a new instance and put it in the collection on the parent object.
     * Return <b>false</b> if this factory cannot create the requested object.
     */
    public boolean createObject(
        JXPathContext context,
        Pointer pointer,
        Object parent,
        String name,
        int index) 
    {
        if (name.equals("map")) {
            ((TestBean) parent).setMap(new HashMap());
            return true;
        }
        else if (name.equals("TestKey1")) {
            ((Map) parent).put(name, "");
            return true;
        }
        else if (name.equals("TestKey2")) {
            ((Map) parent).put(name, new NestedTestBean("newName"));
            return true;
        }
        else if (name.equals("TestKey3")) {
            Vector v = new Vector();
            for (int i = 0; i <= index; i++) {
                v.add(null);
            }
            ((Map) parent).put(name, v);
            return true;
        }
        else if (name.equals("TestKey4")) {
            ((Map) parent).put(name, new Object[] { new TestBean()});
            return true;
        }
        return false;
    }

    public boolean declareVariable(JXPathContext context, String name) {
        return false;
    }
}
