/*
 * Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.InfoSetUtil;

/**
 * Implementation of Expression for the operation "&lt;".
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.3 $ $Date: 2004/02/29 14:17:39 $
 */
public class CoreOperationLessThan extends CoreOperation {

    public CoreOperationLessThan(Expression arg1, Expression arg2) {
        super(new Expression[] { arg1, arg2 });
    }

    public Object computeValue(EvalContext context) {
        double l = InfoSetUtil.doubleValue(args[0].computeValue(context));
        double r = InfoSetUtil.doubleValue(args[1].computeValue(context));
        return l < r ? Boolean.TRUE : Boolean.FALSE;
    }
    
    protected int getPrecedence() {
        return 3;
    }

    protected boolean isSymmetric() {
        return false;
    }
    
    public String getSymbol() {
        return "<";
    }
}
