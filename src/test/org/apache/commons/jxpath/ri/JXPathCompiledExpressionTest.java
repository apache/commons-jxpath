/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/ri/JXPathCompiledExpressionTest.java,v 1.1 2003/01/20 00:00:27 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2003/01/20 00:00:27 $
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

package org.apache.commons.jxpath.ri;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.ri.compiler.*;

/**
 * Test compiler.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2003/01/20 00:00:27 $
 */

public class JXPathCompiledExpressionTest extends JXPathTestCase {
    
    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public JXPathCompiledExpressionTest(String name) {
        super(name);
    }

    public void testConstant() {
        assertXPathExpression("1", Constant.class);
        assertXPathExpression("1.5", Constant.class);
        assertXPathExpression("'foo'", Constant.class);
    }
    
    public void testCoreFunction() {
        assertXPathExpression("last()", CoreFunction.class);
        assertXPathExpression("position()", CoreFunction.class);
        assertXPathExpression("count(book)", CoreFunction.class);
        assertXPathExpression("id(13)", CoreFunction.class);
        assertXPathExpression("local-name()", CoreFunction.class);
        assertXPathExpression("local-name(book)", CoreFunction.class);
        assertXPathExpression("namespace-uri()", CoreFunction.class);
        assertXPathExpression("namespace-uri(book)", CoreFunction.class);
        assertXPathExpression("name()", CoreFunction.class);
        assertXPathExpression("name(book)", CoreFunction.class);
        assertXPathExpression("string(3)", CoreFunction.class);
        assertXPathExpression("concat('a', 'b')", CoreFunction.class);
        assertXPathExpression("starts-with('a', 'b')", CoreFunction.class);
        assertXPathExpression("contains('a', 'b')", CoreFunction.class);
        assertXPathExpression("substring-before('a', 1)", CoreFunction.class);
        assertXPathExpression("substring-after('a', 2)", CoreFunction.class);
        assertXPathExpression("substring('a', 2)", CoreFunction.class);
        assertXPathExpression("substring('a', 2, 3)", CoreFunction.class);
        assertXPathExpression("string-length('a')", CoreFunction.class);
        assertXPathExpression("normalize-space('a')", CoreFunction.class);
        assertXPathExpression("translate('a', 'b', 'c')", CoreFunction.class);
        assertXPathExpression("boolean('true')", CoreFunction.class);
        assertXPathExpression("not(1)", CoreFunction.class);
        assertXPathExpression("true()", CoreFunction.class);
        assertXPathExpression("false()", CoreFunction.class);
        assertXPathExpression("lang('fr')", CoreFunction.class);
        assertXPathExpression("number('12')", CoreFunction.class);
        assertXPathExpression("sum(book/price)", CoreFunction.class);
        assertXPathExpression("floor(11.4)", CoreFunction.class);
        assertXPathExpression("ceiling(11.4)", CoreFunction.class);
        assertXPathExpression("round(11.4)", CoreFunction.class);
        assertXPathExpression("key('title', 'Hobbit')", CoreFunction.class);
    }
    
    public void testCoreOperationAnd() {
        assertXPathExpression(
            "2 and 4",
            CoreOperationAnd.class);

        assertXPathExpression(
            "2 > 1 and 4 < 5",
            CoreOperationAnd.class);            
    }
        
    public void testCoreOperationOr() {
        assertXPathExpression(
            "2 or 4",
            CoreOperationOr.class);

        assertXPathExpression(
            "2 > 1 or 4 < 5",
            CoreOperationOr.class);

        assertXPathExpression(
            "1 > 1 and 2 <= 2 or 3 = 4",
            CoreOperationOr.class);
    }

    public void testCoreOperationEqual() {
        assertXPathExpression(
            "2 = 4",
            CoreOperationEqual.class);

        assertXPathExpression(
            "2 + 1 = 3",
            CoreOperationEqual.class);
    }
    
    public void testCoreOperationNameAttributeTest() {
        assertXPathExpression(
            "@name = 'bar'",
            NameAttributeTest.class);
    }

    public void testCoreOperationNotEqual() {
        assertXPathExpression(
            "2 != 4",
            CoreOperationNotEqual.class);

        assertXPathExpression(
            "2 + 1 != 3",
            CoreOperationNotEqual.class);
    }

    public void testCoreOperationLessThan() {
        assertXPathExpression(
            "3<4",
            CoreOperationLessThan.class,
            "3 < 4");

        assertXPathExpression(
            "3<(2>=1)",
            CoreOperationLessThan.class,
            "3 < (2 >= 1)");
    }
    
    public void testCoreOperationLessThanOrEqual() {
        assertXPathExpression(
            "3<=4",
            CoreOperationLessThanOrEqual.class,
            "3 <= 4");

        assertXPathExpression(
            "3<=(2>=1)",
            CoreOperationLessThanOrEqual.class,
            "3 <= (2 >= 1)");
    }

    public void testCoreOperationGreaterThan() {
        assertXPathExpression(
            "3>4",
            CoreOperationGreaterThan.class,
            "3 > 4");

        assertXPathExpression(
            "3>(2>=1)",
            CoreOperationGreaterThan.class,
            "3 > (2 >= 1)");

        assertXPathExpression(
            "1 > (1 and 2 <= (2 or 3) = 4)",
            CoreOperationGreaterThan.class);
    }
    
    public void testCoreOperationGreaterThanOrEqual() {
        assertXPathExpression(
            "3>=4",
            CoreOperationGreaterThanOrEqual.class,
            "3 >= 4");

        assertXPathExpression(
            "3>=(2>=1)",
            CoreOperationGreaterThanOrEqual.class,
            "3 >= (2 >= 1)");
    }

    public void testCoreOperationDivide() {
        assertXPathExpression(
            "2 div 4",
            CoreOperationDivide.class);

        assertXPathExpression(
            "2|3 div -3",
            CoreOperationDivide.class,
            "2 | 3 div -3");
    }

    public void testCoreOperationMod() {
        assertXPathExpression(
            "2 mod 4",
            CoreOperationMod.class);

        assertXPathExpression(
            "2|3 mod -3",
            CoreOperationMod.class,
            "2 | 3 mod -3");
    }

    public void testCoreOperationMultiply() {
        assertXPathExpression(
            "2*4",
            CoreOperationMultiply.class,
            "2 * 4");
            
        assertXPathExpression(
            "2*(3 + 1)",
            CoreOperationMultiply.class,
            "2 * (3 + 1)");
    }
    
    public void testCoreOperationMinus() {
        assertXPathExpression(
            "1 - 1",
            CoreOperationSubtract.class);
            
        assertXPathExpression(
            "1 - 1 - 2",
            CoreOperationSubtract.class);
            
        assertXPathExpression(
            "1 - (1 - 2)",
            CoreOperationSubtract.class);
    }
    
    public void testCoreOperationSum() {
        assertXPathExpression(
            "3 + 1 + 4", 
            CoreOperationAdd.class);
            
        assertXPathExpression(
            "(3 + 1) + 4",
            CoreOperationAdd.class,
            "3 + 1 + 4");
            
        assertXPathExpression(
            "3 + (1 + 4)",
            CoreOperationAdd.class,
            "3 + 1 + 4");
            
        assertXPathExpression(
            "3 + -1", 
            CoreOperationAdd.class, 
            "3 + -1");
            
        assertXPathExpression(
            "2*-3 + -1",
            CoreOperationAdd.class,
            "2 * -3 + -1");
    }
    
    public void testCoreOperationUnaryMinus() {
        assertXPathExpression("-3", CoreOperationNegate.class);
        assertXPathExpression("-(3 + 1)", CoreOperationNegate.class);
    }

    public void testCoreOperationUnion() {
        assertXPathExpression(
            "3 | 1 | 4",
            CoreOperationUnion.class);
    }
    
    public void testExpressionPath() {
        assertXPathExpression(
            "$x/foo/bar",
            ExpressionPath.class);        
        assertXPathExpression(
            "(2 + 2)/foo/bar",
            ExpressionPath.class);        
        assertXPathExpression(
            "$x[3][2 + 2]/foo/bar",
            ExpressionPath.class);        
    }
    
    public void testExtensionFunction() {
        assertXPathExpression(
            "my:function(3, other.function())",
            ExtensionFunction.class);        
    }
 
    public void testLocationPathAxisSelf() {
        assertXPathExpression(
            "self::foo:bar",
            LocationPath.class);
                 
        assertXPathExpression(
            ".",
            LocationPath.class);     
    }
    
    public void testLocationPathAxisChild() {
        assertXPathExpression(
            "child::foo:bar",
            LocationPath.class,
            "foo:bar");
                 
        assertXPathExpression(
            "foo:bar",
            LocationPath.class);
                 
        assertXPathExpression(
            "/foo:bar",
            LocationPath.class);
                 
        assertXPathExpression(
            "/foo/bar",
            LocationPath.class);     

        assertXPathExpression(
            "*",
            LocationPath.class);
                 
        assertXPathExpression(
            "foo:*",
            LocationPath.class);
                 
    }
    
    public void testLocationPathAxisParent() {
        assertXPathExpression(
            "parent::foo:bar",
            LocationPath.class);
                 
        assertXPathExpression(
            "..",
            LocationPath.class);     
    }
    
    public void testLocationPathAxisAttribute() {
        assertXPathExpression(
            "attribute::foo:bar",
            LocationPath.class,
            "@foo:bar");

        assertXPathExpression(
            "@foo:bar",
            LocationPath.class);

        assertXPathExpression(
            "../@foo:bar",
            LocationPath.class);

        assertXPathExpression(
            "@*",
            LocationPath.class);

        assertXPathExpression(
            "@*[last()]",
            LocationPath.class);
    }
    
    public void testLocationPathAxisDescendant() {
        assertXPathExpression(
            "descendant::foo:bar",
            LocationPath.class);
    }
    
    public void testLocationPathAxisDescendantOrSelf() {
        assertXPathExpression(
            "descendant-or-self::foo:bar",
            LocationPath.class);

        assertXPathExpression(
            "//foo", 
            LocationPath.class);

        assertXPathExpression(
            "foo//bar", 
            LocationPath.class);
    }
    
    public void testLocationPathAxisOther() {
        assertXPathExpression(
            "ancestor::foo:bar",
            LocationPath.class);
            
        assertXPathExpression(
            "ancestor-or-self::foo:bar",
            LocationPath.class);
            
        assertXPathExpression(
            "namespace::foo:bar",
            LocationPath.class);

        assertXPathExpression(
            "preceding::foo:bar",
            LocationPath.class);

        assertXPathExpression(
            "preceding-sibling::foo:bar",
            LocationPath.class);

        assertXPathExpression(
            "following::foo:bar",
            LocationPath.class);

        assertXPathExpression(
            "following-sibling::foo:bar",
            LocationPath.class);
    }
    
    public void testLocationPathNodeTest() {
        assertXPathExpression(
            "node()",
            LocationPath.class);

        assertXPathExpression(
            "text()",
            LocationPath.class);

        assertXPathExpression(
            "comment()",
            LocationPath.class);

        assertXPathExpression(
            "processing-instruction()",
            LocationPath.class);

        assertXPathExpression(
            "processing-instruction('test')",
            LocationPath.class);
    }
    
    public void testVariableReference() {
        assertXPathExpression(
            "$x",
            VariableReference.class);                

        assertXPathExpression(
            "$x:y",
            VariableReference.class);
    }
    
    /**
     * Compiles the xpath into an Expression, checks the expression
     * class, converts the expression to string and checks that the string
     * matches the expected one.
     */
    private void assertXPathExpression(
        String xpath,
        Class expectedClass,
        String expected) 
    {
        JXPathCompiledExpression expression =
            (JXPathCompiledExpression) JXPathContext.compile(xpath);
        
        assertEquals(
            "Expression class for " + xpath,
            expectedClass,
            expression.getExpression().getClass());
            
        assertEquals(
            "Expression toString() for " + xpath,
            expected,
            expression.getExpression().toString());
    }
    
    private void assertXPathExpression(
        String xpath,
        Class expectedClass) 
    {
        assertXPathExpression(xpath, expectedClass, xpath);
    }
    
}