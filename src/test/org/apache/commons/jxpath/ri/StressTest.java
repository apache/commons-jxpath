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

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;

/**
 * Test thread safety.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */

public class StressTest extends TestCase {
    
    private static final int THREAD_COUNT = 50;
    private static final int THREAD_DURATION = 1000;
    private static JXPathContext context;
    private static int count;
    private static Throwable exception;
        
    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public StressTest(String name) {
        super(name);
    }

    public void testThreads() throws Throwable {
        context = JXPathContext.newContext(null, new Double(100));
        Thread[] threadArray = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadArray[i] = new Thread(new StressRunnable());
        }
        
        for (int i = 0; i < threadArray.length; i++) {
            threadArray[i].start();
        }

        for (int i = 0; i < threadArray.length; i++) {
            try {
                threadArray[i].join();
            }
            catch (InterruptedException e) {
                assertTrue("Interrupted", false);
            }
        }

        if (exception != null) {
            throw exception;
        }
        assertEquals("Test count", THREAD_COUNT * THREAD_DURATION, count);
    }    

    private final class StressRunnable implements Runnable {
        public void run() {
            for (int j = 0; j < THREAD_DURATION && exception == null; j++) {
                try { 
                    double random = 1 + Math.random();
                    double sum =
                        ((Double) context.getValue("/ + " + random))
                            .doubleValue();
                    assertEquals(100 + random, sum, 0.0001);
                    synchronized (context) {
                        count++;
                    }
                }                    
                catch (Throwable t) {
                    exception = t;
                }
            }
        }
    }
}