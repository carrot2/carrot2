
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.ngram;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.StopWordFilter;

public class NGramFactory {

    boolean useFilter = false;;
    public NGramFactory() {

    }

    StopWordFilter filter;


    public NGramFactory(StopWordFilter stopWordFilter) {
        this.useFilter = true;
        this.filter = stopWordFilter;
    }

    /**
     * Construct n-gram from sequence of words.
     * Trim sequence from stopwords if filter is activated
     * @param words
     * @return constructed
     */
    public NGram create(String[] words) {
        if (!useFilter)
            return new NGram(words);

//        System.out.print(ArrayUtils.toString(words));
        //truncate out stop words from n-gram's head and tail
        int length = words.length;
        int i=0, j=length-1;

        while(i<length && filter.isStopWord(words[i].toLowerCase()))  {
            i++;
        }

        while(j>=0 && filter.isStopWord(words[j].toLowerCase())) {
            j--;
        }

//        System.out.println(" = "+i+","+j);
        //all words were truncated
        if (i > j)
            return null;
        int newLength = j-i+1;
        String[] w = new String[newLength];
        for(int k=0; k <newLength; k++){
            w[k] = words[i+k].toLowerCase();
        }
        return new NGram(w);
    }
}
