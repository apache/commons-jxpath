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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * The reference implementation of JXPathContext.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class NamespaceResolver implements Cloneable {
    final protected NamespaceResolver parent;
    protected HashMap namespaceMap = new HashMap();
    protected HashMap reverseMap;
    protected NodePointer pointer;
    private String defaultNamespaceURI;
    private boolean sealed;
        
    public NamespaceResolver(NamespaceResolver parent) {
        this.parent = parent;
    }
    
    /**
     * Registers a namespace prefix.
     * 
     * @param prefix A namespace prefix
     * @param namespaceURI A URI for that prefix
     */
    public void registerNamespace(String prefix, String namespaceURI) {
        namespaceMap.put(prefix, namespaceURI);
        reverseMap = null;
    }
    
    /**
     * Register a namespace for the expression context.
     */
    public void setNamespaceContextPointer(NodePointer pointer) {
        this.pointer = pointer;
    }
    
    public Pointer getNamespaceContextPointer() {
        if (pointer == null && parent != null) {
            return parent.getNamespaceContextPointer();
        }
        return pointer;
    }
    
    /**
     * Given a prefix, returns a registered namespace URI. If the requested
     * prefix was not defined explicitly using the registerNamespace method,
     * JXPathContext will then check the context node to see if the prefix is
     * defined there. See
     * {@link #setNamespaceContextPointer(Pointer) setNamespaceContextPointer}.
     * 
     * @param prefix The namespace prefix to look up
     * @return namespace URI or null if the prefix is undefined.
     */
    public String getNamespaceURI(String prefix) {
        String uri = (String) namespaceMap.get(prefix);
        if (uri == null && pointer != null) {
            uri = pointer.getNamespaceURI(prefix);
        }
        if (uri == null && parent != null) {
            return parent.getNamespaceURI(prefix);
        }
//        System.err.println("For prefix " + prefix + " URI=" + uri);
        return uri;
    }
    
    public String getPrefix(String namespaceURI) {
        if (reverseMap == null) {
            reverseMap = new HashMap();
            NodeIterator ni = pointer.namespaceIterator();
            if (ni != null) {
                for (int position = 1; ni.setPosition(position); position++) {
                    NodePointer nsPointer = ni.getNodePointer();
                    String uri = nsPointer.getNamespaceURI();                    
                    String prefix = nsPointer.getName().getName();
                    if (!"".equals(prefix)) {
                        reverseMap.put(uri, prefix);
                    }
                }
            }
            Iterator it = namespaceMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                reverseMap.put(entry.getValue(), entry.getKey());
            }
        }
        String prefix = (String) reverseMap.get(namespaceURI);
        if (prefix == null && parent != null) {
            return parent.getPrefix(namespaceURI);
        }
        return prefix;
    }
        
    public String getDefaultNamespaceURI() {
        return defaultNamespaceURI;
    }

    public void registerDefaultNamespaceURI(String uri) {
        this.defaultNamespaceURI = uri;
    }
    
    public boolean isSealed() {
        return sealed;
    }
    
    public void seal() {
        sealed = true;
        if (parent != null) {
            parent.seal();
        }
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            // Of course, it's supported.
            e.printStackTrace();
            return null;
        }
    }

}