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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jxpath.util.ValueUtils;

/**
 * General purpose test bean for JUnit tests for the "jxpath" component.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class TestBean {

    // ------------------------------------------------------------- Properties

    /**
     * An array of nested java beans.
     */
    private NestedTestBean[] beans;
    {
        beans = new NestedTestBean[2];
        beans[0] = new NestedTestBean("Name 1");
        beans[1] = new NestedTestBean("Name 2");
        beans[1].setInt(3);
    }

    public NestedTestBean[] getBeans() {
        return beans;
    }

    public void setBeans(NestedTestBean[] beans) {
        this.beans = beans;
    }

    /**
     * A boolean property.
     */
    private boolean bool = false;
    public boolean isBoolean() {
        return bool;
    }

    public void setBoolean(boolean bool) {
        this.bool = bool;
    }

    private int integer = 1;
    /**
     * A read-only integer property
     */
    public int getInt() {
        return integer;
    }

    public void setInt(int integer) {
        this.integer = integer;
    }

    /**
     * A read-only array of integers
     */
    private int[] array = { 1, 2, 3, 4 };
    public int[] getIntegers() {
        return array;
    }

    public int getIntegers(int index) {
        return array[index];
    }

    public void setIntegers(int index, int value) {
        if (index >= array.length) {
            array = (int[]) ValueUtils.expandCollection(array, index + 1);
        }
        array[index] = value;
    }

    /**
     * A heterogeneous list: String, Integer, NestedTestBean
     */
    private ArrayList list;
    public List getList() {
        if (list == null) {
            list = new ArrayList();
            list.add("String 3");
            list.add(new Integer(3));
            list.add(new NestedTestBean("Name 3"));
        }
        return list;
    }

    /**
     * A Map
     */
    private HashMap map;
    {
        map = new HashMap();
        map.put("Key1", "Value 1");
        map.put("Key2", new NestedTestBean("Name 6"));
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = (HashMap) map;
    }

    /**
     * A nested read-only java bean
     */
    private NestedTestBean nestedBean = new NestedTestBean("Name 0");
    public NestedTestBean getNestedBean() {
        return nestedBean;
    }

    public void setNestedBean(NestedTestBean bean) {
        this.nestedBean = bean;
    }

    private NestedTestBean object = new NestedTestBean("Name 5");

    /**
     * Returns a NestedTestBean: testing recognition of generic objects
     */
    public Object getObject() {
        return object;
    }

    /**
     * Returns an array of ints: testing recognition of generic objects
     */
    public Object getObjects() {
        return getIntegers();
    }

    /**
     * A heterogeneous set: String, Integer, NestedTestBean
     */
    private HashSet set;
    public Set getSet() {
        if (set == null) {
            set = new HashSet();
            set.add("String 4");
            set.add(new Integer(4));
            set.add(new NestedTestBean("Name 4"));
        }
        return set;
    }

    public String toString() {
        return "ROOT";
    }
}
