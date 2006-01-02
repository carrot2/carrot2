
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


import com.chilang.carrot.filter.cluster.rough.FeatureVector;
import com.chilang.carrot.filter.cluster.rough.Snippet;
import com.chilang.carrot.filter.cluster.rough.clustering.Clusterable;

import java.text.NumberFormat;
import java.util.Set;

public class SnippetDocument implements Snippet, Document, Clusterable{

    private String title;
    private String url;
    private String snippet;

    protected String id;

    private int internalId;

    //cell[i] - number of occurences of term i in current document
//    private Map termFreq;

    protected Set termSet;

    private static final NumberFormat nf = NumberFormat.getNumberInstance();
    static {
        nf.setMaximumFractionDigits(4);
    }
    public Set getStrongTerms() {
        return strongTerms;
    }

    public void setStrongTerms(Set strongTerms) {
        this.strongTerms = strongTerms;
    }

    /**
     * Set of stemmed term which are "strong" in snippet
     */
    private Set strongTerms;

    public SnippetDocument() { }

    public SnippetDocument(String id) {
        this.id = id;
//        System.out.println("id="+id);
//        this.id = counter++;//Integer.parseInt(id);
//        termFreq = CollectionFactory.getHashMap();
    }

    public SnippetDocument(String id, String title, String url, String snippet) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.snippet = snippet;
    }

    public SnippetDocument(String id, String title, String snippet) {
        this.id = id;
        this.title = title;
        this.snippet = snippet;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return snippet;
    }

    public void setDescription(String snippet) {
        this.snippet = snippet;

    }


    public int getInternalId() {
        //TODO ids in XML starts from 1 but algorithm use matrix/array starts from 0
        return internalId;
    }


    public void setInternalId(int internalId) {
        this.internalId = internalId;
    }





    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("ID="+id+"\t"+"UID="+internalId+"\n");
        b.append("TITLE="+title+"\n");
        b.append("SNIPPET="+snippet+"\n");
        b.append("URL="+url+"\n");
//        b.append("TERM=");

//        for (Iterator i = TermUtils.sortByWeight(termFreq.keySet()).iterator(); i.hasNext();) {
//            WeightedTerm term = (WeightedTerm) i.next();
//            b.append("["+termFreq.get(term)+" - "+nf.format(term.getWeight())+"] "+term.getOriginalTerm()+"\n");
//        }
//        b.append("\n");

        return b.toString();
//        return "id="+id+"\ntitle="+title+"\nurl="+url+"\nsnippet="+snippet+")"/*+termFreq.keySet()*/;
    }

    public Set getTermSet() {
        return termSet;
    }

    public void setTermSet(Set termSet) {
        this.termSet = termSet;
    }

    public String getId() {
        return id;  //To change body of implemented methods use Options | File Templates.
    }

    public FeatureVector getFeatures() {
        return null;  //To change body of implemented methods use Options | File Templates.
    }

    public void setFeatures(FeatureVector featureVector) {
        //To change body of implemented methods use Options | File Templates.
    }

    public int getIdentifier() {
        return internalId;
    }

    public void setId(String id) {
        this.id = id;
    }
}
