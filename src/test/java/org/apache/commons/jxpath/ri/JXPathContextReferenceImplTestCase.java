package org.apache.commons.jxpath.ri;

import junit.framework.TestCase;

import org.apache.commons.jxpath.ri.model.container.ContainerPointerFactory;

public class JXPathContextReferenceImplTestCase extends TestCase {

    /**
     * https://issues.apache.org/jira/browse/JXPATH-166
     */
    public void testInit() {
        JXPathContextReferenceImpl.addNodePointerFactory(new ContainerPointerFactory());
    }
}
