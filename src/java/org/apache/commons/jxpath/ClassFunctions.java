/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ClassFunctions.java,v 1.1 2001/08/23 00:46:58 dmitri Exp $
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
import java.lang.reflect.*;
import org.apache.commons.jxpath.functions.*;

/**
 * Extension functions provided by a Java class.
 *
 * Let's say, we declared a ClassFunction like this:
 * <blockquote><pre>
 *     new ClassFunctions(Integer.class, "int")
 * </pre></blockquote>
 *
 * We can now use XPaths like:
 * <dl>
 *  <dt><code>"int:new(3)"</code></dt>
 *  <dd>Equivalent to <code>new Integer(3)</code></dd>
 *  <dt><code>"int:getInteger('foo')"</code></dt>
 *  <dd>Equivalent to <code>Integer.getInteger("foo")</code></dd>
 *  <dt><code>"int:floatValue(int:new(4))"</code></dt>
 *  <dd>Equivalent to <code>new Integer(4).floatValue()</code></dd>
 * </dl>
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:46:58 $
 */
public class ClassFunctions implements Functions {
    private Class functionClass;
    private String namespace;
    private static final Object[] EMPTY_ARRAY = new Object[0];

    public ClassFunctions(Class functionClass, String namespace){
        this.functionClass = functionClass;
        this.namespace = namespace;
    }

    public Set getUsedNamespaces(){
        return Collections.singleton(namespace);
    }

    /**
     * Returns a Function, if any, for the specified namespace,
     * name and parameter types.
     */
    public Function getFunction(String namespace, String name, Object[] parameters){
        if (!namespace.equals(this.namespace)){
            return null;
        }

        if (parameters == null){
            parameters = EMPTY_ARRAY;
        }

        if (name.equals("new")){
            Constructor constructor = Types.lookupConstructor(functionClass, parameters);
            if (constructor != null){
                return new ConstructorFunction(constructor);
            }
        }
        else {
            Method method = Types.lookupStaticMethod(functionClass, name, parameters);
            if (method != null){
                return new MethodFunction(method);
            }

            method = Types.lookupMethod(functionClass, name, parameters);
            if (method != null){
                return new MethodFunction(method);
            }
        }

        return null;
    }
}