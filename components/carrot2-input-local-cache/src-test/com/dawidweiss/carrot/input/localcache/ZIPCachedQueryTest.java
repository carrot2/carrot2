/*
 * Carrot2 Project Copyright (C) 2002-2004, Dawid Weiss Portions (C)
 * Contributors listen in carrot2.CONTRIBUTORS file. All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder of CVS
 * checkout or at: http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */
package com.dawidweiss.carrot.input.localcache;

import java.io.File;
import java.io.InputStream;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import junit.framework.TestCase;

/**
 * Tests the accessor class on a couple of sample data files.
 * 
 * @author Dawid Weiss
 */
public class ZIPCachedQueryTest extends TestCase {
	
	public ZIPCachedQueryTest(String s) {
		super(s);
	}

	public void testSampleCachedDataFile() throws Exception {
		ZIPCachedQuery query = new ZIPCachedQuery(new File("cached" + File.separator + "sample-cached.gz"));

		assertEquals("carrot2.input.snippet-reader.google", query.getComponentId());
		assertEquals("george bush", query.getQuery());
		
		// attempt to read and parse the query XML.
		InputStream input = query.getData();
		SAXReader reader = new SAXReader();
		Element root = reader.read( input ).getRootElement();
		assertEquals( "searchresult", root.getName() );
		input.close();
	}

}
