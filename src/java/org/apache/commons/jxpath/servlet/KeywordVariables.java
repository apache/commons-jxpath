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
package org.apache.commons.jxpath.servlet;

import org.apache.commons.jxpath.Variables;

/**
 * Implementation of the Variables interface that provides access
 * to a single object using a reserved name (keyword).
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class KeywordVariables implements Variables {

    private String keyword;
    private Object object;

    /**
     * Create a new KeywordVariables.
     * @param keyword String
     * @param object value
     */
    public KeywordVariables(String keyword, Object object) {
        if (keyword == null) {
            throw new IllegalArgumentException("keyword cannot be null");
        }
        this.keyword = keyword;
        this.object = object;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDeclaredVariable(String variable) {
        return variable.equals(keyword);
    }

    /**
     * {@inheritDoc}
     */
    public Object getVariable(String variable) {
        return isDeclaredVariable(variable) ? object : null;
    }

    /**
     * {@inheritDoc}
     */
    public void declareVariable(String variable, Object value) {
        throw new UnsupportedOperationException(
            "Cannot declare new keyword variables.");
    }

    /**
     * {@inheritDoc}
     */
    public void undeclareVariable(String variable) {
        throw new UnsupportedOperationException(
            "Cannot undeclare keyword variables.");
    }
}
