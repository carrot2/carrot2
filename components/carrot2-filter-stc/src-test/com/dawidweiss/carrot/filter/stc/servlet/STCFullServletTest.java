package com.dawidweiss.carrot.filter.stc.servlet;

import java.io.InputStream;
import java.util.*;

import org.dom4j.Element;

import com.dawidweiss.carrot.mocktests.FilterRequestProcessorTestBase;

/**
 * Tests the {@link com.dawidweiss.carrot.filter.stc.servlet.STCFullServlet} servlet using
 * mock objects.
 *  
 * @author Dawid Weiss
 */
public class STCFullServletTest extends FilterRequestProcessorTestBase {
    public STCFullServletTest(String s) {
        super(s);
    }

    protected String getFilterRequestProcessorClassName() {
        return STCFullServlet.class.getName();
    }

    /**
     * Tests the remote servlet using mock objects and a cached
     * query.
     */
    public void testRemoteServlet() throws Exception {
        final HashMap params = new HashMap();
        final InputStream is = this.getClass().getResourceAsStream("data-mining.xml");
        getServletInstance().getFilterRequestProcessor().processFilterRequest(
                is, getRequest(), getResponse(), params);

        // check the output.
        final Element result = parseOutputXml();
        final List groups = result.elements("group");
        assertTrue(groups.size() > 10);
        for (Iterator i = groups.iterator(); i.hasNext();) {
            final Element titleElem = ((Element) i.next()).element("title");
            final List phraseElems = titleElem.elements("phrase");
            assertTrue(phraseElems.size() > 0);
        }
    }
}
