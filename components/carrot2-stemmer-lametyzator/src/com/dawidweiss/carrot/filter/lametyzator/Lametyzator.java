
/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.filter.lametyzator;

import com.dawidweiss.carrot.core.local.linguistic.Stemmer;

import org.put.fsa.FSA;

import org.put.linguistics.stemming.fsa.FSAStemmer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * An implementation of a dictionary-driven, heuristic lemmatization engine for
 * the Polish language.
 *
 * @author Dawid Weiss
 * @version $Revision$
 *
 * @see <a
 *      href="http://www.cs.put.poznan.pl/dweiss/xml/projects/lametyzator/index.xml?lang=en">http://www.cs.put.poznan.pl/dweiss/xml/projects/lametyzator/index.xml</a>
 */
public class Lametyzator implements Stemmer {

    /**
     * A shared instance of the automaton with the
     * precompiled dictionary. 
     */
    private static transient FSA fsa;
    
    /**
     * A private instance of the finite state automaton
     * with the compiled dictionary. 
     */
    private transient FSAStemmer stemmer;
    
    /**
     * Initializes the FSA dictionary.
     */
    private void initialize() {
        synchronized (Lametyzator.class) {
            if (fsa != null)
                return;

            InputStream fsaStream = this.getClass().getResourceAsStream("polski.fsa");

            if (fsaStream == null) {
                throw new RuntimeException(
                    "Cannot load the required FSA dictionary 'polski.fsa'");
            }

            try {
				fsa = FSA.getInstance(new BufferedInputStream(fsaStream, 500000),
				        "iso8859-2");
			} catch (IOException e) {
                throw new RuntimeException("Could not load the required resource: polski.fsa");
			}
        }
    }
    
    /**
     * Creates a new instance of Lametyzator lemmatization engine for Polish.
     * The FSA automaton is read using classloader's facilities.
     */
    public Lametyzator() {
        initialize();
        try {
			stemmer = new FSAStemmer(fsa, "iso8859-2", '+');
		} catch (Exception e) {
            throw new RuntimeException("Could not load the required resource: polski.fsa");
		}
    }

    /**
     * Stems a term in characters array (a performance consideration) starting
     * at index <code>beginCharacter</code> (inclusive) and ending at
     * <code>endCharacter</code> (exclusive). This follows the pattern of
     * <code>String.substring</code>. If more than one base form the term is
     * available, the first one returned by the FSAStemmer is returned.
     */
    public String getStem(char[] charArray, int beginCharacter, int endCharacter) {
        String string = new String(charArray, beginCharacter,
                endCharacter - beginCharacter).toLowerCase();
        String[] stemmedForms = stemmer.stem(string);

        if (stemmedForms.length > 0) {
            return stemmedForms[0];
        } else {
            return null;
        }
    }
}
