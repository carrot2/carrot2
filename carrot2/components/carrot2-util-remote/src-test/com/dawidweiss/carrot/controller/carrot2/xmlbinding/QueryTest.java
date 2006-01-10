
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.controller.carrot2.xmlbinding;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class QueryTest extends TestCase {
    public QueryTest(String s) {
        super(s);
    }

    public void testUnmarshal() throws Exception {
        SAXReader reader = new SAXReader();
        Element root = reader.read(this.getClass().getResourceAsStream("query.xml")).getRootElement();
        Query q = Query.unmarshal(root);
        assertNotNull(q);
        assertEquals("test-query", q.getContent());
        assertTrue(q.hasRequestedResults());
        assertEquals(10, q.getRequestedResults());
    }
    
    public void testMarshall() throws Exception {
        final Query q = new Query("My complex query łóńść!", 101, true);
        final StringWriter writer = new StringWriter();
        q.marshal(writer);
        
        SAXReader reader = new SAXReader();
        Query q2 = Query.unmarshal(reader.read(new StringReader(writer.toString())).getRootElement());
        
        assertEquals(q.getContent(), q2.getContent());
        assertEquals(q.getRequestedResults(), q2.getRequestedResults());
    }
}
