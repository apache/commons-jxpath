/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/CoreFunction.java,v 1.1 2001/08/23 00:46:59 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2001/08/23 00:46:59 $
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

import java.util.*;

import org.apache.commons.jxpath.ri.Compiler;

/**
 * An element of the compile tree representing one of built-in functions
 * like "position()" or "number()".
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:46:59 $
 */
public class CoreFunction extends Operation {

    private int functionCode;

    public CoreFunction(int functionCode, Expression args[]){
        super(Expression.OP_CORE_FUNCTION, args);
        this.functionCode = functionCode;
    }

    public int getFunctionCode(){
        return functionCode;
    }

    public Expression getArg1(){
        return args[0];
    }

    public Expression getArg2(){
        return args[1];
    }

    public Expression getArg3(){
        return args[2];
    }

    public int getArgumentCount(){
        if (args == null){
            return 0;
        }
        return args.length;
    }

    /**
     * Returns true if any argument is context dependent or if
     * the function is last(), position(), boolean(), local-name(),
     * name(), string(), lang(), number().
     */
    public boolean computeContextDependent(){
        if (super.computeContextDependent()){
            return true;
        }

        switch(functionCode){
            case Compiler.FUNCTION_LAST:
            case Compiler.FUNCTION_POSITION:
                return true;

            case Compiler.FUNCTION_BOOLEAN:
            case Compiler.FUNCTION_LOCAL_NAME:
            case Compiler.FUNCTION_NAME:
            case Compiler.FUNCTION_NAMESPACE_URI:
            case Compiler.FUNCTION_STRING:
            case Compiler.FUNCTION_LANG:
            case Compiler.FUNCTION_NUMBER:
                return args.length == 0;

            case Compiler.FUNCTION_COUNT:
            case Compiler.FUNCTION_ID:
            case Compiler.FUNCTION_CONCAT:
            case Compiler.FUNCTION_STARTS_WITH:
            case Compiler.FUNCTION_CONTAINS:
            case Compiler.FUNCTION_SUBSTRING_BEFORE:
            case Compiler.FUNCTION_SUBSTRING_AFTER:
            case Compiler.FUNCTION_SUBSTRING:
            case Compiler.FUNCTION_STRING_LENGTH:
            case Compiler.FUNCTION_NORMALIZE_SPACE:
            case Compiler.FUNCTION_TRANSLATE:
            case Compiler.FUNCTION_NOT:
            case Compiler.FUNCTION_TRUE:
            case Compiler.FUNCTION_FALSE:
            case Compiler.FUNCTION_SUM:
            case Compiler.FUNCTION_FLOOR:
            case Compiler.FUNCTION_CEILING:
            case Compiler.FUNCTION_ROUND:
                return false;
        }

        return false;
    }

    protected String opCodeToString(){
        String function = null;
        switch(functionCode){
            case Compiler.FUNCTION_LAST:             function = "last"; break;
            case Compiler.FUNCTION_POSITION:         function = "position"; break;
            case Compiler.FUNCTION_COUNT:            function = "count"; break;
            case Compiler.FUNCTION_ID:               function = "id"; break;
            case Compiler.FUNCTION_LOCAL_NAME:       function = "local-name"; break;
            case Compiler.FUNCTION_NAMESPACE_URI:    function = "namespace-uri"; break;
            case Compiler.FUNCTION_NAME:             function = "name"; break;
            case Compiler.FUNCTION_STRING:           function = "string"; break;
            case Compiler.FUNCTION_CONCAT:           function = "concat"; break;
            case Compiler.FUNCTION_STARTS_WITH:      function = "starts-with"; break;
            case Compiler.FUNCTION_CONTAINS:         function = "contains"; break;
            case Compiler.FUNCTION_SUBSTRING_BEFORE: function = "substring-before"; break;
            case Compiler.FUNCTION_SUBSTRING_AFTER:  function = "substring-after"; break;
            case Compiler.FUNCTION_SUBSTRING:        function = "substring"; break;
            case Compiler.FUNCTION_STRING_LENGTH:    function = "string-length"; break;
            case Compiler.FUNCTION_NORMALIZE_SPACE:  function = "normalize-space"; break;
            case Compiler.FUNCTION_TRANSLATE:        function = "translate"; break;
            case Compiler.FUNCTION_BOOLEAN:          function = "boolean"; break;
            case Compiler.FUNCTION_NOT:              function = "not"; break;
            case Compiler.FUNCTION_TRUE:             function = "true"; break;
            case Compiler.FUNCTION_FALSE:            function = "false"; break;
            case Compiler.FUNCTION_LANG:             function = "lang"; break;
            case Compiler.FUNCTION_NUMBER:           function = "number"; break;
            case Compiler.FUNCTION_SUM:              function = "sum"; break;
            case Compiler.FUNCTION_FLOOR:            function = "floor"; break;
            case Compiler.FUNCTION_CEILING:          function = "ceiling"; break;
            case Compiler.FUNCTION_ROUND:            function = "round"; break;
//            case Compiler.FUNCTION_KEY:            function = "key"; break;
        }
        return super.opCodeToString() + ':' + function;
    }
}