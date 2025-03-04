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
package org.apache.commons.jxpath.util;

import java.math.BigDecimal;

/**
 * Tests BasicTypeConverter (without common-beanutils) - all other tests from
 * BasicTypeConverterTest should still run, but trying to convert anything
 * needing BeanUtils should fail.
 */
public class BasicTypeConverterWithoutBeanUtilsTest extends BasicTypeConverterTest {

    public void testBeanUtilsConverter() {
        assertFalse("Cannot convert: String to BigDecimal without BeanUtils",
                    TypeUtils.canConvert("12", BigDecimal.class));

        Exception e = null;
        try {
            TypeUtils.convert("12", BigDecimal.class);
        }
        catch (Exception ex) {
            e = ex;
        }
        assertNotNull("Exception thrown when trying to convert", e);
    }
}
