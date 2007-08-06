
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

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SimpleTerm implements WeightedTerm{
    private int id;
    private String originalTerm;
    private String stemmedTerm;
    boolean stopWord;
    private double weight;

    protected int tf;


    protected Map tfMap;

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();

    static {
        numberFormat.setMinimumFractionDigits(5);
    }

    public SimpleTerm(String original, String stemmed) {
//        this.id = id;
        this.originalTerm = original;
        this.stemmedTerm = stemmed;
        this.tf = 1;
        this.tfMap = new HashMap();
    }
    public SimpleTerm(String original, String stemmed, boolean isStopWord) {
        this(original, stemmed);
        this.stopWord = isStopWord;
    }

    public String getOriginalTerm() {
        return originalTerm;
//        return stemmedTerm;
    }



    public String toString() {
//        return /*"("+id+")"+*/originalTerm/*+"->"+stemmedTerm*/+"("+numberFormat.format(weight)+")";
        StringBuffer b = new StringBuffer();
        b.append(originalTerm+"->"+stemmedTerm+"(");
        for (Iterator iterator = tfMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry o = (Map.Entry) iterator.next();
            b.append("["+o.getKey()+","+o.getValue()+"],");
        }
        b.append(")");
        return b.toString();
//        return originalTerm;
    }

    public boolean equals(Object obj) {
//        return stemmedTerm.equals(((Term)obj).getOriginalTerm());
        if (obj instanceof Term)
            return stemmedTerm.equals(((Term)obj).getStemmedTerm());

        return false;
    }

    public int hashCode() {
        return stemmedTerm.hashCode();
//        return originalTerm.hashCode();
    }

    public int getId() {
        return id;
    }

    public String getStemmedTerm() {
        return stemmedTerm;
    }

    public boolean isStopWord() {
        return stopWord;
    }

    public void setStopWord(boolean stopWord) {
        this.stopWord = stopWord;
    }

    public void setId(int id) {
        this.id = id;
    }



    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Term copy() {
//        WeightedTerm term = new SimpleTerm(originalTerm, stemmedTerm);
//        term.setWeight(weight);
//        term.setId(id);
//        term.setStopWord(stopWord);
//        return term;
        return this;
    }

    public int getTf(String documentId) {
        return ((Integer)tfMap.get(documentId)).intValue();
    }

    public void setTf(String documentId, int tf) {
        this.tfMap.put(documentId, new Integer(tf));
    }

    public void increaseTf(String documentId) {
        increaseTf(documentId, 1);
    }
    public void increaseTf(String documentId, int delta) {
        if (tfMap.containsKey(documentId)) {
            tfMap.put(documentId, new Integer(((Integer)tfMap.get(documentId)).intValue() + delta));
        } else {
            tfMap.put(documentId, new Integer(delta));
        }

    }




    public Map getTfMap() {
        return tfMap;
    }

}
