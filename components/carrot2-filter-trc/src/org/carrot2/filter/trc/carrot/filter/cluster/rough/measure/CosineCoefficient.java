
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.measure;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.FeatureVector;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering.Clusterable;

/**
 * Measure cosine coefficient between two vectors.
 */
public class CosineCoefficient implements Similarity/*, Statistic.VectorVectorFunction*/ {


//    private static final cern.jet.math.Functions F = cern.jet.math.Functions.functions;

    /**
     * Calculate cosine coefficient between two vectors :
     *  Sum(x[i] * y[i]) / Sqrt(Sum(x[i]^2) * Sum(y[i]^2))
     * When numerator part (Sum(x[i] * y[i]) is 0, returns 0;
     * @param x
     * @param y
     */
//    public double apply(DoubleMatrix1D x, DoubleMatrix1D y) {
//        double nominator =  x.aggregate(y, F.plus, F.mult );
//        //handle case when a == 0 in a/b
//        if (nominator == 0)
//            return 0;
//
//        return nominator /
//                Math.sqrt(x.aggregate(F.plus,F.pow(2)) * y.aggregate(F.plus, F.pow(2)));
//    }

    /**
     * Calculate cosine measure between two vectors
     *                      SUM[k=1..N](w_kj1 * w_kj2)
     *   S_D(d_j1, d_j2) = -------------------------------------------------------
     *                     sqrt(SUM[k=1..N](w_kj1^2)) * sqrt(SUM[k=1..N](w_kj2^2))
     * @param obj1
     * @param obj2
     */
    public double measure(Clusterable obj1, Clusterable obj2) {
        return distanceUsingSparseVector(obj1, obj2);
        /*
        Clusterable[] doc = { obj1, obj2};
//        System.out.println("o1="+obj1+",o2="+obj2);
double[] sum = new double [2];
Map termWeightMap = CollectionFactory.getHashMap();
//store map of term -> weight of first document
sum[0] = 0;
for (Iterator iter = doc[0].getAttributes().iterator(); iter.hasNext(); ) {
WeightedTerm term = (WeightedTerm) iter.next();
double weight = term.getWeight();
termWeightMap.put(term, new Double(weight));
sum[0] += weight * weight;
}
double weightProduct = 0;
sum[1] = 0;

//calculate sum of product of term weight from both document
for (Iterator iter = doc[1].getAttributes().iterator(); iter.hasNext(); ) {
WeightedTerm term = (WeightedTerm) iter.next();
double weight = term.getWeight();

//if a term exist in both document, calculate product of weight
//else : product = 0 * something = 0 => no need to add
if (termWeightMap.containsKey(term)) {
weightProduct += weight * ((Double)termWeightMap.get(term)).doubleValue();
}
sum[1] += weight * weight;
}

return weightProduct / (Math.sqrt(sum[0]) * Math.sqrt(sum[1]));*/
    }

    private double distanceUsingTrove(Clusterable obj1, Clusterable obj2) {
        return 0;
        /*Clusterable[] doc = { obj1, obj2};
//        System.out.println("o1="+obj1+",o2="+obj2);
        double[] sum = new double [2];
        TIntDoubleHashMap termWeightMap = new TIntDoubleHashMap();
        //store map of term -> weight of first document
        sum[0] = 0;
        for (Iterator iter = doc[0].getAttributes().iterator(); iter.hasNext(); ) {
            WeightedTerm term = (WeightedTerm) iter.next();
            double weight = term.getWeight();
            termWeightMap.put(term.getId(), weight);
            sum[0] += weight * weight;
        }
        double weightProduct = 0;
        sum[1] = 0;

        //calculate sum of product of term weight from both document
        for (Iterator iter = doc[1].getAttributes().iterator(); iter.hasNext(); ) {
            WeightedTerm term = (WeightedTerm) iter.next();
            double weight = term.getWeight();

            //if a term exist in both document, calculate product of weight
            //else : product = 0 * something = 0 => no need to add
            if (termWeightMap.containsKey(term.getId())) {
                weightProduct += weight * termWeightMap.get(term.getId());
            }
            sum[1] += weight * weight;
        }

        return weightProduct / (Math.sqrt(sum[0]) * Math.sqrt(sum[1]));*/
    }


    private double distanceUsingSparseVector(Clusterable obj1, Clusterable obj2) {
        Clusterable[] doc = { obj1, obj2};
        double[] sum = new double [2];
        sum[0] = 0;
        double weightProduct = 0;
        FeatureVector[] vector = {doc[0].getFeatures(), doc[1].getFeatures()};
        int size = vector[0].size();
        for (int i = 0; i < size; i++) {
            double x = vector[0].getWeight(i), y =  vector[1].getWeight(i);
            weightProduct += x * y;
            sum[0] += x * x;
            sum[1] += y * y;
        }

        return weightProduct / (Math.sqrt(sum[0]) * Math.sqrt(sum[1]));
    }

    public double measure(double[] vector1, double[] vector2) {
        double[] sum = new double [2];
        int size = vector1.length;
        double weightProduct = 0;
        for (int i = 0; i < size; i++) {
            double x = vector1[i], y = vector2[i];
            weightProduct += x * y;
            sum[0] += x * x;
            sum[1] += y * y;
        }
        return weightProduct / (Math.sqrt(sum[0]) * Math.sqrt(sum[1]));
    }
}
