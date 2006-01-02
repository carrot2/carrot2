
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
package com.chilang.carrot.filter.cluster.rough.data;

import java.util.Map;

/**
 * Represent a term/word in document
 */
public interface Term{
    public String getOriginalTerm();

    String getStemmedTerm();

    boolean isStopWord();

    void setStopWord(boolean stopWord);

    int getId();

    void setId(int id);

    Term copy();

    public void setTf(String documentId, int tf);

    public int getTf(String documentId);

    public void increaseTf(String documentId);

    public void increaseTf(String documentId, int delta);

    /**
     * Return a map of (document id -> term frequency in that document).
     * Size of the map is the "document frequency" of term
     * (i.e. number of document in the corpus in which term occurs)
     * @return
     */
    Map getTfMap();

}
