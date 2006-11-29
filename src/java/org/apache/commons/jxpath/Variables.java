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
 * Variables provide access to a global set of values accessible via XPath.
 * XPath can reference variables using the <code>"$varname"</code> syntax.
 * To use a custom implementation of this interface, pass it to
 * {@link JXPathContext#setVariables JXPathContext.setVariables()}
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public interface Variables {

    /**
     * Returns true if the specified variable is declared.
     */
    boolean isDeclaredVariable(String varName);

    /**
     * Returns the value of the specified variable.
     * Throws IllegalArgumentException if there is no such variable.
     */
    Object getVariable(String varName);

    /**
     * Defines a new variable with the specified value or modifies
     * the value of an existing variable.
     * May throw UnsupportedOperationException.
     */
    void declareVariable(String varName, Object value);

    /**
     * Removes an existing variable. May throw UnsupportedOperationException.
     *
     * @param varName is a variable name without the "$" sign
     */
    void undeclareVariable(String varName);
}