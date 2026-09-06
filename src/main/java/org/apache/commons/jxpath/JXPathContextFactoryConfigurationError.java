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

package org.apache.commons.jxpath;

/**
 * Thrown when a problem with configuration with the {@link JXPathContextFactory JXPathContextFactories} exists. This error will typically be thrown when the
 * class of a factory specified in the system properties cannot be found or instantiated.
 */
public class JXPathContextFactoryConfigurationError extends Error {

    private static final long serialVersionUID = 2L;

    /**
     * Constructs a new {@code JXPathContextFactoryConfigurationError} with no detail mesage.
     */
    public JXPathContextFactoryConfigurationError() {
    }

    /**
     * Constructs a new {@code JXPathContextFactoryConfigurationError} with a given {@code Exception} base cause of the error.
     *
     * @param cause The exception to be encapsulated in a JXPathContextFactoryConfigurationError.
     */
    public JXPathContextFactoryConfigurationError(final Exception cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code JXPathContextFactoryConfigurationError} with the given {@code Exception} base cause and detail message.
     *
     * @param cause   The exception to be encapsulated in a JXPathContextFactoryConfigurationError
     * @param msg The detail message.
     */
    public JXPathContextFactoryConfigurationError(final Exception cause, final String msg) {
        super(msg, cause);
    }

    /**
     * Constructs a new {@code JXPathContextFactoryConfigurationError} with the {@code String } specified as an error message.
     *
     * @param msg The error message for the exception.
     */
    public JXPathContextFactoryConfigurationError(final String msg) {
        super(msg);
    }

    /**
     * Gets the actual exception (if any) that caused this exception to be raised.
     *
     * @return The encapsulated exception, or null if there is none.
     */
    public Exception getException() {
        return (Exception) super.getCause();
    }

}
