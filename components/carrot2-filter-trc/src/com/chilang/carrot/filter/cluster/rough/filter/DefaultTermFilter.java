package com.chilang.carrot.filter.cluster.rough.filter;

/**
 * Default term filter, accepts term only :
 *  - all letters
 *  - minimum length of 3
 */
public class DefaultTermFilter implements TermFilter{

    public boolean accept(String term) {
        return (term != null) && (term.length() > 2) && isAllLetters(term);
    }

    /**
     * Check if token contains only letters
     * @param token
     * @return <code>true</code> iff token is contains only letters
     */
    private static boolean isAllLetters(String token) {

        for (int i=0; i<token.length(); i++) {
            if (!Character.isLetter(token.charAt(i)))
                return false;
        }
        return true;
    }
}
