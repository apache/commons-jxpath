/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/JXPathTestSuite.java,v 1.2 2002/11/28 01:02:05 dmitri Exp $
 * $Revision: 1.2 $
 * $Date: 2002/11/28 01:02:05 $
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.jxpath.ri.axes.SimplePathInterpreterTest;
import org.apache.commons.jxpath.ri.compiler.ContextDependencyTest;
import org.apache.commons.jxpath.ri.compiler.CoreFunctionTest;
import org.apache.commons.jxpath.ri.compiler.CoreOperationTest;
import org.apache.commons.jxpath.ri.compiler.ExtensionFunctionTest;
import org.apache.commons.jxpath.ri.compiler.VariableTest;
import org.apache.commons.jxpath.ri.model.MixedModelTest;
import org.apache.commons.jxpath.ri.model.beans.BeanModelTest;
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
 * @version $Revision: 1.2 $ $Date: 2002/11/28 01:02:05 $
 */

public class JXPathTestSuite extends TestCase
{
    private static boolean enabled = true;

    /**
     * Exercise the whole suite
     */
    public static void main(String args[]) {
        TestRunner.run(suite());
    }

    public JXPathTestSuite(String name){
        super(name);
    }

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(SimplePathInterpreterTest.class);
        suite.addTestSuite(ContextDependencyTest.class);
        suite.addTestSuite(CoreFunctionTest.class);
        suite.addTestSuite(CoreOperationTest.class);
        suite.addTestSuite(ExtensionFunctionTest.class);
        suite.addTestSuite(VariableTest.class);
        suite.addTestSuite(BeanModelTest.class);
        suite.addTestSuite(DynamicPropertiesModelTest.class);
        suite.addTestSuite(DOMModelTest.class);
        suite.addTestSuite(DynaBeanModelTest.class);
        suite.addTestSuite(JDOMModelTest.class);
        suite.addTestSuite(MixedModelTest.class);
        suite.addTestSuite(BasicTypeConverterTest.class);
        return suite;
    }
}