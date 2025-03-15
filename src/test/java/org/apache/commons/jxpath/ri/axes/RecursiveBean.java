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
package org.apache.commons.jxpath.ri.axes;

/**
 * This bean is used to test infinite recursion protection in
 * descendant search contexts.
 */
public class RecursiveBean  {

    private final String name;
    private RecursiveBean first;
    private RecursiveBean second;

    public RecursiveBean(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public RecursiveBean getFirst() {
        return first;
    }

    public void setFirst(final RecursiveBean bean) {
        this.first = bean;
    }

    public RecursiveBean getSecond() {
        return second;
    }

    public void setSecond(final RecursiveBean bean) {
        second = bean;
    }

    @Override
    public String toString() {
        return name;
    }
}
