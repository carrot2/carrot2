
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

package com.kgolembniak.carrot.filter.fi.algorithm;

import java.text.Collator;
import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.TokenizedDocument;
import com.dawidweiss.carrot.core.local.linguistic.tokens.TokenSequence;
import com.dawidweiss.carrot.core.local.linguistic.tokens.TypedToken;
import com.dawidweiss.carrot.util.tokenizer.languages.MutableStemmedToken;
import com.kgolembniak.carrot.filter.fi.FIParameters;
import com.kgolembniak.carrot.filter.haog.measure.Statistics;

/**
 * Class containing main logic for Apriori based algorithm. It contains methods
 * for frequent itemsets generation.
 * @author Karol Gołembniak
 */
public class AprioriEngine {
	
	/**
	 * List of all baskets in a query result.
	 */
	private final List baskets;
	/**
	 * List of all transactions in a query result.
	 */
	private final List transactions;
	/**
	 * Set of all words in a query result.
	 */
	private final Set words;
	/**
	 * List of frequent itemsets generated in previous iteration.
	 */
	private final List previousItemSets;
	/**
	 * List of all frequent itemsets generated for this query results.
	 */
	private final List frequentItemSets;
	/**
	 * Parameters for FI algorithm. 
	 */
	private FIParameters parameters;
	/**
	 * Frequent sets generation start time.
	 */
	private long startTime;
	/**
	 * Words used in frequent sets.
	 */
	private HashSet usedWords;
	
	/**
	 * Default constructor. 
	 */
	public AprioriEngine(){
		this.baskets = new ArrayList();
		this.transactions = new ArrayList();
		this.frequentItemSets = new ArrayList();
		this.previousItemSets = new ArrayList();
		this.words = new HashSet();
		this.usedWords = new HashSet();
	}
	
	/**
	 * This method releases resources connected with this object.
	 */
	public void flushResources(){
		this.frequentItemSets.clear();
		this.baskets.clear();
		this.words.clear();
		this.usedWords.clear();
	}
	
	/**
	 * This method adds document for processing. 
	 * WordBasket and Transaction objects are created from given document.
	 * @param document - Document to add to processing.
	 */
	public void addTokenizedDocument(TokenizedDocument document){
        final WordBasket basket = new WordBasket(document);
        getTransactionsFromDocument(document, basket);
        this.baskets.add(basket);
        this.words.addAll(basket.getWords());
	}
	
	/**
	 * This method creates transactions grom given document.
	 * @param document - Document to process
	 * @param basket - WordBasket connected with this document.
	 */
	private void getTransactionsFromDocument(TokenizedDocument document,
	WordBasket basket) {
		TokenSequence title = document.getTitle();
		getTransactions(title, basket);
		TokenSequence sequence = document.getSnippet();
		getTransactions(sequence, basket);		
	}
	
	/**
	 * Gets transactions from given token sequence and adds created transaction
	 * to {@link #transactions} list.
	 * @param sequence - Sequence of tokens
	 * @param basket - Basket connected with created transactions
	 */
	private void getTransactions(TokenSequence sequence, WordBasket basket){
		short type;
		MutableStemmedToken token;
		Transaction transaction = new Transaction();
		boolean addTransaction = false;
		for (int i1=0; i1<sequence.getLength(); i1++){
			token = (MutableStemmedToken) sequence.getTokenAt(i1);
			type = token.getType();
			addTransaction = true;

			if ((type & TypedToken.TOKEN_FLAG_SENTENCE_DELIM) ==
				TypedToken.TOKEN_FLAG_SENTENCE_DELIM){
				transaction.addSentencePart(token);
				if (transaction.hasWords()){
					transaction.setBasket(basket);
					this.transactions.add(transaction);
					addTransaction = false;
				}
				transaction = new Transaction();
			} else if (((token.getType() & TypedToken.TOKEN_TYPE_TERM) ==
				TypedToken.TOKEN_TYPE_TERM) && 
				((token.getType() & TypedToken.TOKEN_FLAG_STOPWORD)!=
				TypedToken.TOKEN_FLAG_STOPWORD)){
				transaction.addWord(token);
			} else {
				transaction.addSentencePart(token);
			}
		}
		if (addTransaction && transaction.hasWords()) {
			transaction.setBasket(basket);
			this.transactions.add(transaction);
		}
	}

	/**
	 * Checks all baskets for containing all elements from given itemset
	 * and counts support for this itemset.
	 * @param itemSet - Itemset which support to count.
	 * @return Support for given itemset.
	 */
	private double getItemSetSupport(ItemSet itemSet){
		WordBasket basket;
		for (int i1=0; i1<baskets.size(); i1++){
			basket = (WordBasket) baskets.get(i1);
			if (basket.containsAll(itemSet)){
				itemSet.incSupport();
			}
		}

		return (double) itemSet.getSupport() / baskets.size();
	}
	
	/**
	 * Generates initial (one element) frequent itemsets from all collected
	 * words and adds them to {@link frequentItemSets} and 
	 * {@link #previousItemSets} lists.
	 */
	private void generateInitialItemSet(){
		this.words.remove(null);
		ItemSet itemSet;
		String word;
		for (Iterator it = words.iterator(); it.hasNext();){
			word = (String) it.next();
			itemSet = new ItemSet();
			itemSet.add(word);

			final double support = getItemSetSupport(itemSet);
			if (( support >= parameters.getMinSupport())&&
				(support <= parameters.getIgnoreWordIfInHigherDocsPercent())){
				previousItemSets.add(itemSet);
				frequentItemSets.add(itemSet);
				usedWords.addAll(itemSet);
			}
		}
	}

	/**
	 * Generates further (having mote than one element) frequent itemsets from
	 * previous generated itemsets and adds them to {@link #frequentItemSets} 
	 * and {@link #previousItemSets} lists. PreviousItemSets list is cleared
	 * before adding itemsts generated in one iteration. 
	 */
	private void generateFurtherItemSets(){
		HashSet candidates = new HashSet();
		ItemSet firstSet;
		ItemSet secondSet;
		ItemSet newSet;
Timeout:for (int i1=0; i1<previousItemSets.size(); i1++){
			firstSet = (ItemSet) previousItemSets.get(i1);
			for (int i2=0; i2<previousItemSets.size(); i2++){
				if (System.currentTimeMillis() - startTime > 
					parameters.getMaxItemSetsGenerationTime() * 1000){
					break Timeout;
				}
				if (i1!=i2){
					secondSet = (ItemSet) previousItemSets.get(i2);
					if (combineSets(firstSet, secondSet)){
						newSet = new ItemSet();
						newSet.addAll(firstSet);
						newSet.add(secondSet.last());
						if (previousItemSets.containsAll(newSet.getSubSets()) && 
							getItemSetSupport(newSet) >= parameters.getMinSupport()){
							candidates.add(newSet);
							usedWords.addAll(newSet);
						}
					}
				}
			}
		}
		previousItemSets.clear();
		previousItemSets.addAll(candidates);
		frequentItemSets.addAll(candidates);
	}

	/**
	 * According to R. Agraval, R. Srikant article <i>"Fast Algorithms for 
	 * Mining Association Rules"</i> frequent itemset generation can be 
	 * described like this:<br/>
	 * 	
	 * <br/><b>
	 *  insert into Ck<br/>
	 * 	select p.item1, p.item2, ..., p.itemk1, q.itemk1<br/>
	 * 	from Lk-1 p, Lk-1 q<br/>
	 * 	where p.item1 = q.item1, . . ., p.itemk2 = q.itemk2, p.itemk1 < q.itemk1;<br/>
	 * <br/></b>
	 *
	 * This method checks if two itemsets can be joined to get new, bigger 
	 * itemset.
	 * @param firstSet - First itemset to join.
	 * @param secondSet - Second itemset to join.
	 * @return true if itemsets can bo joined, false otherwise.
	 */
	private boolean combineSets(ItemSet firstSet, ItemSet secondSet) {
		Set firstSubset = firstSet.getWithoutLast();
		Set secondSubset = secondSet.getWithoutLast();
		Comparator comparator = Collator.getInstance(Locale.US);
		
		if (firstSubset.equals(secondSubset) &&
			(comparator.compare(firstSet.last(), secondSet.last()) < 0)){
			return true;
		}

		return false;
	}

	/**
	 * This method generates all frequent itemsets.
	 */
	private void generateItemSets(){
		startTime = System.currentTimeMillis();
		generateInitialItemSet();
		while (previousItemSets.size()>0){
			generateFurtherItemSets();
		}
	}
	
	/**
	 * Creates corresponding clusters from generated itemsets.
	 * @return List of clusters.
	 */
	private List createClusters() {
		ArrayList clusters = new ArrayList();
		Cluster cluster;
		ItemSet itemSet;
		for (int i1=0; i1<frequentItemSets.size(); i1++){
			if (i1 > parameters.getMaxPresentedClusters()) {
				break;
			}
			itemSet = (ItemSet) frequentItemSets.get(i1);
			cluster = getClusterFromItemSet(itemSet);
			if (cluster.getDocuments().size() >= 
				parameters.getMinClusterSize()){
				cluster.setId(new Integer(clusters.size()));
				clusters.add(cluster);
			}
		}
		return clusters;
	}

	/**
	 * Creates cluster from given itemset.
	 * @param itemSet - Itemset to create cluster from.
	 * @return Created cluster.
	 */
	private Cluster getClusterFromItemSet(ItemSet itemSet) {
		WordBasket basket;
		Cluster cluster = null;
		for (int i1=0; i1<baskets.size(); i1++){
			basket = (WordBasket) baskets.get(i1);
			if (basket.containsAll(itemSet)){
				if (cluster==null){
					cluster = new Cluster(getClusterDescription(itemSet, basket));
				}
				cluster.addDocument(basket.getDocument());
			}
		}
		return cluster;
	}
	
	/**
	 * Gets description for a cluster, which is created from given itemset. 
	 * Basket parameter is used to get transaction (sentence) which can be 
	 * used for description creation.
	 * @param itemSet - Itemset on which this cluster is based.
	 * @param basket - WordBasket containing all items from itemSet.
	 * @return List of strings representing cluster's description.
	 */
	private List getClusterDescription(ItemSet itemSet, WordBasket basket) {
		
		Transaction transaction = null;
		Transaction fitting = null;
		for(int i1=0; i1<transactions.size(); i1++){
			transaction = (Transaction) transactions.get(i1);
			if ((transaction.getBasket() == basket) &&
				(transaction.containsAll(itemSet))){
				fitting = transaction;
			}
		}

		ArrayList description = new ArrayList();
		if (fitting != null){
			HashSet items = new HashSet(itemSet);
			List sentence = fitting.getSentence();
			Map reverseMap = fitting.getReverseMap();
			String stem = null;
			String word = null;
			boolean addWord = false;
			for (int i1=0; i1<sentence.size(); i1++){
				word = (String) sentence.get(i1);
				stem = (String) reverseMap.get(word);
				if (items.size()<=0) {
					break;
				}
				if (stem!=null) {
					if (items.contains(stem)) {
						addWord = true;
						items.remove(stem);
						description.add(word);
					} else {
						if (addWord && (!usedWords.contains(stem))){
							description.add(word);
						}
					}
				} else {
					if (addWord && (!word.matches("[\\p{Punct}]"))) {
						description.add(word);
					}
				}
			}
		} else {
			description.addAll(basket.getDescriptionForItemSet(itemSet));
		}
		
		return description;
	}

	/**
	 * Adds links between two clusters if they have sufficent common documents 
	 * ratio.
	 * @param clusters - List of clusters to connect.
	 * @return List of clusters after connecting.
	 */
	private List connectClusters(List clusters) {
		Cluster firstCluster;
		Cluster secondCluster;
		int intersectionSize;
		for (int i1=0; i1<clusters.size(); i1++){
			firstCluster = (Cluster) clusters.get(i1);

			for (int i2=0; i2<i1; i2++){
				secondCluster = (Cluster) clusters.get(i2);
				
				intersectionSize = firstCluster
					.getIntersectionSize(secondCluster);
				
				if (intersectionSize / secondCluster.getDocuments().size() >=
					parameters.getLinkThreshold()) {
					firstCluster.addNeighbour(secondCluster);
				}
				
				if (intersectionSize / firstCluster.getDocuments().size() >= 
					parameters.getLinkThreshold()) {
					secondCluster.addNeighbour(firstCluster);
				}
			}
		}
		return clusters;
	}

	/**
	 * Gets clusters created by Apriori based algorithm.
	 * @param params - Parameters for algorithm.
	 * @return - List of cluster created by Apriori based algorithm.
	 */
	public List getClusters(FIParameters params){
		this.parameters = params;

		generateItemSets();
		List clusters = createClusters();

		Statistics.getInstance().startTimer();
		clusters = connectClusters(clusters);
        Statistics.getInstance().endTimer("Cluster Connecting Time (Should be removed from FI cluster creation time, this time is haog part)");

		return clusters;
	}

}
