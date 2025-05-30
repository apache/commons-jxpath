/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Implementations of EvalContext used for different XPath axes (child::, parent:: etc). In order to evaluate a path, RI creates a chain of EvalContexts, one
 * for each step in the path.
 *
 * @see <a href="https://commons.apache.org/proper/commons-jxpath/apidocs/index.html">User's Guide</a>
 */
package org.apache.commons.jxpath.ri.axes;
