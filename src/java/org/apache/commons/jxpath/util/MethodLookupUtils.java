/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/util/MethodLookupUtils.java,v 1.5 2003/10/09 21:31:42 rdonkin Exp $
 * $Revision: 1.5 $
 * $Date: 2003/10/09 21:31:42 $
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
package org.apache.commons.jxpath.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.JXPathException;

/**
 * Method lookup utilities, which find static and non-static methods as well
 * as constructors based on a name and list of parameters.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.5 $ $Date: 2003/10/09 21:31:42 $
 */
public class MethodLookupUtils {

    private static final int NO_MATCH = 0;
    private static final int APPROXIMATE_MATCH = 1;
    private static final int EXACT_MATCH = 2;
    private static final Object[] EMPTY_ARRAY = new Object[0];

     public static Constructor lookupConstructor(
        Class targetClass,
        Object[] parameters) 
     {
        boolean tryExact = true;
        int count = parameters.length;
        Class types[] = new Class[count];
        for (int i = 0; i < count; i++) {
            Object param = parameters[i];
            if (param != null) {
                types[i] = param.getClass();
            }
            else {
                types[i] = null;
                tryExact = false;
            }
        }

        Constructor constructor = null;

        if (tryExact) {
            // First - without type conversion
            try {
                constructor = targetClass.getConstructor(types);
                if (constructor != null) {
                    return constructor;
                }
            }
            catch (NoSuchMethodException ex) {
                // Ignore
            }
        }

        int currentMatch = 0;
        boolean ambiguous = false;

        // Then - with type conversion
        Constructor[] constructors = targetClass.getConstructors();
        for (int i = 0; i < constructors.length; i++) {
            int match =
                matchParameterTypes(
                    constructors[i].getParameterTypes(),
                    parameters);
            if (match != NO_MATCH) {
                if (match > currentMatch) {
                    constructor = constructors[i];
                    currentMatch = match;
                    ambiguous = false;
                }
                else if (match == currentMatch) {
                    ambiguous = true;
                }
            }
        }
        if (ambiguous) {
            throw new JXPathException(
                "Ambigous constructor " + Arrays.asList(parameters));
        }
        return constructor;
    }

    public static Method lookupStaticMethod(
        Class targetClass,
        String name,
        Object[] parameters) 
    {
        boolean tryExact = true;
        int count = parameters.length;
        Class types[] = new Class[count];
        for (int i = 0; i < count; i++) {
            Object param = parameters[i];
            if (param != null) {
                types[i] = param.getClass();
            }
            else {
                types[i] = null;
                tryExact = false;
            }
        }

        Method method = null;

        if (tryExact) {
            // First - without type conversion
            try {
                method = targetClass.getMethod(name, types);
                if (method != null
                    && Modifier.isStatic(method.getModifiers())) {
                    return method;
                }
            }
            catch (NoSuchMethodException ex) {
                // Ignore
            }
        }

        int currentMatch = 0;
        boolean ambiguous = false;

        // Then - with type conversion
        Method[] methods = targetClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (Modifier.isStatic(methods[i].getModifiers())
                && methods[i].getName().equals(name)) {
                int match =
                    matchParameterTypes(
                        methods[i].getParameterTypes(),
                        parameters);
                if (match != NO_MATCH) {
                    if (match > currentMatch) {
                        method = methods[i];
                        currentMatch = match;
                        ambiguous = false;
                    }
                    else if (match == currentMatch) {
                        ambiguous = true;
                    }
                }
            }
        }
        if (ambiguous) {
            throw new JXPathException("Ambigous method call: " + name);
        }
        return method;
    }

    public static Method lookupMethod(
        Class targetClass,
        String name,
        Object[] parameters) 
    {
        if (parameters.length < 1 || parameters[0] == null) {
            return null;
        }

        if (matchType(targetClass, parameters[0]) == NO_MATCH) {
            return null;
        }

        targetClass = TypeUtils.convert(parameters[0], targetClass).getClass();

        boolean tryExact = true;
        int count = parameters.length - 1;
        Class types[] = new Class[count];
        Object arguments[] = new Object[count];
        for (int i = 0; i < count; i++) {
            Object param = parameters[i + 1];
            arguments[i] = param;
            if (param != null) {
                types[i] = param.getClass();
            }
            else {
                types[i] = null;
                tryExact = false;
            }
        }

        Method method = null;

        if (tryExact) {
            // First - without type conversion
            try {
                method = targetClass.getMethod(name, types);
                if (method != null
                    && !Modifier.isStatic(method.getModifiers())) {
                    return method;
                }
            }
            catch (NoSuchMethodException ex) {
                // Ignore
            }
        }

        int currentMatch = 0;
        boolean ambiguous = false;

        // Then - with type conversion
        Method[] methods = targetClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (!Modifier.isStatic(methods[i].getModifiers())
                && methods[i].getName().equals(name)) {
                int match =
                    matchParameterTypes(
                        methods[i].getParameterTypes(),
                        arguments);
                if (match != NO_MATCH) {
                    if (match > currentMatch) {
                        method = methods[i];
                        currentMatch = match;
                        ambiguous = false;
                    }
                    else if (match == currentMatch) {
                        ambiguous = true;
                    }
                }
            }
        }
        if (ambiguous) {
            throw new JXPathException("Ambigous method call: " + name);
        }
        return method;
    }

    private static int matchParameterTypes(
        Class types[],
        Object parameters[]) 
    {
        int pi = 0;
        if (types.length >= 1
            && ExpressionContext.class.isAssignableFrom(types[0])) {
            pi++;
        }
        if (types.length != parameters.length + pi) {
            return NO_MATCH;
        }
        int totalMatch = EXACT_MATCH;
        for (int i = 0; i < parameters.length; i++) {
            int match = matchType(types[i + pi], parameters[i]);
            if (match == NO_MATCH) {
                return NO_MATCH;
            }
            if (match < totalMatch) {
                totalMatch = match;
            }
        }
        return totalMatch;
    }

    private static int matchType(Class expected, Object object) {
        if (object == null) {
            return APPROXIMATE_MATCH;
        }

        Class actual = object.getClass();

        if (expected.equals(actual)) {
            return EXACT_MATCH;
        }
        if (expected.isAssignableFrom(actual)) {
            return EXACT_MATCH;
        }

        if (TypeUtils.canConvert(object, expected)) {
            return APPROXIMATE_MATCH;
        }

        return NO_MATCH;
    }
}