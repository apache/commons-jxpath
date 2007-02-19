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

/**
 * The common subclass for tree elements representing core operations like "+",
 * "- ", "*" etc.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public abstract class CoreOperation extends Operation {
        
    public CoreOperation(Expression args[]) {
        super(args);
    }

    public Object compute(EvalContext context) {
        return computeValue(context);
    }

    public abstract Object computeValue(EvalContext context);
    
    /**
     * Returns the XPath symbol for this operation, e.g. "+", "div", etc.
     */
    public abstract String getSymbol();
    
    /**
     * Returns true if the operation is not sensitive to the order of arguments,
     * e.g. "=", "and" etc, and false if it is, e.g. "&lt;=", "div".
     */
    protected abstract boolean isSymmetric();
    
    /**
     * Computes the precedence of the operation.
     */
    protected abstract int getPrecedence();
    
    public String toString() {
        if (args.length == 1) {
            return getSymbol() + parenthesize(args[0], false);
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                buffer.append(' ');
                buffer.append(getSymbol());
                buffer.append(' ');
            }
            buffer.append(parenthesize(args[i], i == 0));
        }
        return buffer.toString();
    }
    
    private String parenthesize(Expression expression, boolean left) {
        String s = expression.toString();
        if (!(expression instanceof CoreOperation)) {
            return s;
        }
        int compared = getPrecedence() - ((CoreOperation) expression).getPrecedence();

        if (compared < 0) {
            return s;
        }
        if (compared == 0 && (isSymmetric() || left)) {
            return s;
        }
        return '(' + s + ')';
    }    
}