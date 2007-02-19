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

import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.InfoSetUtil;

/**
 * A compile tree element containing a constant number or string.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class Constant extends Expression {

    private Object value;

    public Constant(Number number) {
        this.value = number;
    }

    public Constant(String string) {
        this.value = string;
    }

    public Object compute(EvalContext context) {
        return value;
    }

    /**
     * Returns the value of the constant.
     */
    public Object computeValue(EvalContext context) {
        return value;
    }

    /**
     * Returns false
     */
    public boolean isContextDependent() {
        return false;
    }

    /**
     * Returns false
     */
    public boolean computeContextDependent() {
        return false;
    }

    public String toString() {
        if (value instanceof Number) {
            return InfoSetUtil.stringValue(value);
        }
        return "'" + value + "'";
    }
}