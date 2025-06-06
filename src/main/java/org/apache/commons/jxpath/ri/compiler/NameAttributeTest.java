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

/**
 * Captures the {@code foo[@name=<em>expr</em>]} expression. These expressions are handled in a special way when applied to beans or maps.
 */
public class NameAttributeTest extends CoreOperationEqual {

    /**
     * Constructs a new NameAttributeTest.
     *
     * @param namePath  Expression
     * @param nameValue Expression
     */
    public NameAttributeTest(final Expression namePath, final Expression nameValue) {
        super(namePath, nameValue);
    }

    @Override
    public boolean computeContextDependent() {
        return true;
    }

    /**
     * Gets the name test expression.
     *
     * @return Expression
     */
    public Expression getNameTestExpression() {
        return args[1];
    }
}
