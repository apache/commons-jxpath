/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/ri/compiler/CoreOperationTest.java,v 1.1 2002/10/20 03:48:22 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2002/10/20 03:48:22 $
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

package org.apache.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.Variables;

/**
 * Test basic functionality of JXPath - infoset types,
 * operations.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2002/10/20 03:48:22 $
 */

public class CoreOperationTest extends JXPathTestCase
{
    private JXPathContext context;

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public CoreOperationTest(String name){
        super(name);
    }

    public void setUp(){
        if (context == null){
            context = JXPathContext.newContext(null);
            Variables vars = context.getVariables();
            vars.declareVariable("integer", new Integer(1));
        }
    }

    public void testInfoSetTypes(){

        // Numbers
        assertXPathValue(context, "1",  new Double(1.0));
        assertXPathPointer(context, "1", "1");
        assertXPathValueIterator(context,"1", list(new Double(1.0)));

        assertXPathPointerIterator(context, "1", list("1"));

        assertXPathValue(context,"-1", new Double(-1.0));
        assertXPathValue(context,"2 + 2", new Double(4.0));
        assertXPathValue(context,"3 - 2", new Double(1.0));
        assertXPathValue(context,"1 + 2 + 3 - 4 + 5", new Double(7.0));
        assertXPathValue(context,"3 * 2", new Double(3.0*2.0));
        assertXPathValue(context,"3 div 2", new Double(3.0/2.0));
        assertXPathValue(context,"5 mod 2", new Double(1.0));

        // This test produces a different result with Xalan?
        assertXPathValue(context,"5.9 mod 2.1", new Double(1.0));

        assertXPathValue(context,"5 mod -2", new Double(1.0));
        assertXPathValue(context,"-5 mod 2", new Double(-1.0));
        assertXPathValue(context,"-5 mod -2", new Double(-1.0));
        assertXPathValue(context,"1 < 2", Boolean.TRUE);
        assertXPathValue(context,"1 > 2", Boolean.FALSE);
        assertXPathValue(context,"1 <= 1", Boolean.TRUE);
        assertXPathValue(context,"1 >= 2", Boolean.FALSE);
        assertXPathValue(context,"3 > 2 > 1", Boolean.FALSE);
        assertXPathValue(context,"3 > 2 and 2 > 1", Boolean.TRUE);
        assertXPathValue(context,"3 > 2 and 2 < 1", Boolean.FALSE);
        assertXPathValue(context,"3 < 2 or 2 > 1", Boolean.TRUE);
        assertXPathValue(context,"3 < 2 or 2 < 1", Boolean.FALSE);
        assertXPathValue(context,"1 = 1", Boolean.TRUE);
        assertXPathValue(context,"1 = '1'", Boolean.TRUE);
        assertXPathValue(context,"1 > 2 = 2 > 3", Boolean.TRUE);
        assertXPathValue(context,"1 > 2 = 0", Boolean.TRUE);
        assertXPathValue(context,"1 = 2", Boolean.FALSE);

        assertXPathValue(context, 
                "$integer",
                new Double(1), 
                Double.class);
                
        assertXPathValue(context, 
                "2 + 3",
                "5.0",
                String.class);
                
        assertXPathValue(context, 
                "2 + 3",
                Boolean.TRUE, 
                boolean.class);
                
        assertXPathValue(context, 
                "'true'",
                Boolean.TRUE, 
                Boolean.class);
    }
}