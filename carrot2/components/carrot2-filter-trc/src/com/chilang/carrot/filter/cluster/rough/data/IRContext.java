/**
 * Created by IntelliJ IDEA.
 * User: chilang
 * Date: 2003-07-17
 * Time: 02:11:18
 * To change this template use Options | File Templates.
 */
package com.chilang.carrot.filter.cluster.rough.data;



import com.chilang.carrot.filter.cluster.rough.FeatureVector;
import com.chilang.carrot.filter.cluster.rough.Snippet;
import com.chilang.carrot.filter.cluster.rough.clustering.Clusterable;
import com.chilang.carrot.filter.cluster.rough.filter.StopWordFilter;

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
