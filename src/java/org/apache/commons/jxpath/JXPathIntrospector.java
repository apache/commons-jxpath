/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/JXPathIntrospector.java,v 1.2 2001/09/11 23:34:26 dmitri Exp $
 * $Revision: 1.2 $
 * $Date: 2001/09/11 23:34:26 $
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
import org.apache.commons.jxpath.MapDynamicPropertyHandler;

/**
 * JXPathIntrospector maintains a registry of {@link JXPathBeanInfo JXPathBeanInfo} objects
 * for Java classes.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.2 $ $Date: 2001/09/11 23:34:26 $
 */
public class JXPathIntrospector {

    private static HashMap byClass = new HashMap();
    static {
        registerAtomicClass(Boolean.TYPE);
        registerAtomicClass(Boolean.class);
        registerAtomicClass(Byte.TYPE);
        registerAtomicClass(Byte.class);
        registerAtomicClass(Character.TYPE);
        registerAtomicClass(Character.class);
        registerAtomicClass(Short.TYPE);
        registerAtomicClass(Short.class);
        registerAtomicClass(Integer.TYPE);
        registerAtomicClass(Integer.class);
        registerAtomicClass(Long.TYPE);
        registerAtomicClass(Long.class);
        registerAtomicClass(Float.TYPE);
        registerAtomicClass(Float.class);
        registerAtomicClass(Double.TYPE);
        registerAtomicClass(Double.class);
        registerAtomicClass(String.class);
        registerAtomicClass(Date.class);
        registerAtomicClass(java.sql.Date.class);
        registerAtomicClass(java.sql.Time.class);
        registerAtomicClass(java.sql.Timestamp.class);

        registerDynamicClass(HashMap.class, MapDynamicPropertyHandler.class);
        registerDynamicClass(Properties.class, MapDynamicPropertyHandler.class);
        registerDynamicClass(WeakHashMap.class, MapDynamicPropertyHandler.class);
    }

    /**
     * Automatically creates and registers a JXPathBeanInfo object
     * for the specified class. That object returns true to isAtomic().
     */
    public static void registerAtomicClass(Class beanClass) {
        byClass.put(beanClass, new JXPathBasicBeanInfo(beanClass, true));
    }

    /**
     * Automatically creates and registers a JXPathBeanInfo object
     * for the specified class. That object returns true to isDynamic().
     */
    public static void registerDynamicClass(Class beanClass, Class dynamicPropertyHandlerClass) {
        byClass.put(beanClass, new JXPathBasicBeanInfo(beanClass, dynamicPropertyHandlerClass));
    }

    /**
     * Creates and registers a JXPathBeanInfo object for the supplied class.
     * If the class has already been registered, returns the registered JXPathBeanInfo
     * object.
     * <p>
     * The process of creation of JXPathBeanInfo is as follows:
     * <ul>
     * <li>If class named <code>&lt;beanClass&gt;XBeanInfo</code> exists,
     *     an instance of that class is allocated.
     * <li>Otherwise, an instance of {@link JXPathBasicBeanInfo JXPathBasicBeanInfo} is
     *     allocated.
     * </ul>
     */
    public static JXPathBeanInfo getBeanInfo(Class beanClass) {
        JXPathBeanInfo beanInfo = (JXPathBeanInfo)byClass.get(beanClass);
        if (beanInfo == null){
            beanInfo = findInformant(beanClass);
            if (beanInfo == null){
                beanInfo = new JXPathBasicBeanInfo(beanClass);
            }
            byClass.put(beanClass, beanInfo);
        }
        return beanInfo;
    }

    private static synchronized JXPathBeanInfo findInformant(Class beanClass) {
        String name = beanClass.getName() + "XBeanInfo";
        try {
            return (JXPathBeanInfo)instantiate(beanClass, name);
        } catch (Exception ex) {
            // Just drop through
        }

        // Now try checking if the bean is its own JXPathBeanInfo.
        try {
            if (JXPathBeanInfo.class.isAssignableFrom(beanClass)) {
                return (JXPathBeanInfo)beanClass.newInstance();
            }
        } catch (Exception ex) {
            // Just drop through
        }

        return null;
    }

    /**
     * Try to create an instance of a named class.
     * First try the classloader of "sibling", then try the system
     * classloader.
     */
    private static Object instantiate(Class sibling, String className)
                 throws Exception {

        // First check with sibling's classloader (if any).
        ClassLoader cl = sibling.getClassLoader();
        if (cl != null) {
            try {
                Class cls = cl.loadClass(className);
                return cls.newInstance();
            } catch (Exception ex) {
                // Just drop through and try the system classloader.
            }
        }

        // Now try the bootstrap classloader.
        Class cls = Class.forName(className);
        return cls.newInstance();
    }
}