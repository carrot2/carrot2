
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

package org.carrot2.filter.haog.fi.algorithm;

import junit.framework.TestCase;

import org.carrot2.core.linguistic.tokens.TypedToken;
import org.carrot2.util.tokenizer.languages.MutableStemmedToken;

public class TestTransaction extends TestCase {
	
	private Transaction transaction;
	
	protected void setUp() throws Exception {
		super.setUp();
		this.transaction = new Transaction(); 
	}

	protected void tearDown() throws Exception {
		this.transaction = null; 
		super.tearDown();
	}
	
	public void testAddSentencePart(){
		MutableStemmedToken token = new MutableStemmedToken();
		token.assign("dogs", TypedToken.TOKEN_TYPE_TERM);
		token.setStem("dog");
		transaction.addSentencePart(token);
		
		assertEquals(token.getImage(), transaction.getSentence().get(0));
		assertEquals(0, transaction.getStems().size());
	}
	
	public void testAddWord(){
		MutableStemmedToken token = new MutableStemmedToken();
		token.assign("dogs", TypedToken.TOKEN_TYPE_TERM);
		token.setStem("dog");
		transaction.addWord(token);
		
		assertEquals(token.getImage(), transaction.getSentence().get(0));
		assertEquals(token.getImage() , transaction.getWordsMap().get(token.getStem()));
		assertEquals(token.getStem() , transaction.getReverseMap().get(token.getImage()));
	}

}
