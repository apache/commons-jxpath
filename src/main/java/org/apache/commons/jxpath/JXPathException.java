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

package org.apache.commons.jxpath;

/**
 * Thrown in various situations by JXPath; may contain a nested exception.
 */
public class JXPathException extends RuntimeException {

    private static final long serialVersionUID = 2L;

    /**
     * Create a new {@code JXPathException} with no detail mesage.
     */
    public JXPathException() {
    }

    /**
     * Create a new {@code JXPathException} with the {@code String } specified as an error message.
     *
     * @param msg The error message for the exception.
     */
    public JXPathException(final String msg) {
        super(msg);
    }

    /**
     * Create a new {@code JXPathException} with the given {@code Exception} base cause and detail message.
     *
     * @param msg The detail message.
     * @param cause   The exception to be encapsulated in a JXPathException
     */
    public JXPathException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    /**
     * Create a new {@code JXPathException} with a given {@code Throwable} base cause of the error.
     *
     * @param cause The exception to be encapsulated in a JXPathException.
     */
    public JXPathException(final Throwable cause) {
        super(cause.toString());
    }

    /**
     * Gets the actual exception (if any) that caused this exception to be raised.
     *
     * @return The encapsulated exception, or null if there is none.
     */
    public Throwable getException() {
        return super.getCause();
    }

}
