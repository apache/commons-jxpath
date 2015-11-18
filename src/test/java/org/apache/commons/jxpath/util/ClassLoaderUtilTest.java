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
package org.apache.commons.jxpath.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;

import junit.framework.TestCase;

/**
 * Tests org.apache.commons.jxpath.util.ClassLoaderUtil.
 * 
 * @author John Trimble
 */
public class ClassLoaderUtilTest extends TestCase {
  
  // These must be string literals, and not populated by calling getName() on
  // the respective classes, since the tests below will load this class in a
  // special class loader which may be unable to load those classes.
  private static final String TEST_CASE_CLASS_NAME = "org.apache.commons.jxpath.util.ClassLoaderUtilTest";
  private static final String EXAMPLE_CLASS_NAME = "org.apache.commons.jxpath.util.ClassLoadingExampleClass";
  
  private ClassLoader orginalContextClassLoader;
  
  /**
   * Setup for the tests.
   */
  public void setUp() {
    this.orginalContextClassLoader = Thread.currentThread().getContextClassLoader();
  }
  
  /**
   * Cleanup for the tests.
   */
  public void tearDown() {
    Thread.currentThread().setContextClassLoader(this.orginalContextClassLoader);
  }
  
  /**
   * Tests that JXPath cannot dynamically load a class, which is not visible to
   * its class loader, when the context class loader is null.
   */
  public void testClassLoadFailWithoutContextClassLoader() {
    Thread.currentThread().setContextClassLoader(null);
    ClassLoader cl = new TestClassLoader(getClass().getClassLoader());
    executeTestMethodUnderClassLoader(cl, "callExampleMessageMethodAndAssertClassNotFoundJXPathException");
  }
  
  /**
   * Tests that JXPath can dynamically load a class, which is not visible to 
   * its class loader, when the context class loader is set and can load the
   * class.
   */
  public void testClassLoadSuccessWithContextClassLoader() {
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    ClassLoader cl = new TestClassLoader(getClass().getClassLoader());
    executeTestMethodUnderClassLoader(cl, "callExampleMessageMethodAndAssertSuccess");
  }
  
  /**
   * Tests that JXPath will use its class loader to dynamically load a 
   * requested class when the context class loader is set but unable to load
   * the class.
   */
  public void testCurrentClassLoaderFallback() {
    ClassLoader cl = new TestClassLoader(getClass().getClassLoader());
    Thread.currentThread().setContextClassLoader(cl);
    callExampleMessageMethodAndAssertSuccess();
  }
  
  /**
   * Tests that JXPath can dynamically load a class, which is visible to
   * its class loader, when there is no context class loader set.
   */
  public void testClassLoadSuccessWithoutContextClassLoader() {
    Thread.currentThread().setContextClassLoader(null);
    callExampleMessageMethodAndAssertSuccess();
  }
  
  /**
   * Performs a basic query that requires a class be loaded dynamically by
   * JXPath and asserts the dynamic class load fails.
   */
  public static void callExampleMessageMethodAndAssertClassNotFoundJXPathException() {
    JXPathContext context = JXPathContext.newContext(new Object());
    try {
      context.selectSingleNode(EXAMPLE_CLASS_NAME+".getMessage()");
      fail("We should not be able to load "+EXAMPLE_CLASS_NAME+".");
    } catch( Exception e ) {
      assertTrue( e instanceof JXPathException );
    }
  }
  
  /**
   * Performs a basic query that requires a class be loaded dynamically by
   * JXPath and asserts the dynamic class load succeeds.
   */
  public static void callExampleMessageMethodAndAssertSuccess() {
    JXPathContext context = JXPathContext.newContext(new Object());
    Object value;
    try {
      value = context.selectSingleNode(EXAMPLE_CLASS_NAME+".getMessage()");
      assertEquals("an example class", value);
    } catch( Exception e ) {
      fail(e.getMessage());
    }
  }
  
  /**
   * Loads this class through the given class loader and then invokes the 
   * indicated no argument static method of the class.
   * 
   * @param cl the class loader under which to invoke the method.
   * @param methodName the name of the static no argument method on this class
   * to invoke.
   */
  private void executeTestMethodUnderClassLoader(ClassLoader cl, String methodName) {
    Class testClass = null;
    try {
      testClass = cl.loadClass(TEST_CASE_CLASS_NAME);
    } catch (ClassNotFoundException e) {
      fail(e.getMessage());
    }
    Method testMethod = null;
    try {
      testMethod = testClass.getMethod(methodName, null);
    } catch (SecurityException e) {
      fail(e.getMessage());
    } catch (NoSuchMethodException e) {
      fail(e.getMessage());
    }
    
    try {
      testMethod.invoke(null, null);
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    } catch (IllegalAccessException e) {
      fail(e.getMessage());
    } catch (InvocationTargetException e) {
      if( e.getCause() instanceof RuntimeException ) {
        // Allow the runtime exception to propagate up.
        throw (RuntimeException) e.getCause();
      }
    }
  }
  
  /**
   * A simple class loader which delegates all class loading to its parent 
   * with two exceptions. First, attempts to load the class 
   * <code>org.apache.commons.jxpath.util.ClassLoaderUtilTest</code> will
   * always result in a ClassNotFoundException. Second, loading the class
   * <code>org.apache.commons.jxpath.util.ClassLoadingExampleClass</code> will 
   * result in the class being loaded by this class loader, regardless of 
   * whether the parent can/has loaded it. 
   *
   */
  private static class TestClassLoader extends ClassLoader {
    private Class testCaseClass = null;
    
    public TestClassLoader(ClassLoader classLoader) {
      super(classLoader);
    }

    public synchronized Class loadClass(String name, boolean resolved) throws ClassNotFoundException {
      if( EXAMPLE_CLASS_NAME.equals(name) ) {
        throw new ClassNotFoundException();
      }
      else if( TEST_CASE_CLASS_NAME.equals(name) ) {
        if( testCaseClass == null ) {
          URL clazzUrl = this.getParent().getResource("org/apache/commons/jxpath/util/ClassLoaderUtilTest.class");
          
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          InputStream in = null;
          try {
            in = clazzUrl.openStream();
            byte[] buffer = new byte[2048];
            for( int read = in.read(buffer); read > -1; read = in.read(buffer) ) {
              out.write(buffer, 0, read);
            }
          } catch( IOException e ) {
            throw new ClassNotFoundException("Could not read class from resource "+clazzUrl+".", e);
          } finally {
            try { in.close(); } catch( Exception e ) { }
            try { out.close(); } catch( Exception e ) { }
          }
          
          byte[] clazzBytes = out.toByteArray();
          this.testCaseClass = this.defineClass(TEST_CASE_CLASS_NAME, clazzBytes, 0, clazzBytes.length);
        }
        return this.testCaseClass;
      }
      return this.getParent().loadClass(name);
    }
  }
}
