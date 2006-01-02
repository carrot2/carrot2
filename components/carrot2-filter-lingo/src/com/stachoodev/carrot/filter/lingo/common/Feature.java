
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
package com.stachoodev.carrot.filter.lingo.common;

import java.text.NumberFormat;


/**
 * @author stachoo
 */
public class Feature {
    /** */

    /** DOCUMENT ME! */
    private int code;

    /** */

    /** DOCUMENT ME! */
    private String text;

    /** */

    /** DOCUMENT ME! */
    private String language;

    /** */

    /** DOCUMENT ME! */
    private int tf;

    /** */

    /** DOCUMENT ME! */
    private double idf;

    /** */

    /** DOCUMENT ME! */
    private int length;

    /** */

    /** DOCUMENT ME! */
    private boolean stopWord;

    /** */

    /** DOCUMENT ME! */
    private boolean queryWord;

    /** */

    /** DOCUMENT ME! */
    private boolean strong;

    /** */

    /** DOCUMENT ME! */
    private int[] phraseFeatureIndices;

    /** */

    /** DOCUMENT ME! */
    private int[] snippetIndices;

    /** */

    /** DOCUMENT ME! */
    private int[] snippetTf;

    /**
     * @param text
     * @param code
     * @param length
     * @param tf
     */
    public Feature(String text, int code, int length, int tf) {
        this.text = text;
        this.length = length;
        this.tf = tf;
        this.code = code;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer text = new StringBuffer();
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);

        text.append(this.text);
        text.append(" tf=" + getTf() + " tfidf=" + format.format(getTfidf()) +
            " ");

        if (stopWord) {
            text.append("SW ");
        }

        if (strong) {
            text.append("ST ");
        }

        text.append(language);

        return text.toString();
    }

    /**
     * @param by
     */
    public void increaseTf(int by) {
        tf += by;
    }

    /**
     * @param snippetIndices
     */
    public void setSnippetIndices(int[] snippetIndices) {
        this.snippetIndices = snippetIndices;
    }

    /**
     * @param phraseFeatureIndices
     */
    public void setPhraseFeatureIndices(int[] phraseFeatureIndices) {
        this.phraseFeatureIndices = phraseFeatureIndices;
    }

    /**
     * @return boolean
     */
    public boolean isStopWord() {
        return stopWord;
    }

    /**
     * Sets the stopWord.
     *
     * @param stopWord The stopWord to set
     */
    public void setStopWord(boolean stopWord) {
        this.stopWord = stopWord;
    }

    /**
     * @return String
     */
    public String getText() {
        return text;
    }

    /**
     * @return int
     */
    public int getTf() {
        return tf;
    }

    /**
     * @return int
     */
    public int getCode() {
        return code;
    }

    /**
     * @return int
     */
    public int getLength() {
        return length;
    }

    /**
     * @return double
     */
    public double getTfidf() {
        return tf * idf;
    }

    /**
     * @return int[]
     */
    public int[] getPhraseFeatureIndices() {
        return phraseFeatureIndices;
    }

    /**
     * @return int[]
     */
    public int[] getSnippetIndices() {
        return snippetIndices;
    }

    /**
     * @return int[]
     */
    public int[] getSnippetTf() {
        return snippetTf;
    }

    /**
     * Sets the snippetTf.
     *
     * @param snippetTf The snippetTf to set
     */
    public void setSnippetTf(int[] snippetTf) {
        this.snippetTf = snippetTf;
    }

    /**
     * @return double
     */
    public double getIdf() {
        return idf;
    }

    /**
     * Sets the idf.
     *
     * @param idf The idf to set
     */
    public void setIdf(double idf) {
        this.idf = idf;
    }

    /**
     * Sets the text.
     *
     * @param text The text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return boolean
     */
    public boolean isStrong() {
        return strong;
    }

    /**
     * Sets the strong.
     *
     * @param strong The strong to set
     */
    public void setStrong(boolean strong) {
        this.strong = strong;
    }

    /**
     * @return
     */
    public boolean isQueryWord() {
        return queryWord;
    }

    /**
     * @param b
     */
    public void setQueryWord(boolean b) {
        queryWord = b;
    }

    /**
     * @return
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param string
     */
    public void setLanguage(String string) {
        language = string;
    }
}
