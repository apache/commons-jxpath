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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.jxpath.functions.ConstructorFunction;
import org.apache.commons.jxpath.functions.MethodFunction;
import org.apache.commons.jxpath.ri.JXPathFilter;
import org.apache.commons.jxpath.ri.SystemPropertyJXPathFilter;
import org.apache.commons.jxpath.util.ClassLoaderUtil;
import org.apache.commons.jxpath.util.MethodLookupUtils;
import org.apache.commons.jxpath.util.TypeUtils;

/**
 * Extension functions provided by Java classes.  The class prefix specified
 * in the constructor is used when a constructor or a static method is called.
 * Usually, a class prefix is a package name (hence the name of this class).
 *
 * Let's say, we declared a PackageFunction like this:
 * <blockquote><pre>
 *     new PackageFunctions("java.util.", "util")
 * </pre></blockquote>
 *
 * We can now use XPaths like:
 * <dl>
 *  <dt><code>"util:Date.new()"</code></dt>
 *  <dd>Equivalent to <code>new java.util.Date()</code></dd>
 *  <dt><code>"util:Collections.singleton('foo')"</code></dt>
 *  <dd>Equivalent to <code>java.util.Collections.singleton("foo")</code></dd>
 *  <dt><code>"util:substring('foo', 1, 2)"</code></dt>
 *  <dd>Equivalent to <code>"foo".substring(1, 2)</code>.  Note that in
 *  this case, the class prefix is not used. JXPath does not check that
 *  the first parameter of the function (the method target) is in fact
 *  a member of the package described by this PackageFunctions object.</dd>
 * </dl>
 *
 * <p>
 * If the first argument of a method or constructor is {@link ExpressionContext},
 * the expression context in which the function is evaluated is passed to
 * the method.
 * </p>
 * <p>
 * There is one PackageFunctions object registered by default with each
 * JXPathContext.  It does not have a namespace and uses no class prefix.
 * The existence of this object allows us to use XPaths like:
 * <code>"java.util.Date.new()"</code> and <code>"length('foo')"</code>
 * without the explicit registration of any extension functions.
 * </p>
 */
public class PackageFunctions implements Functions {
    private final String classPrefix;
    private final String namespace;
    private static final Object[] EMPTY_ARRAY = {};

    /**
     * Create a new PackageFunctions.
     * @param classPrefix class prefix
     * @param namespace namespace String
     */
    public PackageFunctions(final String classPrefix, final String namespace) {
        this.classPrefix = classPrefix;
        this.namespace = namespace;
    }

    /**
     * Returns the namespace specified in the constructor
     * @return (singleton) namespace Set
     */
    @Override
    public Set getUsedNamespaces() {
        return Collections.singleton(namespace);
    }

    /**
     * Returns a {@link Function}, if found, for the specified namespace,
     * name and parameter types.
     *
     * @param  namespace - if it is not the same as specified in the
     * construction, this method returns null
     * @param name - name of the method, which can one these forms:
     * <ul>
     * <li><b>methodname</b>, if invoking a method on an object passed as the
     * first parameter</li>
     * <li><b>Classname.new</b>, if looking for a constructor</li>
     * <li><b>subpackage.subpackage.Classname.new</b>, if looking for a
     * constructor in a subpackage</li>
     * <li><b>Classname.methodname</b>, if looking for a static method</li>
     * <li><b>subpackage.subpackage.Classname.methodname</b>, if looking for a
     * static method of a class in a subpackage</li>
     * </ul>
     * @param parameters Object[] of parameters
     * @return a MethodFunction, a ConstructorFunction or null if no function
     * is found
     */
    @Override
    public Function getFunction(
        final String namespace,
        final String name,
        Object[] parameters) {
        return getFunction(namespace, name, parameters, new SystemPropertyJXPathFilter());
    }

    @Override
    public Function getFunction(final String namespace, final String name, Object[] parameters, final JXPathFilter filter) {
        if (filter == null) {
            throw new JXPathException("No extension function is allowed");
        }

        if (!Objects.equals(this.namespace, namespace)) {
            return null;
        }

        if (parameters == null) {
            parameters = EMPTY_ARRAY;
        }

        String functionName = namespace != null ? namespace + ":" + name : name;
        if (parameters.length >= 1) {
            Object target = TypeUtils.convert(parameters[0], Object.class);
            if (target != null) {
                Method method =
                    MethodLookupUtils.lookupMethod(
                        target.getClass(),
                        name,
                        parameters);
                if (method != null) {
                    return new MethodFunction(method, filter);
                }

                if (target instanceof NodeSet) {
                    target = ((NodeSet) target).getPointers();
                }

                method =
                    MethodLookupUtils.lookupMethod(
                        target.getClass(),
                        name,
                        parameters);
                if (method != null) {
                    return new MethodFunction(method, filter);
                }

                if (target instanceof Collection) {
                    final Iterator iter = ((Collection) target).iterator();
                    if (iter.hasNext()) {
                        target = iter.next();
                        if (target instanceof Pointer) {
                            target = ((Pointer) target).getValue();
                        }
                    }
                    else {
                        target = null;
                    }
                }
            }
            if (target != null) {
                final Method method =
                    MethodLookupUtils.lookupMethod(
                        target.getClass(),
                        name,
                        parameters);
                if (method != null) {
                    return new MethodFunction(method, filter);
                }
            }
        }

        final String fullName = classPrefix + name;
        final int inx = fullName.lastIndexOf('.');
        if (inx == -1) {
            return null;
        }

        final String className = fullName.substring(0, inx);
        final String methodName = fullName.substring(inx + 1);

        Class functionClass;
        try {
            functionClass = ClassLoaderUtil.getClass(className, true, filter);
        }
        catch (final ClassNotFoundException ex) {
            throw new JXPathException(
                "Cannot invoke extension function " + functionName,
                ex);
        }

        if (methodName.equals("new")) {
            final Constructor constructor =
                MethodLookupUtils.lookupConstructor(functionClass, parameters);
            if (constructor != null) {
                return new ConstructorFunction(constructor);
            }
        }
        else {
            final Method method =
                MethodLookupUtils.lookupStaticMethod(
                    functionClass,
                    methodName,
                    parameters);
            if (method != null) {
                return new MethodFunction(method, filter);
            }
        }
        return null;
    }
}
