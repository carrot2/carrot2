
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
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents itemset in Apriori algorithm. This set id based on
 * TreeSet and it holds natural order of contained elements.  
 * @author Karol Gołembniak
 */
public class ItemSet extends TreeSet {

	/**
	 * Support for this itemset. Support is number of occurences of all items
	 * in this set in all documents. Initial support is 0.
	 */
	private int support;

	/**
	 * Constructor for this class.
	 */
	public ItemSet(){
		this.support = 0;
	}

	/**
	 * Getter for {@link #support} field.
	 * @return
	 */
	public int getSupport() {
		return support;
	}

	/**
	 * Increases support for this itemset.
	 * @return support after increasing
	 */
	public int incSupport(){
		return this.support++;
	}
	
	/**
	 * Gets all items contained in this set without the last one.
	 * @return set of items without last one.
	 */
	public Set getWithoutLast(){
		HashSet items = new HashSet();
		items.addAll(this);
		items.remove(this.last());
		return items;
	}
	
	/**
	 * Gets all n-1 element subsets for this itemset
	 * @return list of subsets
	 */
	public List getSubSets(){
		ArrayList subSets = new ArrayList();
		HashSet subSet;
		for (Iterator it = this.iterator(); it.hasNext(); ){
			subSet = new HashSet(this);
			subSet.remove(it.next());
			subSets.add(subSet);
		}
		return subSets;
	}

}
