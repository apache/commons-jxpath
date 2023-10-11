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
package org.apache.commons.jxpath.ri.compiler;

/**
 * A test class with few methods, with different argument list
 */
public final class TestFunctions3 {

    static {
        System.out.println("TestFunctions3: static block...");
    }

    public TestFunctions3() {
        System.out.println("TestFunctions3: constructor...");
    }

    public static String testFunction3Method1() {
        System.out.println("TestFunctions3: testFunction3Method1 method...");
        return "testFunction3Method1";
    }

    public String testFunction3Method2(String str) {
        System.out.println("TestFunctions3: testFunction3Method2 method..." + str);
        return "testFunction3Method2:" + str;
    }

    public String testFunction3Method3(String str1, String str2) {
        System.out.println("TestFunctions3: testFunction3Method3 method..." + str1 + ", " + str2);
        return "testFunction3Method3:" + str1 + ":" + str2;
    }

}
