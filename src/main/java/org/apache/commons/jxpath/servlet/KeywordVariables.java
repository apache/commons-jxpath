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

package org.apache.commons.jxpath.servlet;

import org.apache.commons.jxpath.Variables;

/**
 * Implementation of the Variables interface that provides access to a single object using a reserved name (keyword).
 */
public class KeywordVariables implements Variables {

    private static final long serialVersionUID = 894145608741325442L;

    /**
     * The keyword.
     */
    private final String keyword;

    /**
     * The value.
     */
    private final Object value;

    /**
     * Constructs a new KeywordVariables.
     *
     * @param keyword The keyword.
     * @param value  The value.
     */
    public KeywordVariables(final String keyword, final Object value) {
        if (keyword == null) {
            throw new IllegalArgumentException("keyword cannot be null");
        }
        this.keyword = keyword;
        this.value = value;
    }

    @Override
    public void declareVariable(final String variable, final Object value) {
        throw new UnsupportedOperationException("Cannot declare new keyword variables.");
    }

    @Override
    public Object getVariable(final String variable) {
        return isDeclaredVariable(variable) ? value : null;
    }

    @Override
    public boolean isDeclaredVariable(final String variable) {
        return variable.equals(keyword);
    }

    @Override
    public void undeclareVariable(final String variable) {
        throw new UnsupportedOperationException("Cannot undeclare keyword variables.");
    }
}
