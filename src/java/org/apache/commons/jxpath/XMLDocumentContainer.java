/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/XMLDocumentContainer.java,v 1.3 2002/04/24 03:30:17 dmitri Exp $
 * $Revision: 1.3 $
 * $Date: 2002/04/24 03:30:17 $
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

import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
 * @version $Revision: 1.3 $ $Date: 2002/04/24 03:30:17 $
 */
public class XMLDocumentContainer implements Container {

    private Node document;
    private URL xmlURL;
    private Source source;

    /**
     * @param URL is a URL for an XML file. Use getClass().getResource(resourceName)
     * to load XML from a resource file.
     */
    public XMLDocumentContainer(URL xmlURL){
        this.xmlURL = xmlURL;
        if (xmlURL == null){
            throw new RuntimeException("Source is null");
        }
    }

    public XMLDocumentContainer(Source source){
        this.source = source;
        if (source == null){
            throw new RuntimeException("Source is null");
        }
    }

    /**
     * Reads XML, caches it internally and returns the Document.
     */
    public Object getValue(){
        if (document == null){
            try {
                InputStream stream = null;
                try {
                    if (xmlURL != null){
                        stream = xmlURL.openStream();
                        source = new StreamSource(stream);
                    }
                    DOMResult result = new DOMResult();
                    Transformer trans = TransformerFactory.newInstance().newTransformer();
                    trans.transform(source, result);
                    document = (Document) result.getNode();
                }
                finally {
                    if (stream != null){
                        stream.close();
                    }
                }
            }
            catch (Exception ex){
                throw new RuntimeException("Cannot read XML from: " +
                    (xmlURL != null ? xmlURL.toString() :
                        (source != null ? source.getSystemId() : "<<undefined source>>")) + "\n" + ex);
            }
        }
        return document;
    }

    /**
     * Throws an UnsupportedOperationException
     */
    public void setValue(Object value){
        throw new UnsupportedOperationException();
    }
}