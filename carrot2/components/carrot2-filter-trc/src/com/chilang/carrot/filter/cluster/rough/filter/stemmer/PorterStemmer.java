/*
** Copyright (C) 1999 Christian Werner
**
** This program and library is free software; you can redistribute it and/or
** modify it under the terms of the GNU (Library) General Public License
** as published by the Free Software Foundation; either version 2
** of the License, or any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
** GNU (Library) General Public License for more details.
**
** You should have received a copy of the GNU (Library) General Public License
** along with this program; if not, write to the Free Software
** Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
**
** Original C Source Header:
** -------------------------
**
** WIDE AREA INFORMATION SERVER SOFTWARE:
** No guarantees or restrictions.  See the readme file for the full standard
** disclaimer.
** francois@welchgate.welch.jhu.edu
** Copyright (c) CNIDR (see ../COPYRIGHT)
**
**   Purpose:    Implementation of the Porter stemming algorithm documented
**               in: Porter, M.F., "An Algorithm For Suffix Stripping,"
**               Program 14 (3), July 1980, pp. 130-137.
**
**   Provenance: Written by B. Frakes and C. Cox, 1986.
**               Changed by C. Fox, 1990.
**                  - made measure function a DFA
**                  - restructured structs
**                  - renamed functions and variables
**                  - restricted function and variable scopes
**               Changed by C. Fox, July, 1991.
**                  - added ANSI C declarations
**                  - branch tested to 90% coverage
**
**   Notes:      This code will make little sense without the the Porter
**               article.  The stemming function converts its input to
**               lower case.
*/

package com.chilang.carrot.filter.cluster.rough.filter.stemmer;

/**
 * Interface implementing replacement test function for stemming rule
 */

interface StemCond {
    public boolean cond(String word, Integer[] endp);
}

/**
 * Class implementing stemming rule
 */

class StemRule {
    /**
     * Rule identifier returned if rule fired
     */
    int id;

    /**
     * Suffix replaced
     */
    String old_end;

    /**
     * Suffix replacement
     */
    String new_end;

    /**
     * From end of word to start of suffix
     */
    int old_offset;

    /**
     * From beginning to end of new suffix
     */
    int new_offset;

    /**
     * Min root word size for replacement
     */
    int min_root_size;

    /**
     * Replacement test function
     */
    StemCond condition;

    static final String LAMBDA = "";

    StemRule(int id, String old_end, String new_end, int old_offset,
             int new_offset, int min_root_size, StemCond condition) {
        this.id = id;
        this.old_end = old_end;
        this.new_end = new_end;
        this.old_offset = old_offset;
        this.new_offset = new_offset;
        this.min_root_size = min_root_size;
        this.condition = condition;
    }
}

class ContainsVowel implements StemCond {
    /**
     * Some of the rewrite rules apply only to a root containing
     * a vowel, where a vowel is one of "aeiou" or y with a
     * consonant in front of it.
     *
     * Obviously, under the definition of a vowel, a word contains
     * a vowel iff either its first letter is one of "aeiou", or
     * any of its other letters are "aeiouy".  The plan is to
     * test this condition.
     *
     * @param word word to be tested
     * @param endp dummy int array reference to satisfy interface
     * @return true if the word parameter contains a vowel, false otherwise
     */
    public boolean cond(String word, Integer[] endp) {
        if (word.length() == 0) {
            return false;
        }
        if (PorterStemmer.isVowel(word.charAt(0))) {
            return true;
        }
        word = word.substring(1);
        return word.indexOf('a') >= 0 ||
                word.indexOf('e') >= 0 ||
                word.indexOf('i') >= 0 ||
                word.indexOf('o') >= 0 ||
                word.indexOf('u') >= 0 ||
                word.indexOf('y') >= 0;
    }
}

class RemoveAnE implements StemCond {
    /**
     * Rule 502 applies only to a root with this characteristic.
     * Check for size of 1 and no consonant-vowel-consonant ending.
     * @param word word to be tested
     * @param endp int array reference for new word end
     * @return true if the current word meets special conditions
     * for removing an e.
     */
    public boolean cond(String word, Integer[] endp) {
        return PorterStemmer.wordSize(word) == 1 && !PorterStemmer.endsWithCVC(word, endp);
    }
}

class AddAnE implements StemCond {
    /**
     * Rule 122 applies only to a root with this characteristic.
     * Check for size of 1 and a consonant-vowel-consonant ending.
     * @param word word to be tested
     * @param endp int array reference for new word end
     * @return true if the current word meets special conditions
     * for adding an e, false otherwise.
     */
    public boolean cond(String word, Integer[] endp) {
        return PorterStemmer.wordSize(word) == 1 && PorterStemmer.endsWithCVC(word, endp);
    }
}

/**
 * Class implementing stemming on a search word.
 */

public class PorterStemmer implements Stemmer{
    /**
     * Test character being a vowel.
     * @param c character to be tested
     * @return true if character is vowel (a, e, i, o, or u)
     */
    static boolean isVowel(char c) {
        return "aeiou".indexOf(c) >= 0;
    }

    static final ContainsVowel PContainsVowel = new ContainsVowel();
    static final RemoveAnE PRemoveAnE = new RemoveAnE();
    static final AddAnE PAddAnE = new AddAnE();

    static final StemRule[] step1a_rules = {
        new StemRule(101,  "sses",      "ss",    3,  1, -1,  null),
        new StemRule(102,  "ies",       "i",     2,  0, -1,  null),
        new StemRule(103,  "ss",        "ss",    1,  1, -1,  null),
        new StemRule(104,  "s", StemRule.LAMBDA, 0, -1, -1,  null),
    };

    static final StemRule[] step1b_rules = {
        new StemRule(105,  "eed",         "ee",    2,  1,  0, null),
        new StemRule(106,  "ed",  StemRule.LAMBDA, 1, -1, -1, PContainsVowel),
        new StemRule(107,  "ing", StemRule.LAMBDA, 2, -1, -1, PContainsVowel),
    };

    static final StemRule[] step1b1_rules = {
        new StemRule(108,  "at",        "ate",   1,  2, -1,  null),
        new StemRule(109,  "bl",        "ble",   1,  2, -1,  null),
        new StemRule(110,  "iz",        "ize",   1,  2, -1,  null),
        new StemRule(111,  "bb",        "b",     1,  0, -1,  null),
        new StemRule(112,  "dd",        "d",     1,  0, -1,  null),
        new StemRule(113,  "ff",        "f",     1,  0, -1,  null),
        new StemRule(114,  "gg",        "g",     1,  0, -1,  null),
        new StemRule(115,  "mm",        "m",     1,  0, -1,  null),
        new StemRule(116,  "nn",        "n",     1,  0, -1,  null),
        new StemRule(117,  "pp",        "p",     1,  0, -1,  null),
        new StemRule(118,  "rr",        "r",     1,  0, -1,  null),
        new StemRule(119,  "tt",        "t",     1,  0, -1,  null),
        new StemRule(120,  "ww",        "w",     1,  0, -1,  null),
        new StemRule(121,  "xx",        "x",     1,  0, -1,  null),
        new StemRule(122, StemRule.LAMBDA, "e", -1,  0, -1,  PAddAnE),
    };

    static final StemRule[] step1c_rules = {
        new StemRule(123,  "y",         "i",     0,  0, -1,  PContainsVowel),
    };

    static final StemRule[] step2_rules = {
        new StemRule(203,  "ational",   "ate",   6,  2,  0,  null),
        new StemRule(204,  "tional",    "tion",  5,  3,  0,  null),
        new StemRule(205,  "enci",      "ence",  3,  3,  0,  null),
        new StemRule(206,  "anci",      "ance",  3,  3,  0,  null),
        new StemRule(207,  "izer",      "ize",   3,  2,  0,  null),
        new StemRule(208,  "abli",      "able",  3,  3,  0,  null),
        new StemRule(209,  "alli",      "al",    3,  1,  0,  null),
        new StemRule(210,  "entli",     "ent",   4,  2,  0,  null),
        new StemRule(211,  "eli",       "e",     2,  0,  0,  null),
        new StemRule(213,  "ousli",     "ous",   4,  2,  0,  null),
        new StemRule(214,  "ization",   "ize",   6,  2,  0,  null),
        new StemRule(215,  "ation",     "ate",   4,  2,  0,  null),
        new StemRule(216,  "ator",      "ate",   3,  2,  0,  null),
        new StemRule(217,  "alism",     "al",    4,  1,  0,  null),
        new StemRule(218,  "iveness",   "ive",   6,  2,  0,  null),
        new StemRule(219,  "fulnes",    "ful",   5,  2,  0,  null),
        new StemRule(220,  "ousness",   "ous",   6,  2,  0,  null),
        new StemRule(221,  "aliti",     "al",    4,  1,  0,  null),
        new StemRule(222,  "iviti",     "ive",   4,  2,  0,  null),
        new StemRule(223,  "biliti",    "ble",   5,  2,  0,  null),
    };

    static final StemRule[] step3_rules = {
        new StemRule(301,  "icate",     "ic",        4,  1,  0,  null),
        new StemRule(302,  "ative", StemRule.LAMBDA, 4, -1,  0,  null),
        new StemRule(303,  "alize",     "al",        4,  1,  0,  null),
        new StemRule(304,  "iciti",     "ic",        4,  1,  0,  null),
        new StemRule(305,  "ical",      "ic",        3,  1,  0,  null),
        new StemRule(308,  "ful",   StemRule.LAMBDA, 2, -1,  0,  null),
        new StemRule(309,  "ness",  StemRule.LAMBDA, 3, -1,  0,  null),
    };

    static final StemRule[] step4_rules = {
        new StemRule(401,  "al",    StemRule.LAMBDA,  1, -1,  1,  null),
        new StemRule(402,  "ance",  StemRule.LAMBDA,  3, -1,  1,  null),
        new StemRule(403,  "ence",  StemRule.LAMBDA,  3, -1,  1,  null),
        new StemRule(405,  "er",    StemRule.LAMBDA,  1, -1,  1,  null),
        new StemRule(406,  "ic",    StemRule.LAMBDA,  1, -1,  1,  null),
        new StemRule(407,  "able",  StemRule.LAMBDA,  3, -1,  1,  null),
        new StemRule(408,  "ible",  StemRule.LAMBDA,  3, -1,  1,  null),
        new StemRule(409,  "ant",   StemRule.LAMBDA,  2, -1,  1,  null),
        new StemRule(410,  "ement", StemRule.LAMBDA,  4, -1,  1,  null),
        new StemRule(411,  "ment",  StemRule.LAMBDA,  3, -1,  1,  null),
        new StemRule(412,  "ent",   StemRule.LAMBDA,  2, -1,  1,  null),
        new StemRule(423,  "sion",      "s",          3,  0,  1,  null),
        new StemRule(424,  "tion",      "t",          3,  0,  1,  null),
        new StemRule(415,  "ou",    StemRule.LAMBDA,  1, -1,  1,  null),
        new StemRule(416,  "ism",   StemRule.LAMBDA,  2, -1,  1,  null),
        new StemRule(417,  "ate",   StemRule.LAMBDA,  2, -1,  1,  null),
        new StemRule(418,  "iti",   StemRule.LAMBDA,  2, -1,  1,  null),
        new StemRule(419,  "ous",   StemRule.LAMBDA,  2, -1,  1,  null),
        new StemRule(420,  "ive",   StemRule.LAMBDA,  2, -1,  1,  null),
        new StemRule(421,  "ize",   StemRule.LAMBDA,  2, -1,  1,  null),
    };

    static final StemRule[] step5a_rules = {
        new StemRule(501,  "e", StemRule.LAMBDA,  0, -1,  1,  null),
        new StemRule(502,  "e", StemRule.LAMBDA,  0, -1, -1,  PRemoveAnE),
    };

    static final StemRule[] step5b_rules = {
        new StemRule(503,  "ll",        "l",     1,  0,  1,  null),
    };

    /**
     * Some of the rewrite rules apply only to a root with
     * this characteristic.
     * @param word word to be tested
     * @param endp int array reference for new word end
     * @return true if the current word ends with a
     * consonant-vowel-consonant combination, and the second
     * consonant is not w, x, or y, false otherwise.
     */
    static boolean endsWithCVC(String word, Integer[] endp) {
        int length = word.length();
        if (length < 2) {
            return false;
        }
        int end = length - 1;
        boolean result =
                "aeiouwxy".indexOf(word.charAt(end--)) < 0 &&
                "aeiouy".indexOf(word.charAt(end--)) >= 0 &&
                "aeiou".indexOf(word.charAt(end)) < 0;
        if (endp != null) {
            endp[0] = new Integer(end);
        }
        return result;
    }

    /**
     * Count syllables in a special way:  count the number
     * vowel-consonant pairs in a word, disregarding initial
     * consonants and final vowels.  The letter "y" counts as a
     * consonant at the beginning of a word and when it has a vowel
     * in front of it; otherwise (when it follows a consonant) it
     * is treated as a vowel.  For example, the WordSize of "cat"
     * is 1, of "any" is 1, of "amount" is 2, of "anything" is 3.
     *
     * The easiest and fastest way to compute this funny measure is
     * with a finite state machine.  The initial state 0 checks
     * the first letter.  If it is a vowel, then the machine changes
     * to state 1, which is the "last letter was a vowel" state.
     * If the first letter is a consonant or y, then it changes
     * to state 2, the "last letter was a consonant state".  In
     * state 1, a y is treated as a consonant (since it follows
     * a vowel), but in state 2, y is treated as a vowel (since
     * it follows a consonant.  The result counter is incremented
     * on the transition from state 1 to state 2, since this
     * transition only occurs after a vowel-consonant pair, which
     * is what we are counting.
     *
     * @param word word to be counted
     * @return weird count of word size in adjusted syllables
     */
    static int wordSize(String word) {
        int result = 0;
        int state = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            switch (state) {
                case 0:
                    state = PorterStemmer.isVowel(c) ? 1 : 2;
                    break;
                case 1:
                    state = PorterStemmer.isVowel(c) ? 1 : 2;
                    if (state == 2) {
                        ++result;
                    }
                    break;
                case 2:
                    state = (PorterStemmer.isVowel(c) || c == 'y') ? 1 : 2;
                    break;
            }
        }
        return result;
    }

    /**
     * Apply a set of rules to replace the suffix of a word.
     * Loop through the rule set until a match meeting all conditions
     * is found.  If a rule fires, return its id, otherwise return 0.
     * Conditions on the length of the root are checked as part of this
     * function's processing because this check is so often made.
     * This is the main routine driving the stemmer. It goes through
     * a set of suffix replacement rules looking for a match on the
     * current suffix. When it finds one, if the root of the word
     * is long enough, and it meets whatever other conditions are
     * required, then the suffix is replaced, and the function returns.
     * @param word string array with 0th element being word to be stemmed
     * @param rules array of stemming rules to be applied
     * @param endp int array for word end
     * @return integer id for the rule fired, 0 if none has fired
     */
    static int replaceEnd(String[] word, StemRule[] rules, Integer[] endp) {
        int i;
        for (i = 0; i < rules.length; i++) {
            int ending = endp[0].intValue() - rules[i].old_offset;
            if (ending >= 0 && ending < word[0].length()) {
                String w0 = word[0].substring(0, ending);
                String w1 = word[0].substring(ending);
                if (w1.compareTo(rules[i].old_end) == 0) {
                    if (rules[i].min_root_size < wordSize(w0)) {
                        if (rules[i].condition == null ||
                                rules[i].condition.cond(w1, endp)) {
                            word[0] = w0 + rules[i].new_end;
                            endp[0] =
                                    new Integer(ending + rules[i].new_offset);
                            break;
                        }
                    }
                }
            }
        }
        return i < rules.length ? rules[i].id : 0;
    }

    /**
     * Stem a word<br>
     * <ul><li>Part 1: Check to ensure the word is all alphabetic
     *     <li>Part 2: Run through the Porter algorithm
     *     <li>Part 3: Return resulting stemmed word
     * </ul>
     * This function implements the Porter stemming algorithm, with
     * a few additions here and there. See:
     * <pre>
     *     Porter, M.F., "An Algorithm For Suffix Stripping,"
     *     Program 14 (3), July 1980, pp. 130-137.
     * </pre>
     * Porter's algorithm is an ad hoc set of rewrite rules with
     * various conditions on rule firing.  The terminology of
     * "step 1a" and so on, is taken directly from Porter's
     * article, which unfortunately gives almost no justification
     * for the various steps.  Thus this function more or less
     * faithfully refects the opaque presentation in the article.
     * Changes from the article amount to a few additions to the
     * rewrite rules;  these are marked in the RuleList data
     * structures with comments.
     * @param word string to be stemmed
     * @return new stemmed string
     */
    public String stem(String word) {
        /* Part 1: Check to ensure the word is all alphabetic */
        StringBuffer sb = new StringBuffer();
        int end;
        for (end = 0; end < word.length(); end++) {
            if (!Character.isLetter(word.charAt(end))) {
                return word;
            } else {
                sb.append(word.charAt(end));
            }
        }
        --end;
        word = sb.toString();

        /* Part 2: Run through the Porter algorithm */
        String [] w = new String[1];
        w[0] = word;
        Integer [] endp = new Integer[1];
        endp[0] = new Integer(end);
        replaceEnd(w, step1a_rules, endp);
        int rule = replaceEnd(w, step1b_rules, endp);
        if (rule == 106 || rule == 107) {
            replaceEnd(w, step1b1_rules, endp);
        }
        replaceEnd(w, step1c_rules, endp);
        replaceEnd(w, step2_rules, endp);
        replaceEnd(w, step3_rules, endp);
        replaceEnd(w, step4_rules, endp);
        replaceEnd(w, step5a_rules, endp);
        replaceEnd(w, step5b_rules, endp);
        /* Part 3: Return resulting stemmed word */
        return w[0];
    }

}
