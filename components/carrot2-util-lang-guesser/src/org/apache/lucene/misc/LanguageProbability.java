
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package org.apache.lucene.misc;

/*
 * Copyright 2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *  
 */
 
/**
 * A LanguageProbability encompasses a language and its associated probability
 * The language is an ISO-639 code and the probability should be between
 * 1.0 and 0.0
 *
 * @author Jean-Francois Halleux
 */
public class LanguageProbability implements Comparable {
	
	private final String isoCode;
	private float probability;
	
	/**
	 * Construct a LanguageProbability by specifying its associated isoCode and
	 * probability
	 * 
	 * @param isoCode
	 * @param probability
	 */
	LanguageProbability(String isoCode, float probability) {
		this.isoCode=isoCode;
		this.probability=probability;
	}
	
	/**
	 * Sets the probability associated with this LanguageProbability
	 *  
	 * @param probability the new probability of this LanguageProbability
	 */
	void setProbability(float probability) {
		this.probability=probability;
	}
	
	/**
	 * Returns the ISO Code of the language associated with this LanguageProbability.
	 * 
	 * @return the ISO Code of the language associated with this LanguageProbability
	 */
	public String getIsoCode() {
		return isoCode;
	}
	
	/**
	 * Returns the probability associated with this LanguageProbability.
	 * 
	 * @return the probability associated with this LanguageProbability.
	 */
	public float getProbability() {
		return probability;
	}
	
	/**
	 * Compares this LanguageProbability to another Object.
	 * 
	 * @return -1 if this LanguageProbability probability is smaller than o probability or both probabilities are equal but this 
	 * language code is smalled than o language code. 
	 * 1 if this LanguageProbability is greater than o probability or 
	 * both probabilities are equal but this language code is greater than o language code.
	 * 0 if both this probability and language are equal to o probability and language.
	 * 
	 * @see java.lang.Comparable
	 * 
	 * @param o the LanguageProbability to compare to.
	 */
	public int compareTo(Object o) {
		LanguageProbability other=(LanguageProbability)o;
		if (probability < other.probability) return -1;
		if (probability > other.probability) return 1;
		return isoCode.compareTo(other.isoCode);	
	}
	
	
	/**
	 * Returns a String representation of this LanguageProbability
	 * 
	 * @return a String representation of this LanguageProbability
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return isoCode+" : ["+probability+"]";
	}
	
}
