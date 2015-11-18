package org.apache.commons.jxpath.ri.model;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.xml.DocumentContainer;

public class JXPath154Test extends JXPathTestCase {

    protected JXPathContext context;

    protected DocumentContainer createDocumentContainer(String model) {
        return new DocumentContainer(JXPathTestCase.class.getResource("InnerEmptyNamespace.xml"), model);
    }

    protected void doTest(String path, String model, String expectedValue) {
        JXPathContext context = JXPathContext.newContext(createDocumentContainer(model));
        assertEquals(expectedValue, context.getPointer(path).asPath());
    }

    public void testInnerEmptyNamespaceDOM() {
        doTest("b:foo/test", DocumentContainer.MODEL_DOM, "/b:foo[1]/test[1]");
    }

    public void testInnerEmptyNamespaceJDOM() {
        doTest("b:foo/test", DocumentContainer.MODEL_JDOM, "/b:foo[1]/test[1]");
    }
}
