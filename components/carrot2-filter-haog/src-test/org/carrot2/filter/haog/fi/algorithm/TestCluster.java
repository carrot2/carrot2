
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.haog.fi.algorithm;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.carrot2.core.clustering.RawDocumentSnippet;
import org.carrot2.core.clustering.TokenizedDocument;
import org.carrot2.util.tokenizer.SnippetTokenizer;

public class TestCluster extends TestCase {
	private Cluster firstCluster;
	private Cluster secondCluster;
	

	protected void setUp() throws Exception {
		super.setUp();
		ArrayList firstDesc = new ArrayList(1);
		firstDesc.add("Cluster 1 description");
		ArrayList secondDesc = new ArrayList(1);
		secondDesc.add("Cluster 2 description");
		this.firstCluster = new Cluster(firstDesc);
		this.secondCluster = new Cluster(secondDesc);
	}

	protected void tearDown() throws Exception {
		this.firstCluster = null;
		this.secondCluster = null;
		super.tearDown();
	}

	
	public void testGetIntersectionSize(){
		SnippetTokenizer tokenizer = new SnippetTokenizer();
		TokenizedDocument document = null;
		
		document = tokenizer.tokenize(new RawDocumentSnippet("Title1", "Snippet1"));
		firstCluster.addDocument(document);
		document = tokenizer.tokenize(new RawDocumentSnippet("Title2", "Snippet2"));
		firstCluster.addDocument(document);
		secondCluster.addDocument(document);
		document = tokenizer.tokenize(new RawDocumentSnippet("Title3", "Snippet3"));
		firstCluster.addDocument(document);
		document = tokenizer.tokenize(new RawDocumentSnippet("Title4", "Snippet4"));
		firstCluster.addDocument(document);
		secondCluster.addDocument(document);
		document = tokenizer.tokenize(new RawDocumentSnippet("Title5", "Snippet5"));
		secondCluster.addDocument(document);
		document = tokenizer.tokenize(new RawDocumentSnippet("Title6", "Snippet6"));
		secondCluster.addDocument(document);
		document = tokenizer.tokenize(new RawDocumentSnippet("Title7", "Snippet7"));
		secondCluster.addDocument(document);
		firstCluster.addDocument(document);
		document = tokenizer.tokenize(new RawDocumentSnippet("Title8", "Snippet8"));
		firstCluster.addDocument(document);
		
		final int int1 = firstCluster.getIntersectionSize(secondCluster);
		final int int2 = secondCluster.getIntersectionSize(firstCluster);
		
		assertEquals(int1, int2);
		assertTrue(int1==3);
	}
}
