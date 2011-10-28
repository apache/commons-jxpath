/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Port of class loading methods from <code>org.apache.commons.lang.ClassUtils</code> from 
 * the Apache Commons Lang Component. Some adjustments made to remove dependency on 
 * <code>org.apache.commons.lang.StringUtils</code>. Also modified to fall back on the
 * current class loader when an attempt to load a class with the context class loader
 * results in a <code>java.lang.ClassNotFoundException</code>. 
 * 
 * @see org.apache.commons.lang.ClassUtils
 * 
 * @author Stephen Colebourne
 * @author Gary Gregory
 * @author Norm Deane
 * @author Alban Peignier
 * @author Tomasz Blachowicz
 * @author John Trimble
 */
public class ClassLoaderUtil {
  /**
   * Maps a primitive class name to its corresponding abbreviation used in array class names.
   */
  private static Map abbreviationMap = new HashMap();
  
  /**
   * Add primitive type abbreviation to maps of abbreviations.
   * 
   * @param primitive Canonical name of primitive type
   * @param abbreviation Corresponding abbreviation of primitive type
   */
  private static void addAbbreviation(String primitive, String abbreviation) {
      abbreviationMap.put(primitive, abbreviation);
  }
  
  /**
   * Feed abbreviation maps
   */
  static {
      addAbbreviation("int", "I");
      addAbbreviation("boolean", "Z");
      addAbbreviation("float", "F");
      addAbbreviation("long", "J");
      addAbbreviation("short", "S");
      addAbbreviation("byte", "B");
      addAbbreviation("double", "D");
      addAbbreviation("char", "C");
  }
  
  // Class loading
  // ----------------------------------------------------------------------
  /**
   * Returns the class represented by <code>className</code> using the
   * <code>classLoader</code>.  This implementation supports names like
   * "<code>java.lang.String[]</code>" as well as "<code>[Ljava.lang.String;</code>".
   *
   * @param classLoader  the class loader to use to load the class
   * @param className  the class name
   * @param initialize  whether the class must be initialized
   * @return the class represented by <code>className</code> using the <code>classLoader</code>
   * @throws ClassNotFoundException if the class is not found
   */
  public static Class getClass(
          ClassLoader classLoader, String className, boolean initialize) throws ClassNotFoundException {
      Class clazz;
      if (abbreviationMap.containsKey(className)) {
          String clsName = "[" + abbreviationMap.get(className);
          clazz = Class.forName(clsName, initialize, classLoader).getComponentType();
      } else {
          clazz = Class.forName(toCanonicalName(className), initialize, classLoader);
      }
      return clazz;
  }

  /**
   * Returns the (initialized) class represented by <code>className</code>
   * using the <code>classLoader</code>.  This implementation supports names
   * like "<code>java.lang.String[]</code>" as well as
   * "<code>[Ljava.lang.String;</code>".
   *
   * @param classLoader  the class loader to use to load the class
   * @param className  the class name
   * @return the class represented by <code>className</code> using the <code>classLoader</code>
   * @throws ClassNotFoundException if the class is not found
   */
  public static Class getClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
      return getClass(classLoader, className, true);
  }

  /**
   * Returns the (initialized) class represented by <code>className</code>
   * using the current thread's context class loader. This implementation
   * supports names like "<code>java.lang.String[]</code>" as well as
   * "<code>[Ljava.lang.String;</code>".
   *
   * @param className  the class name
   * @return the class represented by <code>className</code> using the current thread's context class loader
   * @throws ClassNotFoundException if the class is not found
   */
  public static Class getClass(String className) throws ClassNotFoundException {
      return getClass(className, true);
  }

  /**
   * Returns the class represented by <code>className</code> using the
   * current thread's context class loader. This implementation supports
   * names like "<code>java.lang.String[]</code>" as well as
   * "<code>[Ljava.lang.String;</code>".
   *
   * @param className  the class name
   * @param initialize  whether the class must be initialized
   * @return the class represented by <code>className</code> using the current thread's context class loader
   * @throws ClassNotFoundException if the class is not found
   */
  public static Class getClass(String className, boolean initialize) throws ClassNotFoundException {
      ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
      ClassLoader currentCL = ClassLoaderUtil.class.getClassLoader();
      if( contextCL != null ) {
          try {
              return getClass(contextCL, className, initialize);
          } catch( ClassNotFoundException e ) {
              // ignore this exception and try the current class loader.
          }
      }
      return getClass(currentCL, className, initialize);
  }
  
  /**
   * Converts a class name to a JLS style class name.
   *
   * @param className  the class name
   * @return the converted name
   */
  private static String toCanonicalName(String className) {
      if (className == null) {
          throw new RuntimeException("Argument className was null.");
      } else if (className.endsWith("[]")) {
          StringBuffer classNameBuffer = new StringBuffer();
          while (className.endsWith("[]")) {
              className = className.substring(0, className.length() - 2);
              classNameBuffer.append("[");
          }
          String abbreviation = (String) abbreviationMap.get(className);
          if (abbreviation != null) {
              classNameBuffer.append(abbreviation);
          } else {
              classNameBuffer.append("L").append(className).append(";");
          }
          className = classNameBuffer.toString();
      }
      return className;
  }
}
