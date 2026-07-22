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

package org.apache.commons.jxpath.xml;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.Document;

/**
 * Tests that the XML parsers do not resolve external entities (XXE).
 */
class XXETest {

    @TempDir
    Path dir;

    private URL buildDocument() {
        try {
            final Path secret = dir.resolve("secret.txt");
            Files.write(secret, "TOP-SECRET".getBytes(StandardCharsets.UTF_8));
            final Path xml = dir.resolve("xxe.xml");
            Files.write(xml, ("<?xml version=\"1.0\"?>\n"
                    + "<!DOCTYPE root [ <!ENTITY xxe SYSTEM \"" + secret.toUri() + "\"> ]>\n"
                    + "<root>&xxe;</root>").getBytes(StandardCharsets.UTF_8));
            return xml.toUri().toURL();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String rootText(final String model, final Object document) {
        if (DocumentContainer.MODEL_JDOM.equals(model)) {
            return ((org.jdom.Document) document).getRootElement().getText();
        }
        return ((Document) document).getDocumentElement().getTextContent();
    }

    @ParameterizedTest
    @ValueSource(strings = {DocumentContainer.MODEL_DOM, DocumentContainer.MODEL_JDOM})
    void externalEntityIsNotResolved(final String model) {
        final DocumentContainer container = new DocumentContainer(buildDocument(), model);
        assertFalse(rootText(model, container.getValue()).contains("TOP-SECRET"));
    }
}
