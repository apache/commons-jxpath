/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/InfoSetUtil.java,v 1.6 2003/01/11 05:41:22 dmitri Exp $
 * $Revision: 1.6 $
 * $Date: 2003/01/11 05:41:22 $
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

import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Type conversions, XPath style.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.6 $ $Date: 2003/01/11 05:41:22 $
 */
public class InfoSetUtil {

    private static final Double ZERO = new Double(0);
    private static final Double ONE = new Double(1);
    private static final Double NOT_A_NUMBER = new Double(Double.NaN);


    /**
     * Converts the supplied object to String
     */
    public static String stringValue(Object object) {
        if (object instanceof String) {
            return (String) object;
        }
        else if (object instanceof Number) {
            double d = ((Number) object).doubleValue();
            long l = ((Number) object).longValue();
            if (d == l) {
                return String.valueOf(l);
            }
            return String.valueOf(d);
        }
        else if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue() ? "true" : "false";
        }
        else if (object == null) {
            return "";
        }
        else if (object instanceof NodePointer) {
            return stringValue(((NodePointer) object).getValue());
        }
        else if (object instanceof EvalContext) {
            EvalContext ctx = (EvalContext) object;
            Pointer ptr = ctx.getSingleNodePointer();
            if (ptr != null) {
                return stringValue(ptr);
            }
            return "";
        }
        return String.valueOf(object);
    }

    /**
     * Converts the supplied object to Number
     */
    public static Number number(Object object) {
        if (object instanceof Number) {
            return (Number) object;
        }
        else if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue() ? ONE : ZERO;
        }
        else if (object instanceof String) {
            Double value;
            try {
                value = new Double((String) object);
            }
            catch (NumberFormatException ex) {
                value = NOT_A_NUMBER;
            }
            return value;
        }
        else if (object instanceof EvalContext) {
            return number(stringValue(object));
        }
        else if (object instanceof NodePointer) {
            return number(((NodePointer) object).getValue());
        }
        return number(stringValue(object));
    }

    /**
     * Converts the supplied object to double
     */
    public static double doubleValue(Object object) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        else if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue() ? 0.0 : 1.0;
        }
        else if (object instanceof String) {
            if (object.equals("")) {
                return 0.0;
            }

            double value;
            try {
                value = Double.parseDouble((String) object);
            }
            catch (NumberFormatException ex) {
                value = Double.NaN;
            }
            return value;
        }
        else if (object instanceof NodePointer) {
            return doubleValue(((NodePointer) object).getValue());
        }
        else if (object instanceof EvalContext) {
            return doubleValue(stringValue(object));
        }
        return doubleValue(stringValue(object));
    }

    /**
     * Converts the supplied object to boolean
     */
    public static boolean booleanValue(Object object) {
        if (object instanceof Number) {
            double value = ((Number) object).doubleValue();
            return value != 0 && value != -0 && !Double.isNaN(value);
        }
        else if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue();
        }
        else if (object instanceof EvalContext) {
            EvalContext ctx = (EvalContext) object;
            return ctx.nextSet() && ctx.nextNode();
        }
        else if (object instanceof String) {
            return ((String) object).length() != 0;
        }
        else if (object instanceof NodePointer) {
            return ((NodePointer) object).isActual();
        }
        else if (object == null) {
            return false;
        }
        return true;
    }
}