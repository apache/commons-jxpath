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
package org.apache.commons.jxpath.ri;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.AbstractJXPathTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test simple ExceptionHandler.
 */
public class ExceptionHandlerTest extends AbstractJXPathTest {
    public static class Bar {
        public Object getBaz() {
            throw new IllegalStateException("baz unavailable");
        }
    }

    private JXPathContext context;
    private final Bar bar = new Bar();

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        context = JXPathContext.newContext(this);
        context.setExceptionHandler((t, ptr) -> {
            if (t instanceof Error) {
                throw (Error) t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new RuntimeException(t);
        });
    }

    public Object getFoo() {
        throw new IllegalStateException("foo unavailable");
    }

    @Test
    public void testHandleFoo() throws Exception {
        Throwable t = assertThrows(Throwable.class, () -> context.getValue("foo"),
            "expected Throwable");

        while (t != null) {
            if ("foo unavailable".equals(t.getMessage())) {
                return;
            }
            t = t.getCause();
        }
        fail("expected \"foo unavailable\" in throwable chain");
    }

    @Test
    public void testHandleBarBaz() throws Exception {
        Throwable t = assertThrows(Throwable.class, () -> context.getValue("bar/baz"),
            "expected Throwable");

        while (t != null) {
            if ("baz unavailable".equals(t.getMessage())) {
                return;
            }
            t = t.getCause();
        }
        fail("expected \"baz unavailable\" in throwable chain");
    }

    public Bar getBar() {
        return bar;
    }
}
