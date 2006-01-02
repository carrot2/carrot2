
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
package com.dawidweiss.carrot.filter.langguesser;

import com.dawidweiss.carrot.core.local.linguistic.LanguageGuesser;

/**
 * Tests of the language guesser factory. 
 * 
 * @author Dawid Weiss
 */
public class LanguageGuesserFactoryTest 
    extends junit.framework.TestCase {
    
    
    /**
     * Test whether the language guesser factory can be instantiated.
     */
    public void testInstantiation() {
    	assertNotNull(
                LanguageGuesserFactory.getLanguageGuesser());
    }
    
    /**
     * Test some samples and their detection accurracy. 
     */
    public void testLanguageDetectionOnSamples() {
        LanguageGuesser guesser = LanguageGuesserFactory.getLanguageGuesser();
        
        String [][] sample = new String [][] {
                { "This is an english text. Well, we assume so anyway. And it has to be long to be detected. So long? No kidding.", "en" },
                { "Tekst po polsku. Mówiony i pisany i nie wiadomo czemu, ale musi być długi. Nawet więcej, bardzo długi.", "pl" },
                { "Apesar de estarem no prelo desde 1851, o autor tinha descuidado na primeira o seu habitual de rever e corrigir", "pt"}
        };

        for (int i=0;i<sample.length;i++) {
            char [] charray = sample[i][0].toCharArray();
        	assertEquals( sample[i][1], guesser.guessLanguage( charray, 0, charray.length));
        }
    }
}
