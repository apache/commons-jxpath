/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/FunctionLibrary.java,v 1.1 2001/08/23 00:46:58 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2001/08/23 00:46:58 $
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Plotnix, Inc,
 * <http://www.plotnix.com/>.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.jxpath;

import java.util.*;

/**
 * An object that aggregates Functions objects into a group Functions object.
 * Since JXPathContext can only register a single Functions object,
 * FunctionLibrary should always be used to group all Functions objects
 * that need to be registered.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:46:58 $
 */
public class FunctionLibrary implements Functions {
    private List allFunctions = new ArrayList();
    private HashMap byNamespace = null;

    /**
     * Add functions to the library
     */
    public void addFunctions(Functions functions){
        allFunctions.add(functions);
        byNamespace = null;
    }

    /**
     * Remove functions from the library.
     */
    public void removeFunctions(Functions functions){
        allFunctions.remove(functions);
        byNamespace = null;
    }

    /**
     * Returns a set containing all namespaces used by the aggregated
     * Functions.
     */
    public Set getUsedNamespaces(){
        if (byNamespace == null){
            prepareCache();
        }
        return byNamespace.keySet();
    }

    /**
     * Returns a Function, if any, for the specified namespace,
     * name and parameter types.
     */
    public Function getFunction(String namespace, String name, Object[] parameters){
        if (byNamespace == null){
            prepareCache();
        }
        Object candidates = byNamespace.get(namespace);
        if (candidates instanceof Functions){
            return ((Functions)candidates).getFunction(namespace, name, parameters);
        }
        else if (candidates instanceof List){
            List list = (List)candidates;
            int count = list.size();
            for (int i = 0; i < count; i++){
                Function function = ((Functions)list.get(i)).getFunction(namespace, name, parameters);
                if (function != null){
                    return function;
                }
            }
        }
        return null;
    }

    private void prepareCache(){
        byNamespace = new HashMap();
        int count = allFunctions.size();
        for (int i = 0; i < count; i++){
            Functions funcs = (Functions)allFunctions.get(i);
            Set namespaces = funcs.getUsedNamespaces();
            for (Iterator it = namespaces.iterator(); it.hasNext();){
                String ns = (String)it.next();
                Object candidates = byNamespace.get(ns);
                if (candidates == null){
                    byNamespace.put(ns, funcs);
                }
                else if (candidates instanceof Functions){
                    List lst = new ArrayList();
                    lst.add(candidates);
                    lst.add(funcs);
                    byNamespace.put(ns, lst);
                }
                else {
                    ((List)candidates).add(funcs);
                }
            }
        }
    }
}