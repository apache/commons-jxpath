/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/xml/DocumentContainer.java,v 1.8 2004/01/18 01:42:58 dmitri Exp $
 * $Revision: 1.8 $
 * $Date: 2004/01/18 01:42:58 $
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
package org.apache.commons.jxpath.xml;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.jxpath.Container;
import org.apache.commons.jxpath.JXPathException;

/**
 * An XML document container reads and parses XML only when it is
 * accessed.  JXPath traverses Containers transparently -
 * you use the same paths to access objects in containers as you
 * do to access those objects directly.  You can create
 * XMLDocumentContainers for various XML documents that may or
 * may not be accessed by XPaths.  If they are, they will be automatically
 * read, parsed and traversed. If they are not - they won't be
 * read at all.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.8 $ $Date: 2004/01/18 01:42:58 $
 */
public class DocumentContainer extends XMLParser2 implements Container {

    public static final String MODEL_DOM = "DOM";
    public static final String MODEL_JDOM = "JDOM";

    private Object document;
    private URL xmlURL;
    private String model;

    private static HashMap parserClasses = new HashMap();
    static {
        parserClasses.put(MODEL_DOM,
                          "org.apache.commons.jxpath.xml.DOMParser");
        parserClasses.put(MODEL_JDOM,
                          "org.apache.commons.jxpath.xml.JDOMParser");
    }

    private static HashMap parsers = new HashMap();

    /**
     * Add an XML parser.  Parsers for the models "DOM" and "JDOM" are
     * pre-registered.
     */
    public static void registerXMLParser(String model, XMLParser parser) {
        parsers.put(model, parser);
    }

    /**
     * Add a class of a custom XML parser. 
     * Parsers for the models "DOM" and "JDOM" are pre-registered.
     */    
    public static void registerXMLParser(String model, String parserClassName) {
        parserClasses.put(model, parserClassName);
    }

    /**
     * Use this constructor if the desired model is DOM.
     *
     * @param URL is a URL for an XML file.
     * Use getClass().getResource(resourceName) to load XML from a
     * resource file.
     */
    public DocumentContainer(URL xmlURL) {
        this(xmlURL, MODEL_DOM);
    }

    /**
     * @param  URL is a URL for an XML file. Use getClass().getResource
     * (resourceName) to load XML from a resource file.
     *
     * @param model is one of the MODEL_* constants defined in this class. It
     *   determines which parser should be used to load the XML.
     */
    public DocumentContainer(URL xmlURL, String model) {
        this.xmlURL = xmlURL;
        if (xmlURL == null) {
            throw new JXPathException("XML URL is null");
        }
        this.model = model;
    }

    /**
     * Reads XML, caches it internally and returns the Document.
     */
    public Object getValue() {
        if (document == null) {
            try {
                InputStream stream = null;
                try {
                    if (xmlURL != null) {
                        stream = xmlURL.openStream();
                    }
                    document = parseXML(stream);
                }
                finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            }
            catch (Exception ex) {
                throw new JXPathException(
                    "Cannot read XML from: " + xmlURL.toString(),
                    ex);
            }
        }
        return document;
    }

    /**
     * Parses XML using the parser for the specified model.
     */
    public Object parseXML(InputStream stream) {
        XMLParser parser = getParser(model);
        if (parser instanceof XMLParser2) {
            XMLParser2 parser2 = (XMLParser2) parser;
            parser2.setValidating(isValidating());
            parser2.setNamespaceAware(isNamespaceAware());
            parser2.setIgnoringElementContentWhitespace(
                    isIgnoringElementContentWhitespace());
            parser2.setExpandEntityReferences(isExpandEntityReferences());
            parser2.setIgnoringComments(isIgnoringComments());
            parser2.setCoalescing(isCoalescing());
        }
        return parser.parseXML(stream);
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public void setValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Maps a model type to a parser.
     */
    private static final XMLParser getParser(String model) {
        XMLParser parser = (XMLParser) parsers.get(model);
        if (parser == null) {
            String className = (String) parserClasses.get(model);
            if (className == null) {
                throw new JXPathException("Unsupported XML model: " + model);
            }
            try {
                Class clazz = Class.forName(className);
                parser = (XMLParser) clazz.newInstance();                
            }
            catch (Exception ex) {
                throw new JXPathException(
                    "Cannot allocate XMLParser: " + className);
            }
            parsers.put(model, parser);
        }
        return parser;
    }
}