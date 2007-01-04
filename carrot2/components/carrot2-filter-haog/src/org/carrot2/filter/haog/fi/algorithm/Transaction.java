
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.carrot2.core.linguistic.tokens.StemmedToken;

/**
 * This class represents transaction. Transaction in case of snippets are
 * sentences. Transactions are further used for cluster description creation.
 * @author Karol Gołembniak
 */
public class Transaction {
	
	/**
	 * List of words in a single snipet sentence. Holds words sequence.
	 */
	private List sentence;
	/**
	 * List of pairs stem -> word. Stems are used to compare words between
	 * baskets and transactions. Words are used for human-friendly description
	 * creation.
	 */
	private Map words;
	/**
	 * List of pairs word -> stem. This map is used for faster word -> stem 
	 * conversion.
	 */
	private Map reverse;
	/**
	 * Word basket connected with this transaction.
	 */
	private WordBasket basket;
	
	/**
	 * Default constructor.
	 */
	public Transaction(){
		this.sentence = new ArrayList();
		this.words = new HashMap();
		this.reverse = new HashMap();
	}
	
	/**
	 * This method adds a non-significant word (stopword, delimiter) from given
	 * token to list of sentence words.
	 * @param token - Token from stemmer
	 */
	public void addSentencePart(StemmedToken token){
		this.sentence.add(token.getImage());
	}

	/**
	 * This method adds a significant word from given token to list of sentence
	 * words and to maps.
	 * @param token - Token from stemmer
	 */
	public void addWord(StemmedToken token){
		this.sentence.add(token.getImage());
		this.words.put(token.getStem(), token.getImage());
		this.reverse.put(token.getImage(), token.getStem());
	}
	
	/**
	 * Method for retrieving stems from this transaction.
	 * @return Returns set of stems for this transaction. 
	 */
	public Set getStems(){
		return this.words.keySet();
	}
	
	/**
	 * This method checks if this transaction contains all elementf form given
	 * ItemSet.
	 * @param items - ItemSet to check 
	 * @return true if transaction contains all elements from ItemSet, false
	 * otherwise.
	 */
	public boolean containsAll(ItemSet items){
		return this.words.keySet().containsAll(items);
	}
	
	/**
	 * Getter for {@link #basket} field.
	 * @return basket connected with this transaction.
	 */
	public WordBasket getBasket() {
		return basket;
	}

	/**
	 * Setter for {@link #basket} field.
	 * @param basket - WordBasket for this transaction.
	 */
	public void setBasket(WordBasket basket) {
		this.basket = basket;
	}

	/**
	 * Checks if this transaction has any significant words.
	 * @return true if transaction has significant words, 
	 * false otherwise.
	 */
	public boolean hasWords() {
		return this.words.size() > 0;
	}

	/**
	 * Gets sentence connected with this transaction.
	 * @return Sentence as a list of words.
	 */
	public List getSentence() {
		return sentence;
	}

	/**
	 * Getter for {@link #reverse} field
	 * @return Reverse map of words.
	 */
	public Map getReverseMap() {
		return reverse;
	}

	/**
	 * Getter for {@link #words} field
	 * @return Wap of words.
	 */
	public Map getWordsMap() {
		return words;
	}
}
