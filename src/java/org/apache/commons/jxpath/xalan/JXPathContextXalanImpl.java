/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/xalan/Attic/JXPathContextXalanImpl.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
 * $Revision: 1.1 $
 * $Date: 2001/08/23 00:47:01 $
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
package org.apache.commons.jxpath.xalan;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
//import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.tree.DOMWrapper;
import org.apache.commons.jxpath.tree.BeanNodeFactory;
import org.apache.commons.jxpath.tree.BeanNodeList;
import org.apache.commons.jxpath.tree.ValueHandle;
import org.apache.commons.jxpath.Variables;
import org.apache.xpath.NodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNull;

import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.XPathContext;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xpath.objects.XObject;

/**
 * <a href="http://xml.apache.org/xalan">Xalan</a>-specific implementation
 * of JXPathContext.
 * <p>
 * Instances of JXPathContextXalanImpl are allocated by {@link JXPathContextFactoryXalanImpl}.
 * </p>
 * <p>
 * Xalan is included in the standard distribution of
 * <a href="http://www.java.sun.com/xml/download.html">JAXP</a>.
 * </p>
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public class JXPathContextXalanImpl extends JXPathContext {

    private Node wrapper;
    private HashMap varWrappers = new HashMap();
    private static final XObject NULL = new XNull();

    protected JXPathContextXalanImpl(JXPathContext parentContext, Object contextBean){
        super(parentContext, contextBean);
    }

    /**
     * Returns (and caches) a DOMWrapper for the context's context bean
     */
    private Node getWrapper(){
        if (wrapper == null){
            wrapper = DOMWrapper.createNode(contextBean, "jxpath");
        }
        return wrapper;
    }

    /**
     * Maintains a cache of Nodes/NodeLists for variable values. Creates a new
     * node or node list using DOMWrapper if there is no node for the specified variable
     * or if the value of the variable has changed.
     */
    private Object getWrapper(String variable, Object value){
        Object w = varWrappers.get(variable);
        if (DOMWrapper.isCollection(value)){
            if (w == null || !(w instanceof BeanNodeList) || (((BeanNodeList)w).getCollection() != value)){
                w = new NodeSet(DOMWrapper.createNodeList(value, variable));
                varWrappers.put(variable, w);
            }
        }
        else {
            if (w == null || !(w instanceof ValueHandle) || (((ValueHandle)w).getBean() != value)){
                w = DOMWrapper.createNode(value, variable);
                varWrappers.put(variable, w);
            }
        }
        return w;
    }

    /**
     * Traverses the xpath and returns the resulting object. Primitive
     * types are wrapped into objects.
     */
    public Object getValue(String xpath){
        return evalNode(getWrapper(), xpath, true);
    }

    /**
     * Traverses the xpath and returns a List of objects. Even if
     * there is only one object that matches the xpath, it will be returned
     * as a collection with one element.  If the xpath matches no properties
     * in the graph, the List will be empty.
     */
    public List eval(String xpath){
        return (List)evalNode(getWrapper(), xpath, false);
    }

    public Pointer locateValue(String xpath){
        throw new UnsupportedOperationException("locateValue");
    }

    public List locate(String xpath){
        throw new UnsupportedOperationException("locate");
    }


    /**
     * Passes the node and the xpath to Xalan XPath.  If needed, the results are
     * unwrapped from ValueHandles and/or wrapped into Lists.
     */
    private Object evalNode(Node node, String xpath, boolean singleValue) {
        Object result = null;
        try {
            XObject o = eval(node, xpath);
            switch (o.getType()){
                case XObject.CLASS_NODESET:
                    NodeIterator set = o.nodeset();
                    Node n = set.nextNode();
                    if (singleValue){
                        if (n != null){
                            if (n instanceof ValueHandle){
                                result = ((ValueHandle)n).getValue();
                            }
                            else {
                                result = n;
                            }
                        }
                    }
                    else {
                        ArrayList list = new ArrayList();
                        while (n != null){
                            if (n instanceof ValueHandle){
                                list.add(((ValueHandle)n).getValue());
                            }
                            else {
                                list.add(n);
                            }
                            n = set.nextNode();
                        }
                        result = list;
                    }
                    set.detach();
                    break;
                case XObject.CLASS_BOOLEAN:
                    result = o.bool() ? Boolean.TRUE : Boolean.FALSE;
                    break;
                case XObject.CLASS_NULL:
                    result = null;
                    break;
                case XObject.CLASS_NUMBER:
                    result = new Double(o.num());
                    break;
                case XObject.CLASS_STRING:
                    result = o.str();
                    break;
                case XObject.CLASS_RTREEFRAG:
                case XObject.CLASS_UNKNOWN:
                case XObject.CLASS_UNRESOLVEDVARIABLE:
                    result = null;
                    break;
            }
        }
        catch (javax.xml.transform.TransformerException ex){
//            ex.printStackTrace();
            throw new RuntimeException("Cannot traverse xpath \"" + xpath + "\": " + ex.getMessage());
        }

        if (!singleValue && !(result instanceof List)){
            if (result == null){
                result = Collections.EMPTY_LIST;
            }
            else {
                ArrayList list = new ArrayList();
                list.add(result);
                result = list;
            }
        }
        return result;
    }

    /**
     * Modifies the value of the property or variable described by the supplied xpath.
     * Will throw an exception if one of the following conditions occurs:
     * <ul>
     * <li>The xpath does not in fact describe an existing property or variable
     * <li>The property is not writable (no public, non-static set method)
     * </ul>
     */
    public void setValue(String xpath, Object value){
        try {
            Node node = getWrapper();
            xpath = xpath.trim();
            if (xpath.startsWith("$")){
                // See if we have a simple variable assignment
                boolean isVar = true;
                char chars[] = xpath.toCharArray();
                for (int i = 1; i < chars.length; i++){
                    // Should do a more precise test
                    if (!Character.isUnicodeIdentifierPart(chars[i])){
                        isVar = false;
                        break;
                    }
                }
                if (isVar){
                    setVariableValue(xpath.substring(1), value);
                    return;
                }
            }

            XObject o = eval(node, xpath);
            if (o.getType() != XObject.CLASS_NODESET){
                throw new RuntimeException("Xpath does not represent a singular property");
            }

            NodeIterator set = o.nodeset();
            Node n = set.nextNode();
            if (n == null){
                throw new RuntimeException("Xpath does not represent a singular property");
            }
            if (!(n instanceof ValueHandle)){
                throw new RuntimeException("Attempt to use setProperty() with a non-JXPath DOM tree");
            }

            ((ValueHandle)n).setValue(value);
        }
        catch (Exception ex){
            throw new RuntimeException("Cannot set property value with xpath \"" + xpath + "\": " + ex.getMessage());
        }
    }

    /**
     * Invokes Xalan to traverse the supplied xpath
     */
    private XObject eval(Node node, String xpath) throws TransformerException {
        Node namespaceNode = node;
        Node contextNode = node;

        XPathContext xpathSupport = new JXalanPathContext();

        PrefixResolverDefault prefixResolver = new PrefixResolverDefault(
          (namespaceNode.getNodeType() == Node.DOCUMENT_NODE)
          ? ((Document) namespaceNode).getDocumentElement() : namespaceNode);

        XPath xp = new XPath(xpath, null, prefixResolver, XPath.SELECT, null);
        return xp.execute(xpathSupport, contextNode, prefixResolver);
    }

    /**
     * Finds the context where the variable is defined and changes its value.
     */
    private void setVariableValue(String varName, Object value) throws TransformerException {
        JXPathContext varCtx = JXPathContextXalanImpl.this;
        Variables vars = null;
        while (varCtx != null){
            vars = varCtx.getVariables();
            if (vars.isDeclaredVariable(varName)){
                break;
            }
            varCtx = varCtx.getParentContext();
            vars = null;
        }

        if (vars != null){
            vars.declareVariable(varName, value);
        }
        else {
            throw new TransformerException("Undefined variable: " + varName);
        }
    }

    /**
     * Finds the context where the variable is defined and returns its value
     */
    private XObject getVariableValue(String varName) throws TransformerException {
        XObject xo = null;
        JXPathContext varCtx = JXPathContextXalanImpl.this;
        Variables vars = null;
        while (varCtx != null){
            vars = varCtx.getVariables();
            if (vars.isDeclaredVariable(varName)){
                break;
            }
            varCtx = varCtx.getParentContext();
            vars = null;
        }

        if (vars != null){
            Object value = vars.getVariable(varName);
            if (value == null){
                xo = NULL;
            }
            else if (value instanceof String ||
                     value instanceof Boolean ||
                     value instanceof Double ||
                     value instanceof DocumentFragment ||
                     value instanceof Node ||
                     value instanceof NodeIterator){
                xo = XObject.create(value);
            }
            else if (value instanceof Number){
                xo = XObject.create(new Double(((Number)value).doubleValue()));
            }
            else {
                // If the parent context is a context of that can produce a wrapper,
                // we should store the wrapper in the cache of that context, not
                // the current one.  Chances are the parent context is reused
                // with multiple child contexts.

                Object wrapper = null;
                if (varCtx instanceof JXPathContextXalanImpl){
                    wrapper = ((JXPathContextXalanImpl)varCtx).getWrapper(varName, value);
                }
                else {
                    wrapper = getWrapper(varName, value);
                }
                xo = XObject.create(wrapper);
            }
        }
        else {
            throw new TransformerException("Undefined variable: " + varName);
        }
        return xo;
    }

    /**
     * Custom subclass of XPathContext that provides access to JXPath
     * Variables pools.
     */
    private class JXalanPathContext extends XPathContext {
        public XObject getVariable(QName qname) throws TransformerException {
            XObject xo = null;
            try {
                xo = super.getVariable(qname);
            }
            catch (Exception te){
            }

            if (xo == null){
                try {
                    xo = getVariableValue(qname.toString());
                }
                catch (TransformerException te){
                    throw te;
                }
                catch (Exception ex){
                    throw new TransformerException("Undefined variable: " + qname, ex);
                }
            }
            return xo;
        }
    }
}