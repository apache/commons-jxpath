/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
 * @version $Revision: 1.6 $ $Date: 2004/02/29 14:17:40 $
 */
public class KeywordVariables implements Variables {

    private String keyword;
    private Object object;

    public KeywordVariables(String keyword, Object object) {
        this.keyword = keyword;
        this.object = object;
    }

    public boolean isDeclaredVariable(String variable) {
        return variable.equals(keyword);
    }

    public Object getVariable(String variable) {
        if (variable.equals(keyword)) {
            return object;
        }
        return null;
    }

    public void declareVariable(String variable, Object value) {
        throw new UnsupportedOperationException(
            "Cannot declare new keyword variables.");
    }

    public void undeclareVariable(String variable) {
        throw new UnsupportedOperationException(
            "Cannot declare new keyword variables.");
    }
}
