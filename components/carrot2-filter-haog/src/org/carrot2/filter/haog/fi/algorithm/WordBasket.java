
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dawidweiss.carrot.core.local.clustering.TokenizedDocument;
import com.dawidweiss.carrot.core.local.linguistic.tokens.TokenSequence;
import com.dawidweiss.carrot.core.local.linguistic.tokens.TypedToken;
import com.dawidweiss.carrot.util.tokenizer.languages.MutableStemmedToken;

/**
 * This class represents basket in Apriori algorithm. In this special case it
 * is a basket of words (java.lang.String). One WordBasket is related with one
 * document and contains all significant words from this document.
 * @author Karol Gołembniak
 */
public class WordBasket {

	/**
	 * Map containing words and their stems as keys.
	 */
	private Map words;
	
	/**
	 * Document related with this basket.
	 */
	private TokenizedDocument document;
	
	/**
	 * This constructor creates WordBasket based on a document.
	 * @param document - Base for this basket.
	 */
	public WordBasket(TokenizedDocument document){
		this.words = new HashMap();
		this.document = document;
		
		TokenSequence title = document.getTitle();
		getWords(title);
		TokenSequence sequence = document.getSnippet();
		getWords(sequence);
	}

	/**
	 * Retrieves words from sequence of tokens and puts them to words map.
	 * @param sequence - Sequence of tokens.
	 */
	private void getWords(TokenSequence sequence) {
		MutableStemmedToken token;
		for (int i1=0; i1<sequence.getLength(); i1++){
			token = (MutableStemmedToken) sequence.getTokenAt(i1);
			if (((token.getType() & TypedToken.TOKEN_TYPE_TERM) == TypedToken.TOKEN_TYPE_TERM) &&
				((token.getType() & TypedToken.TOKEN_FLAG_STOPWORD)!= TypedToken.TOKEN_FLAG_STOPWORD)){
				words.put(token.getStem(), token.getImage());
			}
		}
	}
	
	/**
	 * Checks if this basket contains given word.
	 * @param word - word stem to check
	 * @return true if map contains given stem as a key, false elsewere.
	 */
	public boolean contains(String word){
		return words.keySet().contains(word);
	}

	/**
	 * Checks if this basket contains all items from given itemset.
	 * @param itemSet - set of stems to check
	 * @return true if map contains all given stems as a key, false elsewere.
	 */
	public boolean containsAll(ItemSet itemSet){
		return words.keySet().containsAll(itemSet);
	}

	/**
	 * Gets stems contained by this basket.
	 * @return set of stems
	 */
	public Set getWords() {
		return words.keySet();
	}
	
	/**
	 * Gets oryginal words description for itemset. Itemset contains stems, so
	 * they need to be translated to real words. 
	 * @param itemSet - itemset containing stems
	 * @return list of words (strings)
	 */
	public List getDescriptionForItemSet(ItemSet itemSet){
		ArrayList description = new ArrayList();
		for (Iterator it = itemSet.iterator(); it.hasNext();){
			description.add(this.words.get(it.next()));
		}
		return description;
	}

	/**
	 * Getter for {@link #document} field.
	 * @return returns document related with this basket.
	 */
	public TokenizedDocument getDocument() {
		return document;
	}
}
