
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

import java.io.Reader;

/**
 * This interface should be implemented by all language guessers.
 * 
 * @author Jean-Francois Halleux
 */
public interface LanguageGuesser {
	
	/**
	 * Returns an array of supported languages.
	 * 
	 * <p>The supported languages are all languages the LanguageGuesser
	 * implementation supports, regardless of the restriction that has
	 * been applied.
	 * 
	 * @return an array of at least two ISO-639 code 
	 */
	public abstract String[] supportedLanguages();
	
	/**
	 * Returns the most probable ISO-639 Code of r.
	 * 	
	 * <p>There is no constraint on the number of units that will be read from r
	 * and typically implementations will read r entirely.
	 * 
	 * <p>The language returned will be one of the recognized languages.
	 * 
	 * <p>r is closed before this method returns
	 * 
	 * <p>Implementations are encouraged to make this method thread-safe,
	 * at least when used concurrently with other guessing methods.
	 * 
	 * <p>If an implementation has not enough information in r to be able to guess its language,
	 * it will throw a LanguageGuesserException
	 * 
	 * @param r the reader the language will be guessed from 
	 * @return an ISO-639 Code from the recognized languages
	 * @throws LanguageGuesserException if an implementation cannot gather enough information
	 * from r to guess its language or if reading r caused an IOException
	 */
	public abstract String guessLanguage(Reader r) throws LanguageGuesserException;
	
	/**
	 * Returns the most probable ISO-639 Code of r, processing maximum maxProcessingUnits.
	 * 
	 * r is closed before this method returns.
	 * 
	 * The language returned will be one of the recognized languages.
	 * 
	 * Implementations are encouraged to make this method thread-safe,
	 * at least when used concurrently with other guessing methods.
	 * 
	 * @param r the reader the language will be guessed from 
	 * @param maxProcessingUnits the maximum number of processing units that will be
	 * read from r in order to guess its language
	 * @return an ISO-639 Code from the recognized languages
	 * @throws LanguageGuesserException 
	 * if reading r caused an IOException or if maxProcessingUnits is negative
	 * or if an implementation cannot gather enough information
	 * from r to guess its language
	 */
	public abstract String guessLanguage(Reader r, int maxProcessingUnits)
			throws LanguageGuesserException;
	
	/**
	 * Returns the probabilities of all recognized languages for r.
	 * 
	 * r is closed before this method returns.
	 * 
	 * The LanguageProbability array returned will be sorted by
	 * decreasing order of probability.
	 * The first element in the array should have probability 1.0,
	 * unless all recognized languages have the same probability, in which case
	 * all elements in the array will have probability 0.0
	 * 
	 * Implementations are encouraged to make this method thread-safe,
	 * at least when used concurrently with other guessing methods.
	 * 
	 * @param r the reader the language will be guessed from 
	 * @return an array of LanguageProbability sorted in decreasing order of probability, starting from 1.0 unless
	 * recognized languages have all the same probability for r, in which case probabilities
	 * are all equal to zero
	 * @throws LanguageGuesserException 
	 * if reading r caused an IOException or if an implementation cannot gather enough information
	 * from r to guess its language
	 */
	public abstract LanguageProbability[] guessLanguages(Reader r)
			throws LanguageGuesserException;
	
	/**
	 * Returns the probabilities of all recognized languages for r,
	 * processing maxProcessingUnits.
	 * 
	 * r is closed before this method returns.
	 * 
	 * The LanguageProbability array returned will be sorted by
	 * decreasing order of probability.
	 * The first element in the array should have probability 1.0,
	 * unless all recognized languages have the same probability, in which case
	 * all elements in the array will have probability 0.0
	 * 
	 * Implementations are encouraged to make this method thread-safe,
	 * at least when used concurrently with other guessing methods.
	 * 
	 * @param r the reader the language will be guessed from 
	 * @param maxProcessingUnits the maximum number of processing units that will be
	 * read from r in order to guess its language
	 * @return an array of LanguageProbability sorted in decreasing order of probability, starting from 1.0 unless
	 * recognized languages have all the same probability for r, in which case probabilities
	 * are all equal to zero
	 * @throws LanguageGuesserException 
	 * if reading r caused an IOException or
	 * if an implementation cannot gather enough information
	 * from r to guess its language
	 */
	public abstract LanguageProbability[] guessLanguages(Reader r,
			int maxProcessingUnits) throws LanguageGuesserException;
	
	/**
	 * Restricts guessing to the languages passed in restriction.
	 * 
	 * If a language passed in parameter is not supported by the implementation, it
	 * is simply ignored.  
	 * 
	 * The restriction overrides any previous restriction
	 * 
	 * @param restriction An array containing the ISO-639 language code the guessing methods will guess from
	 * @throws LanguageGuesserException if after applying the restriction, there is less than 2
	 * different recognized languages 
	 */
	public abstract void restrictToLanguages(String[] restriction) throws LanguageGuesserException;
	
	/**
	 * Returns the languages the guesser will guess from. This is the subset of
	 * the supported languages determined by the restriction (if any)
	 * 
	 * @return an array of at least two different language code
	 */
	public abstract String[] recognizedLanguages();
	
	/**
	 * Removes the restriction (if any) on the languages being recognized
	 * by the language guesser
	 */
	public abstract void clearRestriction();
	
}