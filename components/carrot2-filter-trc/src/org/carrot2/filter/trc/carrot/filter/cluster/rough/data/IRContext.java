
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.data;



import org.carrot2.filter.trc.carrot.filter.cluster.rough.FeatureVector;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.Snippet;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering.Clusterable;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.StopWordFilter;

import java.util.Collection;

/**
 * IRContext is a collection of documents
 */
public interface IRContext {

    public Clusterable[] getDocuments();

    public Collection getTermIndex();

    void addDocument(Document document);

    void setDocuments(Collection documents);

    void buildDocumentTermMatrix();

    int[] getMaxWeightIndices();

//    Set recalculateWeight(Set upper, Document doc);

    int[][] getTermFrequency();

    Term[] getTermArray();

    int[] getDocumentFrequency();

    double[][] getTermWeight();

    FeatureVector recalculateWeightUpper(FeatureVector upper, Clusterable doc);

    Snippet[] getSnippetByIndices(int[] indices);

    Term[] getTermByIndices(int[] indices);

    int noOfTerms();

    int noOfDocuments();

    Snippet[] getSnippets();

    void setQuery(String query);

    String[] getQueryWords();

    StopWordFilter getFilter();

    String getSnippetTermWeightAsString(int snippetId);

    String getQuery();
}
