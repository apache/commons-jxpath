/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jxpath.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Tests <a href="https://issues.apache.org/jira/browse/JXPATH-207">JXPATH-207</a>: {@link Pointer#asPath()} of a namespaced DOM element without a usable
 * prefix must resolve back to the same node.
 */
class JXPath207Test {

    private static JXPathContext newContext(final String xml) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        final Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        return JXPathContext.newContext(document);
    }

    @Test
    void testAsPathCountsPrecedingTextNode() throws Exception {
        final JXPathContext context = newContext("<root>before<item xmlns=\"urn:test\"/></root>");
        final Pointer pointer = context.getPointer("/root/*");
        assertEquals("/root[1]/node()[2]", pointer.asPath());
        assertSame(pointer.getNode(), context.getPointer(pointer.asPath()).getNode());
    }

    @Test
    void testAsPathCountsAllKindsOfPrecedingSiblings() throws Exception {
        final JXPathContext context = newContext("<root>a<!--c--><?pi x?><x xmlns=\"urn:a\"/><![CDATA[b]]><y xmlns=\"urn:b\"/></root>");
        final List<String> paths = new ArrayList<>();
        for (final Pointer pointer : iterate(context, "/root/*")) {
            paths.add(pointer.asPath());
            assertSame(pointer.getNode(), context.getPointer(pointer.asPath()).getNode(), pointer.asPath());
        }
        assertEquals(Arrays.asList("/root[1]/node()[4]", "/root[1]/node()[6]"), paths);
    }

    @Test
    void testAsPathWithoutPrecedingNonElementSiblings() throws Exception {
        final JXPathContext context = newContext("<root><x xmlns=\"urn:a\"/><y xmlns=\"urn:b\"/></root>");
        final Pointer first = (Pointer) context.iteratePointers("/root/*").next();
        assertEquals("/root[1]/node()[1]", first.asPath());
    }

    private static List<Pointer> iterate(final JXPathContext context, final String xpath) {
        final List<Pointer> result = new ArrayList<>();
        context.iteratePointers(xpath).forEachRemaining(pointer -> result.add((Pointer) pointer));
        return result;
    }
}
