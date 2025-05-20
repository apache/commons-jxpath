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

package org.apache.commons.jxpath.ri.model;

import java.util.Locale;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.QName;

/**
 * NodePointerFactory to create {@link VariablePointer VariablePointers}.
 *
 * @since JXPath 1.3
 */
public class VariablePointerFactory implements NodePointerFactory {

    /**
     * Node value wrapper to trigger a VariablePointerFactory.
     */
    public static final class VariableContextWrapper {

        private final JXPathContext context;

        /**
         * Constructs a new VariableContextWrapper.
         *
         * @param context to wrap
         */
        private VariableContextWrapper(final JXPathContext context) {
            this.context = context;
        }

        /**
         * Gets the original (unwrapped) context.
         *
         * @return JXPathContext.
         */
        public JXPathContext getContext() {
            return context;
        }
    }

    /** Factory order constant */
    public static final int VARIABLE_POINTER_FACTORY_ORDER = 890;

    /**
     * VariableContextWrapper factory method.
     *
     * @param context the JXPathContext to wrap.
     * @return VariableContextWrapper.
     */
    public static VariableContextWrapper contextWrapper(final JXPathContext context) {
        return new VariableContextWrapper(context);
    }

    /**
     * Constructs a new instance.
     */
    public VariablePointerFactory() {
        // empty
    }

    @Override
    public NodePointer createNodePointer(final NodePointer parent, final QName qName, final Object object) {
        return createNodePointer(qName, object, null);
    }

    @Override
    public NodePointer createNodePointer(final QName qName, final Object object, final Locale locale) {
        if (object instanceof VariableContextWrapper) {
            JXPathContext varCtx = ((VariableContextWrapper) object).getContext();
            while (varCtx != null) {
                final Variables vars = varCtx.getVariables();
                if (vars.isDeclaredVariable(qName.toString())) {
                    return new VariablePointer(vars, qName);
                }
                varCtx = varCtx.getParentContext();
            }
            // The variable is not declared, but we will create
            // a pointer anyway in case the user wants to set, rather
            // than get, the value of the variable.
            return new VariablePointer(qName);
        }
        return null;
    }

    @Override
    public int getOrder() {
        return VARIABLE_POINTER_FACTORY_ORDER;
    }
}
