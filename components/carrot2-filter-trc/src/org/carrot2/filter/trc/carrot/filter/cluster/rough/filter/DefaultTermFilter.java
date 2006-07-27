
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.filter;

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
