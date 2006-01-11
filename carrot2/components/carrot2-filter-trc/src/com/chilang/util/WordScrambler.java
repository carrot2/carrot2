
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

package com.chilang.util;

import java.util.Random;
import java.util.StringTokenizer;

public class WordScrambler {

    private static Random rand = new Random();

    private static final char[] ENGLISH_VOWELS = {
        'a', 'e', 'i', 'o', 'u'
    };
    /**
     * Scramble word.
     *
     * @param word
     */
    public static String scramble(String word) {

        if (word == null)
            return null;
        String trimmed = word.trim();
        if ("".equals(trimmed))
            return "";
        if (trimmed.length() <= 2)
            return trimmed;

        StringBuffer b = new StringBuffer(trimmed.length());
        b.append(trimmed.charAt(0));
        int[] perm = permutation(1, trimmed.length()-2);
        for (int i = 0; i < perm.length; i++) {
            b.append(trimmed.charAt(perm[i]));
        }
        b.append(trimmed.charAt(trimmed.length()-1));
        return b.toString();
    }



    /**
     * Apply scrambling on the whole text
     * @param text
     */
    public static String scrambleText(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text);
        StringBuffer b = new StringBuffer(text.length());
        String sep = "";
        while(tokenizer.hasMoreTokens()) {
            b.append(sep + scramble(tokenizer.nextToken()));
            sep = " ";
        }
        return b.toString();
    }

    public static String removeVowelsFromText(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text);
        StringBuffer b = new StringBuffer(text.length());
        String sep = "";
        while(tokenizer.hasMoreTokens()) {
            b.append(sep + removeVowels(tokenizer.nextToken()));
            sep = " ";
        }
        return b.toString();
    }
    public static String removeVowels(String word) {
        StringBuffer b = new StringBuffer(word.length());
        for(int i=0; i <word.length(); i++) {
            if (!isVowel(word.charAt(i)))
                b.append(word.charAt(i));
        }
        return b.toString();
    }

    private static boolean isVowel(char c) {
        for (int i=0; i < ENGLISH_VOWELS.length; i++) {
            if (c == ENGLISH_VOWELS[i])
                return true;
        }
        return false;
    }

    /**
     * Generate random permuation of sequence of number
     * from min to max (both side inclusive)
     * @param min
     * @param max
     * @return array of randomized permutation
     */
    private static int[] permutation(int min, int max) {
        int len = max - min + 1;
        int[] perm = new int[len];
        for (int i = 0; i < len; i++) {
            boolean unique = true;
            int r = rand.nextInt(len)+1;
            do {
                r = rand.nextInt(len)+1;
                unique = true;
                for (int j = 0; j < i; j++) {
                    if (perm[j] == r) {
                        unique = false;
                        break;
                    }
                }
            } while (!unique);
            perm[i] =r;
        }
        return perm;
    }

    public static void main(String[] argv) {
//        int[] perm = permutation(1,10);
//        System.out.println(ArrayUtils.toString(perm));

        String s = "oncern was growing in Shane Mosley's corner as the rounds went on and it became obvious his fight with Oscar De La Hoya would be going to the judge's scorecards. Jack Mosley wanted his son to do something spectacular -- and fast.\n" +
"\"My father was trying to convey to me since we're in Las Vegas and it's Oscar's town we had to pour it on in the last rounds,\" Mosley said.\n" +
"It turned out the wrong corner was worried. In a city in which De La Hoya scored his biggest victories, he stood in shocked amazement as the judges handed him his most disappointing defeat.\n" +
"De La Hoya thought he should be celebrating. Instead, he plans to start investigating.\n" +
"\"I just feel in my heart the decision should have gone to me,\" De La Hoya said. \"On Monday I will put a full investigation into what happened. I'm fortunate I have the resources to put the best lawyers on it.\"";

//        System.out.println(scrambleText(s));
//        System.out.println(scrambleText(s));
        System.out.println(removeVowelsFromText(s));
    }
}
