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
package org.apache.commons.jxpath.xml;

import java.io.InputStream;

/**
 * The abstract superclass of XML parsers that produce DOM Documents.
 * The features have the same defaults as DocumentBuilderFactory.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public abstract class XMLParser2 implements XMLParser 
{
    private boolean validating = false;
    private boolean namespaceAware = true;
    private boolean whitespace = false;
    private boolean expandEntityRef = true;
    private boolean ignoreComments = false;
    private boolean coalescing = false;
    
    /**
     * @see DocumentBuilderFactory#setValidating(boolean)
     */
    public void setValidating(boolean validating) {
        this.validating = validating;
    }
    
    /**
     * @see DocumentBuilderFactory#isValidating()
     */
    public boolean isValidating() {
        return validating;
    }
    
    /**
     * @see DocumentBuilderFactory#isNamespaceAware()
     */
    public boolean isNamespaceAware() {
        return namespaceAware;
    }
    
    /**
     * @see DocumentBuilderFactory#setNamespaceAware(boolean)
     */
    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }
    
    /**
     * @see DocumentBuilderFactory#setIgnoringElementContentWhitespace(boolean)
     */
    public void setIgnoringElementContentWhitespace(boolean whitespace) {
        this.whitespace = whitespace;
    }
    
    /**
     * @see DocumentBuilderFactory#isIgnoringElementContentWhitespace()
     */
    public boolean isIgnoringElementContentWhitespace() {
        return whitespace;
    }
    
    /**
     * @see DocumentBuilderFactory#isExpandEntityReferences()
     */
    public boolean isExpandEntityReferences() {
        return expandEntityRef;
    }
    
    /**
     * @see DocumentBuilderFactory#setExpandEntityReferences(boolean)
     */
    public void setExpandEntityReferences(boolean expandEntityRef) {
        this.expandEntityRef = expandEntityRef;
    }
    
    /**
     * @see DocumentBuilderFactory#isIgnoringComments()
     */
    public boolean isIgnoringComments() {
        return ignoreComments;
    }
    
    /**
     * @see DocumentBuilderFactory#setIgnoringComments(boolean)
     */
    public void setIgnoringComments(boolean ignoreComments) {
        this.ignoreComments = ignoreComments;
    }
    
    /**
     * @see DocumentBuilderFactory#isCoalescing()
     */
    public boolean isCoalescing() {
        return coalescing;
    }
    
    /**
     * @see DocumentBuilderFactory#setCoalescing(boolean)
     */
    public void setCoalescing(boolean coalescing) {
        this.coalescing = coalescing;
    }
    
    public abstract Object parseXML(InputStream stream);
}