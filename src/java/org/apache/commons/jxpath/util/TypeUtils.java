/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/util/TypeUtils.java,v 1.1 2002/04/21 21:52:34 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2002/04/21 21:52:34 $
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
package org.apache.commons.jxpath.util;

import java.util.*;
import java.lang.reflect.*;
import org.apache.commons.jxpath.*;

/**
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2002/04/21 21:52:34 $
 */
public class TypeUtils {

    private static final int NO_MATCH = 0;
    private static final int APPROXIMATE_MATCH = 1;
    private static final int EXACT_MATCH = 2;
    private static final Object[] EMPTY_ARRAY = new Object[0];

    public static Constructor lookupConstructor(Class targetClass, Object[] parameters){
        boolean tryExact = true;
        int count = parameters.length;
        Class types[] = new Class[count];
        for (int i = 0; i < count; i++){
            Object param = parameters[i];
            if (param != null){
                types[i] = param.getClass();
            }
            else {
                types[i] = null;
                tryExact = false;
            }
        }

        Constructor constructor = null;

        if (tryExact){
            // First - without type conversion
            try {
                constructor = targetClass.getConstructor(types);
                if (constructor != null){
                    return constructor;
                }
            }
            catch (NoSuchMethodException ex){
            }
        }

        int currentMatch = 0;
        boolean ambiguous = false;

        // Then - with type conversion
        Constructor[] constructors = targetClass.getConstructors();
        for (int i = 0; i < constructors.length; i++){
            int match = TypeUtils.matchParameterTypes(constructors[i].getParameterTypes(), parameters);
            if (match != TypeUtils.NO_MATCH){
                if (match > currentMatch){
                    constructor = constructors[i];
                    currentMatch = match;
                    ambiguous = false;
                }
                else if (match == currentMatch){
                    ambiguous = true;
                }
            }
        }
        if (ambiguous){
            throw new RuntimeException("Ambigous constructor " + Arrays.asList(parameters));
        }
        return constructor;
    }

    public static Method lookupStaticMethod(Class targetClass, String name, Object[] parameters){
        boolean tryExact = true;
        int count = parameters.length;
        Class types[] = new Class[count];
        for (int i = 0; i < count; i++){
            Object param = parameters[i];
            if (param != null){
                types[i] = param.getClass();
            }
            else {
                types[i] = null;
                tryExact = false;
            }
        }

        Method method = null;

        if (tryExact){
            // First - without type conversion
            try {
                method = targetClass.getMethod(name, types);
                if (method != null && Modifier.isStatic(method.getModifiers())){
                    return method;
                }
            }
            catch (NoSuchMethodException ex){
            }
        }

        int currentMatch = 0;
        boolean ambiguous = false;

        // Then - with type conversion
        Method[] methods = targetClass.getMethods();
        for (int i = 0; i < methods.length; i++){
            if (Modifier.isStatic(methods[i].getModifiers()) &&
                    methods[i].getName().equals(name)){
                int match = TypeUtils.matchParameterTypes(methods[i].getParameterTypes(), parameters);
                if (match != TypeUtils.NO_MATCH){
                    if (match > currentMatch){
                        method = methods[i];
                        currentMatch = match;
                        ambiguous = false;
                    }
                    else if (match == currentMatch){
                        ambiguous = true;
                    }
                }
            }
        }
        if (ambiguous){
            throw new RuntimeException("Ambigous method call: " + name);
        }
        return method;
    }

    public static Method lookupMethod(Class targetClass, String name, Object[] parameters){
        if (parameters.length < 1 || parameters[0] == null){
            return null;
        }

        if (TypeUtils.matchType(targetClass, parameters[0]) == TypeUtils.NO_MATCH){
            return null;
        }

        targetClass = convert(parameters[0], targetClass).getClass();

        boolean tryExact = true;
        int count = parameters.length - 1;
        Class types[] = new Class[count];
        Object arguments[] = new Object[count];
        for (int i = 0; i < count; i++){
            Object param = parameters[i+1];
            arguments[i] = param;
            if (param != null){
                types[i] = param.getClass();
            }
            else {
                types[i] = null;
                tryExact = false;
            }
        }

        Method method = null;

        if (tryExact){
            // First - without type conversion
            try {
                method = targetClass.getMethod(name, types);
                if (method != null && !Modifier.isStatic(method.getModifiers())){
                    return method;
                }
            }
            catch (NoSuchMethodException ex){
            }
        }

        int currentMatch = 0;
        boolean ambiguous = false;

        // Then - with type conversion
        Method[] methods = targetClass.getMethods();
        for (int i = 0; i < methods.length; i++){
            if (!Modifier.isStatic(methods[i].getModifiers()) &&
                    methods[i].getName().equals(name)){
                int match = TypeUtils.matchParameterTypes(methods[i].getParameterTypes(), arguments);
                if (match != TypeUtils.NO_MATCH){
                    if (match > currentMatch){
                        method = methods[i];
                        currentMatch = match;
                        ambiguous = false;
                    }
                    else if (match == currentMatch){
                        ambiguous = true;
                    }
                }
            }
        }
        if (ambiguous){
            throw new RuntimeException("Ambigous method call: " + name);
        }
        return method;
    }

    public static int matchParameterTypes(Class types[], Object parameters[]){
        int pi = 0;
        if (types.length >= 1 && ExpressionContext.class.isAssignableFrom(types[0])){
            pi++;
        }
        if (types.length != parameters.length + pi){
            return NO_MATCH;
        }
        int totalMatch = EXACT_MATCH;
        for (int i = 0; i < parameters.length; i++){
            int match = matchType(types[i + pi], parameters[i]);
            if (match == NO_MATCH){
                return NO_MATCH;
            }
            if (match < totalMatch){
                totalMatch = match;
            }
        }
        return totalMatch;
    }

    public static int matchType(Class expected, Object object){
        if (object == null){
            return APPROXIMATE_MATCH;
        }

        Class actual = object.getClass();

        if (expected.equals(actual)){
            return EXACT_MATCH;
        }
        if (expected.isAssignableFrom(actual)){
            return EXACT_MATCH;
        }

        if (canConvert(object, expected)){
            return APPROXIMATE_MATCH;
        }

        return NO_MATCH;
    }

    public static boolean canConvert(Object object, Class toType){
        if (object == null){
            return true;
        }

        if (toType == Object.class){
            return true;
        }

        Class fromType = object.getClass();
        if (fromType.equals(toType)){
            return true;
        }

        if (toType.isAssignableFrom(fromType)){
            return true;
        }

        if (toType == String.class){
            return true;
        }

        if (object instanceof Boolean){
            if (toType == boolean.class ||
                    Number.class.isAssignableFrom(toType)){
                return true;
            }
        }
        else if (object instanceof Number){
            if (toType.isPrimitive() ||
                    Number.class.isAssignableFrom(toType)){
                return true;
            }
        }
        else if (object instanceof Character){
            if (toType == char.class){
                return true;
            }
        }
        else if (object instanceof String){
            if (toType.isPrimitive()){
                return true;
            }
        }
        else if (object instanceof ExpressionContext){
            if (Collection.class.isAssignableFrom(toType)){
                return true;
            }
            Pointer pointer = ((ExpressionContext)object).getContextNodePointer();
            if (pointer != null){
                Object value = pointer.getValue();
                return canConvert(value, toType);
            }
        }
        else if (fromType.isArray()){
            if (Array.getLength(object) == 1){
                Object value = Array.get(object, 0);
                return canConvert(value, toType);
            }
        }
        else if (object instanceof List){
            if (((List)object).size() == 1){
                Object value = ((List)object).get(0);
                return canConvert(value, toType);
            }
        }
        else if (object instanceof Collection){
            if (!((Collection)object).isEmpty()){
                Iterator it = ((Collection)object).iterator();
                Object value = it.next();
                return canConvert(value, toType);
            }
        }

        // TBD: date conversion to/from string
        return false;
    }

    public static Object convert(Object object, Class toType){
        if (object == null){
            return null;
        }

        if (toType == Object.class){
            return object;
        }

        if (object instanceof ExpressionContext){
            if (Collection.class.isAssignableFrom(toType)){
                List list = ((ExpressionContext)object).getContextNodeList();
                Collection result = new ArrayList();
                if (toType == List.class || toType == ArrayList.class){
                    result = new ArrayList();
                }
                else if (toType == Vector.class){
                    result = new Vector();
                }
                else if (toType == Set.class || toType == HashSet.class){
                    result = new HashSet();
                }
                int count = list.size();
                for (int i = 0; i < count; i++){
                    Pointer ptr = (Pointer)list.get(i);
                    result.add(ptr.getValue());
                }
                return result;
            }
            else {
                Object value = ((ExpressionContext)object).getContextNodePointer().getValue();
                return convert(value, toType);
            }
        }

        Class fromType = object.getClass();
        if (fromType.equals(toType) || toType.isAssignableFrom(fromType)){
            return object;
        }

        if (toType == String.class){
            return object.toString();
        }

        if (object instanceof Boolean){
            if (toType == boolean.class){
                return object;
            }
            boolean value = ((Boolean)object).booleanValue();
            return allocateNumber(toType, value ? 1 : 0);
        }
        else if (object instanceof Number){
            double value = ((Number)object).doubleValue();
            if (toType == boolean.class || toType == Boolean.class){
                return value == 0.0 ? Boolean.FALSE : Boolean.TRUE;
            }
            if (toType.isPrimitive() ||
                    Number.class.isAssignableFrom(toType)){
                return allocateNumber(toType, value);
            }
        }
        else if (object instanceof Character){
            if (toType == char.class){
                return object;
            }
        }
        else if (object instanceof String){
            if (toType == boolean.class || toType == Boolean.class){
                return new Boolean((String)object);
            }
            if (toType == char.class || toType == Character.class){
                return new Character(((String)object).charAt(0));
            }
            if (toType == byte.class || toType == Byte.class){
                return new Byte((String)object);
            }
            if (toType == short.class || toType == Short.class){
                return new Short((String)object);
            }
            if (toType == int.class || toType == Integer.class){
                return new Integer((String)object);
            }
            if (toType == long.class || toType == Long.class){
                return new Long((String)object);
            }
            if (toType == float.class || toType == Float.class){
                return new Float((String)object);
            }
            if (toType == double.class || toType == Double.class){
                return new Double((String)object);
            }
        }
        else if (fromType.isArray()){
            Object value = Array.get(object, 0);
            return convert(value, toType);
        }
        else if (object instanceof List){
            Object value = ((List)object).get(0);
            return convert(value, toType);
        }
        else if (object instanceof Collection){
            Iterator it = ((Collection)object).iterator();
            Object value = it.next();
            return convert(value, toType);
        }
        return object;
    }

    private static Number allocateNumber(Class type, double value){
        if (type == Byte.class || type == byte.class){
            return new Byte((byte)value);
        }
        if (type == Short.class || type == short.class){
            return new Short((short)value);
        }
        if (type == Integer.class || type == int.class){
            return new Integer((int)value);
        }
        if (type == Long.class || type == long.class){
            return new Long((long)value);
        }
        if (type == Float.class || type == float.class){
            return new Float((float)value);
        }
        if (type == Double.class || type == double.class){
            return new Double(value);
        }
        return null;
    }
}