
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

package org.apache.lucene.misc;
/*
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 not
 * use this file except in compliance with the License. You may obtain a
 copy
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

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * TrigramLanguageGuesser implements language guessing based on trigrams
 *
 * @author Jean-Francois Halleux
 */
public class TrigramLanguageGuesser implements LanguageGuesser {

	//a map isoCode->ref trigrams for this isoCode
	Map langTrigramsMap = new TreeMap();

	private LanguageTrigrams[] supportedLanguages;

	//all guess are made on the restriction
	private LanguageTrigrams[] restriction;

	public static void main(String[] args) throws Exception {
		String dataDir = System.getProperty("dataDir");
		JarInputStream jis = new JarInputStream(new FileInputStream(new File(dataDir, "Trigrams.jar")));
		TrigramLanguageGuesser g = new TrigramLanguageGuesser(jis);
		LanguageProbability[] probs = g.guessLanguages(new StringReader(args[0]));
		for (int i = 0; i < probs.length; i++) {
			System.err.println(probs[i]);
		}
		//System.out.println(g.guessLanguage(new StringReader(args[0])));
	}

    /**
	 * Construct a LanguageGuesser
	 * The JarInputStream is a stream for a jar containing the
	 * language reference files. The Manifest has a language attribute
	 * for each jar entry representing its ISO code.
	 *
	 * The stream is closed when reading has finished
	 *
	 * see http://ftp.ics.uci.edu/pub/ietf/http/related/iso639.txt
	 * @param jis the JarInputStream language reference will be read from
	 * @throws LanguageGuesserException if an error occured
	 * reading the inputstream
	 */
	public TrigramLanguageGuesser(JarInputStream jis) throws
	LanguageGuesserException {
		try {
			DataInputStream dis=
				new DataInputStream(
						new BufferedInputStream(jis));

			for (JarEntry je=jis.getNextJarEntry();je!=null;je=jis.getNextJarEntry())
			{
				Trigrams t = Trigrams.loadFromInputStream(dis);
				Attributes a=je.getAttributes();
				langTrigramsMap.put(a.getValue("Language"), t);
			}

			if (langTrigramsMap.size() == 0)
				throw new LanguageGuesserException("Jar doesn't contain any .tri entry");

            initialize(langTrigramsMap);
            
			dis.close();
		}
		catch (IOException ioe) {
			throw new LanguageGuesserException(ioe);
		}
	}

    /**
     * Construct a LanguageGuesser based on an existing map
     * of ISO language codes to Trigram objects.
     * 
     * @see http://ftp.ics.uci.edu/pub/ietf/http/related/iso639.txt
     * @param languagesAndTrigrams a map of language code (string) to
     *  Trigram object.
     * @throws LanguageGuesserException if an error occured
     * @param languagesAndTrigrams
     */
    public TrigramLanguageGuesser(Map languagesAndTrigrams) 
    throws LanguageGuesserException {
    	initialize( languagesAndTrigrams );
    }

    /**
     * Construct a LanguageGuesser based on an existing map
     * of ISO language codes to Trigram objects.
     */
    private final void initialize(Map languagesAndTrigrams) {
        this.langTrigramsMap.putAll(languagesAndTrigrams);
        supportedLanguages = new LanguageTrigrams[langTrigramsMap.size()];
        restriction = new LanguageTrigrams[langTrigramsMap.size()];

        Iterator it = langTrigramsMap.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            String isoCode = (String) it.next();
            LanguageTrigrams lt =
                new LanguageTrigrams(
                        isoCode,
                        (Trigrams) langTrigramsMap.get(isoCode));
            supportedLanguages[i] = lt;
            restriction[i++] = lt;
        }
    }
    
	/**
	 * Returns an array of supported languages.
	 *
	 * This method is thread-safe.
	 *
	 * @see org.apache.lucene.misc.LanguageGuesserFactory#supportedLanguages()
	 * @return an array of at least two ISO-639 code
	 */
	public String[] supportedLanguages() {
		String[] sla = new String[supportedLanguages.length];
		for (int i = 0; i < supportedLanguages.length; i++) {
			sla[i] = supportedLanguages[i].getLanguage();
		}
		return sla;
	}

	/**
	 * Returns the most probable ISO-639 Code of r.
	 *
	 * All trigrams of r will be read.
	 *
	 * The language returned will be one of the recognized languages.
	 *
	 * r is closed before this method returns
	 *
	 * This method is thread-safe
	 *
	 * If no trigrams can be read from r, this method will return a
	 * LanguageGuesserException
	 *
	 * @see org.apache.lucene.misc.LanguageGuesserFactory#guessLanguage(Reader)
	 * @param r the reader the language will be guessed from
	 * @return an ISO-639 Code from the recognized languages
	 * @throws LanguageGuesserException if this TrigramLanguageGuesser cannot
	 * read
	 * enough trigrams from r to guess its language or if reading r caused an
	 * IOException
	 */
	public String guessLanguage(Reader r) throws LanguageGuesserException {
		return guessLanguage(r, Integer.MAX_VALUE);
	}

	/**
	 * Returns the most probable ISO-639 Code of r, processing maximum
	 * maxTrigrams.
	 *
	 * r is closed before this method returns.
	 *
	 * The language returned will be one of the recognized languages.
	 *
	 * This method is thread-safe
	 *
	 * @param r the reader the language will be guessed from
	 * @param maxTrigrams the maximum number of trigrams that will be
	 * read from r in order to guess its language
	 * @return an ISO-639 Code from the recognized languages
	 * @throws LanguageGuesserException
	 * if reading r caused an IOException or if maxTrigrams is negative
	 * or if this TrigramLanguageGuesser cannot read enough trigrams from r to guess its language
	 */
	public String guessLanguage(Reader r, int maxTrigrams) throws
	LanguageGuesserException {
		if (r == null)
			throw new LanguageGuesserException("Reader r must not be null");
		if (maxTrigrams < 1)
			throw new LanguageGuesserException("maxGrams must be greater or equal to 1");

		LanguageRanker lr = new LanguageRanker(restriction);
		try {
			TrigramGenerator.start(r, lr, maxTrigrams);
		} catch (IOException ioe) {
			throw new LanguageGuesserException(ioe);
		}

		return lr.getLanguage();
	}

	/**
	 * Returns the probabilities of all recognized languages for r.
	 *
	 * r is closed before this method returns.
	 *
	 * The LanguageProbability array returned will be sorted by
	 * decreasing order of probability.
	 * The first element in the array will have probability 1.0,
	 * unless all recognized languages have the same probability, in which case
	 * all elements in the array will have probability 0.0
	 *
	 * This method is thread-safe
	 *
	 * @param r the reader the language will be guessed from
	 * @return an array of LanguageProbability sorted in decreasing order of
     * probability, starting from 1.0 unless
	 * recognized languages have all the same probability for r, in which case
	 * probabilities
	 * are all equal to zero
	 * @throws LanguageGuesserException
	 * if reading r caused an IOException or if this TrigramLanguageGuesser
	 * cannot read enough trigrams
	 * from r to guess its language
	 */
	public LanguageProbability[] guessLanguages(Reader r) throws
	LanguageGuesserException {
		return guessLanguages(r, Integer.MAX_VALUE);
	}

	/**
	 * Returns the probabilities of all recognized languages for r,
	 * processing maxTrigrams at most.
	 *
	 * r is closed before this method returns.
	 *
	 * The LanguageProbability array returned will be sorted by
	 * decreasing order of probability.
	 * The first element in the array will have probability 1.0,
	 * unless all recognized languages have the same probability, in which case
	 * all elements in the array will have probability 0.0
	 *
	 * This method is thread-safe
	 *
	 * @param r the reader the language will be guessed from
	 * @param maxTrigrams the number of trigrams that will be
	 * read from r in order to guess its language
	 * @return an array of LanguageProbability sorted in decreasing order of
	 probability, starting from 1.0 unless
	 * recognized languages have all the same probability for r, in which case
	 probabilities
	 * are all equal to zero
	 * @throws LanguageGuesserException
	 * if reading r caused an IOException or
	 * if this TrigramLanguageGuesser cannot read enough information
	 * from r to guess its language
	 */
	public LanguageProbability[] guessLanguages(Reader r, int maxTrigrams)
	throws LanguageGuesserException {

		if (r == null)
			throw new LanguageGuesserException("Reader r must not be null");
		if (maxTrigrams < 1)
			throw new LanguageGuesserException("maxGrams must be greater or equal to 1");

					LanguageRanker lr = new LanguageRanker(restriction);
					try {
						TrigramGenerator.start(r, lr, maxTrigrams);
					}
					catch (IOException ioe) {
						throw new LanguageGuesserException(ioe);
					}

					return lr.getLanguages();
	}

	/**
	 * Restricts guessing to the languages passed in restriction.
	 *
	 * If a language passed in parameter is not supported by the
	 TrigramLanguageGuesser, it
	 * is simply ignored.
	 *
	 * The restriction overrides any previous restriction
	 *
	 * @param restriction An array containing the ISO-639 language code the
	 guessing methods will guess from
	 * @throws LanguageGuesserException if after applying the restriction,
	 there is less than 2
	 * different recognized languages
	 */
	public synchronized void restrictToLanguages(String[] restriction) {
		if ((restriction == null) || (restriction.length == 0)) {
			clearRestriction();
			return;
		}

		Set languageSet = new TreeSet();
		for (int i = 0; i < restriction.length; i++) {
			String isoCode = restriction[i];
			if (langTrigramsMap.containsKey(isoCode)) {
				languageSet.add(isoCode);
			}
		}
		if (languageSet.size() < 2)
			throw new RuntimeException("Argument restriction should at least contain two different supported languages");

					this.restriction = new LanguageTrigrams[languageSet.size()];
					Iterator it = languageSet.iterator();
					int i = 0;
					while (it.hasNext()) {
						String isoCode = (String) it.next();
						this.restriction[i++] =
							new LanguageTrigrams(
									isoCode,
									(Trigrams) langTrigramsMap.get(isoCode));
					}

	}

	/**
	 * Returns the languages the guesser will guess from. This is the subset of
	 * the supported languages determined by the restriction (if any)
	 *
	 * @return an array of at least two different language code
	 */
	public synchronized String[] recognizedLanguages() {
		String[] rla = new String[restriction.length];
		for (int i = 0; i < restriction.length; i++) {
			rla[i] = restriction[i].getLanguage();
		}
		return rla;
	}

	/**
	 * Removes the restriction (if any) on the languages being recognized
	 * by the language guesser
	 */
	public synchronized void clearRestriction() {
		restriction = new LanguageTrigrams[supportedLanguages.length];
		for (int i = 0; i < supportedLanguages.length; i++) {
			restriction[i] = supportedLanguages[i];
		}
	}

}

class LanguageTrigrams {

	String language;
	Trigrams trigrams;

	LanguageTrigrams(String language, Trigrams trigrams) {
		this.language = language;
		this.trigrams = trigrams;
	}

	String getLanguage() {
		return language;
	}

	Trigrams getTrigrams() {
		return trigrams;
	}

}

class LanguageRanker implements TrigramListener {

	private final LanguageTrigrams[] restriction;
	//distance, language
	long[][] distanceArray;
	int[] penaltyArray;
	long min = Long.MAX_VALUE;
	int best;

	LanguageRanker(LanguageTrigrams[] restriction) {
		this.restriction = restriction;
		distanceArray = new long[restriction.length][2];
		penaltyArray = new int[restriction.length];
		for (int i = 0; i < penaltyArray.length; i++) {
			distanceArray[i][1] = i;
			//TODO: penalty array could be static
			penaltyArray[i] = restriction[i].getTrigrams().size();
		}
	}

	public void addTrigram(Trigram t) {
		for (int i = 0; i < restriction.length; i++) {
			int rank = restriction[i].getTrigrams().getRank(t);
			if (rank == -1)
				rank = penaltyArray[i];
			distanceArray[i][0] += rank;
		}
	}

	String getLanguage() {
		for (int i = 0; i < distanceArray.length; i++) {
			if (distanceArray[i][0] < min) {
				best = i;
				min = distanceArray[i][0];
			}
		}
		return restriction[best].getLanguage();
	}

	LanguageProbability[] getLanguages() {
		LanguageProbability[] lp=new LanguageProbability[distanceArray.length];
		sortDistanceArray();

		for (int i=0;i<distanceArray.length;i++) {
			lp[i]=new LanguageProbability(restriction[i].getLanguage(),distanceArray[i][0]);
		}

		float minprob = lp[0].getProbability();

		//In case of an empty reader, all languages are returned
		//with a probability of 0.0
		if (minprob > 0.0f) {
			for (int i = 0; i < lp.length; i++) {
				LanguageProbability lprob=lp[i];
				lprob.setProbability(minprob / lp[i].getProbability());
			}
		}
		return lp;
	}

	//Adapted from Sedgewick

	private void sortDistanceArray() {
		quicksort(distanceArray, 0, distanceArray.length - 1);
	}

	void quicksort(long[][] a, int left, int right) {
		if (right <= left)
			return;
		int i = partition(a, left, right);
		quicksort(a, left, i - 1);
		quicksort(a, i + 1, right);
	}

	static int partition(long[][] a, int left, int right) {
		int i = left - 1;
		int j = right;

		while (true) {
			while (less(a[++i], a[right]));
			while (less(a[right], a[--j]))
				if (j == left) break;
			if (i >= j)	break;
			exch(a, i, j);
		}
		exch(a, i, right);
		return i;
	}

	static boolean less(long[] a, long[] b) {
		return (a[0] < b[0]);
	}

	static void exch(long[][] a, int i, int j) {
		long[] tmp = a[i];
		a[i] = a[j];
		a[j] = tmp;
	}

}