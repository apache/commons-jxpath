/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/tree/Attic/DOMWrapper.java,v 1.1 2001/08/23 00:47:01 dmitri Exp $
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
package org.apache.commons.jxpath.tree;

import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.commons.jxpath.tree.BeanNodeFactory;

/**
 * DOMWrapper creates a light-weight DOM tree modeled
 * after a JavaBean object graph.  That DOM tree can be obtained by calling
 * {@link DOMWrapper#createNode DOMWrapper.createNode}
 * and used as a reqular read-only DOM tree, for example in XSLT transformations.
 * In the following example we use JXPath and XSLT to produce a nice printout of
 * a JavaBean in the XML format.
 *
 * <pre><blockquote>
 * Employee emp = new Employee();
 * </blockquote></pre>
 *
 * Allocate a JXPath DOM tree:
 *
 * <pre><blockquote>
 * Node dom = DOMWrapper.createNode(emp, "employee");
 * </blockquote></pre>
 *
 * And print it using XSLT:
 *
 * <pre><blockquote>
 * Transformer trans = TransformerFactory.newInstance().newTransformer();
 * trans.setOutputProperty(OutputKeys.INDENT, "yes");
 * trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
 * trans.transform(new DOMSource(dom), new StreamResult(System.err));
 * </blockquote></pre>
 *
 * The printout produced by this code looks like this:
 * <pre><blockquote>
 *    &lt;employee&gt;
 *       &lt;firstName&gt;John&lt;/firstName&gt;
 *       &lt;homeAddress&gt;
 *           &lt;streetNumber&gt;123 Main Drive&lt;/streetNumber&gt;
 *           ...
 *       &lt;/homeAddress&gt;
 *       ...
 *   &lt;/employee&gt;
 * </blockquote></pre>
 *
 * <h2>Performance</h2>
 *
 *     The DOM tree created by DOMWrapper is optimized. The JXPath implementation
 *     follows these performance related rules:
 *     <ol>
 *     <li>It only allocates the parts of the tree (Nodes and NodeLists) as they
 *         are requested, so parts of the tree that are never asked for never get
 *         allocated.
 *     <li>Once a part of the tree is created, it is cached - so if the same
 *         parts of the tree are traversed multiple times, the allocation of the
 *         Nodes and NodeLists is only done once.
 *     <li>Property values are only retrieved when they are requested.
 *         Most of the time XPath only needs the
 *         property name - DOMWrapper produces those without retrieving the
 *         corresponding values.
 *     <li>Xalan XPath relies on linear searches over the DOM structures.
 *         JXPath tree structure is optimized to reduce the average
 *         length of those searches.
 *     </ol>
 *
 * <h2>Notes</h2>
 * <ul>
 * <li>The current version of DOMWrapper does not support DOM attributes. Even though XPaths
 *     like "para[@type='warning']" are legitimate, they will always produce empty results.
 *     This may change in future versions of JXPath: the related trade-offs are currently
 *     being evaluated.
 * <li>The current version of JXPath does not support the <code>id(string)</code> and
 *     <code>key(key, value)</code> XPath functions.
 * </ul>
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.1 $ $Date: 2001/08/23 00:47:01 $
 */
public final class DOMWrapper {

    /**
     * Creates a DOM tree that can be used with different xpaths
     * as long as the data represented by the tree remains unchanged.
     */
    public static Node createNode(Object bean, String rootNodeName){
        if (bean instanceof Node){
            return (Node)bean;
        }
        return BeanNodeFactory.allocateNode(null, null, 0, bean, rootNodeName);
    }

    /**
     * Creates a DOM NodeList for a collection or an array. The node list
     * contains elements with the tag name specified as the elementName parameter.
     */
    public static NodeList createNodeList(Object collection, String elementName){
        if (collection instanceof NodeList){
            return (NodeList)collection;
        }
        return BeanNodeFactory.allocateNodeList(collection, elementName);
    }

    /**
     * Returns true if the object cannot be wrapped into a single-root DOM tree.
     * You can use createNodeList() with collections, but not createNode().
     */
    public static boolean isCollection(Object object){
        return BeanNodeFactory.isMultiElement(object.getClass());
    }
}