
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

package org.carrot2.filter.haog.haog.algorithm;

import junit.framework.TestCase;

public class TestVertex extends TestCase {
	
	private Vertex vertex;
	
	protected void setUp() throws Exception {
		super.setUp();
		this.vertex = new Vertex("Vertex");
	}

	protected void tearDown() throws Exception {
		this.vertex = null;
		super.tearDown();
	}
	
	public void testAddPredecessor(){
		Vertex v = new Vertex("Predecessor");
		int beforeSizeBase = this.vertex.getBasePredList().size();
		int beforeSizePred = this.vertex.getPredList().size();
		this.vertex.addPredecessor(v);
		int afterSizeBase = this.vertex.getBasePredList().size();
		int afterSizePred = this.vertex.getPredList().size();
		
		assertEquals(afterSizeBase, beforeSizeBase + 1);
		assertEquals(afterSizePred, beforeSizePred + 1);
		
		CombinedVertex cv = new CombinedVertex("Predecessor");
		beforeSizeBase = this.vertex.getBasePredList().size();
		beforeSizePred = this.vertex.getPredList().size();
		this.vertex.addPredecessor(cv);
		afterSizeBase = this.vertex.getBasePredList().size();
		afterSizePred = this.vertex.getPredList().size();
		
		assertEquals(afterSizeBase, beforeSizeBase);
		assertEquals(afterSizePred, beforeSizePred + 1);
	}

	public void testAddSuccessor(){
		Vertex v = new Vertex("Successor");
		int beforeSizeBase = this.vertex.getBaseSuccList().size();
		int beforeSizeSucc = this.vertex.getSuccList().size();
		this.vertex.addSuccessor(v);
		int afterSizeBase = this.vertex.getBaseSuccList().size();
		int afterSizeSucc = this.vertex.getSuccList().size();
		
		assertEquals(afterSizeBase, beforeSizeBase + 1);
		assertEquals(afterSizeSucc, beforeSizeSucc + 1);
		
		CombinedVertex cv = new CombinedVertex("Successor");
		beforeSizeBase = this.vertex.getBaseSuccList().size();
		beforeSizeSucc = this.vertex.getSuccList().size();
		this.vertex.addSuccessor(cv);
		afterSizeBase = this.vertex.getBaseSuccList().size();
		afterSizeSucc = this.vertex.getSuccList().size();
		
		assertEquals(afterSizeBase, beforeSizeBase);
		assertEquals(afterSizeSucc, beforeSizeSucc + 1);
	}

}
