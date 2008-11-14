
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.IRContext;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.ngram.NGramCollector;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.ngram.NGram;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.Snippet;
import org.carrot2.filter.trc.util.ArrayUtils;
import org.carrot2.filter.trc.util.FormatterFactory;

import java.util.*;
import java.text.NumberFormat;

import org.apache.commons.collections.Predicate;


/**
 * Generate phrases for label
 */
public class PhraseBasedLabel implements ClusterLabelGenerator {



    private static final double LENGTH_SCALE_FACTOR = 0.3;

    static final NumberFormat nf = FormatterFactory.getNumberFormat(4);
    IRContext context;

    NGramCollector ngram;

    double[][] clusters;
    int maxLength;

    /**
     * Contruct na new phrase generator
     * 
     * @param context document context
     * @param maxLength max length of ngram
     *
     */
    public PhraseBasedLabel(IRContext context, int maxLength, double[][] clusterMembership) {
        this.context = context;
        this.maxLength = maxLength;
        this.clusters = clusterMembership;
    }


    public String[] getLabel(int id) {
        ngram = new NGramCollector(maxLength, context.getFilter());
        int[] nonZeroIndices = ArrayUtils.getNonZeroIndices(clusters[id]);
        Snippet[] snippets = context.getSnippetByIndices(nonZeroIndices);
        for (int i = 0; i < snippets.length; i++) {
            ngram.process(snippets[i]);
        }

        Predicate keyPredicate = new Predicate() {
            public boolean evaluate(Object object) {
                return (object instanceof NGram)
                        && (((NGram) object).length() >= 1);
            }
        };
        Map map = calculateTDIDF(
                ngram.getTermFrequency().getInternalMap(),
                ngram.getDocumentFrequency().getInternalMap(),
                nonZeroIndices.length);
        final double average = averageDouble(map.values());

        Predicate valuePredicate = new Predicate() {
            public boolean evaluate(Object object) {
//                return ((Integer)object).intValue() > 1;
//                return ((Integer)object).intValue() > minFreq;
                return ((Double)object).doubleValue() > average;
//                return ((Double)object).doubleValue() > 0;
            }
        };
        Map tf = ngram.getTermFrequency().getInternalMap();
        Map df = ngram.getDocumentFrequency().getInternalMap();
        Map weightMap = sortMap(filteredMap(map, keyPredicate, valuePredicate));
        if (weightMap.isEmpty()) {
            weightMap = sortMap(map);
        }
//        System.out.println("Weight\tTF\tDF\tTerm");

        int counter = 0;
        int maxCount = 20;

        int maxLabel = 1;
        String[] labels = new String[maxLabel];
        for (Iterator iterator = weightMap.entrySet().iterator();
             iterator.hasNext() && counter < maxCount; counter++ ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            NGram key = (NGram) entry.getKey();
            Double value = (Double) entry.getValue();
            Integer tfi = ((Integer) tf.get(key));
            Integer dfi = ((Integer) df.get(key));

//            System.out.println(
//                    nf.format(value.doubleValue())+
//                    "\t"+nf.format(tfi.doubleValue())+
//                    "\t"+nf.format(dfi.doubleValue())+
//                    "\t"+key.toString());
            if (counter <maxLabel) {
                labels[counter] = capitalize(key.toString());
            }
        }
        return labels;
    }

    /** Capitalize words in phrase */
    private String capitalize(String phrase) {
        StringBuffer b = new StringBuffer(phrase);
        int capitalIndex = 0;
        do {
            b.setCharAt(capitalIndex, Character.toUpperCase(b.charAt(capitalIndex)));
        } while ((capitalIndex = b.indexOf(" ", capitalIndex+1)+1) != 0);
        return b.toString();
    }

    private static Map calculateTDIDF(Map tf, Map df, int ndoc) {
        Map weightMap = new HashMap(tf);
        for (Iterator iterator = weightMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            double docF = ((Integer)df.get(key)).doubleValue();
            double termF = ((Integer)entry.getValue()).doubleValue();
//            double idf = Math.log((double)ndoc / docF);
            double idf = (ndoc - docF)/(ndoc - 1);

            double weight = termF * idf * scaleByLenght(((NGram)key).length());
            entry.setValue(new Double(weight));
        }
        return weightMap;
    }

    private static double scaleByLenght(int len) {
        return 1 + (len - 1) * LENGTH_SCALE_FACTOR;
    }

     private static double averageDouble(Collection frequencies) {
        double sum = 0;
        for (Iterator iterator = frequencies.iterator(); iterator.hasNext();) {
            Double o = (Double) iterator.next();
            sum += o.doubleValue();
        }

        return sum / frequencies.size();
    }

    /**
     * Create a filterd map from original map.
     * Only entries that passed both key predicated and value predicated are added
     * @param originalMap
     * @param keyPredicate
     * @param valuePredicate
     */
    public static SortedMap filteredMap(SortedMap originalMap, Predicate keyPredicate, Predicate valuePredicate) {
        SortedMap map = new TreeMap(originalMap.comparator());
        for (Iterator iterator = originalMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (keyPredicate.evaluate(entry.getKey()) && valuePredicate.evaluate(entry.getValue()))
                map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public static Map filteredMap(Map originalMap, Predicate keyPredicate, Predicate valuePredicate) {
        Map map = new HashMap();
        for (Iterator iterator = originalMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (keyPredicate.evaluate(entry.getKey()) && valuePredicate.evaluate(entry.getValue()))
                map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public static SortedMap sortMap(final Map map) {
        SortedMap sorted = new TreeMap(new Comparator(){
            public int compare(Object o1, Object o2) {
                return ((Double)map.get(o1)).doubleValue() > ((Double)map.get(o2)).doubleValue() ? -1 : 1;
            }
        });
        sorted.putAll(map);
        return sorted;
    }




}
