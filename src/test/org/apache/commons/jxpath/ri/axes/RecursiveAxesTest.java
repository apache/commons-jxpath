package org.apache.commons.jxpath.ri.axes;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;

/**
 * Test for the protection mechanism that stops infinite recursion
 * in descent down a recursive graph. 
 */
public class RecursiveAxesTest extends JXPathTestCase {

    private RecursiveBean bean;
    private JXPathContext context;

    public RecursiveAxesTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(RecursiveAxesTest.class);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        bean = new RecursiveBean("zero");
        RecursiveBean bean1 = new RecursiveBean("one");
        RecursiveBean bean2 = new RecursiveBean("two");
        RecursiveBean bean3 = new RecursiveBean("three");
        bean.setFirst(bean1);
        bean1.setFirst(bean2);
        bean2.setFirst(bean1);
        bean2.setSecond(bean3);

        context = JXPathContext.newContext(null, bean);
    }

    public void testInfiniteDescent() {
        // Existing scalar property
        assertXPathPointer(
            context,
            "//.[name = 'three']",
            "/first/first/second");
    }
}

