/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/JXPathException.java,v 1.1 2002/04/26 03:28:36 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2002/04/26 03:28:36 $
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

/**
 * Thrown in various situations by JXPath; may contain a nested exception.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2002/04/26 03:28:36 $
 */

public class JXPathException extends RuntimeException {

    /** @serial */
    private Throwable exception;

    /**
     * Create a new <code>JXPathException</code> with no
     * detail mesage.
     */

     public JXPathException() {
         super();
         this.exception = null;
     }

    /**
     * Create a new <code>JXPathException</code> with
     * the <code>String </code> specified as an error message.
     *
     * @param msg The error message for the exception.
     */
    public JXPathException(String msg) {
        super(msg);
        this.exception = null;
    }


    /**
     * Create a new <code>JXPathException</code> with a
     * given <code>Throwable</code> base cause of the error.
     *
     * @param e The exception to be encapsulated in a
     * JXPathException.
     */
    public JXPathException(Throwable e) {
        super(e.toString());
        this.exception = e;
    }

    /**
     * Create a new <code>JXPathException</code> with the
     * given <code>Exception</code> base cause and detail message.
     *
     * @param e The exception to be encapsulated in a
     * JXPathException
     * @param msg The detail message.
     */
    public JXPathException(String msg, Throwable e) {
        super(msg);
        this.exception = e;
    }


    /**
     * Return the message (if any) for this error . If there is no
     * message for the exception and there is an encapsulated
     * exception then the message of that exception will be returned.
     *
     * @return The error message.
     */
    public String getMessage () {
        String message = super.getMessage ();

        if (exception != null) {
            if (message == null){
                if (exception.getMessage() != null){
                    return exception.getMessage();
                }
                else {
                    return exception.getClass().getName();
                }
            }
            else {
                if (exception.getMessage() != null){
                    return message + "; " + exception.getMessage();
                }
                else {
                    return message + "; " + exception.getClass().getName();
                }
            }
        }

        return message;
    }

    /**
     * Return the actual exception (if any) that caused this exception to
     * be raised.
     *
     * @return The encapsulated exception, or null if there is none.
     */
    public Throwable getException () {
        return exception;
    }
}