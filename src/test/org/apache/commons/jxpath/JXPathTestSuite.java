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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.jxpath.ri.JXPathCompiledExpressionTest;
import org.apache.commons.jxpath.ri.StressTest;
import org.apache.commons.jxpath.ri.axes.RecursiveAxesTest;
import org.apache.commons.jxpath.ri.axes.SimplePathInterpreterTest;
import org.apache.commons.jxpath.ri.compiler.ContextDependencyTest;
import org.apache.commons.jxpath.ri.compiler.CoreFunctionTest;
import org.apache.commons.jxpath.ri.compiler.CoreOperationTest;
import org.apache.commons.jxpath.ri.compiler.ExtensionFunctionTest;
import org.apache.commons.jxpath.ri.compiler.VariableTest;
import org.apache.commons.jxpath.ri.model.EmptyCollectionTest;
import org.apache.commons.jxpath.ri.model.ExternalXMLNamespaceTest;
import org.apache.commons.jxpath.ri.model.MixedModelTest;
import org.apache.commons.jxpath.ri.model.XMLPreserveSpaceTest;
import org.apache.commons.jxpath.ri.model.XMLSpaceTest;
import org.apache.commons.jxpath.ri.model.beans.BadlyImplementedFactoryTest;
import org.apache.commons.jxpath.ri.model.beans.BeanModelTest;
import org.apache.commons.jxpath.ri.model.container.ContainerModelTest;
import org.apache.commons.jxpath.ri.model.dom.DOMModelTest;
import org.apache.commons.jxpath.ri.model.dynabeans.DynaBeanModelTest;
import org.apache.commons.jxpath.ri.model.dynamic.DynamicPropertiesModelTest;
import org.apache.commons.jxpath.ri.model.jdom.JDOMModelTest;
import org.apache.commons.jxpath.util.BasicTypeConverterTest;

/**
 * <p>
 *  Test Suite for the JXPath class.  The majority of these tests use
 *  instances of the TestBean class, so be sure to update the tests if you
 *  change the characteristics of that class.
 * </p>
 *
 * <p>
 *   Note that the tests are dependent upon the static aspects
 *   (such as array sizes...) of the TestBean.java class, so ensure
 *   that all changes to TestBean are reflected here and in other JXPath tests.
 * </p>
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */

public class JXPathTestSuite extends TestCase {

    /**
     * Exercise the whole suite
     */
    public static void main(String args[]) {
        TestRunner.run(suite());
    }

    public JXPathTestSuite(String name) {
        super(name);
    }

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(JXPathCompiledExpressionTest.class);
        suite.addTestSuite(StressTest.class);
        suite.addTestSuite(SimplePathInterpreterTest.class);
        suite.addTestSuite(ContextDependencyTest.class);
        suite.addTestSuite(CoreFunctionTest.class);
        suite.addTestSuite(CoreOperationTest.class);
        suite.addTestSuite(ExtensionFunctionTest.class);
        suite.addTestSuite(VariableTest.class);
        suite.addTestSuite(ContainerModelTest.class);
        suite.addTestSuite(BeanModelTest.class);
        suite.addTestSuite(EmptyCollectionTest.class);
        suite.addTestSuite(DynamicPropertiesModelTest.class);
        suite.addTestSuite(DOMModelTest.class);
        suite.addTestSuite(DynaBeanModelTest.class);
        suite.addTestSuite(JDOMModelTest.class);
        suite.addTestSuite(MixedModelTest.class);
        suite.addTestSuite(BasicTypeConverterTest.class);
        suite.addTestSuite(RecursiveAxesTest.class);
        suite.addTestSuite(XMLSpaceTest.class);
        suite.addTestSuite(XMLPreserveSpaceTest.class);
        suite.addTestSuite(ExternalXMLNamespaceTest.class);
        suite.addTestSuite(BadlyImplementedFactoryTest.class);
        return suite;
    }
}