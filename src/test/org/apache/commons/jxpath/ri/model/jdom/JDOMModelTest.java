/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/test/org/apache/commons/jxpath/ri/model/jdom/JDOMModelTest.java,v 1.10 2004/01/19 20:44:52 dmitri Exp $
 * $Revision: 1.10 $
 * $Date: 2004/01/19 20:44:52 $
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

package org.apache.commons.jxpath.ri.model.jdom;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.ri.model.XMLModelTestCase;
import org.apache.commons.jxpath.xml.DocumentContainer;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;

/**
 * Tests JXPath with JDOM
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.10 $ $Date: 2004/01/19 20:44:52 $
 */

public class JDOMModelTest extends XMLModelTestCase {
    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public JDOMModelTest(String name) {
        super(name);
    }

    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(JDOMModelTest.class));
    }

    protected String getModel() {
        return DocumentContainer.MODEL_JDOM;
    }
        
    public void testID() {
        // id() is not supported by JDOM
    }

    protected AbstractFactory getAbstractFactory() {
        return new TestJDOMFactory();
    }

    protected String getXMLSignature(
        Object node,
        boolean elements,
        boolean attributes,
        boolean text,
        boolean pi) 
    {
        StringBuffer buffer = new StringBuffer();
        appendXMLSignature(buffer, node, elements, attributes, text, pi);
        return buffer.toString();
    }

    private void appendXMLSignature(
        StringBuffer buffer,
        Object object,
        boolean elements,
        boolean attributes,
        boolean text,
        boolean pi) 
    {
        if (object instanceof Document) {
            buffer.append("<D>");
            appendXMLSignature(
                buffer,
                ((Document) object).getContent(),
                elements,
                attributes,
                text,
                pi);
            buffer.append("</D");
        }
        else if (object instanceof Element) {
            String tag = elements ? ((Element) object).getName() : "E";
            buffer.append("<");
            buffer.append(tag);
            buffer.append(">");
            appendXMLSignature(
                buffer,
                ((Element) object).getContent(),
                elements,
                attributes,
                text,
                pi);
            buffer.append("</");
            buffer.append(tag);
            buffer.append(">");
        }
        else if (object instanceof Text || object instanceof CDATA) {
            if (text) {
                String string = ((Text) object).getText();
                string = string.replace('\n', '=');
                buffer.append(string);
            }
        }
    }

    private void appendXMLSignature(
        StringBuffer buffer,
        List children,
        boolean elements,
        boolean attributes,
        boolean text,
        boolean pi) 
    {
        for (int i = 0; i < children.size(); i++) {
            appendXMLSignature(
                buffer,
                children.get(i),
                elements,
                attributes,
                text,
                pi);
        }
    }
}