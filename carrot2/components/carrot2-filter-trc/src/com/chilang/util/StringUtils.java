package com.chilang.util;

import java.util.Map;
import java.util.LinkedList;

public class StringUtils {
    /**
     * Check if token contains only digits
     * @param token
     * @return <code>true</code> iff token is contains only digits
     */
    public static boolean isAllDigits(String token) {

        for (int i=0; i<token.length(); i++) {
            if (!Character.isDigit(token.charAt(i)))
                return false;
        }
        return true;
    }

    /**
     * Check if token contains only letters
     * @param token
     * @return <code>true</code> iff token is contains only letters
     */
    public static boolean isAllLetters(String token) {

        for (int i=0; i<token.length(); i++) {
            if (!Character.isLetter(token.charAt(i)))
                return false;
        }
        return true;
    }

    /**
     * Calculate the degree of ovelap (how much of second set is contained in the first set)
     * between two set" of words : i.e. = |w1 * w2|/w1
     * @param words the larger set of words
     * @param subset
     * @return
     */
    public static double overlap(String[] words, String[] subset) {
        //brute force version
        int overlap = 0;
        for (int i = 0; i < subset.length; i++) {
            for (int j = 0; j < words.length; j++) {
                if (subset[i].equalsIgnoreCase(words[j]))
                    overlap++;
            }
        }
        return (double)overlap/words.length;
    }

    public static String getQueryNameFromFilePath(String filepath) {
        return filepath.substring(filepath.lastIndexOf("\\")+1, filepath.lastIndexOf(".")).replaceAll("_", " ").toLowerCase();
    }

    public static Object getCarrotParameter(Map params, Object key) {
        LinkedList values = (LinkedList)params.get(key);
        if (values != null) {
            return values.get(0);
        } else {
            return null;
        }

    }


    /**
     * Trim given string from leading/trailing character c
     * @param s String to be trimmed
     * @param c character to be removed
     * @return trimmed String
     */
    public static String trim(String s, char c) {
        StringBuffer b = new StringBuffer(s);
        if (b.length() > 0 && b.charAt(0) == c)
            b.deleteCharAt(0);
        if (b.length() > 0 && b.charAt(b.length()-1) == c)
            b.deleteCharAt(b.length() -1);
        return b.toString();
    }

    public static String join(String[] ss, String joiner) {
        StringBuffer b = new StringBuffer();
        String sep = "";
        for (int i = 0, l = ss.length; i <l; i++) {
            b.append(sep + ss[i]);
            sep = joiner;
        }
        return b.toString();
    }
}
