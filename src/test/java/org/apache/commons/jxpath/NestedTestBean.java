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

/**
 * A general purpose JavaBean for JUnit tests for the "jxpath" component.
 */
public class NestedTestBean {
    private String name = "Name 0";
    private int integer = 1;

    public NestedTestBean() {
    }

    public NestedTestBean(final String name) {
        this.name = name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * A read-only boolean property
     */
    public boolean isBoolean() {
        return false;
    }

    /**
     * A read-only int property
     */
    public int getInt() {
        return integer;
    }

    public void setInt(final int value) {
        this.integer = value;
    }

    /**
     * A read-only String property
     */
    public String getName() {
        return name;
    }

    private String[] strings =
        { "String 1", "String 2", "String 3" };

    public String[] getStrings() {
        return strings;
    }

    public void setStrings(final String[] array) {
        strings = array;
    }

    @Override
    public String toString() {
        return "Nested: " + name;
    }
}