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

package org.apache.commons.jxpath.xml;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

/**
 * Tests that the static parser registries in {@link DocumentContainer} tolerate concurrent access.
 */
class DocumentContainerConcurrencyTest {

    private static final int THREAD_COUNT = 12;
    private static final int ITERATIONS = 400;

    @Test
    void testConcurrentRegisterAndGetValue() throws InterruptedException {
        final URL url = DocumentContainer.class.getResource("/org/apache/commons/jxpath/Vendor.xml");
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final Thread[] threads = new Thread[THREAD_COUNT];
        for (int t = 0; t < THREAD_COUNT; t++) {
            final int tid = t;
            threads[t] = new Thread(() -> {
                for (int i = 0; i < ITERATIONS && failure.get() == null; i++) {
                    final String model = "model-" + tid + "-" + i;
                    try {
                        if ((tid & 1) == 0) {
                            DocumentContainer.registerXMLParser(model, "org.apache.commons.jxpath.xml.DOMParser");
                            new DocumentContainer(url, model).getValue();
                        } else {
                            DocumentContainer.registerXMLParser(model, new DOMParser());
                        }
                    } catch (final Throwable th) {
                        failure.compareAndSet(null, th);
                    }
                }
            });
        }
        for (final Thread thread : threads) {
            thread.start();
        }
        for (final Thread thread : threads) {
            thread.join();
        }
        assertNull(failure.get());
    }
}
