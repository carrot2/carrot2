package com.chilang.carrot.filter.cluster.rough.filter;


import java.io.*;
import java.util.HashSet;
import java.util.Set;


/**
 * Stop-word filter.
 * Filter out all stop-word from given list.
 * Accept only term that pass internal chained filter
 */
public class StopWordsSet
        implements StopWordFilter, TermFilter {

    protected Set stopwords;

    protected TermFilter chainedFilter;


    /**
     * Construct a stop word set based on words listed in given file
     * @param filename file with stop words
     */
    public StopWordsSet(String filename) {
        stopwords = new HashSet();
        try {
            LineNumberReader reader = new LineNumberReader(
                    new BufferedReader(
                            new InputStreamReader(
                                    getClass().getClassLoader().getResourceAsStream(filename))));

            String line = reader.readLine();
            while (line != null) {
                stopwords.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

        chainedFilter = new DefaultTermFilter();
    }

    public boolean isStopWord(String word) {
        return stopwords.contains(word);
    }

    public boolean accept(String term) {
        return chainedFilter.accept(term) && !stopwords.contains(term);
    }


}
