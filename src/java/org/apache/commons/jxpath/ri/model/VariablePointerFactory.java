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
package org.apache.commons.jxpath.ri.model;

import java.util.Locale;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.QName;

/**
 * NodePointerFactory to create {@link VariablePointer VariablePointers}.
 * @author Matt Benson
 * @since JXPath 1.3
 * @version $Revision$ $Date$
 */
public class VariablePointerFactory implements NodePointerFactory {
    /** factory order constant */
    public static final int VARIABLE_POINTER_FACTORY_ORDER = 890;

    /**
     * Node value wrapper to trigger a VariablePointerFactory.
     */
    public static final class VariableContextWrapper {
        private final JXPathContext context;

        /**
         * Create a new VariableContextWrapper.
         * @param context to wrap
         */
        private VariableContextWrapper(JXPathContext context) {
            this.context = context;
        }

        /**
         * Get the original (unwrapped) context.
         *
         * @return JXPathContext.
         */
        public JXPathContext getContext() {
            return context;
        }
    }

    /**
     * VariableContextWrapper factory method.
     * @param context the JXPathContext to wrap.
     * @return VariableContextWrapper.
     */
    public static VariableContextWrapper contextWrapper(JXPathContext context) {
        return new VariableContextWrapper(context);
    }

    public NodePointer createNodePointer(QName name, Object object,
            Locale locale) {
        if (object instanceof VariableContextWrapper) {
            JXPathContext varCtx = ((VariableContextWrapper) object).getContext();
            while (varCtx != null) {
                Variables vars = varCtx.getVariables();
                if (vars.isDeclaredVariable(name.toString())) {
                    return new VariablePointer(vars, name);
                }
                varCtx = varCtx.getParentContext();
            }
            // The variable is not declared, but we will create
            // a pointer anyway in case the user wants to set, rather
            // than get, the value of the variable.
            return new VariablePointer(name);
        }
        return null;
    }

    public NodePointer createNodePointer(NodePointer parent, QName name,
            Object object) {
        return createNodePointer(name, object, null);
    }

    public int getOrder() {
        return VARIABLE_POINTER_FACTORY_ORDER;
    }

}
