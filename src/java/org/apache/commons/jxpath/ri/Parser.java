/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/Parser.java,v 1.6 2003/03/11 00:59:19 dmitri Exp $
 * $Revision: 1.6 $
 * $Date: 2003/03/11 00:59:19 $
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

import java.io.StringReader;

import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.parser.ParseException;
import org.apache.commons.jxpath.ri.parser.TokenMgrError;
import org.apache.commons.jxpath.ri.parser.XPathParser;

/**
 * XPath parser
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.6 $ $Date: 2003/03/11 00:59:19 $
 */
public class Parser {

    private static XPathParser parser = new XPathParser(new StringReader(""));

    /**
     * Parses the XPath expression. Throws a JXPathException in case
     * of a syntax error.
     */
    public static Object parseExpression(
        String expression,
        Compiler compiler) 
    {
        synchronized (parser) {
            parser.setCompiler(compiler);
            Object expr = null;
            try {
                parser.ReInit(new StringReader(expression));
                expr = parser.parseExpression();
            }
            catch (TokenMgrError e) {
                throw new JXPathException(
                    "Invalid XPath: '"
                        + addEscapes(expression)
                        + "'. Invalid symbol '"
                        + addEscapes(String.valueOf(e.getCharacter()))
                        + "' "
                        + describePosition(expression, e.getPosition()));
            }
            catch (ParseException e) {
                throw new JXPathException(
                    "Invalid XPath: '"
                        + addEscapes(expression)
                        + "'. Syntax error "
                        + describePosition(
                            expression,
                            e.currentToken.beginColumn));
            }
            return expr;
        }
    }

    private static String describePosition(String expression, int position) {
        if (position <= 0) {
            return "at the beginning of the expression";
        }
        else if (position >= expression.length()) {
            return "- expression incomplete";
        }
        else {
            return "after: '"
                + addEscapes(expression.substring(0, position)) + "'";
        }
    }

    private static String addEscapes(String string) {
        // Piggy-back on the code generated by JavaCC
        return TokenMgrError.addEscapes(string);
    }
}