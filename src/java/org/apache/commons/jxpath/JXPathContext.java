/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/JXPathContext.java,v 1.9 2002/05/08 23:19:31 dmitri Exp $
 * $Revision: 1.9 $
 * $Date: 2002/05/08 23:19:31 $
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

import java.util.*;

/**
 * JXPathContext provides APIs for the traversal of graphs of JavaBeans using
 * the XPath syntax.
 * Using JXPathContext, you can read and write properties of JavaBeans, arrays, collections
 * and maps. JXPathContext uses JavaBeans introspection to enumerate and access JavaBeans
 * properties.
 * <p>
 * JXPathContext allows alternative implementations. This is why instead of
 * allocating JXPathContext directly, you should call a static <code>newContext</code>
 * method.  This method will utilize the JXPathContextFactory API to locate
 * a suitable implementation of JXPath.
 * JXPath comes bundled with a default implementation called Reference Implementation.
 * </p>
 *
 * <h2>JXPath Interprets XPath Syntax on Java Object Graphs</h2>
 *
 * JXPath uses an intuitive interpretation of the xpath syntax in the context
 * of Java object graphs. Here are some examples:
 *
 * <h3>Example 1: JavaBean Property Access</h3>
 *
 * JXPath can be used to access properties of a JavaBean.
 *
 * <pre><blockquote>
 * public class Employee {
 *    public String getFirstName(){
 *       ...
 *    }
 * }
 *
 * Employee emp = new Employee();
 * ...
 *
 * JXPathContext context = JXPathContext.newContext(emp);
 * String fName = (String)context.getValue("firstName");
 * </blockquote></pre>
 *
 * In this example, we are using JXPath to access a property of the <code>emp</code> bean.
 * In this simple case the invocation of JXPath is equivalent to invocation of getFirstName()
 * on the bean.
 *
 * <h3>Example 2: Nested Bean Property Access</h3>
 * JXPath can traverse object graphs:
 *
 * <pre><blockquote>
 * public class Employee {
 *    public Address getHomeAddress(){
 *       ...
 *    }
 * }
 * public class Address {
 *    public String getStreetNumber(){
 *       ...
 *    }
 * }
 *
 * Employee emp = new Employee();
 * ...
 *
 * JXPathContext context = JXPathContext.newContext(emp);
 * String sNumber = (String)context.getValue("homeAddress/streetNumber");
 * </blockquote></pre>
 *
 * In this case XPath is used to access a property of a nested bean.
 * <p>
 * A property identified by the xpath does not have to be a "leaf" property.
 * For instance, we can extract the whole Address object in above example:
 *
 * <pre><blockquote>
 *    Address addr = (Address)context.getValue("homeAddress");
 * </blockquote></pre>
 * </p>
 *
 * <h3>Example 3: Collection Subscripts</h3>
 * JXPath can extract elements from arrays and collections.
 *
 * <pre><blockquote>
 * public class Integers {
 *    public int[] getNumbers(){
 *       ...
 *    }
 * }
 *
 * Integers ints = new Integers();
 * ...
 *
 * JXPathContext context = JXPathContext.newContext(ints);
 * Integer thirdInt = (Integer)context.getValue("numbers[3]");
 * </blockquote></pre>
 * A collection can be an arbitrary array or an instance of java.util.Collection.
 * <p>
 * Note: in XPath the first element of a collection has index 1, not 0.<br>
 *
 * <h3>Example 4: Map Element Access</h3>
 *
 * JXPath supports maps. To get a value use its key.
 *
 * <pre><blockquote>
 * public class Employee {
 *    public Map getAddresses(){
 *       return addressMap;
 *    }
 *
 *    public void addAddress(String key, Address address){
 *       addressMap.put(key, address);
 *    }
 *    ...
 * }
 *
 * Employee emp = new Employee();
 * emp.addAddress("home", new Address(...));
 * emp.addAddress("office", new Address(...));
 * ...
 *
 * JXPathContext context = JXPathContext.newContext(emp);
 * String homeZipCode = (String)context.getValue("addresses/home/zipCode");
 * </blockquote></pre>
 *
 * Often you will need to use the alternative syntax for accessing Map
 * elements:
 *
 * <pre><blockquote>
 * String homeZipCode = (String)context.getValue("addresses[@name='home']/zipCode");
 * </blockquote></pre>
 *
 * In this case, the key can be an expression, e.g. a variable.<br>
 *
 * Note: At this point JXPath only supports Maps that use strings for keys.<br>
 * Note: JXPath supports the extended notion of Map: any object with
 *       dynamic properties can be handled by JXPath provided that its
 *       class is registered with the {@link JXPathIntrospector}.
 *
 * <h3>Example 5: Retrieving Multiple Results</h3>
 *
 * JXPath can retrieve multiple objects from a graph. Note that the method
 * called in this case is not <code>getValue</code>, but <code>eval</code>.
 *
 * <pre><blockquote>
 * public class Author {
 *    public Book[] getBooks(){
 *       ...
 *    }
 * }
 *
 * Author auth = new Author();
 * ...
 *
 * JXPathContext context = JXPathContext.newContext(auth);
 * List threeBooks = (List)context.eval("books[position() &lt; 4]");
 * </blockquote></pre>
 *
 * This returns a list of at most three books from the array of all books
 * written by the author.
 *
 * <h3>Example 6: Setting Properties</h3>
 * JXPath can be used to modify property values.
 *
 * <pre><blockquote>
 * public class Employee {
 *    public Address getAddress() {
 *       ...
 *    }
 *
 *    public void setAddress(Address address) {
 *       ...
 *    }
 * }
 *
 * Employee emp = new Employee();
 * Address addr = new Address();
 * ...
 *
 * JXPathContext context = JXPathContext.newContext(emp);
 * context.setValue("address", addr);
 * context.setValue("address/zipCode", "90190");
 *
 * </blockquote></pre>
 *
 * <h3>Example 7: Creating objects</h3>
 * JXPath can be used to create new objects. First, create a subclass of
 * {@link AbstractFactory AbstractFactory} and install it on the JXPathContext.
 * Then call {@link JXPathContext#createPath createPath()} instead of "setValue".
 * JXPathContext will invoke your AbstractFactory when it discovers that an
 * intermediate node of the path is <b>null</b>.  It will not override existing
 * nodes.
 *
 * <pre><blockquote>
 * public class AddressFactory extends AbstractFactory {
 *    public boolean createObject(JXPathContext context, Pointer pointer, Object parent, String name, int index){
 *     if ((parent instanceof Employee) &amp;&amp; name.equals("address"){
 *       ((Employee)parent).setAddress(new Address());
 *       return true;
 *     }
 *     return false;
 *   }
 * }
 *
 * JXPathContext context = JXPathContext.newContext(emp);
 * context.setFactory(new AddressFactory());
 * context.createPath("address/zipCode", "90190");
 * </blockquote></pre>
 *
 * <h3>Example 8: Using Variables</h3>
 * JXPath supports the notion of variables. The XPath syntax for accessing
 * variables is <i>"$varName"</i>.
 *
 * <pre><blockquote>
 * public class Author {
 *    public Book[] getBooks(){
 *       ...
 *    }
 * }
 *
 * Author auth = new Author();
 * ...
 *
 * JXPathContext context = JXPathContext.newContext(auth);
 * context.getVariables().declareVariable("index", new Integer(2));
 *
 * Book secondBook = (Book)context.getValue("books[$index]");
 * </blockquote></pre>
 *
 * You can also set variables using JXPath:
 *
 * <pre><blockquote>
 * context.setValue("$index", new Integer(3));
 * </blockquote></pre>
 *
 * Note: you can only <i>change</i> the value of an existing variable this
 * way, you cannot <i>define</i> a new variable.
 *
 * <p>
 * When a variable contains a JavaBean or a collection, you can
 * traverse the bean or collection as well:
 * <pre><blockquote>
 * ...
 * context.getVariables().declareVariable("book", myBook);
 * String title = (String)context.getValue("$book/title);
 *
 * Book array[] = new Book[]{...};
 *
 * context.getVariables().declareVariable("books", array);
 *
 * String title = (String)context.getValue("$books[2]/title);
 * </blockquote></pre>
 *
 * <h3>Example 9: Using Nested Contexts</h3>
 * If you need to use the same set of variable while interpreting
 * XPaths with different beans, it makes sense to put the variables in a separate
 * context and specify that context as a parent context every time you
 * allocate a new JXPathContext for a JavaBean.
 *
 * <pre><blockquote>
 * JXPathContext varContext = JXPathContext.newContext(null);
 * varContext.getVariables().declareVariable("title", "Java");
 *
 * JXPathContext context = JXPathContext.newContext(varContext, auth);
 *
 * List javaBooks = (List)context.eval("books[title = $title]");
 * </blockquote></pre>
 *
 * <h3>Using Custom Variable Pools</h3>
 * By default, JXPathContext creates a HashMap of variables. However,
 * you can substitute a custom implementation of the Variables
 * interface to make JXPath work with an alternative source of variables.
 * For example, you can define implementations of Variables that
 * cover a servlet context, HTTP request or any similar structure.
 *
 * <h3>Example 10: Using Standard Extension Functions</h3>
 * Using the standard extension functions, you can call methods on objects,
 * static methods on classes and create objects using any constructor.
 * The class names should be fully qualified.
 * <p>
 * Here's how you can create new objects:
 * <pre><blockquote>
 * Book book = (Book)context.getValue("org.apache.commons.jxpath.example.Book.new('John Updike')");
 * </blockquote></pre>
 *
 * Here's how you can call static methods:
 * <pre><blockquote>
 * Book book = (Book)context.getValue("org.apache.commons.jxpath.example.Book.getBestBook('John Updike')");
 * </blockquote></pre>
 *
 * Here's how you can call regular methods:
 * <pre><blockquote>
 * String firstName = (String)context.getValue("getAuthorsFirstName($book)");
 * </blockquote></pre>
 * As you can see, the target of the method is specified as the first parameter
 * of the function.
 *
 * <h3>Example 11: Using Custom Extension Functions</h3>
 * Collections of custom extension functions can be implemented
 * as {@link Functions Functions} objects or as Java classes, whose methods
 * become extenstion functions.
 * <p>
 * Let's say the following class implements various formatting operations:
 * <pre><blockquote>
 * public class Formats {
 *    public static String date(Date d, String pattern){
 *        return new SimpleDateFormat(pattern).format(d);
 *    }
 *    ...
 * }
 * </blockquote></pre>
 *
 * We can register this class with a JXPathContext:
 *
 * <pre><blockquote>
 * context.setFunctions(new ClassFunctions(Formats.class, "format"));
 * ...
 *
 * context.getVariables().declareVariable("today", new Date());
 * String today = (String)context.getValue("format:date($today, 'MM/dd/yyyy')");
 *
 * </blockquote></pre>
 * You can also register whole packages of Java classes using PackageFunctions.
 * <p>
 * Also, see {@link FunctionLibrary FunctionLibrary}, which is a class
 * that allows you to register multiple sets of extension functions with
 * the same JXPathContext.
 *
 * <h2>Configuring JXPath</h2>
 *
 * JXPath uses JavaBeans introspection to discover properties of JavaBeans.
 * You can provide alternative property lists by supplying
 * custom JXPathBeanInfo classes (see {@link JXPathBeanInfo JXPathBeanInfo}).
 *
 * <h2>Notes</h2>
 * <ul>
 * <li>JXPath does not support DOM attributes for non-DOM objects. Even though XPaths
 *     like "para[@type='warning']" are legitimate, they will always produce empty results.
 *     The only attribute supported for JavaBeans is "name".  The XPath "foo/bar" is
 *     equivalent to "foo[@name='bar']".
 * <li>The current version of JXPath does not support the <code>id(string)</code>
 *     and <code>key(key, value)</code> XPath functions.
 * </ul>
 *
 * See <a href="http://www.w3schools.com/xpath">XPath Tutorial by W3Schools</a><br>
 * Also see <a href="http://www.w3.org/TR/xpath">XML Path Language (XPath) Version 1.0 </a>
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.9 $ $Date: 2002/05/08 23:19:31 $
 */
public abstract class JXPathContext {
    protected JXPathContext parentContext;
    protected Object contextBean;
    protected Variables vars;
    protected Functions functions;
    protected AbstractFactory factory;
    protected Locale locale;
    protected boolean lenient = false;

    private static JXPathContext compilationContext;

    /**
     * Creates a new JXPathContext with the specified bean as the root node.
     */
    public static JXPathContext newContext(Object contextBean){
        return JXPathContextFactory.newInstance().newContext(null, contextBean);
    }

    /**
     * Creates a new JXPathContext with the specified bean as the root node and the
     * specified parent context. Variables defined in a parent context can be
     * referenced in XPaths passed to the child context.
     */
    public static JXPathContext newContext(JXPathContext parentContext, Object contextBean){
        return JXPathContextFactory.newInstance().newContext(parentContext, contextBean);
    }

    /**
     * This constructor should remain protected - it is to be overridden by subclasses,
     * but never explicitly invoked by clients.
     */
    protected JXPathContext(JXPathContext parentContext, Object contextBean){
        this.parentContext = parentContext;
        this.contextBean = contextBean;
    }

    /**
     * Returns the parent context of this context or null.
     */
    public JXPathContext getParentContext(){
        return parentContext;
    }

    /**
     * Returns the JavaBean associated with this context.
     */
    public Object getContextBean(){
        return contextBean;
    }

    public void setVariables(Variables vars){
        this.vars = vars;
    }

    /**
     * Returns the variable pool associated with the context. If no such
     * pool was specified during the construction of the context,
     * returns the default implementation of Variables, {@link BasicVariables BasicVariables}.
     */
    public Variables getVariables(){
        if (vars == null){
            vars = new BasicVariables();
        }
        return vars;
    }

    public void setFunctions(Functions functions){
        this.functions = functions;
    }

    public Functions getFunctions(){
        // TBD: default lib
        return functions;
    }

    public void setFactory(AbstractFactory factory){
        this.factory = factory;
    }

    /**
     * Returns the AbstractFactory installed on this context.
     * If none has been installed, it calls getFactory() on
     * the parent context.
     */
    public AbstractFactory getFactory(){
        if (factory == null && parentContext != null){
            return parentContext.getFactory();
        }
        return factory;
    }

    /**
     * Set the locale for this context.  The value of the "lang"
     * attribute as well as the the lang() function will be
     * affected by the locale.  By default, JXPath uses
     * <code>Locale.getDefault()</code>
     */
    public void setLocale(Locale locale){
        this.locale = locale;
    }

    /**
     * Returns the locale set with setLocale or Locale.getDefault()
     * by default.
     */
    protected Locale getLocale(){
        if (locale == null){
            locale = Locale.getDefault();
        }
        return locale;
    }

    /**
     * If the context is in the lenient mode, then getValue() returns null
     * for inexistent paths.  Otherwise, a path that does not map to
     * an existing property will throw an exception.  Note that if the
     * property exists, but its value is null, the exception is <i>not</i>
     * thrown.
     * <p>
     * By default, lenient = false
     */
    public void setLenient(boolean lenient){
        this.lenient = lenient;
    }

    /**
     * @see #setLenient(boolean)
     */
    public boolean isLenient(){
        return lenient;
    }

    /**
     * Compiles the supplied XPath and returns an internal representation
     * of the path that can then be evaluated.  Use CompiledExpressions
     * when you need to evaluate the same expression multiple times
     * and there is a convenient place to cache CompiledExpression
     * between invocations.
     */
    public static CompiledExpression compile(String xpath){
        if (compilationContext == null){
            compilationContext = JXPathContext.newContext(null);
        }
        return compilationContext.compilePath(xpath);
    }

    /**
     * Overridden by each concrete implementation of JXPathContext
     * to perform compilation.
     */
    protected abstract CompiledExpression compilePath(String xpath);

    /**
     * Evaluates the xpath and returns the resulting object. Primitive
     * types are wrapped into objects.
     */
    public abstract Object getValue(String xpath);

    /**
     * Evaluates the xpath, converts the result to the specified class and
     * returns the resulting object.
     */
    public abstract Object getValue(String xpath, Class requiredType);

    /**
     * Modifies the value of the property described by the supplied xpath.
     * Will throw an exception if one of the following conditions occurs:
     * <ul>
     * <li>The xpath does not in fact describe an existing property
     * <li>The property is not writable (no public, non-static set method)
     * </ul>
     */
    public abstract void setValue(String xpath, Object value);


    /**
     * Creates missing elements of the path by invoking an AbstractFactory,
     * which should first be installed on the context by calling "setFactory".
     * <p>
     * Will throw an exception if the AbstractFactory fails to create
     * an instance for a path element.
     */
    public abstract Pointer createPath(String xpath);

    /**
     * The same as setValue, except it creates intermediate elements of
     * the path by invoking an AbstractFactory, which should first be
     * installed on the context by calling "setFactory".
     * <p>
     * Will throw an exception if one of the following conditions occurs:
     * <ul>
     * <li>Elements of the xpath aleady exist, but the path does not in
     *  fact describe an existing property
     * <li>The AbstractFactory fails to create an instance for an intermediate
     * element.
     * <li>The property is not writable (no public, non-static set method)
     * </ul>
     */
    public abstract Pointer createPathAndSetValue(String xpath, Object value);

    /**
     * Removes the element of the object graph described by the xpath.
     */
    public abstract void removePath(String xpath);

    /**
     * Removes all elements of the object graph described by the xpath.
     */
    public abstract void removeAll(String xpath);

    /**
     * @deprecated please use createPathAndSetValue(xpath, value)
     */
    public void createPath(String xpath, Object value){
        createPathAndSetValue(xpath, value);
    }

    /**
     * @deprecated Please use iterate
     */
    public List eval(String xpath){
        ArrayList list = new ArrayList();
        Iterator it = iterate(xpath);
        while (it.hasNext()){
            list.add(it.next());
        }
        return list;
    }

    /**
     * Traverses the xpath and returns a Iterator of all results found
     * for the path. If the xpath matches no properties
     * in the graph, the Iterator will not be null.
     */
    public abstract Iterator iterate(String xpath);


    /**
     * @deprecated Please use getPointer(String xpath)
     */
    public Pointer locateValue(String xpath){
        return getPointer(xpath);
    }

    /**
     * Traverses the xpath and returns a Pointer.
     * A Pointer provides easy access to a property.
     * If the xpath matches no properties
     * in the graph, the pointer will be null.
     */
    public abstract Pointer getPointer(String xpath);

    /**
     * @deprecated Please use iteratePointers
     */
    public List locate(String xpath){
        ArrayList list = new ArrayList();
        Iterator it = iteratePointers(xpath);
        while (it.hasNext()){
            list.add(it.next());
        }
        return list;
    }

    /**
     * Traverses the xpath and returns an Iterator of Pointers.
     * A Pointer provides easy access to a property.
     * If the xpath matches no properties
     * in the graph, the Iterator be empty, but not null.
     */
    public abstract Iterator iteratePointers(String xpath);
}