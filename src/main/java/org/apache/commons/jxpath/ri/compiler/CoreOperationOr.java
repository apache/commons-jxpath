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

package org.apache.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.InfoSetUtil;

/**
 * Implementation of {@link Expression} for the operation "or".
 */
public class CoreOperationOr extends CoreOperation {

    /**
     * Constructs a new CoreOperationOr.
     *
     * @param args or'd Expression components
     */
    public CoreOperationOr(final Expression[] args) {
        super(args);
    }

    @Override
    public Object computeValue(final EvalContext context) {
        for (final Expression arg : args) {
            if (InfoSetUtil.booleanValue(arg.computeValue(context))) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    protected int getPrecedence() {
        return OR_PRECEDENCE;
    }

    @Override
    public String getSymbol() {
        return "or";
    }

    @Override
    protected boolean isSymmetric() {
        return true;
    }
}
