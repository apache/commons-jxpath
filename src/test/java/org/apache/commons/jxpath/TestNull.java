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
 * General purpose test bean for JUnit tests for the "jxpath" component.
 */
public class TestNull {

    private Object nothing = null;
    public Object getNothing() {
        return nothing;
    }

    public void setNothing(final Object something) {
        this.nothing = something;
    }

    /**
     */
    private static String[] array = { "a", null, "b" };
    public String[] getArray() {
        return array;
    }

    private TestNull child;

    public TestNull getChild() {
        if (child == null) {
            child = new TestNull();
        }
        return child;
    }
}
