package com.kgolembniak.carrot.filter.fi.algorithm;

import junit.framework.TestCase;

import com.dawidweiss.carrot.core.local.linguistic.tokens.TypedToken;
import com.dawidweiss.carrot.util.tokenizer.languages.MutableStemmedToken;

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
